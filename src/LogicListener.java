// Generated from /home/rsautter/intellijLogic/src/Logic.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LogicParser}.
 */
public interface LogicListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LogicParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(LogicParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(LogicParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogicParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(LogicParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(LogicParser.ExprContext ctx);
}