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
name|java
operator|.
name|util
operator|.
name|Vector
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

begin_comment
comment|/** A ranked list of documents, used to hold search results. */
end_comment

begin_class
DECL|class|Hits
specifier|public
specifier|final
class|class
name|Hits
block|{
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|filter
specifier|private
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
comment|// the total number of hits
DECL|field|hitDocs
specifier|private
name|Vector
name|hitDocs
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
comment|// cache of hits retrieved
DECL|field|first
specifier|private
name|HitDoc
name|first
decl_stmt|;
comment|// head of LRU cache
DECL|field|last
specifier|private
name|HitDoc
name|last
decl_stmt|;
comment|// tail of LRU cache
DECL|field|numDocs
specifier|private
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
comment|// number cached
DECL|field|maxDocs
specifier|private
name|int
name|maxDocs
init|=
literal|200
decl_stmt|;
comment|// max to cache
DECL|method|Hits
name|Hits
parameter_list|(
name|Searcher
name|s
parameter_list|,
name|Query
name|q
parameter_list|,
name|Filter
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|query
operator|=
name|q
expr_stmt|;
name|searcher
operator|=
name|s
expr_stmt|;
name|filter
operator|=
name|f
expr_stmt|;
name|getMoreDocs
argument_list|(
literal|50
argument_list|)
expr_stmt|;
comment|// retrieve 100 initially
block|}
comment|// Tries to add new documents to hitDocs.
comment|// Ensures that the hit numbered<code>min</code> has been retrieved.
DECL|method|getMoreDocs
specifier|private
specifier|final
name|void
name|getMoreDocs
parameter_list|(
name|int
name|min
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hitDocs
operator|.
name|size
argument_list|()
operator|>
name|min
condition|)
block|{
name|min
operator|=
name|hitDocs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|int
name|n
init|=
name|min
operator|*
literal|2
decl_stmt|;
comment|// double # retrieved
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|n
argument_list|)
decl_stmt|;
name|length
operator|=
name|topDocs
operator|.
name|totalHits
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|float
name|scoreNorm
init|=
literal|1.0f
decl_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
operator|&&
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
operator|>
literal|1.0f
condition|)
block|{
name|scoreNorm
operator|=
literal|1.0f
operator|/
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
expr_stmt|;
block|}
name|int
name|end
init|=
name|scoreDocs
operator|.
name|length
operator|<
name|length
condition|?
name|scoreDocs
operator|.
name|length
else|:
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hitDocs
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|hitDocs
operator|.
name|addElement
argument_list|(
operator|new
name|HitDoc
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
operator|*
name|scoreNorm
argument_list|,
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns the total number of hits available in this set. */
DECL|method|length
specifier|public
specifier|final
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/** Returns the nth document in this set.<p>Documents are cached, so that repeated requests for the same element may      return the same Document object. */
DECL|method|doc
specifier|public
specifier|final
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|HitDoc
name|hitDoc
init|=
name|hitDoc
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// Update LRU cache of documents
name|remove
argument_list|(
name|hitDoc
argument_list|)
expr_stmt|;
comment|// remove from list, if there
name|addToFront
argument_list|(
name|hitDoc
argument_list|)
expr_stmt|;
comment|// add to front of list
if|if
condition|(
name|numDocs
operator|>
name|maxDocs
condition|)
block|{
comment|// if cache is full
name|HitDoc
name|oldLast
init|=
name|last
decl_stmt|;
name|remove
argument_list|(
name|last
argument_list|)
expr_stmt|;
comment|// flush last
name|oldLast
operator|.
name|doc
operator|=
literal|null
expr_stmt|;
comment|// let doc get gc'd
block|}
if|if
condition|(
name|hitDoc
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
name|hitDoc
operator|.
name|doc
operator|=
name|searcher
operator|.
name|doc
argument_list|(
name|hitDoc
operator|.
name|id
argument_list|)
expr_stmt|;
comment|// cache miss: read document
block|}
return|return
name|hitDoc
operator|.
name|doc
return|;
block|}
comment|/** Returns the score for the nth document in this set. */
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hitDoc
argument_list|(
name|n
argument_list|)
operator|.
name|score
return|;
block|}
comment|/** Returns the id for the nth document in this set. */
DECL|method|id
specifier|public
specifier|final
name|int
name|id
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hitDoc
argument_list|(
name|n
argument_list|)
operator|.
name|id
return|;
block|}
DECL|method|hitDoc
specifier|private
specifier|final
name|HitDoc
name|hitDoc
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|n
operator|>=
name|length
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Not a valid hit number: "
operator|+
name|n
argument_list|)
throw|;
block|}
if|if
condition|(
name|n
operator|>=
name|hitDocs
operator|.
name|size
argument_list|()
condition|)
block|{
name|getMoreDocs
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|HitDoc
operator|)
name|hitDocs
operator|.
name|elementAt
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|addToFront
specifier|private
specifier|final
name|void
name|addToFront
parameter_list|(
name|HitDoc
name|hitDoc
parameter_list|)
block|{
comment|// insert at front of cache
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|last
operator|=
name|hitDoc
expr_stmt|;
block|}
else|else
block|{
name|first
operator|.
name|prev
operator|=
name|hitDoc
expr_stmt|;
block|}
name|hitDoc
operator|.
name|next
operator|=
name|first
expr_stmt|;
name|first
operator|=
name|hitDoc
expr_stmt|;
name|hitDoc
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
block|}
DECL|method|remove
specifier|private
specifier|final
name|void
name|remove
parameter_list|(
name|HitDoc
name|hitDoc
parameter_list|)
block|{
comment|// remove from cache
if|if
condition|(
name|hitDoc
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
comment|// it's not in the list
return|return;
comment|// abort
block|}
if|if
condition|(
name|hitDoc
operator|.
name|next
operator|==
literal|null
condition|)
block|{
name|last
operator|=
name|hitDoc
operator|.
name|prev
expr_stmt|;
block|}
else|else
block|{
name|hitDoc
operator|.
name|next
operator|.
name|prev
operator|=
name|hitDoc
operator|.
name|prev
expr_stmt|;
block|}
if|if
condition|(
name|hitDoc
operator|.
name|prev
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|hitDoc
operator|.
name|next
expr_stmt|;
block|}
else|else
block|{
name|hitDoc
operator|.
name|prev
operator|.
name|next
operator|=
name|hitDoc
operator|.
name|next
expr_stmt|;
block|}
name|numDocs
operator|--
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|HitDoc
specifier|final
class|class
name|HitDoc
block|{
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|doc
name|Document
name|doc
init|=
literal|null
decl_stmt|;
DECL|field|next
name|HitDoc
name|next
decl_stmt|;
comment|// in doubly-linked cache
DECL|field|prev
name|HitDoc
name|prev
decl_stmt|;
comment|// in doubly-linked cache
DECL|method|HitDoc
name|HitDoc
parameter_list|(
name|float
name|s
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|score
operator|=
name|s
expr_stmt|;
name|id
operator|=
name|i
expr_stmt|;
block|}
block|}
end_class

end_unit

