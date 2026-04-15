
import interpreter.Interpreter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main — The command-line entry point for the SPEEK interpreter.
 *
 * USAGE:
 *   java -cp out main.Main examples/program1.speek
 *
 * The program:
 *   1. Reads the source file path from the command-line argument
 *   2. Loads the file contents into a String
 *   3. Passes the String to the Interpreter to run
 */
public class Main {

    public static void main(String[] args) {

        // ── Validate command-line arguments ──────────────────────────────
        if (args.length != 1) {
            System.err.println("Usage: java -cp out main.Main <source-file.speek>");
            System.err.println("Example: java -cp out main.Main examples/program1.speek");
            System.exit(1);
        }

        String filePath = args[0];

        // ── Read the source file ──────────────────────────────────────────
        String sourceCode;
        try {
            sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error: Could not read file '" + filePath + "'");
            System.err.println("Make sure the file exists and the path is correct.");
            System.exit(1);
            return; // unreachable but required for compiler
        }

        // ── Run the interpreter ───────────────────────────────────────────
        // We can inject custom implementations here if needed (SOLID: Dependency Injection)
        Interpreter interpreter = new Interpreter();
        interpreter.run(sourceCode);
    }
}
