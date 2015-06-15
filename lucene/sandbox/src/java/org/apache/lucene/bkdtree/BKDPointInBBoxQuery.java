begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.bkdtree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|bkdtree
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SortedNumericDocValues
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
name|search
operator|.
name|BooleanClause
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
name|Explanation
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
name|Bits
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
name|ToStringUtils
import|;
end_import

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

begin_comment
comment|/** Finds all previously indexed points that fall within the specified boundings box.  *  *<p>The field must be indexed with {@link BKDTreeDocValuesFormat}, and {@link BKDPointField} added per document.  *  *<p><b>NOTE</b>: for fastest performance, this allocates FixedBitSet(maxDoc) for each segment.  The score of each hit is the query boost.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|BKDPointInBBoxQuery
specifier|public
class|class
name|BKDPointInBBoxQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|minLat
specifier|final
name|double
name|minLat
decl_stmt|;
DECL|field|maxLat
specifier|final
name|double
name|maxLat
decl_stmt|;
DECL|field|minLon
specifier|final
name|double
name|minLon
decl_stmt|;
DECL|field|maxLon
specifier|final
name|double
name|maxLon
decl_stmt|;
comment|/** Matches all points&gt;= minLon, minLat (inclusive) and&lt; maxLon, maxLat (exclusive). */
DECL|method|BKDPointInBBoxQuery
specifier|public
name|BKDPointInBBoxQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
if|if
condition|(
name|BKDTreeWriter
operator|.
name|validLat
argument_list|(
name|minLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minLat="
operator|+
name|minLat
operator|+
literal|" is not a valid latitude"
argument_list|)
throw|;
block|}
if|if
condition|(
name|BKDTreeWriter
operator|.
name|validLat
argument_list|(
name|maxLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxLat="
operator|+
name|maxLat
operator|+
literal|" is not a valid latitude"
argument_list|)
throw|;
block|}
if|if
condition|(
name|BKDTreeWriter
operator|.
name|validLon
argument_list|(
name|minLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minLon="
operator|+
name|minLon
operator|+
literal|" is not a valid longitude"
argument_list|)
throw|;
block|}
if|if
condition|(
name|BKDTreeWriter
operator|.
name|validLon
argument_list|(
name|maxLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxLon="
operator|+
name|maxLon
operator|+
literal|" is not a valid longitude"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minLon
operator|=
name|minLon
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|maxLon
expr_stmt|;
name|this
operator|.
name|minLat
operator|=
name|minLat
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|maxLat
expr_stmt|;
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
comment|// I don't use RandomAccessWeight here: it's no good to approximate with "match all docs"; this is an inverted structure and should be
comment|// used in the first pass:
return|return
operator|new
name|Weight
argument_list|(
name|this
argument_list|)
block|{
specifier|private
name|float
name|queryNorm
decl_stmt|;
specifier|private
name|float
name|queryWeight
decl_stmt|;
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
block|{       }
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|queryWeight
operator|=
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|queryNorm
operator|=
name|norm
operator|*
name|topLevelBoost
expr_stmt|;
name|queryWeight
operator|*=
name|queryNorm
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
specifier|final
name|Scorer
name|s
init|=
name|scorer
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|exists
init|=
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
decl_stmt|;
if|if
condition|(
name|exists
condition|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|queryWeight
argument_list|,
name|BKDPointInBBoxQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
name|BKDPointInBBoxQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't match id "
operator|+
name|doc
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
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|SortedNumericDocValues
name|sdv
init|=
name|reader
operator|.
name|getSortedNumericDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sdv
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment had this field
return|return
literal|null
return|;
block|}
if|if
condition|(
name|sdv
operator|instanceof
name|BKDTreeSortedNumericDocValues
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" was not indexed with BKDTreeDocValuesFormat: got: "
operator|+
name|sdv
argument_list|)
throw|;
block|}
name|BKDTreeSortedNumericDocValues
name|treeDV
init|=
operator|(
name|BKDTreeSortedNumericDocValues
operator|)
name|sdv
decl_stmt|;
name|BKDTreeReader
name|tree
init|=
name|treeDV
operator|.
name|getBKDTreeReader
argument_list|()
decl_stmt|;
name|DocIdSet
name|result
init|=
name|tree
operator|.
name|intersect
argument_list|(
name|acceptDocs
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|,
name|treeDV
operator|.
name|delegate
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Scorer
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|queryWeight
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|disi
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|advance
argument_list|(
name|target
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
name|disi
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
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
comment|// Crosses date line: we just rewrite into OR of two bboxes:
if|if
condition|(
name|maxLon
operator|<
name|minLon
condition|)
block|{
comment|// Disable coord here because a multi-valued doc could match both rects and get unfairly boosted:
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// E.g.: maxLon = -179, minLon = 179
name|BKDPointInBBoxQuery
name|left
init|=
operator|new
name|BKDPointInBBoxQuery
argument_list|(
name|field
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|BKDTreeWriter
operator|.
name|MIN_LON_INCL
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
name|left
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|left
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BKDPointInBBoxQuery
name|right
init|=
operator|new
name|BKDPointInBBoxQuery
argument_list|(
name|field
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|BKDTreeWriter
operator|.
name|MAX_LON_INCL
argument_list|)
decl_stmt|;
name|right
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|right
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|q
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
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
name|hash
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|hash
operator|+=
name|Double
operator|.
name|hashCode
argument_list|(
name|minLat
argument_list|)
operator|^
literal|0x14fa55fb
expr_stmt|;
name|hash
operator|+=
name|Double
operator|.
name|hashCode
argument_list|(
name|maxLat
argument_list|)
operator|^
literal|0x733fa5fe
expr_stmt|;
name|hash
operator|+=
name|Double
operator|.
name|hashCode
argument_list|(
name|minLon
argument_list|)
operator|^
literal|0x14fa55fb
expr_stmt|;
name|hash
operator|+=
name|Double
operator|.
name|hashCode
argument_list|(
name|maxLon
argument_list|)
operator|^
literal|0x733fa5fe
expr_stmt|;
return|return
name|hash
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
name|other
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
name|other
operator|instanceof
name|BKDPointInBBoxQuery
condition|)
block|{
specifier|final
name|BKDPointInBBoxQuery
name|q
init|=
operator|(
name|BKDPointInBBoxQuery
operator|)
name|other
decl_stmt|;
return|return
name|field
operator|.
name|equals
argument_list|(
name|q
operator|.
name|field
argument_list|)
operator|&&
name|minLat
operator|==
name|q
operator|.
name|minLat
operator|&&
name|maxLat
operator|==
name|q
operator|.
name|maxLat
operator|&&
name|minLon
operator|==
name|q
operator|.
name|minLon
operator|&&
name|maxLon
operator|==
name|q
operator|.
name|maxLon
return|;
block|}
return|return
literal|false
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|" Lower Left: ["
argument_list|)
operator|.
name|append
argument_list|(
name|minLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|minLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" Upper Right: ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|maxLat
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

