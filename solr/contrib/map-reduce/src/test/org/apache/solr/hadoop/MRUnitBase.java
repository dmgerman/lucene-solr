begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
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
name|IOException
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
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|hadoop
operator|.
name|morphline
operator|.
name|MorphlineMapRunner
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
name|ExternalPaths
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

begin_class
DECL|class|MRUnitBase
specifier|public
specifier|abstract
class|class
name|MRUnitBase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|RESOURCES_DIR
specifier|protected
specifier|static
specifier|final
name|String
name|RESOURCES_DIR
init|=
name|ExternalPaths
operator|.
name|SOURCE_HOME
operator|+
literal|"/contrib/map-reduce/src/test-files"
decl_stmt|;
DECL|field|DOCUMENTS_DIR
specifier|protected
specifier|static
specifier|final
name|String
name|DOCUMENTS_DIR
init|=
name|RESOURCES_DIR
operator|+
literal|"/test-documents"
decl_stmt|;
DECL|field|solrHomeZip
specifier|protected
specifier|static
name|File
name|solrHomeZip
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHomeZip
operator|=
name|SolrOutputFormat
operator|.
name|createSolrHomeZip
argument_list|(
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/solr/mrunit"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|solrHomeZip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownClass
specifier|public
specifier|static
name|void
name|teardownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHomeZip
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|method|setupHadoopConfig
specifier|protected
name|void
name|setupHadoopConfig
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|tempDir
init|=
name|TEMP_DIR
operator|+
literal|"/test-morphlines-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
operator|new
name|File
argument_list|(
name|tempDir
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/custom-mimetypes.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|tempDir
operator|+
literal|"/custom-mimetypes.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|setupMorphline
argument_list|(
name|tempDir
argument_list|,
literal|"test-morphlines/solrCellDocumentTypes"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|MorphlineMapRunner
operator|.
name|MORPHLINE_FILE_PARAM
argument_list|,
name|tempDir
operator|+
literal|"/test-morphlines/solrCellDocumentTypes.conf"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|SolrOutputFormat
operator|.
name|ZIP_NAME
argument_list|,
name|solrHomeZip
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setupMorphline
specifier|public
specifier|static
name|void
name|setupMorphline
parameter_list|(
name|String
name|tempDir
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|morphlineText
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/"
operator|+
name|file
operator|+
literal|".conf"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|morphlineText
operator|=
name|morphlineText
operator|.
name|replaceAll
argument_list|(
literal|"RESOURCES_DIR"
argument_list|,
operator|new
name|File
argument_list|(
name|tempDir
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|morphlineText
operator|=
name|morphlineText
operator|.
name|replaceAll
argument_list|(
literal|"\\$\\{SOLR_LOCATOR\\}"
argument_list|,
literal|"{ collection : collection1 }"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tempDir
operator|+
literal|"/"
operator|+
name|file
operator|+
literal|".conf"
argument_list|)
argument_list|,
name|morphlineText
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

