begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.pattern
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pattern
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|regex
operator|.
name|Pattern
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
comment|/**  * Factory for {@link PatternCaptureGroupTokenFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_ptncapturegroup" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.KeywordTokenizerFactory"/&gt;  *&lt;filter class="solr.PatternCaptureGroupTokenFilterFactory" pattern="([^a-z])" preserve_original="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  * @see PatternCaptureGroupTokenFilter  */
end_comment

begin_class
DECL|class|PatternCaptureGroupFilterFactory
specifier|public
class|class
name|PatternCaptureGroupFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|pattern
specifier|private
name|Pattern
name|pattern
decl_stmt|;
DECL|field|preserveOriginal
specifier|private
name|boolean
name|preserveOriginal
init|=
literal|true
decl_stmt|;
DECL|method|PatternCaptureGroupFilterFactory
specifier|public
name|PatternCaptureGroupFilterFactory
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
name|pattern
operator|=
name|getPattern
argument_list|(
name|args
argument_list|,
literal|"pattern"
argument_list|)
expr_stmt|;
name|preserveOriginal
operator|=
name|args
operator|.
name|containsKey
argument_list|(
literal|"preserve_original"
argument_list|)
condition|?
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|"preserve_original"
argument_list|)
argument_list|)
else|:
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|PatternCaptureGroupTokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|PatternCaptureGroupTokenFilter
argument_list|(
name|input
argument_list|,
name|preserveOriginal
argument_list|,
name|pattern
argument_list|)
return|;
block|}
block|}
end_class

end_unit

