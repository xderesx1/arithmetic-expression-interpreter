package org.example.expression;

import org.example.service.EvaluationContext;

public sealed interface Expression
        permits Binary, FunctionCall, Number, Unary, Variable {
    double evaluate(EvaluationContext context);
}
