begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LeafReaderContext
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

begin_comment
comment|/**  * Converts individual ValueSource instances to leverage the FunctionValues *Val functions that work with multiple values,  * i.e. {@link org.apache.lucene.queries.function.FunctionValues#doubleVal(int, double[])}  */
end_comment

begin_comment
comment|//Not crazy about the name, but...
end_comment

begin_class
DECL|class|VectorValueSource
specifier|public
class|class
name|VectorValueSource
extends|extends
name|MultiValueSource
block|{
DECL|field|sources
specifier|protected
specifier|final
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
decl_stmt|;
DECL|method|VectorValueSource
specifier|public
name|VectorValueSource
parameter_list|(
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|)
block|{
name|this
operator|.
name|sources
operator|=
name|sources
expr_stmt|;
block|}
DECL|method|getSources
specifier|public
name|List
argument_list|<
name|ValueSource
argument_list|>
name|getSources
parameter_list|()
block|{
return|return
name|sources
return|;
block|}
annotation|@
name|Override
DECL|method|dimension
specifier|public
name|int
name|dimension
parameter_list|()
block|{
return|return
name|sources
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"vector"
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
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|sources
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// special-case x,y and lat,lon since it's so common
if|if
condition|(
name|size
operator|==
literal|2
condition|)
block|{
specifier|final
name|FunctionValues
name|x
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|y
init|=
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|byte
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|vals
index|[
literal|0
index|]
operator|=
name|x
operator|.
name|byteVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
name|y
operator|.
name|byteVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|short
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|vals
index|[
literal|0
index|]
operator|=
name|x
operator|.
name|shortVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
name|y
operator|.
name|shortVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|vals
index|[
literal|0
index|]
operator|=
name|x
operator|.
name|intVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
name|y
operator|.
name|intVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|vals
index|[
literal|0
index|]
operator|=
name|x
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
name|y
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|vals
index|[
literal|0
index|]
operator|=
name|x
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
name|y
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|double
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|vals
index|[
literal|0
index|]
operator|=
name|x
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
name|y
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|vals
index|[
literal|0
index|]
operator|=
name|x
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
name|y
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
throws|throws
name|IOException
block|{
return|return
name|name
argument_list|()
operator|+
literal|"("
operator|+
name|x
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|","
operator|+
name|y
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
specifier|final
name|FunctionValues
index|[]
name|valsArr
init|=
operator|new
name|FunctionValues
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|valsArr
index|[
name|i
index|]
operator|=
name|sources
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|byte
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
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
name|valsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
name|valsArr
index|[
name|i
index|]
operator|.
name|byteVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|short
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
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
name|valsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
name|valsArr
index|[
name|i
index|]
operator|.
name|shortVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
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
name|valsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
name|valsArr
index|[
name|i
index|]
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
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
name|valsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
name|valsArr
index|[
name|i
index|]
operator|.
name|intVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
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
name|valsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
name|valsArr
index|[
name|i
index|]
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|double
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
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
name|valsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
name|valsArr
index|[
name|i
index|]
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
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
name|valsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
name|valsArr
index|[
name|i
index|]
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
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
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
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
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|ValueSource
name|source
range|:
name|sources
control|)
name|source
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
for|for
control|(
name|ValueSource
name|source
range|:
name|sources
control|)
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|VectorValueSource
operator|)
condition|)
return|return
literal|false
return|;
name|VectorValueSource
name|that
init|=
operator|(
name|VectorValueSource
operator|)
name|o
decl_stmt|;
return|return
name|sources
operator|.
name|equals
argument_list|(
name|that
operator|.
name|sources
argument_list|)
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
return|return
name|sources
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

