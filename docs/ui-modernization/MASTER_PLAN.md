# Photon 1.18.2 Editor UI Modernization Plan

## Objective

Repair and modernize the 1.18.2 Photon editor UI so it is usable, readable, and smooth to work with, while preserving all existing particle/FX runtime behavior.

Primary implementation target: `/Users/rubenvancraenenbroeck/IdeaProjects/LDLib-MultiLoader` old GUI/editor stack.

Secondary implementation target: `/Users/rubenvancraenenbroeck/IdeaProjects/Photon` editor-specific presentation code only when LDLib generic fixes are not enough.

## Hard boundaries

- Do not full-backport LDLib2.
- Do not migrate Photon to LDLib2 APIs.
- Do not port LDLib2's layout engine, XML UI system, data binding, event framework, editor framework, or unrelated features.
- Do not rewrite particle physics, particle runtime, rendering pipelines, serialization, save/export formats, commands, or gameplay behavior unless a tiny UI-only fix requires it.
- Preserve current working 1.18.2 Photon features.

## Design references

Use these as inspiration, not migration targets:

1. Local 1.18.2 Photon + LDLib code: source of truth for compatibility.
2. Low-Drag-MC/LDLib-MultiLoader `1.21-ui-refactor`: closest code-style reference for newer old-LDLib UI ideas.
3. Low-Drag-MC/Photon `1.21`: UX/layout reference for how the editor should feel.
4. Low-Drag-MC/LDLib2: visual/UX reference only for modern dark panels, clear controls, better spacing, dialogs, resource lists, and configurator readability.

## Baseline screenshots

Before-state evidence is in `~/Downloads`:

- `Screenshot 2026-05-16 at 03.13.06.png`
- `Screenshot 2026-05-16 at 03.13.23.png`
- `Screenshot 2026-05-16 at 03.13.33.png`
- `Screenshot 2026-05-16 at 03.13.42.png`
- `Screenshot 2026-05-16 at 03.13.46.png`
- `Screenshot 2026-05-16 at 03.13.50.png`
- `Screenshot 2026-05-16 at 03.14.02.png`
- `Screenshot 2026-05-16 at 03.14.07.png`

Visible baseline problems:

- Configurator controls are mostly floating text over the world.
- Right panel lacks a real card/panel background.
- Editable fields, arrows, toggles, and help controls have weak affordance.
- Particle info view is floating text instead of a readable card.
- Dialog/export UI has outline/text but lacks strong modal surface and button affordance.
- Menus/dropdowns work but need better backgrounds, contrast, padding, and layering.
- Resource panel is the most visible area but still needs better tabs, selection, spacing, and clipping.

## Autonomous workflow rules

1. Inspect git status in both repos before work. Do not include unrelated pre-existing changes such as generated run folders or unrelated wrapper edits unless verified as part of this task.
2. Research before each phase. Check local 1.18.2 Minecraft/Forge GUI rendering, `PoseStack`, screen scale, mouse coordinates, scissor/clipping, and widget input behavior before risky edits.
3. Implement phase by phase. Avoid one huge diff.
4. After each coherent subphase:
   - compile relevant repo(s),
   - run the best available client/editor smoke test,
   - review the diff for regressions,
   - run a focused bug-fix pass,
   - update `PROGRESS.md` and `NEXT_ACTIONS.md`,
   - commit.
5. Do not stop because of time. Continue until all code-side phases are complete or a real blocker prevents further safe progress.
6. For ordinary uncertainty, document it in `BLOCKERS.md` and continue with the next safe task.
7. Leave a useful handoff if interrupted.

## Phase 0: Tracker setup and baseline documentation

Create and maintain:

- `MASTER_PLAN.md`: stable plan and rules.
- `PROGRESS.md`: completed work, commits, validation notes.
- `NEXT_ACTIONS.md`: exact next task when resuming.
- `BLOCKERS.md`: real blockers, pending user walkthrough items, known risks.

Gate: tracker exists and references baseline screenshots.

## Phase 1: Baseline audit

Inspect and map:

- Photon editor entry points and editor-owned widgets.
- LDLib editor shell, panels, widgets, textures, dialogs, menus, configurators.
- MC 1.18 GUI rendering/input/scissor/screen-scale APIs.
- Newer reference code for visual/UX ideas only.

Gate: `PROGRESS.md` records likely root causes, target files, and first implementation slice.

## Phase 2: LDLib visual foundation

Goal: make UI surfaces real.

Fix or improve:

