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
name|commongrams
operator|.
name|CommonGramsFilter
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
name|core
operator|.
name|StopAnalyzer
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

begin_comment
comment|/**  * Constructs a {@link CommonGramsFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_cmmngrms" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.CommonGramsFilterFactory" words="commongramsstopwords.txt" ignoreCase="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment

begin_comment
comment|/*  * This is pretty close to a straight copy from StopFilterFactory  */
end_comment

begin_class
DECL|class|CommonGramsFilterFactory
specifier|public
class|class
name|CommonGramsFilterFactory
extends|extends
name|BaseTokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
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
name|commonWordFiles
init|=
name|args
operator|.
name|get
argument_list|(
literal|"words"
argument_list|)
decl_stmt|;
name|ignoreCase
operator|=
name|getBoolean
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|commonWordFiles
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
literal|"snowball"
operator|.
name|equalsIgnoreCase
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|"format"
argument_list|)
argument_list|)
condition|)
block|{
name|commonWords
operator|=
name|getSnowballWordSet
argument_list|(
name|loader
argument_list|,
name|commonWordFiles
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commonWords
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|commonWordFiles
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|commonWords
operator|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
expr_stmt|;
block|}
block|}
comment|//Force the use of a char array set, as it is the most performant, although this may break things if Lucene ever goes away from it.  See SOLR-1095
DECL|field|commonWords
specifier|private
name|CharArraySet
name|commonWords
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
DECL|method|isIgnoreCase
specifier|public
name|boolean
name|isIgnoreCase
parameter_list|()
block|{
return|return
name|ignoreCase
return|;
block|}
DECL|method|getCommonWords
specifier|public
name|CharArraySet
name|getCommonWords
parameter_list|()
block|{
return|return
name|commonWords
return|;
block|}
DECL|method|create
specifier|public
name|CommonGramsFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|CommonGramsFilter
name|commonGrams
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
return|return
name|commonGrams
return|;
block|}
block|}
end_class

end_unit

