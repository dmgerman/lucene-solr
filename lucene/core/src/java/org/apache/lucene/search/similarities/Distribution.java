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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * The probabilistic distribution used to model term occurrence  * in information-based models.  * @see IBSimilarity  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Distribution
specifier|public
specifier|abstract
class|class
name|Distribution
block|{
comment|/**    * Sole constructor. (For invocation by subclass     * constructors, typically implicit.)    */
DECL|method|Distribution
specifier|public
name|Distribution
parameter_list|()
block|{}
comment|/** Computes the score. */
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
parameter_list|,
name|float
name|lambda
parameter_list|)
function_decl|;
comment|/** Explains the score. Returns the name of the model only, since    * both {@code tfn} and {@code lambda} are explained elsewhere. */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|,
name|float
name|lambda
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|(
name|stats
argument_list|,
name|tfn
argument_list|,
name|lambda
argument_list|)
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Subclasses must override this method to return the name of the    * distribution.     */
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

