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
name|ArrayList
import|;
end_import

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
name|Collections
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
name|LinkedHashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|DocsAndPositionsEnum
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
name|FieldInfo
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
name|Fields
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
name|FieldsEnum
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
name|index
operator|.
name|StoredFieldVisitor
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
name|Terms
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
name|TermsEnum
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
name|common
operator|.
name|params
operator|.
name|TermVectorParams
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
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
name|SchemaField
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
name|DocListAndSet
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
name|SolrPluginUtils
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
name|SolrCoreAware
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Return term vectors for the documents in a query result set.  *<p/>  * Info available:  * term, frequency, position, offset, IDF.  *<p/>  *<b>Note</b> Returning IDF can be expensive.  *   *<pre class="prettyprint">  *&lt;searchComponent name="tvComponent" class="solr.TermVectorComponent"/&gt;  *   *&lt;requestHandler name="/terms" class="solr.SearchHandler"&gt;  *&lt;lst name="defaults"&gt;  *&lt;bool name="tv"&gt;true&lt;/bool&gt;  *&lt;/lst&gt;  *&lt;arr name="last-component"&gt;  *&lt;str&gt;tvComponent&lt;/str&gt;  *&lt;/arr&gt;  *&lt;/requestHandler&gt;</pre>  *  *  */
end_comment

