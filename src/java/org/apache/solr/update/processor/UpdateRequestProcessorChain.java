begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|util
operator|.
name|plugin
operator|.
name|PluginInfoInitialized
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
name|PluginInfo
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
name|List
import|;
end_import

begin_comment
comment|/**  * Manages a chain of UpdateRequestProcessorFactories.  *<p>  * Chain can be configured via solrconfig.xml:  *</p>  *<pre>  *&lt;updateRequestProcessors name="key" default="true"&gt;  *&lt;processor class="PathToClass1" /&gt;  *&lt;processor class="PathToClass2" /&gt;  *&lt;processor class="solr.LogUpdateProcessorFactory"&gt;  *&lt;int name="maxNumToLog"&gt;100&lt;/int&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.RunUpdateProcessorFactory" /&gt;  *&lt;/updateRequestProcessors&gt;  *</pre>  *  * @see UpdateRequestProcessorFactory  * @since solr 1.3  */
end_comment

begin_class
DECL|class|UpdateRequestProcessorChain
specifier|public
specifier|final
class|class
name|UpdateRequestProcessorChain
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|chain
specifier|private
name|UpdateRequestProcessorFactory
index|[]
name|chain
decl_stmt|;
DECL|field|solrCore
specifier|private
specifier|final
name|SolrCore
name|solrCore
decl_stmt|;
DECL|method|UpdateRequestProcessorChain
specifier|public
name|UpdateRequestProcessorChain
parameter_list|(
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|this
operator|.
name|solrCore
operator|=
name|solrCore
expr_stmt|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|list
init|=
name|solrCore
operator|.
name|initPlugins
argument_list|(
name|info
operator|.
name|getChildren
argument_list|(
literal|"processor"
argument_list|)
argument_list|,
name|UpdateRequestProcessorFactory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"updateRequestProcessorChain require at least one processor"
argument_list|)
throw|;
block|}
name|chain
operator|=
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|UpdateRequestProcessorFactory
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|UpdateRequestProcessorChain
specifier|public
name|UpdateRequestProcessorChain
parameter_list|(
name|UpdateRequestProcessorFactory
index|[]
name|chain
parameter_list|,
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
name|this
operator|.
name|solrCore
operator|=
name|solrCore
expr_stmt|;
block|}
DECL|method|createProcessor
specifier|public
name|UpdateRequestProcessor
name|createProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|UpdateRequestProcessor
name|processor
init|=
literal|null
decl_stmt|;
name|UpdateRequestProcessor
name|last
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|chain
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|processor
operator|=
name|chain
index|[
name|i
index|]
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|last
argument_list|)
expr_stmt|;
name|last
operator|=
name|processor
operator|==
literal|null
condition|?
name|last
else|:
name|processor
expr_stmt|;
block|}
return|return
name|last
return|;
block|}
DECL|method|getFactories
specifier|public
name|UpdateRequestProcessorFactory
index|[]
name|getFactories
parameter_list|()
block|{
return|return
name|chain
return|;
block|}
block|}
end_class

end_unit

