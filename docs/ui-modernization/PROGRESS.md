# UI Modernization Progress

## Current status

Phase 9 final regression/review/handoff is complete. Code-side UI modernization is ready for the user morning walkthrough.

## Baseline evidence

Screenshots in `/Users/rubenvancraenenbroeck/Downloads` from `2026-05-16 03:13-03:14` show:

- missing/weak configurator panel backgrounds,
- floating text controls over the world,
- unclear editable fields and dropdown affordance,
- particle info view lacking card styling,
- rough dialog/export modal styling,
- rough resource tab/item styling,
- menus/dropdowns needing stronger contrast and spacing.

## Phase 1 audit findings

### Main root causes

1. `ConfigPanel` and its scroll groups do not paint a strong main panel/card background, so configurator text and values visually float over the 3D scene.
2. Most configurator rows rely on very transparent `T_BLACK`/`T_GRAY` textures, tiny 10-15px controls, and no shared row surface/hover state.
3. Menus, dialogs, float views, and selector popups use inconsistent or weak panel surfaces, making buttons and modal content hard to distinguish.
4. Some editor controls are visually too small/cramped after the 1.18.2 port, especially number-function selectors and vector/range configurators.
5. Potential correctness issues were found for later phases: dialog overflow adjustment direction, scroll child visibility handling, raw scissor usage in `DraggableScrollableWidgetGroup.drawOverlay`, and child hit-area behavior in `WidgetGroup`.

### Primary LDLib target files

- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/ColorPattern.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/ui/ConfigPanel.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/ui/ToolPanel.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/ui/ResourcePanel.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/ui/MenuPanel.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/ui/view/FloatViewWidget.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/ui/Editor.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/editor/configurator/*.java`
- `common/src/main/java/com/lowdragmc/lowdraglib/gui/widget/{DialogWidget,MenuWidget,WidgetGroup,DraggableScrollableWidgetGroup}.java`

### Primary Photon target files

- `common/src/main/java/com/lowdragmc/photon/gui/editor/ParticleEditor.java`
- `common/src/main/java/com/lowdragmc/photon/gui/editor/ParticleInfoView.java`
- `common/src/main/java/com/lowdragmc/photon/gui/editor/EmittersList.java`
- `common/src/main/java/com/lowdragmc/photon/gui/editor/configurator/NumberFunctionConfigurator.java`
- `common/src/main/java/com/lowdragmc/photon/gui/editor/configurator/NumberFunction3Configurator.java`

### Reference use

Use Photon `1.21`, LDLib-MultiLoader `1.21-ui-refactor`, and LDLib2 only as visual and interaction inspiration. Do not backport their architecture or runtime systems.

## Completed phases

- [x] Phase 0: Tracker setup started.
- [x] Phase 1: Baseline audit.
- [x] Phase 2: LDLib visual foundation.
- [x] Phase 3: Configurator usability.
- [x] Phase 4: Input/hitbox/GUI-scale correctness.
- [x] Phase 5: Scrolling/clipping/containment.
- [x] Phase 6: Editor shell layout polish.
- [x] Phase 7: Photon-specific polish.
- [x] Phase 8: Safe modern nice-to-haves.
- [x] Phase 9: Final regression/review/handoff.

## Commits

- Photon `b1629c4` - `docs: add photon editor ui modernization plan`
- Photon `68167bd` - `docs: record editor ui baseline audit`
- LDLib-MultiLoader `b905b74f` - `style: strengthen editor ui surfaces`
- LDLib-MultiLoader `43a7a739` - `style: improve editor configurator controls`
- LDLib-MultiLoader `003b38dc` - `fix: correct editor dialog and scroll hit bounds`
- LDLib-MultiLoader `2d7d6b7e` - `style: polish editor scrollable lists`
- LDLib-MultiLoader `1a3a9ce5` - `style: polish editor shell tabs`
- Photon `f81f0d5` - `style: polish photon editor panels`
- LDLib-MultiLoader `f65642c5` - `style: polish editor file dialogs`

## Validation log

- Phase 1: source audit only; no runtime changes.
- Phase 2: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed. Root `./gradlew` is not executable in this repo, so the validation used `sh ./gradlew`.
- Phase 3: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.
- Phase 4: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.
- Phase 5: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.
- Phase 6: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.
- Phase 7: `sh ./gradlew compileJava --no-daemon --stacktrace` in Photon passed.
- Phase 8: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.
- Phase 9: final `sh ./gradlew compileJava --no-daemon --stacktrace` passed in both LDLib-MultiLoader and Photon.
- Phase 9: git diff/status review complete. LDLib-MultiLoader is clean; Photon only has pre-existing unrelated `.gitignore` and `gradlew` changes left uncommitted.

## Phase 3 notes

- Added shared row/card backgrounds and hover treatments to configurator rows.
- Strengthened number/string field surfaces, selector popups, boolean toggles, and color picker surfaces.
- Preserved existing supplier/update callback behavior; no particle runtime behavior was changed.

## Phase 4 notes

- Fixed dialog overflow correction so dialogs move back onto screen instead of farther off-screen.
- Fixed scroll child visibility checks to combine horizontal and vertical clipping instead of overwriting one axis with the other.
- Routed scroll overlay scissoring through the scaled `RenderUtils.useScissor` path.
- Restricted scroll-container hover/hit checks to the visible container bounds so clipped children cannot create invisible hover/click areas.

## Phase 5 notes

- Added stronger backgrounds and visible scrollbars to resource containers, widget toolbox lists, and selector popups.
- Added resource/tool item cards with hover and selected borders to reduce invisible/ambiguous list hits.
- Kept changes inside editor/list presentation; data/resource behavior was not changed.

## Phase 6 notes

- Added stronger top menu/header framing and hoverable menu tab surfaces.
- Added clearer bottom resource tab surfaces.
- Kept changes in persistent editor shell presentation only.

## Phase 7 notes

- Strengthened Particle Information view content, buttons, toggles, and information rows.
- Added visible cards, hover state, selected borders, and scrollbar track styling for the Photon emitter list.
- Made number-function dropdown buttons more visible and opened their menus next to the button instead of far to the right.
- No particle runtime or emitter simulation logic was changed.

## Phase 8 notes

- Added a final low-risk polish pass to file dialogs/export/import surfaces.
- Strengthened file tree and open-folder button styling while preserving dialog behavior.
- Avoided LDLib2 architecture backports and runtime/editor feature changes.

## Phase 9 final review

- Reviewed aggregate LDLib-MultiLoader diff from the pre-work commit through `f65642c5`: changes are scoped to editor colors, surfaces, configurator affordances, scroll/hit containment, menus, dialogs, and selector/resource/toolbox presentation.
- Reviewed aggregate Photon diff from the pre-work commit through `f81f0d5`: code changes are scoped to Particle Info, Emitters List, and Number Function dropdown presentation.
- Confirmed no physics, particle simulation, emitter serialization, project file format, or LDLib2 architecture backport was introduced.
- Final subjective UX checks remain for the user walkthrough because they require live editor feel and screenshots/runtime interaction.

## Local LDLib jar wiring

- Published LDLib-MultiLoader to Maven local as `com.lowdragmc.ldlib:ldlib-forge-1.18.2:1.0.26-ui-local` using `sh ./gradlew publishToMavenLocal -Pmod_version=1.0.26-ui-local --no-daemon --stacktrace`.
- Updated Photon `settings.gradle` so the LDLib version catalog points at `1.0.26-ui-local` instead of the remote `1.0.26`.
- Verified Photon resolves the local jar with `sh ./gradlew :photon-forge:dependencyInsight --dependency ldlib-forge --configuration modImplementation --no-daemon`.
- Re-ran `sh ./gradlew compileJava --no-daemon --stacktrace` in Photon after the dependency switch; it passed.
