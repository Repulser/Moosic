# Moosic

**Host your own 24/7 Discord Music Bot!**

Moosic is a simple bot that is easy to host and setup.
Once setup, moosic will join the designated voice channel and play songs randomly from songs.txt 24/7.
This bot is a fork of [Repulser/Moosic](https://github.com/Repulser/Moosic).

## Getting Started

### Notes:
We suggest using a VPS to keep your music running truly 24/7. Some good hosts are [OVH](https://www.ovh.com/world/vps/) and [VPSCheap](https://www.vpscheap.net/).

This guide will assume you are running **Ubuntu 16.04** on your host.

### Download files
First, we want to move the files onto your host. You can do this with the `wget` command to clone it from a web host.
Run the command below to download the files:
```
wget https://cdn.glitch.com/7b0b23d6-1b33-4dc8-8c24-ac76f236af76%2Fmoosic.zip?1554571243274
```

The files you just downloaded are compressed. Unzip the files with:
```
unzip moosic.zip
```

### Configuring the bot
Now, it's time to edit the bot config. We suggest doing this with an FTP client.

##### **First, find the file named `bot.cfg`.**
This is the file that has all of the configurations that we need.

##### **Edit `bot.cfg`.**
> You will need a program like *Notepad++* to edit `.cfg` files. You can download *Nodepad++* [here](https://notepad-plus-plus.org/download/v7.6.6.html).

Follow the guide below to change each entry:

command_prefix : The prefix for the bot. There is only one command, `np`.
discord_token : The Discord bot token. If you don't know what this is, [check here]
(https://gist.github.com/noahmarshall12/4921d9fe94c209f700b7c1d43718182b).
voice_channel_id : The ID of the voice channel you want the bot to play music in.
volume : How loud the music plays in the voice channel. `50` is reccomened for the least amount of lag.

### **Start a screen.**
To keep your bot running, even when you close your SSH connection, we're going to install `screen`.
Run the command below to install `screen`.
```
sudo apt-get install screen
```

After installing screen on your remote systems, start the screen session:
```
screen
```

### **Start the bot.**
Run the command below to run the bot:
```
java -jar moosic.jar
```

### **Detatch from the screen.**
The last (and one of the most important steps) to keep your bot running.
Press **"Ctrl-A"**, then **"D"**.
You should see a message like below:
```
[detached from 1365.pts-0.server]
```

You can now close your SSH connection.

### **You're done!**
That's it! You're bot should now be online and playing music.
