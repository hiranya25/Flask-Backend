package tokenizer;

/**
 * TokenType — Every possible kind of "word" (token) in the SPEEK language.
 *
 * The Tokenizer scans raw source code and labels each piece
 * with one of these types. The Parser then uses these labels
 * to understand the structure of the program.
 *
 * SPEEK keywords:
 *   let x be 10        → variable assignment
 *   say x              → print
 *   if x is greater than 5 then → conditional
 *   repeat 3 times     → loop
 */
public enum TokenType {

    // ── Keywords ──────────────────────────────────────────────────────────
    LET,            // "let"
    BE,             // "be"
    SAY,            // "say"
    IF,             // "if"
    IS,             // "is"
    GREATER,        // "greater"
    THAN,           // "than"
    LESS,           // "less"
    THEN,           // "then"
    REPEAT,         // "repeat"
    TIMES,          // "times"
    ELSE,           // "else"

    // ── Literals ──────────────────────────────────────────────────────────
    NUMBER,         // e.g. 10, 3.14
    STRING,         // e.g. "hello"

    // ── Identifiers ───────────────────────────────────────────────────────
    IDENTIFIER,     // variable names: x, result, score, etc.

    // ── Arithmetic Operators ──────────────────────────────────────────────
    PLUS,           // +
    MINUS,          // -
    STAR,           // *
    SLASH,          // /

    // ── Comparison Operators ──────────────────────────────────────────────
    EQUAL_EQUAL,    // ==  (bonus: equality check)

    // ── Structural ────────────────────────────────────────────────────────
    NEWLINE,        // end of a line
    EOF             // end of file / source string
}
