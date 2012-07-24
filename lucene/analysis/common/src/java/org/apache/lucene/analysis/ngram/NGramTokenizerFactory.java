begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ngram
operator|.
name|NGramTokenizer
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
name|TokenizerFactory
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory for {@link NGramTokenizer}.  *<pre class="prettyprint">  *&lt;fieldType name="text_ngrm" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.NGramTokenizerFactory" minGramSize="1" maxGramSize="2"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>   *  */
end_comment

begin_class
DECL|class|NGramTokenizerFactory
specifier|public
class|class
name|NGramTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|maxGramSize
specifier|private
name|int
name|maxGramSize
init|=
literal|0
decl_stmt|;
DECL|field|minGramSize
specifier|private
name|int
name|minGramSize
init|=
literal|0
decl_stmt|;
comment|/** Initializes the n-gram min and max sizes and the side from which one should start tokenizing. */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
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
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|maxArg
init|=
name|args
operator|.
name|get
argument_list|(
literal|"maxGramSize"
argument_list|)
decl_stmt|;
name|maxGramSize
operator|=
operator|(
name|maxArg
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|maxArg
argument_list|)
else|:
name|NGramTokenizer
operator|.
name|DEFAULT_MAX_NGRAM_SIZE
operator|)
expr_stmt|;
name|String
name|minArg
init|=
name|args
operator|.
name|get
argument_list|(
literal|"minGramSize"
argument_list|)
decl_stmt|;
name|minGramSize
operator|=
operator|(
name|minArg
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|minArg
argument_list|)
else|:
name|NGramTokenizer
operator|.
name|DEFAULT_MIN_NGRAM_SIZE
operator|)
expr_stmt|;
block|}
comment|/** Creates the {@link TokenStream} of n-grams from the given {@link Reader}. */
DECL|method|create
specifier|public
name|NGramTokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|NGramTokenizer
argument_list|(
name|input
argument_list|,
name|minGramSize
argument_list|,
name|maxGramSize
argument_list|)
return|;
block|}
block|}
end_class

end_unit

