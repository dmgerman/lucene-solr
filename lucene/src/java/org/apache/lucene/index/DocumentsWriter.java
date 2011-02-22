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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PrintStream
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|DocumentsWriterPerThread
operator|.
name|IndexingChain
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
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
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
name|Query
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
name|SimilarityProvider
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
name|AlreadyClosedException
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
name|util
operator|.
name|BitVector
import|;
end_import

begin_comment
comment|/**  * This class accepts multiple added documents and directly  * writes a single segment file.  It does this more  * efficiently than creating a single segment per document  * (with DocumentWriter) and doing standard merges on those  * segments.  *  * Each added document is passed to the {@link DocConsumer},  * which in turn processes the document and interacts with  * other consumers in the indexing chain.  Certain  * consumers, like {@link StoredFieldsWriter} and {@link  * TermVectorsTermsWriter}, digest a document and  * immediately write bytes to the "doc store" files (ie,  * they do not consume RAM per document, except while they  * are processing the document).  *  * Other consumers, eg {@link FreqProxTermsWriter} and  * {@link NormsWriter}, buffer bytes in RAM and flush only  * when a new segment is produced.   * Once we have used our allowed RAM buffer, or the number  * of added docs is large enough (in the case we are  * flushing by doc count instead of RAM usage), we create a  * real segment and flush it to the Directory.  *  * Threads:  *  * Multiple threads are allowed into addDocument at once.  * There is an initial synchronized call to getThreadState  * which allocates a ThreadState for this thread.  The same  * thread will get the same ThreadState over time (thread  * affinity) so that if there are consistent patterns (for  * example each thread is indexing a different content  * source) then we make better use of RAM.  Then  * processDocument is called on that ThreadState without  * synchronization (most of the "heavy lifting" is in this  * call).  Finally the synchronized "finishDocument" is  * called to flush changes to the directory.  *  * When flush is called by IndexWriter we forcefully idle  * all threads and flush only once they are all idle.  This  * means you can call flush with a given thread even while  * other threads are actively adding/deleting documents.  *  *  * Exceptions:  *  * Because this class directly updates in-memory posting  * lists, and flushes stored fields and term vectors  * directly to files in the directory, there are certain  * limited times when an exception can corrupt this state.  * For example, a disk full while flushing stored fields  * leaves this file in a corrupt state.  Or, an OOM  * exception while appending to the in-memory posting lists  * can corrupt that posting list.  We call such exceptions  * "aborting exceptions".  In these cases we must call  * abort() to discard all docs added since the last flush.  *  * All other exceptions ("non-aborting exceptions") can  * still partially update the index structures.  These  * updates are consistent, but, they represent only a part  * of the document seen up until the exception was hit.  * When this happens, we immediately mark the document as  * deleted so that the document is always atomically ("all  * or none") added to the index.  */
end_comment

