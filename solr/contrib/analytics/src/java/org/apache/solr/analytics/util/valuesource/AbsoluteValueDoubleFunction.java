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
comment|/**  *<code>AbsoluteValueDoubleFunction</code> takes the absolute value of the double value of the source it contains.  */
end_comment

begin_class
DECL|class|AbsoluteValueDoubleFunction
specifier|public
class|class
name|AbsoluteValueDoubleFunction
extends|extends
name|SingleDoubleFunction
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
name|ABSOLUTE_VALUE
decl_stmt|;
DECL|method|AbsoluteValueDoubleFunction
specifier|public
name|AbsoluteValueDoubleFunction
parameter_list|(
name|ValueSource
name|source
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
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
name|source
operator|.
name|description
argument_list|()
operator|+
literal|")"
return|;
block|}
DECL|method|func
specifier|protected
name|double
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|d
init|=
name|vals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
return|return
name|d
operator|*
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|d
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
name|AbsoluteValueDoubleFunction
name|other
init|=
operator|(
name|AbsoluteValueDoubleFunction
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
return|;
block|}
block|}
end_class

end_unit

