begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Some commonly-used stemming functions */
end_comment

begin_class
DECL|class|StemmerUtil
specifier|public
class|class
name|StemmerUtil
block|{
comment|/**    * Returns true if the character array starts with the suffix.    *     * @param s Input Buffer    * @param len length of input buffer    * @param prefix Prefix string to test    * @return true if<code>s</code> starts with<code>prefix</code>    */
DECL|method|startsWith
specifier|public
specifier|static
name|boolean
name|startsWith
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
specifier|final
name|int
name|prefixLen
init|=
name|prefix
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefixLen
operator|>
name|len
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefixLen
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|s
index|[
name|i
index|]
operator|!=
name|prefix
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/**    * Returns true if the character array ends with the suffix.    *     * @param s Input Buffer    * @param len length of input buffer    * @param suffix Suffix string to test    * @return true if<code>s</code> ends with<code>suffix</code>    */
DECL|method|endsWith
specifier|public
specifier|static
name|boolean
name|endsWith
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
specifier|final
name|int
name|suffixLen
init|=
name|suffix
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|suffixLen
operator|>
name|len
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
name|suffixLen
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
if|if
condition|(
name|s
index|[
name|len
operator|-
operator|(
name|suffixLen
operator|-
name|i
operator|)
index|]
operator|!=
name|suffix
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/**    * Delete a character in-place    *     * @param s Input Buffer    * @param pos Position of character to delete    * @param len length of input buffer    * @return length of input buffer after deletion    */
DECL|method|delete
specifier|public
specifier|static
name|int
name|delete
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
name|len
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|s
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|s
argument_list|,
name|pos
argument_list|,
name|len
operator|-
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|len
operator|-
literal|1
return|;
block|}
comment|/**    * Delete n characters in-place    *     * @param s Input Buffer    * @param pos Position of character to delete    * @param len Length of input buffer    * @param nChars number of characters to delete    * @return length of input buffer after deletion    */
DECL|method|deleteN
specifier|public
specifier|static
name|int
name|deleteN
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|nChars
parameter_list|)
block|{
comment|// TODO: speed up, this is silly
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nChars
condition|;
name|i
operator|++
control|)
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

