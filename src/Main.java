import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TodoList_Manager todoList = new TodoList_Manager();
        Scanner userInput = new Scanner(System.in);

        do{
            todoList.printDashBoard();
            System.out.println("Bitte Kategorie-Nr wählen: ");
            int cat = userInput.nextInt();
            System.out.println("Bitte Menüpunkt-Nr wählen: ");
            int menuSelection = userInput.nextInt();
            userInput.nextLine();
            switch (cat){
                case 1:
                    switch (menuSelection){
                        case 1: todoList.addUser(); break;
                        case 2:
                            System.out.println("Rows affected: " + todoList.editUser(0));break;
                        case 3:
                            System.out.println("Rows affected: " + todoList.deleteUser(0));break;
                        case 4:
                            System.out.println(todoList.getUser(0).toString());break;
                        case 5:
                            for(User user : todoList.getAllUsers()){
                            System.out.println(user.toString());
                            }
                            break;
                    }
                    break;
                case 2:
                    switch (menuSelection){
                        case 1:
                            System.out.println("HINZUGEFÜGTE ID: " + todoList.addTodoList(0)); break;
                        case 2:
                            System.out.println("Rows affected: " + todoList.editTodoList(0, null));break;
                        case 3:
                            System.out.println("Rows affected: " + todoList.deleteToDoList(0,null));break;
                        case 4:
                            System.out.println(todoList.getTodoList(0,null)); break;
                        case 5: for(String name : todoList.getAllToDoListsOfUser(0)){
                                    System.out.println(name);
                                } break;
                        case 6: for(ToDo todo : todoList.getAllToDos()){
                                    System.out.println(todo.toString());
                                } break;
                    }
                    break;
                case 3:
                    switch (menuSelection){
                        case 1:
                            System.out.println("Neue TaskID : " + todoList.addTask(0, null));break;
                        case 2:
                            System.out.println("Rows affected: " + todoList.editTask(0,null));break;
                        case 3:
                            System.out.println("Rows affected: " + todoList.deleteTask(0,null,0));break;
                        case 4: for(Task task : todoList.getAllTasksOftoDoList(0,null)){
                            System.out.println("ToDo-Liste: " + task.getTodoID());
                            System.out.println(task);
                            }break;
                        case 5:
                            for(Task task : todoList.getAllTasks()){
                                System.out.println("UserID: " + task.getUserID());
                                System.out.println("ToDO-ID: " + task.todoID);
                                System.out.println(task + "\n");
                        } break;
                    }
                    break;
                default: System.out.println("Bitte gütigen Wert eigeben!");
            }
            System.out.println("Programm beenden? (Y/N) : ");
        }while(userInput.nextLine().equalsIgnoreCase("N"));

    }
}
