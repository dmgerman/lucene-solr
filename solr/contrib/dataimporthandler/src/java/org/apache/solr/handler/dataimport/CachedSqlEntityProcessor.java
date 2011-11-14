begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/**  * This class enables caching of data obtained from the DB to avoid too many sql  * queries  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache  * .org/solr/DataImportHandler</a> for more details.  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *   * @since solr 1.3  * @deprecated - Use SqlEntityProcessor with cacheImpl parameter.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|CachedSqlEntityProcessor
specifier|public
class|class
name|CachedSqlEntityProcessor
extends|extends
name|SqlEntityProcessor
block|{
annotation|@
name|Override
DECL|method|initCache
specifier|protected
name|void
name|initCache
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|cacheSupport
operator|=
operator|new
name|DIHCacheSupport
argument_list|(
name|context
argument_list|,
literal|"SortedMapBackedCache"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

