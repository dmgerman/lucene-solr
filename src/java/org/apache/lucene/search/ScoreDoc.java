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
comment|/** Expert: Returned by low-level search implementations.  * @see TopDocs */
end_comment

begin_class
DECL|class|ScoreDoc
specifier|public
class|class
name|ScoreDoc
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** Expert: The score of this document for the query. */
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
comment|/** Expert: A hit document's number.    * @see Searcher#doc(int)    */
DECL|field|doc
specifier|public
name|int
name|doc
decl_stmt|;
comment|/** Expert: Constructs a ScoreDoc. */
DECL|method|ScoreDoc
specifier|public
name|ScoreDoc
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
block|}
end_class

end_unit

