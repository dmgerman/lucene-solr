begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|IndexReader
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
comment|/**  * Take a ValueSourc and produce convert the number to radians and  * return that value  */
end_comment

begin_class
DECL|class|RadianFunction
specifier|public
class|class
name|RadianFunction
extends|extends
name|ValueSource
block|{
DECL|field|valSource
specifier|protected
name|ValueSource
name|valSource
decl_stmt|;
DECL|method|RadianFunction
specifier|public
name|RadianFunction
parameter_list|(
name|ValueSource
name|valSource
parameter_list|)
block|{
name|this
operator|.
name|valSource
operator|=
name|valSource
expr_stmt|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"rad("
operator|+
name|valSource
operator|.
name|description
argument_list|()
operator|+
literal|')'
return|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValues
name|dv
init|=
name|valSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
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
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
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
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
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
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Math
operator|.
name|toRadians
argument_list|(
name|dv
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
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
name|doubleVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
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
operator|+
literal|'='
operator|+
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
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
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|RadianFunction
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|RadianFunction
name|other
init|=
operator|(
name|RadianFunction
operator|)
name|o
decl_stmt|;
return|return
name|description
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|description
argument_list|()
argument_list|)
operator|&&
name|valSource
operator|.
name|equals
argument_list|(
name|other
operator|.
name|valSource
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|description
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|valSource
operator|.
name|hashCode
argument_list|()
return|;
block|}
empty_stmt|;
block|}
end_class

end_unit

