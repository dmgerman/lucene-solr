begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.compound.hyphenation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
operator|.
name|hyphenation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * This interface is used to connect the XML pattern file parser to the  * hyphenation tree.  *   * This class has been taken from the Apache FOP project (http://xmlgraphics.apache.org/fop/). They have been slightly modified.  */
end_comment

begin_interface
DECL|interface|PatternConsumer
specifier|public
interface|interface
name|PatternConsumer
block|{
comment|/**    * Add a character class. A character class defines characters that are    * considered equivalent for the purpose of hyphenation (e.g. "aA"). It    * usually means to ignore case.    *     * @param chargroup character group    */
DECL|method|addClass
name|void
name|addClass
parameter_list|(
name|String
name|chargroup
parameter_list|)
function_decl|;
comment|/**    * Add a hyphenation exception. An exception replaces the result obtained by    * the algorithm for cases for which this fails or the user wants to provide    * his own hyphenation. A hyphenatedword is a vector of alternating String's    * and {@link Hyphen Hyphen} instances    */
DECL|method|addException
name|void
name|addException
parameter_list|(
name|String
name|word
parameter_list|,
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|hyphenatedword
parameter_list|)
function_decl|;
comment|/**    * Add hyphenation patterns.    *     * @param pattern the pattern    * @param values interletter values expressed as a string of digit characters.    */
DECL|method|addPattern
name|void
name|addPattern
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
name|values
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

