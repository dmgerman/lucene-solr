begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestURLDataSource
specifier|public
class|class
name|TestURLDataSource
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|fields
specifier|private
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|dataSource
specifier|private
name|URLDataSource
name|dataSource
init|=
operator|new
name|URLDataSource
argument_list|()
decl_stmt|;
DECL|field|variableResolver
specifier|private
name|VariableResolver
name|variableResolver
init|=
operator|new
name|VariableResolver
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
init|=
name|AbstractDataImportHandlerTestCase
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
name|variableResolver
argument_list|,
name|dataSource
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|initProps
specifier|private
name|Properties
name|initProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|substitutionsOnBaseUrl
specifier|public
name|void
name|substitutionsOnBaseUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
literal|"http://example.com/"
decl_stmt|;
name|variableResolver
operator|.
name|addNamespace
argument_list|(
literal|"dataimporter.request"
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|singletonMap
argument_list|(
literal|"baseurl"
argument_list|,
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|initProps
operator|.
name|setProperty
argument_list|(
name|URLDataSource
operator|.
name|BASE_URL
argument_list|,
literal|"${dataimporter.request.baseurl}"
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|init
argument_list|(
name|context
argument_list|,
name|initProps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|url
argument_list|,
name|dataSource
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

