begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
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
name|time
operator|.
name|LocalDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneOffset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
name|Locale
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
operator|.
name|Builder
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|SolrClientCache
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|StreamComparator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
operator|.
name|ExpressionType
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExplanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionNamedParameter
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|Metric
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|SolrParams
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

begin_class
DECL|class|TimeSeriesStream
specifier|public
class|class
name|TimeSeriesStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|start
specifier|private
name|String
name|start
decl_stmt|;
DECL|field|end
specifier|private
name|String
name|end
decl_stmt|;
DECL|field|gap
specifier|private
name|String
name|gap
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|format
specifier|private
name|String
name|format
decl_stmt|;
DECL|field|formatter
specifier|private
name|DateTimeFormatter
name|formatter
decl_stmt|;
DECL|field|metrics
specifier|private
name|Metric
index|[]
name|metrics
decl_stmt|;
DECL|field|tuples
specifier|private
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|field|zkHost
specifier|private
name|String
name|zkHost
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
decl_stmt|;
DECL|field|cache
specifier|protected
specifier|transient
name|SolrClientCache
name|cache
decl_stmt|;
DECL|field|cloudSolrClient
specifier|protected
specifier|transient
name|CloudSolrClient
name|cloudSolrClient
decl_stmt|;
DECL|method|TimeSeriesStream
specifier|public
name|TimeSeriesStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|Metric
index|[]
name|metrics
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|,
name|String
name|gap
parameter_list|,
name|String
name|format
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|collection
argument_list|,
name|params
argument_list|,
name|field
argument_list|,
name|metrics
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|gap
argument_list|,
name|format
argument_list|,
name|zkHost
argument_list|)
expr_stmt|;
block|}
DECL|method|TimeSeriesStream
specifier|public
name|TimeSeriesStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// grab all parameters out
name|String
name|collectionName
init|=
name|factory
operator|.
name|getValueOperand
argument_list|(
name|expression
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|namedParams
init|=
name|factory
operator|.
name|getNamedOperands
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|startExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"start"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|endExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"end"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|fieldExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|gapExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"gap"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|formatExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"format"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|qExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"q"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|zkHostExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"zkHost"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|metricExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|Expressible
operator|.
name|class
argument_list|,
name|Metric
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|qExpression
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The timeseries expression requires the q parameter"
argument_list|)
throw|;
block|}
name|String
name|start
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|startExpression
operator|!=
literal|null
condition|)
block|{
name|start
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|startExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|String
name|end
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|endExpression
operator|!=
literal|null
condition|)
block|{
name|end
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|endExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|String
name|gap
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|gapExpression
operator|!=
literal|null
condition|)
block|{
name|gap
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|gapExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|String
name|field
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fieldExpression
operator|!=
literal|null
condition|)
block|{
name|field
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|fieldExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|String
name|format
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|formatExpression
operator|!=
literal|null
condition|)
block|{
name|format
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|formatExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
comment|// Collection Name
if|if
condition|(
literal|null
operator|==
name|collectionName
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - collectionName expected as first operand"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Named parameters - passed directly to solr as solrparams
if|if
condition|(
literal|0
operator|==
name|namedParams
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - at least one named parameter expected. eg. 'q=*:*'"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Construct the metrics
name|Metric
index|[]
name|metrics
init|=
operator|new
name|Metric
index|[
name|metricExpressions
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|metricExpressions
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|metrics
index|[
name|idx
index|]
operator|=
name|factory
operator|.
name|constructMetric
argument_list|(
name|metricExpressions
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|==
name|metrics
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - at least one metric expected."
argument_list|,
name|expression
argument_list|,
name|collectionName
argument_list|)
argument_list|)
throw|;
block|}
comment|// pull out known named params
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionNamedParameter
name|namedParam
range|:
name|namedParams
control|)
block|{
if|if
condition|(
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"zkHost"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"start"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"end"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"gap"
argument_list|)
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
name|namedParam
operator|.
name|getName
argument_list|()
argument_list|,
name|namedParam
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// zkHost, optional - if not provided then will look into factory list to get
name|String
name|zkHost
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|zkHostExpression
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getCollectionZkHost
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkHost
operator|==
literal|null
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getDefaultZkHost
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|zkHost
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|zkHost
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - zkHost not found for collection '%s'"
argument_list|,
name|expression
argument_list|,
name|collectionName
argument_list|)
argument_list|)
throw|;
block|}
comment|// We've got all the required items
name|init
argument_list|(
name|collectionName
argument_list|,
name|params
argument_list|,
name|field
argument_list|,
name|metrics
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|gap
argument_list|,
name|format
argument_list|,
name|zkHost
argument_list|)
expr_stmt|;
block|}
DECL|method|getCollection
specifier|public
name|String
name|getCollection
parameter_list|()
block|{
return|return
name|this
operator|.
name|collection
return|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|collection
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|String
name|field
parameter_list|,
name|Metric
index|[]
name|metrics
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|,
name|String
name|gap
parameter_list|,
name|String
name|format
parameter_list|,
name|String
name|zkHost
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|gap
operator|=
name|gap
expr_stmt|;
if|if
condition|(
operator|!
name|gap
operator|.
name|startsWith
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
name|this
operator|.
name|gap
operator|=
literal|"+"
operator|+
name|gap
expr_stmt|;
block|}
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|formatter
operator|=
name|DateTimeFormatter
operator|.
name|ofPattern
argument_list|(
name|format
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// function name
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// collection
name|expression
operator|.
name|addParameter
argument_list|(
name|collection
argument_list|)
expr_stmt|;
comment|// parameters
name|ModifiableSolrParams
name|tmpParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|param
range|:
name|tmpParams
operator|.
name|getMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
name|param
operator|.
name|getKey
argument_list|()
argument_list|,
name|String
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|param
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// metrics
for|for
control|(
name|Metric
name|metric
range|:
name|metrics
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|metric
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"start"
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"end"
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"gap"
argument_list|,
name|gap
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"field"
argument_list|,
name|gap
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
argument_list|)
expr_stmt|;
comment|// zkHost
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"zkHost"
argument_list|,
name|zkHost
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|expression
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamExplanation
name|explanation
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|explanation
operator|.
name|setFunctionName
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_SOURCE
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// child is a datastore so add it at this point
name|StreamExplanation
name|child
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|+
literal|"-datastore"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setFunctionName
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"solr (%s)"
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: fix this so we know the # of workers - check with Joel about a Topic's ability to be in a
comment|// parallel stream.
name|child
operator|.
name|setImplementingClass
argument_list|(
literal|"Solr/Lucene"
argument_list|)
expr_stmt|;
name|child
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|DATASTORE
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|tmpParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|SolrParams
operator|.
name|toMultiMap
argument_list|(
name|params
operator|.
name|toNamedList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|child
operator|.
name|setExpression
argument_list|(
name|tmpParams
operator|.
name|getMap
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|e
lambda|->
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s=%s"
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|addChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
return|return
name|explanation
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|cache
operator|=
name|context
operator|.
name|getSolrClientCache
argument_list|()
expr_stmt|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|()
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cloudSolrClient
operator|=
name|cache
operator|.
name|getCloudSolrClient
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cloudSolrClient
operator|=
operator|new
name|Builder
argument_list|()
operator|.
name|withZkHost
argument_list|(
name|zkHost
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|String
name|json
init|=
name|getJsonFacetString
argument_list|(
name|field
argument_list|,
name|metrics
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|gap
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|paramsLoc
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"json.facet"
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|paramsLoc
argument_list|)
decl_stmt|;
try|try
block|{
name|NamedList
name|response
init|=
name|cloudSolrClient
operator|.
name|request
argument_list|(
name|request
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|getTuples
argument_list|(
name|response
argument_list|,
name|field
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cloudSolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|index
operator|<
name|tuples
operator|.
name|size
argument_list|()
condition|)
block|{
name|Tuple
name|tuple
init|=
name|tuples
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
operator|++
name|index
expr_stmt|;
return|return
name|tuple
return|;
block|}
else|else
block|{
name|Map
name|fields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
name|fields
argument_list|)
decl_stmt|;
return|return
name|tuple
return|;
block|}
block|}
DECL|method|getJsonFacetString
specifier|private
name|String
name|getJsonFacetString
parameter_list|(
name|String
name|field
parameter_list|,
name|Metric
index|[]
name|_metrics
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|appendJson
argument_list|(
name|buf
argument_list|,
name|_metrics
argument_list|,
name|field
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|gap
argument_list|)
expr_stmt|;
return|return
literal|"{"
operator|+
name|buf
operator|.
name|toString
argument_list|()
operator|+
literal|"}"
return|;
block|}
DECL|method|appendJson
specifier|private
name|void
name|appendJson
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|Metric
index|[]
name|_metrics
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"timeseries"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|":{"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\"type\":\"range\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|",\"field\":\""
operator|+
name|field
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|",\"start\":\""
operator|+
name|start
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|",\"end\":\""
operator|+
name|end
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|",\"gap\":\""
operator|+
name|gap
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|",\"facet\":{"
argument_list|)
expr_stmt|;
name|int
name|metricCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Metric
name|metric
range|:
name|_metrics
control|)
block|{
name|String
name|identifier
init|=
name|metric
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|identifier
operator|.
name|startsWith
argument_list|(
literal|"count("
argument_list|)
condition|)
block|{
if|if
condition|(
name|metricCount
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"\"facet_"
operator|+
name|metricCount
operator|+
literal|"\":\""
operator|+
name|identifier
operator|+
literal|"\""
argument_list|)
expr_stmt|;
operator|++
name|metricCount
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"}}"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTuples
specifier|private
name|void
name|getTuples
parameter_list|(
name|NamedList
name|response
parameter_list|,
name|String
name|field
parameter_list|,
name|Metric
index|[]
name|metrics
parameter_list|)
block|{
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
decl_stmt|;
name|NamedList
name|facets
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"facets"
argument_list|)
decl_stmt|;
name|fillTuples
argument_list|(
name|tuples
argument_list|,
name|tuple
argument_list|,
name|facets
argument_list|,
name|field
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
DECL|method|fillTuples
specifier|private
name|void
name|fillTuples
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|Tuple
name|currentTuple
parameter_list|,
name|NamedList
name|facets
parameter_list|,
name|String
name|field
parameter_list|,
name|Metric
index|[]
name|_metrics
parameter_list|)
block|{
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|facets
operator|.
name|get
argument_list|(
literal|"timeseries"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|List
name|allBuckets
init|=
operator|(
name|List
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"buckets"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|b
init|=
literal|0
init|;
name|b
operator|<
name|allBuckets
operator|.
name|size
argument_list|()
condition|;
name|b
operator|++
control|)
block|{
name|NamedList
name|bucket
init|=
operator|(
name|NamedList
operator|)
name|allBuckets
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|Object
name|val
init|=
name|bucket
operator|.
name|get
argument_list|(
literal|"val"
argument_list|)
decl_stmt|;
if|if
condition|(
name|formatter
operator|!=
literal|null
condition|)
block|{
name|LocalDateTime
name|localDateTime
init|=
name|LocalDateTime
operator|.
name|ofInstant
argument_list|(
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|Date
operator|)
name|val
operator|)
operator|.
name|toInstant
argument_list|()
argument_list|,
name|ZoneOffset
operator|.
name|UTC
argument_list|)
decl_stmt|;
name|val
operator|=
name|localDateTime
operator|.
name|format
argument_list|(
name|formatter
argument_list|)
expr_stmt|;
block|}
name|Tuple
name|t
init|=
name|currentTuple
operator|.
name|clone
argument_list|()
decl_stmt|;
name|t
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|int
name|m
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Metric
name|metric
range|:
name|_metrics
control|)
block|{
name|String
name|identifier
init|=
name|metric
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|identifier
operator|.
name|startsWith
argument_list|(
literal|"count("
argument_list|)
condition|)
block|{
name|double
name|d
init|=
operator|(
name|double
operator|)
name|bucket
operator|.
name|get
argument_list|(
literal|"facet_"
operator|+
name|m
argument_list|)
decl_stmt|;
if|if
condition|(
name|metric
operator|.
name|outputLong
condition|)
block|{
name|t
operator|.
name|put
argument_list|(
name|identifier
argument_list|,
name|Math
operator|.
name|round
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|t
operator|.
name|put
argument_list|(
name|identifier
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
operator|++
name|m
expr_stmt|;
block|}
else|else
block|{
name|long
name|l
init|=
operator|(
operator|(
name|Number
operator|)
name|bucket
operator|.
name|get
argument_list|(
literal|"count"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|t
operator|.
name|put
argument_list|(
literal|"count(*)"
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
block|}
name|tuples
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

