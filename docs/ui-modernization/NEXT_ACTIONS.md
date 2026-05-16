# Next Actions

Code-side work is complete. Morning walkthrough checklist for the user:

1. Launch the 1.18.2 client with the updated local LDLib-MultiLoader and Photon.
2. Open the Photon particle editor.
3. Check these UI areas:
   - right Configurator panel backgrounds and configurator rows,
   - number/string/selector/boolean controls,
   - top menus and file dialogs/export dialogs,
   - left emitter list and add-emitter context menu,
   - Particle Information floating view,
   - bottom resource panel tabs and resource item cards.
4. Report any remaining invisible/offset hover zones with screenshots and the exact control being hovered/clicked.
5. If anything still feels bad, continue from this tracker rather than re-auditing from scratch.


LDLib dependency note:
- Photon is now pinned to the local Maven artifact version `1.0.26-ui-local.2`.
- If more LDLib UI edits are made, republish from `/Users/rubenvancraenenbroeck/IdeaProjects/LDLib-MultiLoader` with `sh ./gradlew publishToMavenLocal -Pmod_version=1.0.26-ui-local.2 --no-daemon --stacktrace`, then rerun Photon.
