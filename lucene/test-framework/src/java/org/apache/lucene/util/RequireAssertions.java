begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|ClassValidator
import|;
end_import

begin_comment
comment|/**  * Require assertions for Lucene/Solr packages.  */
end_comment

begin_class
DECL|class|RequireAssertions
specifier|public
class|class
name|RequireAssertions
implements|implements
name|ClassValidator
block|{
annotation|@
name|Override
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|Throwable
block|{
try|try
block|{
assert|assert
literal|false
assert|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Enable assertions globally (-ea) or for Solr/Lucene subpackages only."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|// Ok, enabled.
block|}
block|}
block|}
end_class

end_unit

