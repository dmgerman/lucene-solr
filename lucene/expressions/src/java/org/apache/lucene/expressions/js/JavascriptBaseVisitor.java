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
name|misc
operator|.
name|NotNull
import|;
end_import

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
name|AbstractParseTreeVisitor
import|;
end_import

begin_comment
comment|/**  * This class provides an empty implementation of {@link JavascriptVisitor},  * which can be extended to create a visitor which only needs to handle a subset  * of the available methods.  *  * @param<T> The return type of the visit operation. Use {@link Void} for  * operations with no return type.  */
end_comment

begin_class
DECL|class|JavascriptBaseVisitor
class|class
name|JavascriptBaseVisitor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractParseTreeVisitor
argument_list|<
name|T
argument_list|>
implements|implements
name|JavascriptVisitor
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitCompile
annotation|@
name|Override
specifier|public
name|T
name|visitCompile
parameter_list|(
name|JavascriptParser
operator|.
name|CompileContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitConditional
annotation|@
name|Override
specifier|public
name|T
name|visitConditional
parameter_list|(
name|JavascriptParser
operator|.
name|ConditionalContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBoolor
annotation|@
name|Override
specifier|public
name|T
name|visitBoolor
parameter_list|(
name|JavascriptParser
operator|.
name|BoolorContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBoolcomp
annotation|@
name|Override
specifier|public
name|T
name|visitBoolcomp
parameter_list|(
name|JavascriptParser
operator|.
name|BoolcompContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitNumeric
annotation|@
name|Override
specifier|public
name|T
name|visitNumeric
parameter_list|(
name|JavascriptParser
operator|.
name|NumericContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitAddsub
annotation|@
name|Override
specifier|public
name|T
name|visitAddsub
parameter_list|(
name|JavascriptParser
operator|.
name|AddsubContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitUnary
annotation|@
name|Override
specifier|public
name|T
name|visitUnary
parameter_list|(
name|JavascriptParser
operator|.
name|UnaryContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitPrecedence
annotation|@
name|Override
specifier|public
name|T
name|visitPrecedence
parameter_list|(
name|JavascriptParser
operator|.
name|PrecedenceContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitMuldiv
annotation|@
name|Override
specifier|public
name|T
name|visitMuldiv
parameter_list|(
name|JavascriptParser
operator|.
name|MuldivContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitExternal
annotation|@
name|Override
specifier|public
name|T
name|visitExternal
parameter_list|(
name|JavascriptParser
operator|.
name|ExternalContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBwshift
annotation|@
name|Override
specifier|public
name|T
name|visitBwshift
parameter_list|(
name|JavascriptParser
operator|.
name|BwshiftContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBwor
annotation|@
name|Override
specifier|public
name|T
name|visitBwor
parameter_list|(
name|JavascriptParser
operator|.
name|BworContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBooland
annotation|@
name|Override
specifier|public
name|T
name|visitBooland
parameter_list|(
name|JavascriptParser
operator|.
name|BoolandContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBwxor
annotation|@
name|Override
specifier|public
name|T
name|visitBwxor
parameter_list|(
name|JavascriptParser
operator|.
name|BwxorContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBwand
annotation|@
name|Override
specifier|public
name|T
name|visitBwand
parameter_list|(
name|JavascriptParser
operator|.
name|BwandContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBooleqne
annotation|@
name|Override
specifier|public
name|T
name|visitBooleqne
parameter_list|(
name|JavascriptParser
operator|.
name|BooleqneContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
block|}
end_class

end_unit
