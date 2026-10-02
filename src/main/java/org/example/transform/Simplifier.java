package org.example.transform;

import org.example.expression.Binary;
import org.example.expression.Expression;
import org.example.expression.FunctionCall;
import org.example.expression.Number;
import org.example.expression.Unary;
import org.example.expression.Variable;
import org.example.operation.BinaryOperation;
import org.example.operation.UnaryOperation;

import java.util.List;
import java.util.Optional;

/**
 * Упрощает арифметические выражения: сворачивает константы,
 * удаляет нейтральные элементы, убирает лишние операции.
 *
 * <p>Все методы возвращают новое дерево; исходное не модифицируется.</p>
 */
public final class Simplifier {

    /**
     * Упрощает выражение.
     *
     * @param expression исходное выражение, не null
     * @return упрощённое выражение
     */
    public Expression simplify(Expression expression) {
        return switch (expression) {
            case Number n -> n;
            case Variable v -> v;
            case Unary u -> simplifyUnary(u);
            case Binary b -> simplifyBinary(b);
            case FunctionCall f -> simplifyFunction(f);
        };
    }


    private Expression simplifyUnary(Unary unary) {
        Expression operand = simplify(unary.operand());
        UnaryOperation op = unary.operation();

        if (op == UnaryOperation.POSITIVE) {
            return operand;
        }

        Optional<Double> numValue = asNumber(operand);
        if (numValue.isPresent()) {
            return new Number(-numValue.get());
        }

        if (operand instanceof Unary inner
                && inner.operation() == UnaryOperation.NEGATIVE) {
            return inner.operand();
        }

        return new Unary(op, operand);
    }


    private Expression simplifyBinary(Binary binary) {
        Expression left = simplify(binary.left());
        Expression right = simplify(binary.right());
        BinaryOperation op = binary.operation();

        return switch (op) {
            case PLUS -> simplifyPlus(left, right);
            case MINUS -> simplifyMinus(left, right);
            case MULTIPLY -> simplifyMultiply(left, right);
            case DIVIDE -> simplifyDivide(left, right);
            case POWER -> simplifyPower(left, right);
        };
    }

    // --- Сложение ---
    private Expression simplifyPlus(Expression left, Expression right) {
        if (isZero(left))  return right;
        if (isZero(right)) return left;

        if (isNumber(left) && isNumber(right)) {
            return foldIfBothNumbers(left, right, BinaryOperation.PLUS);
        }

        // a + (b + x) → (a+b) + x
        if (left instanceof Number a
                && right instanceof Binary rb
                && rb.operation() == BinaryOperation.PLUS
                && rb.left() instanceof Number b) {
            return simplify(new Binary(
                    new Number(a.value() + b.value()),
                    BinaryOperation.PLUS,
                    rb.right()));
        }

        // (b + x) + a → (a+b) + x
        if (right instanceof Number a
                && left instanceof Binary lb
                && lb.operation() == BinaryOperation.PLUS
                && lb.left() instanceof Number b) {
            return simplify(new Binary(
                    new Number(a.value() + b.value()),
                    BinaryOperation.PLUS,
                    lb.right()));
        }

        return new Binary(left, BinaryOperation.PLUS, right);
    }

    private Expression simplifyMinus(Expression left, Expression right) {
        // x - 0 → x
        if (isZero(right)) return left;

        // 0 - x → -x
        if (isZero(left)) return new Unary(UnaryOperation.NEGATIVE, right);

        // Оба числа → вычислить
        if (isNumber(left) && isNumber(right)) {
            return foldIfBothNumbers(left, right, BinaryOperation.MINUS);
        }

        // a - (b + x) → (a-b) - x, когда a и b — константы
        if (left instanceof Number a
                && right instanceof Binary rb
                && rb.operation() == BinaryOperation.PLUS
                && rb.left() instanceof Number b) {
            return simplify(new Binary(
                    new Number(a.value() - b.value()),
                    BinaryOperation.MINUS,
                    rb.right()));
        }

        return new Binary(left, BinaryOperation.MINUS, right);
    }

    private Expression simplifyMultiply(Expression left, Expression right) {
        // Поглощающий ноль
        if (isZero(left) || isZero(right)) return new Number(0);
        // Нейтральная единица
        if (isOne(left))  return right;
        if (isOne(right)) return left;

        // Константа * Константа
        if (isNumber(left) && isNumber(right)) {
            return foldIfBothNumbers(left, right, BinaryOperation.MULTIPLY);
        }

        // a * (b * x) → (a*b) * x, когда a и b — константы
        if (left instanceof Number a
                && right instanceof Binary rb
                && rb.operation() == BinaryOperation.MULTIPLY
                && rb.left() instanceof Number b) {
            return simplify(new Binary(
                    new Number(a.value() * b.value()),
                    BinaryOperation.MULTIPLY,
                    rb.right()));
        }

        // (b * x) * a → (a*b) * x, симметричный случай
        if (right instanceof Number a
                && left instanceof Binary lb
                && lb.operation() == BinaryOperation.MULTIPLY
                && lb.left() instanceof Number b) {
            return simplify(new Binary(
                    new Number(a.value() * b.value()),
                    BinaryOperation.MULTIPLY,
                    lb.right()));
        }

        return new Binary(left, BinaryOperation.MULTIPLY, right);
    }

    private Expression simplifyDivide(Expression left, Expression right) {
        if (isZero(left) && !isZero(right)) return new Number(0);

        if (isOne(right)) return left;


        if (isNumber(left) && isNumber(right) && !isZero(right)) {
            return foldIfBothNumbers(left, right, BinaryOperation.DIVIDE);
        }

        return new Binary(left, BinaryOperation.DIVIDE, right);
    }

    private Expression simplifyPower(Expression left, Expression right) {
        if (isOne(right)) return left;

        if (isZero(right)) return new Number(1);

        return foldIfBothNumbers(left, right, BinaryOperation.POWER);
    }


    private Expression simplifyFunction(FunctionCall call) {
        List<Expression> simplifiedArgs = call.arguments().stream()
                .map(this::simplify)
                .toList();

        if (simplifiedArgs.stream().allMatch(this::isNumber)) {
            double[] values = simplifiedArgs.stream()
                    .mapToDouble(a -> asNumber(a).orElseThrow())
                    .toArray();
            double result = call.operation().calculate(values);
            return new Number(result);
        }

        return new FunctionCall(call.operation(), simplifiedArgs);
    }


    /** Проверяет, является ли выражение числом со значением 0. */
    private boolean isZero(Expression e) {
        return asNumber(e).filter(v -> v == 0.0).isPresent();
    }

    /** Проверяет, является ли выражение числом со значением 1. */
    private boolean isOne(Expression e) {
        return asNumber(e).filter(v -> v == 1.0).isPresent();
    }

    /** Проверяет, является ли выражение числовой константой. */
    private boolean isNumber(Expression e) {
        return e instanceof Number;
    }

    /** Если выражение — число, возвращает его значение в Optional. */
    private java.util.Optional<Double> asNumber(Expression e) {
        if (e instanceof Number n) {
            return java.util.Optional.of(n.value());
        }
        return java.util.Optional.empty();
    }

    /**
     * Если оба операнда — числа, вычисляет операцию и возвращает Number.
     * Иначе возвращает новый Binary с упрощёнными операндами.
     */
    private Expression foldIfBothNumbers(Expression left, Expression right, BinaryOperation op) {
        if (isNumber(left) && isNumber(right)) {
            double l = asNumber(left).orElseThrow();
            double r = asNumber(right).orElseThrow();
            return new Number(op.calculate(l, r));
        }
        return new Binary(left, op, right);
    }
}