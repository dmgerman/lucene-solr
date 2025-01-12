begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.charfilter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|charfilter
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
name|util
operator|.
name|CharFilterFactory
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * Factory for {@link HTMLStripCharFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_html" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;charFilter class="solr.HTMLStripCharFilterFactory" escapedTags="a, title" /&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment

begin_class
DECL|class|HTMLStripCharFilterFactory
specifier|public
class|class
name|HTMLStripCharFilterFactory
extends|extends
name|CharFilterFactory
block|{
DECL|field|escapedTags
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|escapedTags
decl_stmt|;
DECL|field|TAG_NAME_PATTERN
specifier|static
specifier|final
name|Pattern
name|TAG_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^\\s,]+"
argument_list|)
decl_stmt|;
comment|/** Creates a new HTMLStripCharFilterFactory */
DECL|method|HTMLStripCharFilterFactory
specifier|public
name|HTMLStripCharFilterFactory
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
name|escapedTags
operator|=
name|getSet
argument_list|(
name|args
argument_list|,
literal|"escapedTags"
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
name|HTMLStripCharFilter
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|HTMLStripCharFilter
name|charFilter
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|escapedTags
condition|)
block|{
name|charFilter
operator|=
operator|new
name|HTMLStripCharFilter
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|charFilter
operator|=
operator|new
name|HTMLStripCharFilter
argument_list|(
name|input
argument_list|,
name|escapedTags
argument_list|)
expr_stmt|;
block|}
return|return
name|charFilter
return|;
block|}
block|}
end_class

end_unit

