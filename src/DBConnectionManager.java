import java.sql.*;

public class DBConnectionManager {

    public static Connection initTablesAndReturnConn() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/todo_list", "root", "");
            System.out.println("Connection successful");
            Statement stmt = conn.createStatement();
            String sql_create_USERS = "CREATE TABLE IF NOT EXISTS users(" +
                    "userID INT AUTO_INCREMENT PRIMARY KEY," +
                    "email VARCHAR(64) UNIQUE NOT NULL," +
                    "password VARCHAR(18) NOT NULL," +
                    "l_name VARCHAR(32)," +
                    "f_name VARCHAR(32))";

            String sql_create_Todos= "CREATE TABLE IF NOT EXISTS todos(" +
                    "userID INT," +
                    "toDoID VARCHAR(64)," +
                    "creationDate timestamp DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY(userID, toDoID)," +
                    "FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE)";


            String sql_create_Tasks= "CREATE TABLE IF NOT EXISTS tasks(" +
                    "userID INT," +
                    "toDoID VARCHAR(64)," +
                    "taskID INT," +
                    "taskDescription VARCHAR(150) NOT NULL," +
                    "PRIMARY KEY(userID, toDoID, taskID)," +
                    "FOREIGN KEY (userID,toDoID) REFERENCES todos(userid,toDoID) ON DELETE CASCADE ON UPDATE CASCADE)";

            stmt.executeUpdate(sql_create_USERS);
            stmt.executeUpdate(sql_create_Todos);
            stmt.executeUpdate(sql_create_Tasks);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
