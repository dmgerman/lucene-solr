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
DECL|interface|Element
specifier|public
interface|interface
name|Element
extends|extends
name|Node
block|{
DECL|method|getTagName
specifier|public
name|String
name|getTagName
parameter_list|()
function_decl|;
DECL|method|attributes
specifier|public
name|AttributeList
name|attributes
parameter_list|()
function_decl|;
DECL|method|setAttribute
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Attribute
name|newAttr
parameter_list|)
function_decl|;
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|()
function_decl|;
DECL|method|getElementsByTagName
specifier|public
name|NodeIterator
name|getElementsByTagName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

