begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|IndexDocValues
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
name|ValueType
import|;
end_import

begin_comment
comment|/**  * Expert: obtains numeric field values from a {@link IndexDocValues} field.  * This {@link ValueSource} is compatible with all numerical  * {@link IndexDocValues}  *   * @lucene.experimental  *   */
end_comment

begin_class
DECL|class|NumericIndexDocValueSource
specifier|public
class|class
name|NumericIndexDocValueSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|NumericIndexDocValueSource
specifier|public
name|NumericIndexDocValueSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexDocValues
operator|.
name|Source
name|source
init|=
name|context
operator|.
name|reader
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
operator|.
name|getSource
argument_list|()
decl_stmt|;
name|ValueType
name|type
init|=
name|source
operator|.
name|type
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
return|return
operator|new
name|DocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|"float: ["
operator|+
name|floatVal
argument_list|(
name|doc
argument_list|)
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|source
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
case|case
name|VAR_INTS
case|:
return|return
operator|new
name|DocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|"float: ["
operator|+
name|floatVal
argument_list|(
name|doc
argument_list|)
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|source
operator|.
name|getInt
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Type: "
operator|+
name|type
operator|+
literal|"is not numeric"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|field
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|field
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|NumericIndexDocValueSource
name|other
init|=
operator|(
name|NumericIndexDocValueSource
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|field
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
literal|"DocValues float("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
block|}
end_class

end_unit

