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
name|Assert
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

begin_comment
comment|/**  *<p>  * Test for NumberFormatTransformer  *</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestNumberFormatTransformer
specifier|public
class|class
name|TestNumberFormatTransformer
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testTransformRow_SingleNumber
specifier|public
name|void
name|testTransformRow_SingleNumber
parameter_list|()
block|{
name|List
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
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"num"
argument_list|,
name|NumberFormatTransformer
operator|.
name|FORMAT_STYLE
argument_list|,
name|NumberFormatTransformer
operator|.
name|NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|l
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
name|m
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"num"
argument_list|,
literal|"123,567"
argument_list|)
decl_stmt|;
operator|new
name|NumberFormatTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|m
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|123567
argument_list|)
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"num"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testTransformRow_MultipleNumbers
specifier|public
name|void
name|testTransformRow_MultipleNumbers
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|"inputs"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|"outputs"
argument_list|,
name|RegexTransformer
operator|.
name|SRC_COL_NAME
argument_list|,
literal|"inputs"
argument_list|,
name|NumberFormatTransformer
operator|.
name|FORMAT_STYLE
argument_list|,
name|NumberFormatTransformer
operator|.
name|NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|inputs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|inputs
operator|.
name|add
argument_list|(
literal|"123,567"
argument_list|)
expr_stmt|;
name|inputs
operator|.
name|add
argument_list|(
literal|"245,678"
argument_list|)
expr_stmt|;
name|Map
name|row
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"inputs"
argument_list|,
name|inputs
argument_list|)
decl_stmt|;
name|VariableResolverImpl
name|resolver
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"e"
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|fields
argument_list|,
literal|null
argument_list|)
decl_stmt|;
operator|new
name|NumberFormatTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|row
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|List
name|output
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|output
operator|.
name|add
argument_list|(
operator|new
name|Long
argument_list|(
literal|123567
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
operator|new
name|Long
argument_list|(
literal|245678
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|outputRow
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"inputs"
argument_list|,
name|inputs
argument_list|,
literal|"outputs"
argument_list|,
name|output
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|outputRow
argument_list|,
name|row
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

