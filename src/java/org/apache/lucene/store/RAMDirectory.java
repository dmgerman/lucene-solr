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
name|IOException
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
name|Serializable
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
name|Set
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
name|util
operator|.
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/**  * A memory-resident {@link Directory} implementation.  Locking  * implementation is by default the {@link SingleInstanceLockFactory}  * but can be changed with {@link #setLockFactory}.  */
end_comment

begin_class
DECL|class|RAMDirectory
specifier|public
class|class
name|RAMDirectory
extends|extends
name|Directory
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1l
decl_stmt|;
DECL|field|fileMap
name|HashMap
argument_list|<
name|String
argument_list|,
name|RAMFile
argument_list|>
name|fileMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RAMFile
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|sizeInBytes
specifier|final
name|AtomicLong
name|sizeInBytes
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// *****
comment|// Lock acquisition sequence:  RAMDirectory, then RAMFile
comment|// *****
comment|/** Constructs an empty {@link Directory}. */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|()
block|{
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new<code>RAMDirectory</code> instance from a different    *<code>Directory</code> implementation.  This can be used to load    * a disk-based index into memory.    *<P>    * This should be used only with indices that can fit into memory.    *<P>    * Note that the resulting<code>RAMDirectory</code> instance is fully    * independent from the original<code>Directory</code> (it is a    * complete copy).  Any subsequent changes to the    * original<code>Directory</code> will not be visible in the    *<code>RAMDirectory</code> instance.    *    * @param dir a<code>Directory</code> value    * @exception IOException if an error occurs    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|RAMDirectory
specifier|private
name|RAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|boolean
name|closeDir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|()
expr_stmt|;
name|Directory
operator|.
name|copy
argument_list|(
name|dir
argument_list|,
name|this
argument_list|,
name|closeDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
specifier|synchronized
specifier|final
name|String
index|[]
name|listAll
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fileNames
init|=
name|fileMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|fileNames
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fileName
range|:
name|fileNames
control|)
name|result
index|[
name|i
operator|++
index|]
operator|=
name|fileName
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Returns true iff the named file exists in this directory. */
annotation|@
name|Override
DECL|method|fileExists
specifier|public
specifier|final
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|file
operator|!=
literal|null
return|;
block|}
comment|/** Returns the time the named file was last modified.    * @throws IOException if the file does not exist    */
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|file
operator|.
name|getLastModified
argument_list|()
return|;
block|}
comment|/** Set the modified time of an existing file to now.    * @throws IOException if the file does not exist    */
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
name|long
name|ts2
decl_stmt|,
name|ts1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
do|do
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|ts2
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|ts1
operator|==
name|ts2
condition|)
do|;
name|file
operator|.
name|setLastModified
argument_list|(
name|ts2
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the length in bytes of a file in the directory.    * @throws IOException if the file does not exist    */
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|file
operator|.
name|getLength
argument_list|()
return|;
block|}
comment|/** Return total size in bytes of all files in this    * directory.  This is currently quantized to    * RAMOutputStream.BUFFER_SIZE. */
DECL|method|sizeInBytes
specifier|public
specifier|synchronized
specifier|final
name|long
name|sizeInBytes
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|sizeInBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Removes an existing file in the directory.    * @throws IOException if the file does not exist    */
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
specifier|synchronized
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|fileMap
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|file
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
name|sizeInBytes
operator|.
name|addAndGet
argument_list|(
operator|-
name|file
operator|.
name|sizeInBytes
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
comment|/** Creates a new, empty file in the directory with the given name. Returns a stream writing this file. */
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|(
name|this
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|RAMFile
name|existing
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|.
name|addAndGet
argument_list|(
operator|-
name|existing
operator|.
name|sizeInBytes
argument_list|)
expr_stmt|;
name|existing
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
block|}
name|fileMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/** Returns a stream reading an existing file. */
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
operator|new
name|RAMInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/** Closes the store to future operations, releasing associated memory. */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|fileMap
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

