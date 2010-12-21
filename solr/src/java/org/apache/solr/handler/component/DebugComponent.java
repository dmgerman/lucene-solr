begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import static
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
operator|.
name|FQ
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|QueryParsing
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
name|SolrPluginUtils
import|;
end_import

begin_comment
comment|/**  * Adds debugging information to a request.  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|DebugComponent
specifier|public
class|class
name|DebugComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"debug"
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
block|{        }
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
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
if|if
condition|(
name|rb
operator|.
name|isDebug
argument_list|()
condition|)
block|{
name|NamedList
name|stdinfo
init|=
name|SolrPluginUtils
operator|.
name|doStandardDebug
argument_list|(
name|rb
operator|.
name|req
argument_list|,
name|rb
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|,
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
argument_list|,
name|rb
operator|.
name|isDebugQuery
argument_list|()
argument_list|,
name|rb
operator|.
name|isDebugResults
argument_list|()
argument_list|)
decl_stmt|;
name|NamedList
name|info
init|=
name|rb
operator|.
name|getDebugInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|rb
operator|.
name|setDebugInfo
argument_list|(
name|stdinfo
argument_list|)
expr_stmt|;
name|info
operator|=
name|stdinfo
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|addAll
argument_list|(
name|stdinfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugQuery
argument_list|()
operator|&&
name|rb
operator|.
name|getQparser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rb
operator|.
name|getQparser
argument_list|()
operator|.
name|addDebugInfo
argument_list|(
name|rb
operator|.
name|getDebugInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|rb
operator|.
name|getDebugInfo
argument_list|()
condition|)
block|{
if|if
condition|(
name|rb
operator|.
name|isDebugQuery
argument_list|()
operator|&&
literal|null
operator|!=
name|rb
operator|.
name|getFilters
argument_list|()
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"filter_queries"
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|FQ
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fqs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|rb
operator|.
name|getFilters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|fq
range|:
name|rb
operator|.
name|getFilters
argument_list|()
control|)
block|{
name|fqs
operator|.
name|add
argument_list|(
name|QueryParsing
operator|.
name|toString
argument_list|(
name|fq
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|add
argument_list|(
literal|"parsed_filter_queries"
argument_list|,
name|fqs
argument_list|)
expr_stmt|;
block|}
comment|// Add this directly here?
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|rb
operator|.
name|getDebugInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|modifyRequest
specifier|public
name|void
name|modifyRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SearchComponent
name|who
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
name|isDebug
argument_list|()
condition|)
return|return;
comment|// Turn on debug to get explain only when retrieving fields
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_FIELDS
operator|)
operator|!=
literal|0
condition|)
block|{
name|sreq
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_GET_DEBUG
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|isDebugAll
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rb
operator|.
name|isDebugQuery
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|QUERY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rb
operator|.
name|isDebugTimings
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|TIMING
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|RESULTS
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
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
block|{   }
DECL|field|excludeSet
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|excludeSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"explain"
argument_list|)
argument_list|)
decl_stmt|;
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
name|rb
operator|.
name|isDebug
argument_list|()
operator|&&
name|rb
operator|.
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
block|{
name|NamedList
name|info
init|=
literal|null
decl_stmt|;
name|NamedList
name|explain
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
index|[]
name|arr
init|=
operator|new
name|NamedList
operator|.
name|NamedListEntry
index|[
name|rb
operator|.
name|resultIds
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|ShardRequest
name|sreq
range|:
name|rb
operator|.
name|finished
control|)
block|{
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_DEBUG
operator|)
operator|==
literal|0
condition|)
continue|continue;
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
name|NamedList
name|sdebug
init|=
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
literal|"debug"
argument_list|)
decl_stmt|;
name|info
operator|=
operator|(
name|NamedList
operator|)
name|merge
argument_list|(
name|sdebug
argument_list|,
name|info
argument_list|,
name|excludeSet
argument_list|)
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|NamedList
name|sexplain
init|=
operator|(
name|NamedList
operator|)
name|sdebug
operator|.
name|get
argument_list|(
literal|"explain"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sexplain
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|sexplain
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// TODO: lookup won't work for non-string ids... String vs Float
name|ShardDoc
name|sdoc
init|=
name|rb
operator|.
name|resultIds
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|sdoc
operator|.
name|positionInResponse
decl_stmt|;
name|arr
index|[
name|idx
index|]
operator|=
operator|new
name|NamedList
operator|.
name|NamedListEntry
argument_list|<
name|Object
argument_list|>
argument_list|(
name|id
argument_list|,
name|sexplain
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|explain
operator|=
name|SolrPluginUtils
operator|.
name|removeNulls
argument_list|(
operator|new
name|SimpleOrderedMap
argument_list|(
name|arr
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
operator|new
name|SimpleOrderedMap
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|int
name|idx
init|=
name|info
operator|.
name|indexOf
argument_list|(
literal|"explain"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
literal|0
condition|)
block|{
name|info
operator|.
name|setVal
argument_list|(
name|idx
argument_list|,
name|explain
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|add
argument_list|(
literal|"explain"
argument_list|,
name|explain
argument_list|)
expr_stmt|;
block|}
block|}
name|rb
operator|.
name|setDebugInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|rb
operator|.
name|getDebugInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|merge
name|Object
name|merge
parameter_list|(
name|Object
name|source
parameter_list|,
name|Object
name|dest
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|exclude
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
return|return
name|dest
return|;
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|source
operator|instanceof
name|NamedList
condition|)
block|{
name|dest
operator|=
name|source
operator|instanceof
name|SimpleOrderedMap
condition|?
operator|new
name|SimpleOrderedMap
argument_list|()
else|:
operator|new
name|NamedList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
name|source
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|dest
operator|instanceof
name|Collection
condition|)
block|{
if|if
condition|(
name|source
operator|instanceof
name|Collection
condition|)
block|{
operator|(
operator|(
name|Collection
operator|)
name|dest
operator|)
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
operator|)
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|Collection
operator|)
name|dest
operator|)
operator|.
name|add
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
elseif|else
if|if
condition|(
name|source
operator|instanceof
name|Number
condition|)
block|{
if|if
condition|(
name|dest
operator|instanceof
name|Number
condition|)
block|{
if|if
condition|(
name|source
operator|instanceof
name|Double
operator|||
name|dest
operator|instanceof
name|Double
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|source
operator|)
operator|.
name|doubleValue
argument_list|()
operator|+
operator|(
operator|(
name|Number
operator|)
name|dest
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
return|return
operator|(
operator|(
name|Number
operator|)
name|source
operator|)
operator|.
name|longValue
argument_list|()
operator|+
operator|(
operator|(
name|Number
operator|)
name|dest
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|// fall through
block|}
elseif|else
if|if
condition|(
name|source
operator|instanceof
name|String
condition|)
block|{
if|if
condition|(
name|source
operator|.
name|equals
argument_list|(
name|dest
argument_list|)
condition|)
block|{
return|return
name|dest
return|;
block|}
comment|// fall through
block|}
block|}
if|if
condition|(
name|source
operator|instanceof
name|NamedList
operator|&&
name|dest
operator|instanceof
name|NamedList
condition|)
block|{
name|NamedList
name|tmp
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|NamedList
name|sl
init|=
operator|(
name|NamedList
operator|)
name|source
decl_stmt|;
name|NamedList
name|dl
init|=
operator|(
name|NamedList
operator|)
name|dest
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sl
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|skey
init|=
name|sl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|exclude
operator|!=
literal|null
operator|&&
name|exclude
operator|.
name|contains
argument_list|(
name|skey
argument_list|)
condition|)
continue|continue;
name|Object
name|sval
init|=
name|sl
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|didx
init|=
operator|-
literal|1
decl_stmt|;
comment|// optimize case where elements are in same position
if|if
condition|(
name|i
operator|<
name|dl
operator|.
name|size
argument_list|()
condition|)
block|{
name|String
name|dkey
init|=
name|dl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|skey
operator|==
name|dkey
operator|||
operator|(
name|skey
operator|!=
literal|null
operator|&&
name|skey
operator|.
name|equals
argument_list|(
name|dkey
argument_list|)
operator|)
condition|)
block|{
name|didx
operator|=
name|i
expr_stmt|;
block|}
block|}
if|if
condition|(
name|didx
operator|==
operator|-
literal|1
condition|)
block|{
name|didx
operator|=
name|dl
operator|.
name|indexOf
argument_list|(
name|skey
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|didx
operator|==
operator|-
literal|1
condition|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|skey
argument_list|,
name|merge
argument_list|(
name|sval
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dl
operator|.
name|setVal
argument_list|(
name|didx
argument_list|,
name|merge
argument_list|(
name|sval
argument_list|,
name|dl
operator|.
name|getVal
argument_list|(
name|didx
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|dl
operator|.
name|addAll
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
return|return
name|dl
return|;
block|}
comment|// merge unlike elements in a list
name|List
name|t
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|t
operator|.
name|add
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|t
operator|.
name|add
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
comment|/////////////////////////////////////////////
comment|///  SolrInfoMBean
comment|////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Debug Information"
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

