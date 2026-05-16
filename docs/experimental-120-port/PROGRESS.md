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
- [x] Photon 1.20 runtime/object model ported to compile gate.
- [x] Photon 1.20 editor/UI ported to compile gate.
- [x] Commands/save/load/runtime integration ported to compile gate.
- [x] Rendering/mixins adapted to MC 1.18.2 compile gate.
- [x] Forge compile passes.
- [x] Forge dev client launches.
- [x] Editor opens.
- [x] Basic create/save/load/spawn smoke passes.
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

## Previous WIP note - resolved by compile slice

The earlier direct Photon 1.20 runtime/editor copy attempt now compiles through the common and Forge compile gates. Runtime/editor smoke testing is still pending.

Files currently involved include:

- Photon-side JOML dependency additions in `common/build.gradle` and `forge/build.gradle`.
- New/copied 1.20 runtime classes under `client/fx/**` and `client/gameobject/**`.
- New/copied 1.20 editor classes: `FXEditor`, `FXProject`, `FXObjectsList`, `ParticleScene`, `ParticleScenePanel`, `ParticleInfoView`, `SceneMenu`.
- 1.18 compatibility stubs/adaptations started for `IrisFramebufferUtils`, `RandomSource -> java.util.Random`, and `GuiGraphics -> PoseStack`.

Latest compile command:

```bash
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" :photon-common:compileJava --no-daemon --stacktrace
```

Earlier failing categories now addressed enough for compile. Historical categories were:

- `PhotonLDLibPlugin.REGISTER_FX_OBJECTS` registry not ported yet.
- `FXProject` / `FX` / `IProject` API mismatch with current 1.18 LDLib and old Photon `FX` shape.
- `FXObjectsList` needs more LDLib 1.20 widget APIs (`TextTextureWidget`, dynamic sizing/layout helpers) or local adaptation.
- Old 1.18 editor classes (`ParticleEditor`, `EmittersList`) now conflict with new `ParticleScene` / `ParticleInfoView`; likely remove/replace old editor path after new editor wiring is ready.
- Render path mismatches: `BloomEffect`, framebuffer helpers, `BufferUploader` signatures, `PhotonShaders` compute shader support, JOML vs Mojang `Matrix4f` / `Vector3f` conversions.
- Remaining MC 1.20 API usages need 1.18 equivalents: `Component.literal`, `BlockPos.containing`, `Vec3.toVector3f`, entity visual rotation helpers, resource manager `open`.

Next implementation step:

1. With the dev client already launched, open the Photon editor and run the manual create/save/load/spawn smoke checklist in `VALIDATION.md`.
2. Fix any editor/runtime issues surfaced by the smoke test.
3. Run and update the bug-triage checklist in `BUG_TRIAGE.md`, then fix or document confirmed issues.

## Photon direct overlay slice - runtime/editor compiles

- Replaced the old 1.18 editor/runtime implementation with the Photon 1.20.1 runtime/editor stack as the active experimental source shape.
- Added/copied the 1.20 FX runtime model (`FXData`, `FXRuntime`, `FXProjectEffect`, `client/gameobject/**`) and the 1.20 editor UI (`FXEditor`, `FXProject`, `FXObjectsList`, `ParticleScenePanel`, updated resource/configurator classes).
- Removed the old 1.18 emitter/editor classes from the active source tree to avoid keeping two incompatible editor/runtime systems.
- Adapted 1.20 code to MC 1.18 APIs: `RandomSource -> java.util.Random`, `GuiGraphics -> PoseStack`, `Component.literal -> TextComponent`, `BlockPos.containing -> new BlockPos`, resource-manager file reads, entity rotation, `BufferUploader.end(...)`, Mojang/JOML matrix/vector conversions, and Forge mixin imports.
- Kept 1.20 compute-shader trail smoothing safely disabled on 1.18; the LDLib bridge exposes compute/SSBO capability flags as false and Photon uses the non-compute path.
- Validation passed on 2026-05-16:
  - `:photon-common:compileJava`
  - `:photon-forge:compileJava`
  - `:photon-forge:dependencyInsight --dependency ldlib-forge --configuration modImplementation` resolved `com.lowdragmc.ldlib:ldlib-forge-1.18.2:1.0.26-120port.1`.
- Remaining required gate: launch `:photon-forge:runClient`, verify mixins/startup, then open the editor and run the manual create/save/load/spawn smoke tests from `VALIDATION.md`.


