package io.github.coho04.votemanager.discord;

import io.github.coho04.votemanager.Main;
import io.github.coho04.votemanager.MysqlConnection;
import io.github.coho04.votemanager.discord.commands.Vote;
import io.github.coho04.mysql.entities.RowBuilder;
import io.github.coho04.mysql.entities.SearchResult;
import io.github.coho04.mysql.entities.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

public class CustomEvents extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equals(Vote.addVoteModelID)) {
            String title = e.getValue(Vote.addVoteModelTitle).getAsString();
            String description = e.getValue(Vote.addVoteModelDescription).getAsString();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(title);
            embedBuilder.setDescription(description);
            embedBuilder.addField("Zuletzt aktualisiert", "Von: " + e.getUser().getAsMention(), false);
            embedBuilder.setTimestamp(new Date().toInstant());
            embedBuilder.setFooter("@VoteManager", e.getJDA().getSelfUser().getAvatarUrl());
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.addField("Abstimmung-ID", "#" + Instant.now().getEpochSecond(), false);
            e.reply("Die Abstimmung wurde hinzugefügt!").setEphemeral(true).queue();
            getTextChannel(e.getGuild()).sendMessageEmbeds(embedBuilder.build()).queue(m -> {
                m.addReaction(Emoji.fromUnicode("U+1F44D")).queue();
                m.addReaction(Emoji.fromUnicode("U+1F44E")).queue();
            });
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
        if (!table.getColumn(MysqlConnection.clmGuildID).getAll().getAsString().contains(e.getGuild().getId())) {
            e.getGuild().createRole().queue(role -> {
                role.getManager().setName("Vote").queue();
                table.insert(
                        new RowBuilder()
                                .with(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId())
                                .with(table.getColumn(MysqlConnection.clmVoteRole), role.getId())
                                .build()
                );
            });
        }
    }

    public TextChannel getTextChannel(Guild guild) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
        HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.clmGuildID), guild.getId()).getData();
        return guild.getTextChannelById(row.get( MysqlConnection.clmVoteChannel).getAsString());
    }
}
