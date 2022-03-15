import java.util.ArrayList;
import java.util.Date;

public class ToDo {

    String toDoID;
    int UserID;
    ArrayList<Task> tasks;
    Date creationDate;

    public ToDo(int userID, String toDoID, ArrayList<Task> tasks, Date creationDate) {
        this.toDoID = toDoID;
        UserID = userID;
        this.tasks = tasks;
        this.creationDate = creationDate;
    }

    public String getToDoID() {
        return toDoID;
    }

    public void setToDoID(String toDoID) {
        this.toDoID = toDoID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        String toString = toDoID +
                ": UserID=" + UserID +
                ", creationDate=" + creationDate + ";" +
                "\nTasks:\n";

        if(tasks != null){
            for(Task temp : tasks){
                toString += temp.toString() + "\n";
            }
        }
        return toString;
    }
}
