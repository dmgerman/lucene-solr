begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|Bits
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
name|MergedIterator
import|;
end_import

begin_comment
comment|/**  * Exposes flex API, merged from flex API of sub-segments.  * This is useful when you're interacting with an {@link  * IndexReader} implementation that consists of sequential  * sub-readers (eg {@link DirectoryReader} or {@link  * MultiReader}).  *  *<p><b>NOTE</b>: for composite readers, you'll get better  * performance by gathering the sub readers using  * {@link IndexReader#getContext()} to get the  * atomic leaves and then operate per-LeafReader,  * instead of using this class.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiFields
specifier|public
specifier|final
class|class
name|MultiFields
extends|extends
name|Fields
block|{
DECL|field|subs
specifier|private
specifier|final
name|Fields
index|[]
name|subs
decl_stmt|;
DECL|field|subSlices
specifier|private
specifier|final
name|ReaderSlice
index|[]
name|subSlices
decl_stmt|;
DECL|field|terms
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
name|terms
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Returns a single {@link Fields} instance for this    *  reader, merging fields/terms/docs/positions on the    *  fly.  This method will return null if the reader     *  has no postings.    *    *<p><b>NOTE</b>: this is a slow way to access postings.    *  It's better to get the sub-readers and iterate through them    *  yourself. */
DECL|method|getFields
specifier|public
specifier|static
name|Fields
name|getFields
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|leaves
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
comment|// already an atomic reader / reader with one leave
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
return|;
default|default:
specifier|final
name|List
argument_list|<
name|Fields
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderSlice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|LeafReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
specifier|final
name|LeafReader
name|r
init|=
name|ctx
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|Fields
name|f
init|=
name|r
operator|.
name|fields
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|slices
operator|.
name|add
argument_list|(
operator|new
name|ReaderSlice
argument_list|(
name|ctx
operator|.
name|docBase
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|fields
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MultiFields
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
name|Fields
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|slices
operator|.
name|toArray
argument_list|(
name|ReaderSlice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Returns a single {@link Bits} instance for this    *  reader, merging live Documents on the    *  fly.  This method will return null if the reader     *  has no deletions.    *    *<p><b>NOTE</b>: this is a very slow way to access live docs.    *  For example, each Bits access will require a binary search.    *  It's better to get the sub-readers and iterate through them    *  yourself. */
DECL|method|getLiveDocs
specifier|public
specifier|static
name|Bits
name|getLiveDocs
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
assert|assert
name|size
operator|>
literal|0
operator|:
literal|"A reader with deletions must have at least one leave"
assert|;
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
return|;
block|}
specifier|final
name|Bits
index|[]
name|liveDocs
init|=
operator|new
name|Bits
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
comment|// record all liveDocs, even if they are null
specifier|final
name|LeafReaderContext
name|ctx
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|liveDocs
index|[
name|i
index|]
operator|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|ctx
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiBits
argument_list|(
name|liveDocs
argument_list|,
name|starts
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**  This method may return null if the field does not exist.*/
DECL|method|getTerms
specifier|public
specifier|static
name|Terms
name|getTerms
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFields
argument_list|(
name|r
argument_list|)
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns {@link PostingsEnum} for the specified field and    *  term.  This will return null if the field or term does    *  not exist. */
DECL|method|getTermDocsEnum
specifier|public
specifier|static
name|PostingsEnum
name|getTermDocsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTermDocsEnum
argument_list|(
name|r
argument_list|,
name|field
argument_list|,
name|term
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
return|;
block|}
comment|/** Returns {@link PostingsEnum} for the specified field and    *  term, with control over whether freqs are required.    *  Some codecs may be able to optimize their    *  implementation when freqs are not required.  This will    *  return null if the field or term does not exist.  See {@link    *  TermsEnum#postings(PostingsEnum,int)}.*/
DECL|method|getTermDocsEnum
specifier|public
specifier|static
name|PostingsEnum
name|getTermDocsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|!=
literal|null
assert|;
specifier|final
name|Terms
name|terms
init|=
name|getTerms
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|flags
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns {@link PostingsEnum} for the specified    *  field and term.  This will return null if the field or    *  term does not exist or positions were not indexed.     *  @see #getTermPositionsEnum(IndexReader, String, BytesRef, int) */
DECL|method|getTermPositionsEnum
specifier|public
specifier|static
name|PostingsEnum
name|getTermPositionsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTermPositionsEnum
argument_list|(
name|r
argument_list|,
name|field
argument_list|,
name|term
argument_list|,
name|PostingsEnum
operator|.
name|ALL
argument_list|)
return|;
block|}
comment|/** Returns {@link PostingsEnum} for the specified    *  field and term, with control over whether offsets and payloads are    *  required.  Some codecs may be able to optimize    *  their implementation when offsets and/or payloads are not    *  required. This will return null if the field or term does not    *  exist. See {@link TermsEnum#postings(PostingsEnum,int)}. */
DECL|method|getTermPositionsEnum
specifier|public
specifier|static
name|PostingsEnum
name|getTermPositionsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|!=
literal|null
assert|;
specifier|final
name|Terms
name|terms
init|=
name|getTerms
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|flags
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Expert: construct a new MultiFields instance directly.    * @lucene.internal    */
comment|// TODO: why is this public?
DECL|method|MultiFields
specifier|public
name|MultiFields
parameter_list|(
name|Fields
index|[]
name|subs
parameter_list|,
name|ReaderSlice
index|[]
name|subSlices
parameter_list|)
block|{
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|subSlices
operator|=
name|subSlices
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|subIterators
index|[]
init|=
operator|new
name|Iterator
index|[
name|subs
operator|.
name|length
index|]
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subIterators
index|[
name|i
index|]
operator|=
name|subs
index|[
name|i
index|]
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|MergedIterator
argument_list|<>
argument_list|(
name|subIterators
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|result
init|=
name|terms
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
name|result
return|;
comment|// Lazy init: first time this field is requested, we
comment|// create& add to terms:
specifier|final
name|List
argument_list|<
name|Terms
argument_list|>
name|subs2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderSlice
argument_list|>
name|slices2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Gather all sub-readers that share this field
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|subs
index|[
name|i
index|]
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|subs2
operator|.
name|add
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|slices2
operator|.
name|add
argument_list|(
name|subSlices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|subs2
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
operator|=
literal|null
expr_stmt|;
comment|// don't cache this case with an unbounded cache, since the number of fields that don't exist
comment|// is unbounded.
block|}
else|else
block|{
name|result
operator|=
operator|new
name|MultiTerms
argument_list|(
name|subs2
operator|.
name|toArray
argument_list|(
name|Terms
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|slices2
operator|.
name|toArray
argument_list|(
name|ReaderSlice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|terms
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/** Call this to get the (merged) FieldInfos for a    *  composite reader.     *<p>    *  NOTE: the returned field numbers will likely not    *  correspond to the actual field numbers in the underlying    *  readers, and codec metadata ({@link FieldInfo#getAttribute(String)}    *  will be unavailable.    */
DECL|method|getMergedFieldInfos
specifier|public
specifier|static
name|FieldInfos
name|getMergedFieldInfos
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
specifier|final
name|FieldInfos
operator|.
name|Builder
name|builder
init|=
operator|new
name|FieldInfos
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|LeafReaderContext
name|ctx
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getFieldInfos
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|finish
argument_list|()
return|;
block|}
comment|/** Call this to get the (merged) FieldInfos representing the    *  set of indexed fields<b>only</b> for a composite reader.     *<p>    *  NOTE: the returned field numbers will likely not    *  correspond to the actual field numbers in the underlying    *  readers, and codec metadata ({@link FieldInfo#getAttribute(String)}    *  will be unavailable.    */
DECL|method|getIndexedFields
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|getIndexedFields
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|FieldInfo
name|fieldInfo
range|:
name|getMergedFieldInfos
argument_list|(
name|reader
argument_list|)
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
block|}
end_class

end_unit

