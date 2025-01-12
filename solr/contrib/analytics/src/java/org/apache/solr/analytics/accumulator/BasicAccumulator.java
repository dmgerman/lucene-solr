begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.accumulator
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|accumulator
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Date
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|solr
operator|.
name|analytics
operator|.
name|expression
operator|.
name|Expression
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
name|analytics
operator|.
name|expression
operator|.
name|ExpressionFactory
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
name|analytics
operator|.
name|request
operator|.
name|AnalyticsRequest
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
name|analytics
operator|.
name|request
operator|.
name|ExpressionRequest
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
name|analytics
operator|.
name|statistics
operator|.
name|StatsCollector
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
name|analytics
operator|.
name|statistics
operator|.
name|StatsCollectorSupplierFactory
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
name|SolrException
operator|.
name|ErrorCode
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
name|search
operator|.
name|DocSet
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A<code>BasicAccumulator</code> manages the ValueCounters and Expressions without regard to Facets.  */
end_comment

begin_class
DECL|class|BasicAccumulator
specifier|public
class|class
name|BasicAccumulator
extends|extends
name|ValueAccumulator
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|searcher
specifier|protected
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|request
specifier|protected
specifier|final
name|AnalyticsRequest
name|request
decl_stmt|;
DECL|field|docs
specifier|protected
specifier|final
name|DocSet
name|docs
decl_stmt|;
DECL|field|statsCollectorArraySupplier
specifier|protected
specifier|final
name|Supplier
argument_list|<
name|StatsCollector
index|[]
argument_list|>
name|statsCollectorArraySupplier
decl_stmt|;
DECL|field|statsCollectors
specifier|protected
specifier|final
name|StatsCollector
index|[]
name|statsCollectors
decl_stmt|;
DECL|field|expressions
specifier|protected
specifier|final
name|Expression
index|[]
name|expressions
decl_stmt|;
DECL|field|expressionNames
specifier|protected
specifier|final
name|String
index|[]
name|expressionNames
decl_stmt|;
DECL|field|expressionStrings
specifier|protected
specifier|final
name|String
index|[]
name|expressionStrings
decl_stmt|;
DECL|field|hiddenExpressions
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenExpressions
decl_stmt|;
DECL|field|context
specifier|protected
name|LeafReaderContext
name|context
init|=
literal|null
decl_stmt|;
DECL|method|BasicAccumulator
specifier|public
name|BasicAccumulator
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|AnalyticsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ExpressionRequest
argument_list|>
name|exRequests
init|=
operator|new
name|ArrayList
argument_list|<
name|ExpressionRequest
argument_list|>
argument_list|(
name|request
operator|.
name|getExpressions
argument_list|()
argument_list|)
decl_stmt|;
comment|// make a copy here
name|Collections
operator|.
name|sort
argument_list|(
name|exRequests
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Processing request '"
operator|+
name|request
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|statsCollectorArraySupplier
operator|=
name|StatsCollectorSupplierFactory
operator|.
name|create
argument_list|(
name|searcher
operator|.
name|getSchema
argument_list|()
argument_list|,
name|exRequests
argument_list|)
expr_stmt|;
name|statsCollectors
operator|=
name|statsCollectorArraySupplier
operator|.
name|get
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|exRequests
operator|.
name|size
argument_list|()
decl_stmt|;
name|expressionNames
operator|=
operator|new
name|String
index|[
name|size
index|]
expr_stmt|;
name|expressionStrings
operator|=
operator|new
name|String
index|[
name|size
index|]
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ExpressionRequest
name|expRequest
range|:
name|exRequests
control|)
block|{
name|expressionNames
index|[
name|count
index|]
operator|=
name|expRequest
operator|.
name|getName
argument_list|()
expr_stmt|;
name|expressionStrings
index|[
name|count
operator|++
index|]
operator|=
name|expRequest
operator|.
name|getExpressionString
argument_list|()
expr_stmt|;
block|}
name|expressions
operator|=
name|makeExpressions
argument_list|(
name|statsCollectors
argument_list|)
expr_stmt|;
name|hiddenExpressions
operator|=
name|request
operator|.
name|getHiddenExpressions
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
for|for
control|(
name|StatsCollector
name|counter
range|:
name|statsCollectors
control|)
block|{
name|counter
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|create
specifier|public
specifier|static
name|BasicAccumulator
name|create
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|AnalyticsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BasicAccumulator
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|request
argument_list|)
return|;
block|}
comment|/**    * Passes the documents on to the {@link StatsCollector}s to be collected.    * @param doc Document to collect from    */
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|StatsCollector
name|statsCollector
range|:
name|statsCollectors
control|)
block|{
name|statsCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compute
specifier|public
name|void
name|compute
parameter_list|()
block|{
for|for
control|(
name|StatsCollector
name|statsCollector
range|:
name|statsCollectors
control|)
block|{
name|statsCollector
operator|.
name|compute
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|export
specifier|public
name|NamedList
argument_list|<
name|?
argument_list|>
name|export
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|base
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|expressions
operator|.
name|length
condition|;
name|count
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|hiddenExpressions
operator|.
name|contains
argument_list|(
name|expressionNames
index|[
name|count
index|]
argument_list|)
condition|)
block|{
name|base
operator|.
name|add
argument_list|(
name|expressionNames
index|[
name|count
index|]
argument_list|,
name|expressions
index|[
name|count
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|base
return|;
block|}
comment|/**    * Builds an array of Expressions with the given list of counters    * @param statsCollectors the stats collectors    * @return The array of Expressions    */
DECL|method|makeExpressions
specifier|public
name|Expression
index|[]
name|makeExpressions
parameter_list|(
name|StatsCollector
index|[]
name|statsCollectors
parameter_list|)
block|{
name|Expression
index|[]
name|expressions
init|=
operator|new
name|Expression
index|[
name|expressionStrings
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|expressionStrings
operator|.
name|length
condition|;
name|count
operator|++
control|)
block|{
name|expressions
index|[
name|count
index|]
operator|=
name|ExpressionFactory
operator|.
name|create
argument_list|(
name|expressionStrings
index|[
name|count
index|]
argument_list|,
name|statsCollectors
argument_list|)
expr_stmt|;
block|}
return|return
name|expressions
return|;
block|}
comment|/**    * Returns the value of an expression to use in a field or query facet.    * @param expressionName the name of the expression    * @return String String representation of pivot value    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"deprecation"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|getResult
specifier|public
name|String
name|getResult
parameter_list|(
name|String
name|expressionName
parameter_list|)
block|{
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|expressionNames
operator|.
name|length
condition|;
name|count
operator|++
control|)
block|{
if|if
condition|(
name|expressionName
operator|.
name|equals
argument_list|(
name|expressionNames
index|[
name|count
index|]
argument_list|)
condition|)
block|{
name|Comparable
name|value
init|=
name|expressions
index|[
name|count
index|]
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|Date
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
operator|(
operator|(
name|Date
operator|)
name|value
operator|)
operator|.
name|toInstant
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Pivot expression "
operator|+
name|expressionName
operator|+
literal|" not found."
argument_list|)
throw|;
block|}
comment|/**    * Used for JMX stats collecting. Counts the number of stats requests    * @return number of unique stats collectors    */
DECL|method|getNumStatsCollectors
specifier|public
name|long
name|getNumStatsCollectors
parameter_list|()
block|{
return|return
name|statsCollectors
operator|.
name|length
return|;
block|}
comment|/**    * Used for JMX stats collecting. Counts the number of queries in all query facets    * @return number of queries requested in all query facets.    */
DECL|method|getNumQueries
specifier|public
name|long
name|getNumQueries
parameter_list|()
block|{
return|return
literal|0l
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
literal|true
return|;
comment|// TODO: is this true?
block|}
block|}
end_class

end_unit

