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
name|index
operator|.
name|TermDocs
import|;
end_import

begin_class
DECL|class|TermScorer
specifier|final
class|class
name|TermScorer
extends|extends
name|Scorer
block|{
DECL|field|weight
specifier|private
name|Weight
name|weight
decl_stmt|;
DECL|field|termDocs
specifier|private
name|TermDocs
name|termDocs
decl_stmt|;
DECL|field|norms
specifier|private
name|byte
index|[]
name|norms
decl_stmt|;
DECL|field|weightValue
specifier|private
name|float
name|weightValue
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
decl_stmt|;
DECL|field|docs
specifier|private
specifier|final
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
literal|32
index|]
decl_stmt|;
comment|// buffered doc numbers
DECL|field|freqs
specifier|private
specifier|final
name|int
index|[]
name|freqs
init|=
operator|new
name|int
index|[
literal|32
index|]
decl_stmt|;
comment|// buffered term freqs
DECL|field|pointer
specifier|private
name|int
name|pointer
decl_stmt|;
DECL|field|pointerMax
specifier|private
name|int
name|pointerMax
decl_stmt|;
DECL|field|SCORE_CACHE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SCORE_CACHE_SIZE
init|=
literal|32
decl_stmt|;
DECL|field|scoreCache
specifier|private
name|float
index|[]
name|scoreCache
init|=
operator|new
name|float
index|[
name|SCORE_CACHE_SIZE
index|]
decl_stmt|;
DECL|method|TermScorer
name|TermScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermDocs
name|td
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|termDocs
operator|=
name|td
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
name|this
operator|.
name|weightValue
operator|=
name|weight
operator|.
name|getValue
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|SCORE_CACHE_SIZE
condition|;
name|i
operator|++
control|)
name|scoreCache
index|[
name|i
index|]
operator|=
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|*
name|weightValue
expr_stmt|;
name|pointerMax
operator|=
name|termDocs
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
expr_stmt|;
comment|// fill buffers
if|if
condition|(
name|pointerMax
operator|!=
literal|0
condition|)
name|doc
operator|=
name|docs
index|[
literal|0
index|]
expr_stmt|;
else|else
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close stream
name|doc
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// set to sentinel value
block|}
block|}
DECL|method|score
specifier|public
specifier|final
name|void
name|score
parameter_list|(
name|HitCollector
name|c
parameter_list|,
specifier|final
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|d
init|=
name|doc
decl_stmt|;
comment|// cache doc in local
name|Similarity
name|similarity
init|=
name|getSimilarity
argument_list|()
decl_stmt|;
comment|// cache sim in local
while|while
condition|(
name|d
operator|<
name|end
condition|)
block|{
comment|// for docs in window
specifier|final
name|int
name|f
init|=
name|freqs
index|[
name|pointer
index|]
decl_stmt|;
name|float
name|score
init|=
comment|// compute tf(f)*weight
name|f
operator|<
name|SCORE_CACHE_SIZE
comment|// check cache
condition|?
name|scoreCache
index|[
name|f
index|]
comment|// cache hit
else|:
name|similarity
operator|.
name|tf
argument_list|(
name|f
argument_list|)
operator|*
name|weightValue
decl_stmt|;
comment|// cache miss
name|score
operator|*=
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|norms
index|[
name|d
index|]
argument_list|)
expr_stmt|;
comment|// normalize for field
name|c
operator|.
name|collect
argument_list|(
name|d
argument_list|,
name|score
argument_list|)
expr_stmt|;
comment|// collect score
if|if
condition|(
operator|++
name|pointer
operator|==
name|pointerMax
condition|)
block|{
name|pointerMax
operator|=
name|termDocs
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
expr_stmt|;
comment|// refill buffers
if|if
condition|(
name|pointerMax
operator|!=
literal|0
condition|)
block|{
name|pointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close stream
name|doc
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// set to sentinel value
return|return;
block|}
block|}
name|d
operator|=
name|docs
index|[
name|pointer
index|]
expr_stmt|;
block|}
name|doc
operator|=
name|d
expr_stmt|;
comment|// flush cache
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|TermQuery
name|query
init|=
operator|(
name|TermQuery
operator|)
name|weight
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|int
name|tf
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pointer
operator|<
name|pointerMax
condition|)
block|{
if|if
condition|(
name|docs
index|[
name|pointer
index|]
operator|==
name|doc
condition|)
name|tf
operator|=
name|freqs
index|[
name|pointer
index|]
expr_stmt|;
name|pointer
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|tf
operator|==
literal|0
condition|)
block|{
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|termDocs
operator|.
name|doc
argument_list|()
operator|==
name|doc
condition|)
block|{
name|tf
operator|=
name|termDocs
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|tf
argument_list|)
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"tf(termFreq("
operator|+
name|query
operator|.
name|getTerm
argument_list|()
operator|+
literal|")="
operator|+
name|tf
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|tfExplanation
return|;
block|}
block|}
end_class

end_unit

