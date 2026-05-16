# Validation Commands and Manual Tests

## Java

Use Java 17 for this repo family.

## Photon compile

```bash
cd /Users/rubenvancraenenbroeck/IdeaProjects/Photon
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" :photon-forge:compileJava --no-daemon --stacktrace
```

## Photon dependency check

After wiring the experimental LDLib artifact:

```bash
cd /Users/rubenvancraenenbroeck/IdeaProjects/Photon
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" :photon-forge:dependencyInsight --dependency ldlib-forge --configuration modImplementation --no-daemon
```

Expected: resolves the experimental local LDLib version, not the normal release artifact.

## Photon runClient

```bash
cd /Users/rubenvancraenenbroeck/IdeaProjects/Photon
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" :photon-forge:runClient --no-daemon --stacktrace
```

## LDLib publish

Finalize after checking LDLib Gradle properties. Expected shape:

```bash
cd /Users/rubenvancraenenbroeck/IdeaProjects/LDLib-MultiLoader
sh ./gradlew -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 17)" publishToMavenLocal -Pmod_version=1.0.26-120port.1 --no-daemon --stacktrace
```

## Manual editor smoke test

In a dev world:

- [ ] Open Photon editor.
- [ ] Confirm 1.20-style layout appears.
- [ ] Add particle FX object.
- [ ] Add beam FX object.
- [ ] Add trail FX object.
- [ ] Select objects in the object/subtree list.
- [ ] Expand/collapse child containers if present.
- [ ] Toggle boolean settings and confirm off/on states are visible.
- [ ] Change dropdown/select settings and confirm the field is visibly clickable.
- [ ] Edit a text field and confirm input background is visible.
- [ ] Use resources panel on a compatible setting.
- [ ] Restart preview.
- [ ] Pause/play preview if available.
- [ ] Save/export an effect.
- [ ] Reload the saved effect.
- [ ] Spawn effect at a block.
- [ ] Spawn effect on an entity.
- [ ] Remove block/entity effect if remove commands are ported.

## Runtime regression checks

- [ ] No startup crash.
- [ ] No missing critical texture/shader crash.
- [ ] No mixin apply failure.
- [ ] No invisible editor panels/buttons/toggles in the main workflow.
- [ ] Particle, beam, and trail render in preview.
- [ ] Particle, beam, and trail render in-world.
