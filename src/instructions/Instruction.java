package instructions;

import environment.Environment;

/**
 * Instruction — Interface for every executable statement in a SPEEK program.
 *
 * While an Expression PRODUCES a value, an Instruction DOES something:
 *   AssignInstruction  → stores a value in the Environment
 *   PrintInstruction   → prints a value to the screen
 *   IfInstruction      → conditionally executes a block of instructions
 *   RepeatInstruction  → runs a block of instructions N times
 *
 * The Parser builds a List<Instruction> from the token stream.
 * The Interpreter then calls execute() on each instruction in order.
 *
 * Each instruction receives the shared Environment so it can read
 * and update variables as needed.
 */
public interface Instruction {

    /**
     * Execute this instruction.
     *
     * @param env  the shared variable store for the running program
     */
    void execute(Environment<?> env);
}
