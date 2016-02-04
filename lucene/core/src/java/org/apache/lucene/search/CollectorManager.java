begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * A manager of collectors. This class is useful to parallelize execution of  * search requests and has two main methods:  *<ul>  *<li>{@link #newCollector()} which must return a NEW collector which  *       will be used to collect a certain set of leaves.</li>  *<li>{@link #reduce(Collection)} which will be used to reduce the  *       results of individual collections into a meaningful result.  *       This method is only called after all leaves have been fully  *       collected.</li>  *</ul>  *  * @see IndexSearcher#search(Query, CollectorManager)  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|CollectorManager
specifier|public
interface|interface
name|CollectorManager
parameter_list|<
name|C
extends|extends
name|Collector
parameter_list|,
name|T
parameter_list|>
block|{
comment|/**    * Return a new {@link Collector}. This must return a different instance on    * each call.    */
DECL|method|newCollector
name|C
name|newCollector
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Reduce the results of individual collectors into a meaningful result.    * For instance a {@link TopDocsCollector} would compute the    * {@link TopDocsCollector#topDocs() top docs} of each collector and then    * merge them using {@link TopDocs#merge(int, TopDocs[])}.    * This method must be called after collection is finished on all provided    * collectors.    */
DECL|method|reduce
name|T
name|reduce
parameter_list|(
name|Collection
argument_list|<
name|C
argument_list|>
name|collectors
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

