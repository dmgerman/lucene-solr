begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|SimilarityBase
operator|.
name|log2
import|;
end_import

begin_comment
comment|/**  * Geometric as limiting form of the Bose-Einstein model.  The formula used in Lucene differs  * slightly from the one in the original paper: {@code F} is increased by {@code 1}  * and {@code N} is increased by {@code F}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BasicModelG
specifier|public
class|class
name|BasicModelG
extends|extends
name|BasicModel
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|BasicModelG
specifier|public
name|BasicModelG
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
block|{
comment|// just like in BE, approximation only holds true when F<< N, so we use lambda = F / (N + F)
name|double
name|F
init|=
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
operator|+
literal|1
decl_stmt|;
name|double
name|N
init|=
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
decl_stmt|;
name|double
name|lambda
init|=
name|F
operator|/
operator|(
name|N
operator|+
name|F
operator|)
decl_stmt|;
comment|// -log(1 / (lambda + 1)) -> log(lambda + 1)
return|return
call|(
name|float
call|)
argument_list|(
name|log2
argument_list|(
name|lambda
operator|+
literal|1
argument_list|)
operator|+
name|tfn
operator|*
name|log2
argument_list|(
operator|(
literal|1
operator|+
name|lambda
operator|)
operator|/
name|lambda
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"G"
return|;
block|}
block|}
end_class

end_unit

