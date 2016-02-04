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
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * Implements the policy for breaking text into multiple fragments for  * consideration by the {@link Highlighter} class. A sophisticated  * implementation may do this on the basis of detecting end of sentences in the  * text.  */
end_comment

begin_interface
DECL|interface|Fragmenter
specifier|public
interface|interface
name|Fragmenter
block|{
comment|/**    * Initializes the Fragmenter. You can grab references to the Attributes you are    * interested in from tokenStream and then access the values in {@link #isNewFragment()}.    *     * @param originalText the original source text    * @param tokenStream the {@link TokenStream} to be fragmented    */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|String
name|originalText
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
function_decl|;
comment|/**    * Test to see if this token from the stream should be held in a new    * TextFragment. Every time this is called, the TokenStream    * passed to start(String, TokenStream) will have been incremented.    *     */
DECL|method|isNewFragment
specifier|public
name|boolean
name|isNewFragment
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

