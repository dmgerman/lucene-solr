begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.aggregator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|aggregator
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An Aggregator is the analogue of Lucene's Collector (see  * {@link org.apache.lucene.search.Collector}), for processing the categories  * belonging to a certain document. The Aggregator is responsible for doing  * whatever it wishes with the categories it is fed, e.g., counting the number  * of times that each category appears, or performing some computation on their  * association values.  *<P>  * Much of the function of an Aggregator implementation is not described by this  * interface. This includes the constructor and getter methods to retrieve the  * results of the aggregation.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Aggregator
specifier|public
interface|interface
name|Aggregator
block|{
comment|/**    * Specify the document (and its score in the search) that the following    * {@link #aggregate(int)} calls will pertain to.    */
DECL|method|setNextDoc
name|void
name|setNextDoc
parameter_list|(
name|int
name|docid
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Collect (and do whatever an implementation deems appropriate) the    * category given by its ordinal. This category belongs to a document    * given earlier by {@link #setNextDoc(int, float)}.    */
DECL|method|aggregate
name|void
name|aggregate
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

