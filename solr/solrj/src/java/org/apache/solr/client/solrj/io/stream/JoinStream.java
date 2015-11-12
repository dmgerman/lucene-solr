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
name|LinkedList
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
name|eq
operator|.
name|FieldEqualitor
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
name|eq
operator|.
name|StreamEqualitor
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Defines a JoinStream which can hold N streams, all joined with the same equalitor */
end_comment

begin_class
DECL|class|JoinStream
specifier|public
specifier|abstract
class|class
name|JoinStream
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
name|List
argument_list|<
name|PushBackStream
argument_list|>
name|streams
decl_stmt|;
DECL|field|eq
specifier|protected
name|StreamEqualitor
name|eq
decl_stmt|;
DECL|method|JoinStream
specifier|public
name|JoinStream
parameter_list|(
name|StreamEqualitor
name|eq
parameter_list|,
name|TupleStream
name|first
parameter_list|,
name|TupleStream
name|second
parameter_list|,
name|TupleStream
modifier|...
name|others
parameter_list|)
block|{
name|this
operator|.
name|streams
operator|=
operator|new
name|ArrayList
argument_list|<
name|PushBackStream
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|eq
operator|=
name|eq
expr_stmt|;
name|this
operator|.
name|streams
operator|.
name|add
argument_list|(
operator|new
name|PushBackStream
argument_list|(
name|first
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|streams
operator|.
name|add
argument_list|(
operator|new
name|PushBackStream
argument_list|(
name|second
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|TupleStream
name|other
range|:
name|others
control|)
block|{
name|this
operator|.
name|streams
operator|.
name|add
argument_list|(
operator|new
name|PushBackStream
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateTupleOrder
specifier|protected
specifier|abstract
name|void
name|validateTupleOrder
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|JoinStream
specifier|public
name|JoinStream
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
name|onExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"on"
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
literal|1
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
if|if
condition|(
name|streamExpressions
operator|.
name|size
argument_list|()
operator|<
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
literal|"Invalid expression %s - expecting at least two streams but found %d (must be PushBackStream types)"
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
name|this
operator|.
name|streams
operator|=
operator|new
name|ArrayList
argument_list|<
name|PushBackStream
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|StreamExpression
name|streamExpression
range|:
name|streamExpressions
control|)
block|{
name|this
operator|.
name|streams
operator|.
name|add
argument_list|(
operator|new
name|PushBackStream
argument_list|(
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpression
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|onExpression
operator|||
operator|!
operator|(
name|onExpression
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
literal|"Invalid expression %s - expecting single 'on' parameter listing fields to join on but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|eq
operator|=
name|factory
operator|.
name|constructEqualitor
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|onExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|FieldEqualitor
operator|.
name|class
argument_list|)
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
comment|// streams
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// on
if|if
condition|(
name|eq
operator|instanceof
name|Expressible
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"on"
argument_list|,
operator|(
operator|(
name|Expressible
operator|)
name|eq
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
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
literal|"This JoinStream contains a non-expressible equalitor - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
return|return
name|expression
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
name|PushBackStream
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
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
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
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|stream
operator|.
name|close
argument_list|()
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
name|list
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
name|list
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|getStream
specifier|public
name|PushBackStream
name|getStream
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
if|if
condition|(
name|streams
operator|.
name|size
argument_list|()
operator|>
name|idx
condition|)
block|{
return|return
name|streams
operator|.
name|get
argument_list|(
name|idx
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Stream idx=%d doesn't exist. Number of streams is %d"
argument_list|,
name|idx
argument_list|,
name|streams
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|isValidTupleOrder
specifier|protected
name|boolean
name|isValidTupleOrder
parameter_list|()
block|{
comment|// Validate that the equalitor is derivable from the comparator in each stream. If it is, then we know all stream
comment|// comparators are
comment|// derivable with each other stream
for|for
control|(
name|TupleStream
name|stream
range|:
name|streams
control|)
block|{
if|if
condition|(
operator|!
name|eq
operator|.
name|isDerivedFrom
argument_list|(
name|stream
operator|.
name|getStreamSort
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Given the stream, start from beginning and load group with all tuples that are equal to the first in stream    * (including the first one in the stream). All matched tuples are removed from the stream. Result is at least one    * tuple will be read from the stream and 0 or more tuples will exist in the group. If the first tuple is EOF then the    * group will have 0 items. Else it will have at least one item. The first group member is returned.    *     * @param group    *          - should be empty    */
DECL|method|loadEqualTupleGroup
specifier|protected
name|Tuple
name|loadEqualTupleGroup
parameter_list|(
name|PushBackStream
name|stream
parameter_list|,
name|LinkedList
argument_list|<
name|Tuple
argument_list|>
name|group
parameter_list|,
name|StreamComparator
name|groupComparator
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Find next set of same tuples from the stream
name|Tuple
name|firstMember
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|firstMember
operator|.
name|EOF
condition|)
block|{
comment|// first in group, implicitly a member
name|group
operator|.
name|add
argument_list|(
name|firstMember
argument_list|)
expr_stmt|;
name|BREAKPOINT
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|Tuple
name|nMember
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|nMember
operator|.
name|EOF
operator|&&
literal|0
operator|==
name|groupComparator
operator|.
name|compare
argument_list|(
name|firstMember
argument_list|,
name|nMember
argument_list|)
condition|)
block|{
comment|// they are in same group
name|group
operator|.
name|add
argument_list|(
name|nMember
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stream
operator|.
name|pushBack
argument_list|(
name|nMember
argument_list|)
expr_stmt|;
break|break
name|BREAKPOINT
break|;
block|}
block|}
block|}
return|return
name|firstMember
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
