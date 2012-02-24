begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|SimpleOrderedMap
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
name|schema
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Factory class for creating instance of {@link org.apache.solr.handler.component.StatsValues}  */
end_comment

begin_class
DECL|class|StatsValuesFactory
specifier|public
class|class
name|StatsValuesFactory
block|{
comment|/**    * Creates an instance of StatsValues which supports values from a field of the given FieldType    *    * @param sf SchemaField for the field whose statistics will be created by the resulting StatsValues    * @return Instance of StatsValues that will create statistics from values from a field of the given type    */
DECL|method|createStatsValues
specifier|public
specifier|static
name|StatsValues
name|createStatsValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
name|FieldType
name|fieldType
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|DoubleField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|IntField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|LongField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|ShortField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|FloatField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|ByteField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|TrieField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|SortableDoubleField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|SortableIntField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|SortableLongField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
operator|||
name|SortableFloatField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
condition|)
block|{
return|return
operator|new
name|NumericStatsValues
argument_list|(
name|sf
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|DateField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
condition|)
block|{
return|return
operator|new
name|DateStatsValues
argument_list|(
name|sf
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|StrField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|fieldType
argument_list|)
condition|)
block|{
return|return
operator|new
name|StringStatsValues
argument_list|(
name|sf
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Field type "
operator|+
name|fieldType
operator|+
literal|" is not currently supported"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|/**  * Abstract implementation of {@link org.apache.solr.handler.component.StatsValues} that provides the default behavior  * for most StatsValues implementations.  *  * There are very few requirements placed on what statistics concrete implementations should collect, with the only required  * statistics being the minimum and maximum values.  */
end_comment

begin_class
DECL|class|AbstractStatsValues
specifier|abstract
class|class
name|AbstractStatsValues
parameter_list|<
name|T
parameter_list|>
implements|implements
name|StatsValues
block|{
DECL|field|FACETS
specifier|private
specifier|static
specifier|final
name|String
name|FACETS
init|=
literal|"facets"
decl_stmt|;
DECL|field|sf
specifier|final
specifier|protected
name|SchemaField
name|sf
decl_stmt|;
DECL|field|ft
specifier|final
specifier|protected
name|FieldType
name|ft
decl_stmt|;
DECL|field|max
specifier|protected
name|T
name|max
decl_stmt|;
DECL|field|min
specifier|protected
name|T
name|min
decl_stmt|;
DECL|field|missing
specifier|protected
name|long
name|missing
decl_stmt|;
DECL|field|count
specifier|protected
name|long
name|count
decl_stmt|;
comment|// facetField   facetValue
DECL|field|facets
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|>
name|facets
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AbstractStatsValues
specifier|protected
name|AbstractStatsValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
name|this
operator|.
name|sf
operator|=
name|sf
expr_stmt|;
name|this
operator|.
name|ft
operator|=
name|sf
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|accumulate
specifier|public
name|void
name|accumulate
parameter_list|(
name|NamedList
name|stv
parameter_list|)
block|{
name|count
operator|+=
operator|(
name|Long
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"count"
argument_list|)
expr_stmt|;
name|missing
operator|+=
operator|(
name|Long
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"missing"
argument_list|)
expr_stmt|;
name|updateMinMax
argument_list|(
operator|(
name|T
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"min"
argument_list|)
argument_list|,
operator|(
name|T
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"max"
argument_list|)
argument_list|)
expr_stmt|;
name|updateTypeSpecificStats
argument_list|(
name|stv
argument_list|)
expr_stmt|;
name|NamedList
name|f
init|=
operator|(
name|NamedList
operator|)
name|stv
operator|.
name|get
argument_list|(
name|FACETS
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|f
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
name|f
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NamedList
name|vals
init|=
operator|(
name|NamedList
operator|)
name|f
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|addTo
init|=
name|facets
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|addTo
operator|==
literal|null
condition|)
block|{
name|addTo
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|()
expr_stmt|;
name|facets
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|addTo
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|vals
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|val
init|=
name|vals
operator|.
name|getName
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|StatsValues
name|vvals
init|=
name|addTo
operator|.
name|get
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|vvals
operator|==
literal|null
condition|)
block|{
name|vvals
operator|=
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|sf
argument_list|)
expr_stmt|;
name|addTo
operator|.
name|put
argument_list|(
name|val
argument_list|,
name|vvals
argument_list|)
expr_stmt|;
block|}
name|vvals
operator|.
name|accumulate
argument_list|(
operator|(
name|NamedList
operator|)
name|vals
operator|.
name|getVal
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|accumulate
specifier|public
name|void
name|accumulate
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|T
name|typedValue
init|=
operator|(
name|T
operator|)
name|ft
operator|.
name|toObject
argument_list|(
name|sf
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|updateMinMax
argument_list|(
name|typedValue
argument_list|,
name|typedValue
argument_list|)
expr_stmt|;
name|updateTypeSpecificStats
argument_list|(
name|typedValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|accumulate
specifier|public
name|void
name|accumulate
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|+=
name|count
expr_stmt|;
name|T
name|typedValue
init|=
operator|(
name|T
operator|)
name|ft
operator|.
name|toObject
argument_list|(
name|sf
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|updateMinMax
argument_list|(
name|typedValue
argument_list|,
name|typedValue
argument_list|)
expr_stmt|;
name|updateTypeSpecificStats
argument_list|(
name|typedValue
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|missing
specifier|public
name|void
name|missing
parameter_list|()
block|{
name|missing
operator|++
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|addMissing
specifier|public
name|void
name|addMissing
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|missing
operator|+=
name|count
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|addFacet
specifier|public
name|void
name|addFacet
parameter_list|(
name|String
name|facetName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|facetValues
parameter_list|)
block|{
name|facets
operator|.
name|put
argument_list|(
name|facetName
argument_list|,
name|facetValues
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|getStatsValues
specifier|public
name|NamedList
argument_list|<
name|?
argument_list|>
name|getStatsValues
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"min"
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"max"
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"missing"
argument_list|,
name|missing
argument_list|)
expr_stmt|;
name|addTypeSpecificStats
argument_list|(
name|res
argument_list|)
expr_stmt|;
comment|// add the facet stats
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
name|nl
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|>
name|entry
range|:
name|facets
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
name|nl2
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|nl2
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|e2
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|nl2
operator|.
name|add
argument_list|(
name|e2
operator|.
name|getKey
argument_list|()
argument_list|,
name|e2
operator|.
name|getValue
argument_list|()
operator|.
name|getStatsValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|res
operator|.
name|add
argument_list|(
name|FACETS
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Updates the minimum and maximum statistics based on the given values    *    * @param min Value that the current minimum should be updated against    * @param max Value that the current maximum should be updated against    */
DECL|method|updateMinMax
specifier|protected
specifier|abstract
name|void
name|updateMinMax
parameter_list|(
name|T
name|min
parameter_list|,
name|T
name|max
parameter_list|)
function_decl|;
comment|/**    * Updates the type specific statistics based on the given value    *    * @param value Value the statistics should be updated against    */
DECL|method|updateTypeSpecificStats
specifier|protected
specifier|abstract
name|void
name|updateTypeSpecificStats
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
comment|/**    * Updates the type specific statistics based on the given value    *    * @param value Value the statistics should be updated against    * @param count Number of times the value is being accumulated    */
DECL|method|updateTypeSpecificStats
specifier|protected
specifier|abstract
name|void
name|updateTypeSpecificStats
parameter_list|(
name|T
name|value
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
comment|/**    * Updates the type specific statistics based on the values in the given list    *    * @param stv List containing values the current statistics should be updated against    */
DECL|method|updateTypeSpecificStats
specifier|protected
specifier|abstract
name|void
name|updateTypeSpecificStats
parameter_list|(
name|NamedList
name|stv
parameter_list|)
function_decl|;
comment|/**    * Add any type specific statistics to the given NamedList    *    * @param res NamedList to add the type specific statistics too    */
DECL|method|addTypeSpecificStats
specifier|protected
specifier|abstract
name|void
name|addTypeSpecificStats
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|/**  * Implementation of StatsValues that supports Double values  */
end_comment

begin_class
DECL|class|NumericStatsValues
class|class
name|NumericStatsValues
extends|extends
name|AbstractStatsValues
argument_list|<
name|Number
argument_list|>
block|{
DECL|field|sum
name|double
name|sum
decl_stmt|;
DECL|field|sumOfSquares
name|double
name|sumOfSquares
decl_stmt|;
DECL|method|NumericStatsValues
specifier|public
name|NumericStatsValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
name|super
argument_list|(
name|sf
argument_list|)
expr_stmt|;
name|min
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
name|max
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateTypeSpecificStats
specifier|public
name|void
name|updateTypeSpecificStats
parameter_list|(
name|NamedList
name|stv
parameter_list|)
block|{
name|sum
operator|+=
operator|(
operator|(
name|Number
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"sum"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
name|sumOfSquares
operator|+=
operator|(
operator|(
name|Number
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"sumOfSquares"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateTypeSpecificStats
specifier|public
name|void
name|updateTypeSpecificStats
parameter_list|(
name|Number
name|v
parameter_list|)
block|{
name|double
name|value
init|=
name|v
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|sumOfSquares
operator|+=
operator|(
name|value
operator|*
name|value
operator|)
expr_stmt|;
comment|// for std deviation
name|sum
operator|+=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateTypeSpecificStats
specifier|public
name|void
name|updateTypeSpecificStats
parameter_list|(
name|Number
name|v
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|double
name|value
init|=
name|v
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|sumOfSquares
operator|+=
operator|(
name|value
operator|*
name|value
operator|*
name|count
operator|)
expr_stmt|;
comment|// for std deviation
name|sum
operator|+=
name|value
operator|*
name|count
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateMinMax
specifier|protected
name|void
name|updateMinMax
parameter_list|(
name|Number
name|min
parameter_list|,
name|Number
name|max
parameter_list|)
block|{
name|this
operator|.
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|min
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|min
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|max
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|max
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds sum, sumOfSquares, mean and standard deviation statistics to the given NamedList    *    * @param res NamedList to add the type specific statistics too    */
DECL|method|addTypeSpecificStats
specifier|protected
name|void
name|addTypeSpecificStats
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|)
block|{
name|res
operator|.
name|add
argument_list|(
literal|"sum"
argument_list|,
name|sum
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"sumOfSquares"
argument_list|,
name|sumOfSquares
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"mean"
argument_list|,
name|sum
operator|/
name|count
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"stddev"
argument_list|,
name|getStandardDeviation
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calculates the standard deviation statistic    *    * @return Standard deviation statistic    */
DECL|method|getStandardDeviation
specifier|private
name|double
name|getStandardDeviation
parameter_list|()
block|{
if|if
condition|(
name|count
operator|<=
literal|1.0D
condition|)
block|{
return|return
literal|0.0D
return|;
block|}
return|return
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
operator|(
name|count
operator|*
name|sumOfSquares
operator|)
operator|-
operator|(
name|sum
operator|*
name|sum
operator|)
operator|)
operator|/
operator|(
name|count
operator|*
operator|(
name|count
operator|-
literal|1.0D
operator|)
operator|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * Implementation of StatsValues that supports Date values  */
end_comment

begin_class
DECL|class|DateStatsValues
class|class
name|DateStatsValues
extends|extends
name|AbstractStatsValues
argument_list|<
name|Date
argument_list|>
block|{
DECL|field|DATE_FIELD
specifier|private
specifier|static
specifier|final
name|DateField
name|DATE_FIELD
init|=
operator|new
name|DateField
argument_list|()
decl_stmt|;
DECL|field|sum
specifier|private
name|long
name|sum
decl_stmt|;
DECL|method|DateStatsValues
specifier|public
name|DateStatsValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
name|super
argument_list|(
name|sf
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateTypeSpecificStats
specifier|protected
name|void
name|updateTypeSpecificStats
parameter_list|(
name|NamedList
name|stv
parameter_list|)
block|{
name|sum
operator|+=
operator|(
operator|(
name|Date
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"sum"
argument_list|)
operator|)
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|updateTypeSpecificStats
specifier|public
name|void
name|updateTypeSpecificStats
parameter_list|(
name|Date
name|value
parameter_list|)
block|{
name|sum
operator|+=
name|value
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|updateTypeSpecificStats
specifier|public
name|void
name|updateTypeSpecificStats
parameter_list|(
name|Date
name|value
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|sum
operator|+=
name|value
operator|.
name|getTime
argument_list|()
operator|*
name|count
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateMinMax
specifier|protected
name|void
name|updateMinMax
parameter_list|(
name|Date
name|min
parameter_list|,
name|Date
name|max
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|min
operator|==
literal|null
operator|||
name|this
operator|.
name|min
operator|.
name|after
argument_list|(
name|min
argument_list|)
condition|)
block|{
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|max
operator|==
literal|null
operator|||
name|this
operator|.
name|max
operator|.
name|before
argument_list|(
name|min
argument_list|)
condition|)
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
block|}
comment|/**    * Adds sum and mean statistics to the given NamedList    *    * @param res NamedList to add the type specific statistics too    */
DECL|method|addTypeSpecificStats
specifier|protected
name|void
name|addTypeSpecificStats
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|)
block|{
name|res
operator|.
name|add
argument_list|(
literal|"sum"
argument_list|,
operator|new
name|Date
argument_list|(
name|sum
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
literal|"mean"
argument_list|,
operator|new
name|Date
argument_list|(
name|sum
operator|/
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|/**  * Implementation of StatsValues that supports String values  */
end_comment

begin_class
DECL|class|StringStatsValues
class|class
name|StringStatsValues
extends|extends
name|AbstractStatsValues
argument_list|<
name|String
argument_list|>
block|{
DECL|method|StringStatsValues
specifier|public
name|StringStatsValues
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
name|super
argument_list|(
name|sf
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateTypeSpecificStats
specifier|protected
name|void
name|updateTypeSpecificStats
parameter_list|(
name|NamedList
name|stv
parameter_list|)
block|{
comment|// No type specific stats
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateTypeSpecificStats
specifier|protected
name|void
name|updateTypeSpecificStats
parameter_list|(
name|String
name|value
parameter_list|)
block|{
comment|// No type specific stats
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateTypeSpecificStats
specifier|protected
name|void
name|updateTypeSpecificStats
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|count
parameter_list|)
block|{
comment|// No type specific stats
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|updateMinMax
specifier|protected
name|void
name|updateMinMax
parameter_list|(
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|max
argument_list|(
name|this
operator|.
name|max
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
argument_list|(
name|this
operator|.
name|min
argument_list|,
name|min
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds no type specific statistics    */
DECL|method|addTypeSpecificStats
specifier|protected
name|void
name|addTypeSpecificStats
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|)
block|{
comment|// Add no statistics
block|}
comment|/**    * Determines which of the given Strings is the maximum, as computed by {@link String#compareTo(Object)}    *    * @param str1 String to compare against b    * @param str2 String compared against a    * @return str1 if it is considered greater by {@link String#compareTo(Object)}, str2 otherwise    */
DECL|method|max
specifier|private
specifier|static
name|String
name|max
parameter_list|(
name|String
name|str1
parameter_list|,
name|String
name|str2
parameter_list|)
block|{
if|if
condition|(
name|str1
operator|==
literal|null
condition|)
block|{
return|return
name|str2
return|;
block|}
elseif|else
if|if
condition|(
name|str2
operator|==
literal|null
condition|)
block|{
return|return
name|str1
return|;
block|}
return|return
operator|(
name|str1
operator|.
name|compareTo
argument_list|(
name|str2
argument_list|)
operator|>
literal|0
operator|)
condition|?
name|str1
else|:
name|str2
return|;
block|}
comment|/**    * Determines which of the given Strings is the minimum, as computed by {@link String#compareTo(Object)}    *    * @param str1 String to compare against b    * @param str2 String compared against a    * @return str1 if it is considered less by {@link String#compareTo(Object)}, str2 otherwise    */
DECL|method|min
specifier|private
specifier|static
name|String
name|min
parameter_list|(
name|String
name|str1
parameter_list|,
name|String
name|str2
parameter_list|)
block|{
if|if
condition|(
name|str1
operator|==
literal|null
condition|)
block|{
return|return
name|str2
return|;
block|}
elseif|else
if|if
condition|(
name|str2
operator|==
literal|null
condition|)
block|{
return|return
name|str1
return|;
block|}
return|return
operator|(
name|str1
operator|.
name|compareTo
argument_list|(
name|str2
argument_list|)
operator|<
literal|0
operator|)
condition|?
name|str1
else|:
name|str2
return|;
block|}
block|}
end_class

end_unit

