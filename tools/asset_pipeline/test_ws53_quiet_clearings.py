"""WS53: source-vetted Quiet Clearings revision, isolated from historic chapters."""
from pathlib import Path
import csv
import unittest

ROOT = Path(__file__).resolve().parents[2]
INPUT = ROOT / "data/lexicon/authoring/chapter-recipes-v1/quiet-clearings-ws53-revision.tsv"
MIRROR = ROOT / "core/src/test/resources/chapter-recipes/ws53-quiet-clearings-revision.tsv"
WOODLAND = ROOT / "core/src/test/resources/reviewed-packs/en-woodland-v1.tsv"
WS52 = ROOT / "data/lexicon/authoring/chapter-recipes-v1/woodland-ws52-manuscript.tsv"
WS50_MAP = ROOT / "data/lexicon/authoring/chapter-recipes-v1/chapter-map.tsv"
PILOT = ROOT / "app/src/debug/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
RELEASE = ROOT / "app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
KOTLIN = ROOT / "core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/QuietClearingsDiversityLabTest.kt"
CI = (
    ROOT / ".github/workflows/ws01-android-qa.yml",
    ROOT / ".github/workflows/ws12g-qa-art-lab.yml"
)

def tsv(path):
    with path.open(newline="") as f:
        return list(csv.DictReader(f, delimiter="\t"))

class QuietClearingsEditorialGuard(unittest.TestCase):
    def test_new_versioned_recipe_and_progression_not_old_chapter_mutation(self):
        self.assertEqual(INPUT.read_bytes(), MIRROR.read_bytes())
        entries = tsv(INPUT)
        self.assertEqual(52,len(entries))
        self.assertEqual(52,len({r["answer"] for r in entries}))
        self.assertEqual([12,16,24],
            [sum(r["page"]==str(p) for r in entries) for p in (1,2,3)])
        self.assertEqual(["Moss and Fern","Grove and Canopy","Woodland Whispers"],
            [next(r["page_title"] for r in entries if r["page"]==str(p))
             for p in (1,2,3)])
        self.assertTrue(all(r["chapter"]=="7" and
            r["chapter_title"]=="Quiet Clearings" and
            r["primary_subtopic"]=="habitats" and
            r["allowed_subtopics"]=="trees,habitats" and
            r["provenance"]=="existing_woodland_QA_not_release" for r in entries))
        means=[]
        for p,count,maxlen,minimum in [(1,12,7,5),(2,16,10,6),(3,24,15,9)]:
            words=[r for r in entries if r["page"]==str(p)]
            self.assertEqual(count,len(words))
            self.assertTrue(all(4<=len(r["answer"])<=maxlen for r in words))
            if p==1:
                self.assertTrue(all(r["tier"]=="familiar" for r in words))
            self.assertGreaterEqual(sum(r["word_subtopic"]=="habitats"
                for r in words),minimum)
            self.assertTrue(all(a["answer"] not in b["answer"] for a in words
                for b in words if a is not b))
            means.append(sum(len(r["answer"]) for r in words)/len(words))
        self.assertLess(means[0],means[1])
        self.assertLess(means[1],means[2])
        # This is a new proposal, not a silent alteration of WS50's existing map.
        self.assertIn("WOODLAND\t7\tQuiet Clearings\thabitats\thabitats,trails",
                      WS50_MAP.read_text())
    def test_only_original_QA_source_terms_and_measured_five_chapter_delta(self):
        original={}
        for line in WOODLAND.read_text().splitlines():
            if line.startswith("gm"):
                word_id,word,display,tags=line.split("\t")
                original[word_id]=(word,display,set(tags.split("|")))
        self.assertEqual(159,len(original))
        draft=tsv(INPUT)
        for row in draft:
            self.assertIn(row["word_id"],original)
            answer,display,tags=original[row["word_id"]]
            self.assertEqual((answer,display),(row["answer"],row["display"]))
            self.assertIn("woodland:"+row["word_subtopic"]+":"+row["tier"],tags)
            self.assertIn(row["word_subtopic"],{"trees","habitats"})
        baseline=tsv(WS52)
        self.assertEqual(260,len(baseline))
        old={c:{r["answer"] for r in baseline if r["chapter"]==str(c)}
             for c in (3,4,6,7,8)}
        new=dict(old)
        new[7]={r["answer"] for r in draft}
        overlap=lambda groups,other:len(groups[7]&groups[other])
        self.assertEqual(48,overlap(old,3))
        self.assertEqual(18,overlap(new,3))
        self.assertTrue(all(overlap(new,c)<30 for c in (3,4,6,8)))
        self.assertEqual(124,len(set.union(*old.values())))
        self.assertEqual(129,len(set.union(*new.values())))
    def test_existing_library_release_and_independent_evidence_contracts(self):
        self.assertIn("const val enabled = false",RELEASE.read_text())
        self.assertIn('volume("woodland", "Woodland", WordTheme.WOODLAND, 171L)',
                      PILOT.read_text())
        code=KOTLIN.read_text()
        for needle in ("WordSearchGeneratorV5.generate(",
                       "BookBoardDigest.sha256(",
                       "BookPageSessionRules.puzzle(",
                       "assertEquals(129, newUnique)",
                       "assertEquals(18, after)",
                       "QA_research_only_NOT_published",
                       "ws53-quiet-clearings-frozen-boards.tsv",
                       "ws53-woodland-overlap-delta.tsv"):
            self.assertIn(needle,code)
        for workflow in CI:
            code=workflow.read_text()
            for marker in (
                "test_ws53_quiet_clearings.py",
                "feature/ws53-quiet-clearings-distinct-editorial-2026-11-20",
                "GridMates-WS53-Quiet-Clearings-QA",
                "core/build/ws53-quiet-clearings-frozen-boards.tsv",
                "core/build/ws53-woodland-overlap-delta.tsv"
            ):
                self.assertIn(marker,code)

if __name__=="__main__":
    unittest.main()
