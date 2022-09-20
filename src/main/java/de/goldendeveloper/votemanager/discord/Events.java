package de.goldendeveloper.votemanager.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.votemanager.Main;
import de.goldendeveloper.votemanager.MysqlConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;

public class Events extends ListenerAdapter {


    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        if (Main.getDeployment()) {
            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
            embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Gestoppt als", Main.getDiscord().getBot().getSelfUser().getName()));
            embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
            embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDD34 Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getDiscord().getProjektVersion()));
            embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
            embed.setTimestamp(new Date().toInstant());
            embed.setColor(0xFF0000);
            new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).thenRun(() -> System.exit(0));
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        User _Coho04_ = e.getJDA().getUserById("513306244371447828");
        User zRazzer = e.getJDA().getUserById("428811057700536331");
        String cmd = e.getName();
        if (e.isFromGuild()) {
            if (cmd.equalsIgnoreCase(Discord.cmdTodo)) {
            } else if (cmd.equalsIgnoreCase(Discord.cmdSettings)) {
            }

            if (e.getName().equalsIgnoreCase(Discord.getCmdShutdown)) {
                if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                    e.getInteraction().reply("Der Bot wird nun heruntergefahren").queue();
                    e.getJDA().shutdown();
                } else {
                    e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot Inhaber sein!").queue();
                }
            } else if (e.getName().equalsIgnoreCase(Discord.getCmdRestart)) {
                if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                    try {
                        e.getInteraction().reply("Der Discord Bot wird nun neugestartet!").queue();
                        Process p = Runtime.getRuntime().exec("screen -AmdS " + Main.getDiscord().getProjektName() + " java -Xms1096M -Xmx1096M -jar " + Main.getDiscord().getProjektName() + "-" + Main.getDiscord().getProjektVersion() + ".jar restart");
                        p.waitFor();
                        e.getJDA().shutdown();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot Inhaber sein!").queue();
                }
            } else if (cmd.equalsIgnoreCase(Discord.cmdHelp)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("**Help Commands**");
                embed.setColor(Color.MAGENTA);
                for (Command cm : Main.getDiscord().getBot().retrieveCommands().complete()) {
                    embed.addField("/" + cm.getName(), cm.getDescription(), true);
                }
                embed.setFooter("@Golden-Developer", e.getJDA().getSelfUser().getAvatarUrl());
                e.getInteraction().replyEmbeds(embed.build()).addActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.link("https://wiki.Golden-Developer.de/", "Online Übersicht"),
                        Button.link("https://support.Golden-Developer.de", "Support Anfragen")
                ).queue();
            }
        } else {
            e.reply("Dieser Command ist nur auf einem Server verfügbar!").queue();
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));

        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
        if (!table.getColumn(MysqlConnection.clmGuildID).getAll().contains(e.getGuild().getId())) {
            e.getGuild().createRole().queue(role ->  {
                role.getManager().setName("Todo").queue();
                table.insert(
                        new RowBuilder()
                                .with(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId())
                                .with(table.getColumn(MysqlConnection.clmPermRole), role.getId())
                                .build()
                );
            });
        }
    }
}
