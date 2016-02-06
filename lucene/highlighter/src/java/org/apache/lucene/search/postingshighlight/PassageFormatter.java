begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
package|;
end_package

begin_comment
comment|/**  * Creates a formatted snippet from the top passages.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PassageFormatter
specifier|public
specifier|abstract
class|class
name|PassageFormatter
block|{
comment|/**    * Formats the top<code>passages</code> from<code>content</code>    * into a human-readable text snippet.    *    * @param passages top-N passages for the field. Note these are sorted in    *        the order that they appear in the document for convenience.    * @param content content for the field.    * @return formatted highlight.  Note that for the    * non-expert APIs in {@link PostingsHighlighter} that    * return String, the toString method on the Object    * returned by this method is used to compute the string.    */
DECL|method|format
specifier|public
specifier|abstract
name|Object
name|format
parameter_list|(
name|Passage
name|passages
index|[]
parameter_list|,
name|String
name|content
parameter_list|)
function_decl|;
block|}
end_class

end_unit

