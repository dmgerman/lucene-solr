begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|valuesource
package|;
end_package

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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|DoubleDocValues
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|DoubleConstValueSource
specifier|public
class|class
name|DoubleConstValueSource
extends|extends
name|ConstNumberSource
block|{
DECL|field|constant
specifier|final
name|double
name|constant
decl_stmt|;
DECL|field|fv
specifier|private
specifier|final
name|float
name|fv
decl_stmt|;
DECL|field|lv
specifier|private
specifier|final
name|long
name|lv
decl_stmt|;
DECL|method|DoubleConstValueSource
specifier|public
name|DoubleConstValueSource
parameter_list|(
name|double
name|constant
parameter_list|)
block|{
name|this
operator|.
name|constant
operator|=
name|constant
expr_stmt|;
name|this
operator|.
name|fv
operator|=
operator|(
name|float
operator|)
name|constant
expr_stmt|;
name|this
operator|.
name|lv
operator|=
operator|(
name|long
operator|)
name|constant
expr_stmt|;
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
literal|"const("
operator|+
name|constant
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
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
name|fv
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|lv
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|lv
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|constant
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|constant
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|constant
return|;
block|}
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
name|description
argument_list|()
return|;
block|}
block|}
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
name|long
name|bits
init|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|constant
argument_list|)
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|bits
operator|^
operator|(
name|bits
operator|>>>
literal|32
operator|)
argument_list|)
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
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|DoubleConstValueSource
operator|)
condition|)
return|return
literal|false
return|;
name|DoubleConstValueSource
name|other
init|=
operator|(
name|DoubleConstValueSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|constant
operator|==
name|other
operator|.
name|constant
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|lv
return|;
block|}
annotation|@
name|Override
DECL|method|getLong
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|lv
return|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
return|return
name|fv
return|;
block|}
annotation|@
name|Override
DECL|method|getDouble
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
return|return
name|constant
return|;
block|}
annotation|@
name|Override
DECL|method|getNumber
specifier|public
name|Number
name|getNumber
parameter_list|()
block|{
return|return
name|constant
return|;
block|}
annotation|@
name|Override
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|()
block|{
return|return
name|constant
operator|!=
literal|0
return|;
block|}
block|}
end_class

end_unit

