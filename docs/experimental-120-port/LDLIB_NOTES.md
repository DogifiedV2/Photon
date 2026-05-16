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

Publish command will be finalized after checking LDLib branch Gradle properties. Expected shape:

```bash
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" publishToMavenLocal -Pmod_version=1.0.26-120port.1 --no-daemon --stacktrace
```

## Port ledger

| Item | Source 1.20 class/path | 1.18 target path | Status | Notes |
|---|---|---|---|---|
| Editor base support | TBD | TBD | pending |  |
| Scene object support | TBD | TBD | pending |  |
| Transform support | TBD | TBD | pending |  |
| Configurator support | TBD | TBD | pending |  |
| Resource panel support | TBD | TBD | pending |  |
| Toggle/dropdown/text field visuals | TBD | TBD | pending |  |

## Known risks

- MC 1.20 LDLib may rely on classes/methods not present in MC 1.18.
- Some UI behavior may be embedded across multiple LDLib widgets, not isolated in Photon.
- Changing LDLib can affect every Photon editor widget, so compile and runtime checks are required after each coherent change.
