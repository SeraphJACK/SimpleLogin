# SimpleLogin

A simple login mod.

Client should set password on configuration GUI(Choose Options -> Mod Options -> Search "simplelogin" -> Config -> client -> set password), or use default random generated UUID as password. When the player join a server at the first time, the server will remember the player's password(SHA256sum) as their password.

Server side can reset player's password via command (`/simplelogin reset <PlayerName>`).

Player can change their password via command `/sl_changepassword <Old Password> <New Password>`

If some modpacks need to execute some commands when player join in, which would be canceled by SimpleLogin due to player hasn't finished login during that time, operators can add the commands' name to SimpleLogin's configuration at server side to prevent that.
