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

begin_comment
comment|/** IndexReader is an abstract class, providing an interface for accessing an   index.  Search of an index is done entirely through this abstract interface,   so that any subclass which implements it is searchable.<p> Concrete subclasses of IndexReader are usually constructed with a call to   the static method {@link #open}.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique   document in the index.  These document numbers are ephemeral--they may change   as documents are added to and deleted from an index.  Clients should thus not   rely on a given document having the same number between sessions.    @author Doug Cutting   @version $Id$ */
end_comment

begin_class
DECL|class|IndexReader
specifier|public
specifier|abstract
class|class
name|IndexReader
block|{
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
name|stale
operator|=
literal|false
expr_stmt|;
name|segmentInfos
operator|=
literal|null
expr_stmt|;
block|}
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
DECL|field|segmentInfos
name|SegmentInfos
name|segmentInfos
init|=
literal|null
decl_stmt|;
DECL|field|stale
specifier|private
name|boolean
name|stale
init|=
literal|false
decl_stmt|;
comment|/** Returns an IndexReader reading the index in an FSDirectory in the named   path. */
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
argument_list|)
return|;
block|}
comment|/** Returns an IndexReader reading the index in an FSDirectory in the named   path. */
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
operator|new
name|SegmentReader
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
literal|true
argument_list|)
return|;
block|}
else|else
block|{
name|SegmentReader
index|[]
name|readers
init|=
operator|new
name|SegmentReader
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
operator|new
name|SegmentReader
argument_list|(
name|infos
argument_list|,
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|,
name|i
operator|==
name|infos
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
operator|new
name|SegmentsReader
argument_list|(
name|infos
argument_list|,
name|directory
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
comment|/** Returns one greater than the largest possible document number.     This may be used to, e.g., determine how big to allocate an array which     will have an element for every document number in an index.    */
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
comment|/** Returns an enumeration of all the terms in the index.     The enumeration is ordered by Term.compareTo().  Each term     is greater than all that precede it in the enumeration.    */
DECL|method|terms
specifier|public
specifier|abstract
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns an enumeration of all terms after a given term.     The enumeration is ordered by Term.compareTo().  Each term     is greater than all that precede it in the enumeration.    */
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
comment|/** Returns an enumeration of all the documents which contain<code>term</code>. For each document, the document number, the frequency of     the term in that document is also provided, for use in search scoring.     Thus, this method implements the mapping:<p><ul>     Term&nbsp;&nbsp; =&gt;&nbsp;&nbsp;&lt;docNum, freq&gt;<sup>*</sup></ul><p>The enumeration is ordered by document number.  Each document number     is greater than all that precede it in the enumeration.   */
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
comment|/** Returns an enumeration of all the documents which contain<code>term</code>.  For each document, in addition to the document number     and frequency of the term in that document, a list of all of the ordinal     positions of the term in the document is available.  Thus, this method     implements the mapping:<p><ul>     Term&nbsp;&nbsp; =&gt;&nbsp;&nbsp;&lt;docNum, freq,&lt;pos<sub>1</sub>, pos<sub>2</sub>, ...           pos<sub>freq-1</sub>&gt;&gt;<sup>*</sup></ul><p> This positional information faciliates phrase and proximity searching.<p>The enumeration is ordered by document number.  Each document number is     greater than all that precede it in the enumeration.   */
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
comment|/** Deletes the document numbered<code>docNum</code>.  Once a document is     deleted it will not appear in TermDocs or TermPostitions enumerations.     Attempts to read its field with the {@link #document}     method will result in an error.  The presence of this document may still be     reflected in the {@link #docFreq} statistic, though     this will be corrected eventually as the index is further modified.   */
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
name|stale
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"IndexReader out of date and no longer valid for deletion"
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
name|segmentInfos
operator|!=
literal|null
operator|&&
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
literal|"IndexReader out of date and no longer valid for deletion"
argument_list|)
throw|;
block|}
block|}
name|doDelete
argument_list|(
name|docNum
argument_list|)
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
comment|/** Deletes all documents containing<code>term</code>.     This is useful if one uses a document field to hold a unique ID string for     the document.  Then to delete such a document, one merely constructs a     term with the appropriate field and the unique ID string as its text and     passes it to this method.  Returns the number of documents deleted.   */
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
specifier|abstract
name|void
name|undeleteAll
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
name|doClose
argument_list|()
expr_stmt|;
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
comment|/**    * Returns a list of all unique field names that exist in the index pointed to by    * this IndexReader.    * @return Collection of Strings indicating the names of the fields    * @throws IOException if there is a problem with accessing the index    */
DECL|method|getFieldNames
specifier|public
specifier|abstract
name|Collection
name|getFieldNames
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of all unique field names that exist in the index pointed to by    * this IndexReader.  The boolean argument specifies whether the fields returned    * are indexed or not.    * @param indexed<code>true</code> if only indexed fields should be returned;    *<code>false</code> if only unindexed fields should be returned.    * @return Collection of Strings indicating the names of the fields    * @throws IOException if there is a problem with accessing the index    */
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
return|return
name|isLocked
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|directory
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Forcibly unlocks the index in the named directory.     *<P>     * Caution: this should only be used by failure recovery code,     * when it is known that no other process nor thread is in fact     * currently accessing this index.     */
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

