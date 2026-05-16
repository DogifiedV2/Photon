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
| PT-001 | Confirmed bug | Blocker | Forge dev launch / LDLib runtime wiring | `/tmp/photon-runclient7.log` / crash report showed `NoSuchMethodError: net.minecraft.client.Minecraft.m_91087_()` from LDLib runtime naming mismatch; `/tmp/photon-runclient9.log` showed `NoClassDefFoundError: org/joml/Vector3fc` during LDLib annotation scanning. | Fixed by publishing LDLib `dev-shadow` classifier to Maven local, consuming that classifier from Photon `modImplementation`, and adding JOML to Forge runtime library wiring. | LDLib `:ldlib-forge:compileJava publishToMavenLocal -Pmod_version=1.0.26-120port.1` passed; Photon `:photon-forge:dependencyInsight --dependency ldlib-forge --configuration modImplementation` passed; Photon `:photon-forge:compileJava` passed; Photon `:photon-forge:runClient` reached the Minecraft client in `/tmp/photon-runclient11.log` with no startup/mixin/LDLib/JOML errors. |
| PT-002 | Confirmed bug | Blocker | Editor command open path | User confirmed `/photon_editor` logs `Photon editor screen opened` but no menu appears. Old 1.18 opened through `ServerCommands -> ParticleEditorFactory -> UIFactory.openUI -> SPacketUIOpen`, not through a direct client command; `/photon editor` using the restored UIFactory path opened the editor. MC 1.18 `ChatScreen.keyPressed` also calls `Minecraft.setScreen(null)` after sending chat/commands, explaining why direct client-screen opening can be overwritten. | Restored a 1.18-style `FXEditorFactory` registered with LDLib `UIFactory` and added `/photon editor` plus `/photon particle_editor` server commands for the new FX editor. Kept `/photon_editor` client debug path for comparison. | User verified `/photon editor` opens the editor. `:photon-forge:compileJava` passed on 2026-05-16. |
| PT-003 | Confirmed bug | Blocker | New Project material resource load / shader resources | Latest crash report `forge/run/crash-reports/crash-2026-05-16_14.07.48-client.txt` shows `ExceptionInInitializerError` from `CustomShaderMaterial.preview -> BloomEffect`, caused by `FileNotFoundException: photon:shaders/core/particle.json`. Current 1.18 resources were missing the 1.20 `particle.json`, `particle.fsh`, and `particle.vsh` files while the ported 1.20 `BloomEffect` requires them. | Copied/adapted the missing Photon 1.20 particle shader resources into `common/src/main/resources/assets/photon/shaders/core/` and merged missing 1.20 language keys into `en_us.json`/`zh_cn.json` to address raw translation-key labels seen in the editor. | JSON parse validation passed for Photon lang/shader JSON files; `:photon-forge:compileJava` passed on 2026-05-16. Requires rerun client and manual New Project click to verify no crash. |

| PT-004 | Confirmed bug | High | LDLib resource panel icons | Latest smoke log showed `Failed to load texture: ldlib:textures/gui/icon/local.png`, and the user's 14:20 screenshot showed magenta/black missing-texture corners on the resource thumbnails. LDLib 1.20 contains `local.png`, `global.png`, checkbox/radiobox, and transform icons that the 1.18 bridge was missing. | Fixed in LDLib by copying the missing 1.20 GUI icon resources and restoring matching `Icons` constants. | LDLib `:ldlib-forge:compileJava publishToMavenLocal -Pmod_version=1.0.26-120port.1` passed; Photon `:photon-forge:compileJava` passed; latest `:photon-forge:runClient` startup log has no `Failed to load texture` / `FileNotFound` / error lines before editor interaction. Requires user/manual editor screenshot to verify thumbnails visually. |
| PT-005 | Confirmed bug | High | Editor preview/render framebuffer state | User 14:20 screenshot showed a large black wedge covering the editor preview. Later comparison against the old fixed 1.18 branch showed the concrete trigger was the cached scene buffer/dummy debug render path in `ParticleScenePanel`, not just the GUI framebuffer bridge. | Fixed by removing the cached scene buffer use and dummy zero-size `renderBox`, then hardening debug-box render state. The earlier Forge render-stage GUI flag bridge remains present and should get a follow-up risk review. | Photon `:photon-forge:compileJava` passed; auto-open screenshots `/tmp/codex-screens/photon-after-floatview.png` and `/tmp/codex-screens/photon-all-emitters.png` show no black preview wedge. |

