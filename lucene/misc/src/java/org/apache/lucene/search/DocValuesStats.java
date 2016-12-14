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
name|NumericDocValues
import|;
end_import

begin_comment
comment|/** Holds statistics for a DocValues field. */
end_comment

begin_class
DECL|class|DocValuesStats
specifier|public
specifier|abstract
class|class
name|DocValuesStats
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|missing
specifier|private
name|int
name|missing
init|=
literal|0
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|min
specifier|protected
name|T
name|min
decl_stmt|;
DECL|field|max
specifier|protected
name|T
name|max
decl_stmt|;
DECL|method|DocValuesStats
specifier|protected
name|DocValuesStats
parameter_list|(
name|String
name|field
parameter_list|,
name|T
name|initialMin
parameter_list|,
name|T
name|initialMax
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|initialMin
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|initialMax
expr_stmt|;
block|}
comment|/**    * Called after #{@link DocValuesStats#accumulate(int)} was processed and verified that the document has a value for    * the field. Implementations should update the statistics based on the value of the current document.    *    * @param count    *          the updated number of documents with value for this field.    */
DECL|method|doAccumulate
specifier|protected
specifier|abstract
name|void
name|doAccumulate
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Initializes this object with the given reader context. Returns whether stats can be computed for this segment (i.e.    * it does have the requested DocValues field).    */
DECL|method|init
specifier|protected
specifier|abstract
name|boolean
name|init
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns whether the given document has a value for the requested DocValues field. */
DECL|method|hasValue
specifier|protected
specifier|abstract
name|boolean
name|hasValue
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|accumulate
specifier|final
name|void
name|accumulate
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasValue
argument_list|(
name|doc
argument_list|)
condition|)
block|{
operator|++
name|count
expr_stmt|;
name|doAccumulate
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|++
name|missing
expr_stmt|;
block|}
block|}
DECL|method|addMissing
specifier|final
name|void
name|addMissing
parameter_list|()
block|{
operator|++
name|missing
expr_stmt|;
block|}
comment|/** The field for which these stats were computed. */
DECL|method|field
specifier|public
specifier|final
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** The number of documents which have a value of the field. */
DECL|method|count
specifier|public
specifier|final
name|int
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/** The number of documents which do not have a value of the field. */
DECL|method|missing
specifier|public
specifier|final
name|int
name|missing
parameter_list|()
block|{
return|return
name|missing
return|;
block|}
comment|/** The minimum value of the field. Undefined when {@link #count} is zero. */
DECL|method|min
specifier|public
specifier|final
name|T
name|min
parameter_list|()
block|{
return|return
name|min
return|;
block|}
comment|/** The maximum value of the field. Undefined when {@link #count} is zero. */
DECL|method|max
specifier|public
specifier|final
name|T
name|max
parameter_list|()
block|{
return|return
name|max
return|;
block|}
comment|/** Holds statistics for a numeric DocValues field. */
DECL|class|NumericDocValuesStats
specifier|public
specifier|static
specifier|abstract
class|class
name|NumericDocValuesStats
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|DocValuesStats
argument_list|<
name|T
argument_list|>
block|{
DECL|field|mean
specifier|protected
name|double
name|mean
init|=
literal|0.0
decl_stmt|;
DECL|field|ndv
specifier|protected
name|NumericDocValues
name|ndv
decl_stmt|;
DECL|method|NumericDocValuesStats
specifier|protected
name|NumericDocValuesStats
parameter_list|(
name|String
name|field
parameter_list|,
name|T
name|initialMin
parameter_list|,
name|T
name|initialMax
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|initialMin
argument_list|,
name|initialMax
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|protected
specifier|final
name|boolean
name|init
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ndv
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
return|return
name|ndv
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasValue
specifier|protected
name|boolean
name|hasValue
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ndv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/** The mean of all values of the field. Undefined when {@link #count} is zero. */
DECL|method|mean
specifier|public
specifier|final
name|double
name|mean
parameter_list|()
block|{
return|return
name|mean
return|;
block|}
block|}
comment|/** Holds DocValues statistics for a numeric field storing {@code long} values. */
DECL|class|LongDocValuesStats
specifier|public
specifier|static
specifier|final
class|class
name|LongDocValuesStats
extends|extends
name|NumericDocValuesStats
argument_list|<
name|Long
argument_list|>
block|{
DECL|method|LongDocValuesStats
specifier|public
name|LongDocValuesStats
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|description
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAccumulate
specifier|protected
name|void
name|doAccumulate
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|val
init|=
name|ndv
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|val
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|<
name|min
condition|)
block|{
name|min
operator|=
name|val
expr_stmt|;
block|}
name|mean
operator|+=
operator|(
name|val
operator|-
name|mean
operator|)
operator|/
name|count
expr_stmt|;
block|}
block|}
comment|/** Holds DocValues statistics for a numeric field storing {@code double} values. */
DECL|class|DoubleDocValuesStats
specifier|public
specifier|static
specifier|final
class|class
name|DoubleDocValuesStats
extends|extends
name|NumericDocValuesStats
argument_list|<
name|Double
argument_list|>
block|{
DECL|method|DoubleDocValuesStats
specifier|public
name|DoubleDocValuesStats
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|description
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAccumulate
specifier|protected
name|void
name|doAccumulate
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|val
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|ndv
operator|.
name|longValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|val
argument_list|,
name|max
argument_list|)
operator|>
literal|0
condition|)
block|{
name|max
operator|=
name|val
expr_stmt|;
block|}
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|val
argument_list|,
name|min
argument_list|)
operator|<
literal|0
condition|)
block|{
name|min
operator|=
name|val
expr_stmt|;
block|}
name|mean
operator|+=
operator|(
name|val
operator|-
name|mean
operator|)
operator|/
name|count
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

