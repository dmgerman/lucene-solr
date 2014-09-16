begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.statistics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|statistics
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|FunctionValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|mutable
operator|.
name|MutableValue
import|;
end_import

begin_comment
comment|/**  *<code>StatsCollector</code> implementations reduce a list of Objects to a single value.  * Most implementations reduce a list to a statistic on that list.  */
end_comment

begin_interface
DECL|interface|StatsCollector
specifier|public
interface|interface
name|StatsCollector
block|{
comment|/**    * Collect values from the value source and add to statistics.    * @param doc Document to collect from    */
DECL|method|collect
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**    * @param context The context to read documents from.    * @throws IOException if setting next reader fails    */
DECL|method|setNextReader
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getValue
name|MutableValue
name|getValue
parameter_list|()
function_decl|;
DECL|method|getFunction
name|FunctionValues
name|getFunction
parameter_list|()
function_decl|;
comment|/**    * @return The set of statistics being computed by the stats collector.    */
DECL|method|getStatsList
name|Set
argument_list|<
name|String
argument_list|>
name|getStatsList
parameter_list|()
function_decl|;
comment|/**    * Return the value of the given statistic.    * @param stat the stat    * @return a comparable    */
DECL|method|getStat
name|Comparable
name|getStat
parameter_list|(
name|String
name|stat
parameter_list|)
function_decl|;
comment|/**    * After all documents have been collected, this method should be    * called to finalize the calculations of each statistic.    */
DECL|method|compute
name|void
name|compute
parameter_list|()
function_decl|;
comment|/**    * @return The string representation of the value source.    */
DECL|method|valueSourceString
name|String
name|valueSourceString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

