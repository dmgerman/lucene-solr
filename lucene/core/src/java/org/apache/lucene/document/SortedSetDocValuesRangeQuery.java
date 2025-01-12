begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|Objects
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
name|DocValues
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|FieldValueQuery
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_class
DECL|class|SortedSetDocValuesRangeQuery
specifier|abstract
class|class
name|SortedSetDocValuesRangeQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|lowerValue
specifier|private
specifier|final
name|BytesRef
name|lowerValue
decl_stmt|;
DECL|field|upperValue
specifier|private
specifier|final
name|BytesRef
name|upperValue
decl_stmt|;
DECL|field|lowerInclusive
specifier|private
specifier|final
name|boolean
name|lowerInclusive
decl_stmt|;
DECL|field|upperInclusive
specifier|private
specifier|final
name|boolean
name|upperInclusive
decl_stmt|;
DECL|method|SortedSetDocValuesRangeQuery
name|SortedSetDocValuesRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|lowerValue
parameter_list|,
name|BytesRef
name|upperValue
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|lowerValue
operator|=
name|lowerValue
expr_stmt|;
name|this
operator|.
name|upperValue
operator|=
name|upperValue
expr_stmt|;
name|this
operator|.
name|lowerInclusive
operator|=
name|lowerInclusive
operator|&&
name|lowerValue
operator|!=
literal|null
expr_stmt|;
name|this
operator|.
name|upperInclusive
operator|=
name|upperInclusive
operator|&&
name|upperValue
operator|!=
literal|null
expr_stmt|;
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
name|SortedSetDocValuesRangeQuery
name|that
init|=
operator|(
name|SortedSetDocValuesRangeQuery
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|field
argument_list|,
name|that
operator|.
name|field
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|lowerValue
argument_list|,
name|that
operator|.
name|lowerValue
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|upperValue
argument_list|,
name|that
operator|.
name|upperValue
argument_list|)
operator|&&
name|lowerInclusive
operator|==
name|that
operator|.
name|lowerInclusive
operator|&&
name|upperInclusive
operator|==
name|that
operator|.
name|upperInclusive
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
name|field
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
name|Objects
operator|.
name|hashCode
argument_list|(
name|lowerValue
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Objects
operator|.
name|hashCode
argument_list|(
name|upperValue
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Boolean
operator|.
name|hashCode
argument_list|(
name|lowerInclusive
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Boolean
operator|.
name|hashCode
argument_list|(
name|upperInclusive
argument_list|)
expr_stmt|;
return|return
name|h
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
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
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
name|b
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|append
argument_list|(
name|lowerInclusive
condition|?
literal|"["
else|:
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
name|lowerValue
operator|==
literal|null
condition|?
literal|"*"
else|:
name|lowerValue
argument_list|)
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
operator|.
name|append
argument_list|(
name|upperValue
operator|==
literal|null
condition|?
literal|"*"
else|:
name|upperValue
argument_list|)
operator|.
name|append
argument_list|(
name|upperInclusive
condition|?
literal|"]"
else|:
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
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
if|if
condition|(
name|lowerValue
operator|==
literal|null
operator|&&
name|upperValue
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|FieldValueQuery
argument_list|(
name|field
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
DECL|method|getValues
specifier|abstract
name|SortedSetDocValues
name|getValues
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|,
name|boost
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
name|SortedSetDocValues
name|values
init|=
name|getValues
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|long
name|minOrd
decl_stmt|;
if|if
condition|(
name|lowerValue
operator|==
literal|null
condition|)
block|{
name|minOrd
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
name|lowerValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|minOrd
operator|=
operator|-
literal|1
operator|-
name|ord
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerInclusive
condition|)
block|{
name|minOrd
operator|=
name|ord
expr_stmt|;
block|}
else|else
block|{
name|minOrd
operator|=
name|ord
operator|+
literal|1
expr_stmt|;
block|}
block|}
specifier|final
name|long
name|maxOrd
decl_stmt|;
if|if
condition|(
name|upperValue
operator|==
literal|null
condition|)
block|{
name|maxOrd
operator|=
name|values
operator|.
name|getValueCount
argument_list|()
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
name|upperValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|maxOrd
operator|=
operator|-
literal|2
operator|-
name|ord
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|upperInclusive
condition|)
block|{
name|maxOrd
operator|=
name|ord
expr_stmt|;
block|}
else|else
block|{
name|maxOrd
operator|=
name|ord
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|minOrd
operator|>
name|maxOrd
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|SortedDocValues
name|singleton
init|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
decl_stmt|;
specifier|final
name|TwoPhaseIterator
name|iterator
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
name|iterator
operator|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|singleton
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
specifier|final
name|long
name|ord
init|=
name|singleton
operator|.
name|ordValue
argument_list|()
decl_stmt|;
return|return
name|ord
operator|>=
name|minOrd
operator|&&
name|ord
operator|<=
name|maxOrd
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|2
return|;
comment|// 2 comparisons
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|iterator
operator|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|values
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
for|for
control|(
name|long
name|ord
init|=
name|values
operator|.
name|nextOrd
argument_list|()
init|;
name|ord
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|;
name|ord
operator|=
name|values
operator|.
name|nextOrd
argument_list|()
control|)
block|{
if|if
condition|(
name|ord
operator|<
name|minOrd
condition|)
block|{
continue|continue;
block|}
comment|// Values are sorted, so the first ord that is>= minOrd is our best candidate
return|return
name|ord
operator|<=
name|maxOrd
return|;
block|}
return|return
literal|false
return|;
comment|// all ords were< minOrd
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|2
return|;
comment|// 2 comparisons
block|}
block|}
expr_stmt|;
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
name|iterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

