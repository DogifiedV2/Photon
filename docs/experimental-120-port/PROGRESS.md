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

1. Run `:photon-forge:runClient` and fix startup/mixin/resource crashes.
2. Open the Photon editor and run the manual create/save/load/spawn smoke checklist in `VALIDATION.md`.
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
