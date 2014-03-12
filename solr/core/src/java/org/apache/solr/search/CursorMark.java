begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|index
operator|.
name|AtomicReaderContext
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
name|FieldComparator
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
name|IndexSearcher
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
name|FieldDoc
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
name|Scorer
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
name|Sort
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
name|SortField
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
name|SolrException
operator|.
name|ErrorCode
import|;
end_import

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
name|CursorMarkParams
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
name|Base64
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
name|JavaBinCodec
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
name|PostFilter
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
name|ExtendedQueryBase
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
name|DelegatingCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
comment|/**  * An object that encapsulates the basic information about the current Mark Point of a   * "Cursor" based request.<code>CursorMark</code> objects track the sort values of   * the last document returned to a user, so that {@link SolrIndexSearcher} can then   * be asked to find all documents "after" the values represented by this   *<code>CursorMark</code>.  *  */
end_comment

begin_class
DECL|class|CursorMark
specifier|public
specifier|final
class|class
name|CursorMark
block|{
comment|/**    * Used for validation and (un)marshalling of sort values    */
DECL|field|sortSpec
specifier|private
specifier|final
name|SortSpec
name|sortSpec
decl_stmt|;
comment|/**    * The raw, unmarshalled, sort values (that corrispond with the SortField's in the     * SortSpec) for knowing which docs this cursor should "search after".  If this     * list is null, then we have no specific values to "search after" and we     * should start from the very begining of the sorted list of documents matching     * the query.    */
DECL|field|values
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
literal|null
decl_stmt|;
comment|/**    * for serializing this CursorMark as a String    */
DECL|field|codec
specifier|private
specifier|final
name|JavaBinCodec
name|codec
init|=
operator|new
name|JavaBinCodec
argument_list|()
decl_stmt|;
comment|/**    * Generates an empty CursorMark bound for use with the     * specified schema and {@link SortSpec}.    *    * @param schema used for basic validation    * @param sortSpec bound to this totem (un)marshalling serialized values    */
DECL|method|CursorMark
specifier|public
name|CursorMark
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|SortSpec
name|sortSpec
parameter_list|)
block|{
specifier|final
name|SchemaField
name|uniqueKey
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|uniqueKey
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
literal|"Cursor functionality is not available unless the IndexSchema defines a uniqueKey field"
argument_list|)
throw|;
block|}
specifier|final
name|Sort
name|sort
init|=
name|sortSpec
operator|.
name|getSort
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|sort
condition|)
block|{
comment|// pure score, by definition we don't include the mandatyr uniqueKey tie breaker
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Cursor functionality requires a sort containing a uniqueKey field tie breaker"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|sortSpec
operator|.
name|getSchemaFields
argument_list|()
operator|.
name|contains
argument_list|(
name|uniqueKey
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
literal|"Cursor functionality requires a sort containing a uniqueKey field tie breaker"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|0
operator|!=
name|sortSpec
operator|.
name|getOffset
argument_list|()
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
literal|"Cursor functionality requires start=0"
argument_list|)
throw|;
block|}
for|for
control|(
name|SortField
name|sf
range|:
name|sort
operator|.
name|getSort
argument_list|()
control|)
block|{
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|SortField
operator|.
name|Type
operator|.
name|DOC
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
literal|"Cursor functionality can not be used with internal doc ordering sort: _docid_"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|sort
operator|.
name|getSort
argument_list|()
operator|.
name|length
operator|!=
name|sortSpec
operator|.
name|getSchemaFields
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Cursor SortSpec failure: sort length != SchemaFields: "
operator|+
name|sort
operator|.
name|getSort
argument_list|()
operator|.
name|length
operator|+
literal|" != "
operator|+
name|sortSpec
operator|.
name|getSchemaFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|sortSpec
operator|=
name|sortSpec
expr_stmt|;
name|this
operator|.
name|values
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Generates an empty CursorMark bound for use with the same {@link SortSpec}    * as the specified existing CursorMark.    *    * @param previous Existing CursorMark whose SortSpec will be reused in the new CursorMark.    * @see #createNext    */
DECL|method|CursorMark
specifier|private
name|CursorMark
parameter_list|(
name|CursorMark
name|previous
parameter_list|)
block|{
name|this
operator|.
name|sortSpec
operator|=
name|previous
operator|.
name|sortSpec
expr_stmt|;
name|this
operator|.
name|values
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Generates an new CursorMark bound for use with the same {@link SortSpec}    * as the current CursorMark but using the new SortValues.    *    */
DECL|method|createNext
specifier|public
name|CursorMark
name|createNext
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|nextSortValues
parameter_list|)
block|{
specifier|final
name|CursorMark
name|next
init|=
operator|new
name|CursorMark
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|next
operator|.
name|setSortValues
argument_list|(
name|nextSortValues
argument_list|)
expr_stmt|;
return|return
name|next
return|;
block|}
comment|/**    * Sets the (raw, unmarshalled) sort values (which must conform to the existing     * sortSpec) to populate this object.  If null, then there is nothing to     * "search after" and the "first page" of results should be returned.    */
DECL|method|setSortValues
specifier|public
name|void
name|setSortValues
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|input
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|input
condition|)
block|{
name|this
operator|.
name|values
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|input
operator|.
name|size
argument_list|()
operator|==
name|sortSpec
operator|.
name|getSort
argument_list|()
operator|.
name|getSort
argument_list|()
operator|.
name|length
assert|;
comment|// defensive copy
name|this
operator|.
name|values
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns a copy of the (raw, unmarshalled) sort values used by this object, or     * null if first page of docs should be returned (ie: no sort after)    */
DECL|method|getSortValues
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|getSortValues
parameter_list|()
block|{
comment|// defensive copy
return|return
literal|null
operator|==
name|this
operator|.
name|values
condition|?
literal|null
else|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|values
argument_list|)
return|;
block|}
comment|/**    * Returns the SortSpec used by this object.    */
DECL|method|getSortSpec
specifier|public
name|SortSpec
name|getSortSpec
parameter_list|()
block|{
return|return
name|this
operator|.
name|sortSpec
return|;
block|}
comment|/**    * Parses the serialized version of a CursorMark from a client     * (which must conform to the existing sortSpec) and populates this object.    *    * @see #getSerializedTotem    */
DECL|method|parseSerializedTotem
specifier|public
name|void
name|parseSerializedTotem
parameter_list|(
specifier|final
name|String
name|serialized
parameter_list|)
block|{
if|if
condition|(
name|CURSOR_MARK_START
operator|.
name|equals
argument_list|(
name|serialized
argument_list|)
condition|)
block|{
name|values
operator|=
literal|null
expr_stmt|;
return|return;
block|}
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|sortSpec
operator|.
name|getSort
argument_list|()
operator|.
name|getSort
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SchemaField
argument_list|>
name|schemaFields
init|=
name|sortSpec
operator|.
name|getSchemaFields
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|pieces
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|rawData
init|=
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|serialized
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|rawData
argument_list|)
decl_stmt|;
try|try
block|{
name|pieces
operator|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|codec
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unable to parse '"
operator|+
name|CURSOR_MARK_PARAM
operator|+
literal|"' after totem: "
operator|+
literal|"value must either be '"
operator|+
name|CURSOR_MARK_START
operator|+
literal|"' or the "
operator|+
literal|"'"
operator|+
name|CURSOR_MARK_NEXT
operator|+
literal|"' returned by a previous search: "
operator|+
name|serialized
argument_list|,
name|ex
argument_list|)
throw|;
block|}
assert|assert
literal|null
operator|!=
name|pieces
operator|:
literal|"pieces wasn't parsed?"
assert|;
if|if
condition|(
name|sortFields
operator|.
name|length
operator|!=
name|pieces
operator|.
name|size
argument_list|()
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
name|CURSOR_MARK_PARAM
operator|+
literal|" does not work with current sort (wrong size): "
operator|+
name|serialized
argument_list|)
throw|;
block|}
name|this
operator|.
name|values
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|sortFields
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|tmpBytes
init|=
operator|new
name|BytesRef
argument_list|()
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
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SortField
name|curSort
init|=
name|sortFields
index|[
name|i
index|]
decl_stmt|;
name|SchemaField
name|curField
init|=
name|schemaFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|rawValue
init|=
name|pieces
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|curField
condition|)
block|{
name|FieldType
name|curType
init|=
name|curField
operator|.
name|getType
argument_list|()
decl_stmt|;
name|rawValue
operator|=
name|curType
operator|.
name|unmarshalSortValue
argument_list|(
name|rawValue
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|values
operator|.
name|add
argument_list|(
name|rawValue
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Generates a Base64 encoded serialized representation of the sort values     * encapsulated by this object, for use in cursor requests.    *    * @see #parseSerializedTotem    */
DECL|method|getSerializedTotem
specifier|public
name|String
name|getSerializedTotem
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|this
operator|.
name|values
condition|)
block|{
return|return
name|CURSOR_MARK_START
return|;
block|}
specifier|final
name|List
argument_list|<
name|SchemaField
argument_list|>
name|schemaFields
init|=
name|sortSpec
operator|.
name|getSchemaFields
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|marshalledValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|values
operator|.
name|size
argument_list|()
operator|+
literal|1
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
name|schemaFields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|fld
init|=
name|schemaFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|safeValue
init|=
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|fld
condition|)
block|{
name|FieldType
name|type
init|=
name|fld
operator|.
name|getType
argument_list|()
decl_stmt|;
name|safeValue
operator|=
name|type
operator|.
name|marshalSortValue
argument_list|(
name|safeValue
argument_list|)
expr_stmt|;
block|}
name|marshalledValues
operator|.
name|add
argument_list|(
name|safeValue
argument_list|)
expr_stmt|;
block|}
comment|// TODO: we could also encode info about the SortSpec for error checking:
comment|// the type/name/dir from the SortFields (or a hashCode to act as a checksum)
comment|// could help provide more validation beyond just the number of clauses.
try|try
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|256
argument_list|)
decl_stmt|;
try|try
block|{
name|codec
operator|.
name|marshal
argument_list|(
name|marshalledValues
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|byte
index|[]
name|rawData
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|rawData
argument_list|,
literal|0
argument_list|,
name|rawData
operator|.
name|length
argument_list|)
return|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to format search after totem"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns a synthetically constructed {@link FieldDoc} whose {@link FieldDoc#fields}     * match the values of this object.      *<p>    * Important Notes:    *</p>    *<ul>    *<li>{@link FieldDoc#doc} will always be set to {@link Integer#MAX_VALUE} so     *    that the tie breaking logic used by<code>IndexSearcher</code> won't select     *    the same doc again based on the internal lucene docId when the Solr     *<code>uniqueKey</code> value is the same.    *</li>    *<li>{@link FieldDoc#score} will always be set to 0.0F since it is not used    *    when applying<code>searchAfter</code> logic. (Even if the sort values themselves     *    contain scores which are used in the sort)    *</li>    *</ul>    *    * @return a {@link FieldDoc} to "search after" or null if the initial     *         page of results is requested.    */
DECL|method|getSearchAfterFieldDoc
specifier|public
name|FieldDoc
name|getSearchAfterFieldDoc
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|values
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|FieldDoc
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0.0F
argument_list|,
name|values
operator|.
name|toArray
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

