# psymod

A Fabric mod for Minecraft 26.2 by psychananaz.

Requires JDK 25, Fabric Loader, and Fabric API.

## Structure

- `PsyMod` is the common entrypoint.
- `item/ModItemIds` contains item resource keys.
- `item/ModItems` registers items and their creative-tab entries.
- Custom item behavior lives in `item/custom` and overrides the relevant vanilla `Item` method.
- `feature/ModFeatures` initializes standalone features.
- `feature/homingbows/HomingBows` owns the Fabric callbacks and tracked-arrow lifecycle.
- `feature/homingbows/HomingArrowBehavior` contains the reusable per-arrow guidance state and logic.
- `src/client` contains client-only hooks such as the homing-arrow debug camera.
- Add common content under `src/main`; create `src/client` only for client-specific code.

## Arrow camera

Press `F6` to follow the newest in-flight bow arrow fired by the local player. If
there is no arrow yet, the camera is armed and attaches to the next one. Press
`F6` again to return to the player. The key can be changed under Controls.

## Build and run

```sh
./gradlew build
./gradlew runClient
```

The production JAR is written to `build/libs/`.

## License

CC0-1.0
