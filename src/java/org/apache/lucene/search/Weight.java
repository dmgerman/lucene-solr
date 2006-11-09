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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
import|;
end_import

begin_comment
comment|/** Expert: Calculate query weights and build query scorers.  *<p>  * The purpose of Weight is to make it so that searching does not modify  * a Query, so that a Query instance can be reused.<br>  * Searcher dependent state of the query should reside in the Weight.<br>  * IndexReader dependent state should reside in the Scorer.  *<p>  * A<code>Weight</code> is used in the following way:  *<ol>  *<li>A<code>Weight</code> is constructed by a top-level query,  *     given a<code>Searcher</code> ({@link Query#createWeight(Searcher)}).  *<li>The {@link #sumOfSquaredWeights()} method is called  *     on the<code>Weight</code> to compute  *     the query normalization factor {@link Similarity#queryNorm(float)}  *     of the query clauses contained in the query.  *<li>The query normalization factor is passed to {@link #normalize(float)}.  *     At this point the weighting is complete.  *<li>A<code>Scorer</code> is constructed by {@link #scorer(IndexReader)}.  *</ol>  */
end_comment

begin_interface
DECL|interface|Weight
specifier|public
interface|interface
name|Weight
extends|extends
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** The query that this concerns. */
DECL|method|getQuery
name|Query
name|getQuery
parameter_list|()
function_decl|;
comment|/** The weight for this query. */
DECL|method|getValue
name|float
name|getValue
parameter_list|()
function_decl|;
comment|/** The sum of squared weights of contained query clauses. */
DECL|method|sumOfSquaredWeights
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Assigns the query normalization factor to this. */
DECL|method|normalize
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
function_decl|;
comment|/** Constructs a scorer for this. */
DECL|method|scorer
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** An explanation of the score computation for the named document. */
DECL|method|explain
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

