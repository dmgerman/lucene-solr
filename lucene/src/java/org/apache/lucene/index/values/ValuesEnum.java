begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
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
name|Attribute
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
name|AttributeSource
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
name|BytesRef
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
name|FloatsRef
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
name|LongsRef
import|;
end_import

begin_comment
comment|/**  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|ValuesEnum
specifier|public
specifier|abstract
class|class
name|ValuesEnum
extends|extends
name|DocIdSetIterator
block|{
DECL|field|source
specifier|private
name|AttributeSource
name|source
decl_stmt|;
DECL|field|enumType
specifier|private
name|Values
name|enumType
decl_stmt|;
DECL|field|bytesRef
specifier|protected
name|BytesRef
name|bytesRef
decl_stmt|;
DECL|field|floatsRef
specifier|protected
name|FloatsRef
name|floatsRef
decl_stmt|;
DECL|field|intsRef
specifier|protected
name|LongsRef
name|intsRef
decl_stmt|;
DECL|method|ValuesEnum
specifier|protected
name|ValuesEnum
parameter_list|(
name|Values
name|enumType
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|enumType
argument_list|)
expr_stmt|;
block|}
DECL|method|ValuesEnum
specifier|protected
name|ValuesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Values
name|enumType
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|enumType
operator|=
name|enumType
expr_stmt|;
switch|switch
condition|(
name|enumType
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|bytesRef
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
break|break;
case|case
name|PACKED_INTS
case|:
name|intsRef
operator|=
operator|new
name|LongsRef
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIMPLE_FLOAT_4BYTE
case|:
case|case
name|SIMPLE_FLOAT_8BYTE
case|:
name|floatsRef
operator|=
operator|new
name|FloatsRef
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|enumType
return|;
block|}
DECL|method|bytes
specifier|public
name|BytesRef
name|bytes
parameter_list|()
block|{
return|return
name|bytesRef
return|;
block|}
DECL|method|getFloat
specifier|public
name|FloatsRef
name|getFloat
parameter_list|()
block|{
return|return
name|floatsRef
return|;
block|}
DECL|method|getInt
specifier|public
name|LongsRef
name|getInt
parameter_list|()
block|{
return|return
name|intsRef
return|;
block|}
DECL|method|copyReferences
specifier|protected
name|void
name|copyReferences
parameter_list|(
name|ValuesEnum
name|valuesEnum
parameter_list|)
block|{
name|intsRef
operator|=
name|valuesEnum
operator|.
name|intsRef
expr_stmt|;
name|floatsRef
operator|=
name|valuesEnum
operator|.
name|floatsRef
expr_stmt|;
name|bytesRef
operator|=
name|valuesEnum
operator|.
name|bytesRef
expr_stmt|;
block|}
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
name|source
operator|=
operator|new
name|AttributeSource
argument_list|()
expr_stmt|;
return|return
name|source
return|;
block|}
DECL|method|addAttribute
specifier|public
parameter_list|<
name|T
extends|extends
name|Attribute
parameter_list|>
name|T
name|addAttribute
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|attr
parameter_list|)
block|{
return|return
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|attr
argument_list|)
return|;
block|}
DECL|method|getAttribute
specifier|public
parameter_list|<
name|T
extends|extends
name|Attribute
parameter_list|>
name|T
name|getAttribute
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|attr
parameter_list|)
block|{
return|return
name|attributes
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|attr
argument_list|)
return|;
block|}
DECL|method|hasAttribute
specifier|public
parameter_list|<
name|T
extends|extends
name|Attribute
parameter_list|>
name|boolean
name|hasAttribute
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|attr
parameter_list|)
block|{
return|return
name|attributes
argument_list|()
operator|.
name|hasAttribute
argument_list|(
name|attr
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|emptyEnum
specifier|public
specifier|static
name|ValuesEnum
name|emptyEnum
parameter_list|(
name|Values
name|type
parameter_list|)
block|{
return|return
operator|new
name|ValuesEnum
argument_list|(
name|type
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{                }
block|}
return|;
block|}
block|}
end_class

end_unit

