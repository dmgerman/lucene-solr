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
DECL|interface|Attribute
specifier|public
interface|interface
name|Attribute
block|{
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getValue
specifier|public
name|Node
name|getValue
parameter_list|()
function_decl|;
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|Node
name|arg
parameter_list|)
function_decl|;
DECL|method|getSpecified
specifier|public
name|boolean
name|getSpecified
parameter_list|()
function_decl|;
DECL|method|setSpecified
specifier|public
name|void
name|setSpecified
parameter_list|(
name|boolean
name|arg
parameter_list|)
function_decl|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

