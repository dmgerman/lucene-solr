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
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|handler
operator|.
name|component
operator|.
name|QueryComponent
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|handler
operator|.
name|component
operator|.
name|SearchComponent
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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

begin_class
DECL|class|SpellCheckCollator
specifier|public
class|class
name|SpellCheckCollator
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SpellCheckCollator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|collate
specifier|public
name|List
argument_list|<
name|SpellCheckCollation
argument_list|>
name|collate
parameter_list|(
name|SpellingResult
name|result
parameter_list|,
name|String
name|originalQuery
parameter_list|,
name|ResponseBuilder
name|ultimateResponse
parameter_list|,
name|int
name|maxCollations
parameter_list|,
name|int
name|maxTries
parameter_list|,
name|int
name|maxEvaluations
parameter_list|)
block|{
name|List
argument_list|<
name|SpellCheckCollation
argument_list|>
name|collations
init|=
operator|new
name|ArrayList
argument_list|<
name|SpellCheckCollation
argument_list|>
argument_list|()
decl_stmt|;
name|QueryComponent
name|queryComponent
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ultimateResponse
operator|.
name|components
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SearchComponent
name|sc
range|:
name|ultimateResponse
operator|.
name|components
control|)
block|{
if|if
condition|(
name|sc
operator|instanceof
name|QueryComponent
condition|)
block|{
name|queryComponent
operator|=
operator|(
name|QueryComponent
operator|)
name|sc
expr_stmt|;
break|break;
block|}
block|}
block|}
name|boolean
name|verifyCandidateWithQuery
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|maxTries
operator|<
literal|1
condition|)
block|{
name|maxTries
operator|=
literal|1
expr_stmt|;
name|verifyCandidateWithQuery
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|queryComponent
operator|==
literal|null
operator|&&
name|verifyCandidateWithQuery
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not find an instance of QueryComponent.  Disabling collation verification against the index."
argument_list|)
expr_stmt|;
name|maxTries
operator|=
literal|1
expr_stmt|;
name|verifyCandidateWithQuery
operator|=
literal|false
expr_stmt|;
block|}
name|int
name|tryNo
init|=
literal|0
decl_stmt|;
name|int
name|collNo
init|=
literal|0
decl_stmt|;
name|PossibilityIterator
name|possibilityIter
init|=
operator|new
name|PossibilityIterator
argument_list|(
name|result
operator|.
name|getSuggestions
argument_list|()
argument_list|,
name|maxTries
argument_list|,
name|maxEvaluations
argument_list|)
decl_stmt|;
while|while
condition|(
name|tryNo
operator|<
name|maxTries
operator|&&
name|collNo
operator|<
name|maxCollations
operator|&&
name|possibilityIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RankedSpellPossibility
name|possibility
init|=
name|possibilityIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|collationQueryStr
init|=
name|getCollation
argument_list|(
name|originalQuery
argument_list|,
name|possibility
operator|.
name|getCorrections
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|hits
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|verifyCandidateWithQuery
condition|)
block|{
name|tryNo
operator|++
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|ultimateResponse
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
name|collationQueryStr
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
comment|// creating a request here... make sure to close it!
name|ResponseBuilder
name|checkResponse
init|=
operator|new
name|ResponseBuilder
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|ultimateResponse
operator|.
name|req
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SearchComponent
index|[]
block|{
name|queryComponent
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|checkResponse
operator|.
name|setQparser
argument_list|(
name|ultimateResponse
operator|.
name|getQparser
argument_list|()
argument_list|)
expr_stmt|;
name|checkResponse
operator|.
name|setFilters
argument_list|(
name|ultimateResponse
operator|.
name|getFilters
argument_list|()
argument_list|)
expr_stmt|;
name|checkResponse
operator|.
name|setQueryString
argument_list|(
name|collationQueryStr
argument_list|)
expr_stmt|;
name|checkResponse
operator|.
name|components
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SearchComponent
index|[]
block|{
name|queryComponent
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|queryComponent
operator|.
name|prepare
argument_list|(
name|checkResponse
argument_list|)
expr_stmt|;
name|queryComponent
operator|.
name|process
argument_list|(
name|checkResponse
argument_list|)
expr_stmt|;
name|hits
operator|=
operator|(
name|Integer
operator|)
name|checkResponse
operator|.
name|rsp
operator|.
name|getToLog
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception trying to re-query to check if a spell check possibility would return any hits."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|checkResponse
operator|.
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hits
operator|>
literal|0
operator|||
operator|!
name|verifyCandidateWithQuery
condition|)
block|{
name|collNo
operator|++
expr_stmt|;
name|SpellCheckCollation
name|collation
init|=
operator|new
name|SpellCheckCollation
argument_list|()
decl_stmt|;
name|collation
operator|.
name|setCollationQuery
argument_list|(
name|collationQueryStr
argument_list|)
expr_stmt|;
name|collation
operator|.
name|setHits
argument_list|(
name|hits
argument_list|)
expr_stmt|;
name|collation
operator|.
name|setInternalRank
argument_list|(
name|possibility
operator|.
name|getRank
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|misspellingsAndCorrections
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SpellCheckCorrection
name|corr
range|:
name|possibility
operator|.
name|getCorrections
argument_list|()
control|)
block|{
name|misspellingsAndCorrections
operator|.
name|add
argument_list|(
name|corr
operator|.
name|getOriginal
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|corr
operator|.
name|getCorrection
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|collation
operator|.
name|setMisspellingsAndCorrections
argument_list|(
name|misspellingsAndCorrections
argument_list|)
expr_stmt|;
name|collations
operator|.
name|add
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collation: "
operator|+
name|collationQueryStr
operator|+
operator|(
name|verifyCandidateWithQuery
condition|?
operator|(
literal|" will return "
operator|+
name|hits
operator|+
literal|" hits."
operator|)
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|collations
return|;
block|}
DECL|method|getCollation
specifier|private
name|String
name|getCollation
parameter_list|(
name|String
name|origQuery
parameter_list|,
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|corrections
parameter_list|)
block|{
name|StringBuilder
name|collation
init|=
operator|new
name|StringBuilder
argument_list|(
name|origQuery
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SpellCheckCorrection
name|correction
range|:
name|corrections
control|)
block|{
name|Token
name|tok
init|=
name|correction
operator|.
name|getOriginal
argument_list|()
decl_stmt|;
comment|// we are replacing the query in order, but injected terms might cause
comment|// illegal offsets due to previous replacements.
if|if
condition|(
name|tok
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|collation
operator|.
name|replace
argument_list|(
name|tok
operator|.
name|startOffset
argument_list|()
operator|+
name|offset
argument_list|,
name|tok
operator|.
name|endOffset
argument_list|()
operator|+
name|offset
argument_list|,
name|correction
operator|.
name|getCorrection
argument_list|()
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|correction
operator|.
name|getCorrection
argument_list|()
operator|.
name|length
argument_list|()
operator|-
operator|(
name|tok
operator|.
name|endOffset
argument_list|()
operator|-
name|tok
operator|.
name|startOffset
argument_list|()
operator|)
expr_stmt|;
block|}
return|return
name|collation
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

