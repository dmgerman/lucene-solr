begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|document
operator|.
name|Document
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
name|Term
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

begin_comment
comment|/** The interface for search implementations.  *  *<p>Implementations provide search over a single index, over multiple  * indices, and over indices on remote servers.  */
end_comment

begin_interface
DECL|interface|Searchable
specifier|public
interface|interface
name|Searchable
extends|extends
name|java
operator|.
name|rmi
operator|.
name|Remote
block|{
comment|/** Lower-level search API.    *    *<p>{@link HitCollector#collect(int,float)} is called for every non-zero    * scoring document.    *    *<p>Applications should only use this if they need<i>all</i> of the    * matching documents.  The high-level search API ({@link    * Searcher#search(Query)}) is usually more efficient, as it skips    * non-high-scoring hits.    *    * @param query to match documents    * @param filter if non-null, a bitset used to eliminate some documents    * @param results to receive hits    */
DECL|method|search
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Frees resources associated with this Searcher. */
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns the number of documents containing<code>term</code>.    * Called by search code to compute term weights.    * @see IndexReader#docFreq(Term).    */
DECL|method|docFreq
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns one greater than the largest possible document number.    * Called by search code to compute term weights.    * @see IndexReader#maxDoc().    */
DECL|method|maxDoc
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Low-level search implementation.  Finds the top<code>n</code>    * hits for<code>query</code>, applying<code>filter</code> if non-null.    *    *<p>Called by {@link Hits}.    *    *<p>Applications should usually call {@link Searcher#search(Query)} or    * {@link Searcher#search(Query,Filter)} instead.    */
DECL|method|search
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns the stored fields of document<code>i</code>.    * Called by {@link HitCollector} implementations.    * @see IndexReader#document(int).    */
DECL|method|doc
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** */
DECL|method|rewrite
name|Query
name|rewrite
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** */
DECL|method|explain
name|Explanation
name|explain
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

