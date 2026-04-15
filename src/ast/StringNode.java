package ast;

import environment.Environment;

/**
 * StringNode — A leaf node representing a string literal in the source code.
 *
 * Examples of what this represents in SPEEK:
 *   let name be "Sitare"          →  StringNode("Sitare")
 *   say "Hello from SPEEK"        →  StringNode("Hello from SPEEK")
 *
 * When evaluated it simply returns the stored string.
 * It does NOT need the Environment because there are no variables involved.
 */
public class StringNode implements Expression<String> {

    private final String value; // the string text (without surrounding quotes)

    /**
     * Create a StringNode for the given text.
     *
     * @param value the string content (quotes already stripped by Tokenizer)
     */
    public StringNode(String value) {
        this.value = value;
    }

    /**
     * Evaluate by returning the stored string.
     * The Environment is not used here.
     */
    @Override
    public String evaluate(Environment<?> env) {
        return value;
    }

    @Override
    public String toString() {
        return "StringNode(\"" + value + "\")";
    }
}
