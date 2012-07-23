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
name|IOException
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
name|util
operator|.
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * Calls check index on close.  */
end_comment

begin_comment
comment|// do NOT make any methods in this class synchronized, volatile
end_comment

begin_comment
comment|// do NOT import anything from the concurrency package.
end_comment

begin_comment
comment|// no randoms, no nothing.
end_comment

begin_class
DECL|class|BaseDirectoryWrapper
specifier|public
class|class
name|BaseDirectoryWrapper
extends|extends
name|Directory
block|{
comment|/** our in directory */
DECL|field|delegate
specifier|protected
specifier|final
name|Directory
name|delegate
decl_stmt|;
comment|/** best effort: base on in Directory is volatile */
DECL|field|open
specifier|protected
name|boolean
name|open
decl_stmt|;
DECL|field|checkIndexOnClose
specifier|private
name|boolean
name|checkIndexOnClose
init|=
literal|true
decl_stmt|;
DECL|field|crossCheckTermVectorsOnClose
specifier|private
name|boolean
name|crossCheckTermVectorsOnClose
init|=
literal|true
decl_stmt|;
DECL|method|BaseDirectoryWrapper
specifier|public
name|BaseDirectoryWrapper
parameter_list|(
name|Directory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
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
name|open
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|checkIndexOnClose
operator|&&
name|indexPossiblyExists
argument_list|()
condition|)
block|{
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|this
argument_list|,
name|crossCheckTermVectorsOnClose
argument_list|)
expr_stmt|;
block|}
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|isOpen
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|open
return|;
block|}
comment|/**     * don't rely upon DirectoryReader.fileExists to determine if we should    * checkIndex() or not. It might mask real problems, where we silently    * don't checkindex at all. instead we look for a segments file.    */
DECL|method|indexPossiblyExists
specifier|protected
name|boolean
name|indexPossiblyExists
parameter_list|()
block|{
name|String
name|files
index|[]
decl_stmt|;
try|try
block|{
name|files
operator|=
name|listAll
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// this means directory doesn't exist, which is ok. return false
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|f
range|:
name|files
control|)
block|{
if|if
condition|(
name|f
operator|.
name|startsWith
argument_list|(
literal|"segments_"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Set whether or not checkindex should be run    * on close    */
DECL|method|setCheckIndexOnClose
specifier|public
name|void
name|setCheckIndexOnClose
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|checkIndexOnClose
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getCheckIndexOnClose
specifier|public
name|boolean
name|getCheckIndexOnClose
parameter_list|()
block|{
return|return
name|checkIndexOnClose
return|;
block|}
DECL|method|setCrossCheckTermVectorsOnClose
specifier|public
name|void
name|setCrossCheckTermVectorsOnClose
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|crossCheckTermVectorsOnClose
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getCrossCheckTermVectorsOnClose
specifier|public
name|boolean
name|getCrossCheckTermVectorsOnClose
parameter_list|()
block|{
return|return
name|crossCheckTermVectorsOnClose
return|;
block|}
comment|// directory methods: delegate
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|listAll
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|delegate
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|delegate
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
return|return
name|delegate
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
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
block|{
return|return
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
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
block|{
name|delegate
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
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
block|{
return|return
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|delegate
operator|.
name|makeLock
argument_list|(
name|name
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
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|delegate
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLockFactory
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getLockFactory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getLockID
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
literal|"BaseDirectoryWrapper("
operator|+
name|delegate
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
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
name|delegate
operator|.
name|copy
argument_list|(
name|to
argument_list|,
name|src
argument_list|,
name|dest
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSlicer
specifier|public
name|IndexInputSlicer
name|createSlicer
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|createSlicer
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
end_class

end_unit

