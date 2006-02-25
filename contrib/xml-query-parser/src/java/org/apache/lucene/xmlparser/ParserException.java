begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment

begin_package
DECL|package|org.apache.lucene.xmlparser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
package|;
end_package

begin_comment
comment|/**  * @author maharwood  */
end_comment

begin_class
DECL|class|ParserException
specifier|public
class|class
name|ParserException
extends|extends
name|Exception
block|{
comment|/** 	 *  	 */
DECL|method|ParserException
specifier|public
name|ParserException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/** 	 * @param message 	 */
DECL|method|ParserException
specifier|public
name|ParserException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/** 	 * @param message 	 * @param cause 	 */
DECL|method|ParserException
specifier|public
name|ParserException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/** 	 * @param cause 	 */
DECL|method|ParserException
specifier|public
name|ParserException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
block|}
end_class

end_unit

