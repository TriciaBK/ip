//import scanner
import java.util.Scanner;
import javax.lang.model.type.NullType;
//import File I/O class
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Botbot {
    public static String line = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

    //create array for list
    public static Task[] list = new Task[100];
    public static int listSize = 0;

    //method to identify command
    public static String identifyCommand(String command) throws DukeException {
        if (command.equals("bye")) {
            return "bye";
        } else if (command.equals("list")){
            return "list";
        } else if (command.contains("mark")){
            return "mark";
        } else if (command.contains("todo")){
            return "todo";
        } else if (command.contains("deadline")){
            return "deadline";
        } else if (command.contains("event")){
            return "event";
        } else {
            throw new DukeException("☹ OOPS!!! I'm sorry, but I don't know what that means :<");
        }
    }


    //method to mark or unmark task
    public static void markUnmarkTask(String command){
        int itemIndex; //int to store index of item to mark/unmark
        //for command unmark
        if(command.contains("unmark")){
            //find the given index to unmark
            itemIndex = Integer.parseInt(command.substring(7))-1;
            //if given index is out of range
            if (itemIndex>=listSize){
                System.out.println("invalid list item");
                return;
            } else {
                list[itemIndex].unmark();
                System.out.println("OK, I've marked this task as not done yet: ");
                System.out.println(list[itemIndex]);
                System.out.println(line);
            }
        } else { //for command mark
            //find the given index to mark
            itemIndex = Integer.parseInt(command.substring(5))-1;
            //if given index is out of range
            if (itemIndex>=listSize){
                System.out.println("invalid list item");
                return;
            }else {
                list[itemIndex].mark();
                System.out.println("Nice! I've marked this task as done: ");
                System.out.println(list[itemIndex]);
                System.out.println(line);
            }
        }
    }

    //method to add todo tasks
    public static void createTodoTasks(String task) {
        //instantiate new todo object
        Todo todoTask = new Todo(task);
        //add to array
        list[listSize] = todoTask;
        listSize++;
        System.out.println("Got it. I've added this task:");
        System.out.println(todoTask);
        System.out.println("Now you have " + (listSize) + " tasks in the list.");
        System.out.println(line);
    }

    //method to add deadline tasks
    public static void createDeadlineTasks(String input) throws DukeException {
        String task;
        String deadline;
        if (!input.contains("/by")) {
            throw new DukeException("Ohno... Please check your format and include '/by'~");
        } else {
            String[] parts = input.split(" /by ");
            //check if task or deadline are null
            if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
                throw new DukeException("Task or deadline cannot be empty... Please check your input again~");
            }
            task = parts[0].substring("deadline ".length());
            deadline = parts[1];
        }
        //instantiate new deadline object
        Deadline deadlineTask = new Deadline(task, deadline);
        //add to array
        list[listSize] = deadlineTask;
        listSize++;
        System.out.println("Got it. I've added this task:");
        System.out.println(deadlineTask);
        System.out.println("Now you have " + (listSize) + " tasks in the list.");
        System.out.println(line);
    }

    //method to add eventTask
    public static void createEventTask(String input) throws DukeException {
        String task;
        String from;
        String to;
        if (!input.contains("/from") || !input.contains("/to")) {
            throw new DukeException("Uhoh... Please check your format and include '/from' and '/to'~");
        } else {
            String[] parts = input.split(" /");
            //check if task, to, from are null
            if (parts.length != 3 || parts[0].isEmpty() || parts[1].equals("from") || parts[2].equals("to")) {
                throw new DukeException("Task, from or to cannot be empty... Please check your input again~");
            }
            task = parts[0].substring("event ".length());
            from = parts[1].substring("from".length());
            to = parts[2].substring("to".length());
        }
        //instantiate new event object
        Event eventTask = new Event(task, from, to);
        //add to array
        list[listSize] = eventTask;
        listSize++;
        System.out.println("Got it. I've added this task:");
        System.out.println(eventTask);
        System.out.println("Now you have " + (listSize) + " tasks in the list.");
        System.out.println(line);
    }

