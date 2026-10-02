package org.example.transform;

import org.example.expression.Binary;
import org.example.expression.Expression;
import org.example.expression.FunctionCall;
import org.example.expression.Number;
import org.example.expression.Unary;
import org.example.expression.Variable;
import org.example.operation.BinaryOperation;
import org.example.operation.UnaryOperation;

/**
 * Символьное дифференцирование выражений по переменной.
 * Результат автоматически упрощается через {@link Simplifier}.
 */
public final class Differentiator {

    private final Simplifier simplifier = new Simplifier();

    /**
     * Возвращает упрощённую производную выражения по переменной.
     *
     * @param expression выражение, не null
     * @param variable   имя переменной дифференцирования
     * @return производная в упрощённом виде
     */
    public Expression differentiate(Expression expression, String variable) {
        Expression raw = rawDerivative(expression, variable);
        return simplifier.simplify(raw);
    }

    private Expression rawDerivative(Expression expression, String variable) {
        return switch (expression) {
            case Number n -> new Number(0);
            case Variable v -> new Number(v.name().equals(variable) ? 1.0 : 0.0);
            case Unary u -> differentiateUnary(u, variable);
            case Binary b -> differentiateBinary(b, variable);
            case FunctionCall f -> throw new UnsupportedOperationException(
                    "Differentiation of min/max/abs functions is not supported");
        };
    }

    private Expression differentiateUnary(Unary unary, String variable) {
        Expression inner = rawDerivative(unary.operand(), variable);

        return unary.operation() == UnaryOperation.POSITIVE
                ? inner
                : new Unary(UnaryOperation.NEGATIVE, inner);
    }

    private Expression differentiateBinary(Binary binary, String variable) {
        Expression left = binary.left();
        Expression right = binary.right();
        Expression dl = rawDerivative(left, variable);
        Expression dr = rawDerivative(right, variable);

        return switch (binary.operation()) {
            // (a + b)' = a' + b'
            case PLUS -> new Binary(dl, BinaryOperation.PLUS, dr);

            // (a - b)' = a' - b'
            case MINUS -> new Binary(dl, BinaryOperation.MINUS, dr);

            // (a * b)' = a' * b + a * b'
            case MULTIPLY -> new Binary(
                    new Binary(dl, BinaryOperation.MULTIPLY, right),
                    BinaryOperation.PLUS,
                    new Binary(left, BinaryOperation.MULTIPLY, dr));

            // (a / b)' = (a' * b - a * b') / b^2
            case DIVIDE -> new Binary(
                    new Binary(
                            new Binary(dl, BinaryOperation.MULTIPLY, right),
                            BinaryOperation.MINUS,
                            new Binary(left, BinaryOperation.MULTIPLY, dr)),
                    BinaryOperation.DIVIDE,
                    new Binary(right, BinaryOperation.POWER, new Number(2)));

            // (a^n)' = n * a^(n-1) * a'  (n — константа)
            case POWER -> differentiatePower(left, right, dl);
        };
    }

    private Expression differentiatePower(Expression base, Expression exponent,
                                          Expression baseDerivative) {
        if (exponent instanceof Number n) {
            return new Binary(
                    new Binary(
                            new Number(n.value()),
                            BinaryOperation.MULTIPLY,
                            new Binary(base, BinaryOperation.POWER, new Number(n.value() - 1))),
                    BinaryOperation.MULTIPLY,
                    baseDerivative);
        }

        throw new UnsupportedOperationException(
                "Differentiation of a power with a variable exponent is not supported");
    }
}