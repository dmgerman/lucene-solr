begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
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

begin_comment
comment|/* subclass to be usable from within Query.rewrite() */
end_comment

begin_comment
comment|/**  * Exception thrown when {@link BasicQueryFactory} would exceed the limit  * of query clauses.  */
end_comment

begin_class
DECL|class|TooManyBasicQueries
specifier|public
class|class
name|TooManyBasicQueries
extends|extends
name|IOException
block|{
DECL|method|TooManyBasicQueries
specifier|public
name|TooManyBasicQueries
parameter_list|(
name|int
name|maxBasicQueries
parameter_list|)
block|{
name|super
argument_list|(
literal|"Exceeded maximum of "
operator|+
name|maxBasicQueries
operator|+
literal|" basic queries."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

