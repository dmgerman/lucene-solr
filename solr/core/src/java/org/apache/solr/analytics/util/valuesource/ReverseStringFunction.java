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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
comment|/**  *<code>ReverseStringFunction</code> reverses the string value of the source it contains.  */
end_comment

begin_class
DECL|class|ReverseStringFunction
specifier|public
class|class
name|ReverseStringFunction
extends|extends
name|SingleStringFunction
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
name|REVERSE
decl_stmt|;
DECL|method|ReverseStringFunction
specifier|public
name|ReverseStringFunction
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
DECL|method|func
specifier|protected
name|CharSequence
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
name|vals
parameter_list|)
block|{
name|String
name|val
init|=
name|vals
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|StringUtils
operator|.
name|reverse
argument_list|(
name|val
argument_list|)
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

