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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Expert: Constructs a TopDocs.*/
DECL|method|TopDocs
name|TopDocs
parameter_list|(
name|int
name|totalHits
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
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
block|}
block|}
end_class

end_unit

