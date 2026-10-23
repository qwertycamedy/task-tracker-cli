package com.example.ui;

import java.util.Scanner;

public interface IUi {
    void welcome();

    void showMenu();

    void showAllCommands();

    String enterCommand(Scanner scanner);

    void exit();

    void showErrorInvalidCommand();
}
