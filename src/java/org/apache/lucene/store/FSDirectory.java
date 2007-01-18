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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
name|index
operator|.
name|IndexFileNameFilter
import|;
end_import

begin_comment
comment|// Used only for WRITE_LOCK_NAME in deprecated create=true case:
end_comment

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
name|IndexWriter
import|;
end_import

begin_comment
comment|/**  * Straightforward implementation of {@link Directory} as a directory of files.  * Locking implementation is by default the {@link SimpleFSLockFactory}, but  * can be changed either by passing in a {@link LockFactory} instance to  *<code>getDirectory</code>, or specifying the LockFactory class by setting  *<code>org.apache.lucene.store.FSDirectoryLockFactoryClass</code> Java system  * property, or by calling {@link #setLockFactory} after creating  * the Directory.   *<p>Directories are cached, so that, for a given canonical  * path, the same FSDirectory instance will always be  * returned by<code>getDirectory</code>.  This permits  * synchronization on directories.</p>  *  * @see Directory  * @author Doug Cutting  */
end_comment

begin_class
DECL|class|FSDirectory
specifier|public
class|class
name|FSDirectory
extends|extends
name|Directory
block|{
comment|/** This cache of directories ensures that there is a unique Directory    * instance per path, so that synchronization on the Directory can be used to    * synchronize access between readers and writers.  We use    * refcounts to ensure when the last use of an FSDirectory    * instance for a given canonical path is closed, we remove the    * instance from the cache.  See LUCENE-776    * for some relevant discussion.    */
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
DECL|field|disableLocks
specifier|private
specifier|static
name|boolean
name|disableLocks
init|=
literal|false
decl_stmt|;
comment|// TODO: should this move up to the Directory base class?  Also: should we
comment|// make a per-instance (in addition to the static "default") version?
comment|/**    * Set whether Lucene's use of lock files is disabled. By default,     * lock files are enabled. They should only be disabled if the index    * is on a read-only medium like a CD-ROM.    */
DECL|method|setDisableLocks
specifier|public
specifier|static
name|void
name|setDisableLocks
parameter_list|(
name|boolean
name|doDisableLocks
parameter_list|)
block|{
name|FSDirectory
operator|.
name|disableLocks
operator|=
name|doDisableLocks
expr_stmt|;
block|}
comment|/**    * Returns whether Lucene's use of lock files is disabled.    * @return true if locks are disabled, false if locks are enabled.    */
DECL|method|getDisableLocks
specifier|public
specifier|static
name|boolean
name|getDisableLocks
parameter_list|()
block|{
return|return
name|FSDirectory
operator|.
name|disableLocks
return|;
block|}
comment|/**    * Directory specified by<code>org.apache.lucene.lockDir</code>    * or<code>java.io.tmpdir</code> system property.     * @deprecated As of 2.1,<code>LOCK_DIR</code> is unused    * because the write.lock is now stored by default in the    * index directory.  If you really want to store locks    * elsewhere you can create your own {@link    * SimpleFSLockFactory} (or {@link NativeFSLockFactory},    * etc.) passing in your preferred lock directory.  Then,    * pass this<code>LockFactory</code> instance to one of    * the<code>getDirectory</code> methods that take a    *<code>lockFactory</code> (for example, {@link #getDirectory(String, LockFactory)}).    */
DECL|field|LOCK_DIR
specifier|public
specifier|static
specifier|final
name|String
name|LOCK_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.lockDir"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
comment|/** The default class which implements filesystem-based directories. */
DECL|field|IMPL
specifier|private
specifier|static
name|Class
name|IMPL
decl_stmt|;
static|static
block|{
try|try
block|{
name|String
name|name
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.FSDirectory.class"
argument_list|,
name|FSDirectory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|IMPL
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot load FSDirectory class: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
try|try
block|{
name|IMPL
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|FSDirectory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot load default FSDirectory class: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|DIGESTER
specifier|private
specifier|static
name|MessageDigest
name|DIGESTER
decl_stmt|;
static|static
block|{
try|try
block|{
name|DIGESTER
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** A buffer optionally used in renameTo method */
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
literal|null
decl_stmt|;
comment|/** Returns the directory instance for the named location.    * @param path the path to the directory.    * @return the FSDirectory for the named file.  */
DECL|method|getDirectory
specifier|public
specifier|static
name|FSDirectory
name|getDirectory
parameter_list|(
name|String
name|path
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
literal|null
argument_list|)
return|;
block|}
comment|/** Returns the directory instance for the named location.    * @param path the path to the directory.    * @param lockFactory instance of {@link LockFactory} providing the    *        locking implementation.    * @return the FSDirectory for the named file.  */
DECL|method|getDirectory
specifier|public
specifier|static
name|FSDirectory
name|getDirectory
parameter_list|(
name|String
name|path
parameter_list|,
name|LockFactory
name|lockFactory
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
name|lockFactory
argument_list|)
return|;
block|}
comment|/** Returns the directory instance for the named location.    * @param file the path to the directory.    * @return the FSDirectory for the named file.  */
DECL|method|getDirectory
specifier|public
specifier|static
name|FSDirectory
name|getDirectory
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
name|file
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Returns the directory instance for the named location.    * @param file the path to the directory.    * @param lockFactory instance of {@link LockFactory} providing the    *        locking implementation.    * @return the FSDirectory for the named file.  */
DECL|method|getDirectory
specifier|public
specifier|static
name|FSDirectory
name|getDirectory
parameter_list|(
name|File
name|file
parameter_list|,
name|LockFactory
name|lockFactory
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
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
name|file
operator|+
literal|" not a directory"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
if|if
condition|(
operator|!
name|file
operator|.
name|mkdirs
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory: "
operator|+
name|file
argument_list|)
throw|;
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
try|try
block|{
name|dir
operator|=
operator|(
name|FSDirectory
operator|)
name|IMPL
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot load FSDirectory class: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|dir
operator|.
name|init
argument_list|(
name|file
argument_list|,
name|lockFactory
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
else|else
block|{
comment|// Catch the case where a Directory is pulled from the cache, but has a
comment|// different LockFactory instance.
if|if
condition|(
name|lockFactory
operator|!=
literal|null
operator|&&
name|lockFactory
operator|!=
name|dir
operator|.
name|getLockFactory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Directory was previously created with a different LockFactory instance; please pass null as the lockFactory instance and use setLockFactory to change it"
argument_list|)
throw|;
block|}
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
comment|/** Returns the directory instance for the named location.    *    * @deprecated Use IndexWriter's create flag, instead, to    * create a new index.    *    * @param path the path to the directory.    * @param create if true, create, or erase any existing contents.    * @return the FSDirectory for the named file.  */
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
comment|/** Returns the directory instance for the named location.    *    * @deprecated Use IndexWriter's create flag, instead, to    * create a new index.    *    * @param file the path to the directory.    * @param create if true, create, or erase any existing contents.    * @return the FSDirectory for the named file.  */
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
name|FSDirectory
name|dir
init|=
name|getDirectory
argument_list|(
name|file
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// This is now deprecated (creation should only be done
comment|// by IndexWriter):
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
return|return
name|dir
return|;
block|}
DECL|method|create
specifier|private
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|directory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|list
argument_list|(
name|IndexFileNameFilter
operator|.
name|getFilter
argument_list|()
argument_list|)
decl_stmt|;
comment|// clear old files
if|if
condition|(
name|files
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read directory "
operator|+
name|directory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
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
literal|"Cannot delete "
operator|+
name|file
argument_list|)
throw|;
block|}
block|}
name|lockFactory
operator|.
name|clearLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
expr_stmt|;
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
specifier|protected
name|FSDirectory
parameter_list|()
block|{}
empty_stmt|;
comment|// permit subclassing
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Set up lockFactory with cascaded defaults: if an instance was passed in,
comment|// use that; else if locks are disabled, use NoLockFactory; else if the
comment|// system property org.apache.lucene.store.FSDirectoryLockFactoryClass is set,
comment|// instantiate that; else, use SimpleFSLockFactory:
name|directory
operator|=
name|path
expr_stmt|;
name|boolean
name|doClearLockID
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|lockFactory
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|disableLocks
condition|)
block|{
comment|// Locks are disabled:
name|lockFactory
operator|=
name|NoLockFactory
operator|.
name|getNoLockFactory
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|lockClassName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.store.FSDirectoryLockFactoryClass"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockClassName
operator|!=
literal|null
operator|&&
operator|!
name|lockClassName
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|Class
name|c
decl_stmt|;
try|try
block|{
name|c
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|lockClassName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to find LockClass "
operator|+
name|lockClassName
argument_list|)
throw|;
block|}
try|try
block|{
name|lockFactory
operator|=
operator|(
name|LockFactory
operator|)
name|c
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"IllegalAccessException when instantiating LockClass "
operator|+
name|lockClassName
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"InstantiationException when instantiating LockClass "
operator|+
name|lockClassName
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to cast LockClass "
operator|+
name|lockClassName
operator|+
literal|" instance to a LockFactory"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// Our default lock is SimpleFSLockFactory;
comment|// default lockDir is our index directory:
name|lockFactory
operator|=
operator|new
name|SimpleFSLockFactory
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|doClearLockID
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
if|if
condition|(
name|doClearLockID
condition|)
block|{
comment|// Clear the prefix because write.lock will be
comment|// stored in our directory:
name|lockFactory
operator|.
name|setLockPrefix
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns an array of strings, one for each Lucene index file in the directory. */
DECL|method|list
specifier|public
name|String
index|[]
name|list
parameter_list|()
block|{
return|return
name|directory
operator|.
name|list
argument_list|(
name|IndexFileNameFilter
operator|.
name|getFilter
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns true iff a file with the given name exists. */
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
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
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
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
name|long
name|fileModified
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
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
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
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
literal|"Cannot delete "
operator|+
name|file
argument_list|)
throw|;
block|}
comment|/** Renames an existing file in the directory. */
DECL|method|renameFile
specifier|public
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
literal|"Cannot delete "
operator|+
name|nu
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
name|IOException
name|newExc
init|=
operator|new
name|IOException
argument_list|(
literal|"Cannot rename "
operator|+
name|old
operator|+
literal|" to "
operator|+
name|nu
argument_list|)
decl_stmt|;
name|newExc
operator|.
name|initCause
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
throw|throw
name|newExc
throw|;
block|}
finally|finally
block|{
try|try
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
literal|"Cannot close input stream: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
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
literal|"Cannot close output stream: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/** Creates a new, empty file in the directory with the given name.       Returns a stream writing this file. */
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
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
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
comment|// delete existing, if any
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot overwrite: "
operator|+
name|file
argument_list|)
throw|;
return|return
operator|new
name|FSIndexOutput
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/** Returns a stream reading an existing file. */
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FSIndexInput
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
comment|/**    * So we can do some byte-to-hexchar conversion below    */
DECL|field|HEX_DIGITS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|HEX_DIGITS
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|}
decl_stmt|;
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
name|String
name|dirName
decl_stmt|;
comment|// name to be hashed
try|try
block|{
name|dirName
operator|=
name|directory
operator|.
name|getCanonicalPath
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
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|byte
name|digest
index|[]
decl_stmt|;
synchronized|synchronized
init|(
name|DIGESTER
init|)
block|{
name|digest
operator|=
name|DIGESTER
operator|.
name|digest
argument_list|(
name|dirName
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"lucene-"
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
name|digest
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|digest
index|[
name|i
index|]
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
operator|(
name|b
operator|>>
literal|4
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
name|b
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Closes the store to future operations. */
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
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
DECL|method|getFile
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
comment|/** For debug output. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"@"
operator|+
name|directory
return|;
block|}
block|}
end_class

begin_class
DECL|class|FSIndexInput
class|class
name|FSIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|class|Descriptor
specifier|private
specifier|static
class|class
name|Descriptor
extends|extends
name|RandomAccessFile
block|{
comment|// remember if the file is open, so that we don't try to close it
comment|// more than once
DECL|field|isOpen
specifier|private
name|boolean
name|isOpen
decl_stmt|;
DECL|field|position
name|long
name|position
decl_stmt|;
DECL|field|length
specifier|final
name|long
name|length
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
name|isOpen
operator|=
literal|true
expr_stmt|;
name|length
operator|=
name|length
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isOpen
condition|)
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|file
specifier|private
specifier|final
name|Descriptor
name|file
decl_stmt|;
DECL|field|isClone
name|boolean
name|isClone
decl_stmt|;
DECL|method|FSIndexInput
specifier|public
name|FSIndexInput
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
block|}
comment|/** IndexInput methods */
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
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// only close the file if this is not a clone
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
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|position
parameter_list|)
block|{   }
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|FSIndexInput
name|clone
init|=
operator|(
name|FSIndexInput
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
comment|/** Method used for testing. Returns true if the underlying    *  file descriptor is valid.    */
DECL|method|isFDValid
name|boolean
name|isFDValid
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|valid
argument_list|()
return|;
block|}
block|}
end_class

begin_class
DECL|class|FSIndexOutput
class|class
name|FSIndexOutput
extends|extends
name|BufferedIndexOutput
block|{
DECL|field|file
name|RandomAccessFile
name|file
init|=
literal|null
decl_stmt|;
comment|// remember if the file is open, so that we don't try to close it
comment|// more than once
DECL|field|isOpen
specifier|private
name|boolean
name|isOpen
decl_stmt|;
DECL|method|FSIndexOutput
specifier|public
name|FSIndexOutput
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
name|isOpen
operator|=
literal|true
expr_stmt|;
block|}
comment|/** output methods: */
DECL|method|flushBuffer
specifier|public
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
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// only close the file if it has not been closed yet
if|if
condition|(
name|isOpen
condition|)
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
name|isOpen
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/** Random-access methods */
DECL|method|seek
specifier|public
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
block|}
end_class

end_unit

