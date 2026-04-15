package environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Environment — The variable store for the running SPEEK program.
 *
 * During execution, every variable that has been assigned is stored here.
 * All instructions share ONE Environment instance so they can all read
 * and write the same variables.
 *
 * Internally it is just a HashMap<String, Object> where:
 *   key   = variable name (e.g. "x", "score")
 *   value = the current value (a Double or a String)
 *
 * WHY DO WE NEED THIS?
 *   If you remove the Environment, variables cannot exist across instructions.
 *   "let x be 10" stores x, and "say x" needs to retrieve it later.
 *   Without the Environment, "say x" has no way to know what x is.
 */
public class Environment<V> {

    // The map that holds all variable names and their current values
    private final Map<String, V> variables = new HashMap<>();

    /**
     * Store or update the value for a variable.
     *
     * @param name  the variable name
     * @param value the new value (Double or String)
     */
    public void set(String name, V value) {
        variables.put(name, value);
    }

    /**
     * Retrieve the current value of a variable.
     *
     * @param name the variable name to look up
     * @return the stored value (Double or String)
     * @throws RuntimeException with a descriptive message if the variable
     *         has not been assigned yet
     */
    public V get(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException(
                "[Runtime] Variable not defined: '" + name + "'. "
                + "Did you forget to assign it with 'let " + name + " be ...'?");
        }
        return variables.get(name);
    }

    /**
     * Check whether a variable exists in the store.
     *
     * @param name the variable name
     * @return true if the variable has been assigned
     */
    public boolean contains(String name) {
        return variables.containsKey(name);
    }

    @Override
    public String toString() {
        return "Environment" + variables.toString();
    }
}
