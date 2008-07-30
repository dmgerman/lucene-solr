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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|StopFilter
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
name|TokenStream
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|File
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

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|StopFilterFactory
specifier|public
class|class
name|StopFilterFactory
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
name|stopWordFiles
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
name|enablePositionIncrements
operator|=
name|getBoolean
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopWordFiles
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|stopWords
operator|==
literal|null
condition|)
name|stopWords
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
try|try
block|{
name|java
operator|.
name|io
operator|.
name|File
name|keepWordsFile
init|=
operator|new
name|File
argument_list|(
name|stopWordFiles
argument_list|)
decl_stmt|;
if|if
condition|(
name|keepWordsFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|stopWordFiles
argument_list|)
decl_stmt|;
name|stopWords
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
operator|(
name|String
index|[]
operator|)
name|wlist
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|StrUtils
operator|.
name|splitFileNames
argument_list|(
name|stopWordFiles
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|stopWords
operator|.
name|addAll
argument_list|(
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
operator|(
name|String
index|[]
operator|)
name|wlist
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|ignoreCase
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|stopWords
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|stopWords
specifier|private
name|Set
name|stopWords
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
decl_stmt|;
DECL|method|isEnablePositionIncrements
specifier|public
name|boolean
name|isEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
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
DECL|method|getStopWords
specifier|public
name|Set
name|getStopWords
parameter_list|()
block|{
return|return
name|stopWords
return|;
block|}
DECL|method|create
specifier|public
name|StopFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|StopFilter
name|stopFilter
init|=
operator|new
name|StopFilter
argument_list|(
name|input
argument_list|,
name|stopWords
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|stopFilter
operator|.
name|setEnablePositionIncrements
argument_list|(
name|enablePositionIncrements
argument_list|)
expr_stmt|;
return|return
name|stopFilter
return|;
block|}
block|}
end_class

end_unit

