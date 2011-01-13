begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|LinkedHashMap
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
name|analysis
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|PossibilityIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|SpellPossibilityIteratorTest
specifier|public
class|class
name|SpellPossibilityIteratorTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|suggestions
specifier|private
specifier|static
name|Map
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|suggestions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|suggestions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|AYE
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"I"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"II"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"III"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"IV"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"V"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"VI"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"VII"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE
operator|.
name|put
argument_list|(
literal|"VIII"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|BEE
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"alpha"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"beta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"gamma"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"delta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"epsilon"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"zeta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"eta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"theta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BEE
operator|.
name|put
argument_list|(
literal|"iota"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|CEE
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"one"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"three"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"four"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"five"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"six"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"seven"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"eight"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"nine"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|.
name|put
argument_list|(
literal|"ten"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"BEE"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"CEE"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpellPossibilityIterator
specifier|public
name|void
name|testSpellPossibilityIterator
parameter_list|()
throws|throws
name|Exception
block|{
name|PossibilityIterator
name|iter
init|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|(
literal|"Three maps (8*9*10) should return 720 iterations but instead returned "
operator|+
name|count
operator|)
argument_list|,
name|count
operator|==
literal|720
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|remove
argument_list|(
operator|new
name|Token
argument_list|(
literal|"CEE"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|iter
operator|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|(
literal|"Two maps (8*9) should return 72 iterations but instead returned "
operator|+
name|count
operator|)
argument_list|,
name|count
operator|==
literal|72
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|remove
argument_list|(
operator|new
name|Token
argument_list|(
literal|"BEE"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|iter
operator|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|(
literal|"One map of 8 should return 8 iterations but instead returned "
operator|+
name|count
operator|)
argument_list|,
name|count
operator|==
literal|8
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|remove
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|iter
operator|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|(
literal|"No maps should return 0 iterations but instead returned "
operator|+
name|count
operator|)
argument_list|,
name|count
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

