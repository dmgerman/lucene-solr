begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|HashMap
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
name|CommonParams
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
name|TermsParams
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

begin_comment
comment|/**  *  Iterates over a gatherNodes() expression and scores the node Tuples based based on tf-idf.  *  *  Expression Syntax:  *  *  Default function call uses the "count(*)" value for node freq.  *  *  You can use a different value for node freq by providing the nodeFreq param  *  scoreNodes(gatherNodes(...), nodeFreq="min(weight)")  *  **/
end_comment

begin_class
DECL|class|ScoreNodesStream
specifier|public
class|class
name|ScoreNodesStream
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
DECL|field|zkHost
specifier|protected
name|String
name|zkHost
decl_stmt|;
DECL|field|stream
specifier|private
name|TupleStream
name|stream
decl_stmt|;
DECL|field|clientCache
specifier|private
specifier|transient
name|SolrClientCache
name|clientCache
decl_stmt|;
DECL|field|nodes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|>
name|nodes
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|tuples
specifier|private
name|Iterator
argument_list|<
name|Tuple
argument_list|>
name|tuples
decl_stmt|;
DECL|field|termFreq
specifier|private
name|String
name|termFreq
decl_stmt|;
DECL|method|ScoreNodesStream
specifier|public
name|ScoreNodesStream
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|,
name|String
name|nodeFreqField
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|tupleStream
argument_list|,
name|nodeFreqField
argument_list|)
expr_stmt|;
block|}
DECL|method|ScoreNodesStream
specifier|public
name|ScoreNodesStream
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
name|nodeFreqParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"termFreq"
argument_list|)
decl_stmt|;
name|String
name|docFreqField
init|=
literal|"count(*)"
decl_stmt|;
if|if
condition|(
name|nodeFreqParam
operator|!=
literal|null
condition|)
block|{
name|docFreqField
operator|=
name|nodeFreqParam
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
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
name|zkHost
operator|=
name|factory
operator|.
name|getDefaultZkHost
argument_list|()
expr_stmt|;
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
literal|"zkHost not found"
argument_list|)
throw|;
block|}
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
name|init
argument_list|(
name|stream
argument_list|,
name|docFreqField
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|,
name|String
name|termFreq
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stream
operator|=
name|tupleStream
expr_stmt|;
name|this
operator|.
name|termFreq
operator|=
name|termFreq
expr_stmt|;
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
comment|// nodeFreqField
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"termFreq"
argument_list|,
name|termFreq
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeStreams
condition|)
block|{
comment|// stream
if|if
condition|(
name|stream
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
name|stream
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
literal|"This ScoreNodesStream contains a non-expressible TupleStream - it cannot be converted to an expression"
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
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withChildren
argument_list|(
operator|new
name|Explanation
index|[]
block|{
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
block|}
argument_list|)
operator|.
name|withFunctionName
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
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
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
name|this
operator|.
name|clientCache
operator|=
name|context
operator|.
name|getSolrClientCache
argument_list|()
expr_stmt|;
name|this
operator|.
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
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
name|List
argument_list|<
name|TupleStream
argument_list|>
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
name|stream
argument_list|)
expr_stmt|;
return|return
name|l
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
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
name|Tuple
name|node
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|String
name|collection
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|node
operator|=
name|stream
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|EOF
condition|)
block|{
break|break;
block|}
name|String
name|nodeId
init|=
name|node
operator|.
name|getString
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|field
operator|=
name|node
operator|.
name|getString
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
name|collection
operator|=
name|node
operator|.
name|getString
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
name|CloudSolrClient
name|client
init|=
name|clientCache
operator|.
name|getCloudSolrClient
argument_list|(
name|zkHost
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/terms"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_STATS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_LIST
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
try|try
block|{
comment|//Get the response from the terms component
name|NamedList
name|response
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Number
argument_list|>
name|stats
init|=
operator|(
name|NamedList
argument_list|<
name|Number
argument_list|>
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"stats"
argument_list|)
decl_stmt|;
name|long
name|numDocs
init|=
name|stats
operator|.
name|get
argument_list|(
literal|"numDocs"
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
name|fields
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fieldName
init|=
name|fields
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Number
argument_list|>
name|terms
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|int
name|tsize
init|=
name|terms
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|tsize
condition|;
name|t
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
operator|.
name|getName
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|Number
name|docFreq
init|=
name|terms
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|Tuple
name|tuple
init|=
name|nodes
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|Number
name|termFreqValue
init|=
operator|(
name|Number
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|termFreq
argument_list|)
decl_stmt|;
name|float
name|score
init|=
name|termFreqValue
operator|.
name|floatValue
argument_list|()
operator|*
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
operator|(
name|numDocs
operator|+
literal|1
operator|)
operator|/
operator|(
name|docFreq
operator|.
name|doubleValue
argument_list|()
operator|+
literal|1
operator|)
argument_list|)
operator|+
literal|1.0
argument_list|)
decl_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"nodeScore"
argument_list|,
name|score
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"docFreq"
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"numDocs"
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
block|}
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
name|tuples
operator|=
name|nodes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getComparator
specifier|public
name|StreamComparator
name|getComparator
parameter_list|()
block|{
return|return
literal|null
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
if|if
condition|(
name|tuples
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|tuples
operator|.
name|next
argument_list|()
return|;
block|}
else|else
block|{
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tuple
argument_list|(
name|map
argument_list|)
return|;
block|}
block|}
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
block|}
end_class

end_unit

