begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.range
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|range
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
name|Collections
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
name|Filter
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
name|FilteredDocIdSet
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
name|NumericUtils
import|;
end_import

begin_comment
comment|/** Represents a range over double values.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|DoubleRange
specifier|public
specifier|final
class|class
name|DoubleRange
extends|extends
name|Range
block|{
DECL|field|minIncl
specifier|final
name|double
name|minIncl
decl_stmt|;
DECL|field|maxIncl
specifier|final
name|double
name|maxIncl
decl_stmt|;
comment|/** Minimum. */
DECL|field|min
specifier|public
specifier|final
name|double
name|min
decl_stmt|;
comment|/** Maximum. */
DECL|field|max
specifier|public
specifier|final
name|double
name|max
decl_stmt|;
comment|/** True if the minimum value is inclusive. */
DECL|field|minInclusive
specifier|public
specifier|final
name|boolean
name|minInclusive
decl_stmt|;
comment|/** True if the maximum value is inclusive. */
DECL|field|maxInclusive
specifier|public
specifier|final
name|boolean
name|maxInclusive
decl_stmt|;
comment|/** Create a DoubleRange. */
DECL|method|DoubleRange
specifier|public
name|DoubleRange
parameter_list|(
name|String
name|label
parameter_list|,
name|double
name|minIn
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|double
name|maxIn
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|super
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|minIn
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|maxIn
expr_stmt|;
name|this
operator|.
name|minInclusive
operator|=
name|minInclusive
expr_stmt|;
name|this
operator|.
name|maxInclusive
operator|=
name|maxInclusive
expr_stmt|;
comment|// TODO: if DoubleDocValuesField used
comment|// NumericUtils.doubleToSortableLong format (instead of
comment|// Double.doubleToRawLongBits) we could do comparisons
comment|// in long space
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|min
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"min cannot be NaN"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|minInclusive
condition|)
block|{
name|minIn
operator|=
name|Math
operator|.
name|nextUp
argument_list|(
name|minIn
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|max
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"max cannot be NaN"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|maxInclusive
condition|)
block|{
comment|// Why no Math.nextDown?
name|maxIn
operator|=
name|Math
operator|.
name|nextAfter
argument_list|(
name|maxIn
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minIn
operator|>
name|maxIn
condition|)
block|{
name|failNoMatch
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|minIncl
operator|=
name|minIn
expr_stmt|;
name|this
operator|.
name|maxIncl
operator|=
name|maxIn
expr_stmt|;
block|}
comment|/** True if this range accepts the provided value. */
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|value
operator|>=
name|minIncl
operator|&&
name|value
operator|<=
name|maxIncl
return|;
block|}
DECL|method|toLongRange
name|LongRange
name|toLongRange
parameter_list|()
block|{
return|return
operator|new
name|LongRange
argument_list|(
name|label
argument_list|,
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|minIncl
argument_list|)
argument_list|,
literal|true
argument_list|,
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|maxIncl
argument_list|)
argument_list|,
literal|true
argument_list|)
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
literal|"DoubleRange("
operator|+
name|minIncl
operator|+
literal|" to "
operator|+
name|maxIncl
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|(
specifier|final
name|Filter
name|fastMatchFilter
parameter_list|,
specifier|final
name|ValueSource
name|valueSource
parameter_list|)
block|{
return|return
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"Filter("
operator|+
name|DoubleRange
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: this is just like ValueSourceScorer,
comment|// ValueSourceFilter (spatial),
comment|// ValueSourceRangeFilter (solr); also,
comment|// https://issues.apache.org/jira/browse/LUCENE-4251
specifier|final
name|FunctionValues
name|values
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSet
name|fastMatchDocs
decl_stmt|;
if|if
condition|(
name|fastMatchFilter
operator|!=
literal|null
condition|)
block|{
name|fastMatchDocs
operator|=
name|fastMatchFilter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|fastMatchDocs
operator|==
literal|null
condition|)
block|{
comment|// No documents match
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
name|fastMatchDocs
operator|=
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|maxDoc
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
return|return
operator|new
name|FilteredDocIdSet
argument_list|(
name|fastMatchDocs
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|acceptDocs
operator|!=
literal|null
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|accept
argument_list|(
name|values
operator|.
name|doubleVal
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

