begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
import|;
end_import

begin_comment
comment|/**  *  Damerau-Levenshtein (optimal string alignment) implemented in a consistent   *  way as Lucene's FuzzyTermsEnum with the transpositions option enabled.  *    *  Notes:  *<ul>  *<li> This metric treats full unicode codepoints as characters  *<li> This metric scales raw edit distances into a floating point score  *         based upon the shortest of the two terms  *<li> Transpositions of two adjacent codepoints are treated as primitive   *         edits.  *<li> Edits are applied in parallel: for example, "ab" and "bca" have   *         distance 3.  *</ul>  *    *  NOTE: this class is not particularly efficient. It is only intended  *  for merging results from multiple DirectSpellCheckers.  */
end_comment

begin_class
DECL|class|LuceneLevenshteinDistance
specifier|public
specifier|final
class|class
name|LuceneLevenshteinDistance
implements|implements
name|StringDistance
block|{
comment|/**    * Creates a new comparator, mimicing the behavior of Lucene's internal    * edit distance.    */
DECL|method|LuceneLevenshteinDistance
specifier|public
name|LuceneLevenshteinDistance
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getDistance
specifier|public
name|float
name|getDistance
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|other
parameter_list|)
block|{
name|IntsRef
name|targetPoints
decl_stmt|;
name|IntsRef
name|otherPoints
decl_stmt|;
name|int
name|n
decl_stmt|;
name|int
name|d
index|[]
index|[]
decl_stmt|;
comment|// cost array
comment|// NOTE: if we cared, we could 3*m space instead of m*n space, similar to
comment|// what LevenshteinDistance does, except cycling thru a ring of three
comment|// horizontal cost arrays... but this comparator is never actually used by
comment|// DirectSpellChecker, it's only used for merging results from multiple shards
comment|// in "distributed spellcheck", and it's inefficient in other ways too...
comment|// cheaper to do this up front once
name|targetPoints
operator|=
name|toIntsRef
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|otherPoints
operator|=
name|toIntsRef
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|n
operator|=
name|targetPoints
operator|.
name|length
expr_stmt|;
specifier|final
name|int
name|m
init|=
name|otherPoints
operator|.
name|length
decl_stmt|;
name|d
operator|=
operator|new
name|int
index|[
name|n
operator|+
literal|1
index|]
index|[
name|m
operator|+
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
operator|||
name|m
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|==
name|m
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|n
argument_list|,
name|m
argument_list|)
return|;
block|}
block|}
comment|// indexes into strings s and t
name|int
name|i
decl_stmt|;
comment|// iterates through s
name|int
name|j
decl_stmt|;
comment|// iterates through t
name|int
name|t_j
decl_stmt|;
comment|// jth character of t
name|int
name|cost
decl_stmt|;
comment|// cost
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|d
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
name|i
expr_stmt|;
block|}
for|for
control|(
name|j
operator|=
literal|0
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
name|d
index|[
literal|0
index|]
index|[
name|j
index|]
operator|=
name|j
expr_stmt|;
block|}
for|for
control|(
name|j
operator|=
literal|1
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
name|t_j
operator|=
name|otherPoints
operator|.
name|ints
index|[
name|j
operator|-
literal|1
index|]
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|cost
operator|=
name|targetPoints
operator|.
name|ints
index|[
name|i
operator|-
literal|1
index|]
operator|==
name|t_j
condition|?
literal|0
else|:
literal|1
expr_stmt|;
comment|// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
index|]
operator|+
literal|1
argument_list|,
name|d
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|)
argument_list|,
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
operator|-
literal|1
index|]
operator|+
name|cost
argument_list|)
expr_stmt|;
comment|// transposition
if|if
condition|(
name|i
operator|>
literal|1
operator|&&
name|j
operator|>
literal|1
operator|&&
name|targetPoints
operator|.
name|ints
index|[
name|i
operator|-
literal|1
index|]
operator|==
name|otherPoints
operator|.
name|ints
index|[
name|j
operator|-
literal|2
index|]
operator|&&
name|targetPoints
operator|.
name|ints
index|[
name|i
operator|-
literal|2
index|]
operator|==
name|otherPoints
operator|.
name|ints
index|[
name|j
operator|-
literal|1
index|]
condition|)
block|{
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|d
index|[
name|i
operator|-
literal|2
index|]
index|[
name|j
operator|-
literal|2
index|]
operator|+
name|cost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|d
index|[
name|n
index|]
index|[
name|m
index|]
operator|/
name|Math
operator|.
name|min
argument_list|(
name|m
argument_list|,
name|n
argument_list|)
operator|)
return|;
block|}
DECL|method|toIntsRef
specifier|private
specifier|static
name|IntsRef
name|toIntsRef
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|IntsRef
name|ref
init|=
operator|new
name|IntsRef
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// worst case
name|int
name|utf16Len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|cp
init|=
literal|0
init|;
name|i
operator|<
name|utf16Len
condition|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|cp
operator|=
name|ref
operator|.
name|ints
index|[
name|ref
operator|.
name|length
operator|++
index|]
operator|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|ref
return|;
block|}
block|}
end_class

end_unit

