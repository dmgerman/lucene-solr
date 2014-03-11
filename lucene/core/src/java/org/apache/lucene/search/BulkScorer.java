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

begin_comment
comment|/** This class is used to score a range of documents at  *  once, and is returned by {@link Weight#bulkScorer}.  Only  *  queries that have a more optimized means of scoring  *  across a range of documents need to override this.  *  Otherwise, a default implementation is wrapped around  *  the {@link Scorer} returned by {@link Weight#scorer}. */
end_comment

begin_class
DECL|class|BulkScorer
specifier|public
specifier|abstract
class|class
name|BulkScorer
block|{
comment|/** Scores and collects all matching documents.    * @param collector The collector to which all matching documents are passed.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|score
argument_list|(
name|collector
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Collects matching documents in a range.    *     * @param collector The collector to which all matching documents are passed.    * @param max Score up to, but not including, this doc    * @return true if more matching documents may remain.    */
DECL|method|score
specifier|public
specifier|abstract
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

