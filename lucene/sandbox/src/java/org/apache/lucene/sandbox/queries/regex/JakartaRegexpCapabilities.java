begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.queries.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
operator|.
name|regex
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRefBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|regexp
operator|.
name|CharacterIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|regexp
operator|.
name|RE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|regexp
operator|.
name|REProgram
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_comment
comment|/**  * Implementation tying<a href="http://jakarta.apache.org/regexp">Jakarta  * Regexp</a> to RegexQuery. Jakarta Regexp internally supports a  * {@link RegexCapabilities.RegexMatcher#prefix()} implementation which can offer   * performance gains under certain circumstances. Yet, the implementation appears   * to be rather shaky as it doesn't always provide a prefix even if one would exist.  */
end_comment

begin_class
DECL|class|JakartaRegexpCapabilities
specifier|public
class|class
name|JakartaRegexpCapabilities
implements|implements
name|RegexCapabilities
block|{
DECL|field|prefixField
specifier|private
specifier|static
name|Field
name|prefixField
decl_stmt|;
DECL|field|getPrefixMethod
specifier|private
specifier|static
name|Method
name|getPrefixMethod
decl_stmt|;
static|static
block|{
try|try
block|{
name|getPrefixMethod
operator|=
name|REProgram
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"getPrefix"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getPrefixMethod
operator|=
literal|null
expr_stmt|;
block|}
try|try
block|{
name|prefixField
operator|=
name|REProgram
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"prefix"
argument_list|)
expr_stmt|;
name|prefixField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|prefixField
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// Define the flags that are possible. Redefine them here
comment|// to avoid exposing the RE class to the caller.
DECL|field|flags
specifier|private
name|int
name|flags
init|=
name|RE
operator|.
name|MATCH_NORMAL
decl_stmt|;
comment|/**    * Flag to specify normal, case-sensitive matching behaviour. This is the default.    */
DECL|field|FLAG_MATCH_NORMAL
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_MATCH_NORMAL
init|=
name|RE
operator|.
name|MATCH_NORMAL
decl_stmt|;
comment|/**    * Flag to specify that matching should be case-independent (folded)    */
DECL|field|FLAG_MATCH_CASEINDEPENDENT
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_MATCH_CASEINDEPENDENT
init|=
name|RE
operator|.
name|MATCH_CASEINDEPENDENT
decl_stmt|;
comment|/**    * Constructs a RegexCapabilities with the default MATCH_NORMAL match style.    */
DECL|method|JakartaRegexpCapabilities
specifier|public
name|JakartaRegexpCapabilities
parameter_list|()
block|{}
comment|/**    * Constructs a RegexCapabilities with the provided match flags.    * Multiple flags should be ORed together.    *     * @param flags The matching style    */
DECL|method|JakartaRegexpCapabilities
specifier|public
name|JakartaRegexpCapabilities
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compile
specifier|public
name|RegexCapabilities
operator|.
name|RegexMatcher
name|compile
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
return|return
operator|new
name|JakartaRegexMatcher
argument_list|(
name|regex
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|flags
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|JakartaRegexpCapabilities
name|other
init|=
operator|(
name|JakartaRegexpCapabilities
operator|)
name|obj
decl_stmt|;
return|return
name|flags
operator|==
name|other
operator|.
name|flags
return|;
block|}
DECL|class|JakartaRegexMatcher
class|class
name|JakartaRegexMatcher
implements|implements
name|RegexCapabilities
operator|.
name|RegexMatcher
block|{
DECL|field|regexp
specifier|private
name|RE
name|regexp
decl_stmt|;
DECL|field|utf16
specifier|private
specifier|final
name|CharsRefBuilder
name|utf16
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|field|utf16wrapper
specifier|private
specifier|final
name|CharacterIterator
name|utf16wrapper
init|=
operator|new
name|CharacterIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|utf16
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEnd
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|>=
name|utf16
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|substring
parameter_list|(
name|int
name|beginIndex
parameter_list|)
block|{
return|return
name|substring
argument_list|(
name|beginIndex
argument_list|,
name|utf16
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|substring
parameter_list|(
name|int
name|beginIndex
parameter_list|,
name|int
name|endIndex
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|utf16
operator|.
name|chars
argument_list|()
argument_list|,
name|beginIndex
argument_list|,
name|endIndex
operator|-
name|beginIndex
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|JakartaRegexMatcher
specifier|public
name|JakartaRegexMatcher
parameter_list|(
name|String
name|regex
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|regexp
operator|=
operator|new
name|RE
argument_list|(
name|regex
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|utf16
operator|.
name|copyUTF8Bytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|regexp
operator|.
name|match
argument_list|(
name|utf16wrapper
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prefix
specifier|public
name|String
name|prefix
parameter_list|()
block|{
try|try
block|{
specifier|final
name|char
index|[]
name|prefix
decl_stmt|;
if|if
condition|(
name|getPrefixMethod
operator|!=
literal|null
condition|)
block|{
name|prefix
operator|=
operator|(
name|char
index|[]
operator|)
name|getPrefixMethod
operator|.
name|invoke
argument_list|(
name|regexp
operator|.
name|getProgram
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prefixField
operator|!=
literal|null
condition|)
block|{
name|prefix
operator|=
operator|(
name|char
index|[]
operator|)
name|prefixField
operator|.
name|get
argument_list|(
name|regexp
operator|.
name|getProgram
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
return|return
name|prefix
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|String
argument_list|(
name|prefix
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// if we cannot get the prefix, return none
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

