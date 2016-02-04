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
comment|/**  * The<em>lambda (&lambda;<sub>w</sub>)</em> parameter in information-based  * models.  * @see IBSimilarity  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lambda
specifier|public
specifier|abstract
class|class
name|Lambda
block|{
comment|/**    * Sole constructor. (For invocation by subclass     * constructors, typically implicit.)    */
DECL|method|Lambda
specifier|public
name|Lambda
parameter_list|()
block|{}
comment|/** Computes the lambda parameter. */
DECL|method|lambda
specifier|public
specifier|abstract
name|float
name|lambda
parameter_list|(
name|BasicStats
name|stats
parameter_list|)
function_decl|;
comment|/** Explains the lambda parameter. */
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|)
function_decl|;
comment|/**    * Subclasses must override this method to return the code of the lambda    * formula. Since the original paper is not very clear on this matter, and    * also uses the DFR naming scheme incorrectly, the codes here were chosen    * arbitrarily.    */
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

