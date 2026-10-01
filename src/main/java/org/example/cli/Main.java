package org.example.cli;

import org.example.service.EvaluationContext;

import java.util.Map;
import java.util.Scanner;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        var context = new EvaluationContext(Map.of(
                "x", 4.0,
                "y", -2.0,
                "z", 0.5
        ));

        Demo demo = new Demo(context);

        boolean interactiveOnly = args.length > 0 && "--interactive".equals(args[0]);

        // Создаём один Scanner на всё приложение
        Scanner scanner = new Scanner(System.in);

        try {
            if (!interactiveOnly) {
                demo.run();
                System.out.println();
                System.out.println("Автодемонстрация завершена.");
                System.out.print("Перейти в интерактивный режим? [y/N]: ");

                if (!confirm(scanner)) {
                    return;
                }

                System.out.println();
            }

            demo.runInteractive(scanner);
        } finally {
            // НЕ закрываем scanner, так как он обёрнут вокруг System.in
            // System.in закроется автоматически при завершении JVM
        }
    }

    private static boolean confirm(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return false;
        }

        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes")
                || answer.equals("д") || answer.equals("да");
    }
}