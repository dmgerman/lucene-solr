begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import

begin_comment
comment|/**  * All Solr filters available to the user's webapp should  * extend this class and not just implement {@link Filter}.  * This class ensures that the logging configuration is correct  * before any Solr specific code is executed.  */
end_comment

begin_class
DECL|class|BaseSolrFilter
specifier|abstract
class|class
name|BaseSolrFilter
implements|implements
name|Filter
block|{
static|static
block|{
name|CheckLoggingConfiguration
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

