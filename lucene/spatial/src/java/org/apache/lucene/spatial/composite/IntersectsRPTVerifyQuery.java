begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.composite
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|composite
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|TwoPhaseIterator
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
name|spatial
operator|.
name|prefix
operator|.
name|AbstractVisitingPrefixTreeFilter
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|spatial
operator|.
name|util
operator|.
name|BitDocIdSetBuilder
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
name|BitDocIdSet
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
name|Bits
import|;
end_import

begin_comment
comment|/**  * A spatial Intersects predicate that distinguishes an approximated match from an exact match based on which cells  * are within the query shape. It exposes a {@link TwoPhaseIterator} that will verify a match with a provided  * predicate in the form of a {@link ValueSource} by calling {@link FunctionValues#boolVal(int)}.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|IntersectsRPTVerifyQuery
specifier|public
class|class
name|IntersectsRPTVerifyQuery
extends|extends
name|Query
block|{
DECL|field|intersectsDiffFilter
specifier|private
specifier|final
name|IntersectsDifferentiatingFilter
name|intersectsDiffFilter
decl_stmt|;
DECL|field|predicateValueSource
specifier|private
specifier|final
name|ValueSource
name|predicateValueSource
decl_stmt|;
comment|// we call FunctionValues.boolVal(doc)
DECL|method|IntersectsRPTVerifyQuery
specifier|public
name|IntersectsRPTVerifyQuery
parameter_list|(
name|Shape
name|queryShape
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|int
name|prefixGridScanLevel
parameter_list|,
name|ValueSource
name|predicateValueSource
parameter_list|)
block|{
name|this
operator|.
name|predicateValueSource
operator|=
name|predicateValueSource
expr_stmt|;
name|this
operator|.
name|intersectsDiffFilter
operator|=
operator|new
name|IntersectsDifferentiatingFilter
argument_list|(
name|queryShape
argument_list|,
name|fieldName
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
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
literal|"IntersectsVerified(fieldName="
operator|+
name|field
operator|+
literal|")"
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|IntersectsRPTVerifyQuery
name|that
init|=
operator|(
name|IntersectsRPTVerifyQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|intersectsDiffFilter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|intersectsDiffFilter
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|predicateValueSource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|predicateValueSource
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
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|intersectsDiffFilter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|predicateValueSource
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
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
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
name|valueSourceContext
init|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
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
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Compute approx& exact
specifier|final
name|IntersectsDifferentiatingFilter
operator|.
name|IntersectsDifferentiatingVisitor
name|result
init|=
name|intersectsDiffFilter
operator|.
name|compute
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|approxDocIdSet
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
name|approxDISI
init|=
name|result
operator|.
name|approxDocIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|approxDISI
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Bits
name|exactDocBits
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|exactDocIdSet
operator|!=
literal|null
condition|)
block|{
comment|// If both sets are the same, there's nothing to verify; we needn't return a TwoPhaseIterator
if|if
condition|(
name|result
operator|.
name|approxDocIdSet
operator|.
name|equals
argument_list|(
name|result
operator|.
name|exactDocIdSet
argument_list|)
condition|)
block|{
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|approxDISI
argument_list|)
return|;
block|}
name|exactDocBits
operator|=
name|result
operator|.
name|exactDocIdSet
operator|.
name|bits
argument_list|()
expr_stmt|;
assert|assert
name|exactDocBits
operator|!=
literal|null
assert|;
block|}
else|else
block|{
name|exactDocBits
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|FunctionValues
name|predFuncValues
init|=
name|predicateValueSource
operator|.
name|getValues
argument_list|(
name|valueSourceContext
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|TwoPhaseIterator
name|twoPhaseIterator
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|approxDISI
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|exactDocBits
operator|!=
literal|null
operator|&&
name|exactDocBits
operator|.
name|get
argument_list|(
name|approxDISI
operator|.
name|docID
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|predFuncValues
operator|.
name|boolVal
argument_list|(
name|approxDISI
operator|.
name|docID
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|twoPhaseIterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|//This is a "Filter" but we don't use it as-such; the caller calls the constructor and then compute() and examines
comment|// the results which consists of two parts -- the approximated results, and a subset of exact matches. The
comment|// difference needs to be verified.
comment|// TODO refactor AVPTF to not be a Query/Filter?
DECL|class|IntersectsDifferentiatingFilter
specifier|private
specifier|static
class|class
name|IntersectsDifferentiatingFilter
extends|extends
name|AbstractVisitingPrefixTreeFilter
block|{
DECL|method|IntersectsDifferentiatingFilter
specifier|public
name|IntersectsDifferentiatingFilter
parameter_list|(
name|Shape
name|queryShape
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|int
name|prefixGridScanLevel
parameter_list|)
block|{
name|super
argument_list|(
name|queryShape
argument_list|,
name|fieldName
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
block|}
DECL|method|compute
name|IntersectsDifferentiatingFilter
operator|.
name|IntersectsDifferentiatingVisitor
name|compute
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IntersectsDifferentiatingFilter
operator|.
name|IntersectsDifferentiatingVisitor
name|result
init|=
operator|new
name|IntersectsDifferentiatingFilter
operator|.
name|IntersectsDifferentiatingVisitor
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
name|result
operator|.
name|getDocIdSet
argument_list|()
expr_stmt|;
comment|//computes
return|return
name|result
return|;
block|}
comment|// TODO consider if IntersectsPrefixTreeFilter should simply do this and provide both sets
DECL|class|IntersectsDifferentiatingVisitor
class|class
name|IntersectsDifferentiatingVisitor
extends|extends
name|VisitorTemplate
block|{
DECL|field|approxBuilder
name|BitDocIdSetBuilder
name|approxBuilder
init|=
operator|new
name|BitDocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
DECL|field|exactBuilder
name|BitDocIdSetBuilder
name|exactBuilder
init|=
operator|new
name|BitDocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
DECL|field|exactDocIdSet
name|BitDocIdSet
name|exactDocIdSet
decl_stmt|;
DECL|field|approxDocIdSet
name|BitDocIdSet
name|approxDocIdSet
decl_stmt|;
DECL|method|IntersectsDifferentiatingVisitor
specifier|public
name|IntersectsDifferentiatingVisitor
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|protected
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
DECL|method|finish
specifier|protected
name|DocIdSet
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|exactDocIdSet
operator|=
name|exactBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
if|if
condition|(
name|approxBuilder
operator|.
name|isDefinitelyEmpty
argument_list|()
condition|)
block|{
name|approxDocIdSet
operator|=
name|exactDocIdSet
expr_stmt|;
comment|//optimization
block|}
else|else
block|{
if|if
condition|(
name|exactDocIdSet
operator|!=
literal|null
condition|)
block|{
name|approxBuilder
operator|.
name|or
argument_list|(
name|exactDocIdSet
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|approxDocIdSet
operator|=
name|approxBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
comment|//unused in this weird re-use of AVPTF
block|}
annotation|@
name|Override
DECL|method|visitPrefix
specifier|protected
name|boolean
name|visitPrefix
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cell
operator|.
name|getShapeRel
argument_list|()
operator|==
name|SpatialRelation
operator|.
name|WITHIN
condition|)
block|{
name|collectDocs
argument_list|(
name|exactBuilder
argument_list|)
expr_stmt|;
comment|//note: we'll add exact to approx on finish()
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
block|{
name|collectDocs
argument_list|(
name|approxBuilder
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|visitLeaf
specifier|protected
name|void
name|visitLeaf
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cell
operator|.
name|getShapeRel
argument_list|()
operator|==
name|SpatialRelation
operator|.
name|WITHIN
condition|)
block|{
name|collectDocs
argument_list|(
name|exactBuilder
argument_list|)
expr_stmt|;
comment|//note: we'll add exact to approx on finish()
block|}
else|else
block|{
name|collectDocs
argument_list|(
name|approxBuilder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
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
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

