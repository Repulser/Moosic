package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;


public class MessageHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().toLowerCase().startsWith(Config.command_prefix.toLowerCase() + "np")) {
            AudioTrack PlayingTrack = Login.scheduler.player.getPlayingTrack();
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor("Now Playing", PlayingTrack.getInfo().uri, null)
                    .setColor(Color.GREEN)
                    .addField("Song Name", PlayingTrack.getInfo().title, true)
                    .addField("Channel", PlayingTrack.getInfo().author, true)
                    .addField("Song Progress", String.format("`%s / %s`", this.getLength(PlayingTrack.getPosition()), this.getLength(PlayingTrack.getInfo().length)), true)
                    .addField("Song Link", "[Youtube Link](" + PlayingTrack.getInfo().uri + ")", true)
                    .setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", PlayingTrack.getInfo().identifier))
                    .build()
            ).queue();
        }
    }

    private String getLength(long length) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length),
                TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))
        );
    }
}
