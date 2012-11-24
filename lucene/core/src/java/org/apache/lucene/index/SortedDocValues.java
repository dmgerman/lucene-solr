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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Comparator
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

begin_comment
comment|// nocommit need marker interface?
end_comment

begin_class
DECL|class|SortedDocValues
specifier|public
specifier|abstract
class|class
name|SortedDocValues
extends|extends
name|BinaryDocValues
block|{
comment|// nocommit throws IOE or not?
DECL|method|getOrd
specifier|public
specifier|abstract
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|// nocommit throws IOE or not?
DECL|method|lookupOrd
specifier|public
specifier|abstract
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
function_decl|;
comment|// nocommit throws IOE or not?
comment|// nocommit .getUniqueValueCount?
DECL|method|getValueCount
specifier|public
specifier|abstract
name|int
name|getValueCount
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|int
name|ord
init|=
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
operator|-
literal|1
condition|)
block|{
comment|// nocommit what to do ... maybe we need to return
comment|// BytesRef?
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"doc has no value"
argument_list|)
throw|;
block|}
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|getTermsEnum
specifier|public
name|TermsEnum
name|getTermsEnum
parameter_list|()
block|{
comment|// nocommit who tests this base impl ...
comment|// Default impl just uses the existing API; subclasses
comment|// can specialize:
return|return
operator|new
name|TermsEnum
argument_list|()
block|{
specifier|private
name|int
name|currentOrd
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
comment|/* ignored */
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|getValueCount
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|seekExact
argument_list|(
name|mid
argument_list|)
expr_stmt|;
name|int
name|cmp
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
block|{
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
comment|// key found
block|}
block|}
if|if
condition|(
name|low
operator|==
name|getValueCount
argument_list|()
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
else|else
block|{
name|seekExact
argument_list|(
name|low
argument_list|)
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|getValueCount
argument_list|()
assert|;
name|currentOrd
operator|=
operator|(
name|int
operator|)
name|ord
expr_stmt|;
name|lookupOrd
argument_list|(
name|currentOrd
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|currentOrd
operator|++
expr_stmt|;
if|if
condition|(
name|currentOrd
operator|>=
name|getValueCount
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|lookupOrd
argument_list|(
name|currentOrd
argument_list|,
name|term
argument_list|)
expr_stmt|;
return|return
name|term
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|currentOrd
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|totalTermFreq
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seekExact
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|!=
literal|null
operator|&&
name|state
operator|instanceof
name|OrdTermState
assert|;
name|this
operator|.
name|seekExact
argument_list|(
operator|(
operator|(
name|OrdTermState
operator|)
name|state
operator|)
operator|.
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
name|OrdTermState
name|state
init|=
operator|new
name|OrdTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|ord
operator|=
name|currentOrd
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newRAMInstance
specifier|public
name|SortedDocValues
name|newRAMInstance
parameter_list|()
block|{
comment|// nocommit optimize this
comment|// nocommit, see also BinaryDocValues nocommits
specifier|final
name|int
name|maxDoc
init|=
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxLength
init|=
name|maxLength
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|fixedLength
init|=
name|isFixedLength
argument_list|()
decl_stmt|;
specifier|final
name|int
name|valueCount
init|=
name|getValueCount
argument_list|()
decl_stmt|;
comment|// nocommit used packed ints and so on
specifier|final
name|byte
index|[]
index|[]
name|values
init|=
operator|new
name|byte
index|[
name|valueCount
index|]
index|[]
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|values
operator|.
name|length
condition|;
name|ord
operator|++
control|)
block|{
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|values
index|[
name|ord
index|]
operator|=
operator|new
name|byte
index|[
name|scratch
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
argument_list|,
name|values
index|[
name|ord
index|]
argument_list|,
literal|0
argument_list|,
name|scratch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|docToOrd
init|=
operator|new
name|int
index|[
name|maxDoc
index|]
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|docToOrd
index|[
name|docID
index|]
operator|=
name|getOrd
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|docToOrd
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|.
name|bytes
operator|=
name|values
index|[
name|ord
index|]
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|result
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFixedLength
parameter_list|()
block|{
return|return
name|fixedLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|maxLength
parameter_list|()
block|{
return|return
name|maxLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortedDocValues
name|newRAMInstance
parameter_list|()
block|{
return|return
name|this
return|;
comment|// see the nocommit in BinaryDocValues
block|}
block|}
return|;
block|}
comment|// nocommit binary search lookup?
DECL|class|EMPTY
specifier|public
specifier|static
class|class
name|EMPTY
extends|extends
name|SortedDocValues
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|EMPTY
specifier|public
name|EMPTY
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
literal|1
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
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|isFixedLength
specifier|public
name|boolean
name|isFixedLength
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|maxLength
specifier|public
name|int
name|maxLength
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
comment|// nocommit javadocs
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|,
name|BytesRef
name|spare
parameter_list|)
block|{
comment|// this special case is the reason that Arrays.binarySearch() isn't useful.
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key must not be null"
argument_list|)
throw|;
block|}
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|getValueCount
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|lookupOrd
argument_list|(
name|mid
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|int
name|cmp
init|=
name|spare
operator|.
name|compareTo
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|mid
return|;
comment|// key found
block|}
block|}
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
comment|// key not found.
block|}
block|}
end_class

end_unit

