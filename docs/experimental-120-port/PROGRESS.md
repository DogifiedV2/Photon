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
- [x] LDLib source comparison worktree created.
- [x] Photon source comparison worktree created.
- [x] LDLib 1.20 editor/UI dependencies identified.
- [x] LDLib experimental artifact published to Maven local.
- [x] Photon dependency switched to experimental LDLib artifact.
- [ ] Photon 1.20 runtime/object model ported.
- [ ] Photon 1.20 editor/UI ported.
- [ ] Commands/save/load/runtime integration ported.
- [ ] Rendering/mixins adapted to MC 1.18.2.
- [x] Forge compile passes.
- [ ] Forge dev client launches.
- [ ] Editor opens.
- [ ] Basic create/save/load/spawn smoke passes.
- [ ] Bug-triage checklist completed.

## Latest notes

- LDLib bridge slice 3 completed: minimal `ShaderSSBO` was added/published for 1.20 trail particle compile support.
- LDLib bridge slice 2 completed: JOML, scene object/transform bridge classes, and SceneWidget compatibility helpers were added, published to Maven local, and Photon compile still passes.
- LDLib bridge slice 1 completed: configurator container/selector and layout enums were added, published to Maven local, and Photon compile still passes.
- Created `docs/experimental-120-port/INVENTORY.md` with the Photon 1.20 -> LDLib 1.18 API gap list.
- Created detached source worktrees for comparison:
  - Photon 1.20.1: `/tmp/photon-120` at `507499f` (`origin/1.20.1`)
  - LDLib 1.20.1: `/tmp/ldlib-120` at `5d68947b` (`origin/1.20.1`)
- LDLib experimental artifact `com.lowdragmc.ldlib:ldlib-forge-1.18.2:1.0.26-120port.1` was published to Maven local.
- Photon now resolves `ldlib-forge-1.18.2:1.0.26-120port.1`; `:photon-forge:dependencyInsight --dependency ldlib-forge --configuration modImplementation` passed on 2026-05-16.
- Photon Forge compile passed against the experimental LDLib artifact on 2026-05-16.
- The user wants a direct Photon 1.20.1 UI/runtime/editor feature port to 1.18.2, including needed LDLib 1.20.1 UI/editor support.
- No old Photon 1.18 effect migration is required because there are no old effects to preserve.
- Use Photon 1.20.1 as the main source of truth. Use Photon 1.21 only as later visual/feature inspiration where it does not imply LDLib2.
- Stay on LDLib1. Do not port LDLib2.

## Next action

1. Start Photon 1.20 runtime/object model port, adding Photon-side JOML dependency as needed.
2. Compile after each file group and fill any remaining LDLib bridge gaps surfaced by errors.
3. Continue with Photon editor files once runtime/object classes compile.
