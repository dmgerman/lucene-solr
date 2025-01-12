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
name|StreamFactory
import|;
end_import

begin_class
DECL|class|ListStream
specifier|public
class|class
name|ListStream
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
DECL|field|streams
specifier|private
name|TupleStream
index|[]
name|streams
decl_stmt|;
DECL|field|currentStream
specifier|private
name|TupleStream
name|currentStream
decl_stmt|;
DECL|field|streamIndex
specifier|private
name|int
name|streamIndex
decl_stmt|;
DECL|method|ListStream
specifier|public
name|ListStream
parameter_list|(
name|TupleStream
modifier|...
name|streams
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
DECL|method|ListStream
specifier|public
name|ListStream
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
name|TupleStream
index|[]
name|streams
init|=
operator|new
name|TupleStream
index|[
name|streamExpressions
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
name|streamExpressions
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|streams
index|[
name|idx
index|]
operator|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|init
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|TupleStream
modifier|...
name|tupleStreams
parameter_list|)
block|{
name|this
operator|.
name|streams
operator|=
name|tupleStreams
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
if|if
condition|(
name|includeStreams
condition|)
block|{
for|for
control|(
name|TupleStream
name|stream
range|:
name|streams
control|)
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
for|for
control|(
name|TupleStream
name|stream
range|:
name|streams
control|)
block|{
name|explanation
operator|.
name|addChild
argument_list|(
name|stream
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
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
for|for
control|(
name|TupleStream
name|stream
range|:
name|streams
control|)
block|{
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
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
argument_list|<
name|TupleStream
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TupleStream
name|stream
range|:
name|streams
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
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
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|currentStream
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|streamIndex
operator|<
name|streams
operator|.
name|length
condition|)
block|{
name|currentStream
operator|=
name|streams
index|[
name|streamIndex
index|]
expr_stmt|;
name|currentStream
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|HashMap
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
name|Tuple
name|tuple
init|=
name|currentStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|currentStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|currentStream
operator|=
literal|null
expr_stmt|;
operator|++
name|streamIndex
expr_stmt|;
block|}
else|else
block|{
return|return
name|tuple
return|;
block|}
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{     }
comment|/** Return the stream sort - ie, the order in which records are returned */
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

