begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktree
operator|.
name|BlockTreeTermsWriter
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
name|SegmentWriteState
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
name|store
operator|.
name|DataOutput
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
name|store
operator|.
name|IndexOutput
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
name|FixedBitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
comment|/**  * Class that plugs into term dictionaries, such as {@link  * BlockTreeTermsWriter}, and handles writing postings.  *   * @see PostingsReaderBase  * @lucene.experimental  */
end_comment

begin_comment
comment|// TODO: find a better name; this defines the API that the
end_comment

begin_comment
comment|// terms dict impls use to talk to a postings impl.
end_comment

begin_comment
comment|// TermsDict + PostingsReader/WriterBase == FieldsProducer/Consumer
end_comment

begin_class
DECL|class|PostingsWriterBase
specifier|public
specifier|abstract
class|class
name|PostingsWriterBase
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|PostingsWriterBase
specifier|protected
name|PostingsWriterBase
parameter_list|()
block|{   }
comment|/** Called once after startup, before any terms have been    *  added.  Implementations typically write a header to    *  the provided {@code termsOut}. */
DECL|method|init
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Write all postings for one term; use the provided    *  {@link TermsEnum} to pull a {@link org.apache.lucene.index.PostingsEnum}.    *  This method should not    *  re-position the {@code TermsEnum}!  It is already    *  positioned on the term that should be written.  This    *  method must set the bit in the provided {@link    *  FixedBitSet} for every docID written.  If no docs    *  were written, this method should return null, and the    *  terms dict will skip the term. */
DECL|method|writeTerm
specifier|public
specifier|abstract
name|BlockTermState
name|writeTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|,
name|FixedBitSet
name|docsSeen
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Encode metadata as long[] and byte[]. {@code absolute} controls whether     * current term is delta encoded according to latest term.     * Usually elements in {@code longs} are file pointers, so each one always     * increases when a new term is consumed. {@code out} is used to write generic    * bytes, which are not monotonic.    *    * NOTE: sometimes long[] might contain "don't care" values that are unused, e.g.     * the pointer to postings list may not be defined for some terms but is defined    * for others, if it is designed to inline  some postings data in term dictionary.    * In this case, the postings writer should always use the last value, so that each    * element in metadata long[] remains monotonic.    */
DECL|method|encodeTerm
specifier|public
specifier|abstract
name|void
name|encodeTerm
parameter_list|(
name|long
index|[]
name|longs
parameter_list|,
name|DataOutput
name|out
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|state
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Sets the current field for writing, and returns the    * fixed length of long[] metadata (which is fixed per    * field), called when the writing switches to another field. */
comment|// TODO: better name?
DECL|method|setField
specifier|public
specifier|abstract
name|int
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

