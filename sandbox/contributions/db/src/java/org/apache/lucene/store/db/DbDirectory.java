begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.db
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|db
package|;
end_package

begin_comment
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|store
operator|.
name|OutputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|DbEnv
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|Db
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|DbConstants
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DatabaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|Dbc
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|DbTxn
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DatabaseException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DbHandleExtractor
import|;
end_import

begin_comment
comment|/**  * A DbDirectory is a Berkeley DB 4.3 based implementation of   * {@link org.apache.lucene.store.Directory Directory}. It uses two  * {@link com.sleepycat.db.internal.Db Db} database handles, one for storing file  * records and another for storing file data blocks.  *  * @author Andi Vajda  */
end_comment

begin_class
DECL|class|DbDirectory
specifier|public
class|class
name|DbDirectory
extends|extends
name|Directory
block|{
DECL|field|files
DECL|field|blocks
specifier|protected
name|Db
name|files
decl_stmt|,
name|blocks
decl_stmt|;
DECL|field|txn
specifier|protected
name|DbTxn
name|txn
decl_stmt|;
DECL|field|flags
specifier|protected
name|int
name|flags
decl_stmt|;
comment|/**      * Instantiate a DbDirectory. The same threading rules that apply to      * Berkeley DB handles apply to instances of DbDirectory.      *      * @param txn a transaction handle that is going to be used for all db      * operations done by this instance. This parameter may be      *<code>null</code>.      * @param files a db handle to store file records.      * @param blocks a db handle to store file data blocks.      * @param flags flags used for db read operations.      */
DECL|method|DbDirectory
specifier|public
name|DbDirectory
parameter_list|(
name|DbTxn
name|txn
parameter_list|,
name|Db
name|files
parameter_list|,
name|Db
name|blocks
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|txn
operator|=
name|txn
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
DECL|method|DbDirectory
specifier|public
name|DbDirectory
parameter_list|(
name|Transaction
name|txn
parameter_list|,
name|Database
name|files
parameter_list|,
name|Database
name|blocks
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|txn
operator|=
name|txn
operator|!=
literal|null
condition|?
name|DbHandleExtractor
operator|.
name|getDbTxn
argument_list|(
name|txn
argument_list|)
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|DbHandleExtractor
operator|.
name|getDb
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|DbHandleExtractor
operator|.
name|getDb
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
DECL|method|DbDirectory
specifier|public
name|DbDirectory
parameter_list|(
name|Transaction
name|txn
parameter_list|,
name|Database
name|files
parameter_list|,
name|Database
name|blocks
parameter_list|)
block|{
name|this
argument_list|(
name|txn
argument_list|,
name|files
argument_list|,
name|blocks
argument_list|,
literal|0
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
block|{     }
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
operator|new
name|DbOutputStream
argument_list|(
name|files
argument_list|,
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|,
name|name
argument_list|,
literal|true
argument_list|)
return|;
block|}
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
operator|new
name|File
argument_list|(
name|name
argument_list|)
operator|.
name|delete
argument_list|(
name|files
argument_list|,
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|File
argument_list|(
name|name
argument_list|)
operator|.
name|exists
argument_list|(
name|files
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
return|;
block|}
DECL|method|fileLength
specifier|public
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
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|(
name|files
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
condition|)
return|return
name|file
operator|.
name|getLength
argument_list|()
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|name
argument_list|)
throw|;
block|}
DECL|method|fileModified
specifier|public
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
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|(
name|files
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
condition|)
return|return
name|file
operator|.
name|getTimeModified
argument_list|()
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|name
argument_list|)
throw|;
block|}
DECL|method|list
specifier|public
name|String
index|[]
name|list
parameter_list|()
throws|throws
name|IOException
block|{
name|Dbc
name|cursor
init|=
literal|null
decl_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|data
operator|.
name|setPartial
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cursor
operator|=
name|files
operator|.
name|cursor
argument_list|(
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
if|if
condition|(
name|cursor
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|data
argument_list|,
name|DbConstants
operator|.
name|DB_SET_RANGE
operator||
name|flags
argument_list|)
operator|!=
name|DbConstants
operator|.
name|DB_NOTFOUND
condition|)
block|{
name|ByteArrayInputStream
name|buffer
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|key
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
while|while
condition|(
name|cursor
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|data
argument_list|,
name|DbConstants
operator|.
name|DB_NEXT
operator||
name|flags
argument_list|)
operator|!=
name|DbConstants
operator|.
name|DB_NOTFOUND
condition|)
block|{
name|buffer
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|key
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|name
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cursor
operator|!=
literal|null
condition|)
name|cursor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|(
name|String
index|[]
operator|)
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
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
operator|new
name|DbInputStream
argument_list|(
name|files
argument_list|,
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|,
name|name
argument_list|)
return|;
block|}
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
operator|new
name|DbLock
argument_list|()
return|;
block|}
DECL|method|renameFile
specifier|public
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
operator|new
name|File
argument_list|(
name|from
argument_list|)
operator|.
name|rename
argument_list|(
name|files
argument_list|,
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
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
name|name
argument_list|)
decl_stmt|;
name|long
name|length
init|=
literal|0L
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|(
name|files
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
condition|)
name|length
operator|=
name|file
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|file
operator|.
name|modify
argument_list|(
name|files
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|,
name|length
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

