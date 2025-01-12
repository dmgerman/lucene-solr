begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|analysis
operator|.
name|Analyzer
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
name|charfilter
operator|.
name|MappingCharFilterFactory
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
name|core
operator|.
name|LowerCaseFilterFactory
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
name|WhitespaceTokenizerFactory
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
name|miscellaneous
operator|.
name|ASCIIFoldingFilterFactory
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
name|miscellaneous
operator|.
name|TrimFilterFactory
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
name|analysis
operator|.
name|TokenizerChain
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
DECL|class|MultiTermTest
specifier|public
class|class
name|MultiTermTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-folding.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiFound
specifier|public
name|void
name|testMultiFound
parameter_list|()
block|{
name|SchemaField
name|field
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getField
argument_list|(
literal|"content_multi"
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|(
operator|(
name|TextField
operator|)
name|field
operator|.
name|getType
argument_list|()
operator|)
operator|.
name|getMultiTermAnalyzer
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|analyzer
operator|instanceof
name|TokenizerChain
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|TokenizerChain
operator|)
name|analyzer
operator|)
operator|.
name|getTokenizerFactory
argument_list|()
operator|instanceof
name|WhitespaceTokenizerFactory
argument_list|)
expr_stmt|;
name|TokenizerChain
name|tc
init|=
operator|(
name|TokenizerChain
operator|)
name|analyzer
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|factory
range|:
name|tc
operator|.
name|getTokenFilterFactories
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
operator|(
name|factory
operator|instanceof
name|ASCIIFoldingFilterFactory
operator|)
operator|||
operator|(
name|factory
operator|instanceof
name|LowerCaseFilterFactory
operator|)
argument_list|)
expr_stmt|;
block|}
name|analyzer
operator|=
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getIndexAnalyzer
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|analyzer
operator|instanceof
name|TokenizerChain
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|TokenizerChain
operator|)
name|analyzer
operator|)
operator|.
name|getTokenizerFactory
argument_list|()
operator|instanceof
name|WhitespaceTokenizerFactory
argument_list|)
expr_stmt|;
name|tc
operator|=
operator|(
name|TokenizerChain
operator|)
name|analyzer
expr_stmt|;
for|for
control|(
name|TokenFilterFactory
name|factory
range|:
name|tc
operator|.
name|getTokenFilterFactories
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
operator|(
name|factory
operator|instanceof
name|ASCIIFoldingFilterFactory
operator|)
operator|||
operator|(
name|factory
operator|instanceof
name|TrimFilterFactory
operator|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tc
operator|.
name|getCharFilterFactories
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryCopiedToMulti
specifier|public
name|void
name|testQueryCopiedToMulti
parameter_list|()
block|{
name|SchemaField
name|field
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getField
argument_list|(
literal|"content_charfilter"
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|(
operator|(
name|TextField
operator|)
name|field
operator|.
name|getType
argument_list|()
operator|)
operator|.
name|getMultiTermAnalyzer
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|analyzer
operator|instanceof
name|TokenizerChain
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|TokenizerChain
operator|)
name|analyzer
operator|)
operator|.
name|getTokenizerFactory
argument_list|()
operator|instanceof
name|KeywordTokenizerFactory
argument_list|)
expr_stmt|;
name|TokenizerChain
name|tc
init|=
operator|(
name|TokenizerChain
operator|)
name|analyzer
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|factory
range|:
name|tc
operator|.
name|getTokenFilterFactories
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|factory
operator|instanceof
name|LowerCaseFilterFactory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tc
operator|.
name|getCharFilterFactories
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tc
operator|.
name|getCharFilterFactories
argument_list|()
index|[
literal|0
index|]
operator|instanceof
name|MappingCharFilterFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultCopiedToMulti
specifier|public
name|void
name|testDefaultCopiedToMulti
parameter_list|()
block|{
name|SchemaField
name|field
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getField
argument_list|(
literal|"content_ws"
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|(
operator|(
name|TextField
operator|)
name|field
operator|.
name|getType
argument_list|()
operator|)
operator|.
name|getMultiTermAnalyzer
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|analyzer
operator|instanceof
name|TokenizerChain
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|TokenizerChain
operator|)
name|analyzer
operator|)
operator|.
name|getTokenizerFactory
argument_list|()
operator|instanceof
name|KeywordTokenizerFactory
argument_list|)
expr_stmt|;
name|TokenizerChain
name|tc
init|=
operator|(
name|TokenizerChain
operator|)
name|analyzer
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|factory
range|:
name|tc
operator|.
name|getTokenFilterFactories
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
operator|(
name|factory
operator|instanceof
name|ASCIIFoldingFilterFactory
operator|)
operator|||
operator|(
name|factory
operator|instanceof
name|LowerCaseFilterFactory
operator|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tc
operator|.
name|getCharFilterFactories
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

