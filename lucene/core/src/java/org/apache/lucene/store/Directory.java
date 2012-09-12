begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

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
name|Closeable
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

begin_comment
comment|// for javadocs
end_comment

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
name|IOUtils
import|;
end_import

begin_comment
comment|/** A Directory is a flat list of files.  Files may be written once, when they  * are created.  Once a file is created it may only be opened for read, or  * deleted.  Random access is permitted both when reading and writing.  *  *<p> Java's i/o APIs not used directly, but rather all i/o is  * through this API.  This permits things such as:<ul>  *<li> implementation of RAM-based indices;  *<li> implementation indices stored in a database, via JDBC;  *<li> implementation of an index as a single file;  *</ul>  *  * Directory locking is implemented by an instance of {@link  * LockFactory}, and can be changed for each Directory  * instance using {@link #setLockFactory}.  *  */
end_comment

begin_class
DECL|class|Directory
specifier|public
specifier|abstract
class|class
name|Directory
implements|implements
name|Closeable
block|{
DECL|field|isOpen
specifier|volatile
specifier|protected
name|boolean
name|isOpen
init|=
literal|true
decl_stmt|;
comment|/** Holds the LockFactory instance (implements locking for    * this Directory instance). */
DECL|field|lockFactory
specifier|protected
name|LockFactory
name|lockFactory
decl_stmt|;
comment|/**    * Returns an array of strings, one for each file in the directory.    *     * @throws NoSuchDirectoryException if the directory is not prepared for any    *         write operations (such as {@link #createOutput(String, IOContext)}).    * @throws IOException in case of other IO errors    */
DECL|method|listAll
specifier|public
specifier|abstract
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns true iff a file with the given name exists. */
DECL|method|fileExists
specifier|public
specifier|abstract
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Removes an existing file in the directory. */
DECL|method|deleteFile
specifier|public
specifier|abstract
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the length of a file in the directory. This method follows the    * following contract:    *<ul>    *<li>Throws {@link FileNotFoundException} if the file does not exist    *<li>Returns a value&ge;0 if the file exists, which specifies its length.    *</ul>    *     * @param name the name of the file for which to return the length.    * @throws FileNotFoundException if the file does not exist.    * @throws IOException if there was an IO error while retrieving the file's    *         length.    */
DECL|method|fileLength
specifier|public
specifier|abstract
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Creates a new, empty file in the directory with the given name.       Returns a stream writing this file. */
DECL|method|createOutput
specifier|public
specifier|abstract
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ensure that any writes to these files are moved to    * stable storage.  Lucene uses this to properly commit    * changes to the index, to prevent a machine/OS crash    * from corrupting the index.<br/>    *<br/>    * NOTE: Clients may call this method for same files over    * and over again, so some impls might optimize for that.    * For other impls the operation can be a noop, for various    * reasons.    */
DECL|method|sync
specifier|public
specifier|abstract
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a stream reading an existing file, with the    * specified read buffer size.  The particular Directory    * implementation may ignore the buffer size.  Currently    * the only Directory implementations that respect this    * parameter are {@link FSDirectory} and {@link    * CompoundFileDirectory}.   */
DECL|method|openInput
specifier|public
specifier|abstract
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Construct a {@link Lock}.    * @param name the name of the lock file    */
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|lockFactory
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Attempt to clear (forcefully unlock and remove) the    * specified lock.  Only call this at a time when you are    * certain this lock is no longer in use.    * @param name name of the lock to be cleared.    */
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lockFactory
operator|!=
literal|null
condition|)
block|{
name|lockFactory
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Closes the store. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the LockFactory that this Directory instance should    * use for its locking implementation.  Each * instance of    * LockFactory should only be used for one directory (ie,    * do not share a single instance across multiple    * Directories).    *    * @param lockFactory instance of {@link LockFactory}.    */
DECL|method|setLockFactory
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|lockFactory
operator|!=
literal|null
assert|;
name|this
operator|.
name|lockFactory
operator|=
name|lockFactory
expr_stmt|;
name|lockFactory
operator|.
name|setLockPrefix
argument_list|(
name|this
operator|.
name|getLockID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the LockFactory that this Directory instance is    * using for its locking implementation.  Note that this    * may be null for Directory implementations that provide    * their own locking implementation.    */
DECL|method|getLockFactory
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|lockFactory
return|;
block|}
comment|/**    * Return a string identifier that uniquely differentiates    * this Directory instance from other Directory instances.    * This ID should be the same if two Directory instances    * (even in different JVMs and/or on different machines)    * are considered "the same index".  This is how locking    * "scopes" to the right index.    */
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
return|return
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|" lockFactory="
operator|+
name|getLockFactory
argument_list|()
return|;
block|}
comment|/**    * Copies the file<i>src</i> to {@link Directory}<i>to</i> under the new    * file name<i>dest</i>.    *<p>    * If you want to copy the entire source directory to the destination one, you    * can do so like this:    *     *<pre class="prettyprint">    * Directory to; // the directory to copy to    * for (String file : dir.listAll()) {    *   dir.copy(to, file, newFile); // newFile can be either file, or a new name    * }    *</pre>    *<p>    *<b>NOTE:</b> this method does not check whether<i>dest</i> exist and will    * overwrite it if it does.    */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|Directory
name|to
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|os
init|=
literal|null
decl_stmt|;
name|IndexInput
name|is
init|=
literal|null
decl_stmt|;
name|IOException
name|priorException
init|=
literal|null
decl_stmt|;
try|try
block|{
name|os
operator|=
name|to
operator|.
name|createOutput
argument_list|(
name|dest
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|is
operator|=
name|openInput
argument_list|(
name|src
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|os
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|is
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|priorException
operator|=
name|ioe
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|priorException
argument_list|,
name|os
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates an {@link IndexInputSlicer} for the given file name.    * IndexInputSlicer allows other {@link Directory} implementations to    * efficiently open one or more sliced {@link IndexInput} instances from a    * single file handle. The underlying file handle is kept open until the    * {@link IndexInputSlicer} is closed.    *    * @throws IOException    *           if an {@link IOException} occurs    * @lucene.internal    * @lucene.experimental    */
DECL|method|createSlicer
specifier|public
name|IndexInputSlicer
name|createSlicer
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|IndexInputSlicer
argument_list|()
block|{
specifier|private
specifier|final
name|IndexInput
name|base
init|=
name|Directory
operator|.
name|this
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|IndexInput
name|openSlice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
return|return
operator|new
name|SlicedIndexInput
argument_list|(
literal|"SlicedIndexInput("
operator|+
name|sliceDescription
operator|+
literal|" in "
operator|+
name|base
operator|+
literal|")"
argument_list|,
name|base
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**    * @throws AlreadyClosedException if this Directory is closed    */
DECL|method|ensureOpen
specifier|protected
specifier|final
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
operator|!
name|isOpen
condition|)
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Directory is closed"
argument_list|)
throw|;
block|}
comment|/**    * Allows to create one or more sliced {@link IndexInput} instances from a single     * file handle. Some {@link Directory} implementations may be able to efficiently map slices of a file    * into memory when only certain parts of a file are required.       * @lucene.internal    * @lucene.experimental    */
DECL|class|IndexInputSlicer
specifier|public
specifier|abstract
class|class
name|IndexInputSlicer
implements|implements
name|Closeable
block|{
comment|/**      * Returns an {@link IndexInput} slice starting at the given offset with the given length.      */
DECL|method|openSlice
specifier|public
specifier|abstract
name|IndexInput
name|openSlice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/** Implementation of an IndexInput that reads from a portion of    *  a file.    */
DECL|class|SlicedIndexInput
specifier|private
specifier|static
specifier|final
class|class
name|SlicedIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|field|base
name|IndexInput
name|base
decl_stmt|;
DECL|field|fileOffset
name|long
name|fileOffset
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
DECL|method|SlicedIndexInput
name|SlicedIndexInput
parameter_list|(
specifier|final
name|String
name|sliceDescription
parameter_list|,
specifier|final
name|IndexInput
name|base
parameter_list|,
specifier|final
name|long
name|fileOffset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|)
block|{
name|this
argument_list|(
name|sliceDescription
argument_list|,
name|base
argument_list|,
name|fileOffset
argument_list|,
name|length
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|SlicedIndexInput
name|SlicedIndexInput
parameter_list|(
specifier|final
name|String
name|sliceDescription
parameter_list|,
specifier|final
name|IndexInput
name|base
parameter_list|,
specifier|final
name|long
name|fileOffset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
block|{
name|super
argument_list|(
literal|"SlicedIndexInput("
operator|+
name|sliceDescription
operator|+
literal|" in "
operator|+
name|base
operator|+
literal|" slice="
operator|+
name|fileOffset
operator|+
literal|":"
operator|+
operator|(
name|fileOffset
operator|+
name|length
operator|)
operator|+
literal|")"
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileOffset
operator|=
name|fileOffset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SlicedIndexInput
name|clone
parameter_list|()
block|{
name|SlicedIndexInput
name|clone
init|=
operator|(
name|SlicedIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|base
operator|=
name|base
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|fileOffset
operator|=
name|fileOffset
expr_stmt|;
name|clone
operator|.
name|length
operator|=
name|length
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/** Expert: implements buffer refill.  Reads bytes from the current      *  position in the input.      * @param b the array to read bytes into      * @param offset the offset in the array to start storing bytes      * @param len the number of bytes to read      */
annotation|@
name|Override
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|+
name|len
operator|>
name|length
condition|)
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
name|base
operator|.
name|seek
argument_list|(
name|fileOffset
operator|+
name|start
argument_list|)
expr_stmt|;
name|base
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: implements seek.  Sets current position in this file, where      *  the next {@link #readInternal(byte[],int,int)} will occur.      * @see #readInternal(byte[],int,int)      */
annotation|@
name|Override
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
block|{}
comment|/** Closes the stream to further operations. */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
block|}
end_class

end_unit

