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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** A LetterTokenizer is a tokenizer that divides text at non-letters.  That's   to say, it defines tokens as maximal strings of adjacent letters, as defined   by java.lang.Character.isLetter() predicate.    Note: this does a decent job for most European languages, but does a terrible   job for some Asian languages, where words are not separated by spaces. */
end_comment

begin_class
DECL|class|LetterTokenizer
specifier|public
class|class
name|LetterTokenizer
extends|extends
name|CharTokenizer
block|{
comment|/** Construct a new LetterTokenizer. */
DECL|method|LetterTokenizer
specifier|public
name|LetterTokenizer
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
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
end_class

end_unit

