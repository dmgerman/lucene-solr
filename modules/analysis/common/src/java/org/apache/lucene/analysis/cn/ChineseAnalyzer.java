begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cn
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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

begin_comment
comment|// javadoc @link
end_comment

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
name|ReusableAnalyzerBase
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
name|Tokenizer
import|;
end_import

begin_comment
comment|/**  * An {@link Analyzer} that tokenizes text with {@link ChineseTokenizer} and  * filters with {@link ChineseFilter}  * @deprecated (3.1) Use {@link StandardAnalyzer} instead, which has the same functionality.  * This analyzer will be removed in Lucene 5.0  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|ChineseAnalyzer
specifier|public
specifier|final
class|class
name|ChineseAnalyzer
extends|extends
name|ReusableAnalyzerBase
block|{
comment|/**    * Creates    * {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents}    * used to tokenize all the text in the provided {@link Reader}.    *     * @return {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents}    *         built from a {@link ChineseTokenizer} filtered with    *         {@link ChineseFilter}    */
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|ChineseTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|ChineseFilter
argument_list|(
name|source
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

