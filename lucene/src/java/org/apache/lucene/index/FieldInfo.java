begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|values
operator|.
name|ValueType
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** @lucene.experimental */
end_comment

begin_class
DECL|class|FieldInfo
specifier|public
specifier|final
class|class
name|FieldInfo
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|number
specifier|public
specifier|final
name|int
name|number
decl_stmt|;
DECL|field|isIndexed
specifier|public
name|boolean
name|isIndexed
decl_stmt|;
DECL|field|docValues
name|ValueType
name|docValues
decl_stmt|;
comment|// true if term vector for this field should be stored
DECL|field|storeTermVector
specifier|public
name|boolean
name|storeTermVector
decl_stmt|;
DECL|field|storeOffsetWithTermVector
specifier|public
name|boolean
name|storeOffsetWithTermVector
decl_stmt|;
DECL|field|storePositionWithTermVector
specifier|public
name|boolean
name|storePositionWithTermVector
decl_stmt|;
DECL|field|omitNorms
specifier|public
name|boolean
name|omitNorms
decl_stmt|;
comment|// omit norms associated with indexed fields
DECL|field|indexOptions
specifier|public
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|field|storePayloads
specifier|public
name|boolean
name|storePayloads
decl_stmt|;
comment|// whether this field stores payloads together with term positions
comment|/**    * Controls how much information is stored in the postings lists.    * @lucene.experimental    */
DECL|enum|IndexOptions
specifier|public
specifier|static
enum|enum
name|IndexOptions
block|{
comment|/** only documents are indexed: term frequencies and positions are omitted */
DECL|enum constant|DOCS_ONLY
name|DOCS_ONLY
block|,
comment|/** only documents and term frequencies are indexed: positions are omitted */
DECL|enum constant|DOCS_AND_FREQS
name|DOCS_AND_FREQS
block|,
comment|/** full postings: documents, frequencies, and positions */
DECL|enum constant|DOCS_AND_FREQS_AND_POSITIONS
name|DOCS_AND_FREQS_AND_POSITIONS
block|}
empty_stmt|;
DECL|method|FieldInfo
name|FieldInfo
parameter_list|(
name|String
name|na
parameter_list|,
name|boolean
name|tk
parameter_list|,
name|int
name|nu
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|,
name|ValueType
name|docValues
parameter_list|)
block|{
name|name
operator|=
name|na
expr_stmt|;
name|isIndexed
operator|=
name|tk
expr_stmt|;
name|number
operator|=
name|nu
expr_stmt|;
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
if|if
condition|(
name|isIndexed
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
name|storeOffsetWithTermVector
expr_stmt|;
name|this
operator|.
name|storePositionWithTermVector
operator|=
name|storePositionWithTermVector
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
name|storePayloads
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|indexOptions
expr_stmt|;
block|}
else|else
block|{
comment|// for non-indexed fields, leave defaults
name|this
operator|.
name|storeTermVector
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|storePositionWithTermVector
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
block|}
assert|assert
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
operator|||
operator|!
name|storePayloads
assert|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|FieldInfo
name|clone
init|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|number
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValues
argument_list|)
decl_stmt|;
return|return
name|clone
return|;
block|}
comment|// should only be called by FieldInfos#addOrUpdate
DECL|method|update
name|void
name|update
parameter_list|(
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|isIndexed
operator|!=
name|isIndexed
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
comment|// once indexed, always index
block|}
if|if
condition|(
name|isIndexed
condition|)
block|{
comment|// if updated field data is not for indexing, leave the updates out
if|if
condition|(
name|this
operator|.
name|storeTermVector
operator|!=
name|storeTermVector
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|storePositionWithTermVector
operator|!=
name|storePositionWithTermVector
condition|)
block|{
name|this
operator|.
name|storePositionWithTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|storeOffsetWithTermVector
operator|!=
name|storeOffsetWithTermVector
condition|)
block|{
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|storePayloads
operator|!=
name|storePayloads
condition|)
block|{
name|this
operator|.
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|omitNorms
operator|!=
name|omitNorms
condition|)
block|{
name|this
operator|.
name|omitNorms
operator|=
literal|true
expr_stmt|;
comment|// if one require omitNorms at least once, it remains off for life
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|!=
name|indexOptions
condition|)
block|{
comment|// downgrade
name|this
operator|.
name|indexOptions
operator|=
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|indexOptions
argument_list|)
operator|<
literal|0
condition|?
name|this
operator|.
name|indexOptions
else|:
name|indexOptions
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
block|}
block|}
assert|assert
name|this
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
operator|||
operator|!
name|this
operator|.
name|storePayloads
assert|;
block|}
DECL|method|setDocValues
name|void
name|setDocValues
parameter_list|(
name|ValueType
name|v
parameter_list|)
block|{
if|if
condition|(
name|docValues
operator|==
literal|null
condition|)
block|{
name|docValues
operator|=
name|v
expr_stmt|;
block|}
block|}
DECL|method|resetDocValues
specifier|public
name|void
name|resetDocValues
parameter_list|(
name|ValueType
name|v
parameter_list|)
block|{
if|if
condition|(
name|docValues
operator|!=
literal|null
condition|)
block|{
name|docValues
operator|=
name|v
expr_stmt|;
block|}
block|}
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|docValues
operator|!=
literal|null
return|;
block|}
DECL|method|getDocValues
specifier|public
name|ValueType
name|getDocValues
parameter_list|()
block|{
return|return
name|docValues
return|;
block|}
DECL|field|vectorsCommitted
specifier|private
name|boolean
name|vectorsCommitted
decl_stmt|;
DECL|field|docValuesCommitted
specifier|private
name|boolean
name|docValuesCommitted
decl_stmt|;
comment|/**    * Reverts all uncommitted changes on this {@link FieldInfo}    * @see #commitVectors()    */
DECL|method|revertUncommitted
name|void
name|revertUncommitted
parameter_list|()
block|{
if|if
condition|(
name|storeTermVector
operator|&&
operator|!
name|vectorsCommitted
condition|)
block|{
name|storeOffsetWithTermVector
operator|=
literal|false
expr_stmt|;
name|storePositionWithTermVector
operator|=
literal|false
expr_stmt|;
name|storeTermVector
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|docValues
operator|!=
literal|null
operator|&&
operator|!
name|docValuesCommitted
condition|)
block|{
name|docValues
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Commits term vector modifications. Changes to term-vectors must be    * explicitly committed once the necessary files are created. If those changes    * are not committed subsequent {@link #revertUncommitted()} will reset the    * all term-vector flags before the next document.    */
DECL|method|commitVectors
name|void
name|commitVectors
parameter_list|()
block|{
assert|assert
name|storeTermVector
assert|;
name|vectorsCommitted
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|commitDocValues
name|void
name|commitDocValues
parameter_list|()
block|{
assert|assert
name|hasDocValues
argument_list|()
assert|;
name|docValuesCommitted
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

