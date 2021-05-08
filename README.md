# SimpleLogin

SimpleLogin is a forge mod aimed to protect servers in off-line mode.

## Introduction

SimpleLogin is similar to some auth plugins in bukkit like AuthMe: the mod will check player's password when they log
in.

However, there are some differences: instead of inputting the password using command, passwords are stored in a specific
file at the client side and automatically sent to the server when player joins, so that players don't have to input the
password every time. Besides, client will generate a random password during the first launch, so in most cases players
won't even need to do anything.

## Installation

Put the `simplelogin-xxx.jar` into the `mods` directory on **both server-side and client-side**.

## Getting started

**You don't need to configure anything if you don't care much.**

Players' password does not need to be inputted manually, instead, client would generate a random UUID as the password
and save it in `.minecraft/.sl_password`, and send it to the server when joining.

The server will remember the player's password when he joins for the first time, and when he joins later the server will
check if the password matches the first one.

## FAQ

_Is my password safe?_

Sure, the password is hashed using SHA256 at the client side, and hashed using BCrypt before storing at the server side.
However, the password is stored without any encryption **at the client side**, so be careful don't
copy `.minecraft/.sl_password` file when sharing your client with others.

_What should I do if I want to change the client?_

You should backup your `minecraft/.sl_entry` file and copy it to your new client.

## Storage providers

There are various ways to store the user data at the server side and each has its own benefits and shortcomings. Simple
Login provides an abstract layer to allow different storage implementations. Each implementation is called a "storage
provider".

Each storage provider has its own unique resource location (i.e. `simplelogin:file`) to identify itself.

Simple Login itself provides two storage providers: `file` and `sqlite`.

### File storage provider

ResourceLocation: `simplelogin:file`

File storage provider is the simplest implementation of the storage provider interface, which stores all the user data
as JSON format at `world/sl_entries.dat`.

### SQLite storage provider

ResourceLocation: `simplelogin:sqlite`

SQLite storage provider might be more efficient than the file provider, but it needs a SQLite JDBC connector which has
roughly 6 MB and I decided not to bundle it in Simple Login. Therefore, if you want to use this provider, you'll need to
install a JDBC connector your self. The simplest way to do that is to [download it][JDBC-Download] and copy `org/sqlite`
and `META-INF/services` directories into Simple Login mod jar.

[JDBC-Download]: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.8.9.1/sqlite-jdbc-3.8.9.1.jar

### Custom storage providers

If you're familiar with Java and forge mods, it won't be very hard.

You need to implement the `top.seraphjack.simplelogin.server.storage.StorageProvider` interface, and register your own
provider before the server starts by
calling `top.seraphjack.simplelogin.server.SLRegistries.STORAGE_PROVIDERS::register`.

## Simple Login plugins

Simple Login plugins, or plugins for short, is an extension to how Simple Login handles when players join, log in and
leave the server.

Just like storage providers, each plugin has its own unique resource location to identify itself.

Some key functionalities of Simple Login are also implemented as built in plugin, and here's a list of them.

### AutoSave

ResourceLocation: `simplelogin:autosave`

This plugin will trigger Storage provider to save all data every five minutes.

This plugin is enabled by default.

### ProtectCoord

ResourceLocation: `simplelogin:protect_coord`

This plugin will teleport players back to the spawn point before they leave the server, and teleport them back when they
successfully logged in, thus prevents possible position leakage by unauthorized log in attempts.

This plugin is disabled by default since its behavior will cause some problems when playing large modpacks.

### ResendRequest

ResourceLocation: `simplelogin:resend_request`

This plugin will ask the client to log in every five seconds if by some mistakes their previous log in packet unable to
arrive the server.

This plugin is enabled by default.

### RestrictGameType

ResourceLocation: `simplelogin:restrict_game_type`

This plugin will change the player's game type to spectator when they leave or join the server, and change their game
type to their default game type (stored in storage providers) when then log in successfully.

This plugin is enabled by default.

### RestrictMovement

ResourceLocation: `simplelogin:restrict_movement`

This plugin will prevent player move around before log in.

This plugin is enabled by default.

### Timeout

ResourceLocation: `simplelogin:timeout`

This plugin will kick players who haven't log in after a specific period of time(defined in Simple Login's
configuration, default is 60 seconds).

This plugin is enabled by default.

## Commands

All commands have auto complete support.

- `/simplelogin unregister <PlayerName>`

    - Unregister the player.

- `/simplelogin save`
    - Save all player entries.

- `/simplelogin setDefaultGameType <PlayerName> <GameType>`

    - Configure the after-login game type for the player.

- `/simplelogin about`
    - Show the version information

- `/simplelogin plugin available`
    - Show the list of available plugins

- `/simplelogin plugin loaded`
    - Show the list of loaded plugins

- `/simplelogin plugin load <ResourceLocation>`
    - Load the specified plugin

- `/simplelogin plugin unload <ResourceLocation>`
    - Unload the specified plugin

## Configuration

### Server Configuration

- secs: Integer
    - Player login time limit.
    - Default 60 seconds.

- plugins: String Array
    - plugins to load by default.

- commandNames: String Array
    - Commands that are allowed to be executed by an unauthenticated player.
    - You need to specify full command without the heading `/`, such as `give @p minecraft:apple 16`

- storageProvider: String
    - The storage provider to use.

- defaultGameType: Integer
    - Default game type for newly registered users.
    - 0, 1, 2, 3 represents survival, creative, adventure and spectator.
