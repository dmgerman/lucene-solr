begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|ArrayList
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
name|Locale
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|StreamContext
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
name|TupleStream
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
name|core
operator|.
name|SolrCore
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
name|analysis
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  The classify expression retrieves a model trained by the train expression and uses it to classify documents from a stream  *  Syntax:  *  classif(model(...), anyStream(...), field="body")  **/
end_comment

begin_class
DECL|class|ClassifyStream
specifier|public
class|class
name|ClassifyStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|docStream
specifier|private
name|TupleStream
name|docStream
decl_stmt|;
DECL|field|modelStream
specifier|private
name|TupleStream
name|modelStream
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|analyzerField
specifier|private
name|String
name|analyzerField
decl_stmt|;
DECL|field|modelTuple
specifier|private
name|Tuple
name|modelTuple
decl_stmt|;
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|termToIndex
specifier|private
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|Integer
argument_list|>
name|termToIndex
decl_stmt|;
DECL|field|idfs
specifier|private
name|List
argument_list|<
name|Double
argument_list|>
name|idfs
decl_stmt|;
DECL|field|modelWeights
specifier|private
name|List
argument_list|<
name|Double
argument_list|>
name|modelWeights
decl_stmt|;
DECL|method|ClassifyStream
specifier|public
name|ClassifyStream
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
if|if
condition|(
name|streamExpressions
operator|.
name|size
argument_list|()
operator|!=
literal|2
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
literal|"Invalid expression %s - expecting two stream but found %d"
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
name|modelStream
operator|=
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
expr_stmt|;
name|docStream
operator|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|StreamExpressionNamedParameter
name|fieldParameter
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
if|if
condition|(
name|fieldParameter
operator|==
literal|null
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
literal|"Invalid expression %s - field parameter must be specified"
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
name|analyzerField
operator|=
name|field
operator|=
name|fieldParameter
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|StreamExpressionNamedParameter
name|analyzerFieldParameter
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"analyzerField"
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzerFieldParameter
operator|!=
literal|null
condition|)
block|{
name|analyzerField
operator|=
name|analyzerFieldParameter
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|Object
name|solrCoreObj
init|=
name|context
operator|.
name|get
argument_list|(
literal|"solr-core"
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrCoreObj
operator|==
literal|null
operator|||
operator|!
operator|(
name|solrCoreObj
operator|instanceof
name|SolrCore
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|INVALID_STATE
argument_list|,
literal|"StreamContext must have SolrCore in solr-core key"
argument_list|)
throw|;
block|}
name|SolrCore
name|solrCore
init|=
operator|(
name|SolrCore
operator|)
name|solrCoreObj
decl_stmt|;
name|analyzer
operator|=
name|solrCore
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldType
argument_list|(
name|analyzerField
argument_list|)
operator|.
name|getIndexAnalyzer
argument_list|()
expr_stmt|;
name|this
operator|.
name|docStream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|modelStream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|docStream
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|modelStream
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
annotation|@
name|Override
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|docStream
operator|.
name|open
argument_list|()
expr_stmt|;
name|this
operator|.
name|modelStream
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|docStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|modelStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|modelTuple
operator|==
literal|null
condition|)
block|{
name|modelTuple
operator|=
name|modelStream
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|modelTuple
operator|==
literal|null
operator|||
name|modelTuple
operator|.
name|EOF
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Model tuple not found for classify stream!"
argument_list|)
throw|;
block|}
name|termToIndex
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
name|modelTuple
operator|.
name|getStrings
argument_list|(
literal|"terms_ss"
argument_list|)
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|termToIndex
operator|.
name|put
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|idfs
operator|=
name|modelTuple
operator|.
name|getDoubles
argument_list|(
literal|"idfs_ds"
argument_list|)
expr_stmt|;
name|modelWeights
operator|=
name|modelTuple
operator|.
name|getDoubles
argument_list|(
literal|"weights_ds"
argument_list|)
expr_stmt|;
block|}
name|Tuple
name|docTuple
init|=
name|docStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|docTuple
operator|.
name|EOF
condition|)
return|return
name|docTuple
return|;
name|String
name|text
init|=
name|docTuple
operator|.
name|getString
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|double
name|tfs
index|[]
init|=
operator|new
name|double
index|[
name|termToIndex
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|analyzerField
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|termCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|termCount
operator|++
expr_stmt|;
if|if
condition|(
name|termToIndex
operator|.
name|containsKey
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|tfs
index|[
name|termToIndex
operator|.
name|get
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
index|]
operator|++
expr_stmt|;
block|}
block|}
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
name|tokenStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|tfidfs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|termToIndex
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|tfidfs
operator|.
name|add
argument_list|(
literal|1.0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tfs
index|[
name|i
index|]
operator|!=
literal|0
condition|)
block|{
name|tfs
index|[
name|i
index|]
operator|=
literal|1
operator|+
name|Math
operator|.
name|log
argument_list|(
name|tfs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|tfidfs
operator|.
name|add
argument_list|(
name|this
operator|.
name|idfs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|*
name|tfs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|double
name|total
init|=
literal|0.0
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
name|tfidfs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|total
operator|+=
name|tfidfs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|*
name|modelWeights
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|double
name|score
init|=
name|total
operator|*
operator|(
call|(
name|float
call|)
argument_list|(
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|termCount
argument_list|)
argument_list|)
operator|)
decl_stmt|;
name|double
name|positiveProb
init|=
name|sigmoid
argument_list|(
name|total
argument_list|)
decl_stmt|;
name|docTuple
operator|.
name|put
argument_list|(
literal|"probability_d"
argument_list|,
name|positiveProb
argument_list|)
expr_stmt|;
name|docTuple
operator|.
name|put
argument_list|(
literal|"score_d"
argument_list|,
name|score
argument_list|)
expr_stmt|;
return|return
name|docTuple
return|;
block|}
DECL|method|sigmoid
specifier|private
name|double
name|sigmoid
parameter_list|(
name|double
name|in
parameter_list|)
block|{
name|double
name|d
init|=
literal|1.0
operator|/
operator|(
literal|1
operator|+
name|Math
operator|.
name|exp
argument_list|(
operator|-
name|in
argument_list|)
operator|)
decl_stmt|;
return|return
name|d
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
if|if
condition|(
name|includeStreams
condition|)
block|{
if|if
condition|(
name|docStream
operator|instanceof
name|Expressible
operator|&&
name|modelStream
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
name|modelStream
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|(
operator|(
name|Expressible
operator|)
name|docStream
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
literal|"This ClassifyStream contains a non-expressible TupleStream - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
block|}
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"field"
argument_list|,
name|field
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
literal|"analyzerField"
argument_list|,
name|analyzerField
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
name|Explanation
operator|.
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
name|explanation
operator|.
name|addChild
argument_list|(
name|docStream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|addChild
argument_list|(
name|modelStream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|explanation
return|;
block|}
block|}
end_class

end_unit

