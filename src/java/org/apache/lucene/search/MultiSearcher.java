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

begin_comment
comment|/** Implements search over a set of<code>Searchables</code>.  *  *<p>Applications usually need only call the inherited {@link #search(Query)}  * or {@link #search(Query,Filter)} methods.  */
end_comment

begin_class
DECL|class|MultiSearcher
specifier|public
class|class
name|MultiSearcher
extends|extends
name|Searcher
implements|implements
name|Searchable
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
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
init|=
literal|0
decl_stmt|;
comment|/** Creates a searcher which searches<i>searchables</i>. */
DECL|method|MultiSearcher
specifier|public
name|MultiSearcher
parameter_list|(
name|Searchable
index|[]
name|searchables
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|searchables
operator|=
name|searchables
expr_stmt|;
name|starts
operator|=
operator|new
name|int
index|[
name|searchables
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
comment|// build starts array
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
name|starts
index|[
name|i
index|]
operator|=
name|maxDoc
expr_stmt|;
name|maxDoc
operator|+=
name|searchables
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
comment|// compute maxDocs
block|}
name|starts
index|[
name|searchables
operator|.
name|length
index|]
operator|=
name|maxDoc
expr_stmt|;
block|}
comment|/** Frees resources associated with this<code>Searcher</code>. */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
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
name|searchables
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
comment|/** For use by {@link HitCollector} implementations. */
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
name|subSearcher
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find searcher index
return|return
name|searchables
index|[
name|i
index|]
operator|.
name|doc
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to searcher
block|}
comment|/** Call {@link #subSearcher} instead.    * @deprecated    */
DECL|method|searcherIndex
specifier|public
name|int
name|searcherIndex
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|subSearcher
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/** Returns index of the searcher for document<code>n</code> in the array    * used to construct this searcher. */
DECL|method|subSearcher
specifier|public
name|int
name|subSearcher
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// find searcher for doc n:
comment|// replace w/ call to Arrays.binarySearch in Java 1.2
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|searchables
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// for first element less
comment|// than n, return its index
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>
literal|1
decl_stmt|;
name|int
name|midValue
init|=
name|starts
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|midValue
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|n
operator|>
name|midValue
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
block|{
comment|// found a match
while|while
condition|(
name|mid
operator|+
literal|1
operator|<
name|searchables
operator|.
name|length
operator|&&
name|starts
index|[
name|mid
operator|+
literal|1
index|]
operator|==
name|midValue
condition|)
block|{
name|mid
operator|++
expr_stmt|;
comment|// scan to last match
block|}
return|return
name|mid
return|;
block|}
block|}
return|return
name|hi
return|;
block|}
comment|/** Returns the document number of document<code>n</code> within its    * sub-index. */
DECL|method|subDoc
specifier|public
name|int
name|subDoc
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|n
operator|-
name|starts
index|[
name|subSearcher
argument_list|(
name|n
argument_list|)
index|]
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|maxDoc
return|;
block|}
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
name|float
name|minScore
init|=
literal|0.0f
decl_stmt|;
name|int
name|totalHits
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
block|{
comment|// search each searcher
name|TopDocs
name|docs
init|=
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
name|nDocs
argument_list|)
decl_stmt|;
name|totalHits
operator|+=
name|docs
operator|.
name|totalHits
expr_stmt|;
comment|// update totalHits
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
if|if
condition|(
name|scoreDoc
operator|.
name|score
operator|>=
name|minScore
condition|)
block|{
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
name|hq
operator|.
name|put
argument_list|(
name|scoreDoc
argument_list|)
expr_stmt|;
comment|// update hit queue
if|if
condition|(
name|hq
operator|.
name|size
argument_list|()
operator|>
name|nDocs
condition|)
block|{
comment|// if hit queue overfull
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
comment|// remove lowest in hit queue
name|minScore
operator|=
operator|(
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|score
expr_stmt|;
comment|// reset minScore
block|}
block|}
else|else
break|break;
comment|// no more scores> minScore
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
comment|/** Lower-level search API.    *    *<p>{@link HitCollector#collect(int,float)} is called for every non-zero    * scoring document.    *    *<p>Applications should only use this if they need<i>all</i> of the    * matching documents.  The high-level search API ({@link    * Searcher#search(Query)}) is usually more efficient, as it skips    * non-high-scoring hits.    *    * @param query to match documents    * @param filter if non-null, a bitset used to eliminate some documents    * @param results to receive hits    */
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
DECL|method|explain
specifier|public
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
block|{
name|int
name|i
init|=
name|subSearcher
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// find searcher index
return|return
name|searchables
index|[
name|i
index|]
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|doc
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to searcher
block|}
block|}
end_class

end_unit

