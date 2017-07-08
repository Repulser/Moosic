package co.moosic.music;

import com.kaaz.configuration.ConfigurationBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.managers.AudioManager;

import javax.security.auth.login.LoginException;
import java.io.File;


public class Login {
    static JDA Jda;
    static TrackScheduler scheduler;

    public static void main(String args[]) {
        try {
            new ConfigurationBuilder(Config.class, new File("bot.cfg")).build();
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        try {
            Jda = new JDABuilder(AccountType.BOT)
                    .setToken(Config.discord_token)
                    .buildBlocking();
            Jda.addEventListener(new MessageHandler());
            System.out.println("User this url to add me:\n" + "https://discordapp.com/oauth2/authorize?client_id=" + Jda.getSelfUser().getId() + "&scope=bot");
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        System.out.println("Created new player manager");
        VoiceChannel channel = Jda.getVoiceChannelById(Config.voice_channel_id);
        if (channel == null) {
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
        scheduler = new TrackScheduler(player, playerManager);
        player.addListener(scheduler);
        player.setVolume(Config.volume);
    }
}
