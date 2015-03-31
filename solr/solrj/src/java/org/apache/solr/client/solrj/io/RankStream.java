begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io
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
name|io
operator|.
name|Serializable
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/** *  Iterates over a TupleStream and Ranks the topN tuples based on a Comparator. **/
end_comment

begin_class
DECL|class|RankStream
specifier|public
class|class
name|RankStream
extends|extends
name|TupleStream
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
DECL|field|tupleStream
specifier|private
name|TupleStream
name|tupleStream
decl_stmt|;
DECL|field|top
specifier|private
name|PriorityQueue
argument_list|<
name|Tuple
argument_list|>
name|top
decl_stmt|;
DECL|field|comp
specifier|private
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
decl_stmt|;
DECL|field|finished
specifier|private
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
DECL|field|topList
specifier|private
name|LinkedList
argument_list|<
name|Tuple
argument_list|>
name|topList
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|method|RankStream
specifier|public
name|RankStream
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|size
parameter_list|,
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
block|{
name|this
operator|.
name|tupleStream
operator|=
name|tupleStream
expr_stmt|;
name|this
operator|.
name|top
operator|=
operator|new
name|PriorityQueue
argument_list|(
name|size
argument_list|,
operator|new
name|ReverseComp
argument_list|(
name|comp
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|this
operator|.
name|topList
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
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
name|tupleStream
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
name|tupleStream
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
name|tupleStream
operator|.
name|open
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
name|tupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
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
operator|!
name|finished
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Tuple
name|tuple
init|=
name|tupleStream
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
name|finished
operator|=
literal|true
expr_stmt|;
name|int
name|s
init|=
name|top
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
name|s
condition|;
name|i
operator|++
control|)
block|{
name|Tuple
name|t
init|=
name|top
operator|.
name|poll
argument_list|()
decl_stmt|;
name|topList
operator|.
name|addFirst
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|topList
operator|.
name|addLast
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|Tuple
name|peek
init|=
name|top
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|top
operator|.
name|size
argument_list|()
operator|>=
name|size
condition|)
block|{
if|if
condition|(
name|comp
operator|.
name|compare
argument_list|(
name|tuple
argument_list|,
name|peek
argument_list|)
operator|<
literal|0
condition|)
block|{
name|top
operator|.
name|poll
argument_list|()
expr_stmt|;
name|top
operator|.
name|add
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|top
operator|.
name|add
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|topList
operator|.
name|pollFirst
argument_list|()
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
DECL|class|ReverseComp
class|class
name|ReverseComp
implements|implements
name|Comparator
argument_list|<
name|Tuple
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|comp
specifier|private
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
decl_stmt|;
DECL|method|ReverseComp
specifier|public
name|ReverseComp
parameter_list|(
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
block|{
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|t1
parameter_list|,
name|Tuple
name|t2
parameter_list|)
block|{
return|return
name|comp
operator|.
name|compare
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
operator|*
operator|(
operator|-
literal|1
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

