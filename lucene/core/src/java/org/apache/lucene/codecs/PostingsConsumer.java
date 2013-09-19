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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Abstract API that consumes postings for an individual term.  *<p>  * The lifecycle is:  *<ol>  *<li>PostingsConsumer is returned for each term by  *        {@link TermsConsumer#startTerm(BytesRef)}.   *<li>{@link #startDoc(int, int)} is called for each  *        document where the term occurs, specifying id   *        and term frequency for that document.  *<li>If positions are enabled for the field, then  *        {@link #addPosition(int, BytesRef, int, int)}  *        will be called for each occurrence in the   *        document.  *<li>{@link #finishDoc()} is called when the producer  *        is done adding positions to the document.  *</ol>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|PostingsConsumer
specifier|public
specifier|abstract
class|class
name|PostingsConsumer
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|PostingsConsumer
specifier|protected
name|PostingsConsumer
parameter_list|()
block|{   }
comment|/** Adds a new doc in this term.     *<code>freq</code> will be -1 when term frequencies are omitted    * for the field. */
DECL|method|startDoc
specifier|public
specifier|abstract
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Add a new position& payload, and start/end offset.  A    *  null payload means no payload; a non-null payload with    *  zero length also means no payload.  Caller may reuse    *  the {@link BytesRef} for the payload between calls    *  (method must fully consume the payload).<code>startOffset</code>    *  and<code>endOffset</code> will be -1 when offsets are not indexed. */
DECL|method|addPosition
specifier|public
specifier|abstract
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when we are done adding positions& payloads    *  for each doc. */
DECL|method|finishDoc
specifier|public
specifier|abstract
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

