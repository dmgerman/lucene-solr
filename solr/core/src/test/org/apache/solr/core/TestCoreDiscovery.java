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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|SolrException
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

begin_class
DECL|class|TestCoreDiscovery
specifier|public
class|class
name|TestCoreDiscovery
extends|extends
name|SolrTestCaseJ4
block|{
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
name|initCore
argument_list|()
expr_stmt|;
block|}
DECL|field|solrHomeDirectory
specifier|private
specifier|final
name|File
name|solrHomeDirectory
init|=
name|createTempDir
argument_list|()
decl_stmt|;
DECL|method|setMeUp
specifier|private
name|void
name|setMeUp
parameter_list|(
name|String
name|alternateCoreDir
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xmlStr
init|=
name|SOLR_XML
decl_stmt|;
if|if
condition|(
name|alternateCoreDir
operator|!=
literal|null
condition|)
block|{
name|xmlStr
operator|=
name|xmlStr
operator|.
name|replace
argument_list|(
literal|"<solr>"
argument_list|,
literal|"<solr><str name=\"coreRootDirectory\">"
operator|+
name|alternateCoreDir
operator|+
literal|"</str> "
argument_list|)
expr_stmt|;
block|}
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|ConfigSolr
operator|.
name|SOLR_XML_FILE
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|tmpFile
argument_list|,
name|xmlStr
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setMeUp
specifier|private
name|void
name|setMeUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|makeCorePropFile
specifier|private
name|Properties
name|makeCorePropFile
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isLazy
parameter_list|,
name|boolean
name|loadOnStartup
parameter_list|,
name|String
modifier|...
name|extraProps
parameter_list|)
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_SCHEMA
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_CONFIG
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_TRANSIENT
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|isLazy
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_LOADONSTARTUP
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|loadOnStartup
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_DATADIR
argument_list|,
literal|"${core.dataDir:stuffandnonsense}"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_INSTDIR
argument_list|,
literal|"totallybogus"
argument_list|)
expr_stmt|;
comment|// For testing that this property is ignored if present.
for|for
control|(
name|String
name|extra
range|:
name|extraProps
control|)
block|{
name|String
index|[]
name|parts
init|=
name|extra
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
DECL|method|addCoreWithProps
specifier|private
name|void
name|addCoreWithProps
parameter_list|(
name|Properties
name|stockProps
parameter_list|,
name|File
name|propFile
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|propFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
name|propFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Writer
name|out
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|propFile
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
decl_stmt|;
try|try
block|{
name|stockProps
operator|.
name|store
argument_list|(
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|addConfFiles
argument_list|(
operator|new
name|File
argument_list|(
name|propFile
operator|.
name|getParent
argument_list|()
argument_list|,
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addCoreWithProps
specifier|private
name|void
name|addCoreWithProps
parameter_list|(
name|String
name|name
parameter_list|,
name|Properties
name|stockProps
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|name
argument_list|)
argument_list|,
name|CorePropertiesLocator
operator|.
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
name|File
name|parent
init|=
name|propFile
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to mkdirs for "
operator|+
name|parent
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|parent
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|stockProps
argument_list|,
name|propFile
argument_list|)
expr_stmt|;
block|}
DECL|method|addConfFiles
specifier|private
name|void
name|addConfFiles
parameter_list|(
name|File
name|confDir
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|top
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to mkdirs for "
operator|+
name|confDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|confDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|CoreContainer
name|init
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cores
init|=
operator|new
name|CoreContainer
argument_list|()
decl_stmt|;
name|cores
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|cores
return|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{    }
comment|// Test the basic setup, create some dirs with core.properties files in them, but solr.xml has discoverCores
comment|// set and insure that we find all the cores and can load them.
annotation|@
name|Test
DECL|method|testDiscovery
specifier|public
name|void
name|testDiscovery
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|()
expr_stmt|;
comment|// name, isLazy, loadOnStartup
name|addCoreWithProps
argument_list|(
literal|"core1"
argument_list|,
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|"dataDir=core1"
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
literal|"core2"
argument_list|,
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|"dataDir=core2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// I suspect what we're adding in here is a "configset" rather than a schema or solrconfig.
comment|//
name|addCoreWithProps
argument_list|(
literal|"lazy1"
argument_list|,
name|makeCorePropFile
argument_list|(
literal|"lazy1"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|"dataDir=lazy1"
argument_list|)
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|ConfigSolrXmlOld
operator|.
name|DEFAULT_DEFAULT_CORE_NAME
argument_list|,
name|cc
operator|.
name|getDefaultCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|TestLazyCores
operator|.
name|checkInCores
argument_list|(
name|cc
argument_list|,
literal|"core1"
argument_list|)
expr_stmt|;
name|TestLazyCores
operator|.
name|checkNotInCores
argument_list|(
name|cc
argument_list|,
literal|"lazy1"
argument_list|,
literal|"core2"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
comment|// force loading of core2 and lazy1 by getting them from the CoreContainer
try|try
init|(
name|SolrCore
name|core1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
init|;
name|SolrCore
name|core2
operator|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core2"
argument_list|)
init|;
name|SolrCore
name|lazy1
operator|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"lazy1"
argument_list|)
init|)
block|{
comment|// Let's assert we did the right thing for implicit properties too.
name|CoreDescriptor
name|desc
init|=
name|core1
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"core1"
argument_list|,
name|desc
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// This is too long and ugly to put in. Besides, it varies.
name|assertNotNull
argument_list|(
name|desc
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
expr_stmt|;
comment|// Prove we're ignoring this even though it's set in the properties file
name|assertFalse
argument_list|(
literal|"InstanceDir should be ignored"
argument_list|,
name|desc
operator|.
name|getInstanceDir
argument_list|()
operator|.
name|contains
argument_list|(
literal|"totallybogus"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"core1"
argument_list|,
name|desc
operator|.
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solrconfig-minimal.xml"
argument_list|,
name|desc
operator|.
name|getConfigName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"schema-tiny.xml"
argument_list|,
name|desc
operator|.
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
name|TestLazyCores
operator|.
name|checkInCores
argument_list|(
name|cc
argument_list|,
literal|"core1"
argument_list|,
literal|"core2"
argument_list|,
literal|"lazy1"
argument_list|)
expr_stmt|;
block|}
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
DECL|method|testDuplicateNames
specifier|public
name|void
name|testDuplicateNames
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|()
expr_stmt|;
comment|// name, isLazy, loadOnStartup
name|addCoreWithProps
argument_list|(
literal|"core1"
argument_list|,
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
literal|"core2"
argument_list|,
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|"name=core1"
argument_list|)
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cc
operator|=
name|init
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception in testDuplicateNames"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|String
name|message
init|=
name|se
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong exception thrown on duplicate core names"
argument_list|,
name|message
operator|.
name|indexOf
argument_list|(
literal|"Found multiple cores with the name [core1]"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|File
operator|.
name|separator
operator|+
literal|"core1 should have been mentioned in the message: "
operator|+
name|message
argument_list|,
name|message
operator|.
name|indexOf
argument_list|(
name|File
operator|.
name|separator
operator|+
literal|"core1"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|File
operator|.
name|separator
operator|+
literal|"core2 should have been mentioned in the message:"
operator|+
name|message
argument_list|,
name|message
operator|.
name|indexOf
argument_list|(
name|File
operator|.
name|separator
operator|+
literal|"core2"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAlternateCoreDir
specifier|public
name|void
name|testAlternateCoreDir
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|alt
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|setMeUp
argument_list|(
name|alt
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|"dataDir=core1"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|alt
argument_list|,
literal|"core1"
operator|+
name|File
operator|.
name|separator
operator|+
name|CorePropertiesLocator
operator|.
name|PROPERTIES_FILENAME
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|"dataDir=core2"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|alt
argument_list|,
literal|"core2"
operator|+
name|File
operator|.
name|separator
operator|+
name|CorePropertiesLocator
operator|.
name|PROPERTIES_FILENAME
argument_list|)
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
try|try
init|(
name|SolrCore
name|core1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
init|;
name|SolrCore
name|core2
operator|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core2"
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|core1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|core2
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
DECL|method|testNoCoreDir
specifier|public
name|void
name|testNoCoreDir
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|noCoreDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|setMeUp
argument_list|(
name|noCoreDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|noCoreDir
argument_list|,
literal|"core1"
operator|+
name|File
operator|.
name|separator
operator|+
name|CorePropertiesLocator
operator|.
name|PROPERTIES_FILENAME
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|noCoreDir
argument_list|,
literal|"core2"
operator|+
name|File
operator|.
name|separator
operator|+
name|CorePropertiesLocator
operator|.
name|PROPERTIES_FILENAME
argument_list|)
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
try|try
init|(
name|SolrCore
name|core1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
init|;
name|SolrCore
name|core2
operator|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core2"
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|core1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|core2
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
comment|// For testing whether finding a solr.xml overrides looking at solr.properties
DECL|field|SOLR_XML
specifier|private
specifier|final
specifier|static
name|String
name|SOLR_XML
init|=
literal|"<solr> "
operator|+
literal|"<int name=\"transientCacheSize\">2</int> "
operator|+
literal|"<solrcloud> "
operator|+
literal|"<str name=\"hostContext\">solrprop</str> "
operator|+
literal|"<int name=\"zkClientTimeout\">20</int> "
operator|+
literal|"<str name=\"host\">222.333.444.555</str> "
operator|+
literal|"<int name=\"hostPort\">6000</int>  "
operator|+
literal|"</solrcloud> "
operator|+
literal|"</solr>"
decl_stmt|;
block|}
end_class

end_unit