//    //method to write to file
//    public static void writeToFile(String filePath, String textToAdd) throws IOException {
//        FileWriter fileWriter = new FileWriter("./data/botbot.txt");
//        fileWriter.write(textToAdd);
//        fileWriter.close();
//    }
    //method to save list to file
    public static void saveListToFile() throws IOException {
        try (FileWriter fileWriter = new FileWriter("data/botbot.txt")){
            for (int i = 0; i < listSize; i++) {
                Task task = list[i];
                fileWriter.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    //method to extract todo task
    public static void extractTodo(String savedTask){
        createTodoTasks(savedTask.substring(8));
    }

    //method to extract deadline task
    public static void extractDeadline(String savedTask) throws DukeException {
        String[] parts = savedTask.split(" \\(by: " );
        String task = parts[0].substring(8);
        String deadline = parts[1].substring(0, parts[1].length() - 1);
        if (task.isEmpty() || deadline.isEmpty()) {
                throw new DukeException("Loaded task or deadline is empty... Please check your saved list again~");
        } else {
            //instantiate new deadline object
            Deadline deadlineTask = new Deadline(task, deadline);
            //add to array
            list[listSize] = deadlineTask;
            listSize++;
        }
    }

    //method to extract event task
    public static void extractEvent(String savedTask) throws DukeException {
        String[] parts = savedTask.split(" \\(");
        String task = parts[0].substring(8);
        String[] timeSplit = parts[1].split(" to: ");
        String from = timeSplit[0].substring(6);
        String to = timeSplit[1].substring(0, timeSplit[1].length() - 1);
        if (task.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new DukeException("Loaded task or time period is empty... Please check your saved list again~");
        } else {
            //instantiate new event object
            Event eventTask = new Event(task, from, to);
            //add to array
            list[listSize] = eventTask;
            listSize++;
        }
    }

    // method to load list from file
    public static void loadListFromFile() throws IOException, DukeException {
        File file = new File("data/botbot.txt");
        if (!file.exists()){
            try {
                boolean createdDirectory = file.mkdirs();
                boolean createdFile = file.createNewFile();
            } catch (IOException e){
                System.out.println("Something went wrong: " + e.getMessage());
            }
        } else {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNext()) {
                String savedTask = fileScanner.nextLine();
                if (savedTask.contains("T")) {
                    extractTodo(savedTask);
                } else if (savedTask.contains("D")) {
                    extractDeadline(savedTask);
                } else if (savedTask.contains("E")) {
                    extractEvent(savedTask);
                }
            }
        }
    }


    //main method
    public static void main(String[] args) throws DukeException, IOException {
        //load file
        loadListFromFile();
        //message
        System.out.println("Hello! I'm Botbot \n" +
                "───────────────────────────────────────────────────────────────────────────────────────────────\n" +
                "─██████████████───██████████████─██████████████─██████████████───██████████████─██████████████─\n" +
                "─██░░░░░░░░░░██───██░░░░░░░░░░██─██░░░░░░░░░░██─██░░░░░░░░░░██───██░░░░░░░░░░██─██░░░░░░░░░░██─\n" +
                "─██░░██████░░██───██░░██████░░██─██████░░██████─██░░██████░░██───██░░██████░░██─██████░░██████─\n" +
                "─██░░██──██░░██───██░░██──██░░██─────██░░██─────██░░██──██░░██───██░░██──██░░██─────██░░██─────\n" +
                "─██░░██████░░████─██░░██──██░░██─────██░░██─────██░░██████░░████─██░░██──██░░██─────██░░██─────\n" +
                "─██░░░░░░░░░░░░██─██░░██──██░░██─────██░░██─────██░░░░░░░░░░░░██─██░░██──██░░██─────██░░██─────\n" +
                "─██░░████████░░██─██░░██──██░░██─────██░░██─────██░░████████░░██─██░░██──██░░██─────██░░██─────\n" +
                "─██░░██────██░░██─██░░██──██░░██─────██░░██─────██░░██────██░░██─██░░██──██░░██─────██░░██─────\n" +
                "─██░░████████░░██─██░░██████░░██─────██░░██─────██░░████████░░██─██░░██████░░██─────██░░██─────\n" +
                "─██░░░░░░░░░░░░██─██░░░░░░░░░░██─────██░░██─────██░░░░░░░░░░░░██─██░░░░░░░░░░██─────██░░██─────\n" +
                "─████████████████─██████████████─────██████─────████████████████─██████████████─────██████─────\n" +
                "───────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.println("What can I do for you?");
        System.out.println(line);

        //create new scanner object
        Scanner scanner = new Scanner(System.in);

        while(true) {
            String input = scanner.nextLine();
            System.out.println(line);

            try {
                //identify the command type
                String command = identifyCommand(input);

                switch (command) {
                    case "bye":
                        System.out.println("Bye! Hope to see you again soon!");
                        //close scanner
                        scanner.close();
                        return;
                    case "list":
                        for (int i = 0; i < listSize; i++) {
                            System.out.print((i + 1) + ". ");
                            System.out.println(list[i]);
                        }
                        System.out.println(line);
                        break;
                    case "mark":
                        markUnmarkTask(input);
                        saveListToFile();
                        break;
                    case "todo":
                        createTodoTasks(input.substring(5));
                        saveListToFile();
                        break;
                    case "deadline":
                        createDeadlineTasks(input);
                        saveListToFile();
                        break;
                    case "event":
                        createEventTask(input);
                        saveListToFile();
                        break;
                    default:
                        return;
                }
            } catch (DukeException | IOException e) {
                System.out.println(e.getMessage() + "\n" + line);
            }
        }
    }
}