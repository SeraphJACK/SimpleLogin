# SimpleLogin

SimpleLogin is a forge mod aimed to protect servers in off-line mode.

## Introduction

SimpleLogin is similar to some auth plugins in bukkit like AuthMe: the mod will check player's password when they login.

But here's some differences: instead of ask password using command, passwords are stored in client's configuration file, so that player don't have to input them every time.
Besides, client will generate a random password, so in most cases users won't even need to do anything.

## Installation

Put the `simplelogin-xxx.jar` into the `mods` directory on both server-side and client-side.

## Usage

**You don't need to change anything in most cases.**

The player's password doesn't need to be inputted manually, instead, the client would generate a random UUID as the password and save it in the client's configuration file, sending it to the server when joining.

The server would store the player's password the first time he joins in, and then check the password every time he joins in.

## FAQ

_Is it safe to store the password in the player's data?_

Sure, the password is saved using BCrypt.

_What should I do if I want to change a client?_

You should backup your `minecraft_folder/config/simplelogin.cfg` file and copy it to your new client.

## Commands

### Server

- `/simplelogin unregister <PlayerName>`

Unregister the player.

- `/simplelogin save`

Save all player entries.

- `/simplelogin setDefaultGameType <PlayerName> <GameType>`

Configure the after-login game type for the player.

### Client

- `/sl_changepassword <OldPassword> <NewPassword>`

Change your password.

Please note that you need to change the password in your client configuration file manually, and it is suggested to change it via the configuration GUI for you don't need to restart your client by this way.

## Configuration

### Client

- Password: String

Your password which will be sent to the server.

### Server

- Login Timeout: Integer

How many seconds to wait before a player login.

- White-listed commands: String Array

Commands which are allowed to be executed by a player who hasn't log in.

- Storage Method: String

How to storage player entries. Available methods are `file` and `capability`.
