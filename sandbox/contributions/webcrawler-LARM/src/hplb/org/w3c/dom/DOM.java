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
DECL|interface|DOM
specifier|public
interface|interface
name|DOM
block|{
DECL|method|createDocument
specifier|public
name|Document
name|createDocument
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
DECL|method|hasFeature
specifier|public
name|boolean
name|hasFeature
parameter_list|(
name|String
name|feature
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

