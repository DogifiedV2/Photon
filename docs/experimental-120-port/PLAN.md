# Experimental Photon 1.20.1 -> 1.18.2 Direct Port Plan

Date: 2026-05-16

## Objective

Create an experimental 1.18.2 branch that directly ports the Photon 1.20.1 editor/runtime/UI stack onto the existing 1.18.2 Photon port.

This is a direct port experiment, not a small polish patch. The target is to make the 1.18.2 dev client use the 1.20.1-style Photon editor and engine model as much as feasible while staying on Minecraft 1.18.2 and LDLib1.

## Branches

- Photon repo: `/Users/rubenvancraenenbroeck/IdeaProjects/Photon`
  - branch: `experiment/1.20-direct-port-to-1.18`
  - based on: `1.18.2`
- LDLib repo: `/Users/rubenvancraenenbroeck/IdeaProjects/LDLib-MultiLoader`
  - branch: `experiment/1.20-ui-to-1.18`
  - based on: `1.18.2`

## Source references

Use these as references, not as blind copy targets:

- Photon 1.20.1 branch: `origin/1.20.1`
- Photon 1.18.2 branch: `1.18.2`
- LDLib-MultiLoader 1.20.1 branch: `origin/1.20.1`
- LDLib-MultiLoader 1.18.2 branch: `1.18.2`
- Photon 1.21 branch: visual/feature inspiration only after 1.20 parity is working; do not port LDLib2.

Recommended workflow for comparing branches without constantly switching the active worktree:

```bash
git worktree add /tmp/photon-120 origin/1.20.1
git -C /Users/rubenvancraenenbroeck/IdeaProjects/LDLib-MultiLoader worktree add /tmp/ldlib-120 origin/1.20.1
```

## Non-goals

- Do not port LDLib2.
- Do not rewrite the project for Minecraft 1.21 APIs.
- Do not preserve old 1.18 Photon effect-file compatibility unless required for basic operation.
- Do not spend time on old-effect migration; the user has no old effects to preserve.
- Do not keep both old 1.18 and new 1.20 editor/runtime systems unless temporarily needed for staging.
- Do not refactor unrelated systems just because they are nearby.

## Success criteria

The experiment is successful when the 1.18.2 Forge dev client can:

1. Compile Photon against the experimental LDLib artifact.
2. Launch the Forge client.
3. Open the Photon editor.
4. Show the 1.20-style editor layout and visual behavior as closely as feasible.
5. Use the 1.20-style FX object/runtime model.
6. Add particle, beam, and trail FX objects.
7. Show/use the FX object list/subtree editor.
8. Configure visible fields, toggles, dropdowns, and resources.
9. Save/export an effect.
10. Reload the saved effect.
11. Spawn an effect in-world on block/entity paths.
12. Remove active block/entity effects if the 1.20 commands are ported.
13. Pass the bug-triage/review gates in `BUG_TRIAGE.md`.

## Phase 0 - Baseline and branch setup

- Confirm both experimental branches exist and are based on the 1.18.2 branches.
- Record baseline commits in `PROGRESS.md`.
- Create temporary 1.20 source worktrees or otherwise document exact source commits.
- Run or at least record the known-good 1.18 compile command.

Completion gate:

- Branches exist.
- Docs exist.
- No unrelated working tree changes are mixed in.

## Phase 1 - LDLib 1.20 UI/editor support onto 1.18

Goal: port only the LDLib 1.20.1 editor/UI/configurator/scene pieces needed by Photon 1.20.1.

Focus areas:

- editor base widgets used by Photon 1.20
- scene editor support used by `ParticleScene` / `ParticleScenePanel`
- object list/tree/drag support used by `FXObjectsList`
- configurators and accessor behavior used by Photon resources/settings
- resource panel behavior and visual assets/textures/icons used by Photon
- visible toggle/dropdown/text field behavior matching the 1.20 UI

Avoid:

- unrelated LDLib systems not required by Photon
- LDLib2 concepts/classes
- broad style rewrites not needed to run Photon

Completion gate:

- LDLib compiles/publishes to Maven local with an experimental 1.18.2 version.
- Photon can resolve that exact experimental artifact.
- `LDLIB_NOTES.md` lists the LDLib classes/features ported and any intentionally skipped pieces.

