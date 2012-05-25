begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|common
operator|.
name|SolrException
import|;
end_import

begin_comment
comment|/**  * A marker interface for denoting that a factory is responsible for handling  * distributed communication of updates across a SolrCloud cluster.  *   * @see UpdateRequestProcessorChain  */
end_comment

begin_interface
DECL|interface|DistributingUpdateProcessorFactory
specifier|public
interface|interface
name|DistributingUpdateProcessorFactory
block|{
comment|/**    * Internal param used to specify the current phase of a distributed update,     * not intended for use by clients.  Any non-blank value can be used to     * indicate to the<code>UpdateRequestProcessorChain</code> that factories     * prior to the<code>DistributingUpdateProcessorFactory</code> can be skipped.    * Implementations of this interface may use the non-blank values any way     * they wish.    */
DECL|field|DISTRIB_UPDATE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|DISTRIB_UPDATE_PARAM
init|=
literal|"update.distrib"
decl_stmt|;
block|}
end_interface

end_unit

