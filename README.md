# Moosic

**Host your own 24/7 Discord Music Bot!**

Moosic is a simple bot that is easy to host and setup.
Once setup, moosic will join the designated voice channel and play songs randomly from songs.txt 24/7.

## Instructions
1. Install java. (Use google)
2. Get the latest version from [Here](https://github.com/Repulser/Moosic/releases/), I recommend you get the full.zip then unpack it for ease of use.
3. Place a list of individual song urls inside `songs.txt`, Supported formats include youtube and soundcloud.
4. Create a bot token, a good guide is [Here](https://twentysix26.github.io/Red-Docs/red_guide_bot_accounts/#creating-a-new-bot-account)
5. Open `config.json` and change the token and volume fields.
6. Inside `config.json` edit the Channel ID field to a voice channel ID, the channels ID can be grabbed using discord developer mode (Settings -> Appearance) Then you can just Right click any voice channel and Copy ID.
7. Run the bot using the bat file if you are using windows and the sh file for linux.
8. To add the bot use the client ID from the page where you created the bot token and place it in this URL:
https://discordapp.com/oauth2/authorize?client_id=:client_id&scope=bot
