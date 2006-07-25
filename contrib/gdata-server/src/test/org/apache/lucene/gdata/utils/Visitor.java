begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

begin_interface
DECL|interface|Visitor
specifier|public
interface|interface
name|Visitor
block|{
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|Object
index|[]
name|objects
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

