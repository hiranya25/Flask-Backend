package parser;

import ast.*;
import instructions.*;
import tokenizer.Token;
import tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser — Converts the flat list of Tokens into a list of Instructions.
 */
public class Parser implements IParser {

    private final List<Token> tokens; // the flat list from the Tokenizer
    private int pos;                  // which token we are currently looking at

    /**
     * Create a Parser for the given token list.
     *
     * @param tokens the token list produced by the Tokenizer
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Public entry point
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Parse all tokens and return the list of top-level Instructions.
     *
     * @return the parsed program as a list of instructions
     */
    public List<Instruction> parse() {
        List<Instruction> instructions = new ArrayList<>();

        while (!isAtEnd()) {
            // Skip blank lines (consecutive newlines)
            if (check(TokenType.NEWLINE)) {
                advance();
                continue;
            }
            // Parse one instruction and add it to the list
            Instruction instruction = parseInstruction();
            if (instruction != null) {
                instructions.add(instruction);
            }
        }

        return instructions;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Instruction parsing
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Decide which kind of instruction starts at the current token and
     * delegate to the appropriate parse method.
     */
    private Instruction parseInstruction() {
        Token current = peek();

        if (current.getType() == TokenType.LET) {
            return parseAssign();
        } else if (current.getType() == TokenType.SAY) {
            return parsePrint();
        } else if (current.getType() == TokenType.IF) {
            return parseIf();
        } else if (current.getType() == TokenType.REPEAT) {
            return parseRepeat();
        }

        // Unknown token at the start of a line — skip the line and warn
        System.err.println("[Parser] Warning: unexpected token '"
                + current.getValue() + "' on line " + current.getLine()
                + " — skipping line.");
        skipLine();
        return null;
    }

    /**
     * Parse:  let <identifier> be <expression>
     */
    private Instruction parseAssign() {
        int line = peek().getLine();
        consume(TokenType.LET, "Expected 'let'");                       // let
        Token nameToken = consume(TokenType.IDENTIFIER,
                "Expected variable name after 'let' on line " + line); // x
        consume(TokenType.BE, "Expected 'be' after variable name '"
                + nameToken.getValue() + "' on line " + line);         // be
        Expression value = parseExpression();                           // <expr>
        consumeNewlineOrEOF();
        return new AssignInstruction(nameToken.getValue(), value);
    }

    /**
     * Parse:  say <expression>
     */
    private Instruction parsePrint() {
        int line = peek().getLine();
        consume(TokenType.SAY, "Expected 'say'");   // say
        Expression expr = parseExpression();        // <expr>
        consumeNewlineOrEOF();
        return new PrintInstruction(expr);
    }

    /**
     * Parse:
     *   if <identifier> is greater than <expression> then
     *     <body>
     *
     *   if <identifier> is less than <expression> then
     *     <body>
     *
     *   if <identifier> == <expression> then
     *     <body>
     */
    private Instruction parseIf() {
        int line = peek().getLine();
        consume(TokenType.IF, "Expected 'if'");                                 // if
        Expression left = parseExpression();
        String operator = parseComparisonOperator(line);
        Expression right = parseExpression();
        consume(TokenType.THEN, "Expected 'then' at end of if-condition on line " + line); // then
        consumeNewlineOrEOF();
        List<Instruction> body = parseBody();

        // Optional: Parse "else" part
        List<Instruction> elseBody = null;
        if (check(TokenType.ELSE)) {
            consume(TokenType.ELSE, "Expected 'else'");
            consumeNewlineOrEOF();
            elseBody = parseBody();
        }

        Expression condition = new BinaryOpNode(left, operator, right);
        return new IfInstruction(condition, body, elseBody);
    }

    /**
     * Parse the comparison operator portion of an if-condition.
     * Handles:
     *   "is greater than"  → ">"
     *   "is less than"     → "<"
     *   "=="               → "=="
     */
    private String parseComparisonOperator(int line) {
        if (check(TokenType.IS)) {
            consume(TokenType.IS, "");
            if (check(TokenType.GREATER)) {
                consume(TokenType.GREATER, "");
                consume(TokenType.THAN,
                        "Expected 'than' after 'greater' on line " + line);
                return ">";
            } else if (check(TokenType.LESS)) {
                consume(TokenType.LESS, "");
                consume(TokenType.THAN,
                        "Expected 'than' after 'less' on line " + line);
                return "<";
            } else {
                throw new RuntimeException(
                    "[Parser] Expected 'greater' or 'less' after 'is' on line " + line);
            }
        } else if (check(TokenType.EQUAL_EQUAL)) {
            advance();
            return "==";
        }
        throw new RuntimeException(
            "[Parser] Expected 'is greater than', 'is less than', or '==' on line " + line);
    }

    /**
     * Parse:
     *   repeat <number> times
     *     <body>
     */
    private Instruction parseRepeat() {
        int line = peek().getLine();
        consume(TokenType.REPEAT, "Expected 'repeat'");                         // repeat
        Expression count = parseExpression();                                   // <number>
        consume(TokenType.TIMES, "Expected 'times' after count on line " + line); // times
        consumeNewlineOrEOF();

        // Parse indented body
        List<Instruction> body = parseBody();

        return new RepeatInstruction(count, body);
    }

    /**
     * Parse the indented body of an if or repeat block.
     *
     * In SPEEK, body lines are indented (start with spaces or a tab).
     * We keep reading instructions as long as the current line is indented.
     * When we hit a non-indented line (or EOF), the body is done.
     *
     * HOW INDENTATION IS DETECTED:
     *   We look ahead in the token list. If the next non-newline token
     *   appears on the same or a later line AND that line starts with
     *   whitespace in the original source, it belongs to the body.
     *
     *   Simpler approach used here: the body ends when we see a top-level
     *   keyword (let, say, if, repeat) at position 0 of a new line,
     *   OR when we reach EOF.
     */
    private List<Instruction> parseBody() {
        List<Instruction> body = new ArrayList<>();

        while (!isAtEnd()) {
            // Skip blank lines inside the body
            if (check(TokenType.NEWLINE)) {
                advance();
                continue;
            }
            // If the next token is a body-level statement, parse it
            if (isBodyStatement()) {
                Instruction instruction = parseInstruction();
                if (instruction != null) {
                    body.add(instruction);
                }
            } else {
                // We've hit a non-body token — the block is over
                break;
            }
        }

        return body;
    }

    /**
     * Determine whether the current token starts a statement that belongs
     * inside a block body.
     *
     * Strategy: body instructions must be indented. Since the tokenizer
     * strips whitespace, we use a source-position trick: we record which
     * token index each new line starts at, then check if the current line
     * began with whitespace. For simplicity, we treat LET, SAY, IF, and
     * REPEAT as body tokens when the token immediately follows a NEWLINE
     * and the source line was indented.
     *
     * Practical rule that works for this project:
     *   A statement is a body statement if it is LET or SAY (the two
     *   statement types that can appear inside a block). Nested IF and
     *   REPEAT are also body statements (supports nested blocks).
     *   Top-level statements that start a new block end the body.
     *
     * To keep things simple we look at whether the *previous* token was
     * a NEWLINE AND the current token is a statement keyword.
     */
    private boolean isBodyStatement() {
        Token current = peek();
        TokenType t = current.getType();

        // ELSE is never a standalone body statement; it's part of an IF block
        if (t == TokenType.ELSE) {
            return false;
        }

        if (t != TokenType.LET && t != TokenType.SAY
                && t != TokenType.IF && t != TokenType.REPEAT) {
            return false;
        }

        // A statement is a body statement only if it is indented.
        // Statements at column 0 are top-level.
        return current.getColumn() > 0;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Expression parsing (implements operator precedence via call chain)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Parse an expression involving + and - (lowest precedence).
     *
     * parseExpression calls parseTerm for each operand.
     * Since parseTerm handles * and /, those operators bind more tightly.
     */
    Expression parseExpression() {
        Expression left = parseTerm(); // get the left side (handles * /)

        // Keep consuming + or - operators and building a tree to the right
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            String operator = advance().getValue(); // consume "+" or "-"
            Expression right = parseTerm();         // get the right side
            left = new BinaryOpNode(left, operator, right);
        }

        return left;
    }

    /**
     * Parse an expression involving * and / (higher precedence than + and -).
     *
     * parseTerm calls parsePrimary for each operand.
     */
    private Expression parseTerm() {
        Expression left = parsePrimary(); // get a single value or parenthesised expr

        while (check(TokenType.STAR) || check(TokenType.SLASH)) {
            String operator = advance().getValue(); // consume "*" or "/"
            Expression right = parsePrimary();
            left = new BinaryOpNode(left, operator, right);
        }

        return left;
    }

    /**
     * Parse a single primary value: a number, a string literal, or a variable name.
     * This is the leaf level of the expression tree.
     */
    private Expression parsePrimary() {
        Token token = peek();

        if (token.getType() == TokenType.NUMBER) {
            advance();
            return new NumberNode(Double.parseDouble(token.getValue()));
        }

        if (token.getType() == TokenType.STRING) {
            advance();
            return new StringNode(token.getValue());
        }

        if (token.getType() == TokenType.IDENTIFIER) {
            advance();
            return new VariableNode(token.getValue());
        }

        throw new RuntimeException(
            "[Parser] Expected a number, string, or variable name on line "
            + token.getLine() + ", but found: '" + token.getValue() + "'");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Token stream helper methods
    // ─────────────────────────────────────────────────────────────────────

    /** Return the current token without consuming it. */
    private Token peek() {
        return tokens.get(pos);
    }

    /** Consume and return the current token, moving to the next one. */
    private Token advance() {
        Token t = tokens.get(pos);
        if (!isAtEnd()) pos++;
        return t;
    }

    /**
     * Check whether the current token has the given type.
     * Does NOT consume the token.
     */
    private boolean check(TokenType type) {
        return peek().getType() == type;
    }

    /**
     * Consume the current token if it matches the expected type.
     * If it does NOT match, throw a RuntimeException with a clear message.
     */
    private Token consume(TokenType expected, String errorMessage) {
        if (check(expected)) {
            return advance();
        }
        Token t = peek();
        throw new RuntimeException(
            "[Parser] " + errorMessage
            + " (found '" + t.getValue() + "' on line " + t.getLine() + ")");
    }

    /**
     * Consume a NEWLINE or EOF token at the end of a statement.
     * Skips any extra newlines as well.
     */
    private void consumeNewlineOrEOF() {
        // Skip any trailing whitespace newlines
        while (check(TokenType.NEWLINE)) {
            advance();
        }
        // EOF is acceptable here too
    }

    /** Returns true if we have consumed all tokens (or reached EOF). */
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    /** Skip all tokens until (and including) the next NEWLINE or EOF. */
    private void skipLine() {
        while (!isAtEnd() && !check(TokenType.NEWLINE)) {
            advance();
        }
        if (check(TokenType.NEWLINE)) advance();
    }
}
