begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|util
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|PriorityQueue
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
name|facet
operator|.
name|search
operator|.
name|Heap
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetRequest
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetRequest
operator|.
name|SortOrder
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResultNode
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Utilities for generating facet results sorted as required  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|ResultSortUtils
specifier|public
class|class
name|ResultSortUtils
block|{
comment|/**    * Create a suitable heap according to facet request being served.     * @return heap for maintaining results for specified request.    * @throws IllegalArgumentException is provided facet request is not supported     */
DECL|method|createSuitableHeap
specifier|public
specifier|static
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|createSuitableHeap
parameter_list|(
name|FacetRequest
name|facetRequest
parameter_list|)
block|{
name|int
name|nresults
init|=
name|facetRequest
operator|.
name|getNumResults
argument_list|()
decl_stmt|;
name|boolean
name|accending
init|=
operator|(
name|facetRequest
operator|.
name|getSortOrder
argument_list|()
operator|==
name|SortOrder
operator|.
name|ASCENDING
operator|)
decl_stmt|;
if|if
condition|(
name|nresults
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
operator|new
name|AllValueHeap
argument_list|(
name|accending
argument_list|)
return|;
block|}
if|if
condition|(
name|accending
condition|)
block|{
switch|switch
condition|(
name|facetRequest
operator|.
name|getSortBy
argument_list|()
condition|)
block|{
case|case
name|VALUE
case|:
return|return
operator|new
name|MaxValueHeap
argument_list|(
name|nresults
argument_list|)
return|;
case|case
name|ORDINAL
case|:
return|return
operator|new
name|MaxOrdinalHeap
argument_list|(
name|nresults
argument_list|)
return|;
block|}
block|}
else|else
block|{
switch|switch
condition|(
name|facetRequest
operator|.
name|getSortBy
argument_list|()
condition|)
block|{
case|case
name|VALUE
case|:
return|return
operator|new
name|MinValueHeap
argument_list|(
name|nresults
argument_list|)
return|;
case|case
name|ORDINAL
case|:
return|return
operator|new
name|MinOrdinalHeap
argument_list|(
name|nresults
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"none supported facet request: "
operator|+
name|facetRequest
argument_list|)
throw|;
block|}
DECL|class|MinValueHeap
specifier|private
specifier|static
class|class
name|MinValueHeap
extends|extends
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
implements|implements
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|method|MinValueHeap
specifier|public
name|MinValueHeap
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FacetResultNode
name|arg0
parameter_list|,
name|FacetResultNode
name|arg1
parameter_list|)
block|{
name|double
name|value0
init|=
name|arg0
operator|.
name|value
decl_stmt|;
name|double
name|value1
init|=
name|arg1
operator|.
name|value
decl_stmt|;
name|int
name|valueCompare
init|=
name|Double
operator|.
name|compare
argument_list|(
name|value0
argument_list|,
name|value1
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueCompare
operator|==
literal|0
condition|)
block|{
return|return
name|arg0
operator|.
name|ordinal
operator|<
name|arg1
operator|.
name|ordinal
return|;
block|}
return|return
name|valueCompare
operator|<
literal|0
return|;
block|}
block|}
DECL|class|MaxValueHeap
specifier|private
specifier|static
class|class
name|MaxValueHeap
extends|extends
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
implements|implements
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|method|MaxValueHeap
specifier|public
name|MaxValueHeap
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FacetResultNode
name|arg0
parameter_list|,
name|FacetResultNode
name|arg1
parameter_list|)
block|{
name|double
name|value0
init|=
name|arg0
operator|.
name|value
decl_stmt|;
name|double
name|value1
init|=
name|arg1
operator|.
name|value
decl_stmt|;
name|int
name|valueCompare
init|=
name|Double
operator|.
name|compare
argument_list|(
name|value0
argument_list|,
name|value1
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueCompare
operator|==
literal|0
condition|)
block|{
return|return
name|arg0
operator|.
name|ordinal
operator|>
name|arg1
operator|.
name|ordinal
return|;
block|}
return|return
name|valueCompare
operator|>
literal|0
return|;
block|}
block|}
DECL|class|MinOrdinalHeap
specifier|private
specifier|static
class|class
name|MinOrdinalHeap
extends|extends
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
implements|implements
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|method|MinOrdinalHeap
specifier|public
name|MinOrdinalHeap
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FacetResultNode
name|arg0
parameter_list|,
name|FacetResultNode
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|ordinal
operator|<
name|arg1
operator|.
name|ordinal
return|;
block|}
block|}
DECL|class|MaxOrdinalHeap
specifier|private
specifier|static
class|class
name|MaxOrdinalHeap
extends|extends
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
implements|implements
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|method|MaxOrdinalHeap
specifier|public
name|MaxOrdinalHeap
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FacetResultNode
name|arg0
parameter_list|,
name|FacetResultNode
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|ordinal
operator|>
name|arg1
operator|.
name|ordinal
return|;
block|}
block|}
comment|/**    * Create a Heap-Look-Alike, which implements {@link Heap}, but uses a    * regular<code>ArrayList</code> for holding<b>ALL</b> the objects given,    * only sorting upon the first call to {@link #pop()}.    */
DECL|class|AllValueHeap
specifier|private
specifier|static
class|class
name|AllValueHeap
implements|implements
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|field|resultNodes
specifier|private
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|resultNodes
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|accending
specifier|final
name|boolean
name|accending
decl_stmt|;
DECL|field|isReady
specifier|private
name|boolean
name|isReady
init|=
literal|false
decl_stmt|;
DECL|method|AllValueHeap
specifier|public
name|AllValueHeap
parameter_list|(
name|boolean
name|accending
parameter_list|)
block|{
name|this
operator|.
name|accending
operator|=
name|accending
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|insertWithOverflow
specifier|public
name|FacetResultNode
name|insertWithOverflow
parameter_list|(
name|FacetResultNode
name|node
parameter_list|)
block|{
name|resultNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|pop
specifier|public
name|FacetResultNode
name|pop
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isReady
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|resultNodes
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResultNode
name|o1
parameter_list|,
name|FacetResultNode
name|o2
parameter_list|)
block|{
name|int
name|value
init|=
name|Double
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|value
argument_list|,
name|o2
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
name|value
operator|=
name|o1
operator|.
name|ordinal
operator|-
name|o2
operator|.
name|ordinal
expr_stmt|;
block|}
if|if
condition|(
name|accending
condition|)
block|{
name|value
operator|=
operator|-
name|value
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|isReady
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|resultNodes
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|resultNodes
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|top
specifier|public
name|FacetResultNode
name|top
parameter_list|()
block|{
if|if
condition|(
name|resultNodes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|resultNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|FacetResultNode
name|add
parameter_list|(
name|FacetResultNode
name|frn
parameter_list|)
block|{
name|resultNodes
operator|.
name|add
argument_list|(
name|frn
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|resultNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

