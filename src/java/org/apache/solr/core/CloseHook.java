begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**     * Interface to request notification when the core is closed.  *<p/>  * Call {@link org.apache.solr.core.SolrCore#addCloseHook(org.apache.solr.core.CloseHook)} during the {@link org.apache.solr.util.plugin.SolrCoreAware#inform(SolrCore)} method to  * add a close hook to your object.  *<p/>  * The close hook can be useful for releasing objects related to the request handler (for instance, if you have a JDBC DataSource or something like that)   *  *   */
end_comment

begin_interface
DECL|interface|CloseHook
specifier|public
interface|interface
name|CloseHook
block|{
DECL|method|close
name|void
name|close
parameter_list|(
name|SolrCore
name|core
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

