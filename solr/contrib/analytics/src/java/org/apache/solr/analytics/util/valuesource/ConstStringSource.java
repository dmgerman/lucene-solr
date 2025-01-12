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
operator|.
name|LiteralValueSource
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
comment|/**  *<code>ConstStringSource</code> returns a constant string for all documents  */
end_comment

begin_class
DECL|class|ConstStringSource
specifier|public
class|class
name|ConstStringSource
extends|extends
name|LiteralValueSource
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
name|CONSTANT_STRING
decl_stmt|;
DECL|method|ConstStringSource
specifier|public
name|ConstStringSource
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|super
argument_list|(
name|string
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
return|return
name|name
argument_list|()
operator|+
literal|"("
operator|+
name|string
operator|+
literal|")"
return|;
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
name|ConstStringSource
operator|)
condition|)
return|return
literal|false
return|;
name|ConstStringSource
name|that
init|=
operator|(
name|ConstStringSource
operator|)
name|o
decl_stmt|;
return|return
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

