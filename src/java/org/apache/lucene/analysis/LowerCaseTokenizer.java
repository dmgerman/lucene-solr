begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * LowerCaseTokenizer performs the function of LetterTokenizer  * and LowerCaseFilter together.  It divides text at non-letters and converts  * them to lower case.  While it is functionally equivalent to the combination  * of LetterTokenizer and LowerCaseFilter, there is a performance advantage  * to doing the two tasks at once, hence this (redundant) implementation.  *<P>  * Note: this does a decent job for most European languages, but does a terrible  * job for some Asian languages, where words are not separated by spaces.  */
end_comment

begin_class
DECL|class|LowerCaseTokenizer
specifier|public
specifier|final
class|class
name|LowerCaseTokenizer
extends|extends
name|LetterTokenizer
block|{
comment|/** Construct a new LowerCaseTokenizer. */
DECL|method|LowerCaseTokenizer
specifier|public
name|LowerCaseTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/** Collects only characters which satisfy    * {@link Character#isLetter(char)}.*/
DECL|method|normalize
specifier|protected
name|char
name|normalize
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|toLowerCase
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
end_class

end_unit

