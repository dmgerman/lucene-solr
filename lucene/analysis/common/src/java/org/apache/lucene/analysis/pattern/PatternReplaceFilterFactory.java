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
name|pattern
operator|.
name|PatternReplaceFilter
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
name|InitializationException
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
import|;
end_import

begin_comment
comment|/**  * Factory for {@link PatternReplaceFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_ptnreplace" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.KeywordTokenizerFactory"/&gt;  *&lt;filter class="solr.PatternReplaceFilterFactory" pattern="([^a-z])" replacement=""  *             replace="all"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  * @see PatternReplaceFilter  */
end_comment

begin_class
DECL|class|PatternReplaceFilterFactory
specifier|public
class|class
name|PatternReplaceFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|p
name|Pattern
name|p
decl_stmt|;
DECL|field|replacement
name|String
name|replacement
decl_stmt|;
DECL|field|all
name|boolean
name|all
init|=
literal|true
decl_stmt|;
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
name|p
operator|=
name|getPattern
argument_list|(
literal|"pattern"
argument_list|)
expr_stmt|;
name|replacement
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"replacement"
argument_list|)
expr_stmt|;
name|String
name|r
init|=
name|args
operator|.
name|get
argument_list|(
literal|"replace"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|r
condition|)
block|{
if|if
condition|(
name|r
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
condition|)
block|{
name|all
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|r
operator|.
name|equals
argument_list|(
literal|"first"
argument_list|)
condition|)
block|{
name|all
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Configuration Error: 'replace' must be 'first' or 'all' in "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|create
specifier|public
name|PatternReplaceFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|PatternReplaceFilter
argument_list|(
name|input
argument_list|,
name|p
argument_list|,
name|replacement
argument_list|,
name|all
argument_list|)
return|;
block|}
block|}
end_class

end_unit

