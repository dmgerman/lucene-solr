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

begin_import
import|import
name|gnu
operator|.
name|gcj
operator|.
name|RawData
import|;
end_import

begin_comment
comment|/** Native file-based {@link IndexInput} implementation, using GCJ.  *  * @author Doug Cutting  */
end_comment

begin_class
DECL|class|GCJIndexInput
specifier|public
class|class
name|GCJIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|file
specifier|private
name|String
name|file
decl_stmt|;
DECL|field|fd
specifier|private
name|int
name|fd
decl_stmt|;
DECL|field|fileLength
specifier|private
name|long
name|fileLength
decl_stmt|;
DECL|field|data
specifier|public
name|RawData
name|data
decl_stmt|;
DECL|field|pointer
specifier|public
name|RawData
name|pointer
decl_stmt|;
DECL|field|isClone
specifier|private
name|boolean
name|isClone
decl_stmt|;
DECL|method|GCJIndexInput
specifier|public
name|GCJIndexInput
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|open
argument_list|()
expr_stmt|;
block|}
DECL|method|open
specifier|private
specifier|native
name|void
name|open
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|readByte
specifier|public
specifier|native
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|readBytes
specifier|public
specifier|native
name|void
name|readBytes
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
function_decl|;
DECL|method|readVInt
specifier|public
specifier|native
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getFilePointer
specifier|public
specifier|native
name|long
name|getFilePointer
parameter_list|()
function_decl|;
DECL|method|seek
specifier|public
specifier|native
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|fileLength
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|GCJIndexInput
name|clone
init|=
operator|(
name|GCJIndexInput
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
operator|!
name|isClone
condition|)
name|doClose
argument_list|()
expr_stmt|;
block|}
DECL|method|doClose
specifier|private
specifier|native
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

