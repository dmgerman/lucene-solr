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
name|Closeable
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
name|io
operator|.
name|Serializable
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

begin_class
DECL|class|TupleStream
specifier|public
specifier|abstract
class|class
name|TupleStream
implements|implements
name|Closeable
implements|,
name|Serializable
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
DECL|method|TupleStream
specifier|public
name|TupleStream
parameter_list|()
block|{    }
DECL|method|writeStreamOpen
specifier|public
specifier|static
name|void
name|writeStreamOpen
parameter_list|(
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|"{\"docs\":["
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStreamClose
specifier|public
specifier|static
name|void
name|writeStreamClose
parameter_list|(
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|"]}"
argument_list|)
expr_stmt|;
block|}
DECL|method|setStreamContext
specifier|public
specifier|abstract
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
function_decl|;
DECL|method|children
specifier|public
specifier|abstract
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
function_decl|;
DECL|method|open
specifier|public
specifier|abstract
name|void
name|open
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|read
specifier|public
specifier|abstract
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getStreamSort
specifier|public
specifier|abstract
name|StreamComparator
name|getStreamSort
parameter_list|()
function_decl|;
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

