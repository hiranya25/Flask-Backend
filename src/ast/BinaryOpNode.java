package ast;

import environment.Environment;

/**
 * BinaryOpNode — An internal tree node representing an operation between two expressions.
 *
 * This is the most important AST node. It handles:
 *   Arithmetic : + - * /
 *   Comparison : > < ==
 *
 * WHY A TREE?
 *   For  x + y * 2  the tree looks like:
 *
 *          BinaryOpNode("+")
 *         /                \
 *   VariableNode("x")   BinaryOpNode("*")
 *                        /             \
 *               VariableNode("y")   NumberNode(2)
 *
 *   The * node is DEEPER, so it evaluates FIRST — giving us correct
 *   operator precedence without any extra logic. The tree shape does the work.
 *
 * EVALUATION RULE:
 *   1. Evaluate the left child  → get a value
 *   2. Evaluate the right child → get a value
 *   3. Apply the operator       → return the result
 *
 *   Arithmetic operators return a Double.
 *   Comparison operators (> < ==) return a Boolean.
 */
public class BinaryOpNode implements Expression<Object> {

    private final Expression<?> left;   // left-hand side expression
    private final String operator;   // the operator: "+", "-", "*", "/", ">", "<", "=="
    private final Expression<?> right;  // right-hand side expression

    /**
     * Create a BinaryOpNode.
     *
     * @param left     left-hand expression
     * @param operator the operator symbol as a string
     * @param right    right-hand expression
     */
    public BinaryOpNode(Expression<?> left, String operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Evaluate both sides and apply the operator.
     *
     * @param env the current variable store (passed through to child nodes)
     * @return    a Double (arithmetic) or Boolean (comparison)
     */
    @Override
    public Object evaluate(Environment<?> env) {
        // Step 1 & 2: evaluate both sides of the expression
        Object leftValue  = left.evaluate(env);
        Object rightValue = right.evaluate(env);

        // Step 3: apply the operator
        switch (operator) {

            // ── Arithmetic — both sides must be numbers ───────────────────
            case "+":
                return toDouble(leftValue) + toDouble(rightValue);
            case "-":
                return toDouble(leftValue) - toDouble(rightValue);
            case "*":
                return toDouble(leftValue) * toDouble(rightValue);
            case "/":
                double divisor = toDouble(rightValue);
                if (divisor == 0) {
                    throw new RuntimeException("[Runtime] Division by zero.");
                }
                return toDouble(leftValue) / divisor;

            // ── Comparison — returns Boolean ──────────────────────────────
            case ">":
                return toDouble(leftValue) > toDouble(rightValue);
            case "<":
                return toDouble(leftValue) < toDouble(rightValue);
            case "==":
                // Works for both numbers and strings
                return leftValue.equals(rightValue);

            default:
                throw new RuntimeException(
                    "[Runtime] Unknown operator: '" + operator + "'");
        }
    }

    /**
     * Convert a value to double, giving a clear error if it is not numeric.
     */
    private double toDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        }
        throw new RuntimeException(
            "[Runtime] Expected a number but got: \"" + value + "\"");
    }

    @Override
    public String toString() {
        return "BinaryOpNode(" + left + " " + operator + " " + right + ")";
    }
}
