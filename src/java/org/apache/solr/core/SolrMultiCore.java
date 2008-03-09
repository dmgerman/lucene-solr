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

begin_comment
comment|/**  * A MultiCore singleton.  * Marked as deprecated to avoid usage proliferation of core code that would  * assume MultiCore being a singleton.  In solr 2.0, the MultiCore factory  * should be popluated with a standard tool like spring.  Until then, this is  * a simple static factory that should not be used widely.   *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SolrMultiCore
specifier|public
specifier|final
class|class
name|SolrMultiCore
extends|extends
name|MultiCore
block|{
DECL|field|instance
specifier|private
specifier|static
name|MultiCore
name|instance
init|=
literal|null
decl_stmt|;
comment|// no one else can make the registry
DECL|method|SolrMultiCore
specifier|private
name|SolrMultiCore
parameter_list|()
block|{}
comment|/** Returns a default MultiCore singleton.    * @return    */
DECL|method|getInstance
specifier|public
specifier|static
specifier|synchronized
name|MultiCore
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|SolrMultiCore
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
block|}
end_class

end_unit

