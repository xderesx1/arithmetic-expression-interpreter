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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

public final class Demo {
    private final EvaluationContext context;

    public Demo(EvaluationContext context) {
        this.context = context;
    }

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
    }

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

    public void runInteractive(Scanner scanner) {
        System.out.println("=== Интерактивный режим ===");
        System.out.println("Вводите выражения построчно.");
        System.out.println("Доступные переменные: " + context.getVariableNames());
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

    private void evaluateLine(String source) {
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
}
