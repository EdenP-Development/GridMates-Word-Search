"""WS56 Quiet Clearings groundcover manuscript, old-source isolation and 6-chapter QA."""
from pathlib import Path
from itertools import combinations
import csv
import unittest

ROOT=Path(__file__).resolve().parents[2]
SOURCE=ROOT/"core/src/test/resources/reviewed-packs/en-woodland-v1.tsv"
CANON=ROOT/"data/lexicon/authoring/chapter-recipes-v1/woodland-ws56-clearings-manuscript.tsv"
MIRROR=ROOT/"core/src/test/resources/chapter-recipes/ws56-clearings-manuscript.tsv"
KOTLIN=ROOT/"core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/QuietClearingsBookwideLabTest.kt"
A=ROOT/"data/lexicon/authoring/chapter-recipes-v1"
WORKFLOWS=(ROOT/".github/workflows/ws01-android-qa.yml",ROOT/".github/workflows/ws12g-qa-art-lab.yml")

def rows(path):
    with path.open(newline="") as f:
        return list(csv.DictReader(f,delimiter="\t"))

def chapters():
    base=rows(A/"woodland-ws52-manuscript.tsv")
    group={i:{r["answer"] for r in base if r["chapter"]==str(i)}
            for i in (3,4)}
    group[5]={r["answer"] for r in rows(A/"birdsong-ws51-qa-manuscript.tsv")}
    group[6]={r["answer"] for r in rows(A/"woodland-ws54-stream-manuscript.tsv")}
    group[7]={r["answer"] for r in rows(A/"quiet-clearings-ws53-revision.tsv")}
    group[8]={r["answer"] for r in rows(A/"woodland-ws55-wildlife-manuscript.tsv")}
    return group

class QuietClearingsSourceGuard(unittest.TestCase):
    def test_exact_source_words_and_editorial_provenance(self):
        self.assertEqual(CANON.read_bytes(),MIRROR.read_bytes())
        original={}
        for line in SOURCE.read_text().splitlines():
            if line.startswith("gm"):
                ident,word,display,topics=line.split("\t")
                original[ident]=(word,display,topics)
        self.assertEqual(159,len(original))
        terms=rows(CANON)
        self.assertEqual(52,len(terms))
        self.assertEqual(52,len({r["answer"] for r in terms}))
        self.assertEqual(52,len({r["word_id"] for r in terms}))
        source=[r for r in terms if r["provenance"]=="existing_woodland_QA_not_release"]
        new=[r for r in terms if r["provenance"]=="ws56_clearings_candidate_unapproved"]
        self.assertEqual((16,36),(len(source),len(new)))
        for t in source:
            w,d,topic=original[t["word_id"]]
            self.assertEqual((w,d),(t["answer"],t["display"]))
            self.assertIn("woodland:"+t["subtopic"]+":"+t["tier"],topic)
        for t in new:
            self.assertTrue(t["word_id"].startswith("ws56q"))
            self.assertNotIn(t["word_id"],original)
            self.assertEqual("clearings",t["subtopic"])
            self.assertEqual("".join(x for x in t["display"].upper() if x.isalpha()),
                t["answer"])
        avg=[]
        titles=["Open Glade","Sunlit Spaces","Woodland Stillness"]
        for page,count,cap in [(1,12,7),(2,16,10),(3,24,15)]:
            group=[r for r in terms if r["page"]==str(page)]
            self.assertEqual(count,len(group))
            self.assertTrue(all(r["chapter"]=="7" and r["chapter_title"]=="Quiet Clearings"
                and r["page_title"]==titles[page-1] for r in group))
            self.assertTrue(all(4<=len(r["answer"])<=cap for r in group))
            self.assertTrue(all(a["answer"] not in b["answer"]
                for a in group for b in group if a is not b))
            if page==1:
                self.assertTrue(all(r["tier"]=="familiar" for r in group))
            avg.append(sum(len(r["answer"]) for r in group)/len(group))
        self.assertTrue(avg[0]<avg[1]<avg[2])
    def test_six_chapter_vocabulary_comparison(self):
        before=chapters()
        new=dict(before,**{7:{r["answer"] for r in rows(CANON)}})
        uniq=lambda x:len(set.union(*x.values()))
        pair=lambda x:sum(len(x[a]&x[b]) for a,b in combinations((3,4,5,6,7,8),2))
        self.assertEqual((233,264),(uniq(before),uniq(new)))
        self.assertEqual((82,49),(pair(before),pair(new)))
        for chapter,old,now in [(3,18,14),(4,26,0),(5,1,0),(6,3,1),(8,0,0)]:
            self.assertEqual((old,now),
                (len(before[7]&before[chapter]),len(new[7]&new[chapter])))
    def test_runtime_and_independent_report_isolation(self):
        release=(ROOT/"app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt").read_text()
        self.assertIn("const val enabled = false",release)
        k=KOTLIN.read_text()
        for name in ("WordSearchGeneratorV5.generate(",
                     "BookPageSessionRules.puzzle(", "BookBoardDigest.sha256(",
                     "ws56-clearings-frozen-pages.tsv","ws56-bookwide-overlap-delta.tsv",
                     "ws56-bookwide-word-frequency.tsv"):
            self.assertIn(name,k)
        for path in WORKFLOWS:
            w=path.read_text()
            for marker in ("test_ws56_quiet_clearings.py",
                           "GridMates-WS56-Quiet-Clearings-QA",
                           "core/build/ws56-clearings-frozen-pages.tsv"):
                self.assertIn(marker,w)

if __name__=="__main__":
    unittest.main()
