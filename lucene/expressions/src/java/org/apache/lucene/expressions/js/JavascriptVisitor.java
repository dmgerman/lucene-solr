begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// ANTLR GENERATED CODE: DO NOT EDIT
end_comment

begin_package
DECL|package|org.apache.lucene.expressions.js
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
package|;
end_package

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|tree
operator|.
name|ParseTreeVisitor
import|;
end_import

begin_comment
comment|/**  * This interface defines a complete generic visitor for a parse tree produced  * by {@link JavascriptParser}.  *  * @param<T> The return type of the visit operation. Use {@link Void} for  * operations with no return type.  */
end_comment

begin_interface
DECL|interface|JavascriptVisitor
interface|interface
name|JavascriptVisitor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ParseTreeVisitor
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * Visit a parse tree produced by {@link JavascriptParser#compile}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitCompile
name|T
name|visitCompile
parameter_list|(
name|JavascriptParser
operator|.
name|CompileContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code conditional}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitConditional
name|T
name|visitConditional
parameter_list|(
name|JavascriptParser
operator|.
name|ConditionalContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code boolor}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBoolor
name|T
name|visitBoolor
parameter_list|(
name|JavascriptParser
operator|.
name|BoolorContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code boolcomp}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBoolcomp
name|T
name|visitBoolcomp
parameter_list|(
name|JavascriptParser
operator|.
name|BoolcompContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code numeric}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitNumeric
name|T
name|visitNumeric
parameter_list|(
name|JavascriptParser
operator|.
name|NumericContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code addsub}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitAddsub
name|T
name|visitAddsub
parameter_list|(
name|JavascriptParser
operator|.
name|AddsubContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code unary}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitUnary
name|T
name|visitUnary
parameter_list|(
name|JavascriptParser
operator|.
name|UnaryContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code precedence}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitPrecedence
name|T
name|visitPrecedence
parameter_list|(
name|JavascriptParser
operator|.
name|PrecedenceContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code muldiv}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitMuldiv
name|T
name|visitMuldiv
parameter_list|(
name|JavascriptParser
operator|.
name|MuldivContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code external}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitExternal
name|T
name|visitExternal
parameter_list|(
name|JavascriptParser
operator|.
name|ExternalContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code bwshift}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBwshift
name|T
name|visitBwshift
parameter_list|(
name|JavascriptParser
operator|.
name|BwshiftContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code bwor}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBwor
name|T
name|visitBwor
parameter_list|(
name|JavascriptParser
operator|.
name|BworContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code booland}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBooland
name|T
name|visitBooland
parameter_list|(
name|JavascriptParser
operator|.
name|BoolandContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code bwxor}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBwxor
name|T
name|visitBwxor
parameter_list|(
name|JavascriptParser
operator|.
name|BwxorContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code bwand}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBwand
name|T
name|visitBwand
parameter_list|(
name|JavascriptParser
operator|.
name|BwandContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Visit a parse tree produced by the {@code booleqne}    * labeled alternative in {@link JavascriptParser#expression}.    * @param ctx the parse tree    * @return the visitor result    */
DECL|method|visitBooleqne
name|T
name|visitBooleqne
parameter_list|(
name|JavascriptParser
operator|.
name|BooleqneContext
name|ctx
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