## Forge dev launch wiring checkpoint

- Fixed the experimental Forge dev launch path after the direct Photon 1.20 overlay.
- LDLib now publishes its named `dev-shadow` classifier to Maven local for the Photon dev run. This avoids the earlier Forge userdev mismatch where the normal remapped Maven artifact could be discovered as a mod but still carried runtime names that crashed the client.
- Photon now resolves `com.lowdragmc.ldlib:ldlib-forge-1.18.2:1.0.26-120port.1:dev-shadow` for `modImplementation`, keeping the dependency tied to the experimental Maven-local LDLib version instead of an absolute local jar path.
- Added JOML to the Forge runtime library path so LDLib/Photon annotation scanning can load 1.20-style shape/object classes using `org.joml.*` types.
- Validation passed on 2026-05-16:
  - LDLib: `:ldlib-forge:compileJava publishToMavenLocal -Pmod_version=1.0.26-120port.1`
  - Photon: `:photon-forge:dependencyInsight --dependency ldlib-forge --configuration modImplementation` resolved `com.lowdragmc.ldlib:ldlib-forge-1.18.2:1.0.26-120port.1`.
  - Photon: `:photon-forge:compileJava`
  - Photon: `:photon-forge:runClient` reached the Minecraft client; latest log `/tmp/photon-runclient11.log` has no startup/mixin/LDLib/JOML errors, only the normal Realms auth message.
- Remaining required gate: open the Photon editor in the launched client and run the manual create/save/load/spawn smoke checklist from `VALIDATION.md`.


## Editor open and New Project crash triage

- Verified old Photon 1.18 opened the editor through `ServerCommands -> ParticleEditorFactory -> LDLib UIFactory.openUI`, not through the Photon 1.20 direct client command.
- Added `FXEditorFactory` and restored `/photon editor` plus `/photon particle_editor` server commands for the new FX editor using the proven LDLib UIFactory open path. The user verified `/photon editor` opens the editor.
- Investigated the New Project crash. Latest crash report `forge/run/crash-reports/crash-2026-05-16_14.07.48-client.txt` showed `BloomEffect` failed to load missing `photon:shaders/core/particle.json` while material resource previews were built.
- Added missing Photon 1.20 particle shader resources: `particle.json`, `particle.fsh`, `particle.vsh`.
- Merged missing Photon 1.20 language keys into `en_us.json` and `zh_cn.json` to reduce raw translation-key labels in the editor.
- Validation passed on 2026-05-16: Photon lang/shader JSON parse check and `:photon-forge:compileJava`.
- Remaining required gate: rerun client and click New Project again; if it no longer crashes, continue particle/beam/trail creation and save/load/spawn smoke tests.

## Resource-icon and GUI framebuffer triage checkpoint

- Investigated the latest broken-editor screenshot and `forge/run/logs/latest.log`.
- Found concrete LDLib resource issue: `Failed to load texture: ldlib:textures/gui/icon/local.png`, matching the magenta/black corners in the bottom resource panel.
- Fixed and republished LDLib with missing 1.20 GUI icon resources (`local`, `global`, checkbox/radiobox, transform mode icons) and matching `Icons` constants.
- Ported Photon Forge's 1.20 GUI-render framebuffer state bridge to 1.18 Forge stages: set GUI mode after `RenderLevelStageEvent.Stage.AFTER_WEATHER`, reset after `AFTER_SKY`. This addresses the likely cause of 1.20 MRT/bloom rendering treating the editor preview as world rendering.
- Validation passed on 2026-05-16:
  - LDLib `:ldlib-forge:compileJava publishToMavenLocal -Pmod_version=1.0.26-120port.1`
  - Photon `:photon-forge:compileJava`
  - Photon `:photon-forge:runClient` reached the client; latest startup log has no `Failed to load texture`, `FileNotFound`, or error lines before editor interaction.
- Remaining required manual gate: open `/photon editor`, create/select a particle, and verify the preview wedge/resources/toggles visually. If the black preview wedge remains, next slice is a deeper BloomEffect/ParticleRenderType 1.18 fallback rather than more UI styling.


## Automated editor smoke checkpoint - 2026-05-16 17:15

- Committed checkpoints now present:
  - Photon `9ad528f fix: make the Photon editor usable on 1.18`
  - LDLib `0e70ca29 fix: restore visible editor controls in LDLib`
  - LDLib `ea003193 fix: match modern float view panel styling`
