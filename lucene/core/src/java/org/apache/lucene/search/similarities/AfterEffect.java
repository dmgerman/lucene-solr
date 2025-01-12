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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
import|;
end_import

begin_comment
comment|/**  * This class acts as the base class for the implementations of the<em>first  * normalization of the informative content</em> in the DFR framework. This  * component is also called the<em>after effect</em> and is defined by the  * formula<em>Inf<sub>2</sub> = 1 - Prob<sub>2</sub></em>, where  *<em>Prob<sub>2</sub></em> measures the<em>information gain</em>.  *   * @see DFRSimilarity  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AfterEffect
specifier|public
specifier|abstract
class|class
name|AfterEffect
block|{
comment|/**    * Sole constructor. (For invocation by subclass     * constructors, typically implicit.)    */
DECL|method|AfterEffect
specifier|public
name|AfterEffect
parameter_list|()
block|{}
comment|/** Returns the aftereffect score. */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
function_decl|;
comment|/** Returns an explanation for the score. */
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
function_decl|;
comment|/** Implementation used when there is no aftereffect. */
DECL|class|NoAfterEffect
specifier|public
specifier|static
specifier|final
class|class
name|NoAfterEffect
extends|extends
name|AfterEffect
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|NoAfterEffect
specifier|public
name|NoAfterEffect
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
return|return
literal|1f
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
specifier|final
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
literal|1
argument_list|,
literal|"no aftereffect"
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
literal|""
return|;
block|}
block|}
comment|/**    * Subclasses must override this method to return the code of the    * after effect formula. Refer to the original paper for the list.     */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_class

end_unit

