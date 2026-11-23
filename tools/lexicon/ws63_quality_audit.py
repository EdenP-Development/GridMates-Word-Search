#!/usr/bin/env python3
"""WS63: read-only, reproducible editorial QA quality/repetition baseline.

Sources: existing en-v1 + fall-v1 authoring CSVs and the proposed chapter map.
Reuses existing WS10 rights/ID/approval validator. A draft 'approved' row means
QA-authoring approval, NEVER owner/public-release authorization.

Nothing in this tool changes content, saved games, puzzle IDs, packs, release
assets, Android Kotlin, or approval flags. Run under Python 3.11, stdlib only.

    python3 tools/lexicon/ws63_quality_audit.py --out-dir qa-ws63-quality
"""
import argparse
from collections import Counter, defaultdict
import csv
import hashlib
import json
from pathlib import Path
import sys

from validate import audit, csv_rows, EXPECTED_WORD_COLS, EXPECTED_TOPIC_COLS

ROOT = Path(__file__).resolve().parents[2]
EDITIONS = ("en-v1", "fall-v1")
SIZES = (("relaxed_12", 12, "familiar", 12),
         ("classic_15", 15, "not-advanced", 16),
         ("expert_20", 20, "all", 24))


def sha256_file(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def usable(answer: str, size: int, tier: str, difficulty: str) -> bool:
    """Matches V5's answer-length and palindrome filter + its tier eligibility."""
    if len(answer) >= size or answer == answer[::-1]:
        return False
    if difficulty == "familiar":
        return tier == "familiar"
    if difficulty == "not-advanced":
        return tier != "advanced"
    return True


def normalize_catalog(edition: str, data: Path) -> dict:
    validation = audit(data)
    words = {r["word_id"]: r for r in csv_rows(data / "words_candidate.csv", EXPECTED_WORD_COLS)}
    links = csv_rows(data / "word_topics_candidate.csv", EXPECTED_TOPIC_COLS)
    topics = {t["id"]: t for t in json.loads((data / "topics.json").read_text(encoding="utf8"))["topics"]}
    source = json.loads((data / "sources.json").read_text(encoding="utf8"))
    active = {k for k, t in topics.items() if t["status"] == "active"}
    eligible = defaultdict(dict)
    topic_rows = defaultdict(list)
    for link in links:
        w = words[link["word_id"]]
        if (link["review_status"] == "approved" and w["review_status"] == "approved"
                and link["topic_id"] in active):
            key = link["topic_id"]
            eligible[key][w["word_id"]] = w
            topic_rows[key].append((w, link))

    by_topic = {}
    for topic in sorted(active):
        pairs = topic_rows[topic]
        by_subtopic = Counter()
        by_tier = Counter()
        for w, link in pairs:
            by_subtopic[link["subtopic_id"]] += 1
            by_tier[link["tier"]] += 1
        by_size = {}
        for label, size, mode, count in SIZES:
            candidates = {w["word_id"] for w, link in pairs if usable(w["answer"], size, link["tier"], mode)}
            n = len(candidates)
            by_size[label] = {
                "unique_eligible": n,
                "required_targets_per_board": count,
                "shortfall_to_one_board": max(0, count - n),
                "soft_depth_target_twice_board": 2 * count,
                "below_soft_depth_target": n < 2 * count,
            }
        by_topic[topic] = {
            "label": topics[topic]["label"],
            "qa_approved_unique": len(eligible[topic]),
            "approved_assignments": len(pairs),
            "by_tier_assignments": dict(sorted(by_tier.items())),
            "by_subtopic_assignments": dict(sorted(by_subtopic.items())),
            "board_eligibility": by_size,
        }

    approved_ids = {id for pairs in eligible.values() for id in pairs}
    approved = {id: words[id] for id in approved_ids}
    len_dist = Counter(len(r["answer"]) for r in approved.values())
    palindrome = sorted(w["answer"] for w in approved.values() if w["answer"] == w["answer"][::-1])
    input_hashes = {p: sha256_file(data / p) for p in (
        "words_candidate.csv", "word_topics_candidate.csv", "topics.json", "sources.json"
    )}
    return {
        "name": edition,
        "source_path": str(data.relative_to(ROOT)) if data.is_relative_to(ROOT) else str(data),
        "source_sha256": input_hashes,
        "validation": validation,
        "word_rows": words,
        "eligible_ids": approved_ids,
        "approved_words": approved,
        "approved_links": [(w, link) for pairs in topic_rows.values() for w, link in pairs],
        "topics": by_topic,
        "approved_answer_length_distribution": dict(sorted(len_dist.items())),
        "v5_palindrome_ineligible": palindrome,
        "source_ledger_status": source.get("status_note", ""),
    }


def conflicts_between_editions(editions: dict) -> list:
    """A stable canonical ID or answer must never silently change identity."""
    id_map, answer_map, issues = {}, {}, []
    for edition in sorted(editions):
        for id, row in sorted(editions[edition]["word_rows"].items()):
            answer = row["answer"]
            if id in id_map and id_map[id][0] != answer:
                issues.append(f"ID {id} changed answer: {id_map[id][0]} vs {answer}")
            if answer in answer_map and answer_map[answer][0] != id:
                issues.append(f"Answer {answer} uses two IDs: {answer_map[answer][0]} vs {id}")
            id_map[id] = (answer, edition)
            answer_map[answer] = (id, edition)
    return sorted(set(issues))


def relationship_flags(words: dict) -> dict:
    """Soft review prompts, not spelling errors or global exclusions."""
    answers = sorted({w["answer"] for w in words.values()})
    answer_set = set(answers)
    plural = []
    for answer in answers:
        if answer + "S" in answer_set:
            plural.append([answer, answer + "S"])
        if answer.endswith("Y") and answer[:-1] + "IES" in answer_set:
            plural.append([answer, answer[:-1] + "IES"])
    # Short/long substrings are legitimate library records; only *one board*
    # needs a generator-specific containment conflict check.
    containment = []
    for idx, a in enumerate(answers):
        if len(a) < 3:
            continue
        for b in answers[idx + 1:]:
            if a in b or b in a:
                containment.append([a, b])
    return {
        "plurals_to_review_count": len(plural),
        "plurals_to_review_examples": plural[:40],
        "contains_related_pair_count": len(containment),
        "contains_related_pair_examples": containment[:40],
        "notes": "Soft flags only: valid inflections, stems and substrings may coexist in the canonical library.",
    }


def proposed_chapter_coverage(english: dict, recipe_path: Path) -> list:
    approved_links = english["approved_links"]
    out = []
    with recipe_path.open(encoding="utf8", newline="") as stream:
        rows = list(csv.DictReader(stream, delimiter="\t"))
    for ch in rows:
        topic = ch["theme"].lower()
        allowed = set(ch["allowed_subtopics"].split(","))
        matches = [(w, a) for w, a in approved_links
                   if a["topic_id"] == topic and a["subtopic_id"] in allowed]
        all_ids = {w["word_id"] for w, _ in matches}
        metrics = {}
        for label, size, diff, targets in SIZES:
            avail = {w["word_id"] for w, a in matches
                     if usable(w["answer"], size, a["tier"], diff)}
            metrics[label] = {
                "eligible_unique": len(avail),
                "targets_per_page": targets,
                "shortfall": max(0, targets - len(avail)),
            }
        out.append({
            "book": ch["theme"], "chapter": int(ch["chapter_number"]),
            "title": ch["chapter_title"], "primary_subtopic": ch["primary_subtopic"],
            "allowed_subtopics": sorted(allowed),
            "distinct_qa_words_for_chapter": len(all_ids),
            "three_page_target_slots": sum(x[3] for x in SIZES),
            "minimum_cross_page_reused_slots_from_pool_limit": max(0, 52 - len(all_ids)),
            "board_eligibility": metrics,
            "status": "proposed authoring coverage only, not frozen page quality",
        })
    if len(out) != 18:
        raise ValueError(f"Expected 18 existing chapter recipes, got {len(out)}")
    return out


def analyze(root: Path = ROOT) -> dict:
    base = root / "data" / "lexicon" / "authoring"
    eds = {edition: normalize_catalog(edition, base / edition) for edition in EDITIONS}
    hard_errors = conflicts_between_editions(eds)
    if hard_errors:
        raise ValueError("Cross-edition canonical identity conflict: " + "; ".join(hard_errors[:12]))

    union = {}
    for edition in EDITIONS:
        for id, row in eds[edition]["approved_words"].items():
            union[id] = row
    all_a = {r["answer"] for r in eds["en-v1"]["approved_words"].values()}
    fall_a = {r["answer"] for r in eds["fall-v1"]["approved_words"].values()}
    chapters = proposed_chapter_coverage(eds["en-v1"],
        base / "chapter-recipes-v1" / "chapter-map.tsv")
    summary = {
        "report": "ws63-quality-baseline-v1",
        "status": "INTERNAL_QA_DIAGNOSTIC_NOT_RELEASE_APPROVAL",
        "production_approved_new_words": 0,
        "production_approved_new_words_meaning": "This tool performs no owner/public release approvals.",
        "goal_unique_owner_approved": 10000,
        "authoring_edition_qa_unique_union": len(union),
        "en_v1_qa_unique": len(all_a),
        "fall_v1_qa_unique": len(fall_a),
        "fall_overlap_with_en_v1": len(all_a & fall_a),
        "fall_new_unique_over_en_v1": len(fall_a - all_a),
        "qa_topic_families_active_total": len({
            name for e in eds.values() for name in e["topics"]}),
        "proposed_chapter_count": len(chapters),
        "relation_review_flags": relationship_flags(union),
        "proposed_chapters": chapters,
        "editions": {},
        "method": {
            "source": "Existing exact committed authoring data; no external corpus ingest",
            "hard_gates": "WS10 validator and cross-edition canonical word identity conflicts",
            "soft_gates": "Chapter depth, topic coverage, morphology and related-substring diagnostics",
            "v5_filter": "A-Z, length < grid width, non-palindrome, difficulty tier",
            "do_not_equate": "QA-reviewed with owner-approved; lexical rows with playable builds",
            "one_puzzle": "Every generated page must independently pass exact occurrence, collision, board QA and frozen ID tests",
        },
    }
    for key in EDITIONS:
        e = eds[key]
        summary["editions"][key] = {
            "validation": e["validation"],
            "source_path": e["source_path"],
            "source_sha256": e["source_sha256"],
            "qa_approved_unique": len(e["eligible_ids"]),
            "approved_answer_length_distribution": e["approved_answer_length_distribution"],
            "v5_palindrome_ineligible": e["v5_palindrome_ineligible"],
            "by_topic": e["topics"],
        }
    return summary


def markdown(report: dict) -> str:
    lines = [
        "# WS63 — Word-library quality baseline (automated internal QA)",
        "",
        "> **Not owner-approved release content.** No source files, apps, boards, packs or saves are modified.",
        "",
        "## Honest unique counts",
        "",
        "| Measure | QA-authoring count |",
        "| --- | ---: |",
        f"| English v1 | {report['en_v1_qa_unique']} |",
        f"| Fall v1 | {report['fall_v1_qa_unique']} |",
        f"| Fall overlap with English v1 | {report['fall_overlap_with_en_v1']} |",
        f"| Fall newly distinct | {report['fall_new_unique_over_en_v1']} |",
        f"| Combined QA-authoring unique | **{report['authoring_edition_qa_unique_union']}** |",
        f"| Active topic families across both editions | {report['qa_topic_families_active_total']} |",
        "| Newly owner/public-release-authorized by this audit | **0** |",
        "",
        "Counts are canonical unique ANSWERS, not assignments or installed/published books.",
        "",
        "## Board-length and familiarity pool (read-only from approved-for-QA assignments)",
        "",
        "| Edition | Topic | QA unique | 12×12 Relaxed (12) | 15×15 Classic (16) | 20×20 Expert (24) |",
        "| --- | --- | ---: | ---: | ---: | ---: |",
    ]
    for edition, ed in report["editions"].items():
        for topic, r in sorted(ed["by_topic"].items()):
            b = r["board_eligibility"]
            lines.append(f"| {edition} | {topic} | {r['qa_approved_unique']} | "
                f"{b['relaxed_12']['unique_eligible']} | "
                f"{b['classic_15']['unique_eligible']} | "
                f"{b['expert_20']['unique_eligible']} |")
    lines += [
        "",
        "Board eligibility excludes V5-incompatible palindromes and words too long for the grid;",
        "it does not certify V5 can place **a particular** full target set without conflicts.",
        "A pool smaller than twice the board's target count is a *soft review prompt*, not a release block.",
        "",
        "## Proposed book-chapter word-pool coverage (NOT frozen game pages)",
        "",
        "| Book | Chapter | Distinct pool | Short eligible | Medium eligible | Large eligible | Minimum repeated slots across 52 slots |",
        "| --- | --- | ---: | ---: | ---: | ---: | ---: |",
    ]
    for row in report["proposed_chapters"]:
        b = row["board_eligibility"]
        lines.append(f"| {row['book']} | {row['chapter']}: {row['title']} | "
            f"{row['distinct_qa_words_for_chapter']} | "
            f"{b['relaxed_12']['eligible_unique']} | {b['classic_15']['eligible_unique']} | "
            f"{b['expert_20']['eligible_unique']} | "
            f"{row['minimum_cross_page_reused_slots_from_pool_limit']} |")
    f = report["relation_review_flags"]
    lines += [
        "",
        "The last column is a mathematical **minimum caused by limited source pool size**,",
        "assuming 52 total slots and every available word can be reused between pages.",
        "It is NOT the measured frozen-board overlap, nor evidence of human perceived repetition.",
        "",
        "## Soft editorial review prompts, not deletions",
        "",
        f"- Possible singular/plural pairs: **{f['plurals_to_review_count']}** (heuristic).",
        f"- Substring-related word pairs: **{f['contains_related_pair_count']}**.",
        "- Neither diagnostic rejects legitimate words from the global library; a specific",
        "  generated board applies its own substring and exact-placement rules.",
        "- Next: review regional spelling, theme meaning, familiarity, offensive/ambiguous",
        "  phrases and rights with human editors before giving any new public approval.",
        "",
        "## Non-negotiable safeguards",
        "",
        "Frozen V1–V5 historical page content, IDs, hashes, seeds, saves, legacy Daily,",
        "Nearby content compatibility and Android QA signing remain unchanged.",
        "External spelling-root quarantine is not a release dictionary; respect pinned",
        "source notices and commercial rights. Report authoring and publication counts separately.",
        "",
    ]
    return "\n".join(lines)


def main(argv=None) -> int:
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--repo-root", type=Path, default=ROOT)
    p.add_argument("--out-dir", type=Path, default=Path("qa-ws63-quality"))
    args = p.parse_args(argv)
    try:
        report = analyze(args.repo_root)
        args.out_dir.mkdir(parents=True, exist_ok=True)
        (args.out_dir / "quality-baseline.json").write_text(
            json.dumps(report, indent=2, sort_keys=True, ensure_ascii=False) + "\n",
            encoding="utf8")
        (args.out_dir / "quality-baseline.md").write_text(markdown(report), encoding="utf8")
        print("WS63 QUALITY_AUDIT_OK:",
              report["authoring_edition_qa_unique_union"], "unique QA English+Fall;",
              len(report["proposed_chapters"]), "proposed chapter pools;",
              "zero newly owner-approved words; no game source changed")
        return 0
    except (ValueError, KeyError, OSError, json.JSONDecodeError, csv.Error) as exc:
        print(f"WS63 QUALITY_AUDIT_FAIL: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
