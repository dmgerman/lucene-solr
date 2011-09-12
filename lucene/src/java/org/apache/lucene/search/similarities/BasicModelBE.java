begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * Limiting form of the Bose-Einstein model. The formula used in Lucene differs  * slightly from the one in the original paper: {@code F} is increased by {@code tfn+1}  * and {@code N} is increased by {@code F}   * @lucene.experimental  */
end_comment

begin_class
DECL|class|BasicModelBE
specifier|public
class|class
name|BasicModelBE
extends|extends
name|BasicModel
block|{
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
name|double
name|F
init|=
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
operator|+
literal|1
operator|+
name|tfn
decl_stmt|;
comment|// approximation only holds true when F<< N, so we use N += F
name|double
name|N
init|=
name|F
operator|+
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
decl_stmt|;
return|return
call|(
name|float
call|)
argument_list|(
operator|-
name|log2
argument_list|(
operator|(
name|N
operator|-
literal|1
operator|)
operator|*
name|Math
operator|.
name|E
argument_list|)
operator|+
name|f
argument_list|(
name|N
operator|+
name|F
operator|-
literal|1
argument_list|,
name|N
operator|+
name|F
operator|-
name|tfn
operator|-
literal|2
argument_list|)
operator|-
name|f
argument_list|(
name|F
argument_list|,
name|F
operator|-
name|tfn
argument_list|)
argument_list|)
return|;
block|}
comment|/** The<em>f</em> helper function defined for<em>B<sub>E</sub></em>. */
DECL|method|f
specifier|private
specifier|final
name|double
name|f
parameter_list|(
name|double
name|n
parameter_list|,
name|double
name|m
parameter_list|)
block|{
return|return
operator|(
name|m
operator|+
literal|0.5
operator|)
operator|*
name|log2
argument_list|(
name|n
operator|/
name|m
argument_list|)
operator|+
operator|(
name|n
operator|-
name|m
operator|)
operator|*
name|log2
argument_list|(
name|n
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
literal|"Be"
return|;
block|}
block|}
end_class

end_unit

