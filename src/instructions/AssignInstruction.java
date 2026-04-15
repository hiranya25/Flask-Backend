package instructions;

import ast.Expression;
import environment.Environment;

/**
 * AssignInstruction — Handles variable assignment.
 *
 * SPEEK syntax:
 *   let x be 10
 *   let result be x + y * 2
 *
 * HOW IT WORKS:
 *   1. Evaluate the expression on the right side of "be"
 *   2. Store the result in the Environment under the given variable name
 *
 * If the variable already exists its old value is overwritten.
 * If it does not exist yet it is created.
 */
public class AssignInstruction implements Instruction {

    private final String variableName; // the name to assign to (e.g. "x")
    private final Expression<?> expression; // the expression to evaluate (e.g. x + y * 2)

    /**
     * Create an AssignInstruction.
     *
     * @param variableName  the name of the variable being assigned
     * @param expression    the expression whose value will be stored
     */
    public AssignInstruction(String variableName, Expression<?> expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    /**
     * Evaluate the expression and store the result in the Environment.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(Environment<?> env) {
        Object value = expression.evaluate(env); // evaluate right-hand side
        ((Environment<Object>) env).set(variableName, value);            // store in the variable store
    }

    @Override
    public String toString() {
        return "AssignInstruction(" + variableName + " = " + expression + ")";
    }
}
