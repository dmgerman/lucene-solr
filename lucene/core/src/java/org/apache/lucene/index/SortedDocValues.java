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

begin_class
DECL|class|SortedDocValues
specifier|public
specifier|abstract
class|class
name|SortedDocValues
extends|extends
name|BinaryDocValues
block|{
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
name|result
operator|.
name|bytes
operator|=
name|MISSING
expr_stmt|;
name|result
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
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
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|SortedDocValues
name|EMPTY
init|=
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
literal|0
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
name|length
operator|=
literal|0
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
literal|1
return|;
block|}
block|}
decl_stmt|;
comment|/** If {@code key} exists, returns its ordinal, else    *  returns {@code -insertionPoint-1}, like {@code    *  Arrays.binarySearch}.    *    *  @param key Key to look up    *  @param spare Spare BytesRef    **/
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

