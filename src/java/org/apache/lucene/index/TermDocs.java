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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/** TermDocs provides an interface for enumerating&lt;document, frequency&gt;  pairs for a term.<p> The document portion names each document containing  the term.  Documents are indicated by number.  The frequency portion gives  the number of times the term occurred in each document.<p> The pairs are  ordered by document number.   @see IndexReader#termDocs()  */
end_comment

begin_interface
DECL|interface|TermDocs
specifier|public
interface|interface
name|TermDocs
block|{
comment|/** Sets this to the data for a term.    * The enumeration is reset to the start of the data for this term.    */
DECL|method|seek
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Sets this to the data for the current term in a {@link TermEnum}.    * This may be optimized in some implementations.    */
DECL|method|seek
name|void
name|seek
parameter_list|(
name|TermEnum
name|termEnum
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the current document number.<p> This is invalid until {@link    #next()} is called for the first time.*/
DECL|method|doc
name|int
name|doc
parameter_list|()
function_decl|;
comment|/** Returns the frequency of the term within the current document.<p> This    is invalid until {@link #next()} is called for the first time.*/
DECL|method|freq
name|int
name|freq
parameter_list|()
function_decl|;
comment|/** Moves to the next pair in the enumeration.<p> Returns true iff there is    such a next pair in the enumeration. */
DECL|method|next
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Attempts to read multiple entries from the enumeration, up to length of    *<i>docs</i>.  Document numbers are stored in<i>docs</i>, and term    * frequencies are stored in<i>freqs</i>.  The<i>freqs</i> array must be as    * long as the<i>docs</i> array.    *    *<p>Returns the number of entries read.  Zero is only returned when the    * stream has been exhausted.  */
DECL|method|read
name|int
name|read
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Skips entries to the first beyond the current whose document number is    * greater than or equal to<i>target</i>.<p>Returns true iff there is such    * an entry.<p>Behaves as if written:<pre>    *   boolean skipTo(int target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> doc());    *     return true;    *   }    *</pre>    * Some implementations are considerably more efficient than that.    */
DECL|method|skipTo
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Frees associated resources. */
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