- panel/card backgrounds,
- borders,
- button backgrounds,
- hover/selected/disabled states,
- menu/dropdown backgrounds,
- modal/dialog backgrounds,
- texture draw order and contrast.

Likely areas:

- `ColorPattern`,
- texture classes,
- `Widget`, `WidgetGroup`, `ImageWidget`, `ButtonWidget`,
- `MenuWidget`, `DialogWidget`,
- tab widgets.

Gate: UI no longer looks like floating text over the world; menus, dropdowns, buttons, and dialogs have visible surfaces.

## Phase 3: Configurator usability

Goal: make the right-side settings panel actually usable.

Fix or improve:

- right panel backing,
- row backgrounds,
- value field backgrounds,
- row spacing,
- section headers,
- dropdown arrows,
- checkbox/switch visibility,
- help icon placement,
- disabled state contrast,
- number/vector/range/color/selector controls.

Likely areas:

- `Configurator`, `ConfiguratorGroup`, `ValueConfigurator`,
- `NumberConfigurator`, `BooleanConfigurator`, `SelectorConfigurator`,
- `ColorConfigurator`, `Vector3Configurator`, `RangeConfigurator`,
- Photon number-function configurators only if generic LDLib fixes are insufficient.

Gate: settings are visibly editable, collapsible sections are obvious, dropdowns are readable, and disabled/enabled states are distinguishable.

## Phase 4: Input, hitbox, and GUI-scale correctness

Goal: visible controls match clickable controls.

Audit/fix:

- mouse coordinate conversion,
- nested widget hit testing,
- hover state accuracy,
- dropdown/menu hitboxes,
- tooltip position,
- GUI-scale behavior,
- click-through prevention.

Gate: no obvious invisible hover zones; buttons click where rendered; menus/dropdowns select correctly; tooltips appear at the intended control.

## Phase 5: Scrolling, clipping, and containment

Goal: panels behave like panels.

Audit/fix:

- scissor/clipping behavior,
- scrollbars,
- clipped child rendering/input,
- long configurator scrolling,
- resource panel containment,
- modal/dialog layering.

Gate: long config lists scroll cleanly; clipped controls are not clickable; resource panels and modals do not bleed through incorrectly.

## Phase 6: Editor shell layout polish

Goal: make the full editor layout stable and pleasant.

Improve:

- top menu,
- left toolbox/emitter list,
- right configurator,
- bottom resources,
- scene overlay controls,
- floating particle info card,
- panel spacing and sizing.

Target mental layout:

```text
+--------------------------------------------------+
| Menu                                             |
+----------+--------------------------+------------+
| Tools    | Scene / preview          | Config     |
| Emitters |                          | Inspector  |
+----------+--------------------------+------------+
| Resources / materials / mesh / colors / curves   |
+--------------------------------------------------+
```

Gate: all major zones are visually distinct, scene remains usable, and panel collapse controls remain usable.

## Phase 7: Photon-specific polish

Goal: fix remaining Photon-owned UI roughness.

Touch only editor presentation code where needed:

- `ParticleEditor`,
- `ParticleInfoView`,
- `EmittersList`,
- `NumberFunctionConfigurator`,
- `NumberFunction3Configurator`,
- material/mesh/color/curve/gradient resource widgets,
- save/export dialog usage where Photon-owned.

Gate: emitter list is readable, particle info is a real card, restart/reset/play/info controls look clickable, resources are usable, and save/export flow is visually sane.

## Phase 8: Safe modern nice-to-haves

Only low-risk improvements:

- clearer selected rows,
- subtle padding,
- resource hover/select states,
- nicer modal buttons,
- better empty states,
- improved labels/tooltips.

Gate: usability improves without introducing framework migrations or runtime behavior changes.

## Phase 9: Final regression/review/handoff

Run the best available validation:

- compile relevant repo(s),
- launch client if possible,
- open Photon editor if possible,
- add/select emitter,
- open configurator sections,
- open dropdowns,
- switch resource tabs,
- open save/export dialog,
- test scrolling,
- test GUI scales if possible.

Then:

- review all diffs,
- fix obvious issues,
- commit final fixes,
- update `PROGRESS.md`, `NEXT_ACTIONS.md`, and `BLOCKERS.md`,
- leave morning walkthrough steps for the user.

Final acceptance target: the editor is in the most confident code-reviewed state possible before user walkthrough, with real backgrounds, visible controls, usable configurator, readable panels, sane dropdown/dialog/resource UI, fixed obvious hitbox/scroll/clipping issues, and no intentional particle/FX runtime changes.
