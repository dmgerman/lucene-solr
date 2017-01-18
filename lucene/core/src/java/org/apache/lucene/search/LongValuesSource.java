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
comment|/**  * Base class for producing {@link LongValues}  *  * To obtain a {@link LongValues} object for a leaf reader, clients should  * call {@link #getValues(LeafReaderContext, DoubleValues)}.  *  * LongValuesSource objects for long and int-valued NumericDocValues fields can  * be obtained by calling {@link #fromLongField(String)} and {@link #fromIntField(String)}.  *  * To obtain a LongValuesSource from a float or double-valued NumericDocValues field,  * use {@link DoubleValuesSource#fromFloatField(String)} or {@link DoubleValuesSource#fromDoubleField(String)}  * and then call {@link DoubleValuesSource#toLongValuesSource()}.  */
end_comment

begin_class
DECL|class|LongValuesSource
specifier|public
specifier|abstract
class|class
name|LongValuesSource
block|{
comment|/**    * Returns a {@link LongValues} instance for the passed-in LeafReaderContext and scores    *    * If scores are not needed to calculate the values (ie {@link #needsScores() returns false}, callers    * may safely pass {@code null} for the {@code scores} parameter.    */
DECL|method|getValues
specifier|public
specifier|abstract
name|LongValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|DoubleValues
name|scores
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return true if document scores are needed to calculate values    */
DECL|method|needsScores
specifier|public
specifier|abstract
name|boolean
name|needsScores
parameter_list|()
function_decl|;
comment|/**    * Create a sort field based on the value of this producer    * @param reverse true if the sort should be decreasing    */
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|LongValuesSortField
argument_list|(
name|this
argument_list|,
name|reverse
argument_list|)
return|;
block|}
comment|/**    * Creates a LongValuesSource that wraps a long-valued field    */
DECL|method|fromLongField
specifier|public
specifier|static
name|LongValuesSource
name|fromLongField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|FieldValuesSource
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/**    * Creates a LongValuesSource that wraps an int-valued field    */
DECL|method|fromIntField
specifier|public
specifier|static
name|LongValuesSource
name|fromIntField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fromLongField
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/**    * Creates a LongValuesSource that always returns a constant value    */
DECL|method|constant
specifier|public
specifier|static
name|LongValuesSource
name|constant
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|LongValuesSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LongValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|DoubleValues
name|scores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
DECL|class|FieldValuesSource
specifier|private
specifier|static
class|class
name|FieldValuesSource
extends|extends
name|LongValuesSource
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|FieldValuesSource
specifier|private
name|FieldValuesSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
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
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FieldValuesSource
name|that
init|=
operator|(
name|FieldValuesSource
operator|)
name|o
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
name|Objects
operator|.
name|hash
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|LongValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|DoubleValues
name|scores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NumericDocValues
name|values
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
name|toLongValues
argument_list|(
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|LongValuesSortField
specifier|private
specifier|static
class|class
name|LongValuesSortField
extends|extends
name|SortField
block|{
DECL|field|producer
specifier|final
name|LongValuesSource
name|producer
decl_stmt|;
DECL|method|LongValuesSortField
specifier|public
name|LongValuesSortField
parameter_list|(
name|LongValuesSource
name|producer
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|super
argument_list|(
name|producer
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|LongValuesComparatorSource
argument_list|(
name|producer
argument_list|)
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|producer
operator|.
name|needsScores
argument_list|()
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
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<"
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverse
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|"!"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|LongValuesHolder
specifier|private
specifier|static
class|class
name|LongValuesHolder
block|{
DECL|field|values
name|LongValues
name|values
decl_stmt|;
block|}
DECL|class|LongValuesComparatorSource
specifier|private
specifier|static
class|class
name|LongValuesComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|field|producer
specifier|private
specifier|final
name|LongValuesSource
name|producer
decl_stmt|;
DECL|method|LongValuesComparatorSource
specifier|public
name|LongValuesComparatorSource
parameter_list|(
name|LongValuesSource
name|producer
parameter_list|)
block|{
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|Long
argument_list|>
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
block|{
return|return
operator|new
name|FieldComparator
operator|.
name|LongComparator
argument_list|(
name|numHits
argument_list|,
name|fieldname
argument_list|,
literal|0L
argument_list|)
block|{
name|LeafReaderContext
name|ctx
decl_stmt|;
name|LongValuesHolder
name|holder
init|=
operator|new
name|LongValuesHolder
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ctx
operator|=
name|context
expr_stmt|;
return|return
name|asNumericDocValues
argument_list|(
name|holder
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|holder
operator|.
name|values
operator|=
name|producer
operator|.
name|getValues
argument_list|(
name|ctx
argument_list|,
name|DoubleValuesSource
operator|.
name|fromScorer
argument_list|(
name|scorer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
DECL|method|toLongValues
specifier|private
specifier|static
name|LongValues
name|toLongValues
parameter_list|(
name|NumericDocValues
name|in
parameter_list|)
block|{
return|return
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|longValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advanceExact
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|asNumericDocValues
specifier|private
specifier|static
name|NumericDocValues
name|asNumericDocValues
parameter_list|(
name|LongValuesHolder
name|in
parameter_list|)
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|values
operator|.
name|longValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|values
operator|.
name|advanceExact
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

