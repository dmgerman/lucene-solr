begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|Map
import|;
end_import

begin_comment
comment|/**  *<code>LinearFloatFunction</code> implements a linear function over  * another {@link ValueSource}.  *<br>  * Normally Used as an argument to a {@link FunctionQuery}  *  * @version $Id$  */
end_comment

begin_class
DECL|class|LinearFloatFunction
specifier|public
class|class
name|LinearFloatFunction
extends|extends
name|ValueSource
block|{
DECL|field|source
specifier|protected
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|field|slope
specifier|protected
specifier|final
name|float
name|slope
decl_stmt|;
DECL|field|intercept
specifier|protected
specifier|final
name|float
name|intercept
decl_stmt|;
DECL|method|LinearFloatFunction
specifier|public
name|LinearFloatFunction
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|float
name|slope
parameter_list|,
name|float
name|intercept
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
name|slope
operator|=
name|slope
expr_stmt|;
name|this
operator|.
name|intercept
operator|=
name|intercept
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
name|slope
operator|+
literal|"*float("
operator|+
name|source
operator|.
name|description
argument_list|()
operator|+
literal|")+"
operator|+
name|intercept
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
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
name|DocValues
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
name|DocValues
argument_list|()
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
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
operator|*
name|slope
operator|+
name|intercept
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
name|floatVal
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
operator|(
name|long
operator|)
name|floatVal
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
operator|(
name|double
operator|)
name|floatVal
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
name|Float
operator|.
name|toString
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
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
block|{
return|return
name|slope
operator|+
literal|"*float("
operator|+
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|")+"
operator|+
name|intercept
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
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|slope
argument_list|)
decl_stmt|;
name|h
operator|=
operator|(
name|h
operator|>>>
literal|2
operator|)
operator||
operator|(
name|h
operator|<<
literal|30
operator|)
expr_stmt|;
name|h
operator|+=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|intercept
argument_list|)
expr_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|14
operator|)
operator||
operator|(
name|h
operator|>>>
literal|19
operator|)
expr_stmt|;
return|return
name|h
operator|+
name|source
operator|.
name|hashCode
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
name|LinearFloatFunction
operator|.
name|class
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|LinearFloatFunction
name|other
init|=
operator|(
name|LinearFloatFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|slope
operator|==
name|other
operator|.
name|slope
operator|&&
name|this
operator|.
name|intercept
operator|==
name|other
operator|.
name|intercept
operator|&&
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
return|;
block|}
block|}
end_class

end_unit

