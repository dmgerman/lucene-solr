begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ValueSource
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
name|IndexSearcher
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
name|Arrays
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
name|Map
import|;
end_import

begin_class
DECL|class|DefFunction
specifier|public
class|class
name|DefFunction
extends|extends
name|MultiFunction
block|{
DECL|method|DefFunction
specifier|public
name|DefFunction
parameter_list|(
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|)
block|{
name|super
argument_list|(
name|sources
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"def"
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
name|fcontext
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Values
argument_list|(
name|valsArr
argument_list|(
name|sources
argument_list|,
name|fcontext
argument_list|,
name|readerContext
argument_list|)
argument_list|)
block|{
specifier|final
name|int
name|upto
init|=
name|valsArr
operator|.
name|length
operator|-
literal|1
decl_stmt|;
specifier|private
name|FunctionValues
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|upto
condition|;
name|i
operator|++
control|)
block|{
name|FunctionValues
name|vals
init|=
name|valsArr
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|vals
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|vals
return|;
block|}
block|}
return|return
name|valsArr
index|[
name|upto
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|byteVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|shortVal
argument_list|(
name|doc
argument_list|)
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
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
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
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|intVal
argument_list|(
name|doc
argument_list|)
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
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
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
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
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
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|boolVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|bytesVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRef
name|target
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|bytesVal
argument_list|(
name|doc
argument_list|,
name|target
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
name|get
argument_list|(
name|doc
argument_list|)
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// return true if any source is exists?
for|for
control|(
name|FunctionValues
name|vals
range|:
name|valsArr
control|)
block|{
if|if
condition|(
name|vals
operator|.
name|exists
argument_list|(
name|doc
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
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
comment|// TODO: need ValueSource.type() to determine correct type
return|return
name|super
operator|.
name|getValueFiller
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

