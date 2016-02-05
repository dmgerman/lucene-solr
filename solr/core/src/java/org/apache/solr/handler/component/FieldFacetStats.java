begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|List
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
name|schema
operator|.
name|SchemaField
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
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  * FieldFacetStats is a utility to accumulate statistics on a set of values in one field,  * for facet values present in another field.  *<p>  * 9/10/2009 - Moved out of StatsComponent to allow open access to UnInvertedField  * @see org.apache.solr.handler.component.StatsComponent  *  */
end_comment

begin_class
DECL|class|FieldFacetStats
specifier|public
class|class
name|FieldFacetStats
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|statsField
specifier|final
name|StatsField
name|statsField
decl_stmt|;
DECL|field|facet_sf
specifier|final
name|SchemaField
name|facet_sf
decl_stmt|;
DECL|field|facetStatsValues
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|facetStatsValues
decl_stmt|;
DECL|field|missingStats
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|missingStats
decl_stmt|;
DECL|field|facetStatsTerms
name|List
argument_list|<
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|facetStatsTerms
decl_stmt|;
DECL|field|topLevelReader
specifier|final
name|LeafReader
name|topLevelReader
decl_stmt|;
DECL|field|leave
name|LeafReaderContext
name|leave
decl_stmt|;
DECL|field|valueSource
specifier|final
name|ValueSource
name|valueSource
decl_stmt|;
DECL|field|context
name|LeafReaderContext
name|context
decl_stmt|;
DECL|field|values
name|FunctionValues
name|values
decl_stmt|;
DECL|field|topLevelSortedValues
name|SortedDocValues
name|topLevelSortedValues
init|=
literal|null
decl_stmt|;
DECL|method|FieldFacetStats
specifier|public
name|FieldFacetStats
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|SchemaField
name|facet_sf
parameter_list|,
name|StatsField
name|statsField
parameter_list|)
block|{
name|this
operator|.
name|statsField
operator|=
name|statsField
expr_stmt|;
name|this
operator|.
name|facet_sf
operator|=
name|facet_sf
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|facet_sf
operator|.
name|getName
argument_list|()
expr_stmt|;
name|topLevelReader
operator|=
name|searcher
operator|.
name|getLeafReader
argument_list|()
expr_stmt|;
name|valueSource
operator|=
name|facet_sf
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|facet_sf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|facetStatsValues
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|facetStatsTerms
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|missingStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|getStatsValues
specifier|private
name|StatsValues
name|getStatsValues
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|StatsValues
name|stats
init|=
name|facetStatsValues
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|stats
operator|=
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|statsField
argument_list|)
expr_stmt|;
name|facetStatsValues
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|stats
return|;
block|}
comment|// docID is relative to the context
DECL|method|facet
specifier|public
name|void
name|facet
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|key
init|=
name|values
operator|.
name|exists
argument_list|(
name|docID
argument_list|)
condition|?
name|values
operator|.
name|strVal
argument_list|(
name|docID
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|StatsValues
name|stats
init|=
name|getStatsValues
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|stats
operator|.
name|accumulate
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
comment|// Function to keep track of facet counts for term number.
comment|// Currently only used by UnInvertedField stats
DECL|method|facetTermNum
specifier|public
name|boolean
name|facetTermNum
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|statsTermNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topLevelSortedValues
operator|==
literal|null
condition|)
block|{
name|topLevelSortedValues
operator|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|topLevelReader
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|int
name|term
init|=
name|topLevelSortedValues
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|int
name|arrIdx
init|=
name|term
decl_stmt|;
if|if
condition|(
name|arrIdx
operator|>=
literal|0
operator|&&
name|arrIdx
operator|<
name|topLevelSortedValues
operator|.
name|getValueCount
argument_list|()
condition|)
block|{
specifier|final
name|String
name|key
decl_stmt|;
if|if
condition|(
name|term
operator|==
operator|-
literal|1
condition|)
block|{
name|key
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|key
operator|=
name|topLevelSortedValues
operator|.
name|lookupOrd
argument_list|(
name|term
argument_list|)
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|facetStatsTerms
operator|.
name|size
argument_list|()
operator|<=
name|statsTermNum
condition|)
block|{
name|facetStatsTerms
operator|.
name|add
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|statsTermCounts
init|=
name|facetStatsTerms
operator|.
name|get
argument_list|(
name|statsTermNum
argument_list|)
decl_stmt|;
name|Integer
name|statsTermCount
init|=
name|statsTermCounts
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|statsTermCount
operator|==
literal|null
condition|)
block|{
name|statsTermCounts
operator|.
name|put
argument_list|(
name|key
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statsTermCounts
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|statsTermCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//function to accumulate counts for statsTermNum to specified value
DECL|method|accumulateTermNum
specifier|public
name|boolean
name|accumulateTermNum
parameter_list|(
name|int
name|statsTermNum
parameter_list|,
name|BytesRef
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|false
return|;
while|while
condition|(
name|facetStatsTerms
operator|.
name|size
argument_list|()
operator|<=
name|statsTermNum
condition|)
block|{
name|facetStatsTerms
operator|.
name|add
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|pairs
range|:
name|facetStatsTerms
operator|.
name|get
argument_list|(
name|statsTermNum
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|pairs
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|StatsValues
name|facetStats
init|=
name|facetStatsValues
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetStats
operator|==
literal|null
condition|)
block|{
name|facetStats
operator|=
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|statsField
argument_list|)
expr_stmt|;
name|facetStatsValues
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|facetStats
argument_list|)
expr_stmt|;
block|}
name|Integer
name|count
init|=
operator|(
name|Integer
operator|)
name|pairs
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|facetStats
operator|.
name|accumulate
argument_list|(
name|value
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|ctx
expr_stmt|;
name|values
operator|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
for|for
control|(
name|StatsValues
name|stats
range|:
name|facetStatsValues
operator|.
name|values
argument_list|()
control|)
block|{
name|stats
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|facetMissingNum
specifier|public
name|void
name|facetMissingNum
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topLevelSortedValues
operator|==
literal|null
condition|)
block|{
name|topLevelSortedValues
operator|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|topLevelReader
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|int
name|ord
init|=
name|topLevelSortedValues
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|!=
operator|-
literal|1
condition|)
block|{
name|Integer
name|missingCount
init|=
name|missingStats
operator|.
name|get
argument_list|(
name|ord
argument_list|)
decl_stmt|;
if|if
condition|(
name|missingCount
operator|==
literal|null
condition|)
block|{
name|missingStats
operator|.
name|put
argument_list|(
name|ord
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|missingStats
operator|.
name|put
argument_list|(
name|ord
argument_list|,
name|missingCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|accumulateMissing
specifier|public
name|void
name|accumulateMissing
parameter_list|()
throws|throws
name|IOException
block|{
name|StatsValues
name|statsValue
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|missingStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|String
name|key
init|=
name|topLevelSortedValues
operator|.
name|lookupOrd
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|statsValue
operator|=
name|facetStatsValues
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|statsValue
operator|.
name|addMissing
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return;
block|}
block|}
end_class

end_unit

