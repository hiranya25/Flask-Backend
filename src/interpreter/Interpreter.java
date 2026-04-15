package interpreter;

import environment.Environment;
import instructions.Instruction;
import parser.IParser;
import parser.Parser;
import tokenizer.ITokenizer;
import tokenizer.Tokenizer;

import java.util.List;

/**
 * Interpreter — The top-level orchestrator that connects the pipeline steps.
 */
public class Interpreter {

    private final ITokenizer tokenizer;
    private final IParser parser;

    /**
     * Default constructor for convenience (Step 5: Dependency Inversion).
     */
    public Interpreter() {
        this.tokenizer = null; // Will be initialized in run() if not injected
        this.parser = null;
    }

    /**
     * Constructor for Dependency Injection.
     *
     * @param tokenizer the tokenizer to use
     * @param parser    the parser to use
     */
    public Interpreter(ITokenizer tokenizer, IParser parser) {
        this.tokenizer = tokenizer;
        this.parser = parser;
    }

    /**
     * Run a SPEEK program given as a source code string.
     *
     * @param sourceCode the full text of the SPEEK program
     */
    public void run(String sourceCode) {
        try {
            // Use injected components or create defaults
            ITokenizer activeTokenizer = (this.tokenizer != null) ? this.tokenizer : new Tokenizer(sourceCode);
            List<tokenizer.Token> tokens = activeTokenizer.tokenize();

            IParser activeParser = (this.parser != null) ? this.parser : new Parser(tokens);
            List<Instruction> instructions = activeParser.parse();

            // Environment is also generic now
            Environment<Object> env = new Environment<>();
            for (Instruction instruction : instructions) {
                instruction.execute(env);
            }

        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
