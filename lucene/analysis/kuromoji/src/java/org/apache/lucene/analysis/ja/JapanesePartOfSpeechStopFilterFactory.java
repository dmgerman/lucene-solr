begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|ja
operator|.
name|JapanesePartOfSpeechStopFilter
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
name|*
import|;
end_import

begin_comment
comment|/**  * Factory for {@link org.apache.lucene.analysis.ja.JapanesePartOfSpeechStopFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_ja" class="solr.TextField"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.JapaneseTokenizerFactory"/&gt;  *&lt;filter class="solr.JapanesePartOfSpeechStopFilterFactory"  *             tags="stopTags.txt"   *             enablePositionIncrements="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;  *</pre>  */
end_comment

begin_class
DECL|class|JapanesePartOfSpeechStopFilterFactory
specifier|public
class|class
name|JapanesePartOfSpeechStopFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
decl_stmt|;
DECL|field|stopTags
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|stopTags
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
name|stopTagFiles
init|=
name|args
operator|.
name|get
argument_list|(
literal|"tags"
argument_list|)
decl_stmt|;
name|enablePositionIncrements
operator|=
name|getBoolean
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|stopTags
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|CharArraySet
name|cas
init|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|stopTagFiles
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|cas
operator|!=
literal|null
condition|)
block|{
name|stopTags
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|element
range|:
name|cas
control|)
block|{
name|char
name|chars
index|[]
init|=
operator|(
name|char
index|[]
operator|)
name|element
decl_stmt|;
name|stopTags
operator|.
name|add
argument_list|(
operator|new
name|String
argument_list|(
name|chars
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"IOException thrown while loading tags"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|stream
parameter_list|)
block|{
comment|// if stoptags is null, it means the file is empty
return|return
name|stopTags
operator|==
literal|null
condition|?
name|stream
else|:
operator|new
name|JapanesePartOfSpeechStopFilter
argument_list|(
name|enablePositionIncrements
argument_list|,
name|stream
argument_list|,
name|stopTags
argument_list|)
return|;
block|}
block|}
end_class

end_unit

