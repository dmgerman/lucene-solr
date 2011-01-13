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
comment|/**  * Copyright 2007 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|Spans
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
name|List
import|;
end_import

begin_comment
comment|/**  * Constrains search results to only match those which also match a provided  * query. Also provides position information about where each document matches  * at the cost of extra space compared with the QueryWrapperFilter.  * There is an added cost to this above what is stored in a {@link QueryWrapperFilter}.  Namely,  * the position information for each matching document is stored.  *<p/>  * This filter does not cache.  See the {@link org.apache.lucene.search.CachingSpanFilter} for a wrapper that  * caches.  */
end_comment

begin_class
DECL|class|SpanQueryFilter
specifier|public
class|class
name|SpanQueryFilter
extends|extends
name|SpanFilter
block|{
DECL|field|query
specifier|protected
name|SpanQuery
name|query
decl_stmt|;
DECL|method|SpanQueryFilter
specifier|protected
name|SpanQueryFilter
parameter_list|()
block|{        }
comment|/** Constructs a filter which only matches documents matching    *<code>query</code>.    * @param query The {@link org.apache.lucene.search.spans.SpanQuery} to use as the basis for the Filter.    */
DECL|method|SpanQueryFilter
specifier|public
name|SpanQueryFilter
parameter_list|(
name|SpanQuery
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanFilterResult
name|result
init|=
name|bitSpans
argument_list|(
name|context
operator|.
name|reader
argument_list|)
decl_stmt|;
return|return
name|result
operator|.
name|getDocIdSet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|bitSpans
specifier|public
name|SpanFilterResult
name|bitSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|OpenBitSet
name|bits
init|=
operator|new
name|OpenBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|Spans
name|spans
init|=
name|query
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SpanFilterResult
operator|.
name|PositionInfo
argument_list|>
name|tmp
init|=
operator|new
name|ArrayList
argument_list|<
name|SpanFilterResult
operator|.
name|PositionInfo
argument_list|>
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
name|SpanFilterResult
operator|.
name|PositionInfo
name|currentInfo
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|spans
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|doc
init|=
name|spans
operator|.
name|doc
argument_list|()
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentDoc
operator|!=
name|doc
condition|)
block|{
name|currentInfo
operator|=
operator|new
name|SpanFilterResult
operator|.
name|PositionInfo
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|currentInfo
argument_list|)
expr_stmt|;
name|currentDoc
operator|=
name|doc
expr_stmt|;
block|}
name|currentInfo
operator|.
name|addPosition
argument_list|(
name|spans
operator|.
name|start
argument_list|()
argument_list|,
name|spans
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SpanFilterResult
argument_list|(
name|bits
argument_list|,
name|tmp
argument_list|)
return|;
block|}
DECL|method|getQuery
specifier|public
name|SpanQuery
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SpanQueryFilter("
operator|+
name|query
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
return|return
name|o
operator|instanceof
name|SpanQueryFilter
operator|&&
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SpanQueryFilter
operator|)
name|o
operator|)
operator|.
name|query
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
name|query
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x923F64B9
return|;
block|}
block|}
end_class

end_unit

