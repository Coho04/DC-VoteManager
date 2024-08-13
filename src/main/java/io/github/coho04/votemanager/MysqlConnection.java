package io.github.coho04.votemanager;

import io.github.coho04.votemanager.errors.CustomExceptionHandler;
import io.github.coho04.mysql.MYSQL;
import io.github.coho04.mysql.entities.Database;
import io.github.coho04.mysql.entities.Table;

public class MysqlConnection {

    private final MYSQL mysql;
    public static String dbName = "vote_manager_db";
    public static String settingTable = "settings";
    public static String clmGuildID = "guild";
    public static String clmVoteChannel = "vote";
    public static String clmVoteRole = "role";

    public MysqlConnection(String hostname, String username, String password, int port) {
        mysql = new MYSQL(hostname, username, password, port, new CustomExceptionHandler());
        if (!mysql.existsDatabase(dbName)) {
            mysql.createDatabase(dbName);
        }
        Database db = mysql.getDatabase(dbName);
        if (!db.existsTable(settingTable)) {
            db.createTable(settingTable);
        }
        Table table = db.getTable(settingTable);
        if (!table.existsColumn(clmGuildID)) {
            table.addColumn(clmGuildID);
        }
        if (!table.existsColumn(clmVoteChannel)) {
            table.addColumn(clmVoteChannel);
        }
        if (!table.existsColumn(clmVoteRole)) {
            table.addColumn(clmVoteRole);
        }
        System.out.println("DC-VoteManager] MYSQL Finished");
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
