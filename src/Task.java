public class Task {

    int userID;
    String todoID;
    int taskID;
    String taskDescription;

    public Task( int userID,String todoID, int taskID, String taskDescription) {
        this.todoID = todoID;
        this.taskID = taskID;
        this.taskDescription = taskDescription;
        this.userID = userID;
    }

    public String getTodoID() {
        return todoID;
    }

    public void setTodoID(String todoID) {
        this.todoID = todoID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return taskID +
                " : " + taskDescription;
    }
}
