package co.moosic.music;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.AudioManager;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Login {
    private static String token;
    private static final JsonParser Parser = new JsonParser();
    static AudioPlayerManager playerManager;
    static int Volume = 30;
    public static JDA Jda;

    public static void main(String args[]) {
        JSONObject obj = null;
        try {
            JsonElement Config = Parser.parse(new FileReader("config.json"));
            obj = new JSONObject(Config.toString());
            token = obj.getString("Token");
            Volume = obj.getInt("Volume");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find config file. exiting.");
            System.exit(1);
        }
        try {
            Jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .buildBlocking();
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (playerManager == null) {
            playerManager = new DefaultAudioPlayerManager();
            AudioSourceManagers.registerRemoteSources(playerManager);
            System.out.println("Created new player manager");
        }
        String vcid = obj.getString("ChannelID");
        VoiceChannel channel = Jda.getVoiceChannelById(vcid);
        if(channel == null) {
            System.out.println("Could not find the channel, make sure the ID is correct and that the bot can see it.");
            System.exit(1);
        }
        AudioManager audioManager = channel.getGuild().getAudioManager();
        try {
            audioManager.openAudioConnection(channel);
            System.out.println("Joined designated voice channel " + channel.getName());
        } catch (Exception ex) {
            System.out.println("Failed to join the voice channel! " + ex.getMessage());
            System.exit(1);
        }
        AudioPlayer player = playerManager.createPlayer();
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
        TrackScheduler scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        player.setVolume(Volume);
    }
}
