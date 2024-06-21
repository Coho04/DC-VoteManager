package io.github.coho04.votemanager;

import io.github.coho04.votemanager.discord.CustomEvents;
import io.github.coho04.votemanager.discord.commands.Settings;
import io.github.coho04.votemanager.discord.commands.Vote;
import io.github.coho04.dcbcore.DCBotBuilder;

import java.sql.SQLException;

public class Main {

    private  static MysqlConnection mysqlConnection;

    public static void main(String[] args) throws SQLException {
        CustomConfig config = new CustomConfig();
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlUsername(), config.getMysqlPassword(), config.getMysqlPort());
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args, true);
        dcBotBuilder.registerCommands(new Vote(), new Settings());
        dcBotBuilder.registerEvents(new CustomEvents());
        dcBotBuilder.build();
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }
}
