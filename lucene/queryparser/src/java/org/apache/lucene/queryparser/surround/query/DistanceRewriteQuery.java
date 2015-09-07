begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|Query
import|;
end_import

begin_class
DECL|class|DistanceRewriteQuery
class|class
name|DistanceRewriteQuery
extends|extends
name|RewriteQuery
argument_list|<
name|DistanceQuery
argument_list|>
block|{
DECL|method|DistanceRewriteQuery
name|DistanceRewriteQuery
parameter_list|(
name|DistanceQuery
name|srndQuery
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
name|super
argument_list|(
name|srndQuery
argument_list|,
name|fieldName
argument_list|,
name|qf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|srndQuery
operator|.
name|getSpanNearQuery
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|,
name|qf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

