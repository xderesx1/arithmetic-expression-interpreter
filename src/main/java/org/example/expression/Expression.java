package org.example.expression;

public sealed interface Expression permits Binary, CallFunction, Number, Unary, Variable {
}
