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
name|util
operator|.
name|Collection
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
name|Field
import|;
end_import

begin_comment
comment|// for javadoc
end_comment

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

begin_comment
comment|/** IndexReader is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable.<p> Concrete subclasses of IndexReader are usually constructed with a call to  the static method {@link #open}.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral--they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.   @author Doug Cutting  @version $Id$ */
end_comment

begin_class
DECL|class|IndexReader
specifier|public
specifier|abstract
class|class
name|IndexReader
block|{
comment|/**    * Constructor used if IndexReader is not owner of its directory.     * This is used for IndexReaders that are used within other IndexReaders that take care or locking directories.    *     * @param directory Directory where IndexReader files reside.    */
DECL|method|IndexReader
specifier|protected
name|IndexReader
parameter_list|(
name|Directory
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
comment|/**    * Constructor used if IndexReader is owner of its directory.    * If IndexReader is owner of its directory, it locks its directory in case of write operations.    *     * @param directory Directory where IndexReader files reside.    * @param segmentInfos Used for write-l    * @param closeDirectory    */
DECL|method|IndexReader
name|IndexReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|closeDirectory
parameter_list|)
block|{
name|init
argument_list|(
name|directory
argument_list|,
name|segmentInfos
argument_list|,
name|closeDirectory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|init
name|void
name|init
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|closeDirectory
parameter_list|,
name|boolean
name|directoryOwner
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentInfos
operator|=
name|segmentInfos
expr_stmt|;
name|this
operator|.
name|directoryOwner
operator|=
name|directoryOwner
expr_stmt|;
name|this
operator|.
name|closeDirectory
operator|=
name|closeDirectory
expr_stmt|;
block|}
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|directoryOwner
specifier|private
name|boolean
name|directoryOwner
decl_stmt|;
DECL|field|closeDirectory
specifier|private
name|boolean
name|closeDirectory
decl_stmt|;
DECL|field|segmentInfos
specifier|private
name|SegmentInfos
name|segmentInfos
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
DECL|field|stale
specifier|private
name|boolean
name|stale
decl_stmt|;
DECL|field|hasChanges
specifier|private
name|boolean
name|hasChanges
decl_stmt|;
comment|/** Returns an IndexReader reading the index in an FSDirectory in the named    path. */
DECL|method|open
specifier|public
specifier|static
name|IndexReader
name|open
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Returns an IndexReader reading the index in an FSDirectory in the named    path. */
DECL|method|open
specifier|public
specifier|static
name|IndexReader
name|open
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Returns an IndexReader reading the index in the given Directory. */
DECL|method|open
specifier|public
specifier|static
name|IndexReader
name|open
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|directory
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|open
specifier|private
specifier|static
name|IndexReader
name|open
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|,
specifier|final
name|boolean
name|closeDirectory
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// in-& inter-process sync
return|return
operator|(
name|IndexReader
operator|)
operator|new
name|Lock
operator|.
name|With
argument_list|(
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|COMMIT_LOCK_NAME
argument_list|)
argument_list|,
name|IndexWriter
operator|.
name|COMMIT_LOCK_TIMEOUT
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
name|SegmentInfos
name|infos
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|infos
operator|.
name|read
argument_list|(
name|directory
argument_list|)
expr_stmt|;
if|if
condition|(
name|infos
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// index is optimized
return|return
name|SegmentReader
operator|.
name|get
argument_list|(
name|infos
argument_list|,
name|infos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|,
name|closeDirectory
argument_list|)
return|;
block|}
else|else
block|{
name|IndexReader
index|[]
name|readers
init|=
operator|new
name|IndexReader
index|[
name|infos
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
literal|0
init|;
name|i
operator|<
name|infos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|readers
index|[
name|i
index|]
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|MultiReader
argument_list|(
name|directory
argument_list|,
name|infos
argument_list|,
name|closeDirectory
argument_list|,
name|readers
argument_list|)
return|;
block|}
block|}
block|}
operator|.
name|run
argument_list|()
return|;
block|}
block|}
comment|/** Returns the directory this index resides in. */
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
comment|/**     * Returns the time the index in the named directory was last modified.     *     *<p>Synchronization of IndexReader and IndexWriter instances is     * no longer done via time stamps of the segments file since the time resolution     * depends on the hardware platform. Instead, a version number is maintained    * within the segments file, which is incremented everytime when the index is    * changed.</p>    *     * @deprecated  Replaced by {@link #getCurrentVersion(String)}    * */
DECL|method|lastModified
specifier|public
specifier|static
name|long
name|lastModified
parameter_list|(
name|String
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lastModified
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Returns the time the index in the named directory was last modified.     *     *<p>Synchronization of IndexReader and IndexWriter instances is     * no longer done via time stamps of the segments file since the time resolution     * depends on the hardware platform. Instead, a version number is maintained    * within the segments file, which is incremented everytime when the index is    * changed.</p>    *     * @deprecated  Replaced by {@link #getCurrentVersion(File)}    * */
DECL|method|lastModified
specifier|public
specifier|static
name|long
name|lastModified
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FSDirectory
operator|.
name|fileModified
argument_list|(
name|directory
argument_list|,
literal|"segments"
argument_list|)
return|;
block|}
comment|/**     * Returns the time the index in the named directory was last modified.     *     *<p>Synchronization of IndexReader and IndexWriter instances is     * no longer done via time stamps of the segments file since the time resolution     * depends on the hardware platform. Instead, a version number is maintained    * within the segments file, which is incremented everytime when the index is    * changed.</p>    *     * @deprecated  Replaced by {@link #getCurrentVersion(Directory)}    * */
DECL|method|lastModified
specifier|public
specifier|static
name|long
name|lastModified
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|directory
operator|.
name|fileModified
argument_list|(
literal|"segments"
argument_list|)
return|;
block|}
comment|/**    * Reads version number from segments files. The version number counts the    * number of changes of the index.    *     * @param directory where the index resides.    * @return version number.    * @throws IOException if segments file cannot be read    */
DECL|method|getCurrentVersion
specifier|public
specifier|static
name|long
name|getCurrentVersion
parameter_list|(
name|String
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getCurrentVersion
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Reads version number from segments files. The version number counts the    * number of changes of the index.    *     * @param directory where the index resides.    * @return version number.    * @throws IOException if segments file cannot be read    */
DECL|method|getCurrentVersion
specifier|public
specifier|static
name|long
name|getCurrentVersion
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|directory
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|long
name|version
init|=
name|getCurrentVersion
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|version
return|;
block|}
comment|/**    * Reads version number from segments files. The version number counts the    * number of changes of the index.    *     * @param directory where the index resides.    * @return version number.    * @throws IOException if segments file cannot be read.    */
DECL|method|getCurrentVersion
specifier|public
specifier|static
name|long
name|getCurrentVersion
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/** Return an array of term frequency vectors for the specified document.    *  The array contains a vector for each vectorized field in the document.    *  Each vector contains terms and frequencies for all terms    *  in a given vectorized field.    *  If no such fields existed, the method returns null.    *    * @see Field#isTermVectorStored()    */
DECL|method|getTermFreqVectors
specifier|abstract
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|docNumber
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Return a term frequency vector for the specified document and field. The    *  vector returned contains terms and frequencies for those terms in    *  the specified field of this document, if the field had storeTermVector    *  flag set.  If the flag was not set, the method returns null.    *    * @see Field#isTermVectorStored()    */
DECL|method|getTermFreqVector
specifier|abstract
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns<code>true</code> if an index exists at the specified directory.    * If the directory does not exist or if there is no index in it.    *<code>false</code> is returned.    * @param  directory the directory to check for an index    * @return<code>true</code> if an index exists;<code>false</code> otherwise    */
DECL|method|indexExists
specifier|public
specifier|static
name|boolean
name|indexExists
parameter_list|(
name|String
name|directory
parameter_list|)
block|{
return|return
operator|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"segments"
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
return|;
block|}
comment|/**    * Returns<code>true</code> if an index exists at the specified directory.    * If the directory does not exist or if there is no index in it.    * @param  directory the directory to check for an index    * @return<code>true</code> if an index exists;<code>false</code> otherwise    */
DECL|method|indexExists
specifier|public
specifier|static
name|boolean
name|indexExists
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
return|return
operator|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"segments"
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
return|;
block|}
comment|/**    * Returns<code>true</code> if an index exists at the specified directory.    * If the directory does not exist or if there is no index in it.    * @param  directory the directory to check for an index    * @return<code>true</code> if an index exists;<code>false</code> otherwise    * @throws IOException if there is a problem with accessing the index    */
DECL|method|indexExists
specifier|public
specifier|static
name|boolean
name|indexExists
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|directory
operator|.
name|fileExists
argument_list|(
literal|"segments"
argument_list|)
return|;
block|}
comment|/** Returns the number of documents in this index. */
DECL|method|numDocs
specifier|public
specifier|abstract
name|int
name|numDocs
parameter_list|()
function_decl|;
comment|/** Returns one greater than the largest possible document number.    This may be used to, e.g., determine how big to allocate an array which    will have an element for every document number in an index.    */
DECL|method|maxDoc
specifier|public
specifier|abstract
name|int
name|maxDoc
parameter_list|()
function_decl|;
comment|/** Returns the stored fields of the<code>n</code><sup>th</sup><code>Document</code> in this index. */
DECL|method|document
specifier|public
specifier|abstract
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns true if document<i>n</i> has been deleted */
DECL|method|isDeleted
specifier|public
specifier|abstract
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
function_decl|;
comment|/** Returns true if any documents have been deleted */
DECL|method|hasDeletions
specifier|public
specifier|abstract
name|boolean
name|hasDeletions
parameter_list|()
function_decl|;
comment|/** Returns the byte-encoded normalization factor for the named field of    * every document.  This is used by the search code to score documents.    *    * @see Field#setBoost(float)    */
DECL|method|norms
specifier|public
specifier|abstract
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Reads the byte-encoded normalization factor for the named field of every    *  document.  This is used by the search code to score documents.    *    * @see Field#setBoost(float)    */
DECL|method|norms
specifier|public
specifier|abstract
name|void
name|norms
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Resets the normalization factor for the named field of the named    * document.  The norm represents the product of the field's {@link    * Field#setBoost(float) boost} and its {@link Similarity#lengthNorm(String,    * int) length normalization}.  Thus, to preserve the length normalization    * values when resetting this, one should base the new value upon the old.    *    * @see #norms(String)    * @see Similarity#decodeNorm(byte)    */
DECL|method|setNorm
specifier|public
specifier|final
specifier|synchronized
name|void
name|setNorm
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|directoryOwner
condition|)
name|aquireWriteLock
argument_list|()
expr_stmt|;
name|doSetNorm
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Implements setNorm in subclass.*/
DECL|method|doSetNorm
specifier|protected
specifier|abstract
name|void
name|doSetNorm
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Resets the normalization factor for the named field of the named    * document.    *    * @see #norms(String)    * @see Similarity#decodeNorm(byte)    */
DECL|method|setNorm
specifier|public
name|void
name|setNorm
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|setNorm
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|Similarity
operator|.
name|encodeNorm
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Returns an enumeration of all the terms in the index.    The enumeration is ordered by Term.compareTo().  Each term    is greater than all that precede it in the enumeration.    */
DECL|method|terms
specifier|public
specifier|abstract
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns an enumeration of all terms after a given term.    The enumeration is ordered by Term.compareTo().  Each term    is greater than all that precede it in the enumeration.    */
DECL|method|terms
specifier|public
specifier|abstract
name|TermEnum
name|terms
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of documents containing the term<code>t</code>. */
DECL|method|docFreq
specifier|public
specifier|abstract
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns an enumeration of all the documents which contain<code>term</code>. For each document, the document number, the frequency of    the term in that document is also provided, for use in search scoring.    Thus, this method implements the mapping:<p><ul>    Term&nbsp;&nbsp; =&gt;&nbsp;&nbsp;&lt;docNum, freq&gt;<sup>*</sup></ul><p>The enumeration is ordered by document number.  Each document number    is greater than all that precede it in the enumeration.    */
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|TermDocs
name|termDocs
init|=
name|termDocs
argument_list|()
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|termDocs
return|;
block|}
comment|/** Returns an unpositioned {@link TermDocs} enumerator. */
DECL|method|termDocs
specifier|public
specifier|abstract
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns an enumeration of all the documents which contain<code>term</code>.  For each document, in addition to the document number    and frequency of the term in that document, a list of all of the ordinal    positions of the term in the document is available.  Thus, this method    implements the mapping:<p><ul>    Term&nbsp;&nbsp; =&gt;&nbsp;&nbsp;&lt;docNum, freq,&lt;pos<sub>1</sub>, pos<sub>2</sub>, ...    pos<sub>freq-1</sub>&gt;&gt;<sup>*</sup></ul><p> This positional information faciliates phrase and proximity searching.<p>The enumeration is ordered by document number.  Each document number is    greater than all that precede it in the enumeration.    */
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|TermPositions
name|termPositions
init|=
name|termPositions
argument_list|()
decl_stmt|;
name|termPositions
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|termPositions
return|;
block|}
comment|/** Returns an unpositioned {@link TermPositions} enumerator. */
DECL|method|termPositions
specifier|public
specifier|abstract
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Tries to acquire the WriteLock on this directory.    * this method is only valid if this IndexReader is directory owner.    *     * @throws IOException If WriteLock cannot be acquired.    */
DECL|method|aquireWriteLock
specifier|private
name|void
name|aquireWriteLock
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stale
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"IndexReader out of date and no longer valid for delete, undelete, or setNorm operations"
argument_list|)
throw|;
if|if
condition|(
name|writeLock
operator|==
literal|null
condition|)
block|{
name|Lock
name|writeLock
init|=
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeLock
operator|.
name|obtain
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_TIMEOUT
argument_list|)
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
comment|// we have to check whether index has changed since this reader was opened.
comment|// if so, this reader is no longer valid for deletion
if|if
condition|(
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
operator|>
name|segmentInfos
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|stale
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"IndexReader out of date and no longer valid for delete, undelete, or setNorm operations"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Deletes the document numbered<code>docNum</code>.  Once a document is    deleted it will not appear in TermDocs or TermPostitions enumerations.    Attempts to read its field with the {@link #document}    method will result in an error.  The presence of this document may still be    reflected in the {@link #docFreq} statistic, though    this will be corrected eventually as the index is further modified.    */
DECL|method|delete
specifier|public
specifier|final
specifier|synchronized
name|void
name|delete
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|directoryOwner
condition|)
name|aquireWriteLock
argument_list|()
expr_stmt|;
name|doDelete
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Implements deletion of the document numbered<code>docNum</code>.    * Applications should call {@link #delete(int)} or {@link #delete(Term)}.    */
DECL|method|doDelete
specifier|protected
specifier|abstract
name|void
name|doDelete
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Deletes all documents containing<code>term</code>.    This is useful if one uses a document field to hold a unique ID string for    the document.  Then to delete such a document, one merely constructs a    term with the appropriate field and the unique ID string as its text and    passes it to this method.  Returns the number of documents deleted.    See {@link #delete(int)} for information about when this deletion will     become effective.    */
DECL|method|delete
specifier|public
specifier|final
name|int
name|delete
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|TermDocs
name|docs
init|=
name|termDocs
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
return|return
literal|0
return|;
name|int
name|n
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|docs
operator|.
name|next
argument_list|()
condition|)
block|{
name|delete
argument_list|(
name|docs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
comment|/** Undeletes all documents currently marked as deleted in this index.*/
DECL|method|undeleteAll
specifier|public
specifier|final
specifier|synchronized
name|void
name|undeleteAll
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|directoryOwner
condition|)
name|aquireWriteLock
argument_list|()
expr_stmt|;
name|doUndeleteAll
argument_list|()
expr_stmt|;
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Implements actual undeleteAll() in subclass. */
DECL|method|doUndeleteAll
specifier|protected
specifier|abstract
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Commit changes resulting from delete, undeleteAll, or setNorm operations    *     * @throws IOException    */
DECL|method|commit
specifier|protected
specifier|final
specifier|synchronized
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasChanges
condition|)
block|{
if|if
condition|(
name|directoryOwner
condition|)
block|{
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
name|IndexWriter
operator|.
name|COMMIT_LOCK_NAME
argument_list|)
argument_list|,
name|IndexWriter
operator|.
name|COMMIT_LOCK_TIMEOUT
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
name|doCommit
argument_list|()
expr_stmt|;
name|segmentInfos
operator|.
name|write
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
else|else
name|doCommit
argument_list|()
expr_stmt|;
block|}
name|hasChanges
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Implements commit. */
DECL|method|doCommit
specifier|protected
specifier|abstract
name|void
name|doCommit
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Closes files associated with this index.    * Also saves any new deletions to disk.    * No other methods should be called after this has been called.    */
DECL|method|close
specifier|public
specifier|final
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|commit
argument_list|()
expr_stmt|;
name|doClose
argument_list|()
expr_stmt|;
if|if
condition|(
name|closeDirectory
condition|)
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Implements close. */
DECL|method|doClose
specifier|protected
specifier|abstract
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Release the write lock, if needed. */
DECL|method|finalize
specifier|protected
specifier|final
name|void
name|finalize
parameter_list|()
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
comment|/**    * Returns a list of all unique field names that exist in the index pointed    * to by this IndexReader.    * @return Collection of Strings indicating the names of the fields    * @throws IOException if there is a problem with accessing the index    */
DECL|method|getFieldNames
specifier|public
specifier|abstract
name|Collection
name|getFieldNames
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of all unique field names that exist in the index pointed    * to by this IndexReader.  The boolean argument specifies whether the fields    * returned are indexed or not.    * @param indexed<code>true</code> if only indexed fields should be returned;    *<code>false</code> if only unindexed fields should be returned.    * @return Collection of Strings indicating the names of the fields    * @throws IOException if there is a problem with accessing the index    */
DECL|method|getFieldNames
specifier|public
specifier|abstract
name|Collection
name|getFieldNames
parameter_list|(
name|boolean
name|indexed
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    *     * @param storedTermVector if true, returns only Indexed fields that have term vector info,     *                        else only indexed fields without term vector info     * @return Collection of Strings indicating the names of the fields    */
DECL|method|getIndexedFieldNames
specifier|public
specifier|abstract
name|Collection
name|getIndexedFieldNames
parameter_list|(
name|boolean
name|storedTermVector
parameter_list|)
function_decl|;
comment|/**    * Returns<code>true</code> iff the index in the named directory is    * currently locked.    * @param directory the directory to check for a lock    * @throws IOException if there is a problem with accessing the index    */
DECL|method|isLocked
specifier|public
specifier|static
name|boolean
name|isLocked
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
operator|.
name|isLocked
argument_list|()
operator|||
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|COMMIT_LOCK_NAME
argument_list|)
operator|.
name|isLocked
argument_list|()
return|;
block|}
comment|/**    * Returns<code>true</code> iff the index in the named directory is    * currently locked.    * @param directory the directory to check for a lock    * @throws IOException if there is a problem with accessing the index    */
DECL|method|isLocked
specifier|public
specifier|static
name|boolean
name|isLocked
parameter_list|(
name|String
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|directory
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|isLocked
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Forcibly unlocks the index in the named directory.    *<P>    * Caution: this should only be used by failure recovery code,    * when it is known that no other process nor thread is in fact    * currently accessing this index.    */
DECL|method|unlock
specifier|public
specifier|static
name|void
name|unlock
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
operator|.
name|release
argument_list|()
expr_stmt|;
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|COMMIT_LOCK_NAME
argument_list|)
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

