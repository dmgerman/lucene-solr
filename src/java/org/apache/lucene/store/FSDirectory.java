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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001, 2002, 2003 The Apache Software Foundation.  All  * rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
import|;
end_import

begin_comment
comment|/**  * Straightforward implementation of {@link Directory} as a directory of files.  *<p>If the system property 'disableLuceneLocks' has the String value of  * "true", lock creation will be disabled.  *  * @see Directory  * @author Doug Cutting  */
end_comment

begin_class
DECL|class|FSDirectory
specifier|public
specifier|final
class|class
name|FSDirectory
extends|extends
name|Directory
block|{
comment|/** This cache of directories ensures that there is a unique Directory    * instance per path, so that synchronization on the Directory can be used to    * synchronize access between readers and writers.    *    * This should be a WeakHashMap, so that entries can be GC'd, but that would    * require Java 1.2.  Instead we use refcounts...    */
DECL|field|DIRECTORIES
specifier|private
specifier|static
specifier|final
name|Hashtable
name|DIRECTORIES
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|field|DISABLE_LOCKS
specifier|private
specifier|static
specifier|final
name|boolean
name|DISABLE_LOCKS
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"disableLuceneLocks"
argument_list|)
operator|||
name|Constants
operator|.
name|JAVA_1_1
decl_stmt|;
comment|/** A buffer optionally used in renameTo method */
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
literal|null
decl_stmt|;
comment|/** Returns the directory instance for the named location.    *    *<p>Directories are cached, so that, for a given canonical path, the same    * FSDirectory instance will always be returned.  This permits    * synchronization on directories.    *    * @param path the path to the directory.    * @param create if true, create, or erase any existing contents.    * @return the FSDirectory for the named file.  */
DECL|method|getDirectory
specifier|public
specifier|static
name|FSDirectory
name|getDirectory
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|,
name|create
argument_list|)
return|;
block|}
comment|/** Returns the directory instance for the named location.    *    *<p>Directories are cached, so that, for a given canonical path, the same    * FSDirectory instance will always be returned.  This permits    * synchronization on directories.    *    * @param file the path to the directory.    * @param create if true, create, or erase any existing contents.    * @return the FSDirectory for the named file.  */
DECL|method|getDirectory
specifier|public
specifier|static
name|FSDirectory
name|getDirectory
parameter_list|(
name|File
name|file
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|FSDirectory
name|dir
decl_stmt|;
synchronized|synchronized
init|(
name|DIRECTORIES
init|)
block|{
name|dir
operator|=
operator|(
name|FSDirectory
operator|)
name|DIRECTORIES
operator|.
name|get
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|dir
operator|=
operator|new
name|FSDirectory
argument_list|(
name|file
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|DIRECTORIES
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|create
condition|)
block|{
name|dir
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|dir
init|)
block|{
name|dir
operator|.
name|refCount
operator|++
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
DECL|field|directory
specifier|private
name|File
name|directory
init|=
literal|null
decl_stmt|;
DECL|field|refCount
specifier|private
name|int
name|refCount
decl_stmt|;
DECL|method|FSDirectory
specifier|private
name|FSDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|directory
operator|=
name|path
expr_stmt|;
if|if
condition|(
name|create
condition|)
name|create
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|directory
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
name|path
operator|+
literal|" not a directory"
argument_list|)
throw|;
block|}
DECL|method|create
specifier|private
specifier|synchronized
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|directory
operator|.
name|exists
argument_list|()
condition|)
if|if
condition|(
operator|!
name|directory
operator|.
name|mkdir
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory: "
operator|+
name|directory
argument_list|)
throw|;
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|list
argument_list|()
decl_stmt|;
comment|// clear old files
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"couldn't delete "
operator|+
name|files
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
comment|/** Returns an array of strings, one for each file in the directory. */
DECL|method|list
specifier|public
specifier|final
name|String
index|[]
name|list
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|directory
operator|.
name|list
argument_list|()
return|;
block|}
comment|/** Returns true iff a file with the given name exists. */
DECL|method|fileExists
specifier|public
specifier|final
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|exists
argument_list|()
return|;
block|}
comment|/** Returns the time the named file was last modified. */
DECL|method|fileModified
specifier|public
specifier|final
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|lastModified
argument_list|()
return|;
block|}
comment|/** Returns the time the named file was last modified. */
DECL|method|fileModified
specifier|public
specifier|static
specifier|final
name|long
name|fileModified
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|lastModified
argument_list|()
return|;
block|}
comment|/** Set the modified time of an existing file to now. */
DECL|method|touchFile
specifier|public
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|file
operator|.
name|setLastModified
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the length in bytes of a file in the directory. */
DECL|method|fileLength
specifier|public
specifier|final
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
comment|/** Removes an existing file in the directory. */
DECL|method|deleteFile
specifier|public
specifier|final
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"couldn't delete "
operator|+
name|name
argument_list|)
throw|;
block|}
comment|/** Renames an existing file in the directory. */
DECL|method|renameFile
specifier|public
specifier|final
specifier|synchronized
name|void
name|renameFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|old
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|from
argument_list|)
decl_stmt|;
name|File
name|nu
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|to
argument_list|)
decl_stmt|;
comment|/* This is not atomic.  If the program crashes between the call to        delete() and the call to renameTo() then we're screwed, but I've        been unable to figure out how else to do this... */
if|if
condition|(
name|nu
operator|.
name|exists
argument_list|()
condition|)
if|if
condition|(
operator|!
name|nu
operator|.
name|delete
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"couldn't delete "
operator|+
name|to
argument_list|)
throw|;
comment|// Rename the old file to the new one. Unfortunately, the renameTo()
comment|// method does not work reliably under some JVMs.  Therefore, if the
comment|// rename fails, we manually rename by copying the old file to the new one
if|if
condition|(
operator|!
name|old
operator|.
name|renameTo
argument_list|(
name|nu
argument_list|)
condition|)
block|{
name|java
operator|.
name|io
operator|.
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
name|java
operator|.
name|io
operator|.
name|OutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|nu
argument_list|)
expr_stmt|;
comment|// see if the buffer needs to be initialized. Initialization is
comment|// only done on-demand since many VM's will never run into the renameTo
comment|// bug and hence shouldn't waste 1K of mem for no reason.
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
literal|1024
index|]
expr_stmt|;
block|}
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|// delete the old file.
name|old
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"couldn't rename "
operator|+
name|from
operator|+
literal|" to "
operator|+
name|to
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"could not close input stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"could not close output stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
comment|/** Creates a new, empty file in the directory with the given name.       Returns a stream writing this file. */
DECL|method|createFile
specifier|public
specifier|final
name|OutputStream
name|createFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FSOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a stream reading an existing file. */
DECL|method|openFile
specifier|public
specifier|final
name|InputStream
name|openFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FSInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/** Constructs a {@link Lock} with the specified name.  Locks are implemented    * with {@link File#createNewFile() }.    *    *<p>In JDK 1.1 or if system property<I>disableLuceneLocks</I> is the    * string "true", locks are disabled.  Assigning this property any other    * string will<B>not</B> prevent creation of lock files.  This is useful for    * using Lucene on read-only medium, such as CD-ROM.    *    * @param name the name of the lock file    * @return an instance of<code>Lock</code> holding the lock    */
DECL|method|makeLock
specifier|public
specifier|final
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|Lock
argument_list|()
block|{
specifier|public
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|DISABLE_LOCKS
condition|)
return|return
literal|true
return|;
return|return
name|lockFile
operator|.
name|createNewFile
argument_list|()
return|;
block|}
specifier|public
name|void
name|release
parameter_list|()
block|{
if|if
condition|(
name|DISABLE_LOCKS
condition|)
return|return;
name|lockFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Lock@"
operator|+
name|lockFile
return|;
block|}
block|}
return|;
block|}
comment|/** Closes the store to future operations. */
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
if|if
condition|(
operator|--
name|refCount
operator|<=
literal|0
condition|)
block|{
synchronized|synchronized
init|(
name|DIRECTORIES
init|)
block|{
name|DIRECTORIES
operator|.
name|remove
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** For debug output. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FSDirectory@"
operator|+
name|directory
return|;
block|}
block|}
end_class

