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
name|util
operator|.
name|Arrays
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|document
operator|.
name|FieldSelector
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
name|DirectoryReader
operator|.
name|MultiTermDocs
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
name|DirectoryReader
operator|.
name|MultiTermEnum
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
name|DirectoryReader
operator|.
name|MultiTermPositions
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
name|DefaultSimilarity
import|;
end_import

begin_comment
comment|/** An IndexReader which reads multiple indexes, appending  * their content. */
end_comment

begin_class
DECL|class|MultiReader
specifier|public
class|class
name|MultiReader
extends|extends
name|IndexReader
implements|implements
name|Cloneable
block|{
DECL|field|subReaders
specifier|protected
name|IndexReader
index|[]
name|subReaders
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
comment|// 1st docno for each segment
DECL|field|decrefOnClose
specifier|private
name|boolean
index|[]
name|decrefOnClose
decl_stmt|;
comment|// remember which subreaders to decRef on close
DECL|field|normsCache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|normsCache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
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
comment|/**   *<p>Construct a MultiReader aggregating the named set of (sub)readers.   * Directory locking for delete, undeleteAll, and setNorm operations is   * left to the subreaders.</p>   *<p>Note that all subreaders are closed if this Multireader is closed.</p>   * @param subReaders set of (sub)readers   * @throws IOException   */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
modifier|...
name|subReaders
parameter_list|)
block|{
name|initialize
argument_list|(
name|subReaders
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>Construct a MultiReader aggregating the named set of (sub)readers.    * Directory locking for delete, undeleteAll, and setNorm operations is    * left to the subreaders.</p>    * @param closeSubReaders indicates whether the subreaders should be closed    * when this MultiReader is closed    * @param subReaders set of (sub)readers    * @throws IOException    */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|,
name|boolean
name|closeSubReaders
parameter_list|)
block|{
name|initialize
argument_list|(
name|subReaders
argument_list|,
name|closeSubReaders
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|,
name|boolean
name|closeSubReaders
parameter_list|)
block|{
name|this
operator|.
name|subReaders
operator|=
operator|(
name|IndexReader
index|[]
operator|)
name|subReaders
operator|.
name|clone
argument_list|()
expr_stmt|;
name|starts
operator|=
operator|new
name|int
index|[
name|subReaders
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
comment|// build starts array
name|decrefOnClose
operator|=
operator|new
name|boolean
index|[
name|subReaders
operator|.
name|length
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
name|subReaders
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
name|subReaders
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
operator|!
name|closeSubReaders
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|decrefOnClose
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|decrefOnClose
index|[
name|i
index|]
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|subReaders
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
name|subReaders
operator|.
name|length
index|]
operator|=
name|maxDoc
expr_stmt|;
block|}
comment|/**    * Tries to reopen the subreaders.    *<br>    * If one or more subreaders could be re-opened (i. e. subReader.reopen()     * returned a new instance != subReader), then a new MultiReader instance     * is returned, otherwise this instance is returned.    *<p>    * A re-opened instance might share one or more subreaders with the old     * instance. Index modification operations result in undefined behavior    * when performed before the old instance is closed.    * (see {@link IndexReader#reopen()}).    *<p>    * If subreaders are shared, then the reference count of those    * readers is increased to ensure that the subreaders remain open    * until the last referring reader is closed.    *     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error     */
DECL|method|reopen
specifier|public
specifier|synchronized
name|IndexReader
name|reopen
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|doReopen
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**    * Clones the subreaders.    * (see {@link IndexReader#clone()}).    *<br>    *<p>    * If subreaders are shared, then the reference count of those    * readers is increased to ensure that the subreaders remain open    * until the last referring reader is closed.    */
DECL|method|clone
specifier|public
specifier|synchronized
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
name|doReopen
argument_list|(
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * If clone is true then we clone each of the subreaders    * @param doClone    * @return New IndexReader, or same one (this) if    *   reopen/clone is not necessary    * @throws CorruptIndexException    * @throws IOException    */
DECL|method|doReopen
specifier|protected
name|IndexReader
name|doReopen
parameter_list|(
name|boolean
name|doClone
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|boolean
name|reopened
init|=
literal|false
decl_stmt|;
name|IndexReader
index|[]
name|newSubReaders
init|=
operator|new
name|IndexReader
index|[
name|subReaders
operator|.
name|length
index|]
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|doClone
condition|)
name|newSubReaders
index|[
name|i
index|]
operator|=
operator|(
name|IndexReader
operator|)
name|subReaders
index|[
name|i
index|]
operator|.
name|clone
argument_list|()
expr_stmt|;
else|else
name|newSubReaders
index|[
name|i
index|]
operator|=
name|subReaders
index|[
name|i
index|]
operator|.
name|reopen
argument_list|()
expr_stmt|;
comment|// if at least one of the subreaders was updated we remember that
comment|// and return a new MultiReader
if|if
condition|(
name|newSubReaders
index|[
name|i
index|]
operator|!=
name|subReaders
index|[
name|i
index|]
condition|)
block|{
name|reopened
operator|=
literal|true
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
operator|!
name|success
operator|&&
name|reopened
condition|)
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
name|newSubReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|newSubReaders
index|[
name|i
index|]
operator|!=
name|subReaders
index|[
name|i
index|]
condition|)
block|{
try|try
block|{
name|newSubReaders
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
comment|// keep going - we want to clean up as much as possible
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|reopened
condition|)
block|{
name|boolean
index|[]
name|newDecrefOnClose
init|=
operator|new
name|boolean
index|[
name|subReaders
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|newSubReaders
index|[
name|i
index|]
operator|==
name|subReaders
index|[
name|i
index|]
condition|)
block|{
name|newSubReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|newDecrefOnClose
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|MultiReader
name|mr
init|=
operator|new
name|MultiReader
argument_list|(
name|newSubReaders
argument_list|)
decl_stmt|;
name|mr
operator|.
name|decrefOnClose
operator|=
name|newDecrefOnClose
expr_stmt|;
return|return
name|mr
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
DECL|method|getTermFreqVectors
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
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
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermFreqVectors
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to segment
block|}
DECL|method|getTermFreqVector
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
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
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermFreqVector
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|docNumber
argument_list|)
decl_stmt|;
comment|// find segment num
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|field
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|docNumber
argument_list|)
decl_stmt|;
comment|// find segment num
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|numDocs
specifier|public
specifier|synchronized
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|n
operator|+=
name|subReaders
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
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|maxDoc
return|;
block|}
comment|// inherit javadoc
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
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
name|subReaders
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
argument_list|,
name|fieldSelector
argument_list|)
return|;
comment|// dispatch to segment reader
block|}
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
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
name|subReaders
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
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|hasDeletions
return|;
block|}
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
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
name|subReaders
index|[
name|i
index|]
operator|.
name|deleteDocument
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
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|subReaders
index|[
name|i
index|]
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|hasDeletions
operator|=
literal|false
expr_stmt|;
name|numDocs
operator|=
operator|-
literal|1
expr_stmt|;
comment|// invalidate cache
block|}
DECL|method|readerIndex
specifier|private
name|int
name|readerIndex
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// find reader for doc n:
return|return
name|DirectoryReader
operator|.
name|readerIndex
argument_list|(
name|n
argument_list|,
name|this
operator|.
name|starts
argument_list|,
name|this
operator|.
name|subReaders
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|subReaders
index|[
name|i
index|]
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|field|ones
specifier|private
name|byte
index|[]
name|ones
decl_stmt|;
DECL|method|fakeNorms
specifier|private
name|byte
index|[]
name|fakeNorms
parameter_list|()
block|{
if|if
condition|(
name|ones
operator|==
literal|null
condition|)
name|ones
operator|=
name|SegmentReader
operator|.
name|createFakeNorms
argument_list|(
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ones
return|;
block|}
DECL|method|norms
specifier|public
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
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
if|if
condition|(
operator|!
name|hasNorms
argument_list|(
name|field
argument_list|)
condition|)
return|return
literal|null
return|;
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|subReaders
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
DECL|method|norms
specifier|public
specifier|synchronized
name|void
name|norms
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|result
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|normsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
comment|// read from segments
name|subReaders
index|[
name|i
index|]
operator|.
name|norms
argument_list|(
name|field
argument_list|,
name|result
argument_list|,
name|offset
operator|+
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
operator|&&
operator|!
name|hasNorms
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|result
argument_list|,
name|offset
argument_list|,
name|result
operator|.
name|length
argument_list|,
name|DefaultSimilarity
operator|.
name|encodeNorm
argument_list|(
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
comment|// cache hit
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|offset
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// read from segments
name|subReaders
index|[
name|i
index|]
operator|.
name|norms
argument_list|(
name|field
argument_list|,
name|result
argument_list|,
name|offset
operator|+
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
synchronized|synchronized
init|(
name|normsCache
init|)
block|{
name|normsCache
operator|.
name|remove
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// clear cache
block|}
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
name|subReaders
index|[
name|i
index|]
operator|.
name|setNorm
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// dispatch
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiTermEnum
argument_list|(
name|this
argument_list|,
name|subReaders
argument_list|,
name|starts
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiTermEnum
argument_list|(
name|this
argument_list|,
name|subReaders
argument_list|,
name|starts
argument_list|,
name|term
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|total
operator|+=
name|subReaders
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
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiTermDocs
argument_list|(
name|this
argument_list|,
name|subReaders
argument_list|,
name|starts
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiTermPositions
argument_list|(
name|this
argument_list|,
name|subReaders
argument_list|,
name|starts
argument_list|)
return|;
block|}
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitUserData
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|subReaders
index|[
name|i
index|]
operator|.
name|commit
argument_list|(
name|commitUserData
argument_list|)
expr_stmt|;
block|}
DECL|method|doClose
specifier|protected
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|decrefOnClose
index|[
name|i
index|]
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|subReaders
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
DECL|method|getFieldNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|(
name|IndexReader
operator|.
name|FieldOption
name|fieldNames
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|DirectoryReader
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|,
name|this
operator|.
name|subReaders
argument_list|)
return|;
block|}
comment|/**    * Checks recursively if all subreaders are up to date.     */
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|subReaders
index|[
name|i
index|]
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// all subreaders are up to date
return|return
literal|true
return|;
block|}
comment|/** Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"MultiReader does not support this method."
argument_list|)
throw|;
block|}
DECL|method|getSequentialSubReaders
specifier|public
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
name|subReaders
return|;
block|}
block|}
end_class

end_unit

