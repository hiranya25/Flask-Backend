package tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer — Converts raw SPEEK source code into a list of Tokens.
 *
 * HOW IT WORKS (Step 1 of the pipeline):
 *   The Tokenizer reads the source string character by character.
 *   When it recognises a complete "word" or symbol it creates a Token
 *   and adds it to the list. At the end it appends an EOF token.
 *
 * SPEEK tokens it recognises:
 *   Keywords  : let, be, say, if, is, greater, than, less, then, repeat, times
 *   Numbers   : sequences of digits, optionally with a decimal point
 *   Strings   : text enclosed in double quotes "like this"
 *   Operators : + - * / ==
 *   Identifiers: variable names (letters/digits, must start with a letter)
 *   Newlines  : end-of-line markers (the Parser uses these as statement ends)
 */
public class Tokenizer implements ITokenizer {

    private final String source;    // the full source code to tokenize
    private int pos;                // current character position
    private int line;               // current line number (1-based)
    private int lineStart;          // index where the current line started
    private final List<Token> tokens; // tokens built up during scanning

    /**
     * Create a Tokenizer for the given source code string.
     *
     * @param source the complete SPEEK program text
     */
    public Tokenizer(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
        this.lineStart = 0;
        this.tokens = new ArrayList<>();
    }

    /**
     * Scan the entire source and return the complete list of tokens.
     * The last token in the list is always EOF.
     *
     * @return list of tokens representing the source program
     */
    public List<Token> tokenize() {
        while (pos < source.length()) {
            char c = source.charAt(pos);

            // ── Skip spaces and tabs (not newlines) ──────────────────────
            if (c == ' ' || c == '\t' || c == '\r') {
                pos++;
                continue;
            }

            // ── Newline marks the end of a statement ─────────────────────
            if (c == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\\n", line, pos - lineStart));
                line++;
                pos++;
                lineStart = pos;
                continue;
            }

            // ── Skip comments: lines starting with # ─────────────────────
            if (c == '#') {
                skipToEndOfLine();
                continue;
            }

            // ── String literal: "hello world" ────────────────────────────
            if (c == '"') {
                readString();
                continue;
            }

            // ── Number literal: 42 or 3.14 ───────────────────────────────
            if (Character.isDigit(c)) {
                readNumber();
                continue;
            }

            // ── Two-character operator: == ────────────────────────────────
            if (c == '=' && peek(1) == '=') {
                tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", line, pos - lineStart));
                pos += 2;
                continue;
            }

            // ── Single-character operators ────────────────────────────────
            if (c == '+') { tokens.add(new Token(TokenType.PLUS,  "+", line, pos - lineStart)); pos++; continue; }
            if (c == '-') { tokens.add(new Token(TokenType.MINUS, "-", line, pos - lineStart)); pos++; continue; }
            if (c == '*') { tokens.add(new Token(TokenType.STAR,  "*", line, pos - lineStart)); pos++; continue; }
            if (c == '/') { tokens.add(new Token(TokenType.SLASH, "/", line, pos - lineStart)); pos++; continue; }

            // ── Keyword or identifier ─────────────────────────────────────
            if (Character.isLetter(c)) {
                readWord();
                continue;
            }

            // ── Unknown character — report clearly and skip ───────────────
            System.err.println("[Tokenizer] Warning: unknown character '" + c
                    + "' on line " + line + " — skipping.");
            pos++;
        }

        tokens.add(new Token(TokenType.EOF, "", line, pos - lineStart));
        return tokens;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Private helper methods
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Read a word (sequence of letters/digits) and decide whether it is
     * a keyword or an identifier (variable name).
     */
    private void readWord() {
        int start = pos;
        int col = pos - lineStart;
        while (pos < source.length() && Character.isLetterOrDigit(source.charAt(pos))) {
            pos++;
        }
        String word = source.substring(start, pos);

        // Map the word to a keyword token type, or IDENTIFIER if unknown
        TokenType type = getKeywordType(word);
        tokens.add(new Token(type, word, line, col));
    }

    /**
     * Return the TokenType for a SPEEK keyword, or IDENTIFIER if the word
     * is not a keyword.
     */
    private TokenType getKeywordType(String word) {
        switch (word.toLowerCase()) {
            case "let":     return TokenType.LET;
            case "be":      return TokenType.BE;
            case "say":     return TokenType.SAY;
            case "if":      return TokenType.IF;
            case "is":      return TokenType.IS;
            case "greater": return TokenType.GREATER;
            case "than":    return TokenType.THAN;
            case "less":    return TokenType.LESS;
            case "then":    return TokenType.THEN;
            case "repeat":  return TokenType.REPEAT;
            case "times":   return TokenType.TIMES;
            case "else":    return TokenType.ELSE;
            default:        return TokenType.IDENTIFIER;
        }
    }

    /**
     * Read a number literal (integer or decimal) and emit a NUMBER token.
     */
    private void readNumber() {
        int start = pos;
        int col = pos - lineStart;
        while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
            pos++;
        }
        // Handle optional decimal part
        if (pos < source.length() && source.charAt(pos) == '.'
                && pos + 1 < source.length() && Character.isDigit(source.charAt(pos + 1))) {
            pos++; // consume '.'
            while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                pos++;
            }
        }
        tokens.add(new Token(TokenType.NUMBER, source.substring(start, pos), line, col));
    }

    /**
     * Read a string literal enclosed in double quotes and emit a STRING token.
     * The stored value does NOT include the surrounding quote characters.
     */
    private void readString() {
        int col = pos - lineStart;
        pos++; // skip opening "
        int start = pos;
        while (pos < source.length() && source.charAt(pos) != '"') {
            if (source.charAt(pos) == '\n') {
                throw new RuntimeException(
                    "[Tokenizer] Unterminated string literal on line " + line);
            }
            pos++;
        }
        if (pos >= source.length()) {
            throw new RuntimeException(
                "[Tokenizer] Unterminated string literal on line " + line);
        }
        String text = source.substring(start, pos);
        pos++; // skip closing "
        tokens.add(new Token(TokenType.STRING, text, line, col));
    }

    /**
     * Skip all characters until (and including) the end of the current line.
     * Used to ignore comment lines that start with '#'.
     */
    private void skipToEndOfLine() {
        while (pos < source.length() && source.charAt(pos) != '\n') {
            pos++;
        }
    }

    /**
     * Peek at the character that is 'offset' positions ahead of the current
     * position without consuming any characters. Returns '\0' if out of bounds.
     */
    private char peek(int offset) {
        int index = pos + offset;
        if (index >= source.length()) return '\0';
        return source.charAt(index);
    }
}
