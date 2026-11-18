# 2026-11-18 — WS42 v104 handset verification

**Signed v104 passed Android QA and is ready for owner handset review.**

## Starting point

The owner asked to continue from [WS42 handset polish](2026-11-17_WS42_HANDSET_POLISH.md). [Draft PR #153](https://github.com/PlayerEden/GridMates-Word-Search/pull/153) started this pass at `c8daac26a44a0682a3f219d71a672b340e2b5d5d`. The last fully verified build was [signed v103](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302499/artifacts/14397980688), SHA-256 `19949e41b1ed9165781c7a39b89857c9996bd5e27cfe25227dc5afabcfe25359`.

## Device test recovery

[The original Android job and retry](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44241271940) both preserved the saved 20×20 grid before the emulator disconnected at the Library. Codex changed the WS42 runner to SwiftShader, increased emulator RAM and streamed Android diagnostics to the evidence artifact. [That run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44251399920) also lost the emulator at the Library. An [earlier cabinet pass](2026-11-17_WS35_LIBRARY_BINDING_DEPTH.md) had recorded similar emulator interruptions.

The final test source `f2304c5d81a57cc1a29197277702d92645460c96` boots independent API 35 emulators for the signed upgrade, the full Library flow and the regression suite. The Library session uses SwiftShader with ANGLE. The harness records separate receipts and logs. Only verification scripts and workflow setup changed after the original v104 app commit.

## Verified APK

| Check | Result |
| --- | --- |
| Android workflow | [Run 44252800362](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44252800362) passed |
| Native screenshots, receipts and Android test report | [Artifact 14405675818](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44252800362/artifacts/14405675818) |
| Independent debug build and release isolation | [Run 44252800303](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44252800303) passed |
| Signed v104 APK | [Artifact 14404433051](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44252800362/artifacts/14404433051) |
| Package | `com.playereden.gridmates.wordsearch.qa`, code 104, name `0.9.60-ws42-qa` |
| APK size and SHA-256 | 34,207,390 bytes, `9c0b68612dd61d192a6382ce35c2ffc04fce39584ae7f9753e1b7b56e4782033` |
| QA signer SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| Reproducibility | All 372 APK entries match the independent build. All 228 assets match v103. |

## Outcome

- The native Android upgrade preserved the existing v103 save bytes and resumed the same 20×20 puzzle. Home controls remained accessible in place while its contents scrolled. The short board tip and expanded gesture help both appeared.
- The native Library flow opened Garden, selected chapter eight, jumped to and played page 19, and checked that the first hint named a word and a row in Game notices.
- All 31 instrumented Android tests passed in 84.347 seconds, including chapter navigation and notice history at 200% text, actual word traces, save migration and backup recovery.

The owner phone playthrough and the two-physical-phone acceptance test remain open. v103 is the previous verified APK. [WS43 Fall authoring](https://github.com/PlayerEden/GridMates-Word-Search/pull/154) supplies the first Fall pack. The next development pass can prepare page recipes using its six reviewed subtopics.
