package org.example.expression;

import org.example.operation.UnaryOperation;
import org.example.service.EvaluationContext;

import java.util.Objects;


public record Unary(
        UnaryOperation operation,
        Expression operand
        ) implements Expression {
    public Unary{
        Objects.requireNonNull(operation, "Operation must not be null");
        Objects.requireNonNull(operand, "Operand must not be null");
    }
    @Override
    public double evaluate(EvaluationContext context) {
        Objects.requireNonNull(context, "EvaluationContext must not be null");
        double value = operand.evaluate(context);
        return operation.calculate(value);
    }
}
