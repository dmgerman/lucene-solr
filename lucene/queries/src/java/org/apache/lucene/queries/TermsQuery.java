begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Objects
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
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|Fields
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
name|LeafReader
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
name|LeafReaderContext
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
name|PostingsEnum
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
name|PrefixCodedTerms
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
name|PrefixCodedTerms
operator|.
name|TermIterator
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
name|TermContext
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
name|TermState
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
name|Terms
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
name|TermsEnum
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|BulkScorer
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
name|ConstantScoreQuery
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
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|IndexSearcher
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
name|Scorer
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
name|TermQuery
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
name|Weight
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
name|Accountable
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
name|ArrayUtil
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
name|BytesRef
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
name|DocIdSetBuilder
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
name|BytesRefBuilder
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  * Specialization for a disjunction over many terms that behaves like a  * {@link ConstantScoreQuery} over a {@link BooleanQuery} containing only  * {@link org.apache.lucene.search.BooleanClause.Occur#SHOULD} clauses.  *<p>For instance in the following example, both @{code q1} and {@code q2}  * would yield the same scores:  *<pre class="prettyprint">  * Query q1 = new TermsQuery(new Term("field", "foo"), new Term("field", "bar"));  *  * BooleanQuery bq = new BooleanQuery();  * bq.add(new TermQuery(new Term("field", "foo")), Occur.SHOULD);  * bq.add(new TermQuery(new Term("field", "bar")), Occur.SHOULD);  * Query q2 = new ConstantScoreQuery(bq);  *</pre>  *<p>When there are few terms, this query executes like a regular disjunction.  * However, when there are many terms, instead of merging iterators on the fly,  * it will populate a bit set with matching docs and return a {@link Scorer}  * over this bit set.  *<p>NOTE: This query produces scores that are equal to its boost  */
end_comment

begin_class
DECL|class|TermsQuery
specifier|public
class|class
name|TermsQuery
extends|extends
name|Query
implements|implements
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|TermsQuery
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Same threshold as MultiTermQueryConstantScoreWrapper
DECL|field|BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD
specifier|static
specifier|final
name|int
name|BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD
init|=
literal|16
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|field|termData
specifier|private
specifier|final
name|PrefixCodedTerms
name|termData
decl_stmt|;
DECL|field|termDataHashCode
specifier|private
specifier|final
name|int
name|termDataHashCode
decl_stmt|;
comment|// cached hashcode of termData
comment|/**    * Creates a new {@link TermsQuery} from the given collection. It    * can contain duplicate terms and multiple fields.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
name|Collection
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|Term
index|[]
name|sortedTerms
init|=
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// already sorted if we are a SortedSet with natural order
name|boolean
name|sorted
init|=
name|terms
operator|instanceof
name|SortedSet
operator|&&
operator|(
operator|(
name|SortedSet
argument_list|<
name|Term
argument_list|>
operator|)
name|terms
operator|)
operator|.
name|comparator
argument_list|()
operator|==
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|sorted
condition|)
block|{
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|sortedTerms
argument_list|)
expr_stmt|;
block|}
name|PrefixCodedTerms
operator|.
name|Builder
name|builder
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Term
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|sortedTerms
control|)
block|{
if|if
condition|(
name|term
operator|.
name|equals
argument_list|(
name|previous
argument_list|)
operator|==
literal|false
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|term
expr_stmt|;
block|}
name|this
operator|.
name|fields
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|termData
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|termDataHashCode
operator|=
name|termData
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsQuery} from the given collection for    * a single field. It can contain duplicate terms.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Collection
argument_list|<
name|BytesRef
argument_list|>
name|terms
parameter_list|)
block|{
name|BytesRef
index|[]
name|sortedTerms
init|=
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// already sorted if we are a SortedSet with natural order
name|boolean
name|sorted
init|=
name|terms
operator|instanceof
name|SortedSet
operator|&&
operator|(
operator|(
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
operator|)
name|terms
operator|)
operator|.
name|comparator
argument_list|()
operator|==
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|sorted
condition|)
block|{
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|sortedTerms
argument_list|)
expr_stmt|;
block|}
name|PrefixCodedTerms
operator|.
name|Builder
name|builder
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BytesRefBuilder
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
range|:
name|sortedTerms
control|)
block|{
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
name|previous
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|previous
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|term
argument_list|)
condition|)
block|{
continue|continue;
comment|// deduplicate
block|}
name|builder
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|previous
operator|.
name|copyBytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|fields
operator|=
name|Collections
operator|.
name|singleton
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|termData
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|termDataHashCode
operator|=
name|termData
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsQuery} from the given {@link BytesRef} array for    * a single field.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
modifier|...
name|terms
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsQuery} from the given array. The array can    * contain duplicate terms and multiple fields.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
specifier|final
name|Term
modifier|...
name|terms
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|threshold
init|=
name|Math
operator|.
name|min
argument_list|(
name|BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|termData
operator|.
name|size
argument_list|()
operator|<=
name|threshold
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|TermIterator
name|iterator
init|=
name|termData
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|iterator
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|iterator
operator|.
name|field
argument_list|()
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermsQuery
name|that
init|=
operator|(
name|TermsQuery
operator|)
name|obj
decl_stmt|;
comment|// termData might be heavy to compare so check the hash code first
return|return
name|termDataHashCode
operator|==
name|that
operator|.
name|termDataHashCode
operator|&&
name|termData
operator|.
name|equals
argument_list|(
name|that
operator|.
name|termData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|termDataHashCode
return|;
block|}
comment|/** Returns the terms wrapped in a PrefixCodedTerms. */
DECL|method|getTermData
specifier|public
name|PrefixCodedTerms
name|getTermData
parameter_list|()
block|{
return|return
name|termData
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|TermIterator
name|iterator
init|=
name|termData
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|iterator
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|new
name|Term
argument_list|(
name|iterator
operator|.
name|field
argument_list|()
argument_list|,
name|term
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
name|termData
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|class|TermAndState
specifier|private
specifier|static
class|class
name|TermAndState
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|termsEnum
specifier|final
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|field|term
specifier|final
name|BytesRef
name|term
decl_stmt|;
DECL|field|state
specifier|final
name|TermState
name|state
decl_stmt|;
DECL|field|docFreq
specifier|final
name|int
name|docFreq
decl_stmt|;
DECL|field|totalTermFreq
specifier|final
name|long
name|totalTermFreq
decl_stmt|;
DECL|method|TermAndState
name|TermAndState
parameter_list|(
name|String
name|field
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|termsEnum
operator|.
name|termState
argument_list|()
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalTermFreq
operator|=
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|WeightOrDocIdSet
specifier|private
specifier|static
class|class
name|WeightOrDocIdSet
block|{
DECL|field|weight
specifier|final
name|Weight
name|weight
decl_stmt|;
DECL|field|set
specifier|final
name|DocIdSet
name|set
decl_stmt|;
DECL|method|WeightOrDocIdSet
name|WeightOrDocIdSet
parameter_list|(
name|Weight
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|WeightOrDocIdSet
name|WeightOrDocIdSet
parameter_list|(
name|DocIdSet
name|bitset
parameter_list|)
block|{
name|this
operator|.
name|set
operator|=
name|bitset
expr_stmt|;
name|this
operator|.
name|weight
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
comment|// no-op
comment|// This query is for abuse cases when the number of terms is too high to
comment|// run efficiently as a BooleanQuery. So likewise we hide its terms in
comment|// order to protect highlighters
block|}
comment|/**        * On the given leaf context, try to either rewrite to a disjunction if        * there are few matching terms, or build a bitset containing matching docs.        */
specifier|private
name|WeightOrDocIdSet
name|rewrite
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|// We will first try to collect up to 'threshold' terms into 'matchingTerms'
comment|// if there are two many terms, we will fall back to building the 'builder'
specifier|final
name|int
name|threshold
init|=
name|Math
operator|.
name|min
argument_list|(
name|BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|termData
operator|.
name|size
argument_list|()
operator|>
name|threshold
operator|:
literal|"Query should have been rewritten"
assert|;
name|List
argument_list|<
name|TermAndState
argument_list|>
name|matchingTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|threshold
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
name|builder
init|=
literal|null
decl_stmt|;
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
name|String
name|lastField
init|=
literal|null
decl_stmt|;
name|Terms
name|terms
init|=
literal|null
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|PostingsEnum
name|docs
init|=
literal|null
decl_stmt|;
name|TermIterator
name|iterator
init|=
name|termData
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|iterator
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
control|)
block|{
name|String
name|field
init|=
name|iterator
operator|.
name|field
argument_list|()
decl_stmt|;
comment|// comparing references is fine here
if|if
condition|(
name|field
operator|!=
name|lastField
condition|)
block|{
name|terms
operator|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|lastField
operator|=
name|field
expr_stmt|;
block|}
if|if
condition|(
name|termsEnum
operator|!=
literal|null
operator|&&
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
condition|)
block|{
if|if
condition|(
name|matchingTerms
operator|==
literal|null
condition|)
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matchingTerms
operator|.
name|size
argument_list|()
operator|<
name|threshold
condition|)
block|{
name|matchingTerms
operator|.
name|add
argument_list|(
operator|new
name|TermAndState
argument_list|(
name|field
argument_list|,
name|termsEnum
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|matchingTerms
operator|.
name|size
argument_list|()
operator|==
name|threshold
assert|;
if|if
condition|(
name|TermsQuery
operator|.
name|this
operator|.
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// common case: all terms are in the same field
comment|// use an optimized builder that leverages terms stats to be more efficient
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// corner case: different fields
comment|// don't make assumptions about the docs we will get
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|docs
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
for|for
control|(
name|TermAndState
name|t
range|:
name|matchingTerms
control|)
block|{
name|t
operator|.
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|t
operator|.
name|term
argument_list|,
name|t
operator|.
name|state
argument_list|)
expr_stmt|;
name|docs
operator|=
name|t
operator|.
name|termsEnum
operator|.
name|postings
argument_list|(
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
name|matchingTerms
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|matchingTerms
operator|!=
literal|null
condition|)
block|{
assert|assert
name|builder
operator|==
literal|null
assert|;
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|TermAndState
name|t
range|:
name|matchingTerms
control|)
block|{
specifier|final
name|TermContext
name|termContext
init|=
operator|new
name|TermContext
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|)
decl_stmt|;
name|termContext
operator|.
name|register
argument_list|(
name|t
operator|.
name|state
argument_list|,
name|context
operator|.
name|ord
argument_list|,
name|t
operator|.
name|docFreq
argument_list|,
name|t
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|t
operator|.
name|field
argument_list|,
name|t
operator|.
name|term
argument_list|)
argument_list|,
name|termContext
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Weight
name|weight
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|q
argument_list|)
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
decl_stmt|;
name|weight
operator|.
name|normalize
argument_list|(
literal|1f
argument_list|,
name|score
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|WeightOrDocIdSet
argument_list|(
name|weight
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|builder
operator|!=
literal|null
assert|;
return|return
operator|new
name|WeightOrDocIdSet
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
name|Scorer
name|scorer
parameter_list|(
name|DocIdSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|disi
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|WeightOrDocIdSet
name|weightOrBitSet
init|=
name|rewrite
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|weightOrBitSet
operator|.
name|weight
operator|!=
literal|null
condition|)
block|{
return|return
name|weightOrBitSet
operator|.
name|weight
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|Scorer
name|scorer
init|=
name|scorer
argument_list|(
name|weightOrBitSet
operator|.
name|set
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|DefaultBulkScorer
argument_list|(
name|scorer
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|WeightOrDocIdSet
name|weightOrBitSet
init|=
name|rewrite
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|weightOrBitSet
operator|.
name|weight
operator|!=
literal|null
condition|)
block|{
return|return
name|weightOrBitSet
operator|.
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|scorer
argument_list|(
name|weightOrBitSet
operator|.
name|set
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

