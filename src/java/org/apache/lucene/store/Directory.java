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

begin_comment
comment|/** A Directory is a flat list of files.  Files may be written once, when they  * are created.  Once a file is created it may only be opened for read, or  * deleted.  Random access is permitted both when reading and writing.  *  *<p> Java's i/o APIs not used directly, but rather all i/o is  * through this API.  This permits things such as:<ul>  *<li> implementation of RAM-based indices;  *<li> implementation indices stored in a database, via JDBC;  *<li> implementation of an index as a single file;  *</ul>  *  * @author Doug Cutting  */
end_comment

begin_class
DECL|class|Directory
specifier|public
specifier|abstract
class|class
name|Directory
block|{
comment|/** Returns an array of strings, one for each file in the directory. */
DECL|method|list
specifier|public
specifier|abstract
name|String
index|[]
name|list
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
comment|/** Returns the time the named file was last modified. */
DECL|method|fileModified
specifier|public
specifier|abstract
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Set the modified time of an existing file to now. */
DECL|method|touchFile
specifier|public
specifier|abstract
name|void
name|touchFile
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
comment|/** Renames an existing file in the directory.     If a file already exists with the new name, then it is replaced.     This replacement should be atomic. */
DECL|method|renameFile
specifier|public
specifier|abstract
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
function_decl|;
comment|/** Returns the length of a file in the directory. */
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
comment|/** @deprecated use {@link #createOutput(String)} */
DECL|method|createFile
specifier|public
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
operator|(
name|OutputStream
operator|)
name|createOutput
argument_list|(
name|name
argument_list|)
return|;
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
comment|// default implementation for back compatibility
comment|// this method should be abstract
return|return
operator|(
name|IndexOutput
operator|)
name|createFile
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** @deprecated use {@link #openInput(String)} */
DECL|method|openFile
specifier|public
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
operator|(
name|InputStream
operator|)
name|openInput
argument_list|(
name|name
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
comment|// default implementation for back compatibility
comment|// this method should be abstract
return|return
operator|(
name|IndexInput
operator|)
name|openFile
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Construct a {@link Lock}.    * @param name the name of the lock file    */
DECL|method|makeLock
specifier|public
specifier|abstract
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
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
block|}
end_class

end_unit

