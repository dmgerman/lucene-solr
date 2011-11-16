begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Methods for manipulating strings.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|StringHelper
specifier|public
specifier|abstract
class|class
name|StringHelper
block|{
comment|/**    * Compares two byte[] arrays, element by element, and returns the    * number of elements common to both arrays.    *    * @param bytes1 The first byte[] to compare    * @param bytes2 The second byte[] to compare    * @return The number of common elements.    */
DECL|method|bytesDifference
specifier|public
specifier|static
name|int
name|bytesDifference
parameter_list|(
name|byte
index|[]
name|bytes1
parameter_list|,
name|int
name|len1
parameter_list|,
name|byte
index|[]
name|bytes2
parameter_list|,
name|int
name|len2
parameter_list|)
block|{
name|int
name|len
init|=
name|len1
operator|<
name|len2
condition|?
name|len1
else|:
name|len2
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|bytes1
index|[
name|i
index|]
operator|!=
name|bytes2
index|[
name|i
index|]
condition|)
return|return
name|i
return|;
return|return
name|len
return|;
block|}
comment|/**    * Compares two byte[] arrays, element by element, and returns the    * number of elements common to both arrays.    *    * @param bytes1 The first byte[] to compare    * @param bytes2 The second byte[] to compare    * @return The number of common elements.    */
DECL|method|bytesDifference
specifier|public
specifier|static
name|int
name|bytesDifference
parameter_list|(
name|byte
index|[]
name|bytes1
parameter_list|,
name|int
name|off1
parameter_list|,
name|int
name|len1
parameter_list|,
name|byte
index|[]
name|bytes2
parameter_list|,
name|int
name|off2
parameter_list|,
name|int
name|len2
parameter_list|)
block|{
name|int
name|len
init|=
name|len1
operator|<
name|len2
condition|?
name|len1
else|:
name|len2
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|bytes1
index|[
name|i
operator|+
name|off1
index|]
operator|!=
name|bytes2
index|[
name|i
operator|+
name|off2
index|]
condition|)
return|return
name|i
return|;
return|return
name|len
return|;
block|}
DECL|method|StringHelper
specifier|private
name|StringHelper
parameter_list|()
block|{   }
comment|/**    * @return a Comparator over versioned strings such as X.YY.Z    * @lucene.internal    */
DECL|method|getVersionComparator
specifier|public
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|getVersionComparator
parameter_list|()
block|{
return|return
name|versionComparator
return|;
block|}
DECL|field|versionComparator
specifier|private
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|versionComparator
init|=
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|a
parameter_list|,
name|String
name|b
parameter_list|)
block|{
name|StringTokenizer
name|aTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|a
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
name|StringTokenizer
name|bTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|b
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
while|while
condition|(
name|aTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|int
name|aToken
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|aTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|int
name|bToken
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|bTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aToken
operator|!=
name|bToken
condition|)
block|{
return|return
name|aToken
operator|<
name|bToken
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
block|}
else|else
block|{
comment|// a has some extra trailing tokens. if these are all zeroes, thats ok.
if|if
condition|(
name|aToken
operator|!=
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
comment|// b has some extra trailing tokens. if these are all zeroes, thats ok.
while|while
condition|(
name|bTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|bTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
operator|!=
literal|0
condition|)
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
DECL|method|equals
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
block|{
return|return
name|s2
operator|==
literal|null
return|;
block|}
else|else
block|{
return|return
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

