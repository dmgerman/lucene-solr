begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package

begin_comment
comment|/**  * Processes terms found in the original text, typically by applying some form   * of mark-up to highlight terms in HTML search results pages.  *  */
end_comment

begin_interface
DECL|interface|Formatter
specifier|public
interface|interface
name|Formatter
block|{
comment|/**    * @param originalText The section of text being considered for markup    * @param tokenGroup contains one or several overlapping Tokens along with    * their scores and positions.    */
DECL|method|highlightTerm
name|String
name|highlightTerm
parameter_list|(
name|String
name|originalText
parameter_list|,
name|TokenGroup
name|tokenGroup
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

