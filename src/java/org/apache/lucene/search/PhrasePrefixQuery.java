begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|MultipleTermPositions
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|TermPositions
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
name|Query
import|;
end_import

begin_comment
comment|/**  * PhrasePrefixQuery is a generalized version of PhraseQuery, with an added  * method {@link #add(Term[])}.  * To use this class, to search for the phrase "Microsoft app*" first use  * add(Term) on the term "Microsoft", then find all terms that has "app" as  * prefix using IndexReader.terms(Term), and use PhrasePrefixQuery.add(Term[]  * terms) to add them to the query.  *   * @deprecated use {@link org.apache.lucene.search.MultiPhraseQuery} instead  * @author Anders Nielsen  * @version 1.0  */
end_comment

begin_class
DECL|class|PhrasePrefixQuery
specifier|public
class|class
name|PhrasePrefixQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|termArrays
specifier|private
name|ArrayList
name|termArrays
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|positions
specifier|private
name|Vector
name|positions
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
literal|0
decl_stmt|;
comment|/** Sets the phrase slop for this query.    * @see PhraseQuery#setSlop(int)    */
DECL|method|setSlop
specifier|public
name|void
name|setSlop
parameter_list|(
name|int
name|s
parameter_list|)
block|{
name|slop
operator|=
name|s
expr_stmt|;
block|}
comment|/** Sets the phrase slop for this query.    * @see PhraseQuery#getSlop()    */
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
comment|/** Add a single term at the next position in the phrase.    * @see PhraseQuery#add(Term)    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
name|term
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Add multiple terms at the next position in the phrase.  Any of the terms    * may match.    *    * @see PhraseQuery#add(Term)    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|)
block|{
name|int
name|position
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|positions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|position
operator|=
operator|(
operator|(
name|Integer
operator|)
name|positions
operator|.
name|lastElement
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
operator|+
literal|1
expr_stmt|;
name|add
argument_list|(
name|terms
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allows to specify the relative position of terms within the phrase.    *     * @see PhraseQuery#add(Term, int)    * @param terms    * @param position    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|,
name|int
name|position
parameter_list|)
block|{
if|if
condition|(
name|termArrays
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
name|field
operator|=
name|terms
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All phrase terms must be in the same field ("
operator|+
name|field
operator|+
literal|"): "
operator|+
name|terms
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
name|termArrays
operator|.
name|add
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|positions
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the relative positions of terms in this phrase.    */
DECL|method|getPositions
specifier|public
name|int
index|[]
name|getPositions
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|positions
operator|.
name|size
argument_list|()
index|]
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
name|positions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|result
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|Integer
operator|)
name|positions
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|PhrasePrefixWeight
specifier|private
class|class
name|PhrasePrefixWeight
implements|implements
name|Weight
block|{
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|value
specifier|private
name|float
name|value
decl_stmt|;
DECL|field|idf
specifier|private
name|float
name|idf
decl_stmt|;
DECL|field|queryNorm
specifier|private
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
decl_stmt|;
DECL|method|PhrasePrefixWeight
specifier|public
name|PhrasePrefixWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|PhrasePrefixQuery
operator|.
name|this
return|;
block|}
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
name|Iterator
name|i
init|=
name|termArrays
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Term
index|[]
name|terms
init|=
operator|(
name|Term
index|[]
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|terms
operator|.
name|length
condition|;
name|j
operator|++
control|)
name|idf
operator|+=
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
operator|.
name|idf
argument_list|(
name|terms
index|[
name|j
index|]
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
name|queryWeight
operator|=
name|idf
operator|*
name|getBoost
argument_list|()
expr_stmt|;
comment|// compute query weight
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
comment|// square it
block|}
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|queryNorm
expr_stmt|;
name|queryWeight
operator|*=
name|queryNorm
expr_stmt|;
comment|// normalize query weight
name|value
operator|=
name|queryWeight
operator|*
name|idf
expr_stmt|;
comment|// idf for document
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termArrays
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// optimize zero-term case
return|return
literal|null
return|;
name|TermPositions
index|[]
name|tps
init|=
operator|new
name|TermPositions
index|[
name|termArrays
operator|.
name|size
argument_list|()
index|]
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
name|tps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Term
index|[]
name|terms
init|=
operator|(
name|Term
index|[]
operator|)
name|termArrays
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|TermPositions
name|p
decl_stmt|;
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|1
condition|)
name|p
operator|=
operator|new
name|MultipleTermPositions
argument_list|(
name|reader
argument_list|,
name|terms
argument_list|)
expr_stmt|;
else|else
name|p
operator|=
name|reader
operator|.
name|termPositions
argument_list|(
name|terms
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|tps
index|[
name|i
index|]
operator|=
name|p
expr_stmt|;
block|}
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
return|return
operator|new
name|ExactPhraseScorer
argument_list|(
name|this
argument_list|,
name|tps
argument_list|,
name|getPositions
argument_list|()
argument_list|,
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|new
name|SloppyPhraseScorer
argument_list|(
name|this
argument_list|,
name|tps
argument_list|,
name|getPositions
argument_list|()
argument_list|,
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
argument_list|,
name|slop
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|idfExpl
init|=
operator|new
name|Explanation
argument_list|(
name|idf
argument_list|,
literal|"idf("
operator|+
name|getQuery
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
comment|// explain query weight
name|Explanation
name|queryExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|queryExpl
operator|.
name|setDescription
argument_list|(
literal|"queryWeight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|boostExpl
init|=
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|boostExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|queryNormExpl
init|=
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
decl_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|queryNormExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|setValue
argument_list|(
name|boostExpl
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|queryNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|queryExpl
argument_list|)
expr_stmt|;
comment|// explain field weight
name|Explanation
name|fieldExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|fieldExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldWeight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|tfExpl
init|=
name|scorer
argument_list|(
name|reader
argument_list|)
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|tfExpl
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|fieldNormExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|byte
index|[]
name|fieldNorms
init|=
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|float
name|fieldNorm
init|=
name|fieldNorms
operator|!=
literal|null
condition|?
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|fieldNorms
index|[
name|doc
index|]
argument_list|)
else|:
literal|0.0f
decl_stmt|;
name|fieldNormExpl
operator|.
name|setValue
argument_list|(
name|fieldNorm
argument_list|)
expr_stmt|;
name|fieldNormExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldNorm(field="
operator|+
name|field
operator|+
literal|", doc="
operator|+
name|doc
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|fieldNormExpl
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|setValue
argument_list|(
name|tfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|fieldExpl
argument_list|)
expr_stmt|;
comment|// combine them
name|result
operator|.
name|setValue
argument_list|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|==
literal|1.0f
condition|)
return|return
name|fieldExpl
return|;
return|return
name|result
return|;
block|}
block|}
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
if|if
condition|(
name|termArrays
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// optimize one-term case
name|Term
index|[]
name|terms
init|=
operator|(
name|Term
index|[]
operator|)
name|termArrays
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|BooleanQuery
name|boq
init|=
operator|new
name|BooleanQuery
argument_list|()
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|boq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|boq
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
return|return
operator|new
name|PhrasePrefixWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
comment|/** Prints a user-readable version of this query. */
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|Iterator
name|i
init|=
name|termArrays
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Term
index|[]
name|terms
init|=
operator|(
name|Term
index|[]
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|terms
index|[
literal|0
index|]
operator|.
name|text
argument_list|()
operator|+
operator|(
name|terms
operator|.
name|length
operator|>
literal|1
condition|?
literal|"*"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|slop
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"~"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|slop
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

