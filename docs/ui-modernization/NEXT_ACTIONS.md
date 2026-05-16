# Next Actions

1. Start Phase 4 input/hitbox/GUI-scale correctness.
2. Fix only clear editor UI correctness issues found in the audit:
   - dialog overflow adjustment should keep dialogs on-screen,
   - scroll child visibility should not be overwritten incorrectly,
   - overlay/scissor drawing should use the same scaled scissor path as normal scroll rendering,
   - avoid expanding hit areas from invisible/offscreen child widgets where it affects editor UI.
3. Compile LDLib after the Phase 4 pass.
4. Review diffs for accidental behavior changes outside GUI containment/input.
5. Commit the LDLib Phase 4 checkpoint, then update this Photon tracker with the commit hash and validation results.
6. Continue into Phase 5 scrolling/clipping/containment after Phase 4 is clean.
