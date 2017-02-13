begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericDocValuesField
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
name|document
operator|.
name|SortedNumericDocValuesField
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
name|MatchNoDocsQuery
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
name|util
operator|.
name|NumericUtils
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
name|common
operator|.
name|SolrException
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
name|FunctionRangeQuery
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
name|QParser
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
name|function
operator|.
name|ValueSourceRangeFilter
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
name|util
operator|.
name|DateMathParser
import|;
end_import

begin_class
DECL|class|NumericFieldType
specifier|public
specifier|abstract
class|class
name|NumericFieldType
extends|extends
name|PrimitiveFieldType
block|{
DECL|field|type
specifier|protected
name|NumberType
name|type
decl_stmt|;
comment|/**    * @return the type of this field    */
annotation|@
name|Override
DECL|method|getNumberType
specifier|public
name|NumberType
name|getNumberType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|field|FLOAT_NEGATIVE_INFINITY_BITS
specifier|private
specifier|static
name|long
name|FLOAT_NEGATIVE_INFINITY_BITS
init|=
operator|(
name|long
operator|)
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
decl_stmt|;
DECL|field|DOUBLE_NEGATIVE_INFINITY_BITS
specifier|private
specifier|static
name|long
name|DOUBLE_NEGATIVE_INFINITY_BITS
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
decl_stmt|;
DECL|field|FLOAT_POSITIVE_INFINITY_BITS
specifier|private
specifier|static
name|long
name|FLOAT_POSITIVE_INFINITY_BITS
init|=
operator|(
name|long
operator|)
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
decl_stmt|;
DECL|field|DOUBLE_POSITIVE_INFINITY_BITS
specifier|private
specifier|static
name|long
name|DOUBLE_POSITIVE_INFINITY_BITS
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
decl_stmt|;
DECL|field|FLOAT_MINUS_ZERO_BITS
specifier|private
specifier|static
name|long
name|FLOAT_MINUS_ZERO_BITS
init|=
operator|(
name|long
operator|)
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|-
literal|0f
argument_list|)
decl_stmt|;
DECL|field|DOUBLE_MINUS_ZERO_BITS
specifier|private
specifier|static
name|long
name|DOUBLE_MINUS_ZERO_BITS
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
operator|-
literal|0d
argument_list|)
decl_stmt|;
DECL|field|FLOAT_ZERO_BITS
specifier|private
specifier|static
name|long
name|FLOAT_ZERO_BITS
init|=
operator|(
name|long
operator|)
name|Float
operator|.
name|floatToIntBits
argument_list|(
literal|0f
argument_list|)
decl_stmt|;
DECL|field|DOUBLE_ZERO_BITS
specifier|private
specifier|static
name|long
name|DOUBLE_ZERO_BITS
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
literal|0d
argument_list|)
decl_stmt|;
DECL|method|getDocValuesRangeQuery
specifier|protected
name|Query
name|getDocValuesRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
assert|assert
name|field
operator|.
name|hasDocValues
argument_list|()
operator|&&
operator|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|isPointField
argument_list|()
operator|||
operator|!
name|field
operator|.
name|multiValued
argument_list|()
operator|)
assert|;
switch|switch
condition|(
name|getNumberType
argument_list|()
condition|)
block|{
case|case
name|INTEGER
case|:
return|return
name|numericDocValuesRangeQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|long
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|min
argument_list|)
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|long
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|max
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
name|field
operator|.
name|multiValued
argument_list|()
argument_list|)
return|;
case|case
name|FLOAT
case|:
if|if
condition|(
name|field
operator|.
name|multiValued
argument_list|()
condition|)
block|{
return|return
name|getRangeQueryForMultiValuedFloatDocValues
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getRangeQueryForFloatDoubleDocValues
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
case|case
name|LONG
case|:
return|return
name|numericDocValuesRangeQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|min
argument_list|)
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|max
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
name|field
operator|.
name|multiValued
argument_list|()
argument_list|)
return|;
case|case
name|DOUBLE
case|:
if|if
condition|(
name|field
operator|.
name|multiValued
argument_list|()
condition|)
block|{
return|return
name|getRangeQueryForMultiValuedDoubleDocValues
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getRangeQueryForFloatDoubleDocValues
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
case|case
name|DATE
case|:
return|return
name|numericDocValuesRangeQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|DateMathParser
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|min
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|DateMathParser
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|max
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
name|field
operator|.
name|multiValued
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unknown type for numeric field"
argument_list|)
throw|;
block|}
block|}
DECL|method|getRangeQueryForFloatDoubleDocValues
specifier|protected
name|Query
name|getRangeQueryForFloatDoubleDocValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|Query
name|query
decl_stmt|;
name|String
name|fieldName
init|=
name|sf
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Number
name|minVal
init|=
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
name|Float
operator|.
name|parseFloat
argument_list|(
name|min
argument_list|)
else|:
name|Double
operator|.
name|parseDouble
argument_list|(
name|min
argument_list|)
decl_stmt|;
name|Number
name|maxVal
init|=
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
name|Float
operator|.
name|parseFloat
argument_list|(
name|max
argument_list|)
else|:
name|Double
operator|.
name|parseDouble
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|Long
name|minBits
init|=
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
operator|(
name|long
operator|)
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|minVal
operator|.
name|floatValue
argument_list|()
argument_list|)
else|:
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|minVal
operator|.
name|doubleValue
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|maxBits
init|=
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
operator|(
name|long
operator|)
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|maxVal
operator|.
name|floatValue
argument_list|()
argument_list|)
else|:
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|maxVal
operator|.
name|doubleValue
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|negativeInfinityBits
init|=
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
name|FLOAT_NEGATIVE_INFINITY_BITS
else|:
name|DOUBLE_NEGATIVE_INFINITY_BITS
decl_stmt|;
name|long
name|positiveInfinityBits
init|=
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
name|FLOAT_POSITIVE_INFINITY_BITS
else|:
name|DOUBLE_POSITIVE_INFINITY_BITS
decl_stmt|;
name|long
name|minusZeroBits
init|=
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
name|FLOAT_MINUS_ZERO_BITS
else|:
name|DOUBLE_MINUS_ZERO_BITS
decl_stmt|;
name|long
name|zeroBits
init|=
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|FLOAT
condition|?
name|FLOAT_ZERO_BITS
else|:
name|DOUBLE_ZERO_BITS
decl_stmt|;
comment|// If min is negative (or -0d) and max is positive (or +0d), then issue a FunctionRangeQuery
if|if
condition|(
operator|(
name|minVal
operator|==
literal|null
operator|||
name|minVal
operator|.
name|doubleValue
argument_list|()
operator|<
literal|0d
operator|||
name|minBits
operator|==
name|minusZeroBits
operator|)
operator|&&
operator|(
name|maxVal
operator|==
literal|null
operator|||
operator|(
name|maxVal
operator|.
name|doubleValue
argument_list|()
operator|>
literal|0d
operator|||
name|maxBits
operator|==
name|zeroBits
operator|)
operator|)
condition|)
block|{
name|ValueSource
name|vs
init|=
name|getValueSource
argument_list|(
name|sf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|query
operator|=
operator|new
name|FunctionRangeQuery
argument_list|(
operator|new
name|ValueSourceRangeFilter
argument_list|(
name|vs
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If both max and min are negative (or -0d), then issue range query with max and min reversed
if|if
condition|(
operator|(
name|minVal
operator|==
literal|null
operator|||
name|minVal
operator|.
name|doubleValue
argument_list|()
operator|<
literal|0d
operator|||
name|minBits
operator|==
name|minusZeroBits
operator|)
operator|&&
operator|(
name|maxVal
operator|!=
literal|null
operator|&&
operator|(
name|maxVal
operator|.
name|doubleValue
argument_list|()
operator|<
literal|0d
operator|||
name|maxBits
operator|==
name|minusZeroBits
operator|)
operator|)
condition|)
block|{
name|query
operator|=
name|numericDocValuesRangeQuery
argument_list|(
name|fieldName
argument_list|,
name|maxBits
argument_list|,
operator|(
name|min
operator|==
literal|null
condition|?
name|negativeInfinityBits
else|:
name|minBits
operator|)
argument_list|,
name|maxInclusive
argument_list|,
name|minInclusive
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If both max and min are positive, then issue range query
name|query
operator|=
name|numericDocValuesRangeQuery
argument_list|(
name|fieldName
argument_list|,
name|minBits
argument_list|,
operator|(
name|max
operator|==
literal|null
condition|?
name|positiveInfinityBits
else|:
name|maxBits
operator|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|query
return|;
block|}
DECL|method|getRangeQueryForMultiValuedDoubleDocValues
specifier|protected
name|Query
name|getRangeQueryForMultiValuedDoubleDocValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|Long
name|minBits
init|=
name|min
operator|==
literal|null
condition|?
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
else|:
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|min
argument_list|)
argument_list|)
decl_stmt|;
name|Long
name|maxBits
init|=
name|max
operator|==
literal|null
condition|?
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
else|:
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|max
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|numericDocValuesRangeQuery
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|minBits
argument_list|,
name|maxBits
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getRangeQueryForMultiValuedFloatDocValues
specifier|protected
name|Query
name|getRangeQueryForMultiValuedFloatDocValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|Long
name|minBits
init|=
call|(
name|long
call|)
argument_list|(
name|min
operator|==
literal|null
condition|?
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
else|:
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|min
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Long
name|maxBits
init|=
call|(
name|long
call|)
argument_list|(
name|max
operator|==
literal|null
condition|?
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
else|:
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|max
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|numericDocValuesRangeQuery
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|minBits
argument_list|,
name|maxBits
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|numericDocValuesRangeQuery
specifier|public
specifier|static
name|Query
name|numericDocValuesRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Number
name|lowerValue
parameter_list|,
name|Number
name|upperValue
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|,
name|boolean
name|multiValued
parameter_list|)
block|{
name|long
name|actualLowerValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
if|if
condition|(
name|lowerValue
operator|!=
literal|null
condition|)
block|{
name|actualLowerValue
operator|=
name|lowerValue
operator|.
name|longValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lowerInclusive
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|actualLowerValue
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
operator|++
name|actualLowerValue
expr_stmt|;
block|}
block|}
name|long
name|actualUpperValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|upperValue
operator|!=
literal|null
condition|)
block|{
name|actualUpperValue
operator|=
name|upperValue
operator|.
name|longValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|upperInclusive
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|actualUpperValue
operator|==
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
operator|--
name|actualUpperValue
expr_stmt|;
block|}
block|}
if|if
condition|(
name|multiValued
condition|)
block|{
comment|// In multiValued case use SortedNumericDocValuesField, this won't work for Trie*Fields wince they use BinaryDV in the multiValue case
return|return
name|SortedNumericDocValuesField
operator|.
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|actualLowerValue
argument_list|,
name|actualUpperValue
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|NumericDocValuesField
operator|.
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|actualLowerValue
argument_list|,
name|actualUpperValue
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

