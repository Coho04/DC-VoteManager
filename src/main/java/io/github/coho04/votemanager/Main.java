package io.github.coho04.votemanager;

import io.github.coho04.votemanager.discord.CustomEvents;
import io.github.coho04.votemanager.discord.commands.Settings;
import io.github.coho04.votemanager.discord.commands.Vote;
import io.github.coho04.dcbcore.DCBotBuilder;

import java.sql.SQLException;

public class Main {

    private  static MysqlConnection mysqlConnection;
    private  static CustomConfig customConfig;

    public static void main(String[] args) throws SQLException {
        customConfig = new CustomConfig();
        mysqlConnection = new MysqlConnection(customConfig.getMysqlHostname(), customConfig.getMysqlUsername(), customConfig.getMysqlPassword(), customConfig.getMysqlPort());
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args, true);
        dcBotBuilder.registerCommands(new Vote(), new Settings());
        dcBotBuilder.registerEvents(new CustomEvents());
        dcBotBuilder.build();
        System.out.println("Java application started successfully");
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public static CustomConfig getCustomConfig() {
        return customConfig;
    }
}
