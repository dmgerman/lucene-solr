begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * $Id$  */
end_comment

begin_package
DECL|package|hplb.org.w3c.dom
package|package
name|hplb
operator|.
name|org
operator|.
name|w3c
operator|.
name|dom
package|;
end_package

begin_comment
comment|/**  * Represents the content of comments:&lt;!-- ... --&gt;  */
end_comment

begin_interface
DECL|interface|Comment
specifier|public
interface|interface
name|Comment
extends|extends
name|Node
block|{
DECL|method|getData
specifier|public
name|String
name|getData
parameter_list|()
function_decl|;
DECL|method|setData
specifier|public
name|void
name|setData
parameter_list|(
name|String
name|arg
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

