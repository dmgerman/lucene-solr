begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|TermFreqIterator
import|;
end_import

begin_comment
comment|/**  * This wrapper buffers the incoming elements and makes sure they are in  * random order.  */
end_comment

begin_class
DECL|class|UnsortedTermFreqIteratorWrapper
specifier|public
class|class
name|UnsortedTermFreqIteratorWrapper
extends|extends
name|BufferingTermFreqIteratorWrapper
block|{
DECL|method|UnsortedTermFreqIteratorWrapper
specifier|public
name|UnsortedTermFreqIteratorWrapper
parameter_list|(
name|TermFreqIterator
name|source
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

