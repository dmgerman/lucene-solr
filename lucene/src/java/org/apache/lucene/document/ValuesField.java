begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|analysis
operator|.
name|TokenStream
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
name|document
operator|.
name|Field
operator|.
name|Index
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
name|document
operator|.
name|Field
operator|.
name|Store
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
name|document
operator|.
name|Field
operator|.
name|TermVector
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
name|values
operator|.
name|PerDocFieldValues
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
name|values
operator|.
name|Values
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

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|ValuesField
specifier|public
class|class
name|ValuesField
extends|extends
name|AbstractField
implements|implements
name|PerDocFieldValues
block|{
DECL|field|bytes
specifier|protected
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|doubleValue
specifier|protected
name|double
name|doubleValue
decl_stmt|;
DECL|field|longValue
specifier|protected
name|long
name|longValue
decl_stmt|;
DECL|field|type
specifier|protected
name|Values
name|type
decl_stmt|;
DECL|field|bytesComparator
specifier|protected
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|bytesComparator
decl_stmt|;
DECL|method|ValuesField
specifier|public
name|ValuesField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|NO
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
name|setDocValues
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|ValuesField
name|ValuesField
parameter_list|()
block|{
name|this
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|type
operator|=
name|Values
operator|.
name|PACKED_INTS
expr_stmt|;
name|longValue
operator|=
name|value
expr_stmt|;
block|}
DECL|method|setFloat
specifier|public
name|void
name|setFloat
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|type
operator|=
name|Values
operator|.
name|SIMPLE_FLOAT_4BYTE
expr_stmt|;
name|doubleValue
operator|=
name|value
expr_stmt|;
block|}
DECL|method|setFloat
specifier|public
name|void
name|setFloat
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|type
operator|=
name|Values
operator|.
name|SIMPLE_FLOAT_8BYTE
expr_stmt|;
name|doubleValue
operator|=
name|value
expr_stmt|;
block|}
DECL|method|setBytes
specifier|public
name|void
name|setBytes
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|Values
name|type
parameter_list|)
block|{
name|setBytes
argument_list|(
name|value
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|setBytes
specifier|public
name|void
name|setBytes
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|Values
name|type
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|bytes
operator|.
name|copy
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|bytesComparator
operator|=
name|comp
expr_stmt|;
block|}
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
DECL|method|bytesComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|bytesComparator
parameter_list|()
block|{
return|return
name|bytesComparator
return|;
block|}
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|()
block|{
return|return
name|doubleValue
return|;
block|}
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|()
block|{
return|return
name|longValue
return|;
block|}
DECL|method|setBytesComparator
specifier|public
name|void
name|setBytesComparator
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
name|this
operator|.
name|bytesComparator
operator|=
name|comp
expr_stmt|;
block|}
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|Values
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
return|return
name|tokenStream
return|;
block|}
DECL|method|set
specifier|public
parameter_list|<
name|T
extends|extends
name|AbstractField
parameter_list|>
name|T
name|set
parameter_list|(
name|T
name|field
parameter_list|)
block|{
name|field
operator|.
name|setDocValues
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|field
return|;
block|}
DECL|method|set
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|AbstractField
parameter_list|>
name|T
name|set
parameter_list|(
name|T
name|field
parameter_list|,
name|Values
name|type
parameter_list|)
block|{
if|if
condition|(
name|field
operator|instanceof
name|ValuesField
condition|)
return|return
name|field
return|;
specifier|final
name|ValuesField
name|valField
init|=
operator|new
name|ValuesField
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
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
name|BytesRef
name|ref
init|=
name|field
operator|.
name|isBinary
argument_list|()
condition|?
operator|new
name|BytesRef
argument_list|(
name|field
operator|.
name|getBinaryValue
argument_list|()
argument_list|,
name|field
operator|.
name|getBinaryOffset
argument_list|()
argument_list|,
name|field
operator|.
name|getBinaryLength
argument_list|()
argument_list|)
else|:
operator|new
name|BytesRef
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
name|valField
operator|.
name|setBytes
argument_list|(
name|ref
argument_list|,
name|type
argument_list|)
expr_stmt|;
break|break;
case|case
name|PACKED_INTS
case|:
name|valField
operator|.
name|setInt
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIMPLE_FLOAT_4BYTE
case|:
name|valField
operator|.
name|setFloat
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIMPLE_FLOAT_8BYTE
case|:
name|valField
operator|.
name|setFloat
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown type: "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
name|valField
operator|.
name|set
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class

end_unit

