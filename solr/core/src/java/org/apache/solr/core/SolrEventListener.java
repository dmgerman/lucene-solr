begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|search
operator|.
name|SolrIndexSearcher
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
name|NamedListInitializedPlugin
import|;
end_import

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
comment|/**  *  */
end_comment

begin_interface
DECL|interface|SolrEventListener
specifier|public
interface|interface
name|SolrEventListener
extends|extends
name|NamedListInitializedPlugin
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrCore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
function_decl|;
DECL|method|postSoftCommit
specifier|public
name|void
name|postSoftCommit
parameter_list|()
function_decl|;
comment|/** The searchers passed here are only guaranteed to be valid for the duration    * of this method call, so care should be taken not to spawn threads or asynchronous    * tasks with references to these searchers.    *<p>    * Implementations should add the {@link org.apache.solr.common.params.EventParams#EVENT} parameter and set it to a value of either:    *<ul>    *<li>{@link org.apache.solr.common.params.EventParams#FIRST_SEARCHER} - First Searcher event</li>    *<li>{@link org.apache.solr.common.params.EventParams#NEW_SEARCHER} - New Searcher event</li>    *</ul>    *    * Sample:    *<pre>     if (currentSearcher != null) {       nlst.add(CommonParams.EVENT, CommonParams.NEW_SEARCHER);     } else {       nlst.add(CommonParams.EVENT, CommonParams.FIRST_SEARCHER);     }    *    *</pre>    *    * @see org.apache.solr.core.AbstractSolrEventListener#addEventParms(org.apache.solr.search.SolrIndexSearcher, org.apache.solr.common.util.NamedList)     *    * @param newSearcher The new {@link org.apache.solr.search.SolrIndexSearcher} to use    * @param currentSearcher The existing {@link org.apache.solr.search.SolrIndexSearcher}.  null if this is a firstSearcher event.    *    */
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

