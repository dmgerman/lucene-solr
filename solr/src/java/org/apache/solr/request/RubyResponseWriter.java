begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * @deprecated use org.apache.solr.response.RubyResponseWriter  */
end_comment

begin_class
DECL|class|RubyResponseWriter
specifier|public
class|class
name|RubyResponseWriter
extends|extends
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|RubyResponseWriter
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RubyResponseWriter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|RubyResponseWriter
specifier|public
name|RubyResponseWriter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|RubyResponseWriter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" is deprecated. Please use the corresponding class in org.apache.solr.response"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

