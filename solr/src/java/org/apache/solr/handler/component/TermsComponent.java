begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|StringHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|CharArr
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
name|SolrException
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
name|*
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|StrField
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
name|SimpleFacets
operator|.
name|CountPair
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
name|SolrIndexReader
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
name|BoundedTreeSet
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|TermsResponse
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Locale
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
comment|/**  * Return TermEnum information, useful for things like auto suggest.  *  * @see org.apache.solr.common.params.TermsParams  *      See Lucene's TermEnum class  */
end_comment

begin_class
DECL|class|TermsComponent
specifier|public
class|class
name|TermsComponent
extends|extends
name|SearchComponent
block|{
DECL|field|UNLIMITED_MAX_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|UNLIMITED_MAX_COUNT
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"terms"
decl_stmt|;
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|rb
operator|.
name|doTerms
operator|=
literal|true
expr_stmt|;
block|}
comment|// TODO: temporary... this should go in a different component.
name|String
name|shards
init|=
name|params
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|shards
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS_QT
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No shards.qt parameter specified"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|lst
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|shards
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|rb
operator|.
name|shards
operator|=
name|lst
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|lst
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|false
argument_list|)
condition|)
return|return;
name|String
index|[]
name|fields
init|=
name|params
operator|.
name|getParams
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|)
decl_stmt|;
name|NamedList
name|termsResult
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"terms"
argument_list|,
name|termsResult
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
name|int
name|limit
init|=
name|params
operator|.
name|getInt
argument_list|(
name|TermsParams
operator|.
name|TERMS_LIMIT
argument_list|,
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|limit
operator|<
literal|0
condition|)
block|{
name|limit
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|String
name|lowerStr
init|=
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_LOWER
argument_list|)
decl_stmt|;
name|String
name|upperStr
init|=
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_UPPER
argument_list|)
decl_stmt|;
name|boolean
name|upperIncl
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermsParams
operator|.
name|TERMS_UPPER_INCLUSIVE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|lowerIncl
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermsParams
operator|.
name|TERMS_LOWER_INCLUSIVE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|sort
init|=
operator|!
name|TermsParams
operator|.
name|TERMS_SORT_INDEX
operator|.
name|equals
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_SORT
argument_list|,
name|TermsParams
operator|.
name|TERMS_SORT_COUNT
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|freqmin
init|=
name|params
operator|.
name|getInt
argument_list|(
name|TermsParams
operator|.
name|TERMS_MINCOUNT
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|freqmax
init|=
name|params
operator|.
name|getInt
argument_list|(
name|TermsParams
operator|.
name|TERMS_MAXCOUNT
argument_list|,
name|UNLIMITED_MAX_COUNT
argument_list|)
decl_stmt|;
if|if
condition|(
name|freqmax
operator|<
literal|0
condition|)
block|{
name|freqmax
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|String
name|prefix
init|=
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_PREFIX_STR
argument_list|)
decl_stmt|;
name|String
name|regexp
init|=
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_REGEXP_STR
argument_list|)
decl_stmt|;
name|Pattern
name|pattern
init|=
name|regexp
operator|!=
literal|null
condition|?
name|Pattern
operator|.
name|compile
argument_list|(
name|regexp
argument_list|,
name|resolveRegexpFlags
argument_list|(
name|params
argument_list|)
argument_list|)
else|:
literal|null
decl_stmt|;
name|boolean
name|raw
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermsParams
operator|.
name|TERMS_RAW
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SolrIndexReader
name|sr
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Fields
name|lfields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|sr
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|NamedList
name|fieldTerms
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|termsResult
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|fieldTerms
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|lfields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// no terms for this field
continue|continue;
block|}
name|FieldType
name|ft
init|=
name|raw
condition|?
literal|null
else|:
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
name|ft
operator|=
operator|new
name|StrField
argument_list|()
expr_stmt|;
comment|// prefix must currently be text
name|BytesRef
name|prefixBytes
init|=
name|prefix
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|BytesRef
name|upperBytes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|upperStr
operator|!=
literal|null
condition|)
block|{
name|upperBytes
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|ft
operator|.
name|readableToIndexed
argument_list|(
name|upperStr
argument_list|,
name|upperBytes
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|lowerBytes
decl_stmt|;
if|if
condition|(
name|lowerStr
operator|==
literal|null
condition|)
block|{
comment|// If no lower bound was specified, use the prefix
name|lowerBytes
operator|=
name|prefixBytes
expr_stmt|;
block|}
else|else
block|{
name|lowerBytes
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
if|if
condition|(
name|raw
condition|)
block|{
comment|// TODO: how to handle binary? perhaps we don't for "raw"... or if the field exists
comment|// perhaps we detect if the FieldType is non-character and expect hex if so?
name|lowerBytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|lowerStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lowerBytes
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|ft
operator|.
name|readableToIndexed
argument_list|(
name|lowerStr
argument_list|,
name|lowerBytes
argument_list|)
expr_stmt|;
block|}
block|}
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|term
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lowerBytes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|termsEnum
operator|.
name|seek
argument_list|(
name|lowerBytes
argument_list|,
literal|true
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
name|termsEnum
operator|.
name|term
argument_list|()
expr_stmt|;
comment|//Only advance the enum if we are excluding the lower bound and the lower Term actually matches
if|if
condition|(
name|lowerIncl
operator|==
literal|false
operator|&&
name|term
operator|.
name|equals
argument_list|(
name|lowerBytes
argument_list|)
condition|)
block|{
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// position termsEnum on first term
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|BoundedTreeSet
argument_list|<
name|CountPair
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|queue
init|=
operator|(
name|sort
condition|?
operator|new
name|BoundedTreeSet
argument_list|<
name|CountPair
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|(
name|limit
argument_list|)
else|:
literal|null
operator|)
decl_stmt|;
name|CharArr
name|external
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
literal|null
operator|&&
operator|(
name|i
operator|<
name|limit
operator|||
name|sort
operator|)
condition|)
block|{
name|boolean
name|externalized
init|=
literal|false
decl_stmt|;
comment|// did we fill in "external" yet for this term?
comment|// stop if the prefix doesn't match
if|if
condition|(
name|prefixBytes
operator|!=
literal|null
operator|&&
operator|!
name|term
operator|.
name|startsWith
argument_list|(
name|prefixBytes
argument_list|)
condition|)
break|break;
if|if
condition|(
name|pattern
operator|!=
literal|null
condition|)
block|{
comment|// indexed text or external text?
comment|// TODO: support "raw" mode?
name|external
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|external
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pattern
operator|.
name|matcher
argument_list|(
name|external
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|upperBytes
operator|!=
literal|null
condition|)
block|{
name|int
name|upperCmp
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|upperBytes
argument_list|)
decl_stmt|;
comment|// if we are past the upper term, or equal to it (when don't include upper) then stop.
if|if
condition|(
name|upperCmp
operator|>
literal|0
operator|||
operator|(
name|upperCmp
operator|==
literal|0
operator|&&
operator|!
name|upperIncl
operator|)
condition|)
break|break;
block|}
comment|// This is a good term in the range.  Check if mincount/maxcount conditions are satisfied.
name|int
name|docFreq
init|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|docFreq
operator|>=
name|freqmin
operator|&&
name|docFreq
operator|<=
name|freqmax
condition|)
block|{
comment|// add the term to the list
if|if
condition|(
name|sort
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
operator|new
name|CountPair
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|,
name|docFreq
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: handle raw somehow
if|if
condition|(
operator|!
name|externalized
condition|)
block|{
name|external
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|external
argument_list|)
expr_stmt|;
block|}
name|String
name|label
init|=
name|external
operator|.
name|toString
argument_list|()
decl_stmt|;
name|fieldTerms
operator|.
name|add
argument_list|(
name|label
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sort
condition|)
block|{
for|for
control|(
name|CountPair
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|item
range|:
name|queue
control|)
block|{
if|if
condition|(
name|i
operator|>=
name|limit
condition|)
break|break;
name|external
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|item
operator|.
name|key
argument_list|,
name|external
argument_list|)
expr_stmt|;
name|fieldTerms
operator|.
name|add
argument_list|(
name|external
operator|.
name|toString
argument_list|()
argument_list|,
name|item
operator|.
name|val
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|resolveRegexpFlags
name|int
name|resolveRegexpFlags
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|String
index|[]
name|flagParams
init|=
name|params
operator|.
name|getParams
argument_list|(
name|TermsParams
operator|.
name|TERMS_REGEXP_FLAG
argument_list|)
decl_stmt|;
if|if
condition|(
name|flagParams
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|flags
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|flagParam
range|:
name|flagParams
control|)
block|{
try|try
block|{
name|flags
operator||=
name|TermsParams
operator|.
name|TermsRegexpFlag
operator|.
name|valueOf
argument_list|(
name|flagParam
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown terms regex flag '"
operator|+
name|flagParam
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
return|return
name|flags
return|;
block|}
annotation|@
name|Override
DECL|method|distributedProcess
specifier|public
name|int
name|distributedProcess
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doTerms
condition|)
block|{
return|return
name|ResponseBuilder
operator|.
name|STAGE_DONE
return|;
block|}
if|if
condition|(
name|rb
operator|.
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_EXECUTE_QUERY
condition|)
block|{
name|TermsHelper
name|th
init|=
name|rb
operator|.
name|_termsHelper
decl_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|rb
operator|.
name|_termsHelper
operator|=
operator|new
name|TermsHelper
argument_list|()
expr_stmt|;
name|th
operator|.
name|init
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ShardRequest
name|sreq
init|=
name|createShardQuery
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|rb
operator|.
name|addRequest
argument_list|(
name|this
argument_list|,
name|sreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|stage
operator|<
name|ResponseBuilder
operator|.
name|STAGE_EXECUTE_QUERY
condition|)
block|{
return|return
name|ResponseBuilder
operator|.
name|STAGE_EXECUTE_QUERY
return|;
block|}
else|else
block|{
return|return
name|ResponseBuilder
operator|.
name|STAGE_DONE
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|handleResponses
specifier|public
name|void
name|handleResponses
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doTerms
operator|||
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_TERMS
operator|)
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|TermsHelper
name|th
init|=
name|rb
operator|.
name|_termsHelper
decl_stmt|;
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ShardResponse
name|srsp
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|th
operator|.
name|parse
argument_list|(
operator|(
name|NamedList
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doTerms
operator|||
name|rb
operator|.
name|stage
operator|!=
name|ResponseBuilder
operator|.
name|STAGE_EXECUTE_QUERY
condition|)
block|{
return|return;
block|}
name|TermsHelper
name|ti
init|=
name|rb
operator|.
name|_termsHelper
decl_stmt|;
name|NamedList
name|terms
init|=
name|ti
operator|.
name|buildResponse
argument_list|()
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"terms"
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|rb
operator|.
name|_termsHelper
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|createShardQuery
specifier|private
name|ShardRequest
name|createShardQuery
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_TERMS
expr_stmt|;
comment|// base shard request on original parameters
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|// don't pass through the shards param
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|ShardParams
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
comment|// remove any limits for shards, we want them to return all possible
comment|// responses
comment|// we want this so we can calculate the correct counts
comment|// dont sort by count to avoid that unnecessary overhead on the shards
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|TermsParams
operator|.
name|TERMS_MAXCOUNT
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|TermsParams
operator|.
name|TERMS_MINCOUNT
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|TermsParams
operator|.
name|TERMS_LIMIT
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|TermsParams
operator|.
name|TERMS_SORT
argument_list|,
name|TermsParams
operator|.
name|TERMS_SORT_INDEX
argument_list|)
expr_stmt|;
comment|// TODO: is there a better way to handle this?
name|String
name|qt
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
decl_stmt|;
if|if
condition|(
name|qt
operator|!=
literal|null
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
name|qt
argument_list|)
expr_stmt|;
block|}
return|return
name|sreq
return|;
block|}
DECL|class|TermsHelper
specifier|public
class|class
name|TermsHelper
block|{
comment|// map to store returned terms
DECL|field|fieldmap
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsResponse
operator|.
name|Term
argument_list|>
argument_list|>
name|fieldmap
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|method|TermsHelper
specifier|public
name|TermsHelper
parameter_list|()
block|{
name|fieldmap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsResponse
operator|.
name|Term
argument_list|>
argument_list|>
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|String
index|[]
name|fields
init|=
name|params
operator|.
name|getParams
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
comment|// TODO: not sure 128 is the best starting size
comment|// It use it because that is what is used for facets
name|fieldmap
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsResponse
operator|.
name|Term
argument_list|>
argument_list|(
literal|128
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|NamedList
name|terms
parameter_list|)
block|{
comment|// exit if there is no terms
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|TermsResponse
name|termsResponse
init|=
operator|new
name|TermsResponse
argument_list|(
name|terms
argument_list|)
decl_stmt|;
comment|// loop though each field and add each term+freq to map
for|for
control|(
name|String
name|key
range|:
name|fieldmap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsResponse
operator|.
name|Term
argument_list|>
name|termmap
init|=
name|fieldmap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TermsResponse
operator|.
name|Term
argument_list|>
name|termlist
init|=
name|termsResponse
operator|.
name|getTerms
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// skip this field if there are no terms
if|if
condition|(
name|termlist
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// loop though each term
for|for
control|(
name|TermsResponse
operator|.
name|Term
name|tc
range|:
name|termlist
control|)
block|{
name|String
name|term
init|=
name|tc
operator|.
name|getTerm
argument_list|()
decl_stmt|;
if|if
condition|(
name|termmap
operator|.
name|containsKey
argument_list|(
name|term
argument_list|)
condition|)
block|{
name|TermsResponse
operator|.
name|Term
name|oldtc
init|=
name|termmap
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|oldtc
operator|.
name|addFrequency
argument_list|(
name|tc
operator|.
name|getFrequency
argument_list|()
argument_list|)
expr_stmt|;
name|termmap
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|oldtc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termmap
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|tc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|buildResponse
specifier|public
name|NamedList
name|buildResponse
parameter_list|()
block|{
name|NamedList
name|response
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
comment|// determine if we are going index or count sort
name|boolean
name|sort
init|=
operator|!
name|TermsParams
operator|.
name|TERMS_SORT_INDEX
operator|.
name|equals
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_SORT
argument_list|,
name|TermsParams
operator|.
name|TERMS_SORT_COUNT
argument_list|)
argument_list|)
decl_stmt|;
comment|// init minimum frequency
name|long
name|freqmin
init|=
literal|1
decl_stmt|;
name|String
name|s
init|=
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_MINCOUNT
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
name|freqmin
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|// init maximum frequency, default to max int
name|long
name|freqmax
init|=
operator|-
literal|1
decl_stmt|;
name|s
operator|=
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_MAXCOUNT
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
name|freqmax
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|freqmax
operator|<
literal|0
condition|)
block|{
name|freqmax
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
comment|// init limit, default to max int
name|long
name|limit
init|=
literal|10
decl_stmt|;
name|s
operator|=
name|params
operator|.
name|get
argument_list|(
name|TermsParams
operator|.
name|TERMS_LIMIT
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
name|limit
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|limit
operator|<
literal|0
condition|)
block|{
name|limit
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
comment|// loop though each field we want terms from
for|for
control|(
name|String
name|key
range|:
name|fieldmap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|NamedList
name|fieldterms
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|TermsResponse
operator|.
name|Term
index|[]
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sort
condition|)
block|{
name|data
operator|=
name|getCountSorted
argument_list|(
name|fieldmap
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
name|getLexSorted
argument_list|(
name|fieldmap
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// loop though each term until we hit limit
name|int
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TermsResponse
operator|.
name|Term
name|tc
range|:
name|data
control|)
block|{
if|if
condition|(
name|tc
operator|.
name|getFrequency
argument_list|()
operator|>=
name|freqmin
operator|&&
name|tc
operator|.
name|getFrequency
argument_list|()
operator|<=
name|freqmax
condition|)
block|{
name|fieldterms
operator|.
name|add
argument_list|(
name|tc
operator|.
name|getTerm
argument_list|()
argument_list|,
name|num
argument_list|(
name|tc
operator|.
name|getFrequency
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|cnt
operator|>=
name|limit
condition|)
block|{
break|break;
block|}
block|}
name|response
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|fieldterms
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
comment|// use<int> tags for smaller facet counts (better back compatibility)
DECL|method|num
specifier|private
name|Number
name|num
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
return|return
operator|(
name|int
operator|)
name|val
return|;
else|else
return|return
name|val
return|;
block|}
comment|// based on code from facets
DECL|method|getLexSorted
specifier|public
name|TermsResponse
operator|.
name|Term
index|[]
name|getLexSorted
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsResponse
operator|.
name|Term
argument_list|>
name|data
parameter_list|)
block|{
name|TermsResponse
operator|.
name|Term
index|[]
name|arr
init|=
name|data
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|TermsResponse
operator|.
name|Term
index|[
name|data
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|arr
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TermsResponse
operator|.
name|Term
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|TermsResponse
operator|.
name|Term
name|o1
parameter_list|,
name|TermsResponse
operator|.
name|Term
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getTerm
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getTerm
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|arr
return|;
block|}
comment|// based on code from facets
DECL|method|getCountSorted
specifier|public
name|TermsResponse
operator|.
name|Term
index|[]
name|getCountSorted
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsResponse
operator|.
name|Term
argument_list|>
name|data
parameter_list|)
block|{
name|TermsResponse
operator|.
name|Term
index|[]
name|arr
init|=
name|data
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|TermsResponse
operator|.
name|Term
index|[
name|data
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|arr
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TermsResponse
operator|.
name|Term
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|TermsResponse
operator|.
name|Term
name|o1
parameter_list|,
name|TermsResponse
operator|.
name|Term
name|o2
parameter_list|)
block|{
name|long
name|freq1
init|=
name|o1
operator|.
name|getFrequency
argument_list|()
decl_stmt|;
name|long
name|freq2
init|=
name|o2
operator|.
name|getFrequency
argument_list|()
decl_stmt|;
if|if
condition|(
name|freq2
operator|<
name|freq1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|freq1
operator|<
name|freq2
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|o1
operator|.
name|getTerm
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getTerm
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|arr
return|;
block|}
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A Component for working with Term Enumerators"
return|;
block|}
block|}
end_class

end_unit

