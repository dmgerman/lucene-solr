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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|store
operator|.
name|Directory
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
name|RAMDirectory
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
name|FSDirectory
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
name|Lock
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
name|InputStream
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
name|OutputStream
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
name|Similarity
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
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_comment
comment|/**   An IndexWriter creates and maintains an index.    The third argument to the<a href="#IndexWriter"><b>constructor</b></a>   determines whether a new index is created, or whether an existing index is   opened for the addition of new documents.    In either case, documents are added with the<a   href="#addDocument"><b>addDocument</b></a> method.  When finished adding   documents,<a href="#close"><b>close</b></a> should be called.    If an index will not have more documents added for a while and optimal search   performance is desired, then the<a href="#optimize"><b>optimize</b></a>   method should be called before the index is closed.   */
end_comment

begin_class
DECL|class|IndexWriter
specifier|public
class|class
name|IndexWriter
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
comment|// where this index resides
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
comment|// how to analyze text
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|// how to normalize
DECL|field|segmentInfos
specifier|private
name|SegmentInfos
name|segmentInfos
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
comment|// the segments
DECL|field|ramDirectory
specifier|private
specifier|final
name|Directory
name|ramDirectory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// for temp segs
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
comment|/** Expert: Set the Similarity implementation used by this IndexWriter.    *    * @see Similarity#setDefault(Similarity)    */
DECL|method|setSimilarity
specifier|public
name|void
name|setSimilarity
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
comment|/** Expert: Return the Similarity implementation used by this IndexWriter.    *    *<p>This defaults to the current value of {@link Similarity#getDefault()}.    */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|similarity
return|;
block|}
comment|/** Constructs an IndexWriter for the index in<code>path</code>.  Text will     be analyzed with<code>a</code>.  If<code>create</code> is true, then a     new, empty index will be created in<code>path</code>, replacing the index     already there, if any. */
DECL|method|IndexWriter
specifier|public
name|IndexWriter
parameter_list|(
name|String
name|path
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|,
name|create
argument_list|)
argument_list|,
name|a
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs an IndexWriter for the index in<code>path</code>.  Text will     be analyzed with<code>a</code>.  If<code>create</code> is true, then a     new, empty index will be created in<code>path</code>, replacing the index     already there, if any. */
DECL|method|IndexWriter
specifier|public
name|IndexWriter
parameter_list|(
name|File
name|path
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|,
name|create
argument_list|)
argument_list|,
name|a
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs an IndexWriter for the index in<code>d</code>.  Text will be     analyzed with<code>a</code>.  If<code>create</code> is true, then a new,     empty index will be created in<code>d</code>, replacing the index already     there, if any. */
DECL|method|IndexWriter
specifier|public
name|IndexWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|Analyzer
name|a
parameter_list|,
specifier|final
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|directory
operator|=
name|d
expr_stmt|;
name|analyzer
operator|=
name|a
expr_stmt|;
name|Lock
name|writeLock
init|=
name|directory
operator|.
name|makeLock
argument_list|(
literal|"write.lock"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeLock
operator|.
name|obtain
argument_list|()
condition|)
comment|// obtain write lock
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Index locked for write: "
operator|+
name|writeLock
argument_list|)
throw|;
name|this
operator|.
name|writeLock
operator|=
name|writeLock
expr_stmt|;
comment|// save it
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// in-& inter-process sync
operator|new
name|Lock
operator|.
name|With
argument_list|(
name|directory
operator|.
name|makeLock
argument_list|(
literal|"commit.lock"
argument_list|)
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|create
condition|)
name|segmentInfos
operator|.
name|write
argument_list|(
name|directory
argument_list|)
expr_stmt|;
else|else
name|segmentInfos
operator|.
name|read
argument_list|(
name|directory
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Flushes all changes to an index, closes all associated files, and closes     the directory that the index is stored in. */
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|flushRamSegments
argument_list|()
expr_stmt|;
name|ramDirectory
operator|.
name|close
argument_list|()
expr_stmt|;
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Release the write lock, if needed. */
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writeLock
operator|!=
literal|null
condition|)
block|{
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Returns the analyzer used by this index. */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/** Returns the number of documents currently in this index. */
DECL|method|docCount
specifier|public
specifier|synchronized
name|int
name|docCount
parameter_list|()
block|{
name|int
name|count
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
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|si
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|count
operator|+=
name|si
operator|.
name|docCount
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/** The maximum number of terms that will be indexed for a single field in a     document.  This limits the amount of memory required for indexing, so that     collections with very large files will not crash the indexing process by     running out of memory.<p>By default, no more than 10,000 terms will be indexed for a field. */
DECL|field|maxFieldLength
specifier|public
name|int
name|maxFieldLength
init|=
literal|10000
decl_stmt|;
comment|/** Adds a document to this index.*/
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|DocumentWriter
name|dw
init|=
operator|new
name|DocumentWriter
argument_list|(
name|ramDirectory
argument_list|,
name|analyzer
argument_list|,
name|similarity
argument_list|,
name|maxFieldLength
argument_list|)
decl_stmt|;
name|String
name|segmentName
init|=
name|newSegmentName
argument_list|()
decl_stmt|;
name|dw
operator|.
name|addDocument
argument_list|(
name|segmentName
argument_list|,
name|doc
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|segmentInfos
operator|.
name|addElement
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|segmentName
argument_list|,
literal|1
argument_list|,
name|ramDirectory
argument_list|)
argument_list|)
expr_stmt|;
name|maybeMergeSegments
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newSegmentName
specifier|private
specifier|final
specifier|synchronized
name|String
name|newSegmentName
parameter_list|()
block|{
return|return
literal|"_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|segmentInfos
operator|.
name|counter
operator|++
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
return|;
block|}
comment|/** Determines how often segment indexes are merged by addDocument().  With    * smaller values, less RAM is used while indexing, and searches on    * unoptimized indexes are faster, but indexing speed is slower.  With larger    * values more RAM is used while indexing and searches on unoptimized indexes    * are slower, but indexing is faster.  Thus larger values (> 10) are best    * for batched index creation, and smaller values (< 10) for indexes that are    * interactively maintained.    *    *<p>This must never be less than 2.  The default value is 10.*/
DECL|field|mergeFactor
specifier|public
name|int
name|mergeFactor
init|=
literal|10
decl_stmt|;
comment|/** Determines the largest number of documents ever merged by addDocument().    * Small values (e.g., less than 10,000) are best for interactive indexing,    * as this limits the length of pauses while indexing to a few seconds.    * Larger values are best for batched indexing and speedier searches.    *    *<p>The default value is {@link Integer#MAX_VALUE}. */
DECL|field|maxMergeDocs
specifier|public
name|int
name|maxMergeDocs
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** If non-null, information about merges will be printed to this. */
DECL|field|infoStream
specifier|public
name|PrintStream
name|infoStream
init|=
literal|null
decl_stmt|;
comment|/** Merges all segments together into a single segment, optimizing an index       for search. */
DECL|method|optimize
specifier|public
specifier|synchronized
name|void
name|optimize
parameter_list|()
throws|throws
name|IOException
block|{
name|flushRamSegments
argument_list|()
expr_stmt|;
while|while
condition|(
name|segmentInfos
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|||
operator|(
name|segmentInfos
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|SegmentReader
operator|.
name|hasDeletions
argument_list|(
name|segmentInfos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|||
name|segmentInfos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
operator|.
name|dir
operator|!=
name|directory
operator|)
operator|)
condition|)
block|{
name|int
name|minSegment
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
operator|-
name|mergeFactor
decl_stmt|;
name|mergeSegments
argument_list|(
name|minSegment
operator|<
literal|0
condition|?
literal|0
else|:
name|minSegment
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Merges all segments from an array of indexes into this index.    *    *<p>This may be used to parallelize batch indexing.  A large document    * collection can be broken into sub-collections.  Each sub-collection can be    * indexed in parallel, on a different thread, process or machine.  The    * complete index can then be created by merging sub-collection indexes    * with this method.    *    *<p>After this completes, the index is optimized. */
DECL|method|addIndexes
specifier|public
specifier|synchronized
name|void
name|addIndexes
parameter_list|(
name|Directory
index|[]
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|optimize
argument_list|()
expr_stmt|;
comment|// start with zero or 1 seg
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
comment|// read infos from dir
name|sis
operator|.
name|read
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sis
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|segmentInfos
operator|.
name|addElement
argument_list|(
name|sis
operator|.
name|info
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
comment|// add each info
block|}
block|}
name|optimize
argument_list|()
expr_stmt|;
comment|// final cleanup
block|}
comment|/** Merges all RAM-resident segments. */
DECL|method|flushRamSegments
specifier|private
specifier|final
name|void
name|flushRamSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|minSegment
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|docCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|minSegment
operator|>=
literal|0
operator|&&
operator|(
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
operator|)
operator|.
name|dir
operator|==
name|ramDirectory
condition|)
block|{
name|docCount
operator|+=
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
operator|.
name|docCount
expr_stmt|;
name|minSegment
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|minSegment
operator|<
literal|0
operator|||
comment|// add one FS segment?
operator|(
name|docCount
operator|+
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
operator|.
name|docCount
operator|)
operator|>
name|mergeFactor
operator|||
operator|!
operator|(
name|segmentInfos
operator|.
name|info
argument_list|(
name|segmentInfos
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|dir
operator|==
name|ramDirectory
operator|)
condition|)
name|minSegment
operator|++
expr_stmt|;
if|if
condition|(
name|minSegment
operator|>=
name|segmentInfos
operator|.
name|size
argument_list|()
condition|)
return|return;
comment|// none to merge
name|mergeSegments
argument_list|(
name|minSegment
argument_list|)
expr_stmt|;
block|}
comment|/** Incremental segment merger.  */
DECL|method|maybeMergeSegments
specifier|private
specifier|final
name|void
name|maybeMergeSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|targetMergeDocs
init|=
name|mergeFactor
decl_stmt|;
while|while
condition|(
name|targetMergeDocs
operator|<=
name|maxMergeDocs
condition|)
block|{
comment|// find segments smaller than current target size
name|int
name|minSegment
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|mergeDocs
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|--
name|minSegment
operator|>=
literal|0
condition|)
block|{
name|SegmentInfo
name|si
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
decl_stmt|;
if|if
condition|(
name|si
operator|.
name|docCount
operator|>=
name|targetMergeDocs
condition|)
break|break;
name|mergeDocs
operator|+=
name|si
operator|.
name|docCount
expr_stmt|;
block|}
if|if
condition|(
name|mergeDocs
operator|>=
name|targetMergeDocs
condition|)
comment|// found a merge to do
name|mergeSegments
argument_list|(
name|minSegment
operator|+
literal|1
argument_list|)
expr_stmt|;
else|else
break|break;
name|targetMergeDocs
operator|*=
name|mergeFactor
expr_stmt|;
comment|// increase target size
block|}
block|}
comment|/** Pops segments off of segmentInfos stack down to minSegment, merges them,     and pushes the merged index onto the top of the segmentInfos stack. */
DECL|method|mergeSegments
specifier|private
specifier|final
name|void
name|mergeSegments
parameter_list|(
name|int
name|minSegment
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|mergedName
init|=
name|newSegmentName
argument_list|()
decl_stmt|;
name|int
name|mergedDocCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|infoStream
operator|.
name|print
argument_list|(
literal|"merging segments"
argument_list|)
expr_stmt|;
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|directory
argument_list|,
name|mergedName
argument_list|)
decl_stmt|;
specifier|final
name|Vector
name|segmentsToDelete
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|minSegment
init|;
name|i
operator|<
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|si
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|infoStream
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|si
operator|.
name|name
operator|+
literal|" ("
operator|+
name|si
operator|.
name|docCount
operator|+
literal|" docs)"
argument_list|)
expr_stmt|;
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|si
argument_list|)
decl_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|reader
operator|.
name|directory
operator|==
name|this
operator|.
name|directory
operator|)
operator|||
comment|// if we own the directory
operator|(
name|reader
operator|.
name|directory
operator|==
name|this
operator|.
name|ramDirectory
operator|)
condition|)
name|segmentsToDelete
operator|.
name|addElement
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// queue segment for deletion
name|mergedDocCount
operator|+=
name|si
operator|.
name|docCount
expr_stmt|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|println
argument_list|()
expr_stmt|;
name|infoStream
operator|.
name|println
argument_list|(
literal|" into "
operator|+
name|mergedName
operator|+
literal|" ("
operator|+
name|mergedDocCount
operator|+
literal|" docs)"
argument_list|)
expr_stmt|;
block|}
name|merger
operator|.
name|merge
argument_list|()
expr_stmt|;
name|segmentInfos
operator|.
name|setSize
argument_list|(
name|minSegment
argument_list|)
expr_stmt|;
comment|// pop old infos& add new
name|segmentInfos
operator|.
name|addElement
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|mergedName
argument_list|,
name|mergedDocCount
argument_list|,
name|directory
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// in-& inter-process sync
operator|new
name|Lock
operator|.
name|With
argument_list|(
name|directory
operator|.
name|makeLock
argument_list|(
literal|"commit.lock"
argument_list|)
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
name|segmentInfos
operator|.
name|write
argument_list|(
name|directory
argument_list|)
expr_stmt|;
comment|// commit before deleting
name|deleteSegments
argument_list|(
name|segmentsToDelete
argument_list|)
expr_stmt|;
comment|// delete now-unused segments
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* Some operating systems (e.g. Windows) don't permit a file to be deleted      while it is opened for read (e.g. by another process or thread).  So we      assume that when a delete fails it is because the file is open in another      process, and queue the file for subsequent deletion. */
DECL|method|deleteSegments
specifier|private
specifier|final
name|void
name|deleteSegments
parameter_list|(
name|Vector
name|segments
parameter_list|)
throws|throws
name|IOException
block|{
name|Vector
name|deletable
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|deleteFiles
argument_list|(
name|readDeleteableFiles
argument_list|()
argument_list|,
name|deletable
argument_list|)
expr_stmt|;
comment|// try to delete deleteable
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|segments
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|directory
operator|==
name|this
operator|.
name|directory
condition|)
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|,
name|deletable
argument_list|)
expr_stmt|;
comment|// try to delete our files
else|else
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|,
name|reader
operator|.
name|directory
argument_list|)
expr_stmt|;
comment|// delete, eg, RAM files
block|}
name|writeDeleteableFiles
argument_list|(
name|deletable
argument_list|)
expr_stmt|;
comment|// note files we can't delete
block|}
DECL|method|deleteFiles
specifier|private
specifier|final
name|void
name|deleteFiles
parameter_list|(
name|Vector
name|files
parameter_list|,
name|Directory
name|directory
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
name|files
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|directory
operator|.
name|deleteFile
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteFiles
specifier|private
specifier|final
name|void
name|deleteFiles
parameter_list|(
name|Vector
name|files
parameter_list|,
name|Vector
name|deletable
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
name|files
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|file
init|=
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// try to delete each file
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// if delete fails
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|file
argument_list|)
condition|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|infoStream
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"; Will re-try later."
argument_list|)
expr_stmt|;
name|deletable
operator|.
name|addElement
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// add to deletable
block|}
block|}
block|}
block|}
DECL|method|readDeleteableFiles
specifier|private
specifier|final
name|Vector
name|readDeleteableFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Vector
name|result
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|directory
operator|.
name|fileExists
argument_list|(
literal|"deletable"
argument_list|)
condition|)
return|return
name|result
return|;
name|InputStream
name|input
init|=
name|directory
operator|.
name|openFile
argument_list|(
literal|"deletable"
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
name|input
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
comment|// read file names
name|result
operator|.
name|addElement
argument_list|(
name|input
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|writeDeleteableFiles
specifier|private
specifier|final
name|void
name|writeDeleteableFiles
parameter_list|(
name|Vector
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|output
init|=
name|directory
operator|.
name|createFile
argument_list|(
literal|"deleteable.new"
argument_list|)
decl_stmt|;
try|try
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|files
operator|.
name|size
argument_list|()
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
name|files
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|output
operator|.
name|writeString
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|directory
operator|.
name|renameFile
argument_list|(
literal|"deleteable.new"
argument_list|,
literal|"deletable"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

