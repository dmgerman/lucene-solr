begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_interface
DECL|interface|ConfigurableDocumentHandler
specifier|public
interface|interface
name|ConfigurableDocumentHandler
extends|extends
name|DocumentHandler
block|{
DECL|method|configure
name|void
name|configure
parameter_list|(
name|Properties
name|props
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

