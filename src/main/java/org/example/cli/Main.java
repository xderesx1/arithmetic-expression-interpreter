package org.example.cli;

import org.example.service.EvaluationContext;

import java.util.Map;
import java.util.Scanner;

/**
 * Точка входа в приложение — консольный демонстратор языка арифметических
 * выражений.
 *
 * <p>Класс является утилитарным: экземпляр не создаётся, вся работа
 * выполняется в статическом методе {@link #main(String[])}. Основная
 * логика демонстрации находится в классе {@link Demo}; этот класс отвечает
 * только за:
 * <ul>
 *   <li>создание контекста вычисления с предопределёнными переменными;</li>
 *   <li>парсинг аргументов командной строки;</li>
 *   <li>выбор режима работы (автоматический, интерактивный или оба);</li>
 *   <li>управление общим {@link Scanner} на {@link System#in}.</li>
 * </ul>
 *
 * <h2>Режимы работы</h2>
 * <ul>
 *   <li>По умолчанию: сначала автоматическая демонстрация из 12 секций,
 *       затем запрос на переход в интерактивный режим.</li>
 *   <li>С аргументом {@code --interactive}: только интерактивный режим,
 *       без автодемонстрации.</li>
 * </ul>
 *
 * <h2>Примеры запуска</h2>
 * <pre>
 * java -cp target/classes org.example.cli.Main                # автодемо + интерактив
 * java -cp target/classes org.example.cli.Main --interactive  # только интерактив
 * </pre>
 *
 * <p>Класс не является потокобезопасным и не рассчитан на повторный вызов
 * {@link #main(String[])} в рамках одного процесса.
 *
 * @see Demo
 * @see EvaluationContext
 */
public final class Main {

    /**
     * Приватный конструктор: класс является утилитарным и не предполагает
     * создания экземпляров.
     */
    private Main() {
    }

    /**
     * Точка входа в приложение.
     *
     * <p>Создаёт контекст вычисления с переменными {@code x = 4},
     * {@code y = -2}, {@code z = 0.5}, экземпляр {@link Demo} и общий
     * {@link Scanner} на {@link System#in}. В зависимости от аргументов
     * командной строки запускает автоматическую демонстрацию, интерактивный
     * режим или оба по очереди.
     *
     * <p>{@link Scanner} намеренно <b>не закрывается</b> в блоке
     * {@code finally}, поскольку он обёрнут вокруг {@link System#in}:
     * закрытие сканера закрыло бы стандартный поток ввода, что сломало бы
     * последующий интерактивный режим. Поток ввода закрывается автоматически
     * при завершении JVM.
     *
     * @param args аргументы командной строки. Поддерживается один
     *             необязательный флаг:
     *             <ul>
     *               <li>{@code --interactive} — запустить только
     *                   интерактивный режим, минуя автодемонстрацию.</li>
     *             </ul>
     *             Любые другие аргументы игнорируются.
     */
    public static void main(String[] args) {
        var context = new EvaluationContext(Map.of(
                "x", 4.0,
                "y", -2.0,
                "z", 0.5
        ));

        Demo demo = new Demo(context);

        boolean interactiveOnly = args.length > 0 && "--interactive".equals(args[0]);

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
        }
    }

    /**
     * Считывает ответ пользователя на вопрос «да/нет» и возвращает
     * {@code true}, если ответ положительный.
     *
     * <p>Распознаёт положительные ответы в верхнем и нижнем регистре на
     * английском ({@code y}, {@code yes}) и русском ({@code д}, {@code да}).
     * Любой другой ответ, пустая строка или EOF интерпретируются как
     * отрицательный ответ.
     *
     * @param scanner источник пользовательского ввода; не должен быть
     *                {@code null}
     * @return {@code true}, если пользователь ответил утвердительно,
     *         иначе {@code false}
     */
    private static boolean confirm(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return false;
        }

        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes")
                || answer.equals("д") || answer.equals("да");
    }
}