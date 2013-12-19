begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
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
name|List
import|;
end_import

begin_class
DECL|class|MedianCalculator
specifier|public
class|class
name|MedianCalculator
block|{
comment|/**    * Calculates the median of the given list of numbers.    *    * @param list A list of {@link Comparable} {@link Number} objects    * @return The median of the given list as a double.    */
DECL|method|getMedian
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Number
operator|&
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|double
name|getMedian
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
name|int
name|size
init|=
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|0
return|;
block|}
name|select
argument_list|(
name|list
argument_list|,
literal|.5
operator|*
name|size
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|int
name|firstIdx
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|floor
argument_list|(
literal|.5
operator|*
name|size
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|secondIdx
init|=
operator|(
name|firstIdx
operator|<=
name|size
operator|&&
name|size
operator|%
literal|2
operator|==
literal|1
operator|)
condition|?
name|firstIdx
operator|+
literal|1
else|:
name|firstIdx
decl_stmt|;
name|double
name|result
init|=
name|list
operator|.
name|get
argument_list|(
name|firstIdx
argument_list|)
operator|.
name|doubleValue
argument_list|()
operator|*
literal|.5
operator|+
name|list
operator|.
name|get
argument_list|(
name|secondIdx
argument_list|)
operator|.
name|doubleValue
argument_list|()
operator|*
literal|.5
decl_stmt|;
return|return
name|result
return|;
block|}
DECL|method|select
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|void
name|select
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|double
name|place
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|T
name|split
decl_stmt|;
if|if
condition|(
name|end
operator|-
name|begin
operator|<
literal|10
condition|)
block|{
name|split
operator|=
name|list
operator|.
name|get
argument_list|(
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
operator|(
name|end
operator|-
name|begin
operator|+
literal|1
operator|)
argument_list|)
operator|+
name|begin
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|split
operator|=
name|split
argument_list|(
name|list
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
name|Point
name|result
init|=
name|partition
argument_list|(
name|list
argument_list|,
name|begin
argument_list|,
name|end
argument_list|,
name|split
argument_list|)
decl_stmt|;
if|if
condition|(
name|place
operator|<
name|result
operator|.
name|low
condition|)
block|{
name|select
argument_list|(
name|list
argument_list|,
name|place
argument_list|,
name|begin
argument_list|,
name|result
operator|.
name|low
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|place
operator|>
name|result
operator|.
name|high
condition|)
block|{
name|select
argument_list|(
name|list
argument_list|,
name|place
argument_list|,
name|result
operator|.
name|high
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|result
operator|.
name|low
operator|==
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|floor
argument_list|(
name|place
argument_list|)
argument_list|)
operator|&&
name|result
operator|.
name|low
operator|>
name|begin
condition|)
block|{
name|select
argument_list|(
name|list
argument_list|,
name|result
operator|.
name|low
argument_list|,
name|begin
argument_list|,
name|result
operator|.
name|low
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|high
operator|==
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|place
argument_list|)
argument_list|)
operator|&&
name|result
operator|.
name|high
operator|<
name|end
condition|)
block|{
name|select
argument_list|(
name|list
argument_list|,
name|result
operator|.
name|high
argument_list|,
name|result
operator|.
name|high
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|split
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|T
name|split
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|T
name|temp
decl_stmt|;
name|int
name|num
init|=
operator|(
name|end
operator|-
name|begin
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|recursiveSize
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
name|double
operator|)
name|num
argument_list|)
decl_stmt|;
name|int
name|step
init|=
name|num
operator|/
name|recursiveSize
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|recursiveSize
condition|;
name|i
operator|++
control|)
block|{
name|int
name|swapFrom
init|=
name|i
operator|*
name|step
operator|+
name|begin
decl_stmt|;
name|int
name|swapTo
init|=
name|i
operator|+
name|begin
decl_stmt|;
name|temp
operator|=
name|list
operator|.
name|get
argument_list|(
name|swapFrom
argument_list|)
expr_stmt|;
name|list
operator|.
name|set
argument_list|(
name|swapFrom
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|swapTo
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|set
argument_list|(
name|swapTo
argument_list|,
name|temp
argument_list|)
expr_stmt|;
block|}
name|recursiveSize
operator|--
expr_stmt|;
name|select
argument_list|(
name|list
argument_list|,
name|recursiveSize
operator|/
literal|2
operator|+
name|begin
argument_list|,
name|begin
argument_list|,
name|recursiveSize
operator|+
name|begin
argument_list|)
expr_stmt|;
return|return
name|list
operator|.
name|get
argument_list|(
name|recursiveSize
operator|/
literal|2
operator|+
name|begin
argument_list|)
return|;
block|}
DECL|method|partition
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|Point
name|partition
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|,
name|T
name|indexElement
parameter_list|)
block|{
name|T
name|temp
decl_stmt|;
name|int
name|left
decl_stmt|,
name|right
decl_stmt|;
for|for
control|(
name|left
operator|=
name|begin
operator|,
name|right
operator|=
name|end
init|;
name|left
operator|<
name|right
condition|;
name|left
operator|++
operator|,
name|right
operator|--
control|)
block|{
while|while
condition|(
name|list
operator|.
name|get
argument_list|(
name|left
argument_list|)
operator|.
name|compareTo
argument_list|(
name|indexElement
argument_list|)
operator|<
literal|0
condition|)
block|{
name|left
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|right
operator|!=
name|begin
operator|-
literal|1
operator|&&
name|list
operator|.
name|get
argument_list|(
name|right
argument_list|)
operator|.
name|compareTo
argument_list|(
name|indexElement
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|right
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|right
operator|<=
name|left
condition|)
block|{
name|left
operator|--
expr_stmt|;
name|right
operator|++
expr_stmt|;
break|break;
block|}
name|temp
operator|=
name|list
operator|.
name|get
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|list
operator|.
name|set
argument_list|(
name|left
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|right
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|set
argument_list|(
name|right
argument_list|,
name|temp
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|left
operator|!=
name|begin
operator|-
literal|1
operator|&&
name|list
operator|.
name|get
argument_list|(
name|left
argument_list|)
operator|.
name|compareTo
argument_list|(
name|indexElement
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|left
operator|--
expr_stmt|;
block|}
while|while
condition|(
name|right
operator|!=
name|end
operator|+
literal|1
operator|&&
name|list
operator|.
name|get
argument_list|(
name|right
argument_list|)
operator|.
name|compareTo
argument_list|(
name|indexElement
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|right
operator|++
expr_stmt|;
block|}
name|int
name|rightMove
init|=
name|right
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|rightMove
operator|<
name|end
operator|+
literal|1
condition|)
block|{
if|if
condition|(
name|list
operator|.
name|get
argument_list|(
name|rightMove
argument_list|)
operator|.
name|equals
argument_list|(
name|indexElement
argument_list|)
condition|)
block|{
name|temp
operator|=
name|list
operator|.
name|get
argument_list|(
name|rightMove
argument_list|)
expr_stmt|;
name|list
operator|.
name|set
argument_list|(
name|rightMove
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|right
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|set
argument_list|(
name|right
argument_list|,
name|temp
argument_list|)
expr_stmt|;
do|do
block|{
name|right
operator|++
expr_stmt|;
block|}
do|while
condition|(
name|list
operator|.
name|get
argument_list|(
name|right
argument_list|)
operator|.
name|equals
argument_list|(
name|indexElement
argument_list|)
condition|)
do|;
if|if
condition|(
name|rightMove
operator|<=
name|right
condition|)
block|{
name|rightMove
operator|=
name|right
expr_stmt|;
block|}
block|}
name|rightMove
operator|++
expr_stmt|;
block|}
return|return
operator|new
name|Point
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
return|;
block|}
block|}
end_class

end_unit

