begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.xml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Factory for {@link QueryBuilder}  */
end_comment

begin_class
DECL|class|QueryBuilderFactory
specifier|public
class|class
name|QueryBuilderFactory
implements|implements
name|QueryBuilder
block|{
DECL|field|builders
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueryBuilder
argument_list|>
name|builders
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|n
parameter_list|)
throws|throws
name|ParserException
block|{
name|QueryBuilder
name|builder
init|=
name|builders
operator|.
name|get
argument_list|(
name|n
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"No QueryObjectBuilder defined for node "
operator|+
name|n
operator|.
name|getNodeName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|builder
operator|.
name|getQuery
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|addBuilder
specifier|public
name|void
name|addBuilder
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|QueryBuilder
name|builder
parameter_list|)
block|{
name|builders
operator|.
name|put
argument_list|(
name|nodeName
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
DECL|method|getQueryBuilder
specifier|public
name|QueryBuilder
name|getQueryBuilder
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
return|return
name|builders
operator|.
name|get
argument_list|(
name|nodeName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

