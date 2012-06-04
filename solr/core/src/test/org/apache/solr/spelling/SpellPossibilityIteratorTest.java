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
name|HashSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
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
DECL|field|TOKEN_AYE
specifier|private
specifier|static
specifier|final
name|Token
name|TOKEN_AYE
init|=
operator|new
name|Token
argument_list|(
literal|"AYE"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
DECL|field|TOKEN_BEE
specifier|private
specifier|static
specifier|final
name|Token
name|TOKEN_BEE
init|=
operator|new
name|Token
argument_list|(
literal|"BEE"
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
decl_stmt|;
DECL|field|TOKEN_AYE_BEE
specifier|private
specifier|static
specifier|final
name|Token
name|TOKEN_AYE_BEE
init|=
operator|new
name|Token
argument_list|(
literal|"AYE BEE"
argument_list|,
literal|0
argument_list|,
literal|7
argument_list|)
decl_stmt|;
DECL|field|TOKEN_CEE
specifier|private
specifier|static
specifier|final
name|Token
name|TOKEN_CEE
init|=
operator|new
name|Token
argument_list|(
literal|"CEE"
argument_list|,
literal|8
argument_list|,
literal|11
argument_list|)
decl_stmt|;
DECL|field|AYE
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|AYE
decl_stmt|;
DECL|field|BEE
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|BEE
decl_stmt|;
DECL|field|AYE_BEE
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|AYE_BEE
decl_stmt|;
DECL|field|CEE
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|CEE
decl_stmt|;
annotation|@
name|Override
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
name|AYE
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
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
name|BEE
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
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
name|AYE_BEE
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"one-alpha"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"two-beta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"three-gamma"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"four-delta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"five-epsilon"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"six-zeta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"seven-eta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"eight-theta"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|AYE_BEE
operator|.
name|put
argument_list|(
literal|"nine-iota"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CEE
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
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
block|}
annotation|@
name|Test
DECL|method|testScalability
specifier|public
name|void
name|testScalability
parameter_list|()
throws|throws
name|Exception
block|{
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
name|lotsaSuggestions
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
name|lotsaSuggestions
operator|.
name|put
argument_list|(
name|TOKEN_AYE
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
name|TOKEN_BEE
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
name|TOKEN_CEE
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE1"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"BEE1"
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"CEE1"
argument_list|,
literal|8
argument_list|,
literal|11
argument_list|)
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE2"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"BEE2"
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"CEE2"
argument_list|,
literal|8
argument_list|,
literal|11
argument_list|)
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE3"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"BEE3"
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"CEE3"
argument_list|,
literal|8
argument_list|,
literal|11
argument_list|)
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE4"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"BEE4"
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"CEE4"
argument_list|,
literal|8
argument_list|,
literal|11
argument_list|)
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
name|PossibilityIterator
name|iter
init|=
operator|new
name|PossibilityIterator
argument_list|(
name|lotsaSuggestions
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|,
literal|false
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
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
name|rsp
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|==
literal|1000
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE_BEE1"
argument_list|,
literal|0
argument_list|,
literal|7
argument_list|)
argument_list|,
name|AYE_BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE_BEE2"
argument_list|,
literal|0
argument_list|,
literal|7
argument_list|)
argument_list|,
name|AYE_BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE_BEE3"
argument_list|,
literal|0
argument_list|,
literal|7
argument_list|)
argument_list|,
name|AYE_BEE
argument_list|)
expr_stmt|;
name|lotsaSuggestions
operator|.
name|put
argument_list|(
operator|new
name|Token
argument_list|(
literal|"AYE_BEE4"
argument_list|,
literal|0
argument_list|,
literal|7
argument_list|)
argument_list|,
name|AYE_BEE
argument_list|)
expr_stmt|;
name|iter
operator|=
operator|new
name|PossibilityIterator
argument_list|(
name|lotsaSuggestions
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|,
literal|true
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
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
name|rsp
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|<
literal|100
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
name|suggestions
operator|.
name|put
argument_list|(
name|TOKEN_AYE
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|put
argument_list|(
name|TOKEN_BEE
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|put
argument_list|(
name|TOKEN_CEE
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
name|PossibilityIterator
name|iter
init|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|,
literal|false
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
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
name|rsp
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"I"
operator|.
name|equals
argument_list|(
name|rsp
operator|.
name|corrections
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCorrection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"alpha"
operator|.
name|equals
argument_list|(
name|rsp
operator|.
name|corrections
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getCorrection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"one"
operator|.
name|equals
argument_list|(
name|rsp
operator|.
name|corrections
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getCorrection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|TOKEN_CEE
argument_list|)
expr_stmt|;
name|iter
operator|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|,
literal|false
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
name|TOKEN_BEE
argument_list|)
expr_stmt|;
name|iter
operator|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|,
literal|5
argument_list|,
literal|10000
argument_list|,
literal|false
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
literal|"We requested 5 suggestions but got "
operator|+
name|count
operator|)
argument_list|,
name|count
operator|==
literal|5
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|remove
argument_list|(
name|TOKEN_AYE
argument_list|)
expr_stmt|;
name|iter
operator|=
operator|new
name|PossibilityIterator
argument_list|(
name|suggestions
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|10000
argument_list|,
literal|false
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
annotation|@
name|Test
DECL|method|testOverlappingTokens
specifier|public
name|void
name|testOverlappingTokens
parameter_list|()
throws|throws
name|Exception
block|{
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
name|overlappingSuggestions
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
name|overlappingSuggestions
operator|.
name|put
argument_list|(
name|TOKEN_AYE
argument_list|,
name|AYE
argument_list|)
expr_stmt|;
name|overlappingSuggestions
operator|.
name|put
argument_list|(
name|TOKEN_BEE
argument_list|,
name|BEE
argument_list|)
expr_stmt|;
name|overlappingSuggestions
operator|.
name|put
argument_list|(
name|TOKEN_AYE_BEE
argument_list|,
name|AYE_BEE
argument_list|)
expr_stmt|;
name|overlappingSuggestions
operator|.
name|put
argument_list|(
name|TOKEN_CEE
argument_list|,
name|CEE
argument_list|)
expr_stmt|;
name|PossibilityIterator
name|iter
init|=
operator|new
name|PossibilityIterator
argument_list|(
name|overlappingSuggestions
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|aCount
init|=
literal|0
decl_stmt|;
name|int
name|abCount
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
argument_list|>
name|dupChecker
init|=
operator|new
name|HashSet
argument_list|<
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
name|rsp
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Token
name|a
init|=
literal|null
decl_stmt|;
name|Token
name|b
init|=
literal|null
decl_stmt|;
name|Token
name|ab
init|=
literal|null
decl_stmt|;
name|Token
name|c
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SpellCheckCorrection
name|scc
range|:
name|rsp
operator|.
name|corrections
control|)
block|{
if|if
condition|(
name|scc
operator|.
name|getOriginal
argument_list|()
operator|.
name|equals
argument_list|(
name|TOKEN_AYE
argument_list|)
condition|)
block|{
name|a
operator|=
name|scc
operator|.
name|getOriginal
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scc
operator|.
name|getOriginal
argument_list|()
operator|.
name|equals
argument_list|(
name|TOKEN_BEE
argument_list|)
condition|)
block|{
name|b
operator|=
name|scc
operator|.
name|getOriginal
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scc
operator|.
name|getOriginal
argument_list|()
operator|.
name|equals
argument_list|(
name|TOKEN_AYE_BEE
argument_list|)
condition|)
block|{
name|ab
operator|=
name|scc
operator|.
name|getOriginal
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scc
operator|.
name|getOriginal
argument_list|()
operator|.
name|equals
argument_list|(
name|TOKEN_CEE
argument_list|)
condition|)
block|{
name|c
operator|=
name|scc
operator|.
name|getOriginal
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ab
operator|!=
literal|null
condition|)
block|{
name|abCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|aCount
operator|++
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|c
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ab
operator|!=
literal|null
operator|||
operator|(
name|a
operator|!=
literal|null
operator|&&
name|b
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ab
operator|==
literal|null
operator|||
operator|(
name|a
operator|==
literal|null
operator|&&
name|b
operator|==
literal|null
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dupChecker
operator|.
name|add
argument_list|(
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|aCount
operator|==
literal|2160
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|abCount
operator|==
literal|180
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

