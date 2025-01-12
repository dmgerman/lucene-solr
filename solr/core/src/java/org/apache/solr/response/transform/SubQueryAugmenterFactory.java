begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Iterator
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
name|concurrent
operator|.
name|Callable
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
name|index
operator|.
name|IndexableField
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|embedded
operator|.
name|EmbeddedSolrServer
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
name|request
operator|.
name|QueryRequest
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
name|QueryResponse
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
name|SolrDocument
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
name|SolrDocumentList
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
name|SolrException
operator|.
name|ErrorCode
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
name|params
operator|.
name|SolrParams
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
name|SolrQueryRequest
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
name|SolrRequestInfo
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
name|ResultContext
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
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocList
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
name|DocSlice
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
name|JoinQParserPlugin
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
name|ReturnFields
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
name|search
operator|.
name|SolrReturnFields
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
name|TermsQParserPlugin
import|;
end_import

begin_comment
comment|/**  *  * This transformer executes subquery per every result document. It must be given an unique name.   * There might be a few of them, eg<code>fl=*,foo:[subquery],bar:[subquery]</code>.   * Every [subquery] occurrence adds a field into a result document with the given name,   * the value of this field is a document list, which is a result of executing subquery using   * document fields as an input.  *   *<h3>Subquery Parameters Shift</h3>  * if subquery is declared as<code>fl=*,foo:[subquery]</code>, subquery parameters   * are prefixed with the given name and period. eg<br>  *<code>q=*:*&amp;fl=*,foo:[subquery]&amp;foo.q=to be continued&amp;foo.rows=10&amp;foo.sort=id desc</code>  *   *<h3>Document Field As An Input For Subquery Parameters</h3>  *   * It's necessary to pass some document field value as a parameter for subquery. It's supported via   * implicit<code>row.<i>fieldname</i></code> parameters, and can be (but might not only) referred via  *  Local Parameters syntax.<br>  *<code>q=namne:john&amp;fl=name,id,depts:[subquery]&amp;depts.q={!terms f=id v=$row.dept_id}&amp;depts.rows=10</code>  * Here departments are retrieved per every employee in search result. We can say that it's like SQL  *<code> join ON emp.dept_id=dept.id</code><br>  * Note, when document field has multiple values they are concatenated with comma by default, it can be changed by  *<code>foo:[subquery separator=' ']</code> local parameter, this mimics {@link TermsQParserPlugin} to work smoothly with.  *   *<h3>Cores And Collections In SolrCloud</h3>  * use<code>foo:[subquery fromIndex=departments]</code> invoke subquery on another core on the same node, it's like  *  {@link JoinQParserPlugin} for non SolrCloud mode.<b>But for SolrCloud</b> just (and only)<b>explicitly specify</b>   * its' native parameters like<code>collection, shards</code> for subquery, eg<br>  *<code>q=*:*&amp;fl=*,foo:[subquery]&amp;foo.q=cloud&amp;foo.collection=departments</code>  *  *<h3>When used in Real Time Get</h3>  *<p>  * When used in the context of a Real Time Get, the<i>values</i> from each document that are used   * in the qubquery are the "real time" values (possibly from the transaction log), but the query   * itself is still executed against the currently open searcher.  Note that this means if a   * document is updated but not yet committed, an RTG request for that document that uses   *<code>[subquery]</code> could include the older (committed) version of that document,   * with differnet field values, in the subquery results.  *</p>  */
end_comment

begin_class
DECL|class|SubQueryAugmenterFactory
specifier|public
class|class
name|SubQueryAugmenterFactory
extends|extends
name|TransformerFactory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|DocTransformer
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|contains
argument_list|(
literal|"["
argument_list|)
operator|||
name|field
operator|.
name|contains
argument_list|(
literal|"]"
argument_list|)
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
literal|"please give an exlicit name for [subquery] column ie fl=relation:[subquery ..]"
argument_list|)
throw|;
block|}
name|checkThereIsNoDupe
argument_list|(
name|field
argument_list|,
name|req
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fromIndex
init|=
name|params
operator|.
name|get
argument_list|(
literal|"fromIndex"
argument_list|)
decl_stmt|;
specifier|final
name|SolrClient
name|solrClient
decl_stmt|;
name|solrClient
operator|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|SolrParams
name|subParams
init|=
name|retainAndShiftPrefix
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
name|field
operator|+
literal|"."
argument_list|)
decl_stmt|;
return|return
operator|new
name|SubQueryAugmenter
argument_list|(
name|solrClient
argument_list|,
name|fromIndex
argument_list|,
name|field
argument_list|,
name|field
argument_list|,
name|subParams
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|TermsQParserPlugin
operator|.
name|SEPARATOR
argument_list|,
literal|","
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|checkThereIsNoDupe
specifier|private
name|void
name|checkThereIsNoDupe
parameter_list|(
name|String
name|field
parameter_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|context
parameter_list|)
block|{
comment|// find a map
specifier|final
name|Map
name|conflictMap
decl_stmt|;
specifier|final
name|String
name|conflictMapKey
init|=
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|containsKey
argument_list|(
name|conflictMapKey
argument_list|)
condition|)
block|{
name|conflictMap
operator|=
operator|(
name|Map
operator|)
name|context
operator|.
name|get
argument_list|(
name|conflictMapKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conflictMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
name|conflictMapKey
argument_list|,
name|conflictMap
argument_list|)
expr_stmt|;
block|}
comment|// check entry absence
if|if
condition|(
name|conflictMap
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"[subquery] name "
operator|+
name|field
operator|+
literal|" is duplicated"
argument_list|)
throw|;
block|}
else|else
block|{
name|conflictMap
operator|.
name|put
argument_list|(
name|field
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|retainAndShiftPrefix
specifier|private
name|SolrParams
name|retainAndShiftPrefix
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|String
name|subPrefix
parameter_list|)
block|{
name|ModifiableSolrParams
name|out
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|baseKeyIt
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|baseKeyIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|baseKeyIt
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|subPrefix
argument_list|)
condition|)
block|{
name|out
operator|.
name|set
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|subPrefix
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
return|;
block|}
block|}
end_class

