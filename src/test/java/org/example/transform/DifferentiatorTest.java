package org.example.transform;

import org.example.expression.*;
import org.example.expression.Number;
import org.example.operation.BinaryOperation;
import org.example.operation.FunctionOperation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DifferentiatorTest {
    private final Differentiator dl = new Differentiator();

    @Test
    void differentiatesPower() {
        Expression expr = new Binary(new Number(3), BinaryOperation.MULTIPLY,
                new Binary(new Variable("x"), BinaryOperation.POWER, new Number(2)));

        Expression expected = new Binary(new Number(6), BinaryOperation.MULTIPLY,
                new Variable("x"));

        assertEquals(expected,  dl.differentiate(expr, "x"));
    }

    @Test
    void differentiatesConstantToZero() {
        assertEquals(new Number(0), dl.differentiate(new Number(42), "x"));
    }

    @Test
    void functionsAreNotDifferentiable() {
        Expression expr = new FunctionCall(FunctionOperation.ABS, List.of(new Variable("x")));

        assertThrows(UnsupportedOperationException.class,
                () -> dl.differentiate(expr, "x"));
    }
}
