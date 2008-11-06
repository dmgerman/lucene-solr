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
name|FSDirectory
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
name|AbstractSolrTestCase
import|;
end_import

begin_class
DECL|class|AlternateDirectoryTest
specifier|public
class|class
name|AlternateDirectoryTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig-altdirectory.xml"
return|;
block|}
comment|/**    * Simple test to ensure that alternate IndexReaderFactory is being used.    *     * @throws Exception    */
DECL|method|testAltDirectoryUsed
specifier|public
name|void
name|testAltDirectoryUsed
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|TestFSDirectoryFactory
operator|.
name|openCalled
argument_list|)
expr_stmt|;
block|}
DECL|class|TestFSDirectoryFactory
specifier|static
specifier|public
class|class
name|TestFSDirectoryFactory
extends|extends
name|DirectoryFactory
block|{
DECL|field|openCalled
specifier|public
specifier|static
name|boolean
name|openCalled
init|=
literal|false
decl_stmt|;
DECL|method|open
specifier|public
name|FSDirectory
name|open
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|openCalled
operator|=
literal|true
expr_stmt|;
return|return
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

