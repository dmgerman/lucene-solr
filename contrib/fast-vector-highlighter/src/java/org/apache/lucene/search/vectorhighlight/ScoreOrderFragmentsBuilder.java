begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

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
name|List
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
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldFragList
operator|.
name|WeightedFragInfo
import|;
end_import

begin_comment
comment|/**  * An implementation of FragmentsBuilder that outputs score-order fragments.  */
end_comment

begin_class
DECL|class|ScoreOrderFragmentsBuilder
specifier|public
class|class
name|ScoreOrderFragmentsBuilder
extends|extends
name|BaseFragmentsBuilder
block|{
comment|/**    * a constructor.    */
DECL|method|ScoreOrderFragmentsBuilder
specifier|public
name|ScoreOrderFragmentsBuilder
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * a constructor.    *     * @param preTags aray of pre-tags for markup terms.    * @param postTags array of post-tags for markup terms.    */
DECL|method|ScoreOrderFragmentsBuilder
specifier|public
name|ScoreOrderFragmentsBuilder
parameter_list|(
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|)
block|{
name|super
argument_list|(
name|preTags
argument_list|,
name|postTags
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sort by score the list of WeightedFragInfo    */
DECL|method|getWeightedFragInfoList
specifier|public
name|List
argument_list|<
name|WeightedFragInfo
argument_list|>
name|getWeightedFragInfoList
parameter_list|(
name|List
argument_list|<
name|WeightedFragInfo
argument_list|>
name|src
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|src
argument_list|,
operator|new
name|ScoreComparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|src
return|;
block|}
DECL|class|ScoreComparator
specifier|public
specifier|static
class|class
name|ScoreComparator
implements|implements
name|Comparator
argument_list|<
name|WeightedFragInfo
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|WeightedFragInfo
name|o1
parameter_list|,
name|WeightedFragInfo
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|totalBoost
operator|>
name|o2
operator|.
name|totalBoost
condition|)
return|return
operator|-
literal|1
return|;
elseif|else
if|if
condition|(
name|o1
operator|.
name|totalBoost
operator|<
name|o2
operator|.
name|totalBoost
condition|)
return|return
literal|1
return|;
comment|// if same score then check startOffset
else|else
block|{
if|if
condition|(
name|o1
operator|.
name|startOffset
operator|<
name|o2
operator|.
name|startOffset
condition|)
return|return
operator|-
literal|1
return|;
elseif|else
if|if
condition|(
name|o1
operator|.
name|startOffset
operator|>
name|o2
operator|.
name|startOffset
condition|)
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

