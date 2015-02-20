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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
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
name|AttributeSource
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
comment|/** Iterates through the postings.  *  NOTE: you must first call {@link #nextDoc} before using  *  any of the per-doc methods. */
end_comment

begin_class
DECL|class|PostingsEnum
specifier|public
specifier|abstract
class|class
name|PostingsEnum
extends|extends
name|DocIdSetIterator
block|{
comment|/**    * Flag to pass to {@link TermsEnum#postings(Bits, PostingsEnum, int)} if you don't    * require per-document postings in the returned enum.    */
DECL|field|NONE
specifier|public
specifier|static
specifier|final
name|int
name|NONE
init|=
literal|0x0
decl_stmt|;
comment|/** Flag to pass to {@link TermsEnum#postings(Bits, PostingsEnum, int)}    *  if you require term frequencies in the returned enum. */
DECL|field|FREQS
specifier|public
specifier|static
specifier|final
name|int
name|FREQS
init|=
literal|0x1
decl_stmt|;
comment|/** Flag to pass to {@link TermsEnum#postings(Bits, PostingsEnum, int)}    * if you require term positions in the returned enum. */
DECL|field|POSITIONS
specifier|public
specifier|static
specifier|final
name|int
name|POSITIONS
init|=
literal|0x3
decl_stmt|;
comment|/** Flag to pass to {@link TermsEnum#postings(Bits, PostingsEnum, int)}    *  if you require offsets in the returned enum. */
DECL|field|OFFSETS
specifier|public
specifier|static
specifier|final
name|int
name|OFFSETS
init|=
literal|0x7
decl_stmt|;
comment|/** Flag to pass to  {@link TermsEnum#postings(Bits, PostingsEnum, int)}    *  if you require payloads in the returned enum. */
DECL|field|PAYLOADS
specifier|public
specifier|static
specifier|final
name|int
name|PAYLOADS
init|=
literal|0xB
decl_stmt|;
comment|/**    * Flag to pass to {@link TermsEnum#postings(Bits, PostingsEnum, int)}    * to get positions, payloads and offsets in the returned enum    */
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|int
name|ALL
init|=
name|POSITIONS
operator||
name|PAYLOADS
decl_stmt|;
comment|/**    * Returns true if the passed in flags require positions to be indexed    * @param flags the postings flags    * @return true if the passed in flags require positions to be indexed    */
DECL|method|requiresPositions
specifier|public
specifier|static
name|boolean
name|requiresPositions
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
return|return
operator|(
operator|(
name|flags
operator|&
name|POSITIONS
operator|)
operator|>=
name|POSITIONS
operator|)
return|;
block|}
DECL|field|atts
specifier|private
name|AttributeSource
name|atts
init|=
literal|null
decl_stmt|;
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|PostingsEnum
specifier|protected
name|PostingsEnum
parameter_list|()
block|{   }
comment|/**    * Returns term frequency in the current document, or 1 if the field was    * indexed with {@link IndexOptions#DOCS}. Do not call this before    * {@link #nextDoc} is first called, nor after {@link #nextDoc} returns    * {@link DocIdSetIterator#NO_MORE_DOCS}.    *     *<p>    *<b>NOTE:</b> if the {@link PostingsEnum} was obtain with {@link #NONE},    * the result of this method is undefined.    */
DECL|method|freq
specifier|public
specifier|abstract
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the related attributes. */
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
if|if
condition|(
name|atts
operator|==
literal|null
condition|)
name|atts
operator|=
operator|new
name|AttributeSource
argument_list|()
expr_stmt|;
return|return
name|atts
return|;
block|}
comment|/**    * Returns the next position, or -1 if positions were not indexed.    * Calling this more than {@link #freq()} times is undefined.    */
DECL|method|nextPosition
specifier|public
specifier|abstract
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns start offset for the current position, or -1    *  if offsets were not indexed. */
DECL|method|startOffset
specifier|public
specifier|abstract
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns end offset for the current position, or -1 if    *  offsets were not indexed. */
DECL|method|endOffset
specifier|public
specifier|abstract
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the payload at this position, or null if no    *  payload was indexed. You should not modify anything     *  (neither members of the returned BytesRef nor bytes     *  in the byte[]). */
DECL|method|getPayload
specifier|public
specifier|abstract
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

