begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test for ClobTransformer  *  *  * @see org.apache.solr.handler.dataimport.ClobTransformer  * @since solr 1.4  */
end_comment

begin_class
DECL|class|TestClobTransformer
specifier|public
class|class
name|TestClobTransformer
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
DECL|method|simple
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|flds
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|f
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//<field column="dsc" clob="true" name="description" />
name|f
operator|.
name|put
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|"dsc"
argument_list|)
expr_stmt|;
name|f
operator|.
name|put
argument_list|(
name|ClobTransformer
operator|.
name|CLOB
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|f
operator|.
name|put
argument_list|(
name|DataImporter
operator|.
name|NAME
argument_list|,
literal|"description"
argument_list|)
expr_stmt|;
name|flds
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|Context
name|ctx
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|flds
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
decl_stmt|;
name|Transformer
name|t
init|=
operator|new
name|ClobTransformer
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Clob
name|clob
init|=
operator|(
name|Clob
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Clob
operator|.
name|class
block|}
argument_list|,
operator|new
name|InvocationHandler
argument_list|()
block|{
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"getCharacterStream"
argument_list|)
condition|)
block|{
return|return
operator|new
name|StringReader
argument_list|(
literal|"hello!"
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|row
operator|.
name|put
argument_list|(
literal|"dsc"
argument_list|,
name|clob
argument_list|)
expr_stmt|;
name|t
operator|.
name|transformRow
argument_list|(
name|row
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello!"
argument_list|,
name|row
operator|.
name|get
argument_list|(
literal|"dsc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

