begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrInfoMBean
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
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @version $Id$  *  * @deprecated Solr now supports multiple cores and no longer uses a static singleton for SolrCore.   */
end_comment

begin_comment
comment|// A Registry to hold a collection of SolrInfo objects
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SolrInfoRegistry
specifier|public
class|class
name|SolrInfoRegistry
block|{
annotation|@
name|Deprecated
DECL|method|getRegistry
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|getRegistry
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
return|;
block|}
block|}
end_class

end_unit