begin_class
DECL|class|SubQueryAugmenter
class|class
name|SubQueryAugmenter
extends|extends
name|DocTransformer
block|{
DECL|class|Result
specifier|private
specifier|static
specifier|final
class|class
name|Result
extends|extends
name|ResultContext
block|{
DECL|field|docList
specifier|private
specifier|final
name|SolrDocumentList
name|docList
decl_stmt|;
DECL|field|justWantAllFields
specifier|final
name|SolrReturnFields
name|justWantAllFields
init|=
operator|new
name|SolrReturnFields
argument_list|()
decl_stmt|;
DECL|method|Result
specifier|private
name|Result
parameter_list|(
name|SolrDocumentList
name|docList
parameter_list|)
block|{
name|this
operator|.
name|docList
operator|=
name|docList
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReturnFields
specifier|public
name|ReturnFields
name|getReturnFields
parameter_list|()
block|{
return|return
name|justWantAllFields
return|;
block|}
annotation|@
name|Override
DECL|method|getProcessedDocuments
specifier|public
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|getProcessedDocuments
parameter_list|()
block|{
return|return
name|docList
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|wantsScores
specifier|public
name|boolean
name|wantsScores
parameter_list|()
block|{
return|return
name|justWantAllFields
operator|.
name|wantsScore
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocList
specifier|public
name|DocList
name|getDocList
parameter_list|()
block|{
return|return
operator|new
name|DocSlice
argument_list|(
operator|(
name|int
operator|)
name|docList
operator|.
name|getStart
argument_list|()
argument_list|,
name|docList
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|,
operator|new
name|float
index|[
name|docList
operator|.
name|size
argument_list|()
index|]
argument_list|,
operator|(
name|int
operator|)
name|docList
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|docList
operator|.
name|getMaxScore
argument_list|()
operator|==
literal|null
condition|?
name|Float
operator|.
name|NaN
else|:
name|docList
operator|.
name|getMaxScore
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSearcher
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getRequest
specifier|public
name|SolrQueryRequest
name|getRequest
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** project document values to prefixed parameters    * multivalues are joined with a separator, it always return single value */
DECL|class|DocRowParams
specifier|static
specifier|final
class|class
name|DocRowParams
extends|extends
name|SolrParams
block|{
DECL|field|doc
specifier|final
specifier|private
name|SolrDocument
name|doc
decl_stmt|;
DECL|field|prefixDotRowDot
specifier|final
specifier|private
name|String
name|prefixDotRowDot
decl_stmt|;
DECL|field|separator
specifier|final
specifier|private
name|String
name|separator
decl_stmt|;
DECL|method|DocRowParams
specifier|public
name|DocRowParams
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|separator
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|prefixDotRowDot
operator|=
literal|"row."
expr_stmt|;
comment|//prefix+ ".row.";
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|param
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|Object
argument_list|>
name|vals
init|=
name|mapToDocField
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|rez
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|vals
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|object
init|=
operator|(
name|Object
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|rez
operator|.
name|append
argument_list|(
name|convertFieldValue
argument_list|(
name|object
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|rez
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|String
index|[]
block|{
name|rez
operator|.
name|toString
argument_list|()
block|}
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|aVal
init|=
name|this
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|aVal
operator|!=
literal|null
condition|)
block|{
assert|assert
name|aVal
operator|.
name|length
operator|==
literal|1
operator|:
literal|"that's how getParams is written"
assert|;
return|return
name|aVal
index|[
literal|0
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** @return null if prefix doesn't match, field is absent or empty */
DECL|method|mapToDocField
specifier|protected
name|Collection
argument_list|<
name|Object
argument_list|>
name|mapToDocField
parameter_list|(
name|String
name|param
parameter_list|)
block|{
if|if
condition|(
name|param
operator|.
name|startsWith
argument_list|(
name|prefixDotRowDot
argument_list|)
condition|)
block|{
specifier|final
name|String
name|docFieldName
init|=
name|param
operator|.
name|substring
argument_list|(
name|prefixDotRowDot
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|Object
argument_list|>
name|vals
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|docFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
operator|||
name|vals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|vals
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|convertFieldValue
specifier|protected
name|String
name|convertFieldValue
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|instanceof
name|IndexableField
condition|)
block|{
name|IndexableField
name|f
init|=
operator|(
name|IndexableField
operator|)
name|val
decl_stmt|;
return|return
name|f
operator|.
name|stringValue
argument_list|()
return|;
block|}
return|return
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getParameterNamesIterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getParameterNamesIterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|doc
operator|.
name|getFieldNames
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|fieldNames
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
specifier|final
name|String
name|fieldName
init|=
name|fieldNames
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|prefixDotRowDot
operator|+
name|fieldName
return|;
block|}
block|}
return|;
block|}
block|}
DECL|field|name
specifier|final
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|baseSubParams
specifier|final
specifier|private
name|SolrParams
name|baseSubParams
decl_stmt|;
DECL|field|prefix
specifier|final
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|separator
specifier|final
specifier|private
name|String
name|separator
decl_stmt|;
DECL|field|server
specifier|final
specifier|private
name|SolrClient
name|server
decl_stmt|;
DECL|field|coreName
specifier|final
specifier|private
name|String
name|coreName
decl_stmt|;
DECL|method|SubQueryAugmenter
specifier|public
name|SubQueryAugmenter
parameter_list|(
name|SolrClient
name|server
parameter_list|,
name|String
name|coreName
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|prefix
parameter_list|,
name|SolrParams
name|baseSubParams
parameter_list|,
name|String
name|separator
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|baseSubParams
operator|=
name|baseSubParams
expr_stmt|;
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|coreName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Returns false -- this transformer does use an IndexSearcher, but it does not (neccessarily) need     * the searcher from the ResultContext of the document being returned.  Instead we use the current     * "live" searcher for the specified core.    */
annotation|@
name|Override
DECL|method|needsSolrIndexSearcher
specifier|public
name|boolean
name|needsSolrIndexSearcher
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|,
name|float
name|score
parameter_list|)
block|{
specifier|final
name|SolrParams
name|docWithDeprefixed
init|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
operator|new
name|DocRowParams
argument_list|(
name|doc
argument_list|,
name|prefix
argument_list|,
name|separator
argument_list|)
argument_list|,
name|baseSubParams
argument_list|)
decl_stmt|;
try|try
block|{
name|Callable
argument_list|<
name|QueryResponse
argument_list|>
name|subQuery
init|=
operator|new
name|Callable
argument_list|<
name|QueryResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QueryResponse
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
return|return
operator|new
name|QueryResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
operator|new
name|QueryRequest
argument_list|(
name|docWithDeprefixed
argument_list|)
argument_list|,
name|coreName
argument_list|)
argument_list|,
name|server
argument_list|)
return|;
block|}
finally|finally
block|{           }
block|}
block|}
decl_stmt|;
name|QueryResponse
name|response
init|=
name|SolrRequestInfoSuspender
operator|.
name|doInSuspension
argument_list|(
name|subQuery
argument_list|)
decl_stmt|;
specifier|final
name|SolrDocumentList
name|docList
init|=
operator|(
name|SolrDocumentList
operator|)
name|response
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|getName
argument_list|()
argument_list|,
operator|new
name|Result
argument_list|(
name|docList
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|docString
init|=
name|doc
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"while invoking "
operator|+
name|name
operator|+
literal|":[subquery"
operator|+
operator|(
name|coreName
operator|!=
literal|null
condition|?
literal|"fromIndex="
operator|+
name|coreName
else|:
literal|""
operator|)
operator|+
literal|"] on doc="
operator|+
name|docString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|100
argument_list|,
name|docString
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{}
block|}
comment|// look ma!! no hands..
DECL|class|SolrRequestInfoSuspender
specifier|final
specifier|static
class|class
name|SolrRequestInfoSuspender
extends|extends
name|SolrRequestInfo
block|{
DECL|method|SolrRequestInfoSuspender
specifier|private
name|SolrRequestInfoSuspender
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
comment|/** Suspends current SolrRequestInfo invoke the given action, and resumes then */
DECL|method|doInSuspension
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|doInSuspension
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|action
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SolrRequestInfo
name|info
init|=
name|threadLocal
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|threadLocal
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
name|action
operator|.
name|call
argument_list|()
return|;
block|}
finally|finally
block|{
name|setRequestInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

