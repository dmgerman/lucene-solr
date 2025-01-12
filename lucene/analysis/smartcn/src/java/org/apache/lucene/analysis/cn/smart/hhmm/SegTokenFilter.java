begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
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
name|cn
operator|.
name|smart
operator|.
name|Utility
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
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|WordType
import|;
end_import

begin_comment
comment|/**  *<p>  * Filters a {@link SegToken} by converting full-width latin to half-width, then lowercasing latin.  * Additionally, all punctuation is converted into {@link Utility#COMMON_DELIMITER}  *</p>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegTokenFilter
specifier|public
class|class
name|SegTokenFilter
block|{
comment|/**    * Filter an input {@link SegToken}    *<p>    * Full-width latin will be converted to half-width, then all latin will be lowercased.    * All punctuation is converted into {@link Utility#COMMON_DELIMITER}    *</p>    *     * @param token input {@link SegToken}    * @return normalized {@link SegToken}    */
DECL|method|filter
specifier|public
name|SegToken
name|filter
parameter_list|(
name|SegToken
name|token
parameter_list|)
block|{
switch|switch
condition|(
name|token
operator|.
name|wordType
condition|)
block|{
case|case
name|WordType
operator|.
name|FULLWIDTH_NUMBER
case|:
case|case
name|WordType
operator|.
name|FULLWIDTH_STRING
case|:
comment|/* first convert full-width -> half-width */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|token
operator|.
name|charArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|>=
literal|0xFF10
condition|)
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|-=
literal|0xFEE0
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|>=
literal|0x0041
operator|&&
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|<=
literal|0x005A
condition|)
comment|/* lowercase latin */
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|+=
literal|0x0020
expr_stmt|;
block|}
break|break;
case|case
name|WordType
operator|.
name|STRING
case|:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|token
operator|.
name|charArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|>=
literal|0x0041
operator|&&
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|<=
literal|0x005A
condition|)
comment|/* lowercase latin */
name|token
operator|.
name|charArray
index|[
name|i
index|]
operator|+=
literal|0x0020
expr_stmt|;
block|}
break|break;
case|case
name|WordType
operator|.
name|DELIMITER
case|:
comment|/* convert all punctuation to Utility.COMMON_DELIMITER */
name|token
operator|.
name|charArray
operator|=
name|Utility
operator|.
name|COMMON_DELIMITER
expr_stmt|;
break|break;
default|default:
break|break;
block|}
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

