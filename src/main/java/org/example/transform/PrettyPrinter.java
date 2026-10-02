package org.example.transform;

import org.example.expression.Binary;
import org.example.expression.Expression;
import org.example.expression.FunctionCall;
import org.example.expression.Number;
import org.example.expression.Unary;
import org.example.expression.Variable;
import org.example.operation.Associativity;
import org.example.operation.BinaryOperation;
import org.example.operation.UnaryOperation;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Печатает дерево выражения обратно в строку с минимально необходимым
 * числом скобок. Гарантируется, что повторный разбор напечатанной строки
 * даёт то же самое дерево (для деревьев, порождённых парсером).
 */
public final class PrettyPrinter {

    /** Приоритет унарных операций: выше умножения, но ниже степени. */
    private static final int UNARY_PRECEDENCE = 3;

    /**
     * Печатает выражение в строку.
     *
     * @param expression выражение, не null
     * @return строковое представление с минимальным числом скобок
     */
    public String print(Expression expression) {
        return switch (expression) {
            case Number n -> formatNumber(n.value());
            case Variable v -> v.name();
            case FunctionCall f -> printFunction(f);
            case Unary u -> printUnary(u);
            case Binary b -> printBinary(b);
        };
    }


    private String printBinary(Binary binary) {
        String left = printChildInBinary(binary.left(), binary.operation(), false);
        String right = printChildInBinary(binary.right(), binary.operation(), true);

        return left + " " + binary.operation().getSymbol() + " " + right;
    }

    private String printUnary(Unary unary) {
        String symbol = switch (unary.operation()) {
            case NEGATIVE -> "-";
            case POSITIVE -> "+";
        };

        String operand = print(unary.operand());

        if (needsParensInUnary(unary.operand())) {
            operand = "(" + operand + ")";
        }

        return symbol + operand;
    }

    private String printFunction(FunctionCall call) {
        String args = call.arguments().stream()
                .map(this::print)
                .collect(Collectors.joining(", "));

        return call.operation().getName() + "(" + args + ")";
    }


    private String printChildInBinary(Expression child, BinaryOperation parent, boolean rightSide) {
        String text = print(child);

        if (needsParensInBinary(child, parent, rightSide)) {
            return "(" + text + ")";
        }

        return text;
    }

    private boolean needsParensInBinary(Expression child, BinaryOperation parent, boolean rightSide) {
        if (child instanceof Number || child instanceof Variable || child instanceof FunctionCall) {
            return false;
        }

        if (child instanceof Unary) {
            return parent == BinaryOperation.POWER && !rightSide;
        }

        if (child instanceof Binary b) {
            int childPrec = precedenceOf(b.operation());
            int parentPrec = precedenceOf(parent);

            if (childPrec > parentPrec) {
                return false;
            }

            if (childPrec < parentPrec) {
                return true;
            }

            Associativity assoc = parent.getAssociativity();

            return rightSide
                    ? assoc == Associativity.LEFT
                    : assoc == Associativity.RIGHT;
        }

        throw new IllegalStateException("Неизвестный тип выражения: " + child);
    }

    private boolean needsParensInUnary(Expression child) {
        // -(2 + 3) и -(2 * 3) — со скобками;
        // -5, -x, --x, -2 ^ 3 — без скобок
        return child instanceof Binary b
                && precedenceOf(b.operation()) < UNARY_PRECEDENCE;
    }


    private int precedenceOf(BinaryOperation operation) {
        return switch (operation) {
            case PLUS, MINUS -> 1;
            case MULTIPLY, DIVIDE -> 2;
            case POWER -> 4; // уровень 3 зарезервирован под унарные операции
        };
    }

    private String formatNumber(double value) {
        if (value == 0.0) {
            return Double.doubleToRawLongBits(value) < 0 ? "-0.0" : "0";
        }

        if (!Double.isFinite(value)) {
            return Double.toString(value);
        }

        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return Long.toString((long) value);
        }

        String text = Double.toString(value);

        if (text.indexOf('E') >= 0 || text.indexOf('e') >= 0) {
            return new BigDecimal(value).toPlainString();
        }

        return text;
    }
}