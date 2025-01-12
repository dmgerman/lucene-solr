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
name|FieldComparator
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import static
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
name|CommonParams
operator|.
name|DISTRIB
import|;
end_import

begin_import
import|import static
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
name|CommonParams
operator|.
name|SORT
import|;
end_import

begin_comment
comment|/**  * The ParallelStream decorates a TupleStream implementation and pushes it to N workers for parallel execution.  * Workers are chosen from a SolrCloud collection.  * Tuples that are streamed back from the workers are ordered by a Comparator.  **/
end_comment

begin_class
DECL|class|ParallelStream
specifier|public
class|class
name|ParallelStream
extends|extends
name|CloudSolrStream
implements|implements
name|Expressible
block|{
DECL|field|tupleStream
specifier|private
name|TupleStream
name|tupleStream
decl_stmt|;
DECL|field|workers
specifier|private
name|int
name|workers
decl_stmt|;
DECL|field|streamFactory
specifier|private
specifier|transient
name|StreamFactory
name|streamFactory
decl_stmt|;
DECL|method|ParallelStream
specifier|public
name|ParallelStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|workers
parameter_list|,
name|StreamComparator
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
name|tupleStream
argument_list|,
name|workers
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
DECL|method|ParallelStream
specifier|public
name|ParallelStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|expressionString
parameter_list|,
name|int
name|workers
parameter_list|,
name|StreamComparator
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|TupleStream
name|tStream
init|=
name|this
operator|.
name|streamFactory
operator|.
name|constructStream
argument_list|(
name|expressionString
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
name|tStream
argument_list|,
name|workers
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
DECL|method|setStreamFactory
specifier|public
name|void
name|setStreamFactory
parameter_list|(
name|StreamFactory
name|streamFactory
parameter_list|)
block|{
name|this
operator|.
name|streamFactory
operator|=
name|streamFactory
expr_stmt|;
block|}
DECL|method|ParallelStream
specifier|public
name|ParallelStream
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
name|StreamExpressionNamedParameter
name|workersParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"workers"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|streamExpressions
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
name|TupleStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|sortExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
name|SORT
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
comment|// validate expression contains only what we want.
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|streamExpressions
operator|.
name|size
argument_list|()
operator|+
literal|3
operator|+
operator|(
literal|null
operator|!=
name|zkHostExpression
condition|?
literal|1
else|:
literal|0
operator|)
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
literal|"Invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
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
comment|// Workers
if|if
condition|(
literal|null
operator|==
name|workersParam
operator|||
literal|null
operator|==
name|workersParam
operator|.
name|getParameter
argument_list|()
operator|||
operator|!
operator|(
name|workersParam
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
operator|)
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
literal|"Invalid expression %s - expecting a single 'workers' parameter of type positive integer but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|workersStr
init|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|workersParam
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|workersInt
init|=
literal|0
decl_stmt|;
try|try
block|{
name|workersInt
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|workersStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|workersInt
operator|<=
literal|0
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
literal|"invalid expression %s - workers '%s' must be greater than 0."
argument_list|,
name|expression
argument_list|,
name|workersStr
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
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
literal|"invalid expression %s - workers '%s' is not a valid integer."
argument_list|,
name|expression
argument_list|,
name|workersStr
argument_list|)
argument_list|)
throw|;
block|}
comment|// Stream
if|if
condition|(
literal|1
operator|!=
name|streamExpressions
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
literal|"Invalid expression %s - expecting a single stream but found %d"
argument_list|,
name|expression
argument_list|,
name|streamExpressions
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|// Sort
if|if
condition|(
literal|null
operator|==
name|sortExpression
operator|||
operator|!
operator|(
name|sortExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
operator|)
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
literal|"Invalid expression %s - expecting single 'sort' parameter telling us how to join the parallel streams but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
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
name|TupleStream
name|stream
init|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|StreamComparator
name|comp
init|=
name|factory
operator|.
name|constructComparator
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|sortExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|FieldComparator
operator|.
name|class
argument_list|)
decl_stmt|;
name|streamFactory
operator|=
name|factory
expr_stmt|;
name|init
argument_list|(
name|zkHost
argument_list|,
name|collectionName
argument_list|,
name|stream
argument_list|,
name|workersInt
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|workers
parameter_list|,
name|StreamComparator
name|comp
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
name|workers
operator|=
name|workers
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|this
operator|.
name|tupleStream
operator|=
name|tupleStream
expr_stmt|;
comment|// requires Expressible stream and comparator
if|if
condition|(
operator|!
operator|(
name|tupleStream
operator|instanceof
name|Expressible
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create ParallelStream with a non-expressible TupleStream."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|toExpression
specifier|private
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|,
name|boolean
name|includeStreams
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
comment|// workers
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"workers"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|workers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeStreams
condition|)
block|{
if|if
condition|(
name|tupleStream
operator|instanceof
name|Expressible
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|(
operator|(
name|Expressible
operator|)
name|tupleStream
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This ParallelStream contains a non-expressible TupleStream - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|expression
operator|.
name|addParameter
argument_list|(
literal|"<stream>"
argument_list|)
expr_stmt|;
block|}
comment|// sort
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
name|SORT
argument_list|,
name|comp
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
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
name|STREAM_DECORATOR
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// add a child for each worker
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|workers
condition|;
operator|++
name|idx
control|)
block|{
name|explanation
operator|.
name|addChild
argument_list|(
name|tupleStream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|explanation
return|;
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
name|List
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|tupleStream
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Tuple
name|tuple
init|=
name|_read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tuple
name|t
init|=
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
decl_stmt|;
comment|/*       Map<String, Map> metrics = new HashMap();       Iterator<Entry<String,Tuple>> it = this.eofTuples.entrySet().iterator();       while(it.hasNext()) {         Map.Entry<String, Tuple> entry = it.next();         if(entry.getValue().fields.size()> 1) {           metrics.put(entry.getKey(), entry.getValue().fields);         }       }        if(metrics.size()> 0) {         t.setMetrics(metrics);       }       */
return|return
name|t
return|;
block|}
return|return
name|tuple
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|streamContext
parameter_list|)
block|{
name|this
operator|.
name|streamContext
operator|=
name|streamContext
expr_stmt|;
if|if
condition|(
name|streamFactory
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|streamFactory
operator|=
name|streamContext
operator|.
name|getStreamFactory
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|tupleStream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
block|}
DECL|method|constructStreams
specifier|protected
name|void
name|constructStreams
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Object
name|pushStream
init|=
operator|(
operator|(
name|Expressible
operator|)
name|tupleStream
operator|)
operator|.
name|toExpression
argument_list|(
name|streamFactory
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|shardUrls
init|=
name|getShards
argument_list|(
name|this
operator|.
name|zkHost
argument_list|,
name|this
operator|.
name|collection
argument_list|,
name|this
operator|.
name|streamContext
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|w
init|=
literal|0
init|;
name|w
operator|<
name|workers
condition|;
name|w
operator|++
control|)
block|{
name|ModifiableSolrParams
name|paramsLoc
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
name|DISTRIB
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// We are the aggregator.
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"numWorkers"
argument_list|,
name|workers
argument_list|)
expr_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"workerID"
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"expr"
argument_list|,
name|pushStream
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
literal|"/stream"
argument_list|)
expr_stmt|;
name|String
name|url
init|=
name|shardUrls
operator|.
name|get
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|SolrStream
name|solrStream
init|=
operator|new
name|SolrStream
argument_list|(
name|url
argument_list|,
name|paramsLoc
argument_list|)
decl_stmt|;
name|solrStreams
operator|.
name|add
argument_list|(
name|solrStream
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|(
name|solrStreams
operator|.
name|size
argument_list|()
operator|==
name|workers
operator|)
assert|;
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
block|}
end_class

end_unit

