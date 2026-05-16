# Experimental 1.20 -> 1.18 Port Progress

Date: 2026-05-16

## Current branches

- Photon: `experiment/1.20-direct-port-to-1.18`
- LDLib-MultiLoader: `experiment/1.20-ui-to-1.18`

## Baseline commits

- Photon experiment branch created from 1.18.2 at: `77c6a51`
- LDLib experiment branch created from 1.18.2 at: `be66528a`

## Status

- [x] Experimental LDLib branch created from 1.18.2.
- [x] Experimental Photon branch created from 1.18.2.
- [x] Photon plan docs created.
- [ ] LDLib source comparison worktree created.
- [ ] Photon source comparison worktree created.
- [ ] LDLib 1.20 editor/UI dependencies identified.
- [ ] LDLib experimental artifact published to Maven local.
- [ ] Photon dependency switched to experimental LDLib artifact.
- [ ] Photon 1.20 runtime/object model ported.
- [ ] Photon 1.20 editor/UI ported.
- [ ] Commands/save/load/runtime integration ported.
- [ ] Rendering/mixins adapted to MC 1.18.2.
- [ ] Forge compile passes.
- [ ] Forge dev client launches.
- [ ] Editor opens.
- [ ] Basic create/save/load/spawn smoke passes.
- [ ] Bug-triage checklist completed.

## Latest notes

- The user wants a direct Photon 1.20.1 UI/runtime/editor feature port to 1.18.2, including needed LDLib 1.20.1 UI/editor support.
- No old Photon 1.18 effect migration is required because there are no old effects to preserve.
- Use Photon 1.20.1 as the main source of truth. Use Photon 1.21 only as later visual/feature inspiration where it does not imply LDLib2.
- Stay on LDLib1. Do not port LDLib2.

## Next action

1. Create/read temporary source worktrees for Photon 1.20.1 and LDLib 1.20.1.
2. Inventory exact Photon 1.20 classes that depend on LDLib 1.20 editor/UI APIs.
3. Start LDLib bridge work first, publish experimental LDLib to Maven local, then wire Photon to it.
