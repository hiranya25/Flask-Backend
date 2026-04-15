package tokenizer;

import java.util.List;

/**
 * ITokenizer — Interface for converting source code into tokens.
 *
 * This abstraction allows the Parser to depend on an interface rather than
 * a concrete implementation (Dependency Inversion Principle).
 */
public interface ITokenizer {

    /**
     * Scan the source and return the list of tokens.
     *
     * @return list of tokens representing the source program
     */
    List<Token> tokenize();
}
