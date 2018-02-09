package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
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
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class TrackScheduler extends AudioEventAdapter {
    AudioPlayer player;
    private AudioPlayerManager playerManager;
    private List<String> AutoPlay = new ArrayList<>();
    private final Random RANDOM = new Random();

    TrackScheduler(AudioPlayer player, AudioPlayerManager playerManager) {
        try (Scanner scanner = new Scanner(new File("songs.txt"))) {
            while (scanner.hasNextLine()) {
                AutoPlay.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find songs.txt Exiting.");
            System.exit(1);
        }
        this.playerManager = playerManager;
        this.player = player;
        proccessTracks();
        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
    }

    private void proccessTracks() {
        System.out.println("Processing " + AutoPlay.size() + " songs");
        for (String song : new ArrayList<>(AutoPlay)) {
            if (isPlaylist(song)) {
                parsePlaylist(song);
            }
        }
        if (AutoPlay.isEmpty()) {
            System.out.println("No supported songs found!");
            System.exit(1);
        }
        System.out.println(AutoPlay.size() + " songs loaded, starting");
    }

    private boolean isPlaylist(String song) {
        for (Pattern pattern : Patterns.validTrackPatterns) {
            if (pattern.matcher(song).matches()) {
                return Patterns.playlistEmbeddedPattern.matcher(song).find() || Patterns.mixEmbeddedPattern.matcher(song).find();
            }
        }
        return true;
    }

    private void parsePlaylist(String song) {
        System.out.println("Found a playlist, parsing");
        try {
            playerManager.loadItem(song, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    AutoPlay.remove(song);
                    for (AudioTrack tr : playlist.getTracks()) {
                        String uri = tr.getInfo().uri;
                        if (!AutoPlay.contains(uri)) AutoPlay.add(uri);
                    }
                    System.out.println("Parsed playlist with " + playlist.getTracks().size() + " songs");
                }

                @Override
                public void noMatches() {
                    AutoPlay.remove(song);
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    AutoPlay.remove(song);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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
                Login.Jda.getPresence().setGame(Game.playing("▶ " + track.getInfo().title));
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
                exception.printStackTrace();
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