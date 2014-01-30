begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Collections
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
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
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
name|util
operator|.
name|DataInputInputStream
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
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_comment
comment|/**  * Provides methods for marshalling an UpdateRequest to a NamedList which can be serialized in the javabin format and  * vice versa.  *  *  * @see org.apache.solr.common.util.JavaBinCodec  * @since solr 1.4  */
end_comment

begin_class
DECL|class|JavaBinUpdateRequestCodec
specifier|public
class|class
name|JavaBinUpdateRequestCodec
block|{
comment|/**    * Converts an UpdateRequest to a NamedList which can be serialized to the given OutputStream in the javabin format    *    * @param updateRequest the UpdateRequest to be written out    * @param os            the OutputStream to which the request is to be written    *    * @throws IOException in case of an exception during marshalling or writing to the stream    */
DECL|method|marshal
specifier|public
name|void
name|marshal
parameter_list|(
name|UpdateRequest
name|updateRequest
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|NamedList
name|params
init|=
name|solrParamsToNamedList
argument_list|(
name|updateRequest
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|updateRequest
operator|.
name|getCommitWithin
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"commitWithin"
argument_list|,
name|updateRequest
operator|.
name|getCommitWithin
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|docIter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|updateRequest
operator|.
name|getDocIterator
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|docIter
operator|=
name|updateRequest
operator|.
name|getDocIterator
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docMap
init|=
name|updateRequest
operator|.
name|getDocumentsMap
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"params"
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|// 0: params
if|if
condition|(
name|updateRequest
operator|.
name|getDeleteByIdMap
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|nl
operator|.
name|add
argument_list|(
literal|"delByIdMap"
argument_list|,
name|updateRequest
operator|.
name|getDeleteByIdMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nl
operator|.
name|add
argument_list|(
literal|"delByQ"
argument_list|,
name|updateRequest
operator|.
name|getDeleteQuery
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|docMap
operator|!=
literal|null
condition|)
block|{
name|nl
operator|.
name|add
argument_list|(
literal|"docsMap"
argument_list|,
name|docMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|docIter
operator|=
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|nl
operator|.
name|add
argument_list|(
literal|"docs"
argument_list|,
name|docIter
argument_list|)
expr_stmt|;
block|}
name|JavaBinCodec
name|codec
init|=
operator|new
name|JavaBinCodec
argument_list|()
decl_stmt|;
name|codec
operator|.
name|marshal
argument_list|(
name|nl
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads a NamedList from the given InputStream, converts it into a SolrInputDocument and passes it to the given    * StreamingUpdateHandler    *    * @param is      the InputStream from which to read    * @param handler an instance of StreamingUpdateHandler to which SolrInputDocuments are streamed one by one    *    * @return the UpdateRequest    *    * @throws IOException in case of an exception while reading from the input stream or unmarshalling    */
DECL|method|unmarshal
specifier|public
name|UpdateRequest
name|unmarshal
parameter_list|(
name|InputStream
name|is
parameter_list|,
specifier|final
name|StreamingUpdateHandler
name|handler
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|>
argument_list|>
name|doclist
decl_stmt|;
name|List
argument_list|<
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|docMap
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|delById
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|delByIdMap
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|delByQ
decl_stmt|;
specifier|final
name|NamedList
index|[]
name|namedList
init|=
operator|new
name|NamedList
index|[
literal|1
index|]
decl_stmt|;
name|JavaBinCodec
name|codec
init|=
operator|new
name|JavaBinCodec
argument_list|()
block|{
comment|// NOTE: this only works because this is an anonymous inner class
comment|// which will only ever be used on a single stream -- if this class
comment|// is ever refactored, this will not work.
specifier|private
name|boolean
name|seenOuterMostDocIterator
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|NamedList
name|readNamedList
parameter_list|(
name|DataInputInputStream
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|sz
init|=
name|readSize
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
if|if
condition|(
name|namedList
index|[
literal|0
index|]
operator|==
literal|null
condition|)
block|{
name|namedList
index|[
literal|0
index|]
operator|=
name|nl
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|readVal
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|Object
name|val
init|=
name|readVal
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|nl
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
name|readIterator
parameter_list|(
name|DataInputInputStream
name|fis
parameter_list|)
throws|throws
name|IOException
block|{
comment|// default behavior for reading any regular Iterator in the stream
if|if
condition|(
name|seenOuterMostDocIterator
condition|)
return|return
name|super
operator|.
name|readIterator
argument_list|(
name|fis
argument_list|)
return|;
comment|// special treatment for first outermost Iterator
comment|// (the list of documents)
name|seenOuterMostDocIterator
operator|=
literal|true
expr_stmt|;
return|return
name|readOuterMostDocIterator
argument_list|(
name|fis
argument_list|)
return|;
block|}
specifier|private
name|List
name|readOuterMostDocIterator
parameter_list|(
name|DataInputInputStream
name|fis
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
name|params
init|=
operator|(
name|NamedList
operator|)
name|namedList
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
name|updateRequest
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|(
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
return|return
name|super
operator|.
name|readIterator
argument_list|(
name|fis
argument_list|)
return|;
name|Integer
name|commitWithin
init|=
literal|null
decl_stmt|;
name|Boolean
name|overwrite
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Object
name|o
init|=
name|readVal
argument_list|(
name|fis
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
name|END_OBJ
condition|)
break|break;
name|SolrInputDocument
name|sdoc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|sdoc
operator|=
name|listToSolrInputDocument
argument_list|(
operator|(
name|List
argument_list|<
name|NamedList
argument_list|>
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|(
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|update
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Map
operator|.
name|Entry
condition|)
block|{
name|sdoc
operator|=
call|(
name|SolrInputDocument
call|)
argument_list|(
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
argument_list|)
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|Map
name|p
init|=
call|(
name|Map
call|)
argument_list|(
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|commitWithin
operator|=
operator|(
name|Integer
operator|)
name|p
operator|.
name|get
argument_list|(
name|UpdateRequest
operator|.
name|COMMIT_WITHIN
argument_list|)
expr_stmt|;
name|overwrite
operator|=
operator|(
name|Boolean
operator|)
name|p
operator|.
name|get
argument_list|(
name|UpdateRequest
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sdoc
operator|=
operator|(
name|SolrInputDocument
operator|)
name|o
expr_stmt|;
block|}
name|handler
operator|.
name|update
argument_list|(
name|sdoc
argument_list|,
name|updateRequest
argument_list|,
name|commitWithin
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
block|}
decl_stmt|;
name|codec
operator|.
name|unmarshal
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|// NOTE: if the update request contains only delete commands the params
comment|// must be loaded now
if|if
condition|(
name|updateRequest
operator|.
name|getParams
argument_list|()
operator|==
literal|null
condition|)
block|{
name|NamedList
name|params
init|=
operator|(
name|NamedList
operator|)
name|namedList
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|updateRequest
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|(
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|delById
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|namedList
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
literal|"delById"
argument_list|)
expr_stmt|;
name|delByIdMap
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|namedList
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
literal|"delByIdMap"
argument_list|)
expr_stmt|;
name|delByQ
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|namedList
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
literal|"delByQ"
argument_list|)
expr_stmt|;
name|doclist
operator|=
operator|(
name|List
operator|)
name|namedList
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
expr_stmt|;
name|docMap
operator|=
operator|(
name|List
argument_list|<
name|Entry
argument_list|<
name|SolrInputDocument
argument_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|namedList
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
literal|"docsMap"
argument_list|)
expr_stmt|;
comment|// we don't add any docs, because they were already processed
comment|// deletes are handled later, and must be passed back on the UpdateRequest
if|if
condition|(
name|delById
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|delById
control|)
block|{
name|updateRequest
operator|.
name|deleteById
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|delByIdMap
operator|!=
literal|null
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|delByIdMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|Long
name|version
init|=
operator|(
name|Long
operator|)
name|params
operator|.
name|get
argument_list|(
name|UpdateRequest
operator|.
name|VER
argument_list|)
decl_stmt|;
name|updateRequest
operator|.
name|deleteById
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateRequest
operator|.
name|deleteById
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|delByQ
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|delByQ
control|)
block|{
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|updateRequest
return|;
block|}
DECL|method|listToSolrInputDocument
specifier|private
name|SolrInputDocument
name|listToSolrInputDocument
parameter_list|(
name|List
argument_list|<
name|NamedList
argument_list|>
name|namedList
parameter_list|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
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
name|namedList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
name|nl
init|=
name|namedList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|doc
operator|.
name|setDocumentBoost
argument_list|(
name|nl
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
operator|==
literal|null
condition|?
literal|1.0f
else|:
operator|(
name|Float
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|addField
argument_list|(
operator|(
name|String
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
argument_list|,
name|nl
operator|.
name|getVal
argument_list|(
literal|1
argument_list|)
argument_list|,
name|nl
operator|.
name|getVal
argument_list|(
literal|2
argument_list|)
operator|==
literal|null
condition|?
literal|1.0f
else|:
operator|(
name|Float
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
return|;
block|}
DECL|method|solrParamsToNamedList
specifier|private
name|NamedList
name|solrParamsToNamedList
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
return|return
operator|new
name|NamedList
argument_list|()
return|;
return|return
name|params
operator|.
name|toNamedList
argument_list|()
return|;
block|}
DECL|interface|StreamingUpdateHandler
specifier|public
specifier|static
interface|interface
name|StreamingUpdateHandler
block|{
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|,
name|UpdateRequest
name|req
parameter_list|,
name|Integer
name|commitWithin
parameter_list|,
name|Boolean
name|override
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

