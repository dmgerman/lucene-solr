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
comment|/**  *   */
end_comment

begin_interface
DECL|interface|DocumentContext
specifier|public
interface|interface
name|DocumentContext
block|{
DECL|method|getDocument
specifier|public
name|Document
name|getDocument
parameter_list|()
function_decl|;
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|Document
name|arg
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

