package instructions;

import ast.Expression;
import environment.Environment;
import java.util.List;

/**
 * IfInstruction — Handles conditional execution.
 *
 * SPEEK syntax:
 *   if score is greater than 50 then
 *     say "Pass"
 *
 * HOW IT WORKS:
 *   1. Evaluate the condition expression
 *   2. If the result is Boolean true → execute all body instructions
 *   3. If the result is Boolean false → skip the body (do nothing)
 *
 * The condition is always a comparison expression (e.g. score > 50)
 * which evaluates to a Boolean. The BinaryOpNode handles the comparison.
 */
public class IfInstruction implements Instruction {

    private final Expression<?> condition;      // the condition to test (e.g. score > 50)
    private final List<Instruction> body;       // instructions to run if condition is true
    private final List<Instruction> elseBody;   // instructions to run if condition is false (optional)

    /**
     * Create an IfInstruction without an else block.
     */
    public IfInstruction(Expression<?> condition, List<Instruction> body) {
        this(condition, body, null);
    }

    /**
     * Create an IfInstruction with an optional else block.
     *
     * @param condition  the condition expression (must evaluate to Boolean)
     * @param body       the list of instructions to execute when condition is true
     * @param elseBody   the list of instructions to execute when condition is false (can be null)
     */
    public IfInstruction(Expression<?> condition, List<Instruction> body, List<Instruction> elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    /**
     * Evaluate the condition and execute the appropriate body.
     */
    @Override
    public void execute(Environment<?> env) {
        // Evaluate the condition — expected to return a Boolean
        Object result = condition.evaluate(env);

        if (!(result instanceof Boolean)) {
            throw new RuntimeException(
                "[Runtime] 'if' condition did not produce true/false: " + result);
        }

        // Run the body if true, otherwise run the elseBody if it exists
        if ((Boolean) result) {
            for (Instruction instruction : body) {
                instruction.execute(env);
            }
        } else if (elseBody != null) {
            for (Instruction instruction : elseBody) {
                instruction.execute(env);
            }
        }
    }

    @Override
    public String toString() {
        return "IfInstruction(condition=" + condition + ", body=" + body + ", elseBody=" + elseBody + ")";
    }
}
