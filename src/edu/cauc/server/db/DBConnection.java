package edu.cauc.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 单例类。创建数据库并建立连接
 */
public class DBConnection {

    // define the driver to use
    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    // the database name
    private static final String dbName = "USERDB";
    // define the Derby connection URL to use
    private static final String connectionURL = "jdbc:derby:" + dbName + ";create=true";
    private static Connection conn = null;

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            Class.forName(driver);
            System.out.println(driver + " loaded. ");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (conn == null) {
                conn = DriverManager.getConnection(connectionURL);
                String sql = "create table USERTABLE " // 表名
                        + "(USERNAME varchar(20) primary key not null, " // 用户名
                        + "HASHEDPWD char(20) for bit data, " // 口令的HASH值
                        + "REGISTERTIME timestamp default CURRENT_TIMESTAMP)"; // 注册时间
                // Create a statement to issue simple commands.
                Statement s = conn.createStatement();
                if (!checkTable()) {
                    System.out.println(" . . . . creating table USERTABLE");
                    s.execute(sql);
                }
                System.out.println("Database openned normally");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    /*** Check for USERTABLE table ****/
    private static boolean checkTable() throws SQLException {
        try {
            Statement s = conn.createStatement();
            s.execute("update USERTABLE set USERNAME= 'TEST', REGISTERTIME = CURRENT_TIMESTAMP where 1=3");
        } catch (SQLException sqle) {
            String theError = (sqle).getSQLState();
            // System.out.println(" Utils GOT: " + theError);
            /** If table exists will get - WARNING 02000: No row was found **/
            if (theError.equals("42X05")) // Table does not exist
            {
                return false;
            } else if (theError.equals("42X14") || theError.equals("42821")) {
                System.out
                        .println("checkTable: Incorrect table definition. Drop table USERTABLE and rerun this program");
                throw sqle;
            } else {
                System.out.println("checkTable: Unhandled SQLException");
                throw sqle;
            }
        }
        return true;
    }

}
