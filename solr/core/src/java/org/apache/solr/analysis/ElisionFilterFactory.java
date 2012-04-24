begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|ResourceLoader
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
name|util
operator|.
name|plugin
operator|.
name|ResourceLoaderAware
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
name|fr
operator|.
name|*
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
name|CharArraySet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Factory for {@link ElisionFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_elsn" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;  *&lt;filter class="solr.LowerCaseFilterFactory"/&gt;  *&lt;filter class="solr.ElisionFilterFactory"   *       articles="stopwordarticles.txt" ignoreCase="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment

begin_class
DECL|class|ElisionFilterFactory
specifier|public
class|class
name|ElisionFilterFactory
extends|extends
name|BaseTokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|articles
specifier|private
name|CharArraySet
name|articles
decl_stmt|;
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|String
name|articlesFile
init|=
name|args
operator|.
name|get
argument_list|(
literal|"articles"
argument_list|)
decl_stmt|;
name|boolean
name|ignoreCase
init|=
name|getBoolean
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|articlesFile
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|articles
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|articlesFile
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"IOException thrown while loading articles"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|create
specifier|public
name|ElisionFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
return|return
name|articles
operator|==
literal|null
condition|?
operator|new
name|ElisionFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|)
else|:
operator|new
name|ElisionFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|,
name|articles
argument_list|)
return|;
block|}
block|}
end_class

end_unit

