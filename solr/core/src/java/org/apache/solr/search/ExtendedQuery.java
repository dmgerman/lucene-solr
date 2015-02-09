begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/** The ExtendedQuery interface provides extra metadata to a query.  *  Implementations of ExtendedQuery must also extend Query.  */
end_comment

begin_interface
DECL|interface|ExtendedQuery
specifier|public
interface|interface
name|ExtendedQuery
block|{
comment|/** Should this query be cached in the query cache or filter cache. */
DECL|method|getCache
specifier|public
name|boolean
name|getCache
parameter_list|()
function_decl|;
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|boolean
name|cache
parameter_list|)
function_decl|;
comment|/** Returns the cost of this query, used to order checking of filters that are not cached.    * If getCache()==false&amp;&amp; getCost()&gt;=100&amp;&amp; this instanceof PostFilter, then    * the PostFilter interface will be used for filtering.    */
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
function_decl|;
DECL|method|setCost
specifier|public
name|void
name|setCost
parameter_list|(
name|int
name|cost
parameter_list|)
function_decl|;
comment|/** If true, the clauses of this boolean query should be cached separately. This is not yet implemented. */
DECL|method|getCacheSep
specifier|public
name|boolean
name|getCacheSep
parameter_list|()
function_decl|;
DECL|method|setCacheSep
specifier|public
name|void
name|setCacheSep
parameter_list|(
name|boolean
name|cacheSep
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

