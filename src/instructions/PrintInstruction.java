package instructions;

import ast.Expression;
import environment.Environment;

/**
 * PrintInstruction — Handles output to the screen.
 *
 * SPEEK syntax:
 *   say x
 *   say "Hello from SPEEK"
 *   say result
 *
 * HOW IT WORKS:
 *   1. Evaluate the expression (could be a variable, number, or string literal)
 *   2. Print the result to standard output
 *
 * For numbers, if the result is a whole number (e.g. 16.0),
 * we print it without the decimal point (prints "16" not "16.0").
 */
public class PrintInstruction implements Instruction {

    private final Expression<?> expression; // the expression to print

    /**
     * Create a PrintInstruction.
     *
     * @param expression the expression whose value will be printed
     */
    public PrintInstruction(Expression<?> expression) {
        this.expression = expression;
    }

    /**
     * Evaluate the expression and print the result to standard output.
     */
    @Override
    public void execute(Environment<?> env) {
        Object value = expression.evaluate(env);

        // If the value is a whole number, print it as an integer (no ".0")
        if (value instanceof Double) {
            double d = (Double) value;
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                System.out.println((long) d); // e.g. prints "16" not "16.0"
                return;
            }
        }

        // For strings, booleans, and decimal numbers: print as-is
        System.out.println(value);
    }

    @Override
    public String toString() {
        return "PrintInstruction(" + expression + ")";
    }
}
