package tokenizer;

/**
 * Token — A single labelled piece of source code.
 *
 * After the Tokenizer reads source code it produces a list of Token objects.
 * Each Token has three pieces of information:
 *   type  — what kind of thing is it (NUMBER, IDENTIFIER, LET, …)
 *   value — the exact text from the source file (e.g. "10", "x", "let")
 *   line  — which line of source it came from (useful for error messages)
 *
 * Tokens are immutable — once created they never change.
 */
public class Token {

    private final TokenType type;   // the category of this token
    private final String value;     // the raw text from the source code
    private final int line;         // source line number (1-based)
    private final int column;       // column number where the token starts (0-based)

    /**
     * Create a new Token.
     *
     * @param type   the token category
     * @param value  the exact text this token represents
     * @param line   the line in the source file where this token appears
     * @param column the column in the source file where this token starts
     */
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    // ── Getters (no setters — tokens are immutable) ───────────────────────

    /** Returns the category of this token (e.g. NUMBER, LET, IDENTIFIER). */
    public TokenType getType() {
        return type;
    }

    /** Returns the raw text this token represents (e.g. "10", "let", "x"). */
    public String getValue() {
        return value;
    }

    /** Returns the source line number where this token was found. */
    public int getLine() {
        return line;
    }

    /** Returns the column number where this token was found. */
    public int getColumn() {
        return column;
    }

    /** Readable representation for debugging. */
    @Override
    public String toString() {
        return "Token(" + type + ", \"" + value + "\", line=" + line + ", col=" + column + ")";
    }
}
