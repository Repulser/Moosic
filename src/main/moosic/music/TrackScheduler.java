package moosic.music;

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

import static moosic.music.Login.playerManager;

public class TrackScheduler extends AudioEventAdapter {
    final AudioPlayer player;
    private List<String> AutoPlay = new ArrayList<>();
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    TrackScheduler(AudioPlayer player) {
        try (Scanner scanner = new Scanner(new File("songs.txt"))) {
            while (scanner.hasNextLine()) {
                AutoPlay.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find songs.txt Exiting.");
            System.exit(1);
        }
        this.player = player;
        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        nextTrack();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        nextTrack();
    }

    private String getRandomSong() {
        synchronized (RANDOM) {
            return AutoPlay.get(RANDOM.nextInt(AutoPlay.size()));
        }
    }

    private void loadTrack(String randomSong) {
        playerManager.loadItem(randomSong, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                System.out.println("Loaded! " + track.getInfo().title);
                player.startTrack(track, false);
                Login.Jda.getPresence().setGame(Game.of("▶ " + track.getInfo().title));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack tr : playlist.getTracks()) {
                    AutoPlay.remove(randomSong);
                    String uri = tr.getInfo().uri;
                    if (!AutoPlay.contains(uri)) AutoPlay.add(uri);
                }
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

    private void nextTrack() {
        String randomSong = getRandomSong();
        loadTrack(randomSong);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}