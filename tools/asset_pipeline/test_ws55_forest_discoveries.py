"""WS55 editorial source and all six Woodland chapter repetition guards."""
import csv
from pathlib import Path
from itertools import combinations
import unittest

ROOT=Path(__file__).resolve().parents[2]
def rows(p):
    with (ROOT/p).open(newline="") as f:
        return list(csv.DictReader(f,delimiter="\t"))
A="data/lexicon/authoring/chapter-recipes-v1/"
M=A+"woodland-ws55-discoveries-manuscript.tsv"
FIX="core/src/test/resources/chapter-recipes/ws55-discoveries-manuscript.tsv"
SRC="core/src/test/resources/reviewed-packs/en-woodland-v1.tsv"

class ForestDiscoveriesSourceQA(unittest.TestCase):
    def test_provenance_pages_and_repetition(self):
        self.assertEqual((ROOT/M).read_bytes(),(ROOT/FIX).read_bytes())
        terms=rows(M)
        self.assertEqual(52,len(terms))
        self.assertEqual(52,len({x["answer"] for x in terms}))
        self.assertEqual(52,len({x["word_id"] for x in terms}))
        src={}
        for line in (ROOT/SRC).read_text().splitlines():
            if line.startswith("gm"):
                id,word,display,topics=line.split("\t")
                src[id]=(word,display,topics)
        original=[x for x in terms if x["provenance"]=="existing_woodland_QA_not_release"]
        proposed=[x for x in terms if x["provenance"]=="ws55_wildlife_signs_candidate_unapproved"]
        self.assertEqual(26,len(original))
        self.assertEqual(26,len(proposed))
        for x in original:
            word,display,topic=src[x["word_id"]]
            self.assertEqual((word,display),(x["answer"],x["display"]))
            self.assertIn("woodland:"+x["subtopic"]+":"+x["tier"],topic)
        for x in proposed:
            self.assertTrue(x["word_id"].startswith("ws55w"))
            self.assertNotIn(x["word_id"],src)
            self.assertEqual("wildlife-signs",x["subtopic"])
        means=[]
        for page,count,cap in [(1,12,7),(2,16,10),(3,24,15)]:
            group=[x for x in terms if x["page"]==str(page)]
            self.assertEqual(count,len(group))
            self.assertTrue(all(x["chapter"]=="8" and
                x["chapter_title"]=="Forest Discoveries" for x in group))
            self.assertTrue(all(x["answer"].isalpha() and
                x["answer"].isupper() and 3<=len(x["answer"])<=cap for x in group))
            self.assertTrue(all(x["answer"] not in y["answer"]
                for x in group for y in group if x is not y))
            if page==1:
                self.assertTrue(all(x["tier"]=="familiar" for x in group))
            means.append(sum(len(x["answer"]) for x in group)/len(group))
        self.assertTrue(means[0]<means[1]<means[2])
        ws52=rows(A+"woodland-ws52-manuscript.tsv")
        old={c:{x["answer"] for x in ws52 if x["chapter"]==str(c)}
             for c in (3,4,8)}
        old[5]={x["answer"] for x in rows(A+"birdsong-ws51-qa-manuscript.tsv")}
        old[6]={x["answer"] for x in rows(A+"woodland-ws54-stream-manuscript.tsv")}
        old[7]={x["answer"] for x in rows(A+"quiet-clearings-ws53-revision.tsv")}
        revised=dict(old,**{8:{x["answer"] for x in terms}})
        def unique(g): return len(set.union(*g.values()))
        def pairs(g): return sum(len(g[a]&g[b]) for a,b in combinations((3,4,5,6,7,8),2))
        self.assertEqual((202,231),(unique(old),unique(revised)))
        self.assertEqual((139,86),(pairs(old),pairs(revised)))
        for chapter,prev,new in [(3,12,4),(4,25,7),(5,0,1),(6,10,1),(7,20,1)]:
            self.assertEqual((prev,new),(len(old[chapter]&old[8]),len(revised[chapter]&revised[8])))
    def test_research_isolation(self):
        release=(ROOT/"app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt").read_text()
        self.assertIn("const val enabled = false",release)
        code=(ROOT/"core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/ForestDiscoveriesWildlifeLabTest.kt").read_text()
        for s in ("WordSearchGeneratorV5.generate(", "BookBoardDigest.sha256(",
                  "BookPageSessionRules.puzzle(", "ws55-discoveries-frozen-pages.tsv",
                  "ws55-discoveries-chapter-overlaps.tsv",
                  "ws55-bookwide-word-frequency.tsv"):
            self.assertIn(s,code)
        for workflow in ("ws01-android-qa.yml","ws12g-qa-art-lab.yml"):
            w=(ROOT/".github/workflows"/workflow).read_text()
            for s in ("test_ws55_forest_discoveries.py",
                      "GridMates-WS55-Forest-Discoveries-QA",
                      "core/build/ws55-discoveries-frozen-pages.tsv"):
                self.assertIn(s,w)

if __name__=="__main__":
    unittest.main()
