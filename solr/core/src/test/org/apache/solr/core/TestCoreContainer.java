begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsInstanceOf
operator|.
name|instanceOf
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

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
name|io
operator|.
name|OutputStreamWriter
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
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
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
name|commons
operator|.
name|io
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
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|handler
operator|.
name|admin
operator|.
name|CollectionsHandler
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
name|handler
operator|.
name|admin
operator|.
name|CoreAdminHandler
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
name|handler
operator|.
name|admin
operator|.
name|InfoHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_class
DECL|class|TestCoreContainer
specifier|public
class|class
name|TestCoreContainer
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|oldSolrHome
specifier|private
specifier|static
name|String
name|oldSolrHome
decl_stmt|;
DECL|field|SOLR_HOME_PROP
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME_PROP
init|=
literal|"solr.solr.home"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|oldSolrHome
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|SOLR_HOME_PROP
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
if|if
condition|(
name|oldSolrHome
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|SOLR_HOME_PROP
argument_list|,
name|oldSolrHome
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|SOLR_HOME_PROP
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|solrHomeDirectory
specifier|private
name|File
name|solrHomeDirectory
decl_stmt|;
DECL|method|init
specifier|private
name|CoreContainer
name|init
parameter_list|(
name|String
name|dirName
parameter_list|)
throws|throws
name|Exception
block|{
name|solrHomeDirectory
operator|=
name|createTempDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|)
argument_list|,
name|solrHomeDirectory
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using solrconfig from "
operator|+
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|CoreContainer
name|ret
init|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|ret
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Test
DECL|method|testShareSchema
specifier|public
name|void
name|testShareSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"shareSchema"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|init
argument_list|(
literal|"_shareSchema"
argument_list|)
decl_stmt|;
try|try
block|{
name|CoreDescriptor
name|descriptor1
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|,
literal|"./collection1"
argument_list|)
decl_stmt|;
name|SolrCore
name|core1
init|=
name|cores
operator|.
name|create
argument_list|(
name|descriptor1
argument_list|)
decl_stmt|;
name|CoreDescriptor
name|descriptor2
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"core2"
argument_list|,
literal|"./collection1"
argument_list|)
decl_stmt|;
name|SolrCore
name|core2
init|=
name|cores
operator|.
name|create
argument_list|(
name|descriptor2
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|core1
operator|.
name|getLatestSchema
argument_list|()
argument_list|,
name|core2
operator|.
name|getLatestSchema
argument_list|()
argument_list|)
expr_stmt|;
name|core1
operator|.
name|close
argument_list|()
expr_stmt|;
name|core2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"shareSchema"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReloadSequential
specifier|public
name|void
name|testReloadSequential
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cc
init|=
name|init
argument_list|(
literal|"_reloadSequential"
argument_list|)
decl_stmt|;
try|try
block|{
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReloadThreaded
specifier|public
name|void
name|testReloadThreaded
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cc
init|=
name|init
argument_list|(
literal|"_reloadThreaded"
argument_list|)
decl_stmt|;
class|class
name|TestThread
extends|extends
name|Thread
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numThreads
init|=
literal|4
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|TestThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoCores
specifier|public
name|void
name|testNoCores
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
comment|//create solrHome
name|File
name|solrHomeDirectory
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|boolean
name|oldSolrXml
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|SetUpHome
argument_list|(
name|solrHomeDirectory
argument_list|,
name|oldSolrXml
condition|?
name|EMPTY_SOLR_XML
else|:
name|EMPTY_SOLR_XML2
argument_list|)
expr_stmt|;
name|CoreContainer
name|cores
init|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|cores
operator|.
name|load
argument_list|()
expr_stmt|;
try|try
block|{
comment|//assert zero cores
name|assertEquals
argument_list|(
literal|"There should not be cores"
argument_list|,
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
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"collection1"
argument_list|)
argument_list|,
name|solrHomeDirectory
argument_list|)
expr_stmt|;
comment|//add a new core
name|CoreDescriptor
name|coreDescriptor
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|SolrCore
name|newCore
init|=
name|cores
operator|.
name|create
argument_list|(
name|coreDescriptor
argument_list|)
decl_stmt|;
name|cores
operator|.
name|register
argument_list|(
name|newCore
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//assert one registered core
name|assertEquals
argument_list|(
literal|"There core registered"
argument_list|,
literal|1
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
if|if
condition|(
name|oldSolrXml
condition|)
block|{
name|assertXmlFile
argument_list|(
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
literal|"/solr/cores[@transientCacheSize='32']"
argument_list|)
expr_stmt|;
block|}
name|newCore
operator|.
name|close
argument_list|()
expr_stmt|;
name|cores
operator|.
name|remove
argument_list|(
literal|"core1"
argument_list|)
expr_stmt|;
comment|//assert cero cores
name|assertEquals
argument_list|(
literal|"There should not be cores"
argument_list|,
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
comment|// try and remove a core that does not exist
name|SolrCore
name|ret
init|=
name|cores
operator|.
name|remove
argument_list|(
literal|"non_existent_core"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLogWatcherEnabledByDefault
specifier|public
name|void
name|testLogWatcherEnabledByDefault
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getLogging
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SetUpHome
specifier|private
name|void
name|SetUpHome
parameter_list|(
name|File
name|solrHomeDirectory
parameter_list|,
name|String
name|xmlFile
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|solrHomeDirectory
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|File
name|solrXmlFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|solrXmlFile
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|xmlFile
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|//init
name|System
operator|.
name|setProperty
argument_list|(
name|SOLR_HOME_PROP
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClassLoaderHierarchy
specifier|public
name|void
name|testClassLoaderHierarchy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cc
init|=
name|init
argument_list|(
literal|"_classLoaderHierarchy"
argument_list|)
decl_stmt|;
try|try
block|{
name|ClassLoader
name|sharedLoader
init|=
name|cc
operator|.
name|loader
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|ClassLoader
name|contextLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|contextLoader
argument_list|,
name|sharedLoader
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|CoreDescriptor
name|descriptor1
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
literal|"core1"
argument_list|,
literal|"./collection1"
argument_list|)
decl_stmt|;
name|SolrCore
name|core1
init|=
name|cc
operator|.
name|create
argument_list|(
name|descriptor1
argument_list|)
decl_stmt|;
name|ClassLoader
name|coreLoader
init|=
name|core1
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|sharedLoader
argument_list|,
name|coreLoader
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|core1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSharedLib
specifier|public
name|void
name|testSharedLib
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmpRoot
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"testSharedLib"
argument_list|)
decl_stmt|;
name|File
name|lib
init|=
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"lib"
argument_list|)
decl_stmt|;
name|lib
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|JarOutputStream
name|jar1
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|lib
argument_list|,
literal|"jar1.jar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|jar1
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"defaultSharedLibFile"
argument_list|)
argument_list|)
expr_stmt|;
name|jar1
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jar1
operator|.
name|close
argument_list|()
expr_stmt|;
name|File
name|customLib
init|=
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"customLib"
argument_list|)
decl_stmt|;
name|customLib
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|JarOutputStream
name|jar2
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|customLib
argument_list|,
literal|"jar2.jar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|jar2
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"customSharedLibFile"
argument_list|)
argument_list|)
expr_stmt|;
name|jar2
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jar2
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"default-lib-solr.xml"
argument_list|)
argument_list|,
literal|"<solr><cores/></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"explicit-lib-solr.xml"
argument_list|)
argument_list|,
literal|"<solr sharedLib=\"lib\"><cores/></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"custom-lib-solr.xml"
argument_list|)
argument_list|,
literal|"<solr sharedLib=\"customLib\"><cores/></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cc1
init|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|tmpRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"default-lib-solr.xml"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|cc1
operator|.
name|loader
operator|.
name|openResource
argument_list|(
literal|"defaultSharedLibFile"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc1
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|final
name|CoreContainer
name|cc2
init|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|tmpRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"explicit-lib-solr.xml"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|cc2
operator|.
name|loader
operator|.
name|openResource
argument_list|(
literal|"defaultSharedLibFile"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|final
name|CoreContainer
name|cc3
init|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|tmpRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"custom-lib-solr.xml"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|cc3
operator|.
name|loader
operator|.
name|openResource
argument_list|(
literal|"customSharedLibFile"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc3
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|EMPTY_SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_SOLR_XML
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
operator|+
literal|"<solr persistent=\"false\">\n"
operator|+
literal|"<cores adminPath=\"/admin/cores\" transientCacheSize=\"32\">\n"
operator|+
literal|"</cores>\n"
operator|+
literal|"</solr>"
decl_stmt|;
DECL|field|EMPTY_SOLR_XML2
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_SOLR_XML2
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
operator|+
literal|"<solr>\n"
operator|+
literal|"</solr>"
decl_stmt|;
DECL|field|CUSTOM_HANDLERS_SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|CUSTOM_HANDLERS_SOLR_XML
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
operator|+
literal|"<solr>"
operator|+
literal|"<str name=\"collectionsHandler\">"
operator|+
name|CustomCollectionsHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"</str>"
operator|+
literal|"<str name=\"infoHandler\">"
operator|+
name|CustomInfoHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"</str>"
operator|+
literal|"<str name=\"adminHandler\">"
operator|+
name|CustomCoreAdminHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"</str>"
operator|+
literal|"</solr>"
decl_stmt|;
DECL|class|CustomCollectionsHandler
specifier|public
specifier|static
class|class
name|CustomCollectionsHandler
extends|extends
name|CollectionsHandler
block|{
DECL|method|CustomCollectionsHandler
specifier|public
name|CustomCollectionsHandler
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|super
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CustomInfoHandler
specifier|public
specifier|static
class|class
name|CustomInfoHandler
extends|extends
name|InfoHandler
block|{
DECL|method|CustomInfoHandler
specifier|public
name|CustomInfoHandler
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|super
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CustomCoreAdminHandler
specifier|public
specifier|static
class|class
name|CustomCoreAdminHandler
extends|extends
name|CoreAdminHandler
block|{
DECL|method|CustomCoreAdminHandler
specifier|public
name|CustomCoreAdminHandler
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|super
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCustomHandlers
specifier|public
name|void
name|testCustomHandlers
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
decl_stmt|;
name|ConfigSolr
name|config
init|=
name|ConfigSolr
operator|.
name|fromString
argument_list|(
name|loader
argument_list|,
name|CUSTOM_HANDLERS_SOLR_XML
argument_list|)
decl_stmt|;
name|CoreContainer
name|cc
init|=
operator|new
name|CoreContainer
argument_list|(
name|loader
argument_list|,
name|config
argument_list|)
decl_stmt|;
try|try
block|{
name|cc
operator|.
name|load
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cc
operator|.
name|getCollectionsHandler
argument_list|()
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|CustomCollectionsHandler
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cc
operator|.
name|getInfoHandler
argument_list|()
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|CustomInfoHandler
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cc
operator|.
name|getMultiCoreHandler
argument_list|()
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|CustomCoreAdminHandler
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

