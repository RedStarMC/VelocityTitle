***If you have any questions or want to submit a bug report, please send
an [Issue](https://github.com/redstarmc/velocitytitle/issues)***

![License](https://img.shields.io/github/license/redstarmc/velocitytitle)
![Commit activity](https://img.shields.io/github/commit-activity/m/redstarmc/VelocityTitle)
![Repo size](https://img.shields.io/github/repo-size/redstarmc/velocitytitle)

# VelocityTitle [中文](../README.md)

**VelocityTitle** is a title plugin designed for Minecraft Velocity proxy networks.
It creates a database on the Velocity proxy to store player titles, and communicates data between Velocity and backend
servers through messaging channels. Titles are ultimately displayed and managed on backend servers via PAPI and GUI (
planned).  
This project mainly aims to fill the gap of open-source, free cross-server title plugins.
PRs are very welcome!

## Download

Before being published on other platforms, please go to [Releases](https://github.com/redstarmc/velocitytitle/releases)
to download the latest version.

## Install

1. Download the plugin  
   You can use the unified plugin, or download platform-specific plugins separately. However, the core module cannot be
   used as a standalone plugin.
2. Install  
   Install the plugin on your server as usual. Note that Velocity is required, otherwise it will not work.
3. Configure the plugin
4. Set up PAPI
   `velocitytitle_prefix` and `velocitytitle_suffix`

## Commands & Permissions

### Velocity Commands

Root command `/velocitytitle` & `/vt`
> [!tip]
> `<>` = required, `[]` = optional, `()` = choice

* `player` Player operations
    - `divide` `<name>` `[player]` Assign a title to the specified player. Defaults to yourself if no player is
      specified.
    - `list` `[player]` List all available titles for the specified player. Defaults to yourself if no player is
      specified.
    - `pick` `( prefix | suffix | all)` Remove a specific type of title from the player.
    - `revoke` `<name>` `<player>` Revoke a title from the specified player.
    - `wear` `<name>` `[player]` Equip a specific title for the specified player. Defaults to yourself if no player is
      specified.
* `title`
    - `create` `( prefix | suffix )` `<name>` `<display>` `[description]` Description defaults to "None" if not
      provided.
    - `delete` `<name>` Delete the specified title.
    - `edit` `<name>` `( display | description )` `<data>`
    - `list` List all titles.
    - `meta` `<name>` Show information about the specified title.
* `reload` Unstable, do not use for now.
* `help` Show the help list.
* `confirm` Execute the pending confirmation command (only available when command confirmation is enabled).
* `cancel` Cancel the pending confirmation command (only available when command confirmation is enabled).

> [!important]
> The title `name` must only contain English letters and underscores.  
> The title `display` can include non-ASCII characters by wrapping them in double quotes.

### Velocity Permissions

`velocitytitle.admin` Admin permission, includes all permissions an admin should have.
Commands intended for players do not require any permission setup.

## Configuration

All configuration files have detailed comments, so they are not repeated here.

## Development Plan

Phase 1 Goals:

* [x] Fix known bugs
* [x] Improve permission management
* [x] Add command confirmation system
* [ ] Optimize list queries

Phase 2 Goals:

* Hot-reload configuration files
* MySQL support
* Add GUI

Phase 3 Goals:

* Redis in-memory database
* Modded server support
* Database operation commands

<details>
<summary>Detailed Checklist</summary>
* [x] Core
    - [x] File operations
    - [x] Logging operations
* [ ] Velocity
    - [x] Command module
        * [x] Root command
        * [x] Reload configuration (Phase 2 plan)
      * [x] Command help
        * [x] Title operations
            - [x] Command help
            - [x] Create
            - [x] Delete
          - [x] Edit
          - [x] View title library
            - [x] View a single title's info
        * [ ] Database operations (optional, console-only, Phase 3)
            - [ ] File backup
            - [ ] Export data
            - [ ] Execute database statements
            - [ ] Command help
        * [x] Player operations
            - [x] Assign
            - [x] Revoke
          - [x] Command help
            - [x] Player equip title
            - [x] Player unequip title
            - [x] Player view own title library
    - [x] Configuration module
        * [x] Plugin configuration
        * [x] Language configuration
        * [x] Configuration reader and saver
    - [x] Database module
        * [x] EasySQL
        * [x] H2 database
        * [ ] MySQL database (Phase 2)
        * [x] Database operations (integrated with command module)
    - [x] Data communication with other components
    - [ ] Other
* [ ] Spigot
    - [x] Data communication with Velocity
    - [ ] Command module
        * [ ] Reload configuration (Phase 2 plan)
        * [x] Command help
        * [x] Root command
    - [ ] GUI module (optional, Phase 3)
    - [x] PAPI or other display methods
    - [ ] Other
* [ ] Fabric, same as Spigot (Phase 3)
* [ ] NeoForge, same as Spigot (Phase 3)
</details>