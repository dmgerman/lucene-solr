begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.util.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|valuesource
package|;
end_package

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
name|Map
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|StrDocValues
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
name|BytesRefBuilder
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
name|mutable
operator|.
name|MutableValue
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
name|mutable
operator|.
name|MutableValueStr
import|;
end_import

begin_comment
comment|/**  * Abstract {@link ValueSource} implementation which wraps multiple ValueSources  * and applies an extendible string function to their values.  **/
end_comment

begin_class
DECL|class|MultiStringFunction
specifier|public
specifier|abstract
class|class
name|MultiStringFunction
extends|extends
name|ValueSource
block|{
DECL|field|sources
specifier|protected
specifier|final
name|ValueSource
index|[]
name|sources
decl_stmt|;
DECL|method|MultiStringFunction
specifier|public
name|MultiStringFunction
parameter_list|(
name|ValueSource
index|[]
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
DECL|method|name
specifier|abstract
specifier|protected
name|String
name|name
parameter_list|()
function_decl|;
DECL|method|func
specifier|abstract
specifier|protected
name|CharSequence
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
index|[]
name|valsArr
parameter_list|)
function_decl|;
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
specifier|final
name|FunctionValues
index|[]
name|valsArr
init|=
operator|new
name|FunctionValues
index|[
name|sources
operator|.
name|length
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
name|sources
operator|.
name|length
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
index|[
name|i
index|]
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
name|StrDocValues
argument_list|(
name|this
argument_list|)
block|{
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
name|CharSequence
name|cs
init|=
name|func
argument_list|(
name|doc
argument_list|,
name|valsArr
argument_list|)
decl_stmt|;
return|return
name|cs
operator|!=
literal|null
condition|?
name|cs
operator|.
name|toString
argument_list|()
else|:
literal|null
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
name|boolean
name|exists
init|=
literal|true
decl_stmt|;
for|for
control|(
name|FunctionValues
name|val
range|:
name|valsArr
control|)
block|{
name|exists
operator|=
name|exists
operator|&
name|val
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|exists
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
name|BytesRefBuilder
name|bytes
parameter_list|)
block|{
name|bytes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|CharSequence
name|cs
init|=
name|func
argument_list|(
name|doc
argument_list|,
name|valsArr
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|.
name|copyChars
argument_list|(
name|func
argument_list|(
name|doc
argument_list|,
name|valsArr
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
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
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueStr
name|mval
init|=
operator|new
name|MutableValueStr
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|mval
operator|.
name|exists
operator|=
name|bytesVal
argument_list|(
name|doc
argument_list|,
name|mval
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
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
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MultiStringFunction
name|other
init|=
operator|(
name|MultiStringFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|sources
argument_list|,
name|other
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|sources
argument_list|)
operator|+
name|name
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

