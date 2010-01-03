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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|OpenBitSet
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
name|OpenBitSetDISI
import|;
end_import

begin_comment
comment|/**  * A container Filter that allows Boolean composition of Filters.  * Filters are allocated into one of three logical constructs;  * SHOULD, MUST NOT, MUST  * The results Filter BitSet is constructed as follows:  * SHOULD Filters are OR'd together  * The resulting Filter is NOT'd with the NOT Filters  * The resulting Filter is AND'd with the MUST Filters  */
end_comment

begin_class
DECL|class|BooleanFilter
specifier|public
class|class
name|BooleanFilter
extends|extends
name|Filter
block|{
DECL|field|shouldFilters
name|ArrayList
argument_list|<
name|Filter
argument_list|>
name|shouldFilters
init|=
literal|null
decl_stmt|;
DECL|field|notFilters
name|ArrayList
argument_list|<
name|Filter
argument_list|>
name|notFilters
init|=
literal|null
decl_stmt|;
DECL|field|mustFilters
name|ArrayList
argument_list|<
name|Filter
argument_list|>
name|mustFilters
init|=
literal|null
decl_stmt|;
DECL|method|getDISI
specifier|private
name|DocIdSetIterator
name|getDISI
parameter_list|(
name|ArrayList
argument_list|<
name|Filter
argument_list|>
name|filters
parameter_list|,
name|int
name|index
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|filters
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Returns the a DocIdSetIterator representing the Boolean composition    * of the filters that have been added.    */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|OpenBitSetDISI
name|res
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|shouldFilters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shouldFilters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|res
operator|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|getDISI
argument_list|(
name|shouldFilters
argument_list|,
name|i
argument_list|,
name|reader
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocIdSet
name|dis
init|=
name|shouldFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|dis
operator|instanceof
name|OpenBitSet
condition|)
block|{
comment|// optimized case for OpenBitSets
name|res
operator|.
name|or
argument_list|(
operator|(
name|OpenBitSet
operator|)
name|dis
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|inPlaceOr
argument_list|(
name|getDISI
argument_list|(
name|shouldFilters
argument_list|,
name|i
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|notFilters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|notFilters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|res
operator|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|getDISI
argument_list|(
name|notFilters
argument_list|,
name|i
argument_list|,
name|reader
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// NOTE: may set bits on deleted docs
block|}
else|else
block|{
name|DocIdSet
name|dis
init|=
name|notFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|dis
operator|instanceof
name|OpenBitSet
condition|)
block|{
comment|// optimized case for OpenBitSets
name|res
operator|.
name|andNot
argument_list|(
operator|(
name|OpenBitSet
operator|)
name|dis
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|inPlaceNot
argument_list|(
name|getDISI
argument_list|(
name|notFilters
argument_list|,
name|i
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|mustFilters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mustFilters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|res
operator|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|getDISI
argument_list|(
name|mustFilters
argument_list|,
name|i
argument_list|,
name|reader
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocIdSet
name|dis
init|=
name|mustFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|dis
operator|instanceof
name|OpenBitSet
condition|)
block|{
comment|// optimized case for OpenBitSets
name|res
operator|.
name|and
argument_list|(
operator|(
name|OpenBitSet
operator|)
name|dis
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|inPlaceAnd
argument_list|(
name|getDISI
argument_list|(
name|mustFilters
argument_list|,
name|i
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
return|return
name|finalResult
argument_list|(
name|res
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
comment|/** Provide a SortedVIntList when it is definitely smaller    * than an OpenBitSet.    * @deprecated Either use CachingWrapperFilter, or    * switch to a different DocIdSet implementation yourself.    * This method will be removed in Lucene 4.0     */
annotation|@
name|Deprecated
DECL|method|finalResult
specifier|protected
specifier|final
name|DocIdSet
name|finalResult
parameter_list|(
name|OpenBitSetDISI
name|result
parameter_list|,
name|int
name|maxDocs
parameter_list|)
block|{
return|return
name|result
return|;
block|}
comment|/**   * Adds a new FilterClause to the Boolean Filter container   * @param filterClause A FilterClause object containing a Filter and an Occur parameter   */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|FilterClause
name|filterClause
parameter_list|)
block|{
if|if
condition|(
name|filterClause
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
condition|)
block|{
if|if
condition|(
name|mustFilters
operator|==
literal|null
condition|)
block|{
name|mustFilters
operator|=
operator|new
name|ArrayList
argument_list|<
name|Filter
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|mustFilters
operator|.
name|add
argument_list|(
name|filterClause
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterClause
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
condition|)
block|{
if|if
condition|(
name|shouldFilters
operator|==
literal|null
condition|)
block|{
name|shouldFilters
operator|=
operator|new
name|ArrayList
argument_list|<
name|Filter
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|shouldFilters
operator|.
name|add
argument_list|(
name|filterClause
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterClause
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|)
condition|)
block|{
if|if
condition|(
name|notFilters
operator|==
literal|null
condition|)
block|{
name|notFilters
operator|=
operator|new
name|ArrayList
argument_list|<
name|Filter
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|notFilters
operator|.
name|add
argument_list|(
name|filterClause
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|equalFilters
specifier|private
name|boolean
name|equalFilters
parameter_list|(
name|ArrayList
argument_list|<
name|Filter
argument_list|>
name|filters1
parameter_list|,
name|ArrayList
argument_list|<
name|Filter
argument_list|>
name|filters2
parameter_list|)
block|{
return|return
operator|(
name|filters1
operator|==
name|filters2
operator|)
operator|||
operator|(
operator|(
name|filters1
operator|!=
literal|null
operator|)
operator|&&
name|filters1
operator|.
name|equals
argument_list|(
name|filters2
argument_list|)
operator|)
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
return|return
literal|true
return|;
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanFilter
name|other
init|=
operator|(
name|BooleanFilter
operator|)
name|obj
decl_stmt|;
return|return
name|equalFilters
argument_list|(
name|notFilters
argument_list|,
name|other
operator|.
name|notFilters
argument_list|)
operator|&&
name|equalFilters
argument_list|(
name|mustFilters
argument_list|,
name|other
operator|.
name|mustFilters
argument_list|)
operator|&&
name|equalFilters
argument_list|(
name|shouldFilters
argument_list|,
name|other
operator|.
name|shouldFilters
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
name|hash
init|=
literal|7
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
operator|(
literal|null
operator|==
name|mustFilters
condition|?
literal|0
else|:
name|mustFilters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
operator|(
literal|null
operator|==
name|notFilters
condition|?
literal|0
else|:
name|notFilters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
operator|(
literal|null
operator|==
name|shouldFilters
condition|?
literal|0
else|:
name|shouldFilters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"BooleanFilter("
argument_list|)
expr_stmt|;
name|appendFilters
argument_list|(
name|shouldFilters
argument_list|,
literal|""
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|appendFilters
argument_list|(
name|mustFilters
argument_list|,
literal|"+"
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|appendFilters
argument_list|(
name|notFilters
argument_list|,
literal|"-"
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|appendFilters
specifier|private
name|void
name|appendFilters
parameter_list|(
name|ArrayList
argument_list|<
name|Filter
argument_list|>
name|filters
parameter_list|,
name|String
name|occurString
parameter_list|,
name|StringBuilder
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|occurString
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|filters
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

