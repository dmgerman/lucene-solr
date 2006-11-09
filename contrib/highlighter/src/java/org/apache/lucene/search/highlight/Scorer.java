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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Adds to the score for a fragment based on its tokens  * @author mark@searcharea.co.uk  */
end_comment

begin_interface
DECL|interface|Scorer
specifier|public
interface|interface
name|Scorer
block|{
comment|/** 	 * called when a new fragment is started for consideration 	 * @param newFragment 	 */
DECL|method|startFragment
specifier|public
name|void
name|startFragment
parameter_list|(
name|TextFragment
name|newFragment
parameter_list|)
function_decl|;
comment|/** 	 * Called for each token in the current fragment 	 * @param token The token to be scored 	 * @return a score which is passed to the Highlighter class to influence the mark-up of the text 	 * (this return value is NOT used to score the fragment) 	 */
DECL|method|getTokenScore
specifier|public
name|float
name|getTokenScore
parameter_list|(
name|Token
name|token
parameter_list|)
function_decl|;
comment|/** 	 * Called when the highlighter has no more tokens for the current fragment - the scorer returns 	 * the weighting it has derived for the most recent fragment, typically based on the tokens 	 * passed to getTokenScore().  	 * 	 */
DECL|method|getFragmentScore
specifier|public
name|float
name|getFragmentScore
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

