begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.mockrandom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|mockrandom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Randomly combines terms index impl w/ postings impls. and uses non-CFS format for docvalues  */
end_comment

begin_class
DECL|class|MockRandomDocValuesCodec
specifier|public
class|class
name|MockRandomDocValuesCodec
extends|extends
name|MockRandomCodec
block|{
DECL|method|MockRandomDocValuesCodec
specifier|public
name|MockRandomDocValuesCodec
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|,
literal|"MockDocValuesCodec"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// uses noCFS for docvalues for test coverage
block|}
block|}
end_class

end_unit

