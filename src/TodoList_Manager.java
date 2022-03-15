import java.sql.*;
import java.util.*;
import java.util.Date;

public class TodoList_Manager implements TodoListManagerDAO{

    static Connection conn = DBConnectionManager.initTablesAndReturnConn();
    static Scanner userInput = new Scanner(System.in);

//User-Methods

    //returns an araylist with all current existing users
    @Override
    public ArrayList<User> getAllUsers(){
        ArrayList<User> allUsers = new ArrayList<>();
        String sql = "Select * from users";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            ResultSet result = stmt.getResultSet();
            while(result.next()) {
                allUsers.add(new User(result.getInt("Userid"), result.getString("email"), result.getString("password"), result.getString("l_name"), result.getString("f_name")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return allUsers;
        }
        System.out.println("Searching successfull");
        return allUsers;
    }

    //adds a new User to the db
    @Override
    public int addUser() {

        String email;
        String password;
        String l_name;
        String f_name;

        System.out.print("\nPflichtfeld Email : ");
        email = userInput.nextLine();
        System.out.print("Pflichtfeld Passwort : ");
        password = userInput.nextLine();
        System.out.print("(Optional) Familienname : ");
        l_name = userInput.nextLine();
        System.out.print("(Optional) Vorname : ");
        f_name = userInput.nextLine();

        String sql = "INSERT INTO users (email , password, l_name, f_name)" +
                "VALUES (?,?,?,?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, l_name.isEmpty() ? null : l_name);
            stmt.setString(4, f_name.isEmpty() ? null : f_name);
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println("INSERT SUCCESSFUL");
                System.out.println("ID DES NEUEN USERS: " + generatedKeys.getInt(1));
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("INSERT FAILED");
        return 0;
    }

    //returns the searched for User-object
    @Override
    public User getUser(int id) {
        if(id == 0){
            System.out.print("\nWelche ID wollen Sie suchen: ");
            id = userInput.nextInt();
        }
        String sql = "SELECT * FROM users WHERE userID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,id);
            stmt.execute();
            ResultSet result = stmt.getResultSet();
            if(result.next()){
                System.out.println("SELECTION SUCCESSFUL");
                User temp = new User(result.getInt("userID"), result.getString("email"), result.getString("password"), result.getString("l_name") != null ? result.getString("l_name") : null, result.getString("f_name") != null ? result.getString("f_name") : null);
                return temp;
            }
            else{
                System.out.println("NICHTS GEFUNDEN!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //remove User and children from DB
    @Override
    public int deleteUser(int id) {
        if(id == 0){
            System.out.print("\nWelche ID wollen Sie löschen: ");
            id = userInput.nextInt();
        }
        String sql = "DELETE FROM users WHERE userID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if(affectedRows > 0){
                System.out.println("DELETION SUCCESSFUL");
                return affectedRows;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("DELETION FAILED");
        return 0;
    }

    //Edits user-data, user can choose what they want to edit
    @Override
    public int editUser(int id){
        if(id == 0){
            System.out.println("Welchen User wollen Sie bearbeiten? :");
            id = userInput.nextInt();
        }
        if(getUser(id) != null){
            try {
                ResultSetMetaData col_names = getMetaData("users");
                int col_count = col_names.getColumnCount();
                HashMap<Integer, String> col_names_map = new HashMap<>();
                for (int i = 2; i <= col_count; i++) {
                    col_names_map.put(i-1, col_names.getColumnLabel(i));
                }

                HashMap<String, String> newValues = new HashMap<>();

                System.out.println("Welche der Spalten wollen Sie bearbeiten (1 - " + (col_names_map.size()) + ") ?");
                do {
                    for (String name : col_names_map.values()) {
                        System.out.printf("%-20s", name);
                    }
                    System.out.println();

                    boolean inputIsRight;
                    do {
                        inputIsRight = false;
                        int userChoice = userInput.nextInt();
                        if (userChoice < 1 || userChoice > col_names_map.size()) {
                            System.out.println("Bitte Wert zwischen (1 - " + (col_names_map.size()) + " eingeben: ");
                        } else {
                            inputIsRight = true;
                            System.out.println((col_names_map.get(userChoice) + " ändern auf: "));
                            userInput.nextLine();
                            String newValue = userInput.nextLine();
                            newValues.put(col_names_map.get(userChoice), newValue);
                        }
                    } while (!inputIsRight);
                    System.out.println("Wollen Sie noch eine Spalte bearbeiten? (Y/N) : ");
                } while (userInput.nextLine().equalsIgnoreCase("Y"));

                String sql = "UPDATE users SET ";
                Iterator<Map.Entry<String, String>> new_Iterator = newValues.entrySet().iterator();
                while (new_Iterator.hasNext()) {
                    Map.Entry<String, String> current = new_Iterator.next();
                    String key = current.getKey();
                    String value = current.getValue();
                    sql += key + " = '" + value + "' "; ;
                    if (new_Iterator.hasNext()) {
                        sql += ", ";
                    }
                }
                sql += "WHERE userID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                int affectedRows =  stmt.executeUpdate();
                if(affectedRows > 0){
                    System.out.println("UPDATE SUCCESSFUL");
                }
                return affectedRows;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("GESUCHTER USER NICHT VORHANDEN.\nBitte mit anderer User-ID Probieren!");
        }
        return 0;
    }

//Methods for ToDoListe

    //returns a Arraylist of Strings with the names of all TODOs-Lists of an user
    @Override
    public ArrayList<String> getAllToDoListsOfUser(int userID) {
        if(userID == 0){
            System.out.println("Für welchen User?: ");
            userID = userInput.nextInt();
            userInput.nextLine();
        }
        ArrayList<String> allToDosNames = new ArrayList<>();
        String sql = "Select * from todos WHERE userID = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.execute();
            ResultSet result = stmt.getResultSet();
            while(result.next()) {
                allToDosNames.add(result.getString("toDoID"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return allToDosNames;
    }

    //returns a existing TODO-List
    @Override
    public ToDo getTodoList(int userID, String todoID) {
        if(userID == 0 && todoID == null){
            System.out.println("Für welche User-ID ist die Liste?");
            userID = userInput.nextInt();
            userInput.nextLine();
            System.out.println("Welche Liste vom User soll ausgegeben werden?");
            todoID = userInput.nextLine();
        }
        String sql_getAllTasks = "Select tasks.userID, tasks.toDoId, creationDate, taskID, taskDescription from todos INNER JOIN tasks on todos.userID = tasks.userID and todos.todoID = tasks.todoID";

        try {
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql_getAllTasks);
            ArrayList<Task> tasklist = new ArrayList<>();
            Date creationdate = null;

            while (results.next()) {
                creationdate = results.getDate("creationDate");
                int taskID = results.getInt("taskID");
                String taskDesc = results.getString("taskDescription");
                tasklist.add(new Task(userID, todoID, taskID, taskDesc));
            }

            ToDo temp = new ToDo(userID, todoID, tasklist, creationdate);
            return temp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //creates new TODO-List and add it and added tasks to DB
    @Override
    public String addTodoList(int userID) {
        if(userID == 0){
            System.out.println("Bei welchem User soll die To-DO-Liste hinzugefügt werden?");
            userID = userInput.nextInt();
            userInput.nextLine();
        }

        System.out.println("Geben Sie den Titel der neuen Todo-Liste an: ");
        String toDoID = userInput.nextLine();

        String sql = "INSERT INTO todos (userID, toDoId) VALUES (?,?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setString(2, toDoID);
            int affectedRows = stmt.executeUpdate();
            if(affectedRows > 0) {
                System.out.println("ToDo List was successfully added");
            }
            System.out.print("Wollen Sie gleich Tasks anlegen? (Y/N): ");
            if(userInput.nextLine().equalsIgnoreCase("Y")){
                do {
                    addTask(userID, toDoID);
                    System.out.println("Noch einen Task anlegen? (Y/N): ");
                }while (userInput.nextLine().equalsIgnoreCase("Y"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toDoID;
    }

    //change title or tasks of an TODO-List
    @Override
    public int editTodoList(int userID, String todoID) {
        if(userID == 0 && todoID == null){
            System.out.println("Für welche User-ID ist die Liste?");
            userID = userInput.nextInt();
            userInput.nextLine();
            System.out.println("Welche Liste soll bearbeitet werden?");
            todoID = userInput.nextLine();
        }
        System.out.println("Was wollen Sie bearbeiten: (1,2..) ");
        System.out.println("1. Name der TODO-Liste!");
        System.out.println("2. Tasks in der ToDo-Liste!");
        int choice = userInput.nextInt();
        userInput.nextLine();
        if(choice == 1){
            System.out.println("Wie lautet der neue Name?: ");
            String newToDoName = userInput.nextLine();
            String sql = "UPDATE todos SET toDoID = ? WHERE userID = ? AND toDoID = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newToDoName);
                stmt.setInt(2, userID);
                stmt.setString(3, todoID);
                return stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            return editTask(userID, todoID);
        }
        return 0;
    }

    //deletes a specified TODO-List and its children
    @Override
    public int deleteToDoList(int userID, String todoID) {
        if(userID == 0 && todoID == null){
            System.out.print("\nBei welchem User wollen Sie eine ToDo-Liste löschen: ");
            userID = userInput.nextInt();
            for (String name : getAllToDoListsOfUser(userID)) {
                System.out.println(name);
            }
            System.out.print("\nWelche der oberen To-DO Listen wollen Sie löschen?: ");
            todoID = userInput.next();
        }
        String sql = "DELETE FROM todos WHERE userID = ? AND toDoID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setString(2, todoID);
            int affectedRows = stmt.executeUpdate();
            if(affectedRows > 0){
                System.out.println("DELETION SUCCESSFUL");
                return affectedRows;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //returns arraylist of all todos with their tasks / entries in table TODOs
    @Override
    public ArrayList<ToDo> getAllToDos() {
        ArrayList<ToDo> allToDos = new ArrayList<>();
        String sql = "Select * from todos";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            ResultSet result = stmt.getResultSet();
            while(result.next()) {
                allToDos.add(new ToDo(result.getInt("userID"),result.getString("toDoID"),getAllTasksOftoDoList(result.getInt("userID"), result.getString("toDoID")),result.getDate("creationDate")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return allToDos;
        }
        return allToDos;
    }

//Methods for Tasks

    //returns arraylist with all tasks of a TODO-List
    @Override
    public ArrayList<Task> getAllTasksOftoDoList(int userID, String toDoID) {
        if(userID == 0 && toDoID == null){
            System.out.println("Für welchen User sollen die Tasks ausgegeben werden?");
            userID = userInput.nextInt();
            userInput.nextLine();
            System.out.println("Von welcher ToDo-Liste sollen die Tasks ausgegeben werden?");
            toDoID = userInput.nextLine();
        }
        ArrayList<Task> allTasks = new ArrayList<>();
        String sql = "Select * from tasks WHERE userID = ? AND toDoID = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setString(2, toDoID);
            stmt.execute();
            ResultSet result = stmt.getResultSet();
            while(result.next()) {
                allTasks.add(new Task(userID ,toDoID, result.getInt("taskID"), result.getString("taskDescription")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return allTasks;
        }
        return allTasks;
    }

    //edits task description (title) and refresh table
    @Override
    public int editTask(int userID, String todoID) {
        if(userID == 0 && todoID == null){
            System.out.println("Von welchem User wollen Sie den Task bearbeiten? : ");
            userID = userInput.nextInt();
            userInput.nextLine();
            System.out.println("Von welcher ToDo-List wollen Sie den Task bearbeiten? : ");
            todoID = userInput.nextLine();
        }

        for (Task task: getAllTasksOftoDoList(userID, todoID)) {
            System.out.println(task.getTaskDescription());
        }
        System.out.println("Welchen der Task wollen Sie bearbeiten? ");
        int taskID = userInput.nextInt();
        userInput.nextLine();
        System.out.println("Neuer Wert von Task Nr. " + taskID + " : ");
        String newDesc = userInput.nextLine();
        String sql = "UPDATE tasks SET taskDescription = ? WHERE userID = ? AND toDoID = ? AND taskID = ?";
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newDesc);
            stmt.setInt(2, userID);
            stmt.setString(3, todoID);
            stmt.setInt(4, taskID);
            int affectedRows = stmt.executeUpdate();
            if(affectedRows > 0){
                System.out.println("UPDATE SUCCESSFUL");
                return affectedRows;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //adds task to table tasks and link it to a existing TODO-ID and User
    @Override
    public int addTask(int userID, String todoID) {
        if(userID == 0 && todoID == null){
            System.out.println("Bei welchem User soll der Task hinzugefügt werden?");
            userID = userInput.nextInt();
            userInput.nextLine();
            System.out.println("Bei welcher TODO-Liste soll der Task hinzugefügt werden?");
            todoID = userInput.nextLine();
        }
        String sql = "INSERT INTO tasks (userid, todoid, taskid, taskDescription)" +
                "VALUES (?,?,?,?)";

        String taskdescription;
        System.out.print("Beschreibung: ");
        taskdescription = userInput.nextLine();

        PreparedStatement stmt_InsertTask;

        try {
            stmt_InsertTask = conn.prepareStatement(sql);
            stmt_InsertTask.setInt(1, userID);
            stmt_InsertTask.setString(2, todoID);
            int newID = getNewTaskID(userID, todoID);
            stmt_InsertTask.setInt(3, newID);
            stmt_InsertTask.setString(4, taskdescription);
            stmt_InsertTask.execute();
            return newID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //removes task with set values from table tasks
    @Override
    public int deleteTask(int userID, String todoID, int taskID) {
        if(userID == 0 && todoID == null && taskID == 0){
            System.out.println("Bei welchem User soll der Task gelöscht werden?");
            userID = userInput.nextInt();
            userInput.nextLine();
            System.out.println("Bei welcher TODO-Liste soll der Task gelöscht werden?");
            todoID = userInput.nextLine();
            System.out.println("Welche Task-ID soll gelöscht werden?");
            taskID = userInput.nextInt();
            userInput.nextLine();
        }
        String sql = "DELETE FROM tasks WHERE userID = ? AND toDoID = ? AND taskID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setString(2, todoID);
            stmt.setInt(3, taskID);
            int affectedRows = stmt.executeUpdate();
            if(affectedRows > 0){
                System.out.println("DELETION SUCCESSFUL");
                return affectedRows;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //returns arraylist of all tasks / entries in table Tasks
    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        String sql = "Select * from tasks";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            ResultSet result = stmt.getResultSet();
            while(result.next()) {
                allTasks.add(new Task(result.getInt("userID"), result.getString("toDoID"),result.getInt("taskID"), result.getString("taskDescription")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return allTasks;
        }
        return allTasks;
    }


//miscellaneous

    //prints out Menu for user to choose from
    @Override
    public void printDashBoard() {
        System.out.println("Willkommen beim ToDo-Manager");
        System.out.println("Folgende Funktionen stehen Ihnen zur Verfühung:");

        System.out.println("1. Kategorie : Benutzer");
        System.out.printf("\t1. Hinzufügen");
        System.out.printf("\n\t2. Bearbeiten");
        System.out.printf("\n\t3. Entfernen");
        System.out.printf("\n\t4. User-Daten anzeigen");
        System.out.printf("\n\t5. Alle User anzeigen!\n");

        System.out.println("2. Kategorie : ToDo-Liste");
        System.out.printf("\t1. Hinzufügen");
        System.out.printf("\n\t2. Bearbeiten");
        System.out.printf("\n\t3. Entfernen");
        System.out.printf("\n\t4. Inhalt einer ToDo-Liste anzeigen");
        System.out.printf("\n\t5. Alle to-Do-Listen von User anzeigen");
        System.out.printf("\n\t6. Alle to-Do-Listen anzeigen\n");

        System.out.println("3. Kategorie : Tasks");
        System.out.printf("\t1. Hinzufügen");
        System.out.printf("\n\t2. Bearbeiten");
        System.out.printf("\n\t3. Entfernen");
        System.out.printf("\n\t4. Alle tasks von einer To-Do-Liste anzeigen");
        System.out.printf("\n\t5. Alle Tasks anzeigen\n");
    }

    //finds table headers in passed table-name
    private ResultSetMetaData getMetaData(String tablename){
        String sql = "SELECT * FROM "+ tablename;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("KEINE TABELLE GEFUNDEN");
        return null;
    }

    //"auto-increments" task-id in relation to ToDoID (e.g. "TODO1" last Task was ID 2 so next Task will get ID 3)
    private int getNewTaskID(int userID, String todoListID){

        String sql = "SELECT taskID from tasks WHERE userID = ? AND toDoID = ? ORDER BY taskID DESC";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setString(2, todoListID);
            ResultSet result = stmt.executeQuery();
            if(result.next()){
                return result.getInt(1)+1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
