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
name|Date
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
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParams
import|;
end_import

begin_comment
comment|/**  *<code>DefaultIsMissingFieldSource</code> wraps a field source to return missing values   * if the value is equal to the default value.  */
end_comment

begin_class
DECL|class|FilterFieldSource
specifier|public
class|class
name|FilterFieldSource
extends|extends
name|ValueSource
block|{
DECL|field|NAME
specifier|public
specifier|final
specifier|static
name|String
name|NAME
init|=
name|AnalyticsParams
operator|.
name|FILTER
decl_stmt|;
DECL|field|missValue
specifier|public
specifier|final
name|Object
name|missValue
decl_stmt|;
DECL|field|source
specifier|protected
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|method|FilterFieldSource
specifier|public
name|FilterFieldSource
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|Object
name|missValue
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
name|missValue
operator|=
name|missValue
expr_stmt|;
block|}
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
if|if
condition|(
name|missValue
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|Date
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|name
argument_list|()
operator|+
literal|"("
operator|+
name|source
operator|.
name|description
argument_list|()
operator|+
literal|","
operator|+
operator|(
operator|(
name|Date
operator|)
name|missValue
operator|)
operator|.
name|toInstant
argument_list|()
operator|+
literal|")"
return|;
block|}
else|else
block|{
return|return
name|name
argument_list|()
operator|+
literal|"("
operator|+
name|source
operator|.
name|description
argument_list|()
operator|+
literal|","
operator|+
name|missValue
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
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
specifier|final
name|FunctionValues
name|vals
init|=
name|source
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
name|byte
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|vals
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
throws|throws
name|IOException
block|{
return|return
name|vals
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
throws|throws
name|IOException
block|{
return|return
name|vals
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
throws|throws
name|IOException
block|{
return|return
name|vals
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
throws|throws
name|IOException
block|{
return|return
name|vals
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
throws|throws
name|IOException
block|{
return|return
name|vals
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
throws|throws
name|IOException
block|{
return|return
name|vals
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
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|exists
argument_list|(
name|doc
argument_list|)
condition|?
name|vals
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
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
throws|throws
name|IOException
block|{
name|Object
name|other
init|=
name|vals
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|other
operator|!=
literal|null
operator|&&
operator|!
name|missValue
operator|.
name|equals
argument_list|(
name|other
argument_list|)
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
throws|throws
name|IOException
block|{
return|return
name|NAME
operator|+
literal|'('
operator|+
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|')'
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
name|ValueFiller
name|delegateFiller
init|=
name|vals
operator|.
name|getValueFiller
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|MutableValue
name|mval
init|=
name|delegateFiller
operator|.
name|getValue
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
throws|throws
name|IOException
block|{
name|delegateFiller
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
name|exists
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|getRootSource
specifier|public
name|ValueSource
name|getRootSource
parameter_list|()
block|{
if|if
condition|(
name|source
operator|instanceof
name|FilterFieldSource
condition|)
block|{
return|return
operator|(
operator|(
name|FilterFieldSource
operator|)
name|source
operator|)
operator|.
name|getRootSource
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|source
return|;
block|}
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
name|FilterFieldSource
name|other
init|=
operator|(
name|FilterFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|source
operator|.
name|equals
argument_list|(
name|other
operator|.
name|source
argument_list|)
operator|&&
name|this
operator|.
name|missValue
operator|.
name|equals
argument_list|(
name|other
operator|.
name|missValue
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
name|source
operator|.
name|hashCode
argument_list|()
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

