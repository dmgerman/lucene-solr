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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
comment|/**  * Native {@link Directory} implementation for Microsoft Windows.  *<p>  * Steps:  *<ol>   *<li>Compile the source code to create WindowsDirectory.dll:  *<blockquote>  * c:\mingw\bin\g++ -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at   * -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -static-libgcc   * -static-libstdc++ -shared WindowsDirectory.cpp -o WindowsDirectory.dll  *</blockquote>   *       For 64-bit JREs, use mingw64, with the -m64 option.   *<li>Put WindowsDirectory.dll into some directory in your windows PATH  *<li>Open indexes with WindowsDirectory and use it.  *</p>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|WindowsDirectory
specifier|public
class|class
name|WindowsDirectory
extends|extends
name|FSDirectory
block|{
DECL|field|DEFAULT_BUFFERSIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFERSIZE
init|=
literal|4096
decl_stmt|;
comment|/* default pgsize on ia32/amd64 */
static|static
block|{
name|System
operator|.
name|loadLibrary
argument_list|(
literal|"WindowsDirectory"
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new WindowsDirectory for the named location.    *     * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException    */
DECL|method|WindowsDirectory
specifier|public
name|WindowsDirectory
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
name|super
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new WindowsDirectory for the named location and {@link NativeFSLockFactory}.    *    * @param path the path of the directory    * @throws IOException    */
DECL|method|WindowsDirectory
specifier|public
name|WindowsDirectory
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|WindowsIndexInput
argument_list|(
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|bufferSize
argument_list|,
name|DEFAULT_BUFFERSIZE
argument_list|)
argument_list|)
return|;
block|}
DECL|class|WindowsIndexInput
specifier|protected
specifier|static
class|class
name|WindowsIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|field|fd
specifier|private
specifier|final
name|long
name|fd
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|isClone
name|boolean
name|isClone
decl_stmt|;
DECL|field|isOpen
name|boolean
name|isOpen
decl_stmt|;
DECL|method|WindowsIndexInput
specifier|public
name|WindowsIndexInput
parameter_list|(
name|File
name|file
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|fd
operator|=
name|WindowsDirectory
operator|.
name|open
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|length
operator|=
name|WindowsDirectory
operator|.
name|length
argument_list|(
name|fd
argument_list|)
expr_stmt|;
name|isOpen
operator|=
literal|true
expr_stmt|;
block|}
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
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|WindowsDirectory
operator|.
name|read
argument_list|(
name|fd
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|getFilePointer
argument_list|()
argument_list|)
operator|!=
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Read past EOF"
argument_list|)
throw|;
block|}
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{     }
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: we synchronize and track "isOpen" because Lucene sometimes closes IIs twice!
if|if
condition|(
operator|!
name|isClone
operator|&&
name|isOpen
condition|)
block|{
name|WindowsDirectory
operator|.
name|close
argument_list|(
name|fd
argument_list|)
expr_stmt|;
name|isOpen
operator|=
literal|false
expr_stmt|;
block|}
block|}
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
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|WindowsIndexInput
name|clone
init|=
operator|(
name|WindowsIndexInput
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
comment|/** Opens a handle to a file. */
DECL|method|open
specifier|private
specifier|static
specifier|native
name|long
name|open
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Reads data from a file at pos into bytes */
DECL|method|read
specifier|private
specifier|static
specifier|native
name|int
name|read
parameter_list|(
name|long
name|fd
parameter_list|,
name|byte
name|bytes
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Closes a handle to a file */
DECL|method|close
specifier|private
specifier|static
specifier|native
name|void
name|close
parameter_list|(
name|long
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the length of a file */
DECL|method|length
specifier|private
specifier|static
specifier|native
name|long
name|length
parameter_list|(
name|long
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

