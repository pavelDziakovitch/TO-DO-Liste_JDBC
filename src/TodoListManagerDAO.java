import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface TodoListManagerDAO {

    //USERS
    int addUser();
    User getUser(int id);
    int deleteUser(int id);
    int editUser(int id);
    ArrayList<User> getAllUsers();

    //TODO-LISTS
    ArrayList<String> getAllToDoListsOfUser(int userID);
    ToDo getTodoList(int userID, String todoID);
    String addTodoList(int userID);
    int editTodoList(int userID, String todoID);
    int deleteToDoList(int userID, String todoID);
    ArrayList<ToDo> getAllToDos();

    //TASKS
    ArrayList<Task> getAllTasksOftoDoList(int userID, String toDoID);
    int editTask(int userID, String todoID);
    int addTask(int userID, String todoID);
    int deleteTask(int userID, String todoID, int taskID);
    ArrayList<Task> getAllTasks();

    //print Menu
    void printDashBoard();
}
