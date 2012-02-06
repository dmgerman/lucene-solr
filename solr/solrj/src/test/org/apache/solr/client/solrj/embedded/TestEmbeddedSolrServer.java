begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|embedded
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|util
operator|.
name|LuceneTestCase
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
name|SolrTestCaseJ4
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
name|FileUtils
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
name|core
operator|.
name|CoreContainer
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
name|core
operator|.
name|SolrCore
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestEmbeddedSolrServer
specifier|public
class|class
name|TestEmbeddedSolrServer
extends|extends
name|LuceneTestCase
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestEmbeddedSolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cores
specifier|protected
name|CoreContainer
name|cores
init|=
literal|null
decl_stmt|;
DECL|field|home
specifier|private
name|File
name|home
decl_stmt|;
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
literal|"solrj/solr/shared"
return|;
block|}
DECL|method|getOrigSolrXml
specifier|public
name|String
name|getOrigSolrXml
parameter_list|()
block|{
return|return
literal|"solr.xml"
return|;
block|}
DECL|method|getSolrXml
specifier|public
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"test-solr.xml"
return|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|getSolrHome
argument_list|()
argument_list|)
expr_stmt|;
name|home
operator|=
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|home
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"pwd: "
operator|+
operator|(
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|origSolrXml
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
name|getOrigSolrXml
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
name|getSolrXml
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|origSolrXml
argument_list|,
name|solrXml
argument_list|)
expr_stmt|;
name|cores
operator|=
operator|new
name|CoreContainer
argument_list|(
name|home
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|solrXml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|AbstractSolrTestCase
operator|.
name|recurseDelete
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"!!!! WARNING: best effort to remove "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" FAILED !!!!!"
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|getSolrCore0
specifier|protected
name|EmbeddedSolrServer
name|getSolrCore0
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core0"
argument_list|)
return|;
block|}
DECL|method|getSolrCore1
specifier|protected
name|EmbeddedSolrServer
name|getSolrCore1
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|)
return|;
block|}
DECL|method|testGetCoreContainer
specifier|public
name|void
name|testGetCoreContainer
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cores
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cores
argument_list|,
name|getSolrCore1
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShutdown
specifier|public
name|void
name|testShutdown
parameter_list|()
block|{
name|EmbeddedSolrServer
name|solrServer
init|=
name|getSolrCore0
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cores
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SolrCore
argument_list|>
name|solrCores
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrCore
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrCore
name|solrCore
range|:
name|cores
operator|.
name|getCores
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|solrCore
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|solrCores
operator|.
name|add
argument_list|(
name|solrCore
argument_list|)
expr_stmt|;
block|}
name|solrServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cores
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrCore
name|solrCore
range|:
name|solrCores
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|solrCore
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

