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
| PT-005 | Confirmed bug | High | Editor preview/render framebuffer state | User 14:20 screenshot showed a large black wedge covering the editor preview. Photon 1.20 toggles `IrisFramebufferUtils.renderingGUIScreen` from Forge render-stage events, but the 1.18 port stub left it false forever, so the 1.20 bloom/MRT path could treat GUI/editor rendering as world rendering. | Ported the Forge render-stage GUI flag bridge using 1.18's closest post-world stage (`AFTER_WEATHER`) and `AFTER_SKY` reset. | Photon `:photon-forge:compileJava` passed; `:photon-forge:runClient` reached the client with no startup errors. Requires user/manual editor screenshot to confirm the preview wedge is gone; if not, continue into BloomEffect/particle render target fallback. |

## Completion gate

Before marking the goal complete:

- [ ] All blocker/high confirmed bugs are fixed.
- [ ] Medium/low deferred issues have evidence and reason.
- [ ] Compile check passes.
- [ ] Dependency check proves the experimental LDLib artifact is used.
- [ ] Manual editor smoke test in `VALIDATION.md` is either passed or remaining failures are logged as blockers/deferred with evidence.
