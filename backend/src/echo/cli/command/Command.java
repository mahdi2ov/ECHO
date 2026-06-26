package echo.cli.command;

import java.util.Scanner;

public interface Command {
    String getName();
    void execute(Scanner scanner);
}
