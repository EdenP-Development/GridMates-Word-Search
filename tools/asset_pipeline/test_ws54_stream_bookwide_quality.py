"""WS54 data provenance and six-chapter editorial comparison contract."""
import csv
from pathlib import Path
import unittest
from itertools import combinations
from collections import Counter

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "core/src/test/resources/reviewed-packs/en-woodland-v1.tsv"
INPUT = ROOT / "data/lexicon/authoring/chapter-recipes-v1/woodland-ws54-stream-manuscript.tsv"
FIXTURE = ROOT / "core/src/test/resources/chapter-recipes/ws54-stream-manuscript.tsv"
WS52 = ROOT / "data/lexicon/authoring/chapter-recipes-v1/woodland-ws52-manuscript.tsv"
WS53 = ROOT / "data/lexicon/authoring/chapter-recipes-v1/quiet-clearings-ws53-revision.tsv"
WS51 = ROOT / "data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv"
KOTLIN = ROOT / "core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/StreamChapterBookwideQualityLabTest.kt"
PILOT = ROOT / "app/src/debug/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
RELEASE = ROOT / "app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
WORKFLOWS = (ROOT / ".github/workflows/ws01-android-qa.yml",
             ROOT / ".github/workflows/ws12g-qa-art-lab.yml")

def rows(path):
    with path.open(newline="") as f:
        return list(csv.DictReader(f, delimiter="\t"))

def chapter_groups():
    base = rows(WS52)
    chapter = {n: {r["answer"] for r in base if r["chapter"] == str(n)}
               for n in (3,4,6,8)}
    chapter[7] = {r["answer"] for r in rows(WS53)}
    chapter[5] = {r["answer"] for r in rows(WS51)}
    return chapter

class StreamBookwideQualityContract(unittest.TestCase):
    def test_source_fidelity_and_33_explicitly_unapproved_terms(self):
        self.assertEqual(INPUT.read_bytes(), FIXTURE.read_bytes())
        source = {}
        for line in SOURCE.read_text().splitlines():
            if line.startswith("gm"):
                ident,answer,display,tags=line.split("\t")
                source[ident]=(answer,display,set(tags.split("|")))
        self.assertEqual(159,len(source))
        terms=rows(INPUT)
        self.assertEqual(52,len(terms))
        self.assertEqual(52,len({t["answer"] for t in terms}))
        self.assertEqual(52,len({t["word_id"] for t in terms}))
        old=[t for t in terms if t["provenance"]=="existing_woodland_QA_not_release"]
        new=[t for t in terms if t["provenance"]=="ws54_streamlife_candidate_unapproved"]
        self.assertEqual(19,len(old))
        self.assertEqual(33,len(new))
        for term in old:
            answer,display,tags=source[term["word_id"]]
            self.assertEqual((answer,display),(term["answer"],term["display"]))
            self.assertIn("woodland:"+term["subtopic"]+":"+term["tier"],tags)
        self.assertTrue(all(t["word_id"].startswith("ws54s") and
            t["subtopic"]=="streamlife" and t["word_id"] not in source
            for t in new))
        self.assertTrue(all(t["chapter"]=="6" and
            t["chapter_title"]=="By the Stream" for t in terms))
        means=[]
        for n,count,cap in [(1,12,7),(2,16,10),(3,24,15)]:
            page=[t for t in terms if t["page"]==str(n)]
            self.assertEqual(count,len(page))
            self.assertTrue(all(t["answer"].isalpha() and
                t["answer"].isupper() and 4<=len(t["answer"])<=cap for t in page))
            if n==1:
                self.assertTrue(all(t["tier"]=="familiar" for t in page))
            self.assertTrue(all(a["answer"] not in b["answer"]
                for a in page for b in page if a is not b))
            means.append(sum(len(t["answer"]) for t in page)/len(page))
        self.assertTrue(means[0]<means[1]<means[2])
    def test_bookwide_diversity_gain_is_measured_not_assumed(self):
        old=chapter_groups()
        new=dict(old)
        new[6]={t["answer"] for t in rows(INPUT)}
        self.assertEqual(52,len(new[6]))
        self.assertEqual(168,len(set.union(*old.values())))
        self.assertEqual(202,len(set.union(*new.values())))
        for other, before, after in [(3,29,10),(4,9,2),(5,1,0),
                                     (7,21,3),(8,30,10)]:
            self.assertEqual(before,len(old[6]&old[other]))
            self.assertEqual(after,len(new[6]&new[other]))
        def total(grps):
            return sum(len(grps[x]&grps[y])
                for x,y in combinations((3,4,5,6,7,8),2))
        self.assertEqual(204,total(old))
        self.assertEqual(139,total(new))
        self.assertEqual(129,len(set.union(*(old[n] for n in (3,4,6,7,8)))))
        self.assertEqual(163,len(set.union(*(new[n] for n in (3,4,6,7,8)))))
    def test_existing_books_and_QA_pipeline_reports_isolated(self):
        self.assertIn("const val enabled = false", RELEASE.read_text())
        self.assertIn('volume("woodland", "Woodland", WordTheme.WOODLAND, 171L)',
                      PILOT.read_text())
        code=KOTLIN.read_text()
        for needle in ("WordSearchGeneratorV5.generate(",
                       "BookPageSessionRules.puzzle(",
                       "BookBoardDigest.sha256(",
                       "assertEquals(202, distinct(after",
                       "ws54-stream-frozen-pages.tsv",
                       "ws54-bookwide-overlap-delta.tsv",
                       "ws54-bookwide-word-frequency.tsv",
                       "UNAPPROVED_QA_RESEARCH_ONLY"):
            self.assertIn(needle,code)
        for p in WORKFLOWS:
            s=p.read_text()
            for needle in ("test_ws54_stream_bookwide_quality.py",
                           "GridMates-WS54-Stream-Bookwide-QA",
                           "feature/ws54-stream-chapter-bookwide-quality-2026-11-20",
                           "core/build/ws54-stream-frozen-pages.tsv",
                           "core/build/ws54-bookwide-overlap-delta.tsv",
                           "core/build/ws54-bookwide-word-frequency.tsv"):
                self.assertIn(needle,s)

if __name__ == "__main__":
    unittest.main()
