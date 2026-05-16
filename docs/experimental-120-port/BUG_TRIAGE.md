# Bug Triage and Review Checklist

This file is a required gate for the experimental 1.20 -> 1.18 port. Run it after major phases and again before declaring the goal complete.

## Triage rules

For every issue found, classify it as one of:

- Confirmed bug
- Likely bug
- Setup/config issue
- Documentation/UX issue
- Intended behavior
- Deferred/non-blocking
- Needs more information

Each issue entry must include:

- observed behavior
- expected behavior
- evidence: file path, log line, command result, screenshot, or reproduction step
- severity: blocker/high/medium/low
- fix/defer decision
- verification after fix if fixed

## Required automated/local checks

Run where relevant:

```bash
git diff --check
```

```bash
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" :photon-forge:compileJava --no-daemon --stacktrace
```

After LDLib wiring:

```bash
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" :photon-forge:dependencyInsight --dependency ldlib-forge --configuration modImplementation --no-daemon
```

Run LDLib publish/compile checks from the LDLib repo after each LDLib phase.

## Diff review checklist

### LDLib changes

- [ ] No LDLib2 imports/classes introduced.
- [ ] MC 1.20-only APIs adapted to 1.18 equivalents.
- [ ] UI widget rendering works with 1.18 rendering stack.
- [ ] Configurator behavior does not make controls invisible.
- [ ] Text inputs, dropdowns, and toggles have visible off/on/hover states.
- [ ] Resource drag/drop or assignment behavior is clear.
- [ ] Changes are limited to Photon-required editor/UI support where possible.

### Photon runtime/object changes

- [ ] `FXRuntime` lifecycle is valid on 1.18.
- [ ] `FXData` save/load works for new-format effects.
- [ ] `IFXObject`/`FXObject` transform, visibility, copy, remove, and reset behavior is valid.
- [ ] Particle/beam/trail emitters tick correctly.
- [ ] Subtree/object list operations do not orphan objects.
- [ ] No old-format migration assumptions block new effect creation.

### Photon editor/UI changes

- [ ] Editor opens without crash.
- [ ] Object/subtree list is visible and selectable.
- [ ] Config panel fields are readable and clickable.
- [ ] Boolean toggles have visible off and on states.
- [ ] Dropdown/select fields have visible backgrounds.
- [ ] Text input fields have visible contrast.
- [ ] Particle info panel has readable background/controls.
- [ ] Resources panel can apply resources or clearly indicates usage.
- [ ] Save/export UI has visible name input field.

### Rendering/mixins

- [ ] No mixin target failure during launch.
- [ ] Particle rendering works in editor preview.
- [ ] Beam rendering works in editor preview.
- [ ] Trail rendering works in editor preview.
- [ ] In-world spawned effects render.
- [ ] Shader/post-processing differences are either adapted or safely disabled/deferred.

### Commands/runtime

- [ ] Editor command opens the editor.
- [ ] Block effect command works.
- [ ] Entity effect command works.
- [ ] Remove commands work if ported.
- [ ] Network packets used by these flows are version-compatible.

## Issue ledger

| ID | Classification | Severity | Area | Evidence | Decision | Verification |
|---|---|---:|---|---|---|---|
|  |  |  |  |  |  |  |

## Completion gate

Before marking the goal complete:

- [ ] All blocker/high confirmed bugs are fixed.
- [ ] Medium/low deferred issues have evidence and reason.
- [ ] Compile check passes.
- [ ] Dependency check proves the experimental LDLib artifact is used.
- [ ] Manual editor smoke test in `VALIDATION.md` is either passed or remaining failures are logged as blockers/deferred with evidence.
