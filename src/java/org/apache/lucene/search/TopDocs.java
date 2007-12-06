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

begin_comment
comment|/** Expert: Returned by low-level search implementations.  * @see Searcher#search(Query,Filter,int) */
end_comment

begin_class
DECL|class|TopDocs
specifier|public
class|class
name|TopDocs
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** Expert: The total number of hits for the query.    * @see Hits#length()   */
DECL|field|totalHits
specifier|public
name|int
name|totalHits
decl_stmt|;
comment|/** Expert: The top hits for the query. */
DECL|field|scoreDocs
specifier|public
name|ScoreDoc
index|[]
name|scoreDocs
decl_stmt|;
comment|/** Expert: Stores the maximum score value encountered, needed for normalizing. */
DECL|field|maxScore
specifier|private
name|float
name|maxScore
decl_stmt|;
comment|/** Expert: Returns the maximum score value encountered. */
DECL|method|getMaxScore
specifier|public
name|float
name|getMaxScore
parameter_list|()
block|{
return|return
name|maxScore
return|;
block|}
comment|/** Expert: Sets the maximum score value encountered. */
DECL|method|setMaxScore
specifier|public
name|void
name|setMaxScore
parameter_list|(
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
comment|/** Expert: Constructs a TopDocs.*/
DECL|method|TopDocs
specifier|public
name|TopDocs
parameter_list|(
name|int
name|totalHits
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
name|this
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
block|}
end_class

end_unit

