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
name|solr
operator|.
name|core
operator|.
name|SolrConfig
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
name|core
operator|.
name|SolrCore
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|SynonymFilterFactory
specifier|public
class|class
name|SynonymFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
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
name|solrConfig
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|synonyms
init|=
name|args
operator|.
name|get
argument_list|(
literal|"synonyms"
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
name|expand
operator|=
name|getBoolean
argument_list|(
literal|"expand"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|synonyms
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
literal|null
decl_stmt|;
try|try
block|{
name|wlist
operator|=
name|solrConfig
operator|.
name|getLines
argument_list|(
name|synonyms
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
name|synMap
operator|=
operator|new
name|SynonymMap
argument_list|()
expr_stmt|;
name|parseRules
argument_list|(
name|wlist
argument_list|,
name|synMap
argument_list|,
literal|"=>"
argument_list|,
literal|","
argument_list|,
name|ignoreCase
argument_list|,
name|expand
argument_list|)
expr_stmt|;
if|if
condition|(
name|wlist
operator|.
name|size
argument_list|()
operator|<=
literal|20
condition|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|fine
argument_list|(
literal|"SynonymMap "
operator|+
name|synonyms
operator|+
literal|":"
operator|+
name|synMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|synMap
specifier|private
name|SynonymMap
name|synMap
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|expand
specifier|private
name|boolean
name|expand
decl_stmt|;
DECL|method|parseRules
specifier|private
specifier|static
name|void
name|parseRules
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|rules
parameter_list|,
name|SynonymMap
name|map
parameter_list|,
name|String
name|mappingSep
parameter_list|,
name|String
name|synSep
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|,
name|boolean
name|expansion
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|rule
range|:
name|rules
control|)
block|{
comment|// To use regexes, we need an expression that specifies an odd number of chars.
comment|// This can't really be done with string.split(), and since we need to
comment|// do unescaping at some point anyway, we wouldn't be saving any effort
comment|// by using regexes.
name|List
argument_list|<
name|String
argument_list|>
name|mapping
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|rule
argument_list|,
name|mappingSep
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|source
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|target
decl_stmt|;
if|if
condition|(
name|mapping
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid Synonym Rule:"
operator|+
name|rule
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|mapping
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
name|source
operator|=
name|getSynList
argument_list|(
name|mapping
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|synSep
argument_list|)
expr_stmt|;
name|target
operator|=
name|getSynList
argument_list|(
name|mapping
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|synSep
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
name|getSynList
argument_list|(
name|mapping
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|synSep
argument_list|)
expr_stmt|;
if|if
condition|(
name|expansion
condition|)
block|{
comment|// expand to all arguments
name|target
operator|=
name|source
expr_stmt|;
block|}
else|else
block|{
comment|// reduce to first argument
name|target
operator|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|target
operator|.
name|add
argument_list|(
name|source
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|includeOrig
init|=
literal|false
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|fromToks
range|:
name|source
control|)
block|{
name|count
operator|++
expr_stmt|;
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|toToks
range|:
name|target
control|)
block|{
name|map
operator|.
name|add
argument_list|(
name|ignoreCase
condition|?
name|StrUtils
operator|.
name|toLower
argument_list|(
name|fromToks
argument_list|)
else|:
name|fromToks
argument_list|,
name|SynonymMap
operator|.
name|makeTokens
argument_list|(
name|toToks
argument_list|)
argument_list|,
name|includeOrig
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// a , b c , d e f => [[a],[b,c],[d,e,f]]
DECL|method|getSynList
specifier|private
specifier|static
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getSynList
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|separator
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|strList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|str
argument_list|,
name|separator
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// now split on whitespace to get a list of token strings
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|synList
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|toks
range|:
name|strList
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tokList
init|=
name|StrUtils
operator|.
name|splitWS
argument_list|(
name|toks
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|synList
operator|.
name|add
argument_list|(
name|tokList
argument_list|)
expr_stmt|;
block|}
return|return
name|synList
return|;
block|}
DECL|method|create
specifier|public
name|SynonymFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|SynonymFilter
argument_list|(
name|input
argument_list|,
name|synMap
argument_list|,
name|ignoreCase
argument_list|)
return|;
block|}
block|}
end_class

end_unit

