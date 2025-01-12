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
name|document
operator|.
name|LongPoint
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
name|document
operator|.
name|SortedNumericDocValuesField
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
name|Term
import|;
end_import

begin_comment
comment|/**  * A query that uses either an index structure (points or terms) or doc values  * in order to run a query, depending which one is more efficient. This is  * typically useful for range queries, whose {@link Weight#scorer} is costly  * to create since it usually needs to sort large lists of doc ids. For  * instance, for a field that both indexed {@link LongPoint}s and  * {@link SortedNumericDocValuesField}s with the same values, an efficient  * range query could be created by doing:  *<pre class="prettyprint">  *   String field;  *   long minValue, maxValue;  *   Query pointQuery = LongPoint.newRangeQuery(field, minValue, maxValue);  *   Query dvQuery = SortedNumericDocValuesField.newRangeQuery(field, minValue, maxValue);  *   Query query = new IndexOrDocValuesQuery(pointQuery, dvQuery);  *</pre>  * The above query will be efficient as it will use points in the case that they  * perform better, ie. when we need a good lead iterator that will be almost  * entirely consumed; and doc values otherwise, ie. in the case that another  * part of the query is already leading iteration but we still need the ability  * to verify that some documents match.  *<p><b>NOTE</b>This query currently only works well with point range/exact  * queries and their equivalent doc values queries.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndexOrDocValuesQuery
specifier|public
specifier|final
class|class
name|IndexOrDocValuesQuery
extends|extends
name|Query
block|{
DECL|field|indexQuery
DECL|field|dvQuery
specifier|private
specifier|final
name|Query
name|indexQuery
decl_stmt|,
name|dvQuery
decl_stmt|;
comment|/**    * Create an {@link IndexOrDocValuesQuery}. Both provided queries must match    * the same documents and give the same scores.    * @param indexQuery a query that has a good iterator but whose scorer may be costly to create    * @param dvQuery a query whose scorer is cheap to create that can quickly check whether a given document matches    */
DECL|method|IndexOrDocValuesQuery
specifier|public
name|IndexOrDocValuesQuery
parameter_list|(
name|Query
name|indexQuery
parameter_list|,
name|Query
name|dvQuery
parameter_list|)
block|{
name|this
operator|.
name|indexQuery
operator|=
name|indexQuery
expr_stmt|;
name|this
operator|.
name|dvQuery
operator|=
name|dvQuery
expr_stmt|;
block|}
comment|/** Return the wrapped query that may be costly to initialize but has a good    *  iterator. */
DECL|method|getIndexQuery
specifier|public
name|Query
name|getIndexQuery
parameter_list|()
block|{
return|return
name|indexQuery
return|;
block|}
comment|/** Return the wrapped query that may be slow at identifying all matching    *  documents, but which is cheap to initialize and can efficiently    *  verify that some documents match. */
DECL|method|getRandomAccessQuery
specifier|public
name|Query
name|getRandomAccessQuery
parameter_list|()
block|{
return|return
name|dvQuery
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
name|field
parameter_list|)
block|{
return|return
name|indexQuery
operator|.
name|toString
argument_list|(
name|field
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
name|sameClassAs
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|IndexOrDocValuesQuery
name|that
init|=
operator|(
name|IndexOrDocValuesQuery
operator|)
name|obj
decl_stmt|;
return|return
name|indexQuery
operator|.
name|equals
argument_list|(
name|that
operator|.
name|indexQuery
argument_list|)
operator|&&
name|dvQuery
operator|.
name|equals
argument_list|(
name|that
operator|.
name|dvQuery
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
name|int
name|h
init|=
name|classHash
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|indexQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|dvQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
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
name|Query
name|indexRewrite
init|=
name|indexQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|dvRewrite
init|=
name|dvQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexQuery
operator|!=
name|indexRewrite
operator|||
name|dvQuery
operator|!=
name|dvRewrite
condition|)
block|{
return|return
operator|new
name|IndexOrDocValuesQuery
argument_list|(
name|indexRewrite
argument_list|,
name|dvRewrite
argument_list|)
return|;
block|}
return|return
name|this
return|;
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
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|indexWeight
init|=
name|indexQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
decl_stmt|;
specifier|final
name|Weight
name|dvWeight
init|=
name|dvQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
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
name|indexWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We need to check a single doc, so the dv query should perform better
return|return
name|dvWeight
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
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
comment|// Bulk scorers need to consume the entire set of docs, so using an
comment|// index structure should perform better
return|return
name|indexWeight
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScorerSupplier
name|scorerSupplier
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ScorerSupplier
name|indexScorerSupplier
init|=
name|indexWeight
operator|.
name|scorerSupplier
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|ScorerSupplier
name|dvScorerSupplier
init|=
name|dvWeight
operator|.
name|scorerSupplier
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexScorerSupplier
operator|==
literal|null
operator|||
name|dvScorerSupplier
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
name|ScorerSupplier
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|get
parameter_list|(
name|boolean
name|randomAccess
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|randomAccess
condition|?
name|dvScorerSupplier
else|:
name|indexScorerSupplier
operator|)
operator|.
name|get
argument_list|(
name|randomAccess
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|indexScorerSupplier
operator|.
name|cost
argument_list|()
argument_list|,
name|dvScorerSupplier
operator|.
name|cost
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
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
name|ScorerSupplier
name|scorerSupplier
init|=
name|scorerSupplier
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorerSupplier
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|scorerSupplier
operator|.
name|get
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