begin_class
DECL|class|TermVectorComponent
specifier|public
class|class
name|TermVectorComponent
extends|extends
name|SearchComponent
implements|implements
name|SolrCoreAware
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"tv"
decl_stmt|;
DECL|field|initParams
specifier|protected
name|NamedList
name|initParams
decl_stmt|;
DECL|field|TERM_VECTORS
specifier|public
specifier|static
specifier|final
name|String
name|TERM_VECTORS
init|=
literal|"termVectors"
decl_stmt|;
comment|/**    * Helper method for determining the list of fields that we should     * try to find term vectors on.      *<p>    * Does simple (non-glob-supporting) parsing on the     * {@link TermVectorParams#FIELDS} param if specified, otherwise it returns     * the concrete field values specified in {@link CommonParams#FL} --     * ignoring functions, transformers, or literals.      *</p>    *<p>    * If "fl=*" is used, or neither param is specified, then<code>null</code>     * will be returned.  If the empty set is returned, it means the "fl"     * specified consisted entirely of things that are not real fields     * (ie: functions, transformers, partial-globs, score, etc...) and not     * supported by this component.     *</p>    */
DECL|method|getFields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getFields
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
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
name|String
index|[]
name|fldLst
init|=
name|params
operator|.
name|getParams
argument_list|(
name|TermVectorParams
operator|.
name|FIELDS
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fldLst
operator|||
literal|0
operator|==
name|fldLst
operator|.
name|length
operator|||
operator|(
literal|1
operator|==
name|fldLst
operator|.
name|length
operator|&&
literal|0
operator|==
name|fldLst
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|)
condition|)
block|{
comment|// no tv.fl, parse the main fl
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
argument_list|,
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|rf
operator|.
name|getLuceneFieldNames
argument_list|()
decl_stmt|;
return|return
operator|(
literal|null
operator|!=
name|fieldNames
operator|)
condition|?
name|fieldNames
else|:
comment|// return empty set indicating no fields should be used
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
return|;
block|}
comment|// otherwise us the raw fldList as is, no special parsing or globs
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fl
range|:
name|fldLst
control|)
block|{
name|fieldNames
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|SolrPluginUtils
operator|.
name|split
argument_list|(
name|fl
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldNames
return|;
block|}
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
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|termVectors
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
name|TERM_VECTORS
argument_list|,
name|termVectors
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|keyField
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|String
name|uniqFieldName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|keyField
operator|!=
literal|null
condition|)
block|{
name|uniqFieldName
operator|=
name|keyField
operator|.
name|getName
argument_list|()
expr_stmt|;
name|termVectors
operator|.
name|add
argument_list|(
literal|"uniqueKeyFieldName"
argument_list|,
name|uniqFieldName
argument_list|)
expr_stmt|;
block|}
name|FieldOptions
name|allFields
init|=
operator|new
name|FieldOptions
argument_list|()
decl_stmt|;
comment|//figure out what options we have, and try to get the appropriate vector
name|allFields
operator|.
name|termFreq
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allFields
operator|.
name|positions
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allFields
operator|.
name|offsets
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allFields
operator|.
name|docFreq
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allFields
operator|.
name|tfIdf
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//boolean cacheIdf = params.getBool(TermVectorParams.IDF, false);
comment|//short cut to all values.
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|allFields
operator|.
name|termFreq
operator|=
literal|true
expr_stmt|;
name|allFields
operator|.
name|positions
operator|=
literal|true
expr_stmt|;
name|allFields
operator|.
name|offsets
operator|=
literal|true
expr_stmt|;
name|allFields
operator|.
name|docFreq
operator|=
literal|true
expr_stmt|;
name|allFields
operator|.
name|tfIdf
operator|=
literal|true
expr_stmt|;
block|}
comment|//Build up our per field mapping
name|Map
argument_list|<
name|String
argument_list|,
name|FieldOptions
argument_list|>
name|fieldOptions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldOptions
argument_list|>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|warnings
init|=
operator|new
name|NamedList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|noTV
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|noPos
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|noOff
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|getFields
argument_list|(
name|rb
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|fields
condition|)
block|{
comment|//we have specific fields to retrieve, or no fields
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
comment|// workarround SOLR-3523
if|if
condition|(
literal|null
operator|==
name|field
operator|||
literal|"score"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
continue|continue;
comment|// we don't want to issue warnings about the uniqueKey field
comment|// since it can cause lots of confusion in distributed requests
comment|// where the uniqueKey field is injected into the fl for merging
specifier|final
name|boolean
name|fieldIsUniqueKey
init|=
name|field
operator|.
name|equals
argument_list|(
name|uniqFieldName
argument_list|)
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sf
operator|.
name|storeTermVector
argument_list|()
condition|)
block|{
name|FieldOptions
name|option
init|=
name|fieldOptions
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|option
operator|==
literal|null
condition|)
block|{
name|option
operator|=
operator|new
name|FieldOptions
argument_list|()
expr_stmt|;
name|option
operator|.
name|fieldName
operator|=
name|field
expr_stmt|;
name|fieldOptions
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
comment|//get the per field mappings
name|option
operator|.
name|termFreq
operator|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
name|allFields
operator|.
name|termFreq
argument_list|)
expr_stmt|;
name|option
operator|.
name|docFreq
operator|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|TermVectorParams
operator|.
name|DF
argument_list|,
name|allFields
operator|.
name|docFreq
argument_list|)
expr_stmt|;
name|option
operator|.
name|tfIdf
operator|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
name|allFields
operator|.
name|tfIdf
argument_list|)
expr_stmt|;
comment|//Validate these are even an option
name|option
operator|.
name|positions
operator|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
name|allFields
operator|.
name|positions
argument_list|)
expr_stmt|;
if|if
condition|(
name|option
operator|.
name|positions
operator|&&
operator|!
name|sf
operator|.
name|storeTermPositions
argument_list|()
operator|&&
operator|!
name|fieldIsUniqueKey
condition|)
block|{
name|noPos
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|option
operator|.
name|offsets
operator|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
name|allFields
operator|.
name|offsets
argument_list|)
expr_stmt|;
if|if
condition|(
name|option
operator|.
name|offsets
operator|&&
operator|!
name|sf
operator|.
name|storeTermOffsets
argument_list|()
operator|&&
operator|!
name|fieldIsUniqueKey
condition|)
block|{
name|noOff
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//field doesn't have term vectors
if|if
condition|(
operator|!
name|fieldIsUniqueKey
condition|)
name|noTV
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//field doesn't exist
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
literal|"undefined field: "
operator|+
name|field
argument_list|)
throw|;
block|}
block|}
block|}
comment|//else, deal with all fields
comment|// NOTE: currently all typs of warnings are schema driven, and garunteed
comment|// to be consistent across all shards - if additional types of warnings
comment|// are added that might be differnet between shards, finishStage() needs
comment|// to be changed to account for that.
name|boolean
name|hasWarnings
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|noTV
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|warnings
operator|.
name|add
argument_list|(
literal|"noTermVectors"
argument_list|,
name|noTV
argument_list|)
expr_stmt|;
name|hasWarnings
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|noPos
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|warnings
operator|.
name|add
argument_list|(
literal|"noPositions"
argument_list|,
name|noPos
argument_list|)
expr_stmt|;
name|hasWarnings
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|noOff
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|warnings
operator|.
name|add
argument_list|(
literal|"noOffsets"
argument_list|,
name|noOff
argument_list|)
expr_stmt|;
name|hasWarnings
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|hasWarnings
condition|)
block|{
name|termVectors
operator|.
name|add
argument_list|(
literal|"warnings"
argument_list|,
name|warnings
argument_list|)
expr_stmt|;
block|}
name|DocListAndSet
name|listAndSet
init|=
name|rb
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|docIds
init|=
name|getInts
argument_list|(
name|params
operator|.
name|getParams
argument_list|(
name|TermVectorParams
operator|.
name|DOC_IDS
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
decl_stmt|;
if|if
condition|(
name|docIds
operator|!=
literal|null
operator|&&
operator|!
name|docIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|iter
operator|=
name|docIds
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|DocList
name|list
init|=
name|listAndSet
operator|.
name|docList
decl_stmt|;
name|iter
operator|=
name|list
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|SolrIndexSearcher
name|searcher
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
comment|//the TVMapper is a TermVectorMapper which can be used to optimize loading of Term Vectors
comment|//Only load the id field to get the uniqueKey of that
comment|//field
specifier|final
name|String
name|finalUniqFieldName
init|=
name|uniqFieldName
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|uniqValues
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: is this required to be single-valued? if so, we should STOP
comment|// once we find it...
specifier|final
name|StoredFieldVisitor
name|getUniqValue
init|=
operator|new
name|StoredFieldVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|uniqValues
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|intField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|uniqValues
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|longField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|uniqValues
operator|.
name|add
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|(
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
name|finalUniqFieldName
argument_list|)
operator|)
condition|?
name|Status
operator|.
name|YES
else|:
name|Status
operator|.
name|NO
return|;
block|}
block|}
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Integer
name|docId
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|docNL
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyField
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|document
argument_list|(
name|docId
argument_list|,
name|getUniqValue
argument_list|)
expr_stmt|;
name|String
name|uniqVal
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|uniqValues
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|uniqVal
operator|=
name|uniqValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|uniqValues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docNL
operator|.
name|add
argument_list|(
literal|"uniqueKey"
argument_list|,
name|uniqVal
argument_list|)
expr_stmt|;
name|termVectors
operator|.
name|add
argument_list|(
name|uniqVal
argument_list|,
name|docNL
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// support for schemas w/o a unique key,
name|termVectors
operator|.
name|add
argument_list|(
literal|"doc-"
operator|+
name|docId
argument_list|,
name|docNL
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|fields
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FieldOptions
argument_list|>
name|entry
range|:
name|fieldOptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|field
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|Terms
name|vector
init|=
name|reader
operator|.
name|getTermVector
argument_list|(
name|docId
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|vector
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
name|mapOneVector
argument_list|(
name|docNL
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|vector
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// extract all fields
specifier|final
name|Fields
name|vectors
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docId
argument_list|)
decl_stmt|;
specifier|final
name|FieldsEnum
name|fieldsEnum
init|=
name|vectors
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|field
decl_stmt|;
while|while
condition|(
operator|(
name|field
operator|=
name|fieldsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Terms
name|terms
init|=
name|fieldsEnum
operator|.
name|terms
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
name|mapOneVector
argument_list|(
name|docNL
argument_list|,
name|allFields
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|termsEnum
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|mapOneVector
specifier|private
name|void
name|mapOneVector
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|docNL
parameter_list|,
name|FieldOptions
name|fieldOptions
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|docID
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|fieldNL
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|docNL
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|fieldNL
argument_list|)
expr_stmt|;
name|BytesRef
name|text
decl_stmt|;
name|DocsAndPositionsEnum
name|dpEnum
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|text
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|term
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|termInfo
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|fieldNL
operator|.
name|add
argument_list|(
name|term
argument_list|,
name|termInfo
argument_list|)
expr_stmt|;
specifier|final
name|int
name|freq
init|=
operator|(
name|int
operator|)
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldOptions
operator|.
name|termFreq
operator|==
literal|true
condition|)
block|{
name|termInfo
operator|.
name|add
argument_list|(
literal|"tf"
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
name|dpEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|)
expr_stmt|;
name|boolean
name|useOffsets
init|=
literal|false
decl_stmt|;
name|boolean
name|usePositions
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|dpEnum
operator|!=
literal|null
condition|)
block|{
name|dpEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|usePositions
operator|=
name|fieldOptions
operator|.
name|positions
expr_stmt|;
name|useOffsets
operator|=
name|fieldOptions
operator|.
name|offsets
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|positionsNL
init|=
literal|null
decl_stmt|;
name|NamedList
argument_list|<
name|Number
argument_list|>
name|theOffsets
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|usePositions
operator|||
name|useOffsets
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|freq
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|pos
init|=
name|dpEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|usePositions
operator|&&
name|pos
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|positionsNL
operator|==
literal|null
condition|)
block|{
name|positionsNL
operator|=
operator|new
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|termInfo
operator|.
name|add
argument_list|(
literal|"positions"
argument_list|,
name|positionsNL
argument_list|)
expr_stmt|;
block|}
name|positionsNL
operator|.
name|add
argument_list|(
literal|"position"
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useOffsets
operator|&&
name|theOffsets
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|dpEnum
operator|.
name|startOffset
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|useOffsets
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|theOffsets
operator|=
operator|new
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|()
expr_stmt|;
name|termInfo
operator|.
name|add
argument_list|(
literal|"offsets"
argument_list|,
name|theOffsets
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|theOffsets
operator|!=
literal|null
condition|)
block|{
name|theOffsets
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
name|dpEnum
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|theOffsets
operator|.
name|add
argument_list|(
literal|"end"
argument_list|,
name|dpEnum
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|fieldOptions
operator|.
name|docFreq
condition|)
block|{
name|termInfo
operator|.
name|add
argument_list|(
literal|"df"
argument_list|,
name|getDocFreq
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldOptions
operator|.
name|tfIdf
condition|)
block|{
name|double
name|tfIdfVal
init|=
operator|(
operator|(
name|double
operator|)
name|freq
operator|)
operator|/
name|getDocFreq
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|termInfo
operator|.
name|add
argument_list|(
literal|"tf-idf"
argument_list|,
name|tfIdfVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getInts
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|getInts
parameter_list|(
name|String
index|[]
name|vals
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
operator|&&
name|vals
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|vals
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|getDocFreq
specifier|private
specifier|static
name|int
name|getDocFreq
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
name|int
name|result
init|=
literal|1
decl_stmt|;
try|try
block|{
name|result
operator|=
name|reader
operator|.
name|docFreq
argument_list|(
name|field
argument_list|,
name|term
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
return|return
name|result
return|;
block|}
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
block|{    }
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
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
block|{
name|NamedList
name|termVectors
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
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
name|PURPOSE_GET_FIELDS
operator|)
operator|==
literal|0
operator|||
operator|!
name|sreq
operator|.
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
argument_list|<
name|Object
argument_list|>
name|nl
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
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
name|TERM_VECTORS
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
name|nl
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|nl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ShardDoc
name|sdoc
init|=
name|rb
operator|.
name|resultIds
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|sdoc
condition|)
block|{
comment|// metadata, only need from one node, leave in order
if|if
condition|(
name|termVectors
operator|.
name|indexOf
argument_list|(
name|key
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
name|termVectors
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|nl
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
name|key
argument_list|,
name|nl
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
comment|// remove nulls in case not all docs were able to be retrieved
name|termVectors
operator|.
name|addAll
argument_list|(
name|SolrPluginUtils
operator|.
name|removeNulls
argument_list|(
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|arr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
name|TERM_VECTORS
argument_list|,
name|termVectors
argument_list|)
expr_stmt|;
block|}
block|}
comment|//////////////////////// NamedListInitializedPlugin methods //////////////////////
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|initParams
operator|=
name|args
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{    }
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
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A Component for working with Term Vectors"
return|;
block|}
block|}
end_class

begin_class
DECL|class|FieldOptions
class|class
name|FieldOptions
block|{
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|termFreq
DECL|field|positions
DECL|field|offsets
DECL|field|docFreq
DECL|field|tfIdf
name|boolean
name|termFreq
decl_stmt|,
name|positions
decl_stmt|,
name|offsets
decl_stmt|,
name|docFreq
decl_stmt|,
name|tfIdf
decl_stmt|;
block|}
end_class

end_unit

