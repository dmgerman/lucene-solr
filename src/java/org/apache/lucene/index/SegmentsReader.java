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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001, 2002, 2003 The Apache Software Foundation.  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/**  * FIXME: Describe class<code>SegmentsReader</code> here.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|SegmentsReader
specifier|final
class|class
name|SegmentsReader
extends|extends
name|IndexReader
block|{
DECL|field|readers
specifier|protected
name|SegmentReader
index|[]
name|readers
decl_stmt|;
DECL|field|starts
specifier|protected
name|int
index|[]
name|starts
decl_stmt|;
comment|// 1st docno for each segment
DECL|field|normsCache
specifier|private
name|Hashtable
name|normsCache
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
init|=
literal|0
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|hasDeletions
specifier|private
name|boolean
name|hasDeletions
init|=
literal|false
decl_stmt|;
DECL|method|SegmentsReader
name|SegmentsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentReader
index|[]
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|readers
operator|=
name|r
expr_stmt|;
name|starts
operator|=
operator|new
name|int
index|[
name|readers
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
name|readers
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
name|readers
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
comment|// compute maxDocs
if|if
condition|(
name|readers
index|[
name|i
index|]
operator|.
name|hasDeletions
argument_list|()
condition|)
name|hasDeletions
operator|=
literal|true
expr_stmt|;
block|}
name|starts
index|[
name|readers
operator|.
name|length
index|]
operator|=
name|maxDoc
expr_stmt|;
block|}
DECL|method|numDocs
specifier|public
specifier|final
specifier|synchronized
name|int
name|numDocs
parameter_list|()
block|{
if|if
condition|(
name|numDocs
operator|==
operator|-
literal|1
condition|)
block|{
comment|// check cache
name|int
name|n
init|=
literal|0
decl_stmt|;
comment|// cache miss--recompute
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|n
operator|+=
name|readers
index|[
name|i
index|]
operator|.
name|numDocs
argument_list|()
expr_stmt|;
comment|// sum from readers
name|numDocs
operator|=
name|n
expr_stmt|;
block|}
return|return
name|numDocs
return|;
block|}
DECL|method|maxDoc
specifier|public
specifier|final
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
DECL|method|document
specifier|public
specifier|final
name|Document
name|document
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
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
return|return
name|readers
index|[
name|i
index|]
operator|.
name|document
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to segment reader
block|}
DECL|method|isDeleted
specifier|public
specifier|final
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
return|return
name|readers
index|[
name|i
index|]
operator|.
name|isDeleted
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to segment reader
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|hasDeletions
return|;
block|}
DECL|method|doDelete
specifier|protected
specifier|final
specifier|synchronized
name|void
name|doDelete
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|numDocs
operator|=
operator|-
literal|1
expr_stmt|;
comment|// invalidate cache
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
name|readers
index|[
name|i
index|]
operator|.
name|doDelete
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// dispatch to segment reader
name|hasDeletions
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|readerIndex
specifier|private
specifier|final
name|int
name|readerIndex
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// find reader for doc n:
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|readers
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// for first element less
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
name|readers
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
DECL|method|norms
specifier|public
specifier|final
specifier|synchronized
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
operator|(
name|byte
index|[]
operator|)
name|normsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
return|return
name|bytes
return|;
comment|// cache hit
name|bytes
operator|=
operator|new
name|byte
index|[
name|maxDoc
argument_list|()
index|]
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|readers
index|[
name|i
index|]
operator|.
name|norms
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|,
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|normsCache
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
comment|// update cache
return|return
name|bytes
return|;
block|}
DECL|method|terms
specifier|public
specifier|final
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentsTermEnum
argument_list|(
name|readers
argument_list|,
name|starts
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|terms
specifier|public
specifier|final
name|TermEnum
name|terms
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentsTermEnum
argument_list|(
name|readers
argument_list|,
name|starts
argument_list|,
name|term
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
comment|// sum freqs in segments
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|total
operator|+=
name|readers
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
expr_stmt|;
return|return
name|total
return|;
block|}
DECL|method|termDocs
specifier|public
specifier|final
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentsTermDocs
argument_list|(
name|readers
argument_list|,
name|starts
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
specifier|final
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentsTermPositions
argument_list|(
name|readers
argument_list|,
name|starts
argument_list|)
return|;
block|}
DECL|method|doClose
specifier|protected
specifier|final
specifier|synchronized
name|void
name|doClose
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|readers
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * @see IndexReader#getFieldNames()    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|()
throws|throws
name|IOException
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|()
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
name|readers
index|[
name|i
index|]
decl_stmt|;
name|Collection
name|names
init|=
name|reader
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
comment|// iterate through the field names and add them to the set
for|for
control|(
name|Iterator
name|iterator
init|=
name|names
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|s
init|=
operator|(
name|String
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|fieldSet
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fieldSet
return|;
block|}
comment|/**    * @see IndexReader#getFieldNames(boolean)    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|boolean
name|indexed
parameter_list|)
throws|throws
name|IOException
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|()
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
name|readers
index|[
name|i
index|]
decl_stmt|;
name|Collection
name|names
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|indexed
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
block|}
end_class

begin_class
DECL|class|SegmentsTermEnum
class|class
name|SegmentsTermEnum
extends|extends
name|TermEnum
block|{
DECL|field|queue
specifier|private
name|SegmentMergeQueue
name|queue
decl_stmt|;
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
DECL|field|docFreq
specifier|private
name|int
name|docFreq
decl_stmt|;
DECL|method|SegmentsTermEnum
name|SegmentsTermEnum
parameter_list|(
name|SegmentReader
index|[]
name|readers
parameter_list|,
name|int
index|[]
name|starts
parameter_list|,
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|queue
operator|=
operator|new
name|SegmentMergeQueue
argument_list|(
name|readers
operator|.
name|length
argument_list|)
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
name|readers
index|[
name|i
index|]
decl_stmt|;
name|SegmentTermEnum
name|termEnum
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|termEnum
operator|=
operator|(
name|SegmentTermEnum
operator|)
name|reader
operator|.
name|terms
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
else|else
name|termEnum
operator|=
operator|(
name|SegmentTermEnum
operator|)
name|reader
operator|.
name|terms
argument_list|()
expr_stmt|;
name|SegmentMergeInfo
name|smi
init|=
operator|new
name|SegmentMergeInfo
argument_list|(
name|starts
index|[
name|i
index|]
argument_list|,
name|termEnum
argument_list|,
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|?
name|smi
operator|.
name|next
argument_list|()
else|:
name|termEnum
operator|.
name|term
argument_list|()
operator|!=
literal|null
condition|)
name|queue
operator|.
name|put
argument_list|(
name|smi
argument_list|)
expr_stmt|;
comment|// initialize queue
else|else
name|smi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|next
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|next
specifier|public
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|SegmentMergeInfo
name|top
init|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|top
operator|==
literal|null
condition|)
block|{
name|term
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
name|term
operator|=
name|top
operator|.
name|term
expr_stmt|;
name|docFreq
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|top
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|top
operator|.
name|term
argument_list|)
operator|==
literal|0
condition|)
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|docFreq
operator|+=
name|top
operator|.
name|termEnum
operator|.
name|docFreq
argument_list|()
expr_stmt|;
comment|// increment freq
if|if
condition|(
name|top
operator|.
name|next
argument_list|()
condition|)
name|queue
operator|.
name|put
argument_list|(
name|top
argument_list|)
expr_stmt|;
comment|// restore queue
else|else
name|top
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// done with a segment
name|top
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|term
specifier|public
specifier|final
name|Term
name|term
parameter_list|()
block|{
return|return
name|term
return|;
block|}
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|docFreq
return|;
block|}
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|queue
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|SegmentsTermDocs
class|class
name|SegmentsTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|readers
specifier|protected
name|SegmentReader
index|[]
name|readers
decl_stmt|;
DECL|field|starts
specifier|protected
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
DECL|field|base
specifier|protected
name|int
name|base
init|=
literal|0
decl_stmt|;
DECL|field|pointer
specifier|protected
name|int
name|pointer
init|=
literal|0
decl_stmt|;
DECL|field|segTermDocs
specifier|private
name|SegmentTermDocs
index|[]
name|segTermDocs
decl_stmt|;
DECL|field|current
specifier|protected
name|SegmentTermDocs
name|current
decl_stmt|;
comment|// == segTermDocs[pointer]
DECL|method|SegmentsTermDocs
name|SegmentsTermDocs
parameter_list|(
name|SegmentReader
index|[]
name|r
parameter_list|,
name|int
index|[]
name|s
parameter_list|)
block|{
name|readers
operator|=
name|r
expr_stmt|;
name|starts
operator|=
name|s
expr_stmt|;
name|segTermDocs
operator|=
operator|new
name|SegmentTermDocs
index|[
name|r
operator|.
name|length
index|]
expr_stmt|;
block|}
DECL|method|doc
specifier|public
specifier|final
name|int
name|doc
parameter_list|()
block|{
return|return
name|base
operator|+
name|current
operator|.
name|doc
return|;
block|}
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
block|{
return|return
name|current
operator|.
name|freq
return|;
block|}
DECL|method|seek
specifier|public
specifier|final
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|base
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|pointer
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|current
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|termEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|termEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|current
operator|!=
literal|null
operator|&&
name|current
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|pointer
operator|<
name|readers
operator|.
name|length
condition|)
block|{
name|base
operator|=
name|starts
index|[
name|pointer
index|]
expr_stmt|;
name|current
operator|=
name|termDocs
argument_list|(
name|pointer
operator|++
argument_list|)
expr_stmt|;
return|return
name|next
argument_list|()
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/** Optimized implementation. */
DECL|method|read
specifier|public
specifier|final
name|int
name|read
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
while|while
condition|(
name|current
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|pointer
operator|<
name|readers
operator|.
name|length
condition|)
block|{
comment|// try next segment
name|base
operator|=
name|starts
index|[
name|pointer
index|]
expr_stmt|;
name|current
operator|=
name|termDocs
argument_list|(
name|pointer
operator|++
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
name|int
name|end
init|=
name|current
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|==
literal|0
condition|)
block|{
comment|// none left in segment
name|current
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// got some
specifier|final
name|int
name|b
init|=
name|base
decl_stmt|;
comment|// adjust doc numbers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|docs
index|[
name|i
index|]
operator|+=
name|b
expr_stmt|;
return|return
name|end
return|;
block|}
block|}
block|}
comment|/** As yet unoptimized implementation. */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
do|do
block|{
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
do|while
condition|(
name|target
operator|>
name|doc
argument_list|()
condition|)
do|;
return|return
literal|true
return|;
block|}
DECL|method|termDocs
specifier|private
name|SegmentTermDocs
name|termDocs
parameter_list|(
name|int
name|i
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
return|return
literal|null
return|;
name|SegmentTermDocs
name|result
init|=
name|segTermDocs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
name|result
operator|=
name|segTermDocs
index|[
name|i
index|]
operator|=
name|termDocs
argument_list|(
name|readers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|termDocs
specifier|protected
name|SegmentTermDocs
name|termDocs
parameter_list|(
name|SegmentReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|SegmentTermDocs
operator|)
name|reader
operator|.
name|termDocs
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
specifier|final
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
name|segTermDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|segTermDocs
index|[
name|i
index|]
operator|!=
literal|null
condition|)
name|segTermDocs
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|SegmentsTermPositions
class|class
name|SegmentsTermPositions
extends|extends
name|SegmentsTermDocs
implements|implements
name|TermPositions
block|{
DECL|method|SegmentsTermPositions
name|SegmentsTermPositions
parameter_list|(
name|SegmentReader
index|[]
name|r
parameter_list|,
name|int
index|[]
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|termDocs
specifier|protected
specifier|final
name|SegmentTermDocs
name|termDocs
parameter_list|(
name|SegmentReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|SegmentTermDocs
operator|)
name|reader
operator|.
name|termPositions
argument_list|()
return|;
block|}
DECL|method|nextPosition
specifier|public
specifier|final
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|SegmentTermPositions
operator|)
name|current
operator|)
operator|.
name|nextPosition
argument_list|()
return|;
block|}
block|}
end_class

end_unit

