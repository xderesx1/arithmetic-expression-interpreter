package org.example.expression;

import org.example.operation.BinaryOperation;
import org.example.service.EvaluationContext;

import java.util.Objects;

public record Binary(
        Expression left,
        BinaryOperation operation,
        Expression right
)implements Expression{
    public Binary {
        Objects.requireNonNull(left, "Left expression must not be null");
        Objects.requireNonNull(right, "Right expression must not be null");
        Objects.requireNonNull(operation, "Operation must not be null");
    }
    @Override
    public double evaluate(EvaluationContext context) {
        Objects.requireNonNull(context, "EvaluationContext must not be null");
        double leftValue = left.evaluate(context);
        double rightValue = right.evaluate(context);

        return operation.calculate(leftValue, rightValue);
    }
}
