begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
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
DECL|class|SimpleAggValueSource
specifier|public
specifier|abstract
class|class
name|SimpleAggValueSource
extends|extends
name|AggValueSource
block|{
DECL|field|arg
name|ValueSource
name|arg
decl_stmt|;
DECL|method|SimpleAggValueSource
specifier|public
name|SimpleAggValueSource
parameter_list|(
name|String
name|name
parameter_list|,
name|ValueSource
name|vs
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|arg
operator|=
name|vs
expr_stmt|;
block|}
DECL|method|getArg
specifier|public
name|ValueSource
name|getArg
parameter_list|()
block|{
return|return
name|arg
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|ValueSource
name|otherArg
init|=
operator|(
operator|(
name|SimpleAggValueSource
operator|)
name|o
operator|)
operator|.
name|arg
decl_stmt|;
if|if
condition|(
name|arg
operator|==
name|otherArg
condition|)
return|return
literal|true
return|;
return|return
operator|(
name|arg
operator|!=
literal|null
operator|&&
name|arg
operator|.
name|equals
argument_list|(
name|otherArg
argument_list|)
operator|)
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
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|arg
operator|==
literal|null
condition|?
literal|0
else|:
name|arg
operator|.
name|hashCode
argument_list|()
operator|)
return|;
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
name|name
argument_list|()
operator|+
literal|"("
operator|+
operator|(
name|arg
operator|==
literal|null
condition|?
literal|""
else|:
name|arg
operator|.
name|description
argument_list|()
operator|)
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