begin_class
DECL|class|DocumentsWriter
specifier|final
class|class
name|DocumentsWriter
block|{
DECL|field|bytesUsed
specifier|final
name|AtomicLong
name|bytesUsed
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|directory
name|Directory
name|directory
decl_stmt|;
DECL|field|bufferIsFull
name|boolean
name|bufferIsFull
decl_stmt|;
comment|// True when it's time to write segment
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
decl_stmt|;
DECL|field|infoStream
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|similarityProvider
name|SimilarityProvider
name|similarityProvider
decl_stmt|;
DECL|field|newFiles
name|List
argument_list|<
name|String
argument_list|>
name|newFiles
decl_stmt|;
DECL|field|indexWriter
specifier|final
name|IndexWriter
name|indexWriter
decl_stmt|;
DECL|field|numDocsInRAM
specifier|private
name|AtomicInteger
name|numDocsInRAM
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|ramUsed
specifier|private
name|AtomicLong
name|ramUsed
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// How much RAM we can use before flushing.  This is 0 if
comment|// we are flushing by doc count instead.
DECL|field|ramBufferSize
specifier|private
name|long
name|ramBufferSize
init|=
call|(
name|long
call|)
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
comment|// Flush @ this number of docs.  If ramBufferSize is
comment|// non-zero we will flush by RAM usage instead.
DECL|field|maxBufferedDocs
specifier|private
name|int
name|maxBufferedDocs
init|=
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DOCS
decl_stmt|;
DECL|field|bufferedDeletesStream
specifier|final
name|BufferedDeletesStream
name|bufferedDeletesStream
decl_stmt|;
comment|// TODO: cutover to BytesRefHash
DECL|field|pendingDeletes
specifier|private
name|BufferedDeletes
name|pendingDeletes
init|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|chain
specifier|final
name|IndexingChain
name|chain
decl_stmt|;
DECL|field|perThreadPool
specifier|final
name|DocumentsWriterPerThreadPool
name|perThreadPool
decl_stmt|;
DECL|method|DocumentsWriter
name|DocumentsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|IndexingChain
name|chain
parameter_list|,
name|DocumentsWriterPerThreadPool
name|indexerThreadPool
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|BufferedDeletesStream
name|bufferedDeletesStream
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|indexWriter
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|similarityProvider
operator|=
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getSimilarityProvider
argument_list|()
expr_stmt|;
name|this
operator|.
name|bufferedDeletesStream
operator|=
name|bufferedDeletesStream
expr_stmt|;
name|this
operator|.
name|perThreadPool
operator|=
name|indexerThreadPool
expr_stmt|;
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
name|this
operator|.
name|perThreadPool
operator|.
name|initialize
argument_list|(
name|this
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteQueries
name|boolean
name|deleteQueries
parameter_list|(
specifier|final
name|Query
modifier|...
name|queries
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|state
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|state
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|state
operator|.
name|perThread
operator|.
name|deleteQueries
argument_list|(
name|queries
argument_list|)
expr_stmt|;
name|deleted
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|Query
name|query
range|:
name|queries
control|)
block|{
name|pendingDeletes
operator|.
name|addQuery
argument_list|(
name|query
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|deleteQuery
name|boolean
name|deleteQuery
parameter_list|(
specifier|final
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|deleteQueries
argument_list|(
name|query
argument_list|)
return|;
block|}
DECL|method|deleteTerms
name|boolean
name|deleteTerms
parameter_list|(
specifier|final
name|Term
modifier|...
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|state
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|deleted
operator|=
literal|true
expr_stmt|;
name|state
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|state
operator|.
name|perThread
operator|.
name|deleteTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|pendingDeletes
operator|.
name|addTerm
argument_list|(
name|term
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// TODO: we could check w/ FreqProxTermsWriter: if the
comment|// term doesn't exist, don't bother buffering into the
comment|// per-DWPT map (but still must go into the global map)
DECL|method|deleteTerm
name|boolean
name|deleteTerm
parameter_list|(
specifier|final
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|deleteTerms
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|deleteTerm
name|boolean
name|deleteTerm
parameter_list|(
specifier|final
name|Term
name|term
parameter_list|,
name|ThreadState
name|exclude
parameter_list|)
block|{
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|deleted
operator|=
literal|true
expr_stmt|;
name|ThreadState
name|state
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|!=
name|exclude
condition|)
block|{
name|state
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|state
operator|.
name|perThread
operator|.
name|deleteTerms
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|deleted
return|;
block|}
comment|/** If non-null, various details of indexing are printed    *  here. */
DECL|method|setInfoStream
specifier|synchronized
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|pushConfigChange
argument_list|()
expr_stmt|;
block|}
DECL|method|setSimilarityProvider
specifier|synchronized
name|void
name|setSimilarityProvider
parameter_list|(
name|SimilarityProvider
name|similarityProvider
parameter_list|)
block|{
name|this
operator|.
name|similarityProvider
operator|=
name|similarityProvider
expr_stmt|;
name|pushConfigChange
argument_list|()
expr_stmt|;
block|}
DECL|method|pushConfigChange
specifier|private
specifier|final
name|void
name|pushConfigChange
parameter_list|()
block|{
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|it
init|=
name|perThreadPool
operator|.
name|getAllPerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocumentsWriterPerThread
name|perThread
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|perThread
decl_stmt|;
name|perThread
operator|.
name|docState
operator|.
name|infoStream
operator|=
name|this
operator|.
name|infoStream
expr_stmt|;
name|perThread
operator|.
name|docState
operator|.
name|similarityProvider
operator|=
name|this
operator|.
name|similarityProvider
expr_stmt|;
block|}
block|}
comment|/** Set how much RAM we can use before flushing. */
DECL|method|setRAMBufferSizeMB
specifier|synchronized
name|void
name|setRAMBufferSizeMB
parameter_list|(
name|double
name|mb
parameter_list|)
block|{
if|if
condition|(
name|mb
operator|==
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
condition|)
block|{
name|ramBufferSize
operator|=
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
expr_stmt|;
block|}
else|else
block|{
name|ramBufferSize
operator|=
call|(
name|long
call|)
argument_list|(
name|mb
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRAMBufferSizeMB
specifier|synchronized
name|double
name|getRAMBufferSizeMB
parameter_list|()
block|{
if|if
condition|(
name|ramBufferSize
operator|==
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
condition|)
block|{
return|return
name|ramBufferSize
return|;
block|}
else|else
block|{
return|return
name|ramBufferSize
operator|/
literal|1024.
operator|/
literal|1024.
return|;
block|}
block|}
comment|/** Set max buffered docs, which means we will flush by    *  doc count instead of by RAM usage. */
DECL|method|setMaxBufferedDocs
name|void
name|setMaxBufferedDocs
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|maxBufferedDocs
operator|=
name|count
expr_stmt|;
block|}
DECL|method|getMaxBufferedDocs
name|int
name|getMaxBufferedDocs
parameter_list|()
block|{
return|return
name|maxBufferedDocs
return|;
block|}
comment|/** Returns how many docs are currently buffered in RAM. */
DECL|method|getNumDocs
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocsInRAM
operator|.
name|get
argument_list|()
return|;
block|}
DECL|field|abortedFiles
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|abortedFiles
decl_stmt|;
comment|// List of files that were written before last abort()
DECL|method|abortedFiles
name|Collection
argument_list|<
name|String
argument_list|>
name|abortedFiles
parameter_list|()
block|{
return|return
name|abortedFiles
return|;
block|}
DECL|method|message
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|indexWriter
operator|.
name|message
argument_list|(
literal|"DW: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexWriter is closed"
argument_list|)
throw|;
block|}
block|}
comment|/** Called if we hit an exception at a bad time (when    *  updating the index files) and must discard all    *  currently buffered docs.  This resets our state,    *  discarding any docs added since last flush. */
DECL|method|abort
specifier|synchronized
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"docWriter: abort"
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|perThread
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|perThread
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|perThread
operator|.
name|perThread
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|perThread
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"docWriter: done abort; abortedFiles="
operator|+
name|abortedFiles
operator|+
literal|" success="
operator|+
name|success
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|anyChanges
specifier|synchronized
name|boolean
name|anyChanges
parameter_list|()
block|{
return|return
name|numDocsInRAM
operator|.
name|get
argument_list|()
operator|!=
literal|0
operator|||
name|anyDeletions
argument_list|()
return|;
block|}
DECL|method|getBufferedDeleteTermsSize
specifier|public
name|int
name|getBufferedDeleteTermsSize
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|it
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocumentsWriterPerThread
name|dwpt
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|perThread
decl_stmt|;
name|size
operator|+=
name|dwpt
operator|.
name|pendingDeletes
operator|.
name|terms
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|size
operator|+=
name|pendingDeletes
operator|.
name|terms
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
comment|//for testing
DECL|method|getNumBufferedDeleteTerms
specifier|public
name|int
name|getNumBufferedDeleteTerms
parameter_list|()
block|{
name|int
name|numDeletes
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|it
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocumentsWriterPerThread
name|dwpt
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|perThread
decl_stmt|;
name|numDeletes
operator|+=
name|dwpt
operator|.
name|pendingDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|numDeletes
operator|+=
name|pendingDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
expr_stmt|;
return|return
name|numDeletes
return|;
block|}
comment|// TODO: can we improve performance of this method by keeping track
comment|// here in DW of whether any DWPT has deletions?
DECL|method|anyDeletions
specifier|public
specifier|synchronized
name|boolean
name|anyDeletions
parameter_list|()
block|{
if|if
condition|(
name|pendingDeletes
operator|.
name|any
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|state
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|state
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|.
name|perThread
operator|.
name|pendingDeletes
operator|.
name|any
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
finally|finally
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|close
name|void
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|updateDocument
name|boolean
name|updateDocument
parameter_list|(
specifier|final
name|Document
name|doc
parameter_list|,
specifier|final
name|Analyzer
name|analyzer
parameter_list|,
specifier|final
name|Term
name|delTerm
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|SegmentInfo
name|newSegment
init|=
literal|null
decl_stmt|;
name|BufferedDeletes
name|segmentDeletes
init|=
literal|null
decl_stmt|;
name|BitVector
name|deletedDocs
init|=
literal|null
decl_stmt|;
name|ThreadState
name|perThread
init|=
name|perThreadPool
operator|.
name|getAndLock
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|this
argument_list|,
name|doc
argument_list|)
decl_stmt|;
try|try
block|{
name|DocumentsWriterPerThread
name|dwpt
init|=
name|perThread
operator|.
name|perThread
decl_stmt|;
name|long
name|perThreadRAMUsedBeforeAdd
init|=
name|dwpt
operator|.
name|bytesUsed
argument_list|()
decl_stmt|;
name|dwpt
operator|.
name|updateDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|,
name|delTerm
argument_list|)
expr_stmt|;
name|numDocsInRAM
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|newSegment
operator|=
name|finishAddDocument
argument_list|(
name|dwpt
argument_list|,
name|perThreadRAMUsedBeforeAdd
argument_list|)
expr_stmt|;
if|if
condition|(
name|newSegment
operator|!=
literal|null
condition|)
block|{
name|deletedDocs
operator|=
name|dwpt
operator|.
name|flushState
operator|.
name|deletedDocs
expr_stmt|;
if|if
condition|(
name|dwpt
operator|.
name|pendingDeletes
operator|.
name|any
argument_list|()
condition|)
block|{
name|segmentDeletes
operator|=
name|dwpt
operator|.
name|pendingDeletes
expr_stmt|;
name|dwpt
operator|.
name|pendingDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|perThread
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|segmentDeletes
operator|!=
literal|null
condition|)
block|{
name|pushDeletes
argument_list|(
name|newSegment
argument_list|,
name|segmentDeletes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newSegment
operator|!=
literal|null
condition|)
block|{
name|perThreadPool
operator|.
name|clearThreadBindings
argument_list|(
name|perThread
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addFlushedSegment
argument_list|(
name|newSegment
argument_list|,
name|deletedDocs
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// delete term from other DWPTs later, so that this thread
comment|// doesn't have to lock multiple DWPTs at the same time
if|if
condition|(
name|delTerm
operator|!=
literal|null
condition|)
block|{
name|deleteTerm
argument_list|(
name|delTerm
argument_list|,
name|perThread
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|finishAddDocument
specifier|private
specifier|final
name|SegmentInfo
name|finishAddDocument
parameter_list|(
name|DocumentsWriterPerThread
name|perThread
parameter_list|,
name|long
name|perThreadRAMUsedBeforeAdd
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentInfo
name|newSegment
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|perThread
operator|.
name|getNumDocsInRAM
argument_list|()
operator|==
name|maxBufferedDocs
condition|)
block|{
name|newSegment
operator|=
name|perThread
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|long
name|deltaRAM
init|=
name|perThread
operator|.
name|bytesUsed
argument_list|()
operator|-
name|perThreadRAMUsedBeforeAdd
decl_stmt|;
name|long
name|oldValue
init|=
name|ramUsed
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|ramUsed
operator|.
name|compareAndSet
argument_list|(
name|oldValue
argument_list|,
name|oldValue
operator|+
name|deltaRAM
argument_list|)
condition|)
block|{
name|oldValue
operator|=
name|ramUsed
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
name|newSegment
return|;
block|}
DECL|method|substractFlushedNumDocs
specifier|final
name|void
name|substractFlushedNumDocs
parameter_list|(
name|int
name|numFlushed
parameter_list|)
block|{
name|int
name|oldValue
init|=
name|numDocsInRAM
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|numDocsInRAM
operator|.
name|compareAndSet
argument_list|(
name|oldValue
argument_list|,
name|oldValue
operator|-
name|numFlushed
argument_list|)
condition|)
block|{
name|oldValue
operator|=
name|numDocsInRAM
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|pushDeletes
specifier|private
specifier|final
name|void
name|pushDeletes
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|BufferedDeletes
name|segmentDeletes
parameter_list|)
block|{
synchronized|synchronized
init|(
name|indexWriter
init|)
block|{
comment|// Lock order: DW -> BD
specifier|final
name|long
name|delGen
init|=
name|bufferedDeletesStream
operator|.
name|getNextGen
argument_list|()
decl_stmt|;
if|if
condition|(
name|segmentDeletes
operator|.
name|any
argument_list|()
condition|)
block|{
if|if
condition|(
name|indexWriter
operator|.
name|segmentInfos
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|segmentInfo
operator|!=
literal|null
condition|)
block|{
specifier|final
name|FrozenBufferedDeletes
name|packet
init|=
operator|new
name|FrozenBufferedDeletes
argument_list|(
name|segmentDeletes
argument_list|,
name|delGen
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"flush: push buffered deletes"
argument_list|)
expr_stmt|;
block|}
name|bufferedDeletesStream
operator|.
name|push
argument_list|(
name|packet
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"flush: delGen="
operator|+
name|packet
operator|.
name|gen
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|segmentInfo
operator|!=
literal|null
condition|)
block|{
name|segmentInfo
operator|.
name|setBufferedDeletesGen
argument_list|(
name|packet
operator|.
name|gen
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"flush: drop buffered deletes: no segments"
argument_list|)
expr_stmt|;
block|}
comment|// We can safely discard these deletes: since
comment|// there are no segments, the deletions cannot
comment|// affect anything.
block|}
block|}
elseif|else
if|if
condition|(
name|segmentInfo
operator|!=
literal|null
condition|)
block|{
name|segmentInfo
operator|.
name|setBufferedDeletesGen
argument_list|(
name|delGen
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|flushAllThreads
specifier|final
name|boolean
name|flushAllThreads
parameter_list|(
specifier|final
name|boolean
name|flushDeletes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|flushDeletes
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|pushDeletes
argument_list|(
literal|null
argument_list|,
name|pendingDeletes
argument_list|)
expr_stmt|;
name|pendingDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
name|boolean
name|anythingFlushed
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SegmentInfo
name|newSegment
init|=
literal|null
decl_stmt|;
name|BufferedDeletes
name|segmentDeletes
init|=
literal|null
decl_stmt|;
name|BitVector
name|deletedDocs
init|=
literal|null
decl_stmt|;
name|ThreadState
name|perThread
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|perThread
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|DocumentsWriterPerThread
name|dwpt
init|=
name|perThread
operator|.
name|perThread
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|dwpt
operator|.
name|getNumDocsInRAM
argument_list|()
decl_stmt|;
comment|// Always flush docs if there are any
name|boolean
name|flushDocs
init|=
name|numDocs
operator|>
literal|0
decl_stmt|;
name|String
name|segment
init|=
name|dwpt
operator|.
name|getSegment
argument_list|()
decl_stmt|;
comment|// If we are flushing docs, segment must not be null:
assert|assert
name|segment
operator|!=
literal|null
operator|||
operator|!
name|flushDocs
assert|;
if|if
condition|(
name|flushDocs
condition|)
block|{
name|newSegment
operator|=
name|dwpt
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|newSegment
operator|!=
literal|null
condition|)
block|{
name|anythingFlushed
operator|=
literal|true
expr_stmt|;
name|deletedDocs
operator|=
name|dwpt
operator|.
name|flushState
operator|.
name|deletedDocs
expr_stmt|;
name|perThreadPool
operator|.
name|clearThreadBindings
argument_list|(
name|perThread
argument_list|)
expr_stmt|;
if|if
condition|(
name|dwpt
operator|.
name|pendingDeletes
operator|.
name|any
argument_list|()
condition|)
block|{
name|segmentDeletes
operator|=
name|dwpt
operator|.
name|pendingDeletes
expr_stmt|;
name|dwpt
operator|.
name|pendingDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|flushDeletes
operator|&&
name|dwpt
operator|.
name|pendingDeletes
operator|.
name|any
argument_list|()
condition|)
block|{
name|segmentDeletes
operator|=
name|dwpt
operator|.
name|pendingDeletes
expr_stmt|;
name|dwpt
operator|.
name|pendingDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|perThread
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|segmentDeletes
operator|!=
literal|null
condition|)
block|{
name|pushDeletes
argument_list|(
name|newSegment
argument_list|,
name|segmentDeletes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newSegment
operator|!=
literal|null
condition|)
block|{
comment|// important do unlock the perThread before finishFlushedSegment
comment|// is called to prevent deadlock on IndexWriter mutex
name|indexWriter
operator|.
name|addFlushedSegment
argument_list|(
name|newSegment
argument_list|,
name|deletedDocs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|anythingFlushed
return|;
block|}
comment|//  /* We have three pools of RAM: Postings, byte blocks
comment|//   * (holds freq/prox posting data) and per-doc buffers
comment|//   * (stored fields/term vectors).  Different docs require
comment|//   * varying amount of storage from these classes.  For
comment|//   * example, docs with many unique single-occurrence short
comment|//   * terms will use up the Postings RAM and hardly any of
comment|//   * the other two.  Whereas docs with very large terms will
comment|//   * use alot of byte blocks RAM.  This method just frees
comment|//   * allocations from the pools once we are over-budget,
comment|//   * which balances the pools to match the current docs. */
comment|//  void balanceRAM() {
comment|//
comment|//    final boolean doBalance;
comment|//    final long deletesRAMUsed;
comment|//
comment|//    deletesRAMUsed = bufferedDeletes.bytesUsed();
comment|//
comment|//    synchronized(this) {
comment|//      if (ramBufferSize == IndexWriterConfig.DISABLE_AUTO_FLUSH || bufferIsFull) {
comment|//        return;
comment|//      }
comment|//
comment|//      doBalance = bytesUsed() + deletesRAMUsed>= ramBufferSize;
comment|//    }
comment|//
comment|//    if (doBalance) {
comment|//
comment|//      if (infoStream != null)
comment|//        message("  RAM: balance allocations: usedMB=" + toMB(bytesUsed()) +
comment|//                " vs trigger=" + toMB(ramBufferSize) +
comment|//                " deletesMB=" + toMB(deletesRAMUsed) +
comment|//                " byteBlockFree=" + toMB(byteBlockAllocator.bytesUsed()) +
comment|//                " perDocFree=" + toMB(perDocAllocator.bytesUsed()));
comment|//
comment|//      final long startBytesUsed = bytesUsed() + deletesRAMUsed;
comment|//
comment|//      int iter = 0;
comment|//
comment|//      // We free equally from each pool in 32 KB
comment|//      // chunks until we are below our threshold
comment|//      // (freeLevel)
comment|//
comment|//      boolean any = true;
comment|//
comment|//      while(bytesUsed()+deletesRAMUsed> freeLevel) {
comment|//
comment|//        synchronized(this) {
comment|//          if (0 == perDocAllocator.numBufferedBlocks()&&
comment|//              0 == byteBlockAllocator.numBufferedBlocks()&&
comment|//              0 == freeIntBlocks.size()&& !any) {
comment|//            // Nothing else to free -- must flush now.
comment|//            bufferIsFull = bytesUsed()+deletesRAMUsed> ramBufferSize;
comment|//            if (infoStream != null) {
comment|//              if (bytesUsed()+deletesRAMUsed> ramBufferSize)
comment|//                message("    nothing to free; set bufferIsFull");
comment|//              else
comment|//                message("    nothing to free");
comment|//            }
comment|//            break;
comment|//          }
comment|//
comment|//          if ((0 == iter % 4)&& byteBlockAllocator.numBufferedBlocks()> 0) {
comment|//            byteBlockAllocator.freeBlocks(1);
comment|//          }
comment|//          if ((1 == iter % 4)&& freeIntBlocks.size()> 0) {
comment|//            freeIntBlocks.remove(freeIntBlocks.size()-1);
comment|//            bytesUsed.addAndGet(-INT_BLOCK_SIZE * RamUsageEstimator.NUM_BYTES_INT);
comment|//          }
comment|//          if ((2 == iter % 4)&& perDocAllocator.numBufferedBlocks()> 0) {
comment|//            perDocAllocator.freeBlocks(32); // Remove upwards of 32 blocks (each block is 1K)
comment|//          }
comment|//        }
comment|//
comment|//        if ((3 == iter % 4)&& any)
comment|//          // Ask consumer to free any recycled state
comment|//          any = consumer.freeRAM();
comment|//
comment|//        iter++;
comment|//      }
comment|//
comment|//      if (infoStream != null)
comment|//        message("    after free: freedMB=" + nf.format((startBytesUsed-bytesUsed()-deletesRAMUsed)/1024./1024.) + " usedMB=" + nf.format((bytesUsed()+deletesRAMUsed)/1024./1024.));
comment|//    }
comment|//  }
block|}
end_class

end_unit

