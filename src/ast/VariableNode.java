package ast;

import environment.Environment;

/**
 * VariableNode — A leaf node representing a variable reference in the source code.
 *
 * Examples of what this represents in SPEEK:
 *   say x          →  VariableNode("x")
 *   let z be x + y →  VariableNode("x") and VariableNode("y")
 *
 * When evaluated it asks the Environment for the variable's current value
 * and returns whatever was stored there.
 *
 * If the variable has not been assigned yet, the Environment will throw
 * a RuntimeException with a clear error message.
 */
public class VariableNode implements Expression<Object> {

    private final String name; // the variable name as it appeared in source code

    /**
     * Create a VariableNode for the given variable name.
     *
     * @param name the variable name (e.g. "x", "score", "result")
     */
    public VariableNode(String name) {
        this.name = name;
    }

    /**
     * Evaluate by looking up this variable's value in the Environment.
     *
     * @param env  the current variable store
     * @return     the value stored for this variable (Double or String)
     */
    @Override
    public Object evaluate(Environment<?> env) {
        return env.get(name); // delegate to the Environment
    }

    /** Returns the variable name this node references. */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "VariableNode(" + name + ")";
    }
}
