package org.example.cli;

import org.example.exception.DivisionByZeroException;
import org.example.exception.SyntaxException;
import org.example.exception.UnknownVariableException;
import org.example.expression.Binary;
import org.example.expression.Expression;
import org.example.expression.FunctionCall;
import org.example.expression.Number;
import org.example.expression.Unary;
import org.example.expression.Variable;
import org.example.operation.BinaryOperation;
import org.example.operation.FunctionOperation;
import org.example.operation.UnaryOperation;
import org.example.parser.Parser;
import org.example.service.EvaluationContext;
import org.example.transform.Differentiator;
import org.example.transform.PrettyPrinter;
import org.example.transform.Simplifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Scanner;

/**
 * Демонстрационный класс, показывающий все возможности языка арифметических
 * выражений: базовый и повышенный уровни.
 *
 * <p>Состоит из двух режимов работы:
 * <ul>
 *   <li>{@link #run()} — автоматическая демонстрация из 12 секций;</li>
 *   <li>{@link #runInteractive(Scanner)} — интерактивный режим с разбором
 *       пользовательского ввода и поддержкой специальных команд.</li>
 * </ul>
 *
 * <p>Секции автоматической демонстрации:
 * <ol>
 *   <li>Наполнение модели объектами (формальное требование ≥30 узлов);</li>
 *   <li>Разбор выражений (строка → дерево);</li>
 *   <li>Вычисление выражений;</li>
 *   <li>Переменные из контекста;</li>
 *   <li>Функции min, max, abs;</li>
 *   <li>Скобки, меняющие приоритет;</li>
 *   <li>Унарный минус и плюс;</li>
 *   <li>Степень с правой ассоциативностью;</li>
 *   <li>Обработка ошибок (три типа исключений);</li>
 *   <li>Упрощение выражений (повышенный уровень);</li>
 *   <li>Печать дерева с минимальными скобками (повышенный уровень);</li>
 *   <li>Символьное дифференцирование (повышенный уровень).</li>
 * </ol>
 *
 * <p>Экземпляр создаётся с неизменяемым {@link EvaluationContext} и
 * рассчитан на однократный вызов {@link #run()}. Интерактивный режим
 * можно запускать многократно.
 *
 * <p>Класс не является потокобезопасным.
 */
public final class Demo {
    private final EvaluationContext context;

    /**
     * Создаёт демонстрацию с заданным контекстом переменных.
     *
     * <p>Контекст используется во всех секциях, где требуется вычисление
     * выражений с переменными. Контекст не копируется и не изменяется;
     * предполагается, что он уже неизменяем.
     *
     * @param context контекст вычисления со значениями переменных;
     *                не должен быть {@code null}
     */
    public Demo(EvaluationContext context) {
        this.context = context;
    }

    /**
     * Запускает автоматическую демонстрацию из 12 секций.
     *
     * <p>Каждая секция выводит заголовок и набор примеров с результатами.
     * Ошибки в секции «Обработка ошибок» перехватываются и выводятся
     * как диагностические сообщения; в остальных секциях исключения
     * не ожидаются.
     *
     * <p>Вывод направлен в {@link System#out}. Метод не выбрасывает
     * проверяемых исключений.
     */
    public void run() {
        fillModel();
        demoParsing();
        demoEvaluation();
        demoVariables();
        demoFunctions();
        demoParentheses();
        demoUnaryMinus();
        demoPower();
        demoErrors();
        demoSimplification();
        demoPrettyPrint();
        demoDifferentiation();
        demoFunctions();
    }