- Added/used the dev auto-open harness to launch a world, open the editor, create a new FX project, and create selected emitters without human clicks.
- Harness can now create multiple emitter types with `PHOTON_AUTO_EMITTERS`, e.g. `PHOTON_AUTO_EMITTERS=particle,beam,trail`.
- Validation passed after the latest changes:
  - LDLib `:ldlib-forge:compileJava publishToMavenLocal -Pmod_version=1.0.26-120port.1`
  - Photon `:photon-forge:compileJava --refresh-dependencies` after LDLib republish
  - Photon `:photon-forge:compileJava` after the multi-emitter harness update
  - Photon `:photon-forge:runClient` with `PHOTON_AUTO_OPEN_EDITOR=true PHOTON_AUTO_LOAD_WORLD='New World (1)' PHOTON_AUTO_EMITTERS='particle,beam,trail'`
- Screenshot evidence:
  - `/tmp/codex-screens/photon-after-floatview.png`: editor opens with 1.20-style dark panels, visible checkbox toggles, no `A` placeholders, no black preview wedge.
  - `/tmp/codex-screens/photon-all-emitters.png`: smoke project contains `particle`, `beam`, and `trail`; particle/beam render in the preview; object list/config panel/resource panel remain visible.
- Latest editor smoke log only showed expected local/offline auth noise plus macOS OpenGL unsupported warnings; no Photon/LDLib missing texture, shader, mixin, or runtime exception was found after editor open.
- Resource usage note from code review: material resources are draggable onto the emitter `Material > preview` box (`MaterialSetting.preview` has an `IMaterial` drag consumer). Mesh/color/curve/gradient resources use the equivalent compatible preview/selector targets.

Remaining gates:

1. Review the render/bloom bridge changes for unnecessary risk now that the actual black-wedge fix is known.
2. Keep the dev harness until final validation is done, then decide whether to leave it env-gated or remove it.


## Export/reload/spawn smoke checkpoint - 2026-05-16 17:33

- Found and fixed a real save/export blocker: serializing the 1.20 `Transform` object failed on 1.18 because LDLib did not register JOML `Vector3f`/`Quaternionf` sync-data accessors.
- LDLib now includes/registers the 1.20 `Vector3fAccessor` and `QuaternionfAccessor` bridge accessors.
- Added env-gated Photon smoke support:
  - `PHOTON_AUTO_EXPORT_SMOKE=true` exports `forge/run/ldlib/assets/photon/fx/codex_smoke.fx`.
  - `PHOTON_AUTO_SPAWN_SMOKE=true` reloads resource packs, loads `photon:codex_smoke` through `FXHelper`, and spawns it as a `BlockEffect` at the player position.
- Validation passed:
  - LDLib `:ldlib-forge:compileJava publishToMavenLocal -Pmod_version=1.0.26-120port.1`
  - Photon `:photon-forge:compileJava --refresh-dependencies`
  - Photon `:photon-forge:runClient` with `PHOTON_AUTO_OPEN_EDITOR=true PHOTON_AUTO_LOAD_WORLD='New World (1)' PHOTON_AUTO_EMITTERS='particle,beam,trail' PHOTON_AUTO_EXPORT_SMOKE=true PHOTON_AUTO_SPAWN_SMOKE=true`
- Evidence:
  - Log showed `Photon dev smoke exported FX to .../forge/run/ldlib/assets/photon/fx/codex_smoke.fx`.
  - Log showed `Photon dev smoke spawned exported FX at BlockPos{x=8, y=72, z=10}`.
  - Exported file exists: `forge/run/ldlib/assets/photon/fx/codex_smoke.fx`.
  - Screenshot: `/tmp/codex-screens/photon-export-spawn-smoke.png`.
- Note: the smoke harness resource reload can visually clear/reset parts of the open configurator panel; this path is env-gated and is only for validation. Normal editor open/use is unaffected.


## Remove-command packet review - 2026-05-16

- Reviewed block/entity remove packet encode/decode paths while closing the command/runtime smoke checklist.
- Block remove already encoded `force` and optional `location`.
- Entity remove did not encode/decode `force`, so `/photon fx remove entity <entities> true` would behave like non-forced removal on the client.
- Fixed `RemoveEntityEffectCommand` to send/read `force` before the optional location flag.
- Validation: Photon `:photon-forge:compileJava` passed.
