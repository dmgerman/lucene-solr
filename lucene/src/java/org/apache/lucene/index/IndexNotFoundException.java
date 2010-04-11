begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_comment
comment|/**  * Signals that no index was found in the Directory. Possibly because the  * directory is empty, however can slso indicate an index corruption.  */
end_comment

begin_class
DECL|class|IndexNotFoundException
specifier|public
specifier|final
class|class
name|IndexNotFoundException
extends|extends
name|FileNotFoundException
block|{
DECL|method|IndexNotFoundException
specifier|public
name|IndexNotFoundException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

