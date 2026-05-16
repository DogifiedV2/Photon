# UI Modernization Progress

## Current status

Phase 5 scrolling/list containment polish is complete in LDLib-MultiLoader. Next work is Phase 6: editor shell layout polish for the top/side/bottom panels.

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
- [ ] Phase 6: Editor shell layout polish.
- [ ] Phase 7: Photon-specific polish.
- [ ] Phase 8: Safe modern nice-to-haves.
- [ ] Phase 9: Final regression/review/handoff.

## Commits

- Photon `b1629c4` - `docs: add photon editor ui modernization plan`
- Photon `68167bd` - `docs: record editor ui baseline audit`
- LDLib-MultiLoader `b905b74f` - `style: strengthen editor ui surfaces`
- LDLib-MultiLoader `43a7a739` - `style: improve editor configurator controls`
- LDLib-MultiLoader `003b38dc` - `fix: correct editor dialog and scroll hit bounds`
- LDLib-MultiLoader `2d7d6b7e` - `style: polish editor scrollable lists`

## Validation log

- Phase 1: source audit only; no runtime changes.
- Phase 2: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed. Root `./gradlew` is not executable in this repo, so the validation used `sh ./gradlew`.
- Phase 3: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.
- Phase 4: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.
- Phase 5: `sh ./gradlew compileJava --no-daemon --stacktrace` in LDLib-MultiLoader passed.

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
