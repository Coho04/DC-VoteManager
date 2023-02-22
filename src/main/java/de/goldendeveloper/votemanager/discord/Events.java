package de.goldendeveloper.votemanager.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.SearchResult;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.votemanager.Main;
import de.goldendeveloper.votemanager.MysqlConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

public class Events extends ListenerAdapter {

    public static final String addVoteModelID = "add-vote";
    public static final String addVoteModelTitle = "title";
    public static final String addVoteModelDescription = "description";

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        if (Main.getDeployment()) {
            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
            embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Gestoppt als", Main.getDiscord().getBot().getSelfUser().getName()));
            embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
            embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDD34 Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getConfig().getProjektVersion()));
            embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
            embed.setTimestamp(new Date().toInstant());
            embed.setColor(0xFF0000);
            new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).thenRun(() -> System.exit(0));
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        Main.getServerCommunicator().removeServer(e.getGuild().getId());
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        User _Coho04_ = e.getJDA().getUserById("513306244371447828");
        User Razzer = e.getJDA().getUserById("428811057700536331");
        String cmd = e.getName();
        if (e.isFromGuild()) {
            if (cmd.equalsIgnoreCase(Discord.cmdVote)) {
                Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
                String roleID = table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).getData().get(MysqlConnection.clmVoteRole).getAsString();
                if (!roleID.isBlank()) {
                    Role role = Main.getDiscord().getBot().getRoleById(roleID);
                    if (hasRole(role, e.getMember())) {
                        TextInput title = TextInput.create(addVoteModelTitle, "Titel", TextInputStyle.SHORT)
                                .setPlaceholder("Abstimmung´s Title").setMinLength(5).setMaxLength(50).build();

                        TextInput description = TextInput.create(addVoteModelDescription, "Beschreibung", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Abstimmung´s Beschreibung").setMinLength(15).setMaxLength(1000).build();

                        Modal modal = Modal.create(addVoteModelID, "Hinzufügen eines Abstimmung´s eintrags!")
                                .addActionRows(ActionRow.of(title), ActionRow.of(description)).build();

                        e.replyModal(modal).queue();
                    } else {
                        e.reply("Du besitzt nicht genügend Rechte für diesen Befehl! (Dir fehlt die Rolle [" + role.getAsMention() + "])").queue();
                    }
                } else {
                    e.reply("ERROR: [404][Role in MysqlTable not Found] Lade den Bot neu auf deinen Discord Server ein!!!").queue();
                }
            } else if (cmd.equalsIgnoreCase(Discord.cmdSettings)) {
                if (e.getSubcommandName() != null) {
                    if (Discord.cmdSettingsSubCmdSetVoteChannel.equals(e.getSubcommandName())) {
                        TextChannel channel = e.getOption(Discord.cmdSettingsSubCmdOptionChannel).getAsChannel().asTextChannel();
                        if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                            if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.settingTable)) {
                                Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
                                if (table.getColumn(MysqlConnection.clmGuildID).getAll().getAsString().contains(e.getGuild().getId())) {
                                    table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).set(table.getColumn(MysqlConnection.clmVoteChannel), channel.getId());
                                    e.reply("Der Channel wurde erfolgreich eingestellt!").queue();
                                } else {
                                    e.reply("ERROR: [404][Column in MysqlTable not Found] Lade den Bot neu auf deinen Discord Server ein!!!").queue();
                                }
                            }
                        }
                    }
                }
            }

            if (e.getName().equalsIgnoreCase(Discord.getCmdShutdown)) {
                if (e.getUser() == Razzer || e.getUser() == _Coho04_) {
                    e.getInteraction().reply("Der Bot wird nun heruntergefahren").queue();
                    e.getJDA().shutdown();
                } else {
                    e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot Inhaber sein!").queue();
                }
            } else if (e.getName().equalsIgnoreCase(Discord.getCmdRestart)) {
                if (e.getUser() == Razzer || e.getUser() == _Coho04_) {
                    try {
                        e.getInteraction().reply("Der Discord Bot wird nun neugestartet!").queue();
                        Process p = Runtime.getRuntime().exec("screen -AmdS " + Main.getConfig().getProjektName() + " java -Xms1096M -Xmx1096M -jar " + Main.getConfig().getProjektName() + "-" + Main.getConfig().getProjektVersion() + ".jar restart");
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
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equals(addVoteModelID)) {
            String title = e.getValue(addVoteModelTitle).getAsString();
            String description = e.getValue(addVoteModelDescription).getAsString();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(title);
            embedBuilder.setDescription(description);
            embedBuilder.addField("Zuletzt aktualisiert", "Von: " + e.getUser().getAsMention(), false);
            embedBuilder.setTimestamp(new Date().toInstant());
            embedBuilder.setFooter("@Golden-Developer", e.getJDA().getSelfUser().getAvatarUrl());
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
        Main.getServerCommunicator().addServer(e.getGuild().getId());
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));

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

    public boolean hasRole(Role role, Member m) {
        for (Role r : m.getRoles()) {
            if (r == role) {
                return true;
            }
        }
        return false;
    }


    public static TextChannel getTextChannel(Guild guild) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
        HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.clmGuildID), guild.getId()).getData();
        return guild.getTextChannelById(row.get( MysqlConnection.clmVoteChannel).getAsString());
    }
}
