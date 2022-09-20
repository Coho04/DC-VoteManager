package de.goldendeveloper.votemanager;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.Table;

public class MysqlConnection {

    private final MYSQL mysql;
    public static String dbName = "GD-TodoManager";
    public static String settingTable = "settings";
    public static String clmGuildID = "guild";
    public static String clmOpenChannel = "open";
    public static String clmProcessChannel = "process";
    public static String clmClosedChannel = "closed";
    public static String clmPermRole = "role";

    public MysqlConnection(String hostname, String username, String password, int port) {
        mysql = new MYSQL(hostname, username, password, port);
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
        if (!table.existsColumn(clmOpenChannel)) {
            table.addColumn(clmOpenChannel);
        }
        if (!table.existsColumn(clmProcessChannel)) {
            table.addColumn(clmProcessChannel);
        }
        if (!table.existsColumn(clmClosedChannel)) {
            table.addColumn(clmClosedChannel);
        }
        if (!table.existsColumn(clmPermRole)) {
            table.addColumn(clmPermRole);
        }
        System.out.println("MYSQL Finished");
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
