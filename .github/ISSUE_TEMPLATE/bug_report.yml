name: Report a Bug
description: File a bug report. Please note that Github Issues is not for general support.
body:
- type: markdown
  attributes:
    value: "## Before You Begin"
- type: markdown
  attributes:
    value: |
      Github Issues are for filing bug reports and requesting new features only. If you need general support, please check out the following places first:
      
      * [Read more about how to use Nucleus on our website.](https://v2.nucleuspowered.org/docs)
      * [Got a quick question? Ask us on our official Discord guild](https://discord.gg/A9QHG5H)
      
      If you are sure you have found a bug with Nucleus, please make sure you have searched existing issues to see if someone else has reported your issue first. We will close duplicates.
      
      If no-one has filed an issue describing the problem you're having yet, please fill out the form below.
- type: input
  id: nucleus-version
  attributes:
    label: Nucleus Version
    description: What version of Nucleus are you running?
    placeholder: ex. 2.4.1, 3.0.0-BETA2
  validations:
    required: true
- type: input
  id: sponge-version
  attributes:
    label: Sponge Version
    description: What version of Sponge are you running? Include whether it is SpongeForge or SpongeVanilla
    placeholder: ex. SpongeVanilla 1.16.5-36.2.5-8.0.0-RC1000
  validations:
    required: true
- type: input
  id: java
  attributes:
    label: Java Version
    description: "You can get this by running `java -version` from the command line"
  validations:
    required: true
- type: textarea
  id: plugins
  attributes:
    label: Plugins/Mods
    description: |
      List all plugins and mods that you are using. Please provide the list as a text list, do not screenshot your mod and plugin folders.
    render: shell
  validations:
    required: true
- type: textarea
  id: what-happened
  attributes:
    label: Describe the bug
    description: |
      Explain what the bug is, how to reproduce it, and any workarounds you may have already found. Please include as much detail as possible, but please do not screenshot text. Logs should be provided in a Github Gist (preferred) or paste.gg and linked to here.

      If you are a developer that is reporting issues based on your code, please include that code here, enough that we can copy/paste into a test project and run it without modification and see the issue.
  validations:
    required: true
