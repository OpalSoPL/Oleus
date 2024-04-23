Oleus v3
====
> [!IMPORTANT]
>**This project is fork of unsupported plugin Nucleus.**<br>
>**[Original Source] by *dualspiral***
* [Source]
* [Issues]
* [Downloads]
* [Old Documentation]

Licence: [MIT](LICENSE.md) (except for any third party shaded code)

Nucleus is a Sponge plugin that forms a solid base for your server, providing essential commands, events, and other
tidbits that you might need. Extremely configurable, only loading up the commands and modules you want (and providing a way for
plugins to disable modules that they replace the functionality of), and providing a simple and rich API, Nucleus is an
elite plugin for providing simple server tasks, and an essential addition to your server!

## Contributions

Are you a talented programmer wanting to contribute some code? Perhaps someone who likes to write documentation? Do you 
have a bug that you want to report? Or perhaps you have an idea for a cool new idea that would fit in with Nucleus? We'd
be grateful for your contributions - we're an open community that appreciates any help you are willing to give!

* Read our [guidelines].
* Open an issue if you have a bug to report, or a pull request with your changes.

## Getting and Building Nucleus

Nucleus currently uses Gradle 7.3.3 and is compiled using JDK 17 (but to a Java 8 target). 

To get a copy of the Nucleus source, ensure you have Git installed, and run the following commands from a command prompt
or terminal:

1. `git clone git@github.com:OpalSoPL/Oleus.git`
2. `cd Nucleus`
3. `cp scripts/pre-commit .git/hooks`

To build Nucleus, navigate to the source directory and run either:

* `./gradlew build` on UNIX and UNIX like systems (including macOS and Linux)
* `gradlew build` on Windows systems

You will find the compiled JAR which will be named like `Nucleus-[version]-plugin.jar` in `output/`. A corresponding API and
javadocs jar will also exist.

## Building against the Nucleus API

Nucleus is available via a Maven repository.

* Repo: `https://repo.drnaylor.co.uk/artifactory/list/minecraft`
* Group ID: `io.github.nucleuspowered`
* Artifact Name: `nucleus-api`

The versioning follows `version[-SNAPSHOT|-ALPHAn|-BETAn|-RCn]`, where `n` is an integer. Add the `-SNAPSHOT` section for the latest snapshot.

You can also get Nucleus as a whole this way, but internals may break at any time. The API is guaranteed to be more stable.

You can also use [JitPack](https://jitpack.io/#NucleusPowered/Nucleus) as a repository, if you prefer.

## Third Party Libraries

The compiled Nucleus plugin includes the following libraries (with their licences in parentheses):

* Vavr 1.0-alpha-4 (Apache 2.0)

See [THIRDPARTY.md](THIRDPARTY.md) for more details.

[Source]: https://github.com/OpalSoPL/Oleus
[Issues]: https://github.com/OpalSoPL/Oleus/issues
[Downloads]: #
[Old Documentation]: http://v2.nucleuspowered.org/docs
[guidelines]: Contributing.md
[Original Source]: https://github.com/NucleusPowered/Nucleus

