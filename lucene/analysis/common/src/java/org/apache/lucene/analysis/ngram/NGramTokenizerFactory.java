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
name|util
operator|.
name|TokenizerFactory
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
name|AttributeFactory
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
name|Version
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
comment|/**  * Factory for {@link NGramTokenizer}.  *<pre class="prettyprint">  *&lt;fieldType name="text_ngrm" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.NGramTokenizerFactory" minGramSize="1" maxGramSize="2"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
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
specifier|final
name|int
name|maxGramSize
decl_stmt|;
DECL|field|minGramSize
specifier|private
specifier|final
name|int
name|minGramSize
decl_stmt|;
comment|/** Creates a new NGramTokenizerFactory */
DECL|method|NGramTokenizerFactory
specifier|public
name|NGramTokenizerFactory
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
name|minGramSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"minGramSize"
argument_list|,
name|NGramTokenizer
operator|.
name|DEFAULT_MIN_NGRAM_SIZE
argument_list|)
expr_stmt|;
name|maxGramSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"maxGramSize"
argument_list|,
name|NGramTokenizer
operator|.
name|DEFAULT_MAX_NGRAM_SIZE
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
comment|/** Creates the {@link TokenStream} of n-grams from the given {@link Reader} and {@link AttributeFactory}. */
annotation|@
name|Override
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
if|if
condition|(
name|luceneMatchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_4
argument_list|)
condition|)
block|{
return|return
operator|new
name|NGramTokenizer
argument_list|(
name|luceneMatchVersion
argument_list|,
name|factory
argument_list|,
name|minGramSize
argument_list|,
name|maxGramSize
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Lucene43NGramTokenizer
argument_list|(
name|factory
argument_list|,
name|minGramSize
argument_list|,
name|maxGramSize
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

