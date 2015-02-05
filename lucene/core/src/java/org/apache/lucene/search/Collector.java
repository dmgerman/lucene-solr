begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_comment
comment|/**  *<p>Expert: Collectors are primarily meant to be used to  * gather raw results from a search, and implement sorting  * or custom result filtering, collation, etc.</p>  *  *<p>Lucene's core collectors are derived from {@link Collector}  * and {@link SimpleCollector}. Likely your application can  * use one of these classes, or subclass {@link TopDocsCollector},  * instead of implementing Collector directly:  *  *<ul>  *  *<li>{@link TopDocsCollector} is an abstract base class  *   that assumes you will retrieve the top N docs,  *   according to some criteria, after collection is  *   done.</li>  *  *<li>{@link TopScoreDocCollector} is a concrete subclass  *   {@link TopDocsCollector} and sorts according to score +  *   docID.  This is used internally by the {@link  *   IndexSearcher} search methods that do not take an  *   explicit {@link Sort}. It is likely the most frequently  *   used collector.</li>  *  *<li>{@link TopFieldCollector} subclasses {@link  *   TopDocsCollector} and sorts according to a specified  *   {@link Sort} object (sort by field).  This is used  *   internally by the {@link IndexSearcher} search methods  *   that take an explicit {@link Sort}.  *  *<li>{@link TimeLimitingCollector}, which wraps any other  *   Collector and aborts the search if it's taken too much  *   time.</li>  *  *<li>{@link PositiveScoresOnlyCollector} wraps any other  *   Collector and prevents collection of hits whose score  *   is&lt;= 0.0</li>  *  *</ul>  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Collector
specifier|public
interface|interface
name|Collector
block|{
comment|/**    * Create a new {@link LeafCollector collector} to collect the given context.    *    * @param context    *          next atomic reader context    */
DECL|method|getLeafCollector
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Indicates if document scores are needed by this collector.    *     * @return {@code true} if scores are needed.    */
DECL|method|needsScores
name|boolean
name|needsScores
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

