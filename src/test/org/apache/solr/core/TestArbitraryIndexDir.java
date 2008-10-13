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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|standard
operator|.
name|StandardAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriter
operator|.
name|MaxFieldLength
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
name|queryParser
operator|.
name|ParseException
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
name|queryParser
operator|.
name|QueryParser
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
name|search
operator|.
name|Hits
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|common
operator|.
name|SolrException
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|TestHarness
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|TestArbitraryIndexDir
specifier|public
class|class
name|TestArbitraryIndexDir
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"solr"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"data"
argument_list|)
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|solrConfig
operator|=
name|h
operator|.
name|createConfig
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|=
operator|new
name|TestHarness
argument_list|(
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|solrConfig
argument_list|,
name|getSchemaFile
argument_list|()
argument_list|)
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|"version"
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Test
DECL|method|testLoadNewIndexDir
specifier|public
name|void
name|testLoadNewIndexDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|ParseException
block|{
comment|//add a doc in original index dir
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"name"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//create a new index dir and index.properties file
name|File
name|idxprops
init|=
operator|new
name|File
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDataDir
argument_list|()
operator|+
literal|"index.properties"
argument_list|)
decl_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|File
name|newDir
init|=
operator|new
name|File
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDataDir
argument_list|()
operator|+
literal|"index_temp"
argument_list|)
decl_stmt|;
name|newDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"index"
argument_list|,
name|newDir
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|idxprops
argument_list|)
expr_stmt|;
name|p
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|"index properties"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to write index.properties"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|//add a doc in the new index dir
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|newDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
operator|new
name|MaxFieldLength
argument_list|(
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"name2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//commit will cause searcher to open with the new index dir
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//new index dir contains just 1 doc.
name|assertQ
argument_list|(
literal|"return doc with id 2"
argument_list|,
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|newDir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

