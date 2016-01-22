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

begin_comment
comment|/**  * Computes the measure of divergence from independence for DFI  * scoring functions.  *<p>  * See http://trec.nist.gov/pubs/trec21/papers/irra.web.nb.pdf for more information  * on different methods.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Independence
specifier|public
specifier|abstract
class|class
name|Independence
block|{
comment|/**    * Sole constructor. (For invocation by subclass     * constructors, typically implicit.)    */
DECL|method|Independence
specifier|public
name|Independence
parameter_list|()
block|{}
comment|/**    * Computes distance from independence    * @param freq actual term frequency    * @param expected expected term frequency    */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|(
name|float
name|freq
parameter_list|,
name|float
name|expected
parameter_list|)
function_decl|;
comment|// subclasses must provide a name
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

