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
DECL|interface|AttributeList
specifier|public
interface|interface
name|AttributeList
block|{
DECL|method|getAttribute
specifier|public
name|Attribute
name|getAttribute
parameter_list|(
name|String
name|attrName
parameter_list|)
function_decl|;
DECL|method|setAttribute
specifier|public
name|Attribute
name|setAttribute
parameter_list|(
name|Attribute
name|attr
parameter_list|)
function_decl|;
DECL|method|remove
specifier|public
name|Attribute
name|remove
parameter_list|(
name|String
name|attrName
parameter_list|)
function_decl|;
DECL|method|item
specifier|public
name|Attribute
name|item
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

