"""WS55: Forest Discoveries chapter QA source and book-level repetition guard."""
from pathlib import Path
from itertools import combinations
import csv
import unittest

ROOT=Path(__file__).resolve().parents[2]
SOURCE=ROOT/"core/src/test/resources/reviewed-packs/en-woodland-v1.tsv"
MANUSCRIPT=ROOT/"data/lexicon/authoring/chapter-recipes-v1/woodland-ws55-wildlife-manuscript.tsv"
FIXTURE=ROOT/"core/src/test/resources/chapter-recipes/ws55-wildlife-manuscript.tsv"
WS52=ROOT/"data/lexicon/authoring/chapter-recipes-v1/woodland-ws52-manuscript.tsv"
WS53=ROOT/"data/lexicon/authoring/chapter-recipes-v1/quiet-clearings-ws53-revision.tsv"
WS51=ROOT/"data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv"
WS54=ROOT/"data/lexicon/authoring/chapter-recipes-v1/woodland-ws54-stream-manuscript.tsv"
KOTLIN=ROOT/"core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/ForestDiscoveriesBookwideLabTest.kt"
PILOT=ROOT/"app/src/debug/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
RELEASE=ROOT/"app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
WORKFLOWS=(ROOT/".github/workflows/ws01-android-qa.yml",
           ROOT/".github/workflows/ws12g-qa-art-lab.yml")

def rows(path):
    with path.open(newline="") as f:
        return list(csv.DictReader(f,delimiter="\t"))

def chapters():
    base=rows(WS52)
    groups={n:{r["answer"] for r in base if r["chapter"]==str(n)}
            for n in (3,4,8)}
    groups[7]={r["answer"] for r in rows(WS53)}
    groups[6]={r["answer"] for r in rows(WS54)}
    groups[5]={r["answer"] for r in rows(WS51)}
    return groups

class ForestDiscoveriesEditorialContract(unittest.TestCase):
    def test_authoring_is_single_source_and_progressive(self):
        self.assertEqual(MANUSCRIPT.read_bytes(), FIXTURE.read_bytes())
        data=rows(MANUSCRIPT)
        self.assertEqual(52,len(data))
        self.assertEqual(52,len({r["answer"] for r in data}))
        self.assertEqual(52,len({r["word_id"] for r in data}))
        self.assertTrue(all(r["chapter"]=="8" and
            r["chapter_title"]=="Forest Discoveries" for r in data))
        self.assertEqual(["Woodland Neighbors","Signs Along the Path",
            "Wildlife Field Notes"],
            [next(r["page_title"] for r in data if r["page"]==str(i))
             for i in (1,2,3)])
        lengths=[]
        for number,count,cap in [(1,12,7),(2,16,10),(3,24,15)]:
            subset=[r for r in data if r["page"]==str(number)]
            self.assertEqual(count,len(subset))
            self.assertTrue(all(r["answer"].isupper() and r["answer"].isalpha()
                and 4<=len(r["answer"])<=cap for r in subset))
            self.assertTrue(all("".join(c for c in r["display"].upper() if "A"<=c<="Z")
                ==r["answer"] for r in subset))
            if number==1:
                self.assertTrue(all(r["tier"]=="familiar" for r in subset))
            self.assertTrue(all(a["answer"] not in b["answer"]
                for a in subset for b in subset if a is not b))
            lengths.append(sum(len(r["answer"]) for r in subset)/len(subset))
        self.assertTrue(lengths[0]<lengths[1]<lengths[2])

    def test_22_original_and_30_unapproved_wildlife_sources(self):
        original={}
        for line in SOURCE.read_text().splitlines():
            if line.startswith("gm"):
                ident,answer,display,tags=line.split("\t")
                original[ident]=(answer,display,set(tags.split("|")))
        self.assertEqual(159,len(original))
        data=rows(MANUSCRIPT)
        existing=[r for r in data if r["provenance"]=="existing_woodland_QA_not_release"]
        proposals=[r for r in data if r["provenance"]=="ws55_wildlife_candidate_unapproved"]
        self.assertEqual(22,len(existing))
        self.assertEqual(30,len(proposals))
        for row in existing:
            word,display,topics=original[row["word_id"]]
            self.assertEqual((word,display),(row["answer"],row["display"]))
            self.assertIn("woodland:forest-animals:"+row["tier"],topics)
        self.assertTrue(all(r["word_id"].startswith("ws55f") and
            r["word_id"] not in original and r["subtopic"]=="wildlife-signs"
            for r in proposals))
        self.assertTrue(all(r["subtopic"]=="forest-animals" for r in existing))

    def test_six_chapter_gains_and_non_release_isolation(self):
        old=chapters()
        new=dict(old)
        new[8]={r["answer"] for r in rows(MANUSCRIPT)}
        self.assertEqual(52,len(new[8]))
        self.assertEqual(202,len(set.union(*old.values())))
        self.assertEqual(233,len(set.union(*new.values())))
        def pairwise(groups):
            return sum(len(groups[a]&groups[b])
                for a,b in combinations((3,4,5,6,7,8),2))
        self.assertEqual(139,pairwise(old))
        self.assertEqual(82,pairwise(new))
        for chapter,prior,current in [(3,12,0),(4,25,7),
                                      (5,0,0),(6,10,3),(7,20,0)]:
            self.assertEqual(prior,len(old[8]&old[chapter]))
            self.assertEqual(current,len(new[8]&new[chapter]))
        self.assertIn("const val enabled = false",RELEASE.read_text())
        self.assertIn('volume("woodland", "Woodland", WordTheme.WOODLAND, 171L)',
                      PILOT.read_text())
        test=KOTLIN.read_text()
        for mark in ("WordSearchGeneratorV5.generate(",
            "BookBoardDigest.sha256(","BookPageSessionRules.puzzle(",
            "assertEquals(233, unique(revised))",
            "ws55-forest-frozen-pages.tsv","ws55-bookwide-overlap-delta.tsv",
            "ws55-bookwide-word-frequency.tsv",
            "UNAPPROVED_QA_RESEARCH_ONLY"):
            self.assertIn(mark,test)
        for workflow in WORKFLOWS:
            code=workflow.read_text()
            for mark in ("test_ws55_forest_discoveries.py",
                "GridMates-WS55-Forest-Bookwide-QA",
                "feature/ws55-forest-discoveries-editorial-2026-11-21",
                "core/build/ws55-forest-frozen-pages.tsv",
                "core/build/ws55-bookwide-overlap-delta.tsv",
                "core/build/ws55-bookwide-word-frequency.tsv"):
                self.assertIn(mark,code)

if __name__=="__main__":
    unittest.main()
