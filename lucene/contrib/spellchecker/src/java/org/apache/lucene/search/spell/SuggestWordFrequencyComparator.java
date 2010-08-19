begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  Frequency first, then score.  Must have   *  **/
end_comment

begin_class
DECL|class|SuggestWordFrequencyComparator
specifier|public
class|class
name|SuggestWordFrequencyComparator
implements|implements
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|SuggestWord
name|first
parameter_list|,
name|SuggestWord
name|second
parameter_list|)
block|{
comment|// first criteria: the frequency
if|if
condition|(
name|first
operator|.
name|freq
operator|>
name|second
operator|.
name|freq
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|first
operator|.
name|freq
operator|<
name|second
operator|.
name|freq
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// second criteria (if first criteria is equal): the score
if|if
condition|(
name|first
operator|.
name|score
operator|>
name|second
operator|.
name|score
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|first
operator|.
name|score
operator|<
name|second
operator|.
name|score
condition|)
block|{
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
end_class

end_unit

