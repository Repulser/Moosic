package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static co.moosic.music.Login.playerManager;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    public List<String> autoplay = new ArrayList<>();
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
    }

    public void nextTrack() {
        try (Scanner scanner = new Scanner(new File("songs.txt"))) {
            if (!autoplay.isEmpty()) autoplay.clear();
            while (scanner.hasNextLine()) {
                autoplay.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find songs.txt Exiting.");
            System.exit(1);
        }
        String randomSong;
        synchronized (RANDOM) {
            randomSong = autoplay.get(RANDOM.nextInt(autoplay.size()));
        }
        playerManager.loadItem(randomSong, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                System.out.println("Loaded! " + track.getInfo().title);
                player.startTrack(track, false);
                Login.Jda.getPresence().setGame(Game.of(track.getInfo().title));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                nextTrack();
            }

            @Override
            public void noMatches() {
                nextTrack();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                nextTrack();
            }
        });

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}