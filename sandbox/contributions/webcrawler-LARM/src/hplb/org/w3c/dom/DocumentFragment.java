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
DECL|interface|DocumentFragment
specifier|public
interface|interface
name|DocumentFragment
extends|extends
name|Node
block|{
DECL|method|getMasterDoc
specifier|public
name|Document
name|getMasterDoc
parameter_list|()
function_decl|;
DECL|method|setMasterDoc
specifier|public
name|void
name|setMasterDoc
parameter_list|(
name|Document
name|arg
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

