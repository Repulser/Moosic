package co.moosic.music;

import com.kaaz.configuration.ConfigurationBuilder;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;


import javax.security.auth.login.LoginException;
import java.io.File;


public class Login {
    static JDA Jda;
    static TrackScheduler scheduler;

    public static void main(String[] args) {
        try {
            new ConfigurationBuilder(Config.class, new File("bot.cfg")).build(true);
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        try {
            if (isNas()) {
                System.out.println("Enabling native audio sending");
                Jda = JDABuilder.createDefault(Config.discord_token)
                        .setAudioSendFactory(new NativeAudioSendFactory())
                        .build().awaitReady();
            } else {
                Jda = JDABuilder.createDefault(Config.discord_token)
                        .build().awaitReady();
            }
            Jda.addEventListener(new MessageHandler());
            System.out.println("Use this url to add me:\n" + "https://discord.com/oauth2/authorize?client_id=" + Jda.getSelfUser().getId() + "&scope=bot");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
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

    private static boolean isNas() {
        String os = System.getProperty("os.name");
        return (os.contains("Windows") || os.contains("Linux"))
                && !System.getProperty("os.arch").equalsIgnoreCase("arm")
                && !System.getProperty("os.arch").equalsIgnoreCase("arm-linux");

    }
}
