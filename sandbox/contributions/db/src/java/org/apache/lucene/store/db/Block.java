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

begin_comment
comment|/**  * @author Andi Vajda  */
end_comment

begin_class
DECL|class|Block
specifier|public
class|class
name|Block
extends|extends
name|Object
block|{
DECL|field|key
DECL|field|data
specifier|protected
name|DatabaseEntry
name|key
decl_stmt|,
name|data
decl_stmt|;
DECL|method|Block
specifier|protected
name|Block
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|fileKey
init|=
name|file
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|key
operator|=
operator|new
name|DatabaseEntry
argument_list|(
operator|new
name|byte
index|[
name|fileKey
operator|.
name|length
operator|+
literal|8
index|]
argument_list|)
expr_stmt|;
name|key
operator|.
name|setUserBuffer
argument_list|(
name|fileKey
operator|.
name|length
operator|+
literal|8
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|DatabaseEntry
argument_list|(
operator|new
name|byte
index|[
name|DbOutputStream
operator|.
name|BLOCK_LEN
index|]
argument_list|)
expr_stmt|;
name|data
operator|.
name|setUserBuffer
argument_list|(
name|data
operator|.
name|getSize
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|fileKey
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|fileKey
operator|.
name|length
argument_list|)
expr_stmt|;
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|getKey
specifier|protected
name|byte
index|[]
name|getKey
parameter_list|()
block|{
return|return
name|key
operator|.
name|getData
argument_list|()
return|;
block|}
DECL|method|getData
specifier|protected
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|data
operator|.
name|getData
argument_list|()
return|;
block|}
DECL|method|seek
specifier|protected
name|void
name|seek
parameter_list|(
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|key
operator|.
name|getData
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|data
operator|.
name|length
operator|-
literal|8
decl_stmt|;
name|position
operator|>>>=
name|DbOutputStream
operator|.
name|BLOCK_SHIFT
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|56
operator|)
argument_list|)
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|48
operator|)
argument_list|)
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|40
operator|)
argument_list|)
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|24
operator|)
argument_list|)
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|5
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|16
operator|)
argument_list|)
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|8
operator|)
argument_list|)
expr_stmt|;
name|data
index|[
name|index
operator|+
literal|7
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
name|position
operator|>>>
literal|0
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|protected
name|void
name|get
parameter_list|(
name|Db
name|blocks
parameter_list|,
name|DbTxn
name|txn
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|blocks
operator|.
name|get
argument_list|(
name|txn
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
name|flags
argument_list|)
expr_stmt|;
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
block|}
DECL|method|put
specifier|protected
name|void
name|put
parameter_list|(
name|Db
name|blocks
parameter_list|,
name|DbTxn
name|txn
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|blocks
operator|.
name|put
argument_list|(
name|txn
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class

end_unit

