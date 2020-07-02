# SimpleLogin

SimpleLogin is a forge mod aimed to protect servers in the off-line mode.

## Introduction

SimpleLogin is similar to some auth plugins in bukkit like AuthMe: the mod will check player's password when they log in.

However, here're some differences: instead of ask password using command, passwords are stored in the client's configuration file, so that player doesn't have to input them every time.
Besides, client will generate a random password, so in most cases users won't even need to do anything.

## Installation

Put the `simplelogin-xxx.jar` into the `mods` directory on **both server-side and client-side**.

## Usage

**You don't need to change anything in most cases.**

Players' password does not need to be inputted manually, instead, client would generate a random UUID as the password and save it in the client's configuration file, and send it to the server when joining.

The server would store the player's password the first time he joins in, and then check the password every time he joins in.

## FAQ

_Is it safe to store the password in the player's data?_

Sure, the password is saved using BCrypt.

_What should I do if I want to change the client?_

You should backup your `minecraft_folder/config/simplelogin.cfg` or `minecraft_folder/sl_password.txt` file and copy it to your new client.

## Commands

> All commands have auto complete support.

### Server Commands

- `/simplelogin unregister <PlayerName>`

Unregister the player.

- `/simplelogin save`

Save all player entries.

- `/simplelogin setDefaultGameType <PlayerName> <GameType>`

Configure the after-login game type for the player.

### Client Commands

- `/sl_changepassword <OldPassword> <NewPassword>`

Change your password.

Please note that you need to change the password in your client configuration file manually, and it is suggested to change it via the configuration GUI.

## Configuration

### Client Configuration

- Password: String

Your password which will be sent to server.

- StorePasswordExternally: boolean

Choose to store password within the configuration file or in `minecraft_folder/sl_password.txt`

### Server Configuration

- Login Timeout: Integer

Player login time limit.

- White-listed commands: String Array

Commands that are allowed to be executed by an unauthenticated player.

- Storage Method: String

How to storage player entries. Available methods are `file`, `capability` and `mysql`.

- mysql: MySQL

MySQL properties.

- Enable Command Login Mode: boolean

Whether to enable the command log in mode

- Protect Player Coordinate: boolean

Whether to teleport players back to spawn point when they log out
