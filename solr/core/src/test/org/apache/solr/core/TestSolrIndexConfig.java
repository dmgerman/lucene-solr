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
name|IndexWriterConfig
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
name|LiveIndexWriterConfig
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
name|RefCounted
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
name|RandomMergePolicy
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
name|update
operator|.
name|LoggingInfoStream
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
DECL|class|TestSolrIndexConfig
specifier|public
class|class
name|TestSolrIndexConfig
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
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"solrconfig-indexconfig.xml"
else|:
literal|"solrconfig-indexconfig-mergepolicyfactory.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLiveWriter
specifier|public
name|void
name|testLiveWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|iw
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|getIndexWriter
argument_list|(
name|core
argument_list|)
decl_stmt|;
try|try
block|{
name|checkIndexWriterConfig
argument_list|(
name|iw
operator|.
name|get
argument_list|()
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|iw
condition|)
name|iw
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testIndexConfigParsing
specifier|public
name|void
name|testIndexConfigParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriterConfig
name|iwc
init|=
name|solrConfig
operator|.
name|indexConfig
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|checkIndexWriterConfig
argument_list|(
name|iwc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|iwc
operator|.
name|getInfoStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkIndexWriterConfig
specifier|private
name|void
name|checkIndexWriterConfig
parameter_list|(
name|LiveIndexWriterConfig
name|iwc
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|iwc
operator|.
name|getInfoStream
argument_list|()
operator|instanceof
name|LoggingInfoStream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|iwc
operator|.
name|getMergePolicy
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|RandomMergePolicy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

