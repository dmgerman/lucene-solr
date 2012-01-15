begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|directory
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CorruptIndexException
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
name|MultiFields
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|// getParent() needs to be extremely efficient, to the point that we need
end_comment

begin_comment
comment|// to fetch all the data in advance into memory, and answer these calls
end_comment

begin_comment
comment|// from memory. Currently we use a large integer array, which is
end_comment

begin_comment
comment|// initialized when the taxonomy is opened, and potentially enlarged
end_comment

begin_comment
comment|// when it is refresh()ed.
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ParentArray
class|class
name|ParentArray
block|{
comment|// These arrays are not syncrhonized. Rather, the reference to the array
comment|// is volatile, and the only writing operation (refreshPrefetchArrays)
comment|// simply creates a new array and replaces the reference. The volatility
comment|// of the reference ensures the correct atomic replacement and its
comment|// visibility properties (the content of the array is visible when the
comment|// new reference is visible).
DECL|field|prefetchParentOrdinal
specifier|private
specifier|volatile
name|int
name|prefetchParentOrdinal
index|[]
init|=
literal|null
decl_stmt|;
DECL|method|getArray
specifier|public
name|int
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|prefetchParentOrdinal
return|;
block|}
comment|/**    * refreshPrefetch() refreshes the parent array. Initially, it fills the    * array from the positions of an appropriate posting list. If called during    * a refresh(), when the arrays already exist, only values for new documents    * (those beyond the last one in the array) are read from the positions and    * added to the arrays (that are appropriately enlarged). We assume (and    * this is indeed a correct assumption in our case) that existing categories    * are never modified or deleted.    */
DECL|method|refresh
name|void
name|refresh
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Note that it is not necessary for us to obtain the read lock.
comment|// The reason is that we are only called from refresh() (precluding
comment|// another concurrent writer) or from the constructor (when no method
comment|// could be running).
comment|// The write lock is also not held during the following code, meaning
comment|// that reads *can* happen while this code is running. The "volatile"
comment|// property of the prefetchParentOrdinal and prefetchDepth array
comment|// references ensure the correct visibility property of the assignment
comment|// but other than that, we do *not* guarantee that a reader will not
comment|// use an old version of one of these arrays (or both) while a refresh
comment|// is going on. But we find this acceptable - until a refresh has
comment|// finished, the reader should not expect to see new information
comment|// (and the old information is the same in the old and new versions).
name|int
name|first
decl_stmt|;
name|int
name|num
init|=
name|indexReader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefetchParentOrdinal
operator|==
literal|null
condition|)
block|{
name|prefetchParentOrdinal
operator|=
operator|new
name|int
index|[
name|num
index|]
expr_stmt|;
comment|// Starting Lucene 2.9, following the change LUCENE-1542, we can
comment|// no longer reliably read the parent "-1" (see comment in
comment|// LuceneTaxonomyWriter.SinglePositionTokenStream). We have no way
comment|// to fix this in indexing without breaking backward-compatibility
comment|// with existing indexes, so what we'll do instead is just
comment|// hard-code the parent of ordinal 0 to be -1, and assume (as is
comment|// indeed the case) that no other parent can be -1.
if|if
condition|(
name|num
operator|>
literal|0
condition|)
block|{
name|prefetchParentOrdinal
index|[
literal|0
index|]
operator|=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
expr_stmt|;
block|}
name|first
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|first
operator|=
name|prefetchParentOrdinal
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|first
operator|==
name|num
condition|)
block|{
return|return;
comment|// nothing to do - no category was added
block|}
comment|// In Java 6, we could just do Arrays.copyOf()...
name|int
index|[]
name|newarray
init|=
operator|new
name|int
index|[
name|num
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|prefetchParentOrdinal
argument_list|,
literal|0
argument_list|,
name|newarray
argument_list|,
literal|0
argument_list|,
name|prefetchParentOrdinal
operator|.
name|length
argument_list|)
expr_stmt|;
name|prefetchParentOrdinal
operator|=
name|newarray
expr_stmt|;
block|}
comment|// Read the new part of the parents array from the positions:
comment|// TODO (Facet): avoid Multi*?
name|Bits
name|liveDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|positions
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|indexReader
argument_list|,
name|liveDocs
argument_list|,
name|Consts
operator|.
name|FIELD_PAYLOADS
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Consts
operator|.
name|PAYLOAD_PARENT
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|positions
operator|==
literal|null
operator|||
name|positions
operator|.
name|advance
argument_list|(
name|first
argument_list|)
operator|==
name|DocsAndPositionsEnum
operator|.
name|NO_MORE_DOCS
operator|)
operator|&&
name|first
operator|<
name|num
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|first
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
name|first
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
comment|// Note that we know positions.doc()>= i (this is an
comment|// invariant kept throughout this loop)
if|if
condition|(
name|positions
operator|.
name|docID
argument_list|()
operator|==
name|i
condition|)
block|{
if|if
condition|(
name|positions
operator|.
name|freq
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// shouldn't happen
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|i
argument_list|)
throw|;
block|}
comment|// TODO (Facet): keep a local (non-volatile) copy of the prefetchParentOrdinal
comment|// reference, because access to volatile reference is slower (?).
comment|// Note: The positions we get here are one less than the position
comment|// increment we added originally, so we get here the right numbers:
name|prefetchParentOrdinal
index|[
name|i
index|]
operator|=
name|positions
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|positions
operator|.
name|nextDoc
argument_list|()
operator|==
name|DocsAndPositionsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|num
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
throw|;
block|}
break|break;
block|}
block|}
else|else
block|{
comment|// this shouldn't happen
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * add() is used in LuceneTaxonomyWriter, not in LuceneTaxonomyReader.    * It is only called from a synchronized method, so it is not reentrant,    * and also doesn't need to worry about reads happening at the same time.    *     * NOTE: add() and refresh() CANNOT be used together. If you call add(),    * this changes the arrays and refresh() can no longer be used.    */
DECL|method|add
name|void
name|add
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
name|parentOrdinal
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ordinal
operator|>=
name|prefetchParentOrdinal
operator|.
name|length
condition|)
block|{
comment|// grow the array, if necessary.
comment|// In Java 6, we could just do Arrays.copyOf()...
name|int
index|[]
name|newarray
init|=
operator|new
name|int
index|[
name|ordinal
operator|*
literal|2
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|prefetchParentOrdinal
argument_list|,
literal|0
argument_list|,
name|newarray
argument_list|,
literal|0
argument_list|,
name|prefetchParentOrdinal
operator|.
name|length
argument_list|)
expr_stmt|;
name|prefetchParentOrdinal
operator|=
name|newarray
expr_stmt|;
block|}
name|prefetchParentOrdinal
index|[
name|ordinal
index|]
operator|=
name|parentOrdinal
expr_stmt|;
block|}
block|}
end_class

end_unit

