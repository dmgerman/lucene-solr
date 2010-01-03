begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|document
operator|.
name|NumericField
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|NumericRangeQuery
import|;
end_import

begin_comment
comment|// for javadocs
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
name|NumericUtils
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_comment
comment|// do not remove this class in 3.0, it may be needed to decode old indexes!
end_comment

begin_comment
comment|/**  * Provides support for converting longs to Strings, and back again. The strings  * are structured so that lexicographic sorting order is preserved.  *   *<p>  * That is, if l1 is less than l2 for any two longs l1 and l2, then  * NumberTools.longToString(l1) is lexicographically less than  * NumberTools.longToString(l2). (Similarly for "greater than" and "equals".)  *   *<p>  * This class handles<b>all</b> long values (unlike  * {@link org.apache.lucene.document.DateField}).  *   * @deprecated For new indexes use {@link NumericUtils} instead, which  * provides a sortable binary representation (prefix encoded) of numeric  * values.  * To index and efficiently query numeric values use {@link NumericField}  * and {@link NumericRangeQuery}.  * This class is included for use with existing  * indices and will be removed in a future release (possibly Lucene 4.0).  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|NumberTools
specifier|public
class|class
name|NumberTools
block|{
DECL|field|RADIX
specifier|private
specifier|static
specifier|final
name|int
name|RADIX
init|=
literal|36
decl_stmt|;
DECL|field|NEGATIVE_PREFIX
specifier|private
specifier|static
specifier|final
name|char
name|NEGATIVE_PREFIX
init|=
literal|'-'
decl_stmt|;
comment|// NB: NEGATIVE_PREFIX must be< POSITIVE_PREFIX
DECL|field|POSITIVE_PREFIX
specifier|private
specifier|static
specifier|final
name|char
name|POSITIVE_PREFIX
init|=
literal|'0'
decl_stmt|;
comment|//NB: this must be less than
comment|/**      * Equivalent to longToString(Long.MIN_VALUE)      */
DECL|field|MIN_STRING_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|MIN_STRING_VALUE
init|=
name|NEGATIVE_PREFIX
operator|+
literal|"0000000000000"
decl_stmt|;
comment|/**      * Equivalent to longToString(Long.MAX_VALUE)      */
DECL|field|MAX_STRING_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|MAX_STRING_VALUE
init|=
name|POSITIVE_PREFIX
operator|+
literal|"1y2p0ij32e8e7"
decl_stmt|;
comment|/**      * The length of (all) strings returned by {@link #longToString}      */
DECL|field|STR_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|STR_SIZE
init|=
name|MIN_STRING_VALUE
operator|.
name|length
argument_list|()
decl_stmt|;
comment|/**      * Converts a long to a String suitable for indexing.      */
DECL|method|longToString
specifier|public
specifier|static
name|String
name|longToString
parameter_list|(
name|long
name|l
parameter_list|)
block|{
if|if
condition|(
name|l
operator|==
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
comment|// special case, because long is not symmetric around zero
return|return
name|MIN_STRING_VALUE
return|;
block|}
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|STR_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|<
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|NEGATIVE_PREFIX
argument_list|)
expr_stmt|;
name|l
operator|=
name|Long
operator|.
name|MAX_VALUE
operator|+
name|l
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|POSITIVE_PREFIX
argument_list|)
expr_stmt|;
block|}
name|String
name|num
init|=
name|Long
operator|.
name|toString
argument_list|(
name|l
argument_list|,
name|RADIX
argument_list|)
decl_stmt|;
name|int
name|padLen
init|=
name|STR_SIZE
operator|-
name|num
operator|.
name|length
argument_list|()
operator|-
name|buf
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|padLen
operator|--
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|num
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Converts a String that was returned by {@link #longToString} back to a      * long.      *       * @throws IllegalArgumentException      *             if the input is null      * @throws NumberFormatException      *             if the input does not parse (it was not a String returned by      *             longToString()).      */
DECL|method|stringToLong
specifier|public
specifier|static
name|long
name|stringToLong
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"string cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|!=
name|STR_SIZE
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"string is the wrong size"
argument_list|)
throw|;
block|}
if|if
condition|(
name|str
operator|.
name|equals
argument_list|(
name|MIN_STRING_VALUE
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|MIN_VALUE
return|;
block|}
name|char
name|prefix
init|=
name|str
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|l
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|RADIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
name|POSITIVE_PREFIX
condition|)
block|{
comment|// nop
block|}
elseif|else
if|if
condition|(
name|prefix
operator|==
name|NEGATIVE_PREFIX
condition|)
block|{
name|l
operator|=
name|l
operator|-
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"string does not begin with the correct prefix"
argument_list|)
throw|;
block|}
return|return
name|l
return|;
block|}
block|}
end_class

end_unit

