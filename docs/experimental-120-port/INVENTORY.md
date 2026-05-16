# Photon 1.20 -> LDLib 1.18 API Inventory

Generated/updated: 2026-05-16

Source inputs:

- Photon 1.20.1 worktree: `/tmp/photon-120` at `507499f`
- LDLib 1.20.1 worktree: `/tmp/ldlib-120` at `5d68947b`
- LDLib 1.18 experimental branch: `/Users/rubenvancraenenbroeck/IdeaProjects/LDLib-MultiLoader`

## Summary

- Unique Photon 1.20 LDLib imports scanned: 108
- Imports/classes/packages already present in LDLib 1.18 branch: 100
- Missing or unknown imports needing bridge/adaptation: 8

## Missing / adaptation-required imports

| Status | Import | Photon 1.20 usage files | Action |
|---|---|---|---|
| missing class | `com.lowdragmc.lowdraglib.client.shader.management.ShaderSSBO` | client/gameobject/particle/TrailParticle.java | Backport/defer shader SSBO support depending on 1.18 GL/render feasibility. |
| missing class | `com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorSelectorConfigurator` | client/gameobject/emitter/data/RendererSetting.java<br>gui/editor/configurator/NumberFunction3Accessor.java | Backport configurator class/API from LDLib 1.20. |
| missing class | `com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.SceneEditorWidget` | gui/editor/ParticleScene.java | Backport LDLib 1.20 scene editor API, adapted to MC 1.18 rendering/math. |
| missing class | `com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.data.Transform` | client/gameobject/FXObject.java<br>gui/editor/FXObjectsList.java | Backport LDLib 1.20 scene editor API, adapted to MC 1.18 rendering/math. |
| missing class | `com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.IScene` | client/fx/FXRuntime.java<br>client/gameobject/FXObject.java | Backport LDLib 1.20 scene editor API, adapted to MC 1.18 rendering/math. |
| missing class | `com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneObject` | client/fx/FXRuntime.java<br>client/gameobject/IFXObject.java | Backport LDLib 1.20 scene editor API, adapted to MC 1.18 rendering/math. |
| missing class | `com.lowdragmc.lowdraglib.gui.widget.layout.Layout` | gui/editor/FXObjectsList.java | Backport minimal layout enum/helper needed by FX object list UI. |
| missing class | `com.lowdragmc.lowdraglib.utils.Vector3fHelper` | client/gameobject/particle/TileParticle.java<br>client/gameobject/emitter/data/TrailsSetting.java<br>client/gameobject/emitter/data/VelocityOverLifetimeSetting.java<br>client/gameobject/emitter/data/shape/Cone.java<br>client/gameobject/emitter/data/shape/Circle.java<br>client/gameobject/emitter/data/shape/Sphere.java<br>client/gameobject/emitter/data/shape/Cylinder.java<br>client/gameobject/emitter/data/shape/Mesh.java<br>... +3 more | Backport helper or adapt Photon math calls during runtime port. |

## High-priority LDLib bridge groups

1. Scene/object tree support: `SceneEditorWidget`, `Transform`, `IScene`, `ISceneObject` and related scene object classes. This blocks Photon 1.20 `FXRuntime`, `FXObject`, `ParticleScene`, and `FXObjectsList`.
2. Configurator support: `ConfiguratorSelectorConfigurator` and related container/accessor behavior. This blocks modern renderer/number-function/resource settings UI.
3. Widget/layout support: `gui.widget.layout.Layout` needed by `FXObjectsList`.
4. Math/render helper support: `Vector3fHelper` and possibly `ShaderSSBO`. These should be handled together with Photon runtime/render port because 1.20 uses JOML while MC 1.18 largely uses Mojang math classes.

## Important adaptation note

Do not blindly copy LDLib 1.20 scene editor classes: they use MC 1.20/JOML-facing APIs such as `org.joml.Vector3f`, `org.joml.Matrix4f`, `GuiGraphics`, and newer render signatures. The 1.18 branch uses `com.mojang.math.*` and `PoseStack` UI draw methods. The bridge should preserve 1.20 behavior but adapt types/signatures to 1.18 equivalents.

