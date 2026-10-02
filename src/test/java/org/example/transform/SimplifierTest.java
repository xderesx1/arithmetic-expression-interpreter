package org.example.transform;

import org.example.expression.*;
import org.example.expression.Number;
import org.example.operation.BinaryOperation;
import org.example.operation.FunctionOperation;
import org.example.operation.UnaryOperation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

class SimplifierTest {

    @Test
    void simplifyConstantFolding() {
        // 2 * 3 → 6
        Expression expr = new Binary(new Number(2), BinaryOperation.MULTIPLY, new Number(3));
        assertEquals(new Number(6), new Simplifier().simplify(expr));
    }

    @Test
    void simplifyAddZero() {
        // x + 0 → x
        Expression expr = new Binary(new Variable("x"), BinaryOperation.PLUS, new Number(0));
        assertEquals(new Variable("x"), new Simplifier().simplify(expr));
    }

    @Test
    void simplifyMultiplyByOne() {
        // x * 1 → x
        Expression expr = new Binary(new Variable("x"), BinaryOperation.MULTIPLY, new Number(1));
        assertEquals(new Variable("x"), new Simplifier().simplify(expr));
    }

    @Test
    void simplifyMultiplyByZero() {
        // x * 0 → 0
        Expression expr = new Binary(new Variable("x"), BinaryOperation.MULTIPLY, new Number(0));
        assertEquals(new Number(0), new Simplifier().simplify(expr));
    }

    @Test
    void simplifyPowerZero() {
        // x ^ 0 → 1
        Expression expr = new Binary(new Variable("x"), BinaryOperation.POWER, new Number(0));
        assertEquals(new Number(1), new Simplifier().simplify(expr));
    }

    @Test
    void simplifyDoubleNegation() {
        // -(-x) → x
        Expression expr = new Unary(UnaryOperation.NEGATIVE,
                new Unary(UnaryOperation.NEGATIVE, new Variable("x")));
        assertEquals(new Variable("x"), new Simplifier().simplify(expr));
    }

    @Test
    void simplifyUnaryPlus() {
        // +x → x
        Expression expr = new Unary(UnaryOperation.POSITIVE, new Variable("x"));
        assertEquals(new Variable("x"), new Simplifier().simplify(expr));
    }

    @Test
    void simplifyAbsOfConstant() {
        // abs(-5) → 5
        Expression expr = new FunctionCall(FunctionOperation.ABS, List.of(new Number(-5)));
        assertEquals(new Number(5), new Simplifier().simplify(expr));
    }
}
