begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|store
operator|.
name|FSDirectory
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
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|search
operator|.
name|SolrIndexSearcher
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
name|HighFrequencyDictionary
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *<p>  * A spell checker implementation that loads words from Solr as well as arbitary Lucene indices.  *</p>  *   *<p>  * Refer to<a href="http://wiki.apache.org/solr/SpellCheckComponent">SpellCheckComponent</a>  * for more details.  *</p>  *   * @since solr 1.3  **/
end_comment

begin_class
DECL|class|IndexBasedSpellChecker
specifier|public
class|class
name|IndexBasedSpellChecker
extends|extends
name|AbstractLuceneSpellChecker
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexBasedSpellChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|THRESHOLD_TOKEN_FREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|THRESHOLD_TOKEN_FREQUENCY
init|=
literal|"thresholdTokenFrequency"
decl_stmt|;
DECL|field|threshold
specifier|protected
name|float
name|threshold
decl_stmt|;
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|threshold
operator|=
name|config
operator|.
name|get
argument_list|(
name|THRESHOLD_TOKEN_FREQUENCY
argument_list|)
operator|==
literal|null
condition|?
literal|0.0f
else|:
operator|(
name|Float
operator|)
name|config
operator|.
name|get
argument_list|(
name|THRESHOLD_TOKEN_FREQUENCY
argument_list|)
expr_stmt|;
name|initSourceReader
argument_list|()
expr_stmt|;
return|return
name|name
return|;
block|}
DECL|method|initSourceReader
specifier|private
name|void
name|initSourceReader
parameter_list|()
block|{
if|if
condition|(
name|sourceLocation
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|FSDirectory
name|luceneIndexDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|sourceLocation
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|luceneIndexDir
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|sourceLocation
operator|==
literal|null
condition|)
block|{
comment|// Load from Solr's index
name|reader
operator|=
name|searcher
operator|.
name|getReader
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Load from Lucene index at given sourceLocation
name|reader
operator|=
name|this
operator|.
name|reader
expr_stmt|;
block|}
comment|// Create the dictionary
name|dictionary
operator|=
operator|new
name|HighFrequencyDictionary
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|threshold
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|clearIndex
argument_list|()
expr_stmt|;
name|spellChecker
operator|.
name|indexDictionary
argument_list|(
name|dictionary
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|determineReader
specifier|protected
name|IndexReader
name|determineReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|IndexReader
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sourceLocation
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|this
operator|.
name|reader
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|reader
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reload
argument_list|()
expr_stmt|;
comment|//reload the source
name|initSourceReader
argument_list|()
expr_stmt|;
block|}
DECL|method|getThreshold
specifier|public
name|float
name|getThreshold
parameter_list|()
block|{
return|return
name|threshold
return|;
block|}
block|}
end_class

end_unit

