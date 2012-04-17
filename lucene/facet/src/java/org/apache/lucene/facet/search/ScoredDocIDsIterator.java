begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
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
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Iterator over document IDs and their scores. Each {@link #next()} retrieves  * the next docID and its score which can be later be retrieved by  * {@link #getDocID()} and {@link #getScore()}.<b>NOTE:</b> you must call  * {@link #next()} before {@link #getDocID()} and/or {@link #getScore()}, or  * otherwise the returned values are unexpected.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|ScoredDocIDsIterator
specifier|public
interface|interface
name|ScoredDocIDsIterator
block|{
comment|/** Default score used in case scoring is disabled. */
DECL|field|DEFAULT_SCORE
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_SCORE
init|=
literal|1.0f
decl_stmt|;
comment|/** Iterate to the next document/score pair. Returns true iff there is such a pair. */
DECL|method|next
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
function_decl|;
comment|/** Returns the ID of the current document. */
DECL|method|getDocID
specifier|public
specifier|abstract
name|int
name|getDocID
parameter_list|()
function_decl|;
comment|/** Returns the score of the current document. */
DECL|method|getScore
specifier|public
specifier|abstract
name|float
name|getScore
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

