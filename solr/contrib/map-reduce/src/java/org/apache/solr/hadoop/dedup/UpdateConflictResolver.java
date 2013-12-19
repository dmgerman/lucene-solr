begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop.dedup
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
operator|.
name|dedup
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configurable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Reducer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Reducer
operator|.
name|Context
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
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_comment
comment|/**  * Interface that enables deduplication and ordering of a series of document  * updates for the same unique document key.  *   * For example, a MapReduce batch job might index multiple files in the same job  * where some of the files contain old and new versions of the very same  * document, using the same unique document key.  *   * Typically, implementations of this interface forbid collisions by throwing an  * exception, or ignore all but the most recent document version, or, in the  * general case, order colliding updates ascending from least recent to most  * recent (partial) update.  *   * The caller of this interface (i.e. the Hadoop Reducer) will then apply the  * updates to Solr in the order returned by the orderUpdates() method.  *   * Configuration: If an UpdateConflictResolver implementation also implements  * {@link Configurable} then the Hadoop Reducer will call  * {@link Configurable#setConf(org.apache.hadoop.conf.Configuration)} on  * instance construction and pass the standard Hadoop configuration information.  */
end_comment

begin_interface
DECL|interface|UpdateConflictResolver
specifier|public
interface|interface
name|UpdateConflictResolver
block|{
comment|/**    * Given a list of all colliding document updates for the same unique document    * key, this method returns zero or more documents in an application specific    * order.    *     * The caller will then apply the updates for this key to Solr in the order    * returned by the orderUpdate() method.    *     * @param uniqueKey    *          the document key common to all collidingUpdates mentioned below    * @param collidingUpdates    *          all updates in the MapReduce job that have a key equal to    *          {@code uniqueKey} mentioned above. The input order is unspecified.    * @param context    *          The<code>Context</code> passed from the {@link Reducer}    *          implementations.    * @return the order in which the updates shall be applied to Solr    */
DECL|method|orderUpdates
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|orderUpdates
parameter_list|(
name|Text
name|uniqueKey
parameter_list|,
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|collidingUpdates
parameter_list|,
name|Context
name|context
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

