# SimpleLogin

SimpleLogin is a forge mod aimed to protect servers in off-line mode.

## Installation

Put the `simplelogin-xxx.jar` into the `mods` directory on both server side and client side.

## Usage

**You don't need to change anything in most cases.**

The player's password doesn't need to be inputed manually, instead, the client would generate a random UUID as the password and save it in the client's configuration file, sending it to the server when joining.

The server would store the player's password in his player data the first time he join in, and check the password every time he join in after the "registration".

## FAQ

_Is it safe to store the password in player's data?_

Sure, the password is saved as SHA256 sum.

_What should I do if I want to change a client?_

You should backup your `minecraft_folder/config/simplelogin.cfg` file and copy it to your new client.

## Commands

### Server

- `/simplelogin reset <PlayerName>`

Overwrite the player's password the next time he join in.

- `/simplelogin list`

Show the list of players whose password is being reset.

### Client

- `/sl_changepassword <OldPassword> <NewPassword>`

Change your password.

Please note that you need to change the password in your client configuration file manually, and it is suggested to change it throw the configuration GUI for you don't need to restart your client by this way.

## Configuration

### Client

- Password: String

Your password which will be sent to the server.

### Server

- Login Timeout: Integer

How many seconds to wait before a player login.

- White-listed commands: String Array

Commands which are allowed to be executed by a player who haven't login.

