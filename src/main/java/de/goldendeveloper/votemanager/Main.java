package de.goldendeveloper.votemanager;

import de.goldendeveloper.dcbcore.DCBotBuilder;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.votemanager.discord.CustomEvents;
import de.goldendeveloper.votemanager.discord.commands.Settings;
import de.goldendeveloper.votemanager.discord.commands.Vote;

import java.util.LinkedList;

public class Main {

    private  static MysqlConnection mysqlConnection;

    public static void main(String[] args) {
        CustomConfig config = new CustomConfig();
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlUsername(), config.getMysqlPassword(), config.getMysqlPort());
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args);
        LinkedList<CommandInterface> commands = new LinkedList<>();
        commands.add(new Vote());
        commands.add(new Settings());
        dcBotBuilder.registerCommands(commands);
        dcBotBuilder.registerEvents(new CustomEvents());
        dcBotBuilder.build();
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }
}