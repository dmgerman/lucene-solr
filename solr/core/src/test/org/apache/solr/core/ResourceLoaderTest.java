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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharacterCodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|core
operator|.
name|KeywordTokenizerFactory
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
name|ngram
operator|.
name|NGramFilterFactory
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
name|util
operator|.
name|ResourceLoaderAware
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
name|util
operator|.
name|TokenFilterFactory
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
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
operator|.
name|LukeRequestHandler
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
name|component
operator|.
name|FacetComponent
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
name|response
operator|.
name|JSONResponseWriter
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
name|SolrCoreAware
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
operator|.
name|assertAwareCompatibility
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
name|Is
operator|.
name|is
import|;
end_import

begin_class
DECL|class|ResourceLoaderTest
specifier|public
class|class
name|ResourceLoaderTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testInstanceDir
specifier|public
name|void
name|testInstanceDir
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|()
init|)
block|{
name|assertThat
argument_list|(
name|loader
operator|.
name|getInstancePath
argument_list|()
argument_list|,
name|is
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEscapeInstanceDir
specifier|public
name|void
name|testEscapeInstanceDir
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|temp
init|=
name|createTempDir
argument_list|(
literal|"testEscapeInstanceDir"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|temp
operator|.
name|resolve
argument_list|(
literal|"dummy.txt"
argument_list|)
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
expr_stmt|;
name|Path
name|instanceDir
init|=
name|temp
operator|.
name|resolve
argument_list|(
literal|"instance"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|instanceDir
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|instanceDir
argument_list|)
init|)
block|{
name|loader
operator|.
name|openResource
argument_list|(
literal|"../../dummy.txt"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is outside resource loader dir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAwareCompatibility
specifier|public
name|void
name|testAwareCompatibility
parameter_list|()
throws|throws
name|Exception
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|ResourceLoaderAware
operator|.
name|class
decl_stmt|;
comment|// Check ResourceLoaderAware valid objects
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|NGramFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|KeywordTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure it throws an error for invalid objects
name|Object
index|[]
name|invalid
init|=
operator|new
name|Object
index|[]
block|{
comment|// new NGramTokenFilter( null ),
literal|"hello"
block|,
operator|new
name|Float
argument_list|(
literal|12.3f
argument_list|)
block|,
operator|new
name|LukeRequestHandler
argument_list|()
block|,
operator|new
name|JSONResponseWriter
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|invalid
control|)
block|{
try|try
block|{
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
name|obj
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should be invalid class: "
operator|+
name|obj
operator|+
literal|" FOR "
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// OK
block|}
name|clazz
operator|=
name|SolrCoreAware
operator|.
name|class
expr_stmt|;
comment|// Check ResourceLoaderAware valid objects
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|LukeRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|FacetComponent
argument_list|()
argument_list|)
expr_stmt|;
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
operator|new
name|JSONResponseWriter
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure it throws an error for invalid objects
name|invalid
operator|=
operator|new
name|Object
index|[]
block|{
operator|new
name|NGramFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
block|,
literal|"hello"
block|,
operator|new
name|Float
argument_list|(
literal|12.3f
argument_list|)
block|,
operator|new
name|KeywordTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
block|}
expr_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|invalid
control|)
block|{
try|try
block|{
name|assertAwareCompatibility
argument_list|(
name|clazz
argument_list|,
name|obj
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should be invalid class: "
operator|+
name|obj
operator|+
literal|" FOR "
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// OK
block|}
block|}
DECL|method|testBOMMarkers
specifier|public
name|void
name|testBOMMarkers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|fileWithBom
init|=
literal|"stopwithbom.txt"
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// preliminary sanity check
name|InputStream
name|bomStream
init|=
name|loader
operator|.
name|openResource
argument_list|(
name|fileWithBom
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|bomExpected
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|17
block|,
operator|-
literal|69
block|,
operator|-
literal|65
block|}
decl_stmt|;
specifier|final
name|byte
index|[]
name|firstBytes
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have been able to read 3 bytes from bomStream"
argument_list|,
literal|3
argument_list|,
name|bomStream
operator|.
name|read
argument_list|(
name|firstBytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"This test only works if "
operator|+
name|fileWithBom
operator|+
literal|" contains a BOM -- it appears someone removed it."
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|bomExpected
argument_list|,
name|firstBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|bomStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* IGNORE */
block|}
block|}
comment|// now make sure getLines skips the BOM...
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|fileWithBom
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lines
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BOMsAreEvil"
argument_list|,
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testWrongEncoding
specifier|public
name|void
name|testWrongEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|wrongEncoding
init|=
literal|"stopwordsWrongEncoding.txt"
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ensure we get our exception
try|try
block|{
name|loader
operator|.
name|getLines
argument_list|(
name|wrongEncoding
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CharacterCodingException
argument_list|)
expr_stmt|;
block|}
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testClassLoaderLibs
specifier|public
name|void
name|testClassLoaderLibs
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tmpRoot
init|=
name|createTempDir
argument_list|(
literal|"testClassLoaderLibs"
argument_list|)
decl_stmt|;
name|Path
name|lib
init|=
name|tmpRoot
operator|.
name|resolve
argument_list|(
literal|"lib"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|lib
argument_list|)
expr_stmt|;
try|try
init|(
name|JarOutputStream
name|os
init|=
operator|new
name|JarOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|lib
operator|.
name|resolve
argument_list|(
literal|"jar1.jar"
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|os
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"aLibFile"
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|Path
name|otherLib
init|=
name|tmpRoot
operator|.
name|resolve
argument_list|(
literal|"otherLib"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|otherLib
argument_list|)
expr_stmt|;
try|try
init|(
name|JarOutputStream
name|os
init|=
operator|new
name|JarOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|otherLib
operator|.
name|resolve
argument_list|(
literal|"jar2.jar"
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|os
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"explicitFile"
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|JarOutputStream
name|os
init|=
operator|new
name|JarOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|otherLib
operator|.
name|resolve
argument_list|(
literal|"jar3.jar"
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|os
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"otherFile"
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|tmpRoot
argument_list|)
decl_stmt|;
comment|// ./lib is accessible by default
name|assertNotNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"aLibFile"
argument_list|)
argument_list|)
expr_stmt|;
comment|// add inidividual jars from other paths
name|loader
operator|.
name|addToClassLoader
argument_list|(
name|otherLib
operator|.
name|resolve
argument_list|(
literal|"jar2.jar"
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"explicitFile"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"otherFile"
argument_list|)
argument_list|)
expr_stmt|;
comment|// add all jars from another path
name|loader
operator|.
name|addToClassLoader
argument_list|(
name|SolrResourceLoader
operator|.
name|getURLs
argument_list|(
name|otherLib
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"otherFile"
argument_list|)
argument_list|)
expr_stmt|;
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|class|DeprecatedTokenFilterFactory
specifier|public
specifier|static
specifier|final
class|class
name|DeprecatedTokenFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|method|DeprecatedTokenFilterFactory
specifier|public
name|DeprecatedTokenFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testLoadDeprecatedFactory
specifier|public
name|void
name|testLoadDeprecatedFactory
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
name|Paths
operator|.
name|get
argument_list|(
literal|"solr/collection1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ensure we get our exception
name|loader
operator|.
name|newInstance
argument_list|(
name|DeprecatedTokenFilterFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|TokenFilterFactory
operator|.
name|class
argument_list|,
literal|null
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Map
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|}
argument_list|)
expr_stmt|;
comment|// TODO: How to check that a warning was printed to log file?
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

