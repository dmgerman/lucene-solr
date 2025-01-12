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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|ArrayList
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
name|graph
operator|.
name|Traversal
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
name|handler
operator|.
name|GraphHandler
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
name|request
operator|.
name|SolrQueryRequest
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

begin_class
DECL|class|GraphMLResponseWriter
specifier|public
class|class
name|GraphMLResponseWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
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
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|/* NOOP */
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|res
parameter_list|)
block|{
return|return
literal|"application/xml"
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|Exception
name|e1
init|=
name|res
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|e1
operator|!=
literal|null
condition|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|TupleStream
name|stream
init|=
operator|(
name|TupleStream
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"stream"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stream
operator|instanceof
name|GraphHandler
operator|.
name|DummyErrorStream
condition|)
block|{
name|GraphHandler
operator|.
name|DummyErrorStream
name|d
init|=
operator|(
name|GraphHandler
operator|.
name|DummyErrorStream
operator|)
name|stream
decl_stmt|;
name|Exception
name|e
init|=
name|d
operator|.
name|getException
argument_list|()
decl_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|Traversal
name|traversal
init|=
operator|(
name|Traversal
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"traversal"
argument_list|)
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
name|Tuple
name|tuple
init|=
literal|null
decl_stmt|;
name|int
name|edgeCount
init|=
literal|0
decl_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" "
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|print
argument_list|(
literal|"xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns "
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|"http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">"
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|"<graph id=\"G\" edgedefault=\"directed\">"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//Output the graph
name|tuple
operator|=
name|stream
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
break|break;
block|}
name|String
name|id
init|=
name|tuple
operator|.
name|getString
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
if|if
condition|(
name|traversal
operator|.
name|isMultiCollection
argument_list|()
condition|)
block|{
name|id
operator|=
name|tuple
operator|.
name|getString
argument_list|(
literal|"collection"
argument_list|)
operator|+
literal|"."
operator|+
name|id
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"<node id=\""
operator|+
name|replace
argument_list|(
name|id
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|outfields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|tuple
operator|.
name|fields
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|keys
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"node"
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
literal|"ancestors"
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
literal|"collection"
argument_list|)
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|outfields
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|outfields
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|printWriter
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nodeAttribute
range|:
name|outfields
control|)
block|{
name|Object
name|o
init|=
name|tuple
operator|.
name|get
argument_list|(
name|nodeAttribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|printWriter
operator|.
name|println
argument_list|(
literal|"<data key=\""
operator|+
name|nodeAttribute
operator|+
literal|"\">"
operator|+
name|o
operator|.
name|toString
argument_list|()
operator|+
literal|"</data>"
argument_list|)
expr_stmt|;
block|}
block|}
name|printWriter
operator|.
name|println
argument_list|(
literal|"</node>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|printWriter
operator|.
name|println
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|ancestors
init|=
name|tuple
operator|.
name|getStrings
argument_list|(
literal|"ancestors"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ancestors
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|ancestor
range|:
name|ancestors
control|)
block|{
operator|++
name|edgeCount
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"<edge id=\""
operator|+
name|edgeCount
operator|+
literal|"\" "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" source=\""
operator|+
name|replace
argument_list|(
name|ancestor
argument_list|)
operator|+
literal|"\" "
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|" target=\""
operator|+
name|replace
argument_list|(
name|id
argument_list|)
operator|+
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"</graph></graphml>"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|replace
specifier|private
name|String
name|replace
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|">"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|replace
argument_list|(
literal|">"
argument_list|,
literal|"&gt;"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|"<"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|replace
argument_list|(
literal|"<"
argument_list|,
literal|"&lt;"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|"\""
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|"&quot;"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|"'"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|replace
argument_list|(
literal|"'"
argument_list|,
literal|"&apos;"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|"&"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|replace
argument_list|(
literal|"&"
argument_list|,
literal|"&amp;"
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

