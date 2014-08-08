begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
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
name|MockAnalyzer
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
name|Tokenizer
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
name|KeywordTokenizer
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
name|EdgeNGramTokenFilter
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
name|reverse
operator|.
name|ReverseStringFilter
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
name|Term
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
name|TermQuery
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
name|BytesRef
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link SimpleNaiveBayesClassifier}  */
end_comment

begin_comment
comment|// TODO : eventually remove this if / when fallback methods exist for all un-supportable codec methods (see LUCENE-4872)
end_comment

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
literal|"Lucene3x"
argument_list|)
DECL|class|SimpleNaiveBayesClassifierTest
specifier|public
class|class
name|SimpleNaiveBayesClassifierTest
extends|extends
name|ClassificationTestBase
argument_list|<
name|BytesRef
argument_list|>
block|{
annotation|@
name|Test
DECL|method|testBasicUsage
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|checkCorrectClassification
argument_list|(
operator|new
name|SimpleNaiveBayesClassifier
argument_list|()
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
name|TECHNOLOGY_RESULT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|textFieldName
argument_list|,
name|categoryFieldName
argument_list|)
expr_stmt|;
name|checkCorrectClassification
argument_list|(
operator|new
name|SimpleNaiveBayesClassifier
argument_list|()
argument_list|,
name|POLITICS_INPUT
argument_list|,
name|POLITICS_RESULT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|textFieldName
argument_list|,
name|categoryFieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicUsageWithQuery
specifier|public
name|void
name|testBasicUsageWithQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|checkCorrectClassification
argument_list|(
operator|new
name|SimpleNaiveBayesClassifier
argument_list|()
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
name|TECHNOLOGY_RESULT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|textFieldName
argument_list|,
name|categoryFieldName
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|textFieldName
argument_list|,
literal|"it"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNGramUsage
specifier|public
name|void
name|testNGramUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|checkCorrectClassification
argument_list|(
operator|new
name|SimpleNaiveBayesClassifier
argument_list|()
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
name|TECHNOLOGY_RESULT
argument_list|,
operator|new
name|NGramAnalyzer
argument_list|()
argument_list|,
name|textFieldName
argument_list|,
name|categoryFieldName
argument_list|)
expr_stmt|;
block|}
DECL|class|NGramAnalyzer
specifier|private
class|class
name|NGramAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|ReverseStringFilter
argument_list|(
operator|new
name|EdgeNGramTokenFilter
argument_list|(
operator|new
name|ReverseStringFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPerformance
specifier|public
name|void
name|testPerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|checkPerformance
argument_list|(
operator|new
name|SimpleNaiveBayesClassifier
argument_list|()
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|categoryFieldName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

