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

begin_comment
comment|/** Holds one hit in {@link TopDocs}. */
end_comment

begin_class
DECL|class|ScoreDoc
specifier|public
class|class
name|ScoreDoc
block|{
comment|/** The score of this document for the query. */
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
comment|/** A hit document's number.    * @see IndexSearcher#doc(int) */
DECL|field|doc
specifier|public
name|int
name|doc
decl_stmt|;
comment|/** Only set by {@link TopDocs#merge} */
DECL|field|shardIndex
specifier|public
name|int
name|shardIndex
decl_stmt|;
comment|/** Constructs a ScoreDoc. */
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
argument_list|(
name|doc
argument_list|,
name|score
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a ScoreDoc. */
DECL|method|ScoreDoc
specifier|public
name|ScoreDoc
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|,
name|int
name|shardIndex
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
name|this
operator|.
name|shardIndex
operator|=
name|shardIndex
expr_stmt|;
block|}
comment|// A convenience method for debugging.
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"doc="
operator|+
name|doc
operator|+
literal|" score="
operator|+
name|score
operator|+
literal|" shardIndex="
operator|+
name|shardIndex
return|;
block|}
block|}
end_class

end_unit

