package de.goldendeveloper.votemanager.discord.commands;

import de.goldendeveloper.dcbcore.DCBot;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.votemanager.Main;
import de.goldendeveloper.votemanager.MysqlConnection;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Settings implements CommandInterface {

    private final String cmdSettingsSubCmdSetVoteChannel = "set-vote-channel";
    private final String cmdSettingsSubCmdOptionChannel = "textchannel";

    @Override
    public CommandData commandData() {
        return Commands.slash("settings", "Stellt den Discord Bot für diesen Server ein!")
                .addSubcommands(
                        new SubcommandData(cmdSettingsSubCmdSetVoteChannel, "Setzt den Vote Channel!").addOption(OptionType.CHANNEL, cmdSettingsSubCmdOptionChannel, "Der Channel in den die Votes gesendet werden sollen!", true)
                );
    }

    @Override
    public void runSlashCommand(SlashCommandInteractionEvent e, DCBot dcBot) {
        if (e.getSubcommandName() != null && e.getSubcommandName().equals(cmdSettingsSubCmdSetVoteChannel)) {
            TextChannel channel = e.getOption(cmdSettingsSubCmdOptionChannel).getAsChannel().asTextChannel();
            if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.settingTable)) {
                    Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
                    if (table.getColumn(MysqlConnection.clmGuildID).getAll().getAsString().contains(e.getGuild().getId())) {
                        table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).set(table.getColumn(MysqlConnection.clmVoteChannel), channel.getId());
                        e.reply("Der Channel wurde erfolgreich eingestellt!").queue();
                    } else {
                        e.reply("ERROR: Lade den Bot neu auf deinen Discord Server ein!!!").queue();
                    }
                }
            }
        }
    }
}
