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
name|automaton
operator|.
name|CompiledAutomaton
import|;
end_import

begin_comment
comment|/**  * Access to the terms in a specific field.  See {@link Fields}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Terms
specifier|public
specifier|abstract
class|class
name|Terms
block|{
comment|/** Returns an iterator that will step through all    *  terms. This method will not return null.  If you have    *  a previous TermsEnum, for example from a different    *  field, you can pass it for possible reuse if the    *  implementation can do so. */
DECL|method|iterator
specifier|public
specifier|abstract
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a TermsEnum that iterates over all terms that    *  are accepted by the provided {@link    *  CompiledAutomaton}.  If the<code>startTerm</code> is    *  provided then the returned enum will only accept terms    *><code>startTerm</code>, but you still must call    *  next() first to get to the first term.  Note that the    *  provided<code>startTerm</code> must be accepted by    *  the automaton.    *    *<p><b>NOTE</b>: the returned TermsEnum cannot    * seek</p>. */
DECL|method|intersect
specifier|public
name|TermsEnum
name|intersect
parameter_list|(
name|CompiledAutomaton
name|compiled
parameter_list|,
specifier|final
name|BytesRef
name|startTerm
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: eventually we could support seekCeil/Exact on
comment|// the returned enum, instead of only being able to seek
comment|// at the start
if|if
condition|(
name|compiled
operator|.
name|type
operator|!=
name|CompiledAutomaton
operator|.
name|AUTOMATON_TYPE
operator|.
name|NORMAL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"please use CompiledAutomaton.getTermsEnum instead"
argument_list|)
throw|;
block|}
if|if
condition|(
name|startTerm
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|AutomatonTermsEnum
argument_list|(
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|,
name|compiled
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|AutomatonTermsEnum
argument_list|(
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|,
name|compiled
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|BytesRef
name|nextSeekTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
name|term
operator|=
name|startTerm
expr_stmt|;
block|}
return|return
name|super
operator|.
name|nextSeekTerm
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
comment|/** Return the BytesRef Comparator used to sort terms    *  provided by the iterator.  This method may return null    *  if there are no terms.  This method may be invoked    *  many times; it's best to cache a single instance&    *  reuse it. */
DECL|method|getComparator
specifier|public
specifier|abstract
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of terms for this field, or -1 if this     *  measure isn't stored by the codec. Note that, just like     *  other term measures, this measure does not take deleted     *  documents into account. */
DECL|method|size
specifier|public
specifier|abstract
name|long
name|size
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the sum of {@link TermsEnum#totalTermFreq} for    *  all terms in this field, or -1 if this measure isn't    *  stored by the codec (or if this fields omits term freq    *  and positions).  Note that, just like other term    *  measures, this measure does not take deleted documents    *  into account. */
DECL|method|getSumTotalTermFreq
specifier|public
specifier|abstract
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the sum of {@link TermsEnum#docFreq()} for    *  all terms in this field, or -1 if this measure isn't    *  stored by the codec.  Note that, just like other term    *  measures, this measure does not take deleted documents    *  into account. */
DECL|method|getSumDocFreq
specifier|public
specifier|abstract
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of documents that have at least one    *  term for this field, or -1 if this measure isn't    *  stored by the codec.  Note that, just like other term    *  measures, this measure does not take deleted documents    *  into account. */
DECL|method|getDocCount
specifier|public
specifier|abstract
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns true if documents in this field store offsets. */
DECL|method|hasOffsets
specifier|public
specifier|abstract
name|boolean
name|hasOffsets
parameter_list|()
function_decl|;
comment|/** Returns true if documents in this field store positions. */
DECL|method|hasPositions
specifier|public
specifier|abstract
name|boolean
name|hasPositions
parameter_list|()
function_decl|;
comment|/** Returns true if documents in this field store payloads. */
DECL|method|hasPayloads
specifier|public
specifier|abstract
name|boolean
name|hasPayloads
parameter_list|()
function_decl|;
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|Terms
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Terms
index|[
literal|0
index|]
decl_stmt|;
block|}
end_class

end_unit

