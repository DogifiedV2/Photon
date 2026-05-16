# LDLib Notes for Photon 1.20 -> 1.18 Experiment

## Purpose

Track the LDLib 1.20.1 editor/UI/configurator/scene pieces required to run Photon 1.20.1-style UI on MC 1.18.2.

## Branch

- Repo: `/Users/rubenvancraenenbroeck/IdeaProjects/LDLib-MultiLoader`
- Branch: `experiment/1.20-ui-to-1.18`

## Rule

Port only LDLib1 1.20.1 support required by Photon. Do not port LDLib2.

## Areas to inspect first

From Photon 1.20 usage, inspect LDLib dependencies around:

- `com.lowdragmc.lowdraglib.gui.editor.ui.Editor`
- `ToolPanel`
- `ConfigPanel`
- `ResourcePanel`
- `MenuPanel`
- `StringTabContainer`
- `sceneeditor.sceneobject.ISceneObject`
- `sceneeditor.sceneobject.IScene`
- `sceneeditor.data.Transform`
- draggable/scrollable widget groups
- configurators/accessors used by number functions, shapes, resources, materials
- visible button/toggle/dropdown/text field rendering

## Experimental artifact

Use a clearly local/experimental version. Suggested:

```text
1.0.26-120port.1
```

Current status: published to Maven local on 2026-05-16 and verified from Photon with dependencyInsight. The Forge dev run consumes the Maven-local `dev-shadow` classifier of the same experimental version so the discovered LDLib mod uses named dev-runtime classes.

Publish command used:

```bash
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" :ldlib-forge:compileJava publishToMavenLocal -Pmod_version=1.0.26-120port.1 --no-daemon --stacktrace
```

## Port ledger

| Item | Source 1.20 class/path | 1.18 target path | Status | Notes |
|---|---|---|---|---|
| Editor base support | `/tmp/ldlib-120/.../gui/editor/ui/Editor.java` and related panels | current LDLib editor UI package | inspecting | Existing 1.18 editor compiles; 1.20 adds history/save helpers and panel behavior. |
| Scene object support | `/tmp/ldlib-120/.../gui/editor/ui/sceneeditor/**` | LDLib experimental branch | partial | Scene/runtime bridge classes added; transform gizmo visuals are deferred/minimal. |
| Transform support | `sceneeditor/data/Transform.java`, `Ray.java` | LDLib experimental branch | partial | JOML dependency and Transform/Ray bridge added; editor accessors for JOML fields still need verification during Photon port. |
| Configurator support | `ConfiguratorSelectorConfigurator`, `IConfiguratorContainer`, newer accessors | LDLib experimental branch | partial | `IConfiguratorContainer` and `ConfiguratorSelectorConfigurator` added; newer accessors still pending if compile requires them. |
| Resource panel support | 1.20 `ResourcePanel`, `ResourceContainer`, resource classes | current LDLib resource UI package | inspecting | Existing resource UI has older behavior; compare before replacing. |
| Toggle/dropdown/text field visuals | 1.20 configurators/widgets | current LDLib configurators/widgets | partial | Existing branch contains prior 1.18 UI polish; `SelectorWidget#setCandidatesSupplier` added for 1.20 selector behavior. |

## Known risks

- MC 1.20 LDLib may rely on classes/methods not present in MC 1.18.
- Some UI behavior may be embedded across multiple LDLib widgets, not isolated in Photon.
- Changing LDLib can affect every Photon editor widget, so compile and runtime checks are required after each coherent change.

## LDLib bridge commits

- `4a2759c5` recorded the initial Photon-required LDLib API inventory.
- `c1e637d3` added `IConfiguratorContainer`, `ConfiguratorSelectorConfigurator`, `Layout`, `Align`, and dynamic selector candidates; LDLib compile/publish passed on 2026-05-16.
- Current LDLib bridge slice added JOML, scene object/transform bridge classes, and SceneWidget compatibility helpers; LDLib compile/publish and Photon compile passed on 2026-05-16.

- LDLib commit `20e772b7` added a minimal `ShaderSSBO` bridge required by Photon 1.20 trail particles; LDLib compile/publish passed on 2026-05-16.

- LDLib now publishes `ldlib-forge-1.18.2-1.0.26-120port.1-dev-shadow.jar` to Maven local as a `dev-shadow` classifier for Photon Forge dev runs.
- LDLib Forge also declares JOML on the Forge runtime library path because the backported scene/shape APIs expose `org.joml.*` types during annotation scanning.
