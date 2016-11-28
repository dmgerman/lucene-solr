begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|SolrTestCaseJ4
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
name|JavabinTupleStreamParser
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
name|SolrDocument
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
name|SolrDocumentList
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
name|JavaBinCodec
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
name|common
operator|.
name|util
operator|.
name|Utils
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
name|response
operator|.
name|SmileWriterTest
operator|.
name|constructSolrDocList
import|;
end_import

begin_class
DECL|class|TestJavabinTupleStreamParser
specifier|public
class|class
name|TestJavabinTupleStreamParser
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testKnown
specifier|public
name|void
name|testKnown
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"  \"responseHeader\":{\n"
operator|+
literal|"    \"zkConnected\":true,\n"
operator|+
literal|"    \"status\":0,\n"
operator|+
literal|"    \"QTime\":46},\n"
operator|+
literal|"  \"response\":{\n"
operator|+
literal|"    \"numFound\":2,\n"
operator|+
literal|"    \"start\":0,\n"
operator|+
literal|"    \"docs\":[\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"id\":\"2\",\n"
operator|+
literal|"        \"a_s\":\"hello2\",\n"
operator|+
literal|"        \"a_i\":2,\n"
operator|+
literal|"        \"a_f\":0.0},\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"id\":\"3\",\n"
operator|+
literal|"        \"a_s\":\"hello3\",\n"
operator|+
literal|"        \"a_i\":3,\n"
operator|+
literal|"        \"a_f\":3.0}]}}"
decl_stmt|;
name|SimpleOrderedMap
name|nl
init|=
name|convert2OrderedMap
argument_list|(
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSONString
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|serialize
argument_list|(
name|nl
argument_list|)
decl_stmt|;
name|JavabinTupleStreamParser
name|parser
init|=
operator|new
name|JavabinTupleStreamParser
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|map
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
DECL|method|convert2OrderedMap
specifier|public
name|SimpleOrderedMap
name|convert2OrderedMap
parameter_list|(
name|Map
name|m
parameter_list|)
block|{
name|SimpleOrderedMap
name|result
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|m
operator|.
name|forEach
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|v
operator|instanceof
name|List
condition|)
name|v
operator|=
operator|(
operator|(
name|List
operator|)
name|v
operator|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|v
operator|instanceof
name|Map
condition|)
name|v
operator|=
name|convert2OrderedMap
argument_list|(
operator|(
name|Map
operator|)
name|v
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"f"
argument_list|,
literal|1.0f
argument_list|,
literal|"s"
argument_list|,
literal|"Some str 1"
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|,
literal|"f"
argument_list|,
literal|2.0f
argument_list|,
literal|"s"
argument_list|,
literal|"Some str 2"
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|,
literal|"f"
argument_list|,
literal|1.0f
argument_list|,
literal|"s"
argument_list|,
literal|"Some str 3"
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|,
literal|"RESPONSE_TIME"
argument_list|,
literal|206
argument_list|,
literal|"sleepMillis"
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
init|=
name|l
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|TupleStream
name|tupleStream
init|=
operator|new
name|TupleStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{        }
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
return|return
operator|new
name|Tuple
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
return|;
else|else
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
name|withFunctionName
argument_list|(
literal|"Dummy"
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
name|Explanation
operator|.
name|ExpressionType
operator|.
name|STREAM_SOURCE
argument_list|)
operator|.
name|withExpression
argument_list|(
literal|"--non-expressible--"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|serialize
argument_list|(
name|tupleStream
argument_list|)
decl_stmt|;
name|JavabinTupleStreamParser
name|parser
init|=
operator|new
name|JavabinTupleStreamParser
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
name|m
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
operator|(
name|Double
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"f"
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
operator|(
name|Double
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"f"
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
operator|(
name|Double
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"f"
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"EOF"
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|JavabinTupleStreamParser
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
operator|(
name|Float
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"f"
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
operator|(
name|Float
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"f"
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
operator|(
name|Float
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"f"
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"EOF"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSolrDocumentList
specifier|public
name|void
name|testSolrDocumentList
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryResponse
name|response
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrDocumentList
name|l
init|=
name|constructSolrDocList
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|marshal
argument_list|(
name|response
operator|.
name|getValues
argument_list|()
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|serialize
argument_list|(
name|response
operator|.
name|getValues
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
name|m
init|=
literal|null
decl_stmt|;
name|JavabinTupleStreamParser
name|parser
init|=
operator|new
name|JavabinTupleStreamParser
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|m
operator|=
name|parser
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|l
operator|.
name|size
argument_list|()
argument_list|,
name|list
operator|.
name|size
argument_list|()
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|compareSolrDocument
argument_list|(
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|SolrDocument
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|serialize
specifier|public
specifier|static
name|byte
index|[]
name|serialize
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrQueryResponse
name|response
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|getValues
argument_list|()
operator|.
name|add
argument_list|(
literal|"results"
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|marshal
argument_list|(
name|response
operator|.
name|getValues
argument_list|()
argument_list|,
name|baos
argument_list|)
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

