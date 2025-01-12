begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package

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
name|Collection
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
name|CharTokenizer
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
import|import static
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
name|StandardTokenizer
operator|.
name|MAX_TOKEN_LENGTH_LIMIT
import|;
end_import

begin_comment
comment|/**  * Factory for {@link WhitespaceTokenizer}.   *<pre class="prettyprint">  *&lt;fieldType name="text_ws" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory" rule="unicode"  maxTokenLen="256"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  * Options:  *<ul>  *<li>rule: either "java" for {@link WhitespaceTokenizer}  *      or "unicode" for {@link UnicodeWhitespaceTokenizer}</li>  *<li>maxTokenLen: max token length, should be greater than 0 and less than MAX_TOKEN_LENGTH_LIMIT (1024*1024).  *       It is rare to need to change this  *      else {@link CharTokenizer}::DEFAULT_MAX_TOKEN_LEN</li>  *</ul>  */
end_comment

begin_class
DECL|class|WhitespaceTokenizerFactory
specifier|public
class|class
name|WhitespaceTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|RULE_JAVA
specifier|public
specifier|static
specifier|final
name|String
name|RULE_JAVA
init|=
literal|"java"
decl_stmt|;
DECL|field|RULE_UNICODE
specifier|public
specifier|static
specifier|final
name|String
name|RULE_UNICODE
init|=
literal|"unicode"
decl_stmt|;
DECL|field|RULE_NAMES
specifier|private
specifier|static
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|RULE_NAMES
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|RULE_JAVA
argument_list|,
name|RULE_UNICODE
argument_list|)
decl_stmt|;
DECL|field|rule
specifier|private
specifier|final
name|String
name|rule
decl_stmt|;
DECL|field|maxTokenLen
specifier|private
specifier|final
name|int
name|maxTokenLen
decl_stmt|;
comment|/** Creates a new WhitespaceTokenizerFactory */
DECL|method|WhitespaceTokenizerFactory
specifier|public
name|WhitespaceTokenizerFactory
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
name|rule
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"rule"
argument_list|,
name|RULE_NAMES
argument_list|,
name|RULE_JAVA
argument_list|)
expr_stmt|;
name|maxTokenLen
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"maxTokenLen"
argument_list|,
name|CharTokenizer
operator|.
name|DEFAULT_MAX_WORD_LEN
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxTokenLen
operator|>
name|MAX_TOKEN_LENGTH_LIMIT
operator|||
name|maxTokenLen
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTokenLen must be greater than 0 and less than "
operator|+
name|MAX_TOKEN_LENGTH_LIMIT
operator|+
literal|" passed: "
operator|+
name|maxTokenLen
argument_list|)
throw|;
block|}
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
switch|switch
condition|(
name|rule
condition|)
block|{
case|case
name|RULE_JAVA
case|:
return|return
operator|new
name|WhitespaceTokenizer
argument_list|(
name|factory
argument_list|,
name|maxTokenLen
argument_list|)
return|;
case|case
name|RULE_UNICODE
case|:
return|return
operator|new
name|UnicodeWhitespaceTokenizer
argument_list|(
name|factory
argument_list|,
name|maxTokenLen
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

