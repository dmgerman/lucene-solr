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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|index
operator|.
name|Term
import|;
end_import

begin_comment
comment|/** Implements parallel search over a set of<code>Searchables</code>.  *  *<p>Applications usually need only call the inherited {@link #search(Query)}  * or {@link #search(Query,Filter)} methods.  */
end_comment

begin_class
DECL|class|ParallelMultiSearcher
specifier|public
class|class
name|ParallelMultiSearcher
extends|extends
name|MultiSearcher
block|{
DECL|field|searchables
specifier|private
name|Searchable
index|[]
name|searchables
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
comment|/** Creates a searcher which searches<i>searchables</i>. */
DECL|method|ParallelMultiSearcher
specifier|public
name|ParallelMultiSearcher
parameter_list|(
name|Searchable
index|[]
name|searchables
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|searchables
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchables
operator|=
name|searchables
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|getStarts
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * TODO: parallelize this one too 	 */
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|docFreq
operator|+=
name|searchables
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|docFreq
return|;
block|}
comment|/** 	* A search implementation which spans a new thread for each 	* Searchable, waits for each search to complete and merge 	* the results back together. 	*/
DECL|method|search
specifier|public
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
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|HitQueue
name|hq
init|=
operator|new
name|HitQueue
argument_list|(
name|nDocs
argument_list|)
decl_stmt|;
name|int
name|totalHits
init|=
literal|0
decl_stmt|;
name|MultiSearcherThread
index|[]
name|msta
init|=
operator|new
name|MultiSearcherThread
index|[
name|searchables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searcher
comment|// Assume not too many searchables and cost of creating a thread is by far inferior to a search
name|msta
index|[
name|i
index|]
operator|=
operator|new
name|MultiSearcherThread
argument_list|(
name|searchables
index|[
name|i
index|]
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|hq
argument_list|,
name|i
argument_list|,
name|starts
argument_list|,
literal|"MultiSearcher thread #"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|msta
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|msta
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
empty_stmt|;
comment|// TODO: what should we do with this???
block|}
name|IOException
name|ioe
init|=
name|msta
index|[
name|i
index|]
operator|.
name|getIOException
argument_list|()
decl_stmt|;
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
block|{
name|totalHits
operator|+=
name|msta
index|[
name|i
index|]
operator|.
name|hits
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// if one search produced an IOException, rethrow it
throw|throw
name|ioe
throw|;
block|}
block|}
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|)
return|;
block|}
comment|/** Lower-level search API. 	 * 	 *<p>{@link HitCollector#collect(int,float)} is called for every non-zero 	 * scoring document. 	 * 	 *<p>Applications should only use this if they need<i>all</i> of the 	 * matching documents.  The high-level search API ({@link 	 * Searcher#search(Query)}) is usually more efficient, as it skips 	 * non-high-scoring hits. 	 * 	 * @param query to match documents 	 * @param filter if non-null, a bitset used to eliminate some documents 	 * @param results to receive hits 	 *  	 * TODO: parallelize this one too 	 */
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|start
init|=
name|starts
index|[
name|i
index|]
decl_stmt|;
name|searchables
index|[
name|i
index|]
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|results
operator|.
name|collect
argument_list|(
name|doc
operator|+
name|start
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* 	 * TODO: this one could be parallelized too 	 * @see org.apache.lucene.search.Searchable#rewrite(org.apache.lucene.search.Query) 	 */
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
index|[]
name|queries
init|=
operator|new
name|Query
index|[
name|searchables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|queries
index|[
name|i
index|]
operator|=
name|searchables
index|[
name|i
index|]
operator|.
name|rewrite
argument_list|(
name|original
argument_list|)
expr_stmt|;
block|}
return|return
name|original
operator|.
name|combine
argument_list|(
name|queries
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * A thread subclass for searching a single searchable   */
end_comment

begin_class
DECL|class|MultiSearcherThread
class|class
name|MultiSearcherThread
extends|extends
name|Thread
block|{
DECL|field|searchable
specifier|private
name|Searchable
name|searchable
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|filter
specifier|private
name|Filter
name|filter
decl_stmt|;
DECL|field|nDocs
specifier|private
name|int
name|nDocs
decl_stmt|;
DECL|field|hits
specifier|private
name|int
name|hits
decl_stmt|;
DECL|field|docs
specifier|private
name|TopDocs
name|docs
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
decl_stmt|;
DECL|field|hq
specifier|private
name|HitQueue
name|hq
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|ioe
specifier|private
name|IOException
name|ioe
decl_stmt|;
DECL|method|MultiSearcherThread
specifier|public
name|MultiSearcherThread
parameter_list|(
name|Searchable
name|searchable
parameter_list|,
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|HitQueue
name|hq
parameter_list|,
name|int
name|i
parameter_list|,
name|int
index|[]
name|starts
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchable
operator|=
name|searchable
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|nDocs
operator|=
name|nDocs
expr_stmt|;
name|this
operator|.
name|hq
operator|=
name|hq
expr_stmt|;
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|starts
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|docs
operator|=
name|searchable
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|)
expr_stmt|;
block|}
comment|// Store the IOException for later use by the caller of this thread
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|this
operator|.
name|ioe
operator|=
name|ioe
expr_stmt|;
block|}
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
block|{
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|docs
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// merge scoreDocs into hq
name|ScoreDoc
name|scoreDoc
init|=
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|scoreDoc
operator|.
name|doc
operator|+=
name|starts
index|[
name|i
index|]
expr_stmt|;
comment|// convert doc
comment|//it would be so nice if we had a thread-safe insert
synchronized|synchronized
init|(
name|hq
init|)
block|{
if|if
condition|(
operator|!
name|hq
operator|.
name|insert
argument_list|(
name|scoreDoc
argument_list|)
condition|)
break|break;
block|}
comment|// no more scores> minScore
block|}
block|}
block|}
DECL|method|hits
specifier|public
name|int
name|hits
parameter_list|()
block|{
return|return
name|docs
operator|.
name|totalHits
return|;
block|}
DECL|method|getIOException
specifier|public
name|IOException
name|getIOException
parameter_list|()
block|{
return|return
name|ioe
return|;
block|}
block|}
end_class

end_unit

