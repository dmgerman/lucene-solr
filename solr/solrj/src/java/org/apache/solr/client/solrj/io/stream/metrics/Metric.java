begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.metrics
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
operator|.
name|metrics
package|;
end_package

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
name|stream
operator|.
name|expr
operator|.
name|Expressible
import|;
end_import

begin_class
DECL|class|Metric
specifier|public
specifier|abstract
class|class
name|Metric
implements|implements
name|Serializable
implements|,
name|Expressible
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|functionName
specifier|private
name|String
name|functionName
decl_stmt|;
DECL|field|identifier
specifier|private
name|String
name|identifier
decl_stmt|;
comment|//  @Override
DECL|method|getFunctionName
specifier|public
name|String
name|getFunctionName
parameter_list|()
block|{
return|return
name|functionName
return|;
block|}
comment|//  @Override
DECL|method|setFunctionName
specifier|public
name|void
name|setFunctionName
parameter_list|(
name|String
name|functionName
parameter_list|)
block|{
name|this
operator|.
name|functionName
operator|=
name|functionName
expr_stmt|;
block|}
DECL|method|getIdentifier
specifier|public
name|String
name|getIdentifier
parameter_list|()
block|{
return|return
name|identifier
return|;
block|}
DECL|method|setIdentifier
specifier|public
name|void
name|setIdentifier
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
block|}
DECL|method|setIdentifier
specifier|public
name|void
name|setIdentifier
parameter_list|(
name|String
modifier|...
name|identifierParts
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|identifierParts
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|identifier
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
specifier|abstract
name|double
name|getValue
parameter_list|()
function_decl|;
DECL|method|update
specifier|public
specifier|abstract
name|void
name|update
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
function_decl|;
DECL|method|newInstance
specifier|public
specifier|abstract
name|Metric
name|newInstance
parameter_list|()
function_decl|;
DECL|method|getColumns
specifier|public
specifier|abstract
name|String
index|[]
name|getColumns
parameter_list|()
function_decl|;
block|}
end_class

end_unit

