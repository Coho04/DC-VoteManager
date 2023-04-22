package de.goldendeveloper.votemanager;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.votemanager.errors.ExceptionHandler;

public class MysqlConnection {

    private final MYSQL mysql;
    public static String dbName = "GD-VoteManager";
    public static String settingTable = "settings";
    public static String clmGuildID = "guild";
    public static String clmVoteChannel = "vote";
    public static String clmVoteRole = "role";

    public MysqlConnection(String hostname, String username, String password, int port) {
        mysql = new MYSQL(hostname, username, password, port, new ExceptionHandler());
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
        System.out.println("[Golden-Developer][GD-VoteManager] MYSQL Finished");
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
