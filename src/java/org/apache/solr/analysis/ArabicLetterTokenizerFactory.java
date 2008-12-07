begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
operator|.
name|ArabicLetterTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|ArabicLetterTokenizerFactory
specifier|public
class|class
name|ArabicLetterTokenizerFactory
extends|extends
name|BaseTokenizerFactory
block|{
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|ArabicLetterTokenizer
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
end_class

end_unit

