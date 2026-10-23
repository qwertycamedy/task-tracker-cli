package com.example.ui;

import java.util.Scanner;

public final class Ui implements IUi {
    @Override
    public final void welcome() {
        System.out.println("----------------");
        System.out.println("TASK TRACKER CLI");
    }

    @Override
    public final void showMenu() {
        System.out.println("--------------------------");
        System.out.println("- Show all commands (help)");
        System.out.println("- Exit (exit)");
    }

    @Override
    public final void showAllCommands() {
        System.out.println("---------------------------------------------");
        System.out.println("- Add Task (add)");
        System.out.println("- Update Task (update)");
        System.out.println("- Remove Task By Id (remove)");
        System.out.println("- Get Task By Id (get_task)");
        System.out.println("- Get All Tasks (get_all)");
        System.out.println("- Get Tasks with <Priority> (get_by_priority)");
        System.out.println("- Get Tasks with <Status> (get_by_status)");
        System.out.println("- Get Tasks with <Deadline> (get_by_deadline)");
        System.out.println("- Exit (exit)");
    }

    @Override
    public final String enterCommand(Scanner scanner) {
        System.out.println("----------------------");
        System.out.print("- Enter command: ");
        return scanner.nextLine();
    }

    @Override
    public final void exit() {
        System.out.println("------");
        System.out.println("Bye :)");
    }

    @Override
    public final void showErrorInvalidCommand() {
        System.out.println("----------------");
        System.out.println("Invalid command!");
    }
}
