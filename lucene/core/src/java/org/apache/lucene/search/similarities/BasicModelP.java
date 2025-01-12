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
comment|/**  * Implements the Poisson approximation for the binomial model for DFR.  * @lucene.experimental  *<p>  * WARNING: for terms that do not meet the expected random distribution  * (e.g. stopwords), this model may give poor performance, such as  * abnormally high scores for low tf values.  */
end_comment

begin_class
DECL|class|BasicModelP
specifier|public
class|class
name|BasicModelP
extends|extends
name|BasicModel
block|{
comment|/** {@code log2(Math.E)}, precomputed. */
DECL|field|LOG2_E
specifier|protected
specifier|static
name|double
name|LOG2_E
init|=
name|log2
argument_list|(
name|Math
operator|.
name|E
argument_list|)
decl_stmt|;
comment|/** Sole constructor: parameter-free */
DECL|method|BasicModelP
specifier|public
name|BasicModelP
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
name|float
name|lambda
init|=
call|(
name|float
call|)
argument_list|(
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
operator|+
literal|1
argument_list|)
operator|/
operator|(
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
operator|+
literal|1
operator|)
decl_stmt|;
return|return
call|(
name|float
call|)
argument_list|(
name|tfn
operator|*
name|log2
argument_list|(
name|tfn
operator|/
name|lambda
argument_list|)
operator|+
operator|(
name|lambda
operator|+
literal|1
operator|/
operator|(
literal|12
operator|*
name|tfn
operator|)
operator|-
name|tfn
operator|)
operator|*
name|LOG2_E
operator|+
literal|0.5
operator|*
name|log2
argument_list|(
literal|2
operator|*
name|Math
operator|.
name|PI
operator|*
name|tfn
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
literal|"P"
return|;
block|}
block|}
end_class

end_unit

