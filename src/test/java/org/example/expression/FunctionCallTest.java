package org.example.expression;

import org.example.operation.FunctionOperation;
import org.example.service.EvaluationContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FunctionCallTest {

    private final EvaluationContext emptyContext = new EvaluationContext(Map.of());

    private final EvaluationContext contextWithX5 = new EvaluationContext(
            Map.of("x", 5.0)
    );
    private final EvaluationContext contextWithXMinus5 = new EvaluationContext(
            Map.of("x", -5.0)
    );

    @Test
    void absReturnsAbsoluteValueForNegativeNumber() {
        Expression expression = new FunctionCall(
                FunctionOperation.ABS,
                List.of(new Number(-5))
        );

        double result = expression.evaluate(emptyContext);

        assertEquals(5.0, result, 1e-9);
    }

    @Test
    void absReturnsSameValueForPositiveNumber() {
        Expression expression = new FunctionCall(
                FunctionOperation.ABS,
                List.of(new Number(7))
        );

        double result = expression.evaluate(emptyContext);

        assertEquals(7.0, result, 1e-9);
    }

    @Test
    void absReturnsZeroForZero() {
        Expression expression = new FunctionCall(
                FunctionOperation.ABS,
                List.of(new Number(0))
        );

        double result = expression.evaluate(emptyContext);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void minReturnsSmallerValue() {
        Expression expression = new FunctionCall(
                FunctionOperation.MIN,
                List.of(new Number(2), new Number(3))
        );

        double result = expression.evaluate(emptyContext);

        assertEquals(2.0, result, 1e-9);
    }

    @Test
    void maxReturnsGreaterValue() {
        Expression expression = new FunctionCall(
                FunctionOperation.MAX,
                List.of(new Number(2), new Number(3))
        );

        double result = expression.evaluate(emptyContext);

        assertEquals(3.0, result, 1e-9);
    }

    @Test
    void constructorThrowsWhenOperationIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new FunctionCall(
                        null,
                        List.of(new Number(1))
                )
        );
    }

    @Test
    void constructorThrowsWhenArgumentsListIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new FunctionCall(
                        FunctionOperation.ABS,
                        null
                )
        );
    }

    @Test
    void constructorThrowsWhenArgumentIsNull() {
        List<Expression> arguments = new ArrayList<>();
        arguments.add(new Number(1));
        arguments.add(null);

        assertThrows(
                NullPointerException.class,
                () -> new FunctionCall(
                        FunctionOperation.MIN,
                        arguments
                )
        );
    }

    @Test
    void constructorThrowsWhenAbsGetsTwoArguments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FunctionCall(
                        FunctionOperation.ABS,
                        List.of(new Number(1), new Number(2))
                )
        );
    }

    @Test
    void constructorThrowsWhenAbsGetsZeroArguments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FunctionCall(
                        FunctionOperation.ABS,
                        List.of()
                )
        );
    }

    @Test
    void constructorThrowsWhenMinGetsOneArgument() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FunctionCall(
                        FunctionOperation.MIN,
                        List.of(new Number(1))
                )
        );
    }

    @Test
    void constructorThrowsWhenMinGetsThreeArguments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FunctionCall(
                        FunctionOperation.MIN,
                        List.of(new Number(1), new Number(2), new Number(3))
                )
        );
    }

    @Test
    void constructorCopiesArgumentsList() {
        List<Expression> arguments = new ArrayList<>();
        arguments.add(new Number(5));

        FunctionCall call = new FunctionCall(
                FunctionOperation.ABS,
                arguments
        );

        arguments.add(new Number(10));

        assertEquals(1, call.arguments().size());
    }

    @Test
    void argumentsListCannotBeModified() {
        FunctionCall call = new FunctionCall(
                FunctionOperation.ABS,
                List.of(new Number(-5))
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> call.arguments().add(new Number(1))
        );
    }

    @Test
    void absWorksWithVariable() {
        Expression expression = new FunctionCall(
                FunctionOperation.ABS,
                List.of(new Variable("x"))
        );

        double result = expression.evaluate(contextWithXMinus5);

        assertEquals(5.0, result, 1e-9);
    }
}