## Phase 2 - Wire Photon to experimental LDLib

- Update Photon dependency coordinates to the experimental LDLib artifact.
- Verify dependency resolution with Gradle.
- Keep the version clearly experimental, e.g. `1.0.26-120port.1` or similar.

Completion gate:

- `:photon-forge:dependencyInsight` shows the experimental LDLib artifact.
- No accidental dependency on normal release LDLib for the Forge dev run.

## Phase 3 - Photon 1.20 runtime/object model

Port/adapt the 1.20 runtime model:

- `FXRuntime`
- `FXData`
- `IFXObject`
- `FXObject`
- `EmptyFXObject`
- `client/gameobject/emitter/**`
- modern emitter configs/settings/shapes needed by those objects

Expected result:

```text
FXRuntime
└─ root IFXObject
   ├─ particle object
   ├─ beam object
   └─ trail object
```

Completion gate:

- Photon common code compiles or compile blockers are logged with exact errors.
- Runtime serialization/deserialization is internally consistent for new 1.20-style effects.
- No old-effect migration work is required unless basic new-format loading depends on it.

## Phase 4 - Photon 1.20 editor/UI

Port/adapt the 1.20 editor stack:

- `FXEditor`
- `FXProject`
- `FXObjectsList`
- `ParticleScene`
- `ParticleScenePanel`
- `SceneMenu`
- `ParticleInfoView`
- resource classes and containers
- number function/configurator/accessor pieces used by the editor

UI target:

- match the 1.20 Photon editor look/feel as closely as feasible on LDLib1/MC 1.18
- visible panel backgrounds
- visible/clickable toggles
- visible dropdown/select fields
- readable text inputs
- usable resources panel
- object/subtree list visible and usable
- clean particle info/preview controls

Completion gate:

- Forge compile passes.
- Editor opens in dev client.
- User can add/select/configure basic FX objects without invisible controls.

## Phase 5 - Commands, save/load, and runtime integration

Port/adapt:

- editor launch path
- save/export/load
- block effect command/runtime
- entity effect command/runtime
- remove block/entity effect commands if feasible
- networking packets touched by these flows

Completion gate:

- Save/export writes a new-format effect.
- Reloading the saved effect works.
- Block/entity spawn path works in a dev world.

## Phase 6 - Rendering, shaders, and mixins

Adapt only what is necessary for MC 1.18.2:

- particle render signatures
- beam/trail render paths
- shader loading differences
- framebuffer/post-processing differences
- Forge event differences
- mixin target differences

Prefer the smallest 1.18-compatible adaptation over a 1.21-style render rewrite.

Completion gate:

- Particle/beam/trail render in editor preview.
- Spawned effects render in-world.
- No startup crash from mixin target drift.

## Phase 7 - Bug-triage and review pass

Run the full checklist in `BUG_TRIAGE.md` after major phases and before calling the goal complete.

Required checks:

- diff scope audit
- compile/build check
- dependency resolution check
- file-by-file review of LDLib changes
- file-by-file review of Photon runtime/editor/render changes
- runtime smoke tests
- classification of every issue found as confirmed bug, likely bug, setup/config issue, docs/UX issue, intended behavior, deferred, or needs more information
- fix confirmed/likely bugs where safe
- document deferred/non-blocking items with exact evidence

Completion gate:

- No unresolved confirmed blocker remains undocumented.
- All real bugs found during the triage pass are either fixed or explicitly logged as deferred with reason/evidence.

## Commit discipline

Commit after each coherent successful subphase in the relevant repo.

Suggested commit pattern:

- LDLib: `change: start 1.20 ui bridge branch docs`
- LDLib: `change: backport 1.20 editor widget support`
- Photon: `change: document 1.20 direct port experiment`
- Photon: `change: wire experimental ldlib artifact`
- Photon: `change: port 1.20 fx runtime model`
- Photon: `change: port 1.20 editor ui`
- Photon: `fix: adapt 1.20 render path to 1.18`
- Photon: `fix: resolve triaged editor regressions`

Do not batch the entire experiment into one final commit.
