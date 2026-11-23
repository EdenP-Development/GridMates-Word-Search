# Fall vocabulary, first QA edition

The Fall snapshot contains 150 English answers. It adds 117 canonical IDs and reuses 33 approved rows from the first GridMates QA catalog. Every answer has a Fall subtopic and a `seasons/autumn` assignment.

| Fall subtopic | Familiar | Intermediate |
| --- | ---: | ---: |
| Turning leaves | 20 | 5 |
| Crisp air | 20 | 5 |
| Harvest | 20 | 5 |
| Woodland season | 20 | 5 |
| Autumn kitchen | 20 | 5 |
| Fall traditions | 20 | 5 |

144 answers fit a 12×12 grid by length, 149 fit 15×15, and all 150 fit 20×20. Page planning still needs full-board playthroughs for repeated stems, difficulty and placement.

This edition is for internal editorial QA. The vocabulary, chapter recipes and matched book art need owner review before release. The Android build currently reads the first Woodland, Space, Garden and Everyday packs.

Reproduce [the frozen snapshot](snapshots/en-fall-v1.json) from the repository root.

```bash
python3 tools/lexicon/export_snapshot.py \
  --data-dir data/lexicon/authoring/fall-v1 \
  --pack-id en-fall-v1 --topics fall \
  --output-dir data/lexicon/authoring/fall-v1/snapshots
```

Give an edited export a new pack version. Retain the canonical ID when making a display or classification correction to the same answer.
