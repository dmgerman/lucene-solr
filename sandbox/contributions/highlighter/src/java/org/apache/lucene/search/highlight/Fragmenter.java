begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
import|;
end_import

begin_comment
comment|/**  * Implements the policy for breaking text into multiple fragments for consideration  * by the {@link Highlighter} class. A sophisticated implementation may do this on the basis  * of detecting end of sentences in the text.   * @author mark@searcharea.co.uk  */
end_comment

begin_interface
DECL|interface|Fragmenter
specifier|public
interface|interface
name|Fragmenter
block|{
comment|/** 	 * Initializes the Fragmenter 	 * @param originalText 	 */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|String
name|originalText
parameter_list|)
function_decl|;
comment|/** 	 * Test to see if this token from the stream should be held in a new TextFragment 	 * @param nextToken 	 * @return 	 */
DECL|method|isNewFragment
specifier|public
name|boolean
name|isNewFragment
parameter_list|(
name|Token
name|nextToken
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

