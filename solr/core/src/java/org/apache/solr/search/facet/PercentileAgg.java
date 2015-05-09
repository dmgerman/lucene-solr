begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
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
name|nio
operator|.
name|ByteBuffer
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
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|tdunning
operator|.
name|math
operator|.
name|stats
operator|.
name|AVLTreeDigest
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
name|solr
operator|.
name|search
operator|.
name|FunctionQParser
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
name|search
operator|.
name|SyntaxError
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
name|search
operator|.
name|ValueSourceParser
import|;
end_import

begin_class
DECL|class|PercentileAgg
specifier|public
class|class
name|PercentileAgg
extends|extends
name|SimpleAggValueSource
block|{
DECL|field|percentiles
name|List
argument_list|<
name|Double
argument_list|>
name|percentiles
decl_stmt|;
DECL|method|PercentileAgg
specifier|public
name|PercentileAgg
parameter_list|(
name|ValueSource
name|vs
parameter_list|,
name|List
argument_list|<
name|Double
argument_list|>
name|percentiles
parameter_list|)
block|{
name|super
argument_list|(
literal|"percentile"
argument_list|,
name|vs
argument_list|)
expr_stmt|;
name|this
operator|.
name|percentiles
operator|=
name|percentiles
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSlotAcc
specifier|public
name|SlotAcc
name|createSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Acc
argument_list|(
name|getArg
argument_list|()
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFacetMerger
specifier|public
name|FacetMerger
name|createFacetMerger
parameter_list|(
name|Object
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|Merger
argument_list|()
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
operator|!
operator|(
name|o
operator|instanceof
name|PercentileAgg
operator|)
condition|)
return|return
literal|false
return|;
name|PercentileAgg
name|other
init|=
operator|(
name|PercentileAgg
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|arg
operator|.
name|equals
argument_list|(
name|other
operator|.
name|arg
argument_list|)
operator|&&
name|this
operator|.
name|percentiles
operator|.
name|equals
argument_list|(
name|other
operator|.
name|percentiles
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
name|super
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|percentiles
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|class|Parser
specifier|public
specifier|static
class|class
name|Parser
extends|extends
name|ValueSourceParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|List
argument_list|<
name|Double
argument_list|>
name|percentiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ValueSource
name|vs
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
while|while
condition|(
name|fp
operator|.
name|hasMoreArguments
argument_list|()
condition|)
block|{
name|double
name|val
init|=
name|fp
operator|.
name|parseDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
argument_list|<
literal|0
operator|||
name|val
argument_list|>
literal|100
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"requested percentile must be between 0 and 100.  got "
operator|+
name|val
argument_list|)
throw|;
block|}
name|percentiles
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|percentiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"expected percentile(valsource,percent1[,percent2]*)  EXAMPLE:percentile(myfield,50)"
argument_list|)
throw|;
block|}
return|return
operator|new
name|PercentileAgg
argument_list|(
name|vs
argument_list|,
name|percentiles
argument_list|)
return|;
block|}
block|}
DECL|method|getValueFromDigest
specifier|protected
name|Object
name|getValueFromDigest
parameter_list|(
name|AVLTreeDigest
name|digest
parameter_list|)
block|{
if|if
condition|(
name|digest
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|percentiles
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|digest
operator|.
name|quantile
argument_list|(
name|percentiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|*
literal|0.01
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Double
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|(
name|percentiles
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Double
name|percentile
range|:
name|percentiles
control|)
block|{
name|double
name|val
init|=
name|digest
operator|.
name|quantile
argument_list|(
name|percentile
operator|*
literal|0.01
argument_list|)
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|lst
return|;
block|}
DECL|class|Acc
class|class
name|Acc
extends|extends
name|FuncSlotAcc
block|{
DECL|field|digests
specifier|protected
name|AVLTreeDigest
index|[]
name|digests
decl_stmt|;
DECL|field|buf
specifier|protected
name|ByteBuffer
name|buf
decl_stmt|;
DECL|field|sortvals
specifier|protected
name|double
index|[]
name|sortvals
decl_stmt|;
DECL|method|Acc
specifier|public
name|Acc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
name|digests
operator|=
operator|new
name|AVLTreeDigest
index|[
name|numSlots
index|]
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
if|if
condition|(
operator|!
name|values
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
return|return;
name|double
name|val
init|=
name|values
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|AVLTreeDigest
name|digest
init|=
name|digests
index|[
name|slotNum
index|]
decl_stmt|;
if|if
condition|(
name|digest
operator|==
literal|null
condition|)
block|{
name|digests
index|[
name|slotNum
index|]
operator|=
name|digest
operator|=
operator|new
name|AVLTreeDigest
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// TODO: make compression configurable
block|}
name|digest
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
if|if
condition|(
name|sortvals
operator|==
literal|null
condition|)
block|{
name|fillSortVals
argument_list|()
expr_stmt|;
block|}
return|return
name|Double
operator|.
name|compare
argument_list|(
name|sortvals
index|[
name|slotA
index|]
argument_list|,
name|sortvals
index|[
name|slotB
index|]
argument_list|)
return|;
block|}
DECL|method|fillSortVals
specifier|private
name|void
name|fillSortVals
parameter_list|()
block|{
name|sortvals
operator|=
operator|new
name|double
index|[
name|digests
operator|.
name|length
index|]
expr_stmt|;
name|double
name|sortp
init|=
name|percentiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|*
literal|0.01
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
name|digests
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|AVLTreeDigest
name|digest
init|=
name|digests
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|digest
operator|==
literal|null
condition|)
block|{
name|sortvals
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
else|else
block|{
name|sortvals
index|[
name|i
index|]
operator|=
name|digest
operator|.
name|quantile
argument_list|(
name|sortp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fcontext
operator|.
name|isShard
argument_list|()
condition|)
block|{
return|return
name|getShardValue
argument_list|(
name|slotNum
argument_list|)
return|;
block|}
if|if
condition|(
name|sortvals
operator|!=
literal|null
operator|&&
name|percentiles
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// we've already calculated everything we need
return|return
name|sortvals
index|[
name|slotNum
index|]
return|;
block|}
return|return
name|getValueFromDigest
argument_list|(
name|digests
index|[
name|slotNum
index|]
argument_list|)
return|;
block|}
DECL|method|getShardValue
specifier|public
name|Object
name|getShardValue
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|AVLTreeDigest
name|digest
init|=
name|digests
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|digest
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// no values for this slot
name|digest
operator|.
name|compress
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|digest
operator|.
name|byteSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|buf
operator|==
literal|null
operator|||
name|buf
operator|.
name|capacity
argument_list|()
operator|<
name|sz
condition|)
block|{
name|buf
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|sz
operator|+
operator|(
name|sz
operator|>>
literal|1
operator|)
argument_list|)
expr_stmt|;
comment|// oversize by 50%
block|}
else|else
block|{
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|digest
operator|.
name|asSmallBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|byte
index|[]
name|arr
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|buf
operator|.
name|array
argument_list|()
argument_list|,
name|buf
operator|.
name|position
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|digests
operator|=
operator|new
name|AVLTreeDigest
index|[
name|digests
operator|.
name|length
index|]
expr_stmt|;
name|sortvals
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
name|digests
operator|=
name|resizer
operator|.
name|resize
argument_list|(
name|digests
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Merger
class|class
name|Merger
extends|extends
name|FacetSortableMerger
block|{
DECL|field|digest
specifier|protected
name|AVLTreeDigest
name|digest
decl_stmt|;
DECL|field|sortVal
specifier|protected
name|Double
name|sortVal
decl_stmt|;
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Object
name|facetResult
parameter_list|)
block|{
name|byte
index|[]
name|arr
init|=
operator|(
name|byte
index|[]
operator|)
name|facetResult
decl_stmt|;
name|AVLTreeDigest
name|subDigest
init|=
name|AVLTreeDigest
operator|.
name|fromBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|arr
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|digest
operator|==
literal|null
condition|)
block|{
name|digest
operator|=
name|subDigest
expr_stmt|;
block|}
else|else
block|{
name|digest
operator|.
name|add
argument_list|(
name|subDigest
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMergedResult
specifier|public
name|Object
name|getMergedResult
parameter_list|()
block|{
if|if
condition|(
name|percentiles
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
return|return
name|getSortVal
argument_list|()
return|;
return|return
name|getValueFromDigest
argument_list|(
name|digest
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|FacetSortableMerger
name|other
parameter_list|,
name|FacetField
operator|.
name|SortDirection
name|direction
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|getSortVal
argument_list|()
argument_list|,
operator|(
operator|(
name|Merger
operator|)
name|other
operator|)
operator|.
name|getSortVal
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getSortVal
specifier|private
name|Double
name|getSortVal
parameter_list|()
block|{
if|if
condition|(
name|sortVal
operator|==
literal|null
condition|)
block|{
name|sortVal
operator|=
name|digest
operator|.
name|quantile
argument_list|(
name|percentiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|*
literal|0.01
argument_list|)
expr_stmt|;
block|}
return|return
name|sortVal
return|;
block|}
block|}
block|}
end_class

end_unit

