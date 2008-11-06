begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|store
operator|.
name|Directory
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import

begin_comment
comment|/**  * Provides access to a Directory implementation.   *   */
end_comment

begin_class
DECL|class|DirectoryFactory
specifier|public
specifier|abstract
class|class
name|DirectoryFactory
implements|implements
name|NamedListInitializedPlugin
block|{
comment|/**    * Opens a Lucene directory    *     * @return    * @throws IOException    */
DECL|method|open
specifier|public
specifier|abstract
name|Directory
name|open
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
block|}
end_class

end_unit

