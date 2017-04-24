package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.awt.*;

public class HandleCommands {
    @SubscribeEvent
    public void onGuildMessage(GuildMessageReceivedEvent e) {
        if (e.getMessage().getContent().toLowerCase().startsWith(Config.command_prefix.toLowerCase() + "np")) {
            AudioTrack PlayingTrack = Login.scheduler.player.getPlayingTrack();
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor("Now Playing in " + e.getJDA().getSelfUser().getName(), PlayingTrack.getInfo().uri, e.getJDA().getSelfUser().getAvatarUrl())
                    .setColor(Color.GREEN)
                    .addField("Song Name", PlayingTrack.getInfo().title, true)
                    .addField("Channel", PlayingTrack.getInfo().author, true)
                    .addField("Song Progress", String.format("`%s / %s`", Utils.getLength(PlayingTrack.getPosition()), Utils.getLength(PlayingTrack.getInfo().length)), true)
                    .addField("Song Link", "[Youtube Link](" + PlayingTrack.getInfo().uri + ")", true)
                    .setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", PlayingTrack.getInfo().identifier))
                    .build()
            ).queue();
        }
    }
}
