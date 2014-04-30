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
name|File
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

begin_comment
comment|/**  *<p>Implements {@link LockFactory} using {@link  * File#createNewFile()}.</p>  *  *<p><b>NOTE:</b> the {@linkplain File#createNewFile() javadocs  * for<code>File.createNewFile()</code>} contain a vague  * yet spooky warning about not using the API for file  * locking.  This warning was added due to<a target="_top"  * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4676183">this  * bug</a>, and in fact the only known problem with using  * this API for locking is that the Lucene write lock may  * not be released when the JVM exits abnormally.</p>   *<p>When this happens, a {@link LockObtainFailedException}  * is hit when trying to create a writer, in which case you  * need to explicitly clear the lock file first.  You can  * either manually remove the file, or use the {@link  * org.apache.lucene.index.IndexWriter#unlock(Directory)}  * API.  But, first be certain that no writer is in fact  * writing to the index otherwise you can easily corrupt  * your index.</p>  *  *<p>Special care needs to be taken if you change the locking  * implementation: First be certain that no writer is in fact  * writing to the index otherwise you can easily corrupt  * your index. Be sure to do the LockFactory change all Lucene  * instances and clean up all leftover lock files before starting  * the new configuration for the first time. Different implementations  * can not work together!</p>  *  *<p>If you suspect that this or any other LockFactory is  * not working properly in your environment, you can easily  * test it by using {@link VerifyingLockFactory}, {@link  * LockVerifyServer} and {@link LockStressTest}.</p>  *  * @see LockFactory  */
end_comment

begin_class
DECL|class|SimpleFSLockFactory
specifier|public
class|class
name|SimpleFSLockFactory
extends|extends
name|FSLockFactory
block|{
comment|/**    * Create a SimpleFSLockFactory instance, with null (unset)    * lock directory. When you pass this factory to a {@link FSDirectory}    * subclass, the lock directory is automatically set to the    * directory itself. Be sure to create one instance for each directory    * your create!    */
DECL|method|SimpleFSLockFactory
specifier|public
name|SimpleFSLockFactory
parameter_list|()
block|{
name|this
argument_list|(
operator|(
name|File
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiate using the provided directory (as a File instance).    * @param lockDir where lock files should be created.    */
DECL|method|SimpleFSLockFactory
specifier|public
name|SimpleFSLockFactory
parameter_list|(
name|File
name|lockDir
parameter_list|)
block|{
name|setLockDir
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiate using the provided directory name (String).    * @param lockDirName where lock files should be created.    */
DECL|method|SimpleFSLockFactory
specifier|public
name|SimpleFSLockFactory
parameter_list|(
name|String
name|lockDirName
parameter_list|)
block|{
name|setLockDir
argument_list|(
operator|new
name|File
argument_list|(
name|lockDirName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
block|{
if|if
condition|(
name|lockPrefix
operator|!=
literal|null
condition|)
block|{
name|lockName
operator|=
name|lockPrefix
operator|+
literal|"-"
operator|+
name|lockName
expr_stmt|;
block|}
return|return
operator|new
name|SimpleFSLock
argument_list|(
name|lockDir
argument_list|,
name|lockName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lockDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|lockPrefix
operator|!=
literal|null
condition|)
block|{
name|lockName
operator|=
name|lockPrefix
operator|+
literal|"-"
operator|+
name|lockName
expr_stmt|;
block|}
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|lockDir
argument_list|,
name|lockName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockFile
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|lockFile
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot delete "
operator|+
name|lockFile
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_class
DECL|class|SimpleFSLock
class|class
name|SimpleFSLock
extends|extends
name|Lock
block|{
DECL|field|lockFile
name|File
name|lockFile
decl_stmt|;
DECL|field|lockDir
name|File
name|lockDir
decl_stmt|;
DECL|method|SimpleFSLock
specifier|public
name|SimpleFSLock
parameter_list|(
name|File
name|lockDir
parameter_list|,
name|String
name|lockFileName
parameter_list|)
block|{
name|this
operator|.
name|lockDir
operator|=
name|lockDir
expr_stmt|;
name|lockFile
operator|=
operator|new
name|File
argument_list|(
name|lockDir
argument_list|,
name|lockFileName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Ensure that lockDir exists and is a directory:
if|if
condition|(
operator|!
name|lockDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|lockDir
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
name|lockDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|lockDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// TODO: NoSuchDirectoryException instead?
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Found regular file where directory expected: "
operator|+
name|lockDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|lockFile
operator|.
name|createNewFile
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// On Windows, on concurrent createNewFile, the 2nd process gets "access denied".
comment|// In that case, the lock was not aquired successfully, so return false.
comment|// We record the failure reason here; the obtain with timeout (usually the
comment|// one calling us) will use this as "root cause" if it fails to get the lock.
name|failureReason
operator|=
name|ioe
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|LockReleaseFailedException
block|{
if|if
condition|(
name|lockFile
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|lockFile
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|LockReleaseFailedException
argument_list|(
literal|"failed to delete "
operator|+
name|lockFile
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|lockFile
operator|.
name|exists
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
literal|"SimpleFSLock@"
operator|+
name|lockFile
return|;
block|}
block|}
end_class

end_unit

