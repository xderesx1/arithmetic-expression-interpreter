package org.example.transform;

import org.example.expression.Expression;
import org.example.parser.Parser;
import org.example.service.EvaluationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrettyPrinterTest {

    private final PrettyPrinter printer = new PrettyPrinter();

    private Expression parse(String source) {
        return new Parser(source).parse();
    }

    // ===== Round-trip: parse -> print -> parse даёт то же дерево =====

    @ParameterizedTest
    @ValueSource(strings = {
            "42",
            "x",
            "2 + 3 * 4",
            "(2 + 3) * 4",
            "a - b - c",
            "a - (b - c)",
            "a + (b + c)",
            "10 / (2 / 5)",
            "1 / 2 / 3",
            "2 ^ 3 ^ 2",
            "(2 ^ 3) ^ 2",
            "-2 ^ 2",
            "(-2) ^ 2",
            "2 ^ -3",
            "-(2 + 3)",
            "2 * -3",
            "--x",
            "+x",
            "min(x + 1, 2) * abs(-y)",
            "max(abs(x), 0) ^ 2"
    })
    void roundTripPreservesTree(String source) {
        Expression first = parse(source);
        String printed = printer.print(first);
        Expression second = parse(printed);

        assertEquals(first, second, "round-trip сломался на: " + source);
    }

    // ===== Печать стабильна: print(parse(print(t))) == print(t) =====

    @ParameterizedTest
    @ValueSource(strings = {
            "2 + 3 * 4",
            "(2 + 3) * 4",
            "-2 ^ 2",
            "a - (b - c)"
    })
    void printingIsStable(String source) {
        String once = printer.print(parse(source));
        String twice = printer.print(parse(once));

        assertEquals(once, twice);
    }

    // ===== Минимальность скобок: точные ожидания =====

    @Test
    void printsWithoutRedundantParentheses() {
        assertEquals("2 + 3 * 4", printer.print(parse("2 + (3 * 4)")));
        assertEquals("a - b - c", printer.print(parse("(a - b) - c")));
        assertEquals("2 ^ 3 ^ 2", printer.print(parse("2 ^ (3 ^ 2)")));
        assertEquals("-2 ^ 2", printer.print(parse("-(2 ^ 2)")));
        assertEquals("2 * -3", printer.print(parse("2 * (-3)")));
        assertEquals("--x", printer.print(parse("-(-x)")));
    }

    @Test
    void printsNecessaryParentheses() {
        assertEquals("(2 + 3) * 4", printer.print(parse("(2 + 3) * 4")));
        assertEquals("a - (b - c)", printer.print(parse("a - (b - c)")));
        assertEquals("(2 ^ 3) ^ 2", printer.print(parse("(2 ^ 3) ^ 2")));
        assertEquals("(-2) ^ 2", printer.print(parse("(-2) ^ 2")));
        assertEquals("-(2 + 3)", printer.print(parse("-(2 + 3)")));
        assertEquals("10 / (2 - 3)", printer.print(parse("10 / (2 - 3)")));
    }

    // ===== Форматирование чисел =====

    @Test
    void formatsWholeNumbersWithoutFraction() {
        assertEquals("2 + 3", printer.print(parse("2.0 + 3.0")));
    }

    // ===== Совместная работа с упрощением =====

    @Test
    void printsSimplifiedExpression() {
        Expression simplified = new Simplifier().simplify(parse("2 * 3 + x * 1"));
        assertEquals("6 + x", printer.print(simplified));
    }

    @Test
    void simplifiedTreeKeepsValueAfterReprint() {
        var context = new EvaluationContext(Map.of("x", 3.0));
        Expression simplified = new Simplifier().simplify(parse("2 - 5 + x"));
        String printed = printer.print(simplified);

        assertEquals(parse("2 - 5 + x").evaluate(context),
                parse(printed).evaluate(context), 1e-9);
    }
}