begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.sql
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|jdbc
operator|.
name|CalciteConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|jdbc
operator|.
name|Driver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * JDBC driver for Calcite Solr.  *  *<p>It accepts connect strings that start with "jdbc:calcitesolr:".</p>  */
end_comment

begin_class
DECL|class|CalciteSolrDriver
specifier|public
class|class
name|CalciteSolrDriver
extends|extends
name|Driver
block|{
DECL|field|CONNECT_STRING_PREFIX
specifier|public
specifier|final
specifier|static
name|String
name|CONNECT_STRING_PREFIX
init|=
literal|"jdbc:calcitesolr:"
decl_stmt|;
DECL|method|CalciteSolrDriver
specifier|private
name|CalciteSolrDriver
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
static|static
block|{
operator|new
name|CalciteSolrDriver
argument_list|()
operator|.
name|register
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConnectStringPrefix
specifier|protected
name|String
name|getConnectStringPrefix
parameter_list|()
block|{
return|return
name|CONNECT_STRING_PREFIX
return|;
block|}
annotation|@
name|Override
DECL|method|connect
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|url
parameter_list|,
name|Properties
name|info
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|acceptsURL
argument_list|(
name|url
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Connection
name|connection
init|=
name|super
operator|.
name|connect
argument_list|(
name|url
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
operator|(
name|CalciteConnection
operator|)
name|connection
decl_stmt|;
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|String
name|schemaName
init|=
name|info
operator|.
name|getProperty
argument_list|(
literal|"zk"
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"zk must be set"
argument_list|)
throw|;
block|}
name|rootSchema
operator|.
name|add
argument_list|(
name|schemaName
argument_list|,
operator|new
name|SolrSchema
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the default schema
name|calciteConnection
operator|.
name|setSchema
argument_list|(
name|schemaName
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
end_class

end_unit

