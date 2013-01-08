begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.aggregator
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
operator|.
name|aggregator
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A CountingAggregator updates a counter array with the size of the whole  * taxonomy, counting the number of times each category appears in the given set  * of documents.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CountingAggregator
specifier|public
class|class
name|CountingAggregator
implements|implements
name|Aggregator
block|{
DECL|field|counterArray
specifier|protected
name|int
index|[]
name|counterArray
decl_stmt|;
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
operator|++
name|counterArray
index|[
name|ordinal
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextDoc
specifier|public
name|void
name|setNextDoc
parameter_list|(
name|int
name|docid
parameter_list|,
name|float
name|score
parameter_list|)
block|{
comment|// There's nothing for us to do here since we only increment the count by 1
comment|// in this aggregator.
block|}
DECL|method|CountingAggregator
specifier|public
name|CountingAggregator
parameter_list|(
name|int
index|[]
name|counterArray
parameter_list|)
block|{
name|this
operator|.
name|counterArray
operator|=
name|counterArray
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CountingAggregator
name|that
init|=
operator|(
name|CountingAggregator
operator|)
name|obj
decl_stmt|;
return|return
name|that
operator|.
name|counterArray
operator|==
name|this
operator|.
name|counterArray
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hashCode
init|=
name|counterArray
operator|==
literal|null
condition|?
literal|0
else|:
name|counterArray
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
name|hashCode
return|;
block|}
block|}
end_class

end_unit

