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

begin_comment
comment|/**  * Re-scores the topN results ({@link TopDocs}) from an original  * query.  See {@link QueryRescorer} for an actual  * implementation.  Typically, you run a low-cost  * first-pass query across the entire index, collecting the  * top few hundred hits perhaps, and then use this class to  * mix in a more costly second pass scoring.  *  *<p>See {@link  * QueryRescorer#rescore(IndexSearcher,TopDocs,Query,double,int)}  * for a simple static method to call to rescore using a 2nd  * pass {@link Query}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Rescorer
specifier|public
specifier|abstract
class|class
name|Rescorer
block|{
comment|/**     * Rescore an initial first-pass {@link TopDocs}.    *    * @param searcher {@link IndexSearcher} used to produce the    *   first pass topDocs    * @param firstPassTopDocs Hits from the first pass    *   search.  It's very important that these hits were    *   produced by the provided searcher; otherwise the doc    *   IDs will not match!    * @param topN How many re-scored hits to return    */
DECL|method|rescore
specifier|public
specifier|abstract
name|TopDocs
name|rescore
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TopDocs
name|firstPassTopDocs
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Explains how the score for the specified document was    * computed.    */
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Explanation
name|firstPassExplanation
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

