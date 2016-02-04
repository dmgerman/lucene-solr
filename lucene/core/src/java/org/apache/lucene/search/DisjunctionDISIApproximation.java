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
comment|/**  * A {@link DocIdSetIterator} which is a disjunction of the approximations of  * the provided iterators.  * @lucene.internal  */
end_comment

begin_class
DECL|class|DisjunctionDISIApproximation
specifier|public
class|class
name|DisjunctionDISIApproximation
extends|extends
name|DocIdSetIterator
block|{
DECL|field|subIterators
specifier|final
name|DisiPriorityQueue
name|subIterators
decl_stmt|;
DECL|field|cost
specifier|final
name|long
name|cost
decl_stmt|;
DECL|method|DisjunctionDISIApproximation
specifier|public
name|DisjunctionDISIApproximation
parameter_list|(
name|DisiPriorityQueue
name|subIterators
parameter_list|)
block|{
name|this
operator|.
name|subIterators
operator|=
name|subIterators
expr_stmt|;
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DisiWrapper
name|w
range|:
name|subIterators
control|)
block|{
name|cost
operator|+=
name|w
operator|.
name|cost
expr_stmt|;
block|}
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|subIterators
operator|.
name|top
argument_list|()
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|DisiWrapper
name|top
init|=
name|subIterators
operator|.
name|top
argument_list|()
decl_stmt|;
specifier|final
name|int
name|doc
init|=
name|top
operator|.
name|doc
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|approximation
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|top
operator|=
name|subIterators
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|==
name|doc
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|DisiWrapper
name|top
init|=
name|subIterators
operator|.
name|top
argument_list|()
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|approximation
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|top
operator|=
name|subIterators
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|<
name|target
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
block|}
end_class

end_unit

