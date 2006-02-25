begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  * Implemented by objects that produce Lucene Query objects from XML streams. Implementations are  * expected to be thread-safe so that they can be used to simultaneously parse multiple XML documents.  * @author maharwood  */
end_comment

begin_interface
DECL|interface|QueryBuilder
specifier|public
interface|interface
name|QueryBuilder
block|{
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
function_decl|;
block|}
end_interface

end_unit