| PT-006 | Confirmed bug | High | Editor labels / text suppliers | Editor panels showed placeholder `A` labels where 1.20 supplier-backed `TextTexture` values should have rendered names/values. | Fixed in LDLib by updating supplier-backed `TextTexture` state before draw. | LDLib publish passed; Photon compile passed; screenshots `/tmp/codex-screens/photon-after-floatview.png` and `/tmp/codex-screens/photon-all-emitters.png` show normal labels (`Particle`, `Configurator`, `Particle Information`, resource names) instead of `A`. |
| PT-007 | Confirmed bug | High | Toggle controls / dropdown affordance | Boolean settings were visually invisible when off and only showed a checkmark when on; configurator toggles could be offset/unusable. | Fixed in LDLib by adding opt-in checkbox-style `SwitchWidget` rendering, applying it to boolean/toggle configurators and Photon particle-info toggles, and keeping configurator row controls within readable row layout. | Auto-open screenshots show visible gray bordered checkbox off state and filled on state in configurator and Particle Information controls. |
| PT-008 | Documentation/UX issue | Medium | Resource application | User could not tell how to apply resources to particles. Code review shows material resources drag `IMaterial` values from `MaterialsResourceContainer`; `MaterialSetting.preview` accepts `IMaterial` drops and rebuilds the material configurator. | No code fix yet; documented usage and left UI enhancement open if user wants clearer affordance. | Code evidence: `MaterialsResourceContainer#setDragging(...)` and `MaterialSetting.preview.setDraggingConsumer(o -> o instanceof IMaterial, ...)`. |
| PT-009 | Confirmed smoke pass | Medium | Particle/beam/trail editor preview | Needed evidence that the 1.20 object model is not just particle-only. | Extended the env-gated dev smoke harness to create `particle`, `beam`, and `trail` via `PHOTON_AUTO_EMITTERS=particle,beam,trail`. | Photon `:photon-forge:compileJava` passed; runClient opened editor; `/tmp/codex-screens/photon-all-emitters.png` shows all three objects in the FX Object List and particle/beam rendering in preview. |
| PT-010 | Confirmed bug | Blocker | Save/export serialization | Auto export smoke crashed with `Failed to create ref of localPosition with type:org.joml.Vector3f` because the 1.18 LDLib bridge had JOML classes but no sync-data payload accessors registered for JOML `Vector3f`/`Quaternionf`. | Fixed in LDLib by backporting/registering `Vector3fAccessor` and `QuaternionfAccessor`. Added env-gated export/reload/spawn smoke to Photon harness. | LDLib publish passed; Photon refresh compile passed; runClient with `PHOTON_AUTO_EXPORT_SMOKE=true PHOTON_AUTO_SPAWN_SMOKE=true` exported `codex_smoke.fx`, reloaded it through `FXHelper`, and spawned it as a block effect; screenshot `/tmp/codex-screens/photon-export-spawn-smoke.png`. |
| PT-011 | Confirmed bug | Medium | Entity remove command packet | Code review found `RemoveEntityEffectCommand.encode` did not write the `force` field and `decode` did not read it, unlike block removal. Forced entity removal would be lost client-side. | Fixed by encoding/decoding `force` before the optional location flag. | Photon `:photon-forge:compileJava` passed. |

## Completion gate

Before marking the goal complete:

- [ ] All blocker/high confirmed bugs are fixed.
- [ ] Medium/low deferred issues have evidence and reason.
- [ ] Compile check passes.
- [ ] Dependency check proves the experimental LDLib artifact is used.
- [ ] Manual editor smoke test in `VALIDATION.md` is either passed or remaining failures are logged as blockers/deferred with evidence.
