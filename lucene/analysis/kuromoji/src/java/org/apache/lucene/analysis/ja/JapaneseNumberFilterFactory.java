begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
package|;
end_package

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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link JapaneseNumberFilter}.  *<br>  *<pre class="prettyprint">  *&lt;fieldType name="text_ja" class="solr.TextField"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.JapaneseTokenizerFactory" discardPunctuation="false"/&gt;  *&lt;filter class="solr.JapaneseNumberFilter"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;  *</pre>  *<p>  * It is important that punctuation is not discarded by the tokenizer so use  * {@code discardPunctuation="false"} in your {@link JapaneseTokenizerFactory}.  */
end_comment

begin_class
DECL|class|JapaneseNumberFilterFactory
specifier|public
class|class
name|JapaneseNumberFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|method|JapaneseNumberFilterFactory
specifier|public
name|JapaneseNumberFilterFactory
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
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|JapaneseNumberFilter
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
end_class

end_unit

