begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestMultiset
specifier|public
class|class
name|TestMultiset
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDuplicatesMatter
specifier|public
name|void
name|testDuplicatesMatter
parameter_list|()
block|{
name|Multiset
argument_list|<
name|Integer
argument_list|>
name|s1
init|=
operator|new
name|Multiset
argument_list|<>
argument_list|()
decl_stmt|;
name|Multiset
argument_list|<
name|Integer
argument_list|>
name|s2
init|=
operator|new
name|Multiset
argument_list|<>
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
operator|.
name|size
argument_list|()
argument_list|,
name|s2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s1
operator|.
name|add
argument_list|(
literal|42
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s2
operator|.
name|add
argument_list|(
literal|42
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|s2
operator|.
name|add
argument_list|(
literal|42
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
name|s1
operator|.
name|add
argument_list|(
literal|43
argument_list|)
expr_stmt|;
name|s1
operator|.
name|add
argument_list|(
literal|43
argument_list|)
expr_stmt|;
name|s2
operator|.
name|add
argument_list|(
literal|43
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s1
operator|.
name|size
argument_list|()
argument_list|,
name|s2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toCountMap
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Map
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
name|toCountMap
parameter_list|(
name|Multiset
argument_list|<
name|T
argument_list|>
name|set
parameter_list|)
block|{
name|Map
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|recomputedSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|element
range|:
name|set
control|)
block|{
name|add
argument_list|(
name|map
argument_list|,
name|element
argument_list|)
expr_stmt|;
name|recomputedSize
operator|+=
literal|1
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|set
operator|.
name|toString
argument_list|()
argument_list|,
name|recomputedSize
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
DECL|method|add
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|add
parameter_list|(
name|Map
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
name|map
parameter_list|,
name|T
name|element
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|element
argument_list|,
name|map
operator|.
name|getOrDefault
argument_list|(
name|element
argument_list|,
literal|0
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|remove
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|remove
parameter_list|(
name|Map
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
name|map
parameter_list|,
name|T
name|element
parameter_list|)
block|{
name|Integer
name|count
init|=
name|map
operator|.
name|get
argument_list|(
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|count
operator|.
name|intValue
argument_list|()
operator|==
literal|1
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|element
argument_list|,
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|reference
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Multiset
argument_list|<
name|Integer
argument_list|>
name|multiset
init|=
operator|new
name|Multiset
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
name|remove
argument_list|(
name|reference
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|multiset
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|reference
operator|.
name|clear
argument_list|()
expr_stmt|;
name|multiset
operator|.
name|clear
argument_list|()
expr_stmt|;
break|break;
default|default:
name|add
argument_list|(
name|reference
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|multiset
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
name|assertEquals
argument_list|(
name|reference
argument_list|,
name|toCountMap
argument_list|(
name|multiset
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

