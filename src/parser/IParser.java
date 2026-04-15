package parser;

import instructions.Instruction;
import java.util.List;

/**
 * IParser — Interface for converting tokens into a list of executable instructions.
 */
public interface IParser {

    /**
     * Parse all tokens and return the list of top-level Instructions.
     *
     * @return the parsed program as a list of instructions
     */
    List<Instruction> parse();
}
