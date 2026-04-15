package ast;

import environment.Environment;

/**
 * NumberNode — A leaf node representing a numeric literal in the source code.
 *
 * Examples of what this represents in SPEEK:
 *   let x be 10     →  NumberNode(10.0)
 *   let y be 3.14   →  NumberNode(3.14)
 *   repeat 4 times  →  NumberNode(4.0)
 *
 * When evaluated it simply returns the stored number.
 * It does NOT need the Environment because it does not involve any variables.
 */
public class NumberNode implements Expression<Double> {

    private final double value; // the numeric value, stored as double

    /**
     * Create a NumberNode for the given numeric value.
     *
     * @param value the number this node represents
     */
    public NumberNode(double value) {
        this.value = value;
    }

    /**
     * Evaluate by returning the stored number.
     * The Environment is not used here.
     */
    @Override
    public Double evaluate(Environment<?> env) {
        return value;
    }

    @Override
    public String toString() {
        return "NumberNode(" + value + ")";
    }
}