    /**
     * Запускает интерактивный режим: читает выражения из {@link Scanner}
     * и выводит результат вычисления или диагностики.
     *
     * <p>Поддерживаемые команды:
     * <ul>
     *   <li>{@code <expr>} — разобрать и вычислить выражение в текущем
     *       контексте;</li>
     *   <li>{@code /simplify <expr>} — упростить выражение через
     *       {@link Simplifier};</li>
     *   <li>{@code /print <expr>} — напечатать выражение с минимальными
     *       скобками через {@link PrettyPrinter};</li>
     *   <li>{@code /diff <expr> by <var>} — продифференцировать выражение
     *       по переменной {@code var} через {@link Differentiator};</li>
     *   <li>пустая строка, {@code exit} или {@code quit} — выход из режима;</li>
     *   <li>EOF (Ctrl+D / Ctrl+Z) — корректный выход.</li>
     * </ul>
     *
     * <p>Исключения ({@link SyntaxException}, {@link UnknownVariableException},
     * {@link DivisionByZeroException}, {@link UnsupportedOperationException})
     * перехватываются внутри цикла и выводятся как читаемые сообщения,
     * поэтому режим не прерывается при ошибках пользователя.
     *
     * @param scanner источник пользовательского ввода; не должен быть
     *                {@code null}. Обычно создаётся как
     *                {@code new Scanner(System.in)}
     */
    public void runInteractive(Scanner scanner) {
        System.out.println("=== Интерактивный режим ===");
        System.out.println("Вводите выражения построчно.");
        System.out.println("Доступные переменные: " + context.getVariableNames());
        System.out.println("Команды: /simplify <expr>, /print <expr>, /diff <expr> by <var>");
        System.out.println("Для выхода введите пустую строку или 'exit'.");
        System.out.println();

        while (true) {
            System.out.print("> ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String line = scanner.nextLine().trim();

            if (line.isEmpty() || line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                System.out.println("Выход из интерактивного режима.");
                break;
            }

            evaluateLine(line);
        }
    }

    // ... приватные методы без изменений ...

    private void fillModel() {
        header("1. Наполнение модели объектами");

        List<Expression> models = new ArrayList<>();

        models.add(new Number(2));
        models.add(new Number(3.5));
        models.add(new Number(0));
        models.add(new Variable("x"));
        models.add(new Variable("y"));
        models.add(new Variable("z"));
        models.add(new Unary(UnaryOperation.NEGATIVE, new Variable("x")));
        models.add(new Unary(UnaryOperation.POSITIVE, new Number(7)));
        models.add(new Binary(new Number(2), BinaryOperation.PLUS, new Number(3)));
        models.add(new Binary(new Number(9), BinaryOperation.MINUS, new Number(4)));
        models.add(new Binary(new Number(6), BinaryOperation.MULTIPLY, new Number(7)));
        models.add(new Binary(new Number(8), BinaryOperation.DIVIDE, new Number(2)));
        models.add(new Binary(new Number(2), BinaryOperation.POWER, new Number(10)));
        models.add(new FunctionCall(FunctionOperation.ABS, List.of(new Number(-5))));
        models.add(new FunctionCall(FunctionOperation.MIN, List.of(new Number(1), new Number(2))));
        models.add(new FunctionCall(FunctionOperation.MAX, List.of(new Variable("x"), new Number(0))));

        List<String> sources = List.of(
                "2 + 3 * 4",
                "(2 + 3) * 4",
                "-2 ^ 2",
                "2 ^ 3 ^ 2",
                "x + y * 2",
                "min(x, y) + 1",
                "max(abs(y), z)",
                "-(x - y) / 2"
        );

        for (String source : sources) {
            models.add(new Parser(source).parse());
        }

        int nodes = models.stream().mapToInt(Demo::countNodes).sum();

        System.out.println("Выражений верхнего уровня: " + models.size());
        System.out.println("Всего объектов модели (узлов деревьев): " + nodes);
    }

    private static int countNodes(Expression expression) {
        return 1 + switch (expression) {
            case Number number -> 0;
            case Variable variable -> 0;
            case Unary unary -> countNodes(unary.operand());
            case Binary binary -> countNodes(binary.left()) + countNodes(binary.right());
            case FunctionCall call ->
                    call.arguments().stream().mapToInt(Demo::countNodes).sum();
        };
    }


    private void demoParsing() {
        header("2. Разбор выражений (строка -> дерево)");

        showTree("2 + 3 * 4");
        showTree("(2 + 3) * 4");
        showTree("-x ^ 2");
        showTree("min(x, 2) + abs(y)");
    }

    private void demoEvaluation() {
        header("3. Вычисление выражений");

        showValue("2 + 3 * 4");
        showValue("10 / 4");
        showValue("7 - 2 * 3");
    }


    private void demoVariables() {
        header("4. Переменные из контекста");

        System.out.println("Контекст: x = 4, y = -2, z = 0.5");

        showValue("x + y * 2");
        showValue("x - y");
        showValue("z ^ 2");
    }


    private void demoFunctions() {
        header("5. Функции min, max, abs");

        showValue("min(3, 7)");
        showValue("max(3, 7)");
        showValue("abs(-9)");
        showValue("min(x, y) + max(abs(y), 1)");
    }


    private void demoParentheses() {
        header("6. Скобки меняют приоритет");

        showValue("2 + 3 * 4");
        showValue("(2 + 3) * 4");
        showValue("10 - 2 - 3");
        showValue("10 - (2 - 3)");
    }

    private void demoUnaryMinus() {
        header("7. Унарный минус и плюс");

        showValue("-5");
        showValue("--5");
        showValue("-x");
        showValue("2 * -3");
        showValue("+7 - 2");
    }


    private void demoPower() {
        header("8. Степень");

        showValue("2 ^ 10");
        showValue("2 ^ 3 ^ 2");
        showValue("-2 ^ 2");
        showValue("2 ^ -2");
    }


    private void demoErrors() {
        header("9. Обработка ошибок");

        showError("2 + * 3");      // синтаксическая, этап разбора
        showError("(1 + 2");       // синтаксическая, этап разбора
        showError("q + 1");        // неизвестная переменная, этап вычисления
        showError("1 / 0");        // деление на ноль, этап вычисления
        showError("x / (y - y)");  // деление на ноль через переменные
    }


    private void showTree(String source) {
        Expression expression = new Parser(source).parse();
        System.out.println(source + "  ->  " + expression);
    }

    private void showValue(String source) {
        Expression expression = new Parser(source).parse();
        double value = expression.evaluate(context);
        System.out.printf("%-22s = %s%n", source, format(value));
    }

    private void showError(String source) {
        System.out.println("Выражение: " + source);

        try {
            Expression expression = new Parser(source).parse();
            double value = expression.evaluate(context);
            System.out.println("  Неожиданный успех: " + format(value));
        } catch (SyntaxException e) {
            System.out.println("  Ошибка разбора:");
            System.out.println(indent(e.getMessage()));
        } catch (UnknownVariableException e) {
            System.out.println("  Ошибка вычисления: " + e.getMessage());
        } catch (DivisionByZeroException e) {
            System.out.println("  Ошибка вычисления: " + e.getMessage());
        }
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static String format(double value) {
        if (!Double.isFinite(value)) {
            return String.valueOf(value);
        }

        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }

        return String.valueOf(value);
    }

    private static String indent(String text) {
        return text.lines()
                .map(line -> "    " + line)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private void evaluateLine(String line) {
        // Специальные команды
        if (line.startsWith("/simplify ")) {
            String expr = line.substring("/simplify ".length()).trim();
            handleSimplify(expr);
            return;
        }
        if (line.startsWith("/diff ") && line.contains(" by ")) {
            int byIndex = line.lastIndexOf(" by ");
            String expr = line.substring("/diff ".length(), byIndex).trim();
            String var = line.substring(byIndex + " by ".length()).trim();
            handleDiff(expr, var);
            return;
        }
        if (line.startsWith("/print ")) {
            String expr = line.substring("/print ".length()).trim();
            handlePrint(expr);
            return;
        }

        // Обычное вычисление
        evaluateExpression(line);
    }

    private void evaluateExpression(String source) {
        try {
            Expression expression = new Parser(source).parse();
            double value = expression.evaluate(context);

            System.out.println("  " + source + "  =  " + format(value));
        } catch (SyntaxException e) {
            System.out.println(indent(e.getMessage()));
        } catch (UnknownVariableException e) {
            System.out.println("  Неизвестная переменная: " + e.getMessage());
        } catch (DivisionByZeroException e) {
            System.out.println("  Деление на ноль: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("  Внутренняя ошибка: " + e.getMessage());
        }
    }

    private void handleSimplify(String source) {
        try {
            Expression tree = new Parser(source).parse();
            Expression simplified = new Simplifier().simplify(tree);
            PrettyPrinter printer = new PrettyPrinter();
            System.out.printf("  %s  →  %s%n", source, printer.print(simplified));
        } catch (RuntimeException e) {
            System.out.println("  Ошибка: " + e.getMessage());
        }
    }

    private void handleDiff(String source, String variable) {
        try {
            Expression tree = new Parser(source).parse();
            Expression derivative = new Differentiator().differentiate(tree, variable);
            PrettyPrinter printer = new PrettyPrinter();
            System.out.printf("  d/d%s(%s) = %s%n", variable, source, printer.print(derivative));
        } catch (UnsupportedOperationException e) {
            System.out.println("  Не поддерживается: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("  Ошибка: " + e.getMessage());
        }
    }

    private void handlePrint(String source) {
        try {
            Expression tree = new Parser(source).parse();
            System.out.printf("  %s%n", new PrettyPrinter().print(tree));
        } catch (RuntimeException e) {
            System.out.println("  Ошибка: " + e.getMessage());
        }
    }

    private void demoPrettyPrint() {
        header("11. Печать с минимальными скобками");

        PrettyPrinter printer = new PrettyPrinter();

        List<String> sources = List.of(
                "2 + (3 * 4)",
                "(2 + 3) * 4",
                "(a - b) - c",
                "a - (b - c)",
                "2 ^ (3 ^ 2)",
                "(2 ^ 3) ^ 2",
                "-(2 ^ 2)",
                "(-2) ^ 2",
                "2 ^ (-3)",
                "-(2 + 3)",
                "2 * (-3)",
                "min(x + 1, 2) * abs(-y)"
        );

        for (String source : sources) {
            Expression tree = new Parser(source).parse();
            System.out.printf("%-24s ->  %s%n", source, printer.print(tree));
        }
    }

    private void demoSimplification() {
        header("10. Упрощение выражений");
        Simplifier simplifier = new Simplifier();

        showSimplified(simplifier, "2 * 3 + x");
        showSimplified(simplifier, "x * 1");
        showSimplified(simplifier, "x + 0");
        showSimplified(simplifier, "0 * x");
        showSimplified(simplifier, "x ^ 1");
        showSimplified(simplifier, "x ^ 0");
        showSimplified(simplifier, "-(-x)");
        showSimplified(simplifier, "+x");
        showSimplified(simplifier, "abs(-5)");
        showSimplified(simplifier, "min(2, 3) + max(4, 1)");
    }

    private void showSimplified(Simplifier simplifier, String source) {
        Expression original = new Parser(source).parse();
        Expression simplified = simplifier.simplify(original);
        System.out.printf("%-25s  →  %s%n", source, simplified);
    }

    private void demoDifferentiation() {
        header("12. Символьное дифференцирование");

        Differentiator differentiator = new Differentiator();
        PrettyPrinter printer = new PrettyPrinter();

        // Пары: (выражение, переменная дифференцирования)
        List<Map.Entry<String, String>> cases = List.of(
                Map.entry("5", "x"),
                Map.entry("x", "x"),
                Map.entry("y", "x"),
                Map.entry("x + 1", "x"),
                Map.entry("x * x", "x"),
                Map.entry("x ^ 2", "x"),
                Map.entry("x ^ 3", "x"),
                Map.entry("2 * x ^ 2 + 3 * x - 5", "x"),
                Map.entry("x * y", "x"),
                Map.entry("x * y", "y"),
                Map.entry("(x + 1) * (x - 1)", "x")
        );

        for (Map.Entry<String, String> c : cases) {
            String source = c.getKey();
            String variable = c.getValue();

            Expression tree = new Parser(source).parse();
            Expression derivative = differentiator.differentiate(tree, variable);

            System.out.printf("d/d%-2s(%-22s) = %s%n",
                    variable, source, printer.print(derivative));
        }
    }

    private void demoFullPipeline() {
        header("13. Полный пайплайн: разбор → упрощение → печать → производная");

        Simplifier simplifier = new Simplifier();
        PrettyPrinter printer = new PrettyPrinter();
        Differentiator differentiator = new Differentiator();

        List<String> sources = List.of(
                "2 * 3 + x ^ 2",
                "x * x - x * x",
                "(x + 1) * 2 - (1 + 1)",
                "3 * (2 * x ^ 2) + 0"
        );

        for (String source : sources) {
            Expression parsed = new Parser(source).parse();
            Expression simplified = simplifier.simplify(parsed);
            Expression derivative = differentiator.differentiate(parsed, "x");

            System.out.println();
            System.out.println("Исходное:     " + printer.print(parsed));
            System.out.println("Упрощённое:   " + printer.print(simplified));
            System.out.println("Производная:  " + printer.print(derivative));
        }
        System.out.println();
    }
}