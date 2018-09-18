// Generated from /home/rsautter/intellijLogic/src/Logic.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LogicParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LogicVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LogicParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(LogicParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link LogicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(LogicParser.ExprContext ctx);
}