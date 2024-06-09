Better Beginnings Mod
=========

Better Beginnings mod for Minecraft v1.7.10. Requires Forge (built with 10.13.3.1448).

Kind of complicated. See the [forum topic](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/2192122-betterbeginnings-mod).

1.8 Version:
-----------
This repository is for the 1.7.10 branch of BetterBeginnings. It was moved to its own repository because GitHub in IntelliJ can be very finnicky. Source for the 1.8 version can be found [here](https://github.com/einsteinsci/betterbeginnings).

Issues:
-------
Issues regarding this repository should be posted on the [1.8 repository](https://github.com/einsteinsci/betterbeginnings/issues), to keep them together. They will be marked with a "1.7.10" version tag if the issue is confirmed.

Contributing to this project:
-----------------------------
1. Download Forge v10.13.3.1448
2. Setup with gradlew setupDecompWorkspace and gradlew eclipse
3. Fork and clone this repository in a separate folder
4. Add a new project, with src/main/java and src/main/resources
5. Change the src/main/java and src/main/resources folder to use the src/main/java and src/main/resources folders from the source you downloaded.
6. Delete the src/main/java and src/main/resources from the default "minecraft" package
7. Add the project to the build path (or something like that)
8. Do cool stuff with the code
9. When you're finished with your changes, submit a pull request.
