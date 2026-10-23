package com.example;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.example.model.EPriority;
import com.example.model.EStatus;
import com.example.model.Task;
import com.example.service.TaskService;
import com.example.ui.Ui;
import com.example.utils.DateUtils;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Ui ui = new Ui();
    private static final TaskService taskService = new TaskService();

    public static void main(String[] args) {
        boolean isOnline = true;
        String command = "";

        ui.welcome();
        ui.showMenu();

        while (isOnline) {
            command = ui.enterCommand(scanner);

            switch (command) {
                case "exit" -> {
                    ui.exit();
                    isOnline = false;
                }
                case "help" -> {
                    ui.showAllCommands();
                }
                case "add" -> {
                    System.out.println("------------");
                    System.out.print("Add title: ");
                    String title = addValue();

                    System.out.print("Add priority (optional) (low / medium / high): ");
                    EPriority priority = EPriority.fromLabel(scanner.nextLine());

                    System.out.print("Add status (optional) (to-do / in progress / completed): ");
                    EStatus status = EStatus.fromLabel(scanner.nextLine());

                    System.out.print("Add deadline (optional) (dd.mm.yyyy): ");
                    LocalDate deadline = DateUtils.parseDate(scanner.nextLine());

                    Task task = new Task(title, priority, status, deadline);
                    taskService.add(task);

                    System.out.println("Task added successfully: " + task);
                }
                case "update" -> {
                    System.out.println("--------------------------------------");
                    System.out.print("Enter id which task you want to update: ");
                    int taskId = scanner.nextInt();
                    scanner.nextLine();

                    Task task = taskService.getById(taskId);

                    String input = null;

                    System.out.println("Update title: ");
                    input = scanner.nextLine();
                    if (!input.isBlank()) {
                        task.setTitle(input);
                    }

                    System.out.println("Update priority (low / medium / high): ");
                    input = scanner.nextLine();
                    if (EPriority.fromLabel(input) != null) {
                        task.setPriority(EPriority.fromLabel(input));
                    }

                    System.out.println("Update status (to-do / in progress / completed): ");
                    input = scanner.nextLine();
                    if (EStatus.fromLabel(input) != null) {
                        task.setStatus(EStatus.fromLabel(input));
                    }

                    System.out.println("Update deadline (dd.mm.yyyy): ");
                    input = scanner.nextLine();
                    if (DateUtils.parseDate(input) != null) {
                        task.setDeadline(DateUtils.parseDate(input));
                    }

                    taskService.update(task);

                    System.out.println("Task updated successfully: " + task);

                }
                case "remove" -> {
                    System.out.println("------------");
                    System.out.print("Enter id which task you want to remove: ");
                    int taskId = scanner.nextInt();
                    scanner.nextLine();
                    boolean isRemoved = taskService.removeById(taskId);

                    System.out.println("-----------------------");
                    if (isRemoved) {
                        System.out.printf("Task #%d is removed!", taskId);
                    } else {
                        System.out.printf("Task #%d is not removed!", taskId);
                    }

                }
                case "get_task" -> {
                    System.out.println("------------");
                    System.out.print("Enter task id: ");
                    int taskId = scanner.nextInt();
                    scanner.nextLine();
                    Task task = taskService.getById(taskId);
                    System.out.println(task);
                }
                case "get_all" -> {
                    System.out.println("-----------");
                    System.out.println("All tasks: ");
                    List<Task> tasks = taskService.getAll();
                    renderTasks(tasks);
                }
                case "get_by_priority" -> {
                    System.out.println("----------------------------------");
                    System.out.print("Enter which priority do you want: ");
                    EPriority priority = EPriority.fromLabel(scanner.nextLine());

                    if (priority != null) {
                        List<Task> tasks = taskService.getByPriority(priority);
                        renderTasks(tasks);
                    } else {
                        throw new InputMismatchException("Invalid priority!");
                    }
                }
                case "get_by_status" -> {
                    System.out.println("----------------------------------");
                    System.out.print("Enter which status do you want: ");
                    EStatus status = EStatus.fromLabel(scanner.nextLine());

                    if (status != null) {
                        List<Task> tasks = taskService.getByStatus(status);
                        renderTasks(tasks);
                    } else {
                        throw new InputMismatchException("Invalid status!");
                    }
                }
                case "get_by_deadline" -> {
                    System.out.println("----------------------------------");
                    System.out.print("Enter which deadline do you want (dd.mm.yyyy): ");
                    LocalDate deadline = DateUtils.parseDate(scanner.nextLine());
                    List<Task> tasks = taskService.getByDeadline(deadline);
                    renderTasks(tasks);
                }
                default -> {
                    ui.showErrorInvalidCommand();
                    ui.exit();
                    isOnline = false;
                }
            }
        }

        scanner.close();
    }

    public static String addValue() {
        String value = scanner.nextLine();

        if (value != null && !value.isBlank()) {
            return value;
        }

        return null;
    }

    public static void renderTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("----------------");
            System.out.println("Tasks not found!");
        } else {
            tasks.forEach(System.out::println);
        }
    }
}