begin_class
DECL|class|FSInputStream
specifier|final
class|class
name|FSInputStream
extends|extends
name|InputStream
block|{
DECL|class|Descriptor
specifier|private
class|class
name|Descriptor
extends|extends
name|RandomAccessFile
block|{
DECL|field|position
specifier|public
name|long
name|position
decl_stmt|;
DECL|method|Descriptor
specifier|public
name|Descriptor
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|file
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|file
name|Descriptor
name|file
init|=
literal|null
decl_stmt|;
DECL|field|isClone
name|boolean
name|isClone
decl_stmt|;
DECL|method|FSInputStream
specifier|public
name|FSInputStream
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|=
operator|new
name|Descriptor
argument_list|(
name|path
argument_list|,
literal|"r"
argument_list|)
expr_stmt|;
name|length
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|/** InputStream methods */
DECL|method|readInternal
specifier|protected
specifier|final
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
synchronized|synchronized
init|(
name|file
init|)
block|{
name|long
name|position
init|=
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|position
operator|!=
name|file
operator|.
name|position
condition|)
block|{
name|file
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|file
operator|.
name|position
operator|=
name|position
expr_stmt|;
block|}
name|int
name|total
init|=
literal|0
decl_stmt|;
do|do
block|{
name|int
name|i
init|=
name|file
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|offset
operator|+
name|total
argument_list|,
name|len
operator|-
name|total
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
operator|-
literal|1
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
name|file
operator|.
name|position
operator|+=
name|i
expr_stmt|;
name|total
operator|+=
name|i
expr_stmt|;
block|}
do|while
condition|(
name|total
operator|<
name|len
condition|)
do|;
block|}
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
if|if
condition|(
operator|!
name|isClone
condition|)
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Random-access methods */
DECL|method|seekInternal
specifier|protected
specifier|final
name|void
name|seekInternal
parameter_list|(
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{   }
DECL|method|finalize
specifier|protected
specifier|final
name|void
name|finalize
parameter_list|()
throws|throws
name|IOException
block|{
name|close
argument_list|()
expr_stmt|;
comment|// close the file
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|FSInputStream
name|clone
init|=
operator|(
name|FSInputStream
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class

begin_class
DECL|class|FSOutputStream
specifier|final
class|class
name|FSOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|file
name|RandomAccessFile
name|file
init|=
literal|null
decl_stmt|;
DECL|method|FSOutputStream
specifier|public
name|FSOutputStream
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|path
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
block|}
comment|/** output methods: */
DECL|method|flushBuffer
specifier|public
specifier|final
name|void
name|flushBuffer
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Random-access methods */
DECL|method|seek
specifier|public
specifier|final
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
DECL|method|length
specifier|public
specifier|final
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|finalize
specifier|protected
specifier|final
name|void
name|finalize
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close the file
block|}
block|}
end_class

end_unit

