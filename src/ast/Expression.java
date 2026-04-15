package ast;

import environment.Environment;

/**
 * Expression — Interface for every node in the expression tree (AST).
 *
 * An expression is anything that PRODUCES A VALUE when evaluated:
 *   • A number literal  → 42
 *   • A string literal  → "hello"
 *   • A variable name   → x  (looks up the current value)
 *   • A binary operation → x + y * 2  (computes a result)
 *
 * Every concrete node class (NumberNode, StringNode, VariableNode,
 * BinaryOpNode) implements this interface by providing an evaluate()
 * method that returns either a Double (for numbers) or a String (for text).
 *
 * WHY AN INTERFACE?
 *   Using an interface lets the Parser and Evaluator handle ALL expression
 *   types through a single type. A BinaryOpNode stores its left and right
 *   children as Expression — it doesn't need to know whether they are
 *   numbers, strings, or more complex sub-expressions.
 */
public interface Expression<T> {

    /**
     * Evaluate this expression and return its value.
     *
     * @param env  the current variable store (needed for VariableNode lookups)
     * @return     the evaluated result of type T
     */
    T evaluate(Environment<?> env);
}
