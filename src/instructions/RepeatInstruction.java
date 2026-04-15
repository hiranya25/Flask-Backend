package instructions;

import ast.Expression;
import environment.Environment;
import java.util.List;

/**
 * RepeatInstruction — Handles fixed-count loops.
 *
 * SPEEK syntax:
 *   repeat 3 times
 *     say "hello"
 *
 *   repeat 4 times
 *     say i
 *     let i be i + 1
 *
 * HOW IT WORKS:
 *   1. Evaluate the count expression to get the number of iterations
 *   2. Execute all body instructions, repeated that many times
 *
 * The count must evaluate to a number. Each iteration re-executes
 * all instructions in the body, so variables modified in the body
 * (like a counter) carry over to the next iteration.
 */
public class RepeatInstruction implements Instruction {

    private final Expression<?> countExpression; // how many times to repeat
    private final List<Instruction> body;     // instructions to repeat

    /**
     * Create a RepeatInstruction.
     *
     * @param countExpression  expression for the number of repetitions
     * @param body             the list of instructions to run each iteration
     */
    public RepeatInstruction(Expression<?> countExpression, List<Instruction> body) {
        this.countExpression = countExpression;
        this.body = body;
    }

    /**
     * Evaluate the count and run the body that many times.
     */
    @Override
    public void execute(Environment<?> env) {
        // Evaluate how many times to loop
        Object countValue = countExpression.evaluate(env);

        if (!(countValue instanceof Double)) {
            throw new RuntimeException(
                "[Runtime] 'repeat' count must be a number, got: " + countValue);
        }

        int count = (int) Math.round((Double) countValue);

        // Run the body 'count' times
        for (int i = 0; i < count; i++) {
            for (Instruction instruction : body) {
                instruction.execute(env);
            }
        }
    }

    @Override
    public String toString() {
        return "RepeatInstruction(count=" + countExpression + ", body=" + body + ")";
    }
}
