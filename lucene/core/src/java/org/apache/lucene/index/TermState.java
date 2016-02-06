begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Encapsulates all required internal state to position the associated  * {@link TermsEnum} without re-seeking.  *   * @see TermsEnum#seekExact(org.apache.lucene.util.BytesRef, TermState)  * @see TermsEnum#termState()  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TermState
specifier|public
specifier|abstract
class|class
name|TermState
implements|implements
name|Cloneable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|TermState
specifier|protected
name|TermState
parameter_list|()
block|{   }
comment|/**    * Copies the content of the given {@link TermState} to this instance    *     * @param other    *          the TermState to copy    */
DECL|method|copyFrom
specifier|public
specifier|abstract
name|void
name|copyFrom
parameter_list|(
name|TermState
name|other
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|clone
specifier|public
name|TermState
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|TermState
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|cnse
parameter_list|)
block|{
comment|// should not happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|cnse
argument_list|)
throw|;
block|}
block|}
comment|/** Returns true if this term is real (e.g., not an auto-prefix term).    *  @lucene.internal */
DECL|method|isRealTerm
specifier|public
name|boolean
name|isRealTerm
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TermState"
return|;
block|}
block|}
end_class

end_unit

