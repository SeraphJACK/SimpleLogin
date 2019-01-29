# SimpleLogin

A simple login mod.

Client should set password on configuration GUI(Choose Options -> Mod Options -> Search "simplelogin" -> Config -> client -> set password), or use default random generated UUID as password. When the player join a server at the first time, the server will remember the player's password(SHA256sum) as their password.

Server side can reset player's password via command (`/simplelogin reset <PlayerName>`).

Code can be find at GitHub
