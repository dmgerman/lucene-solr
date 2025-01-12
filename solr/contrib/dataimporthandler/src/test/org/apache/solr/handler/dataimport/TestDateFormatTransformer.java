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
name|text
operator|.
name|SimpleDateFormat
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
comment|/**  *<p>  * Test for DateFormatTransformer  *</p>  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestDateFormatTransformer
specifier|public
class|class
name|TestDateFormatTransformer
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testTransformRow_SingleRow
specifier|public
name|void
name|testTransformRow_SingleRow
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
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|"lastModified"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|"dateAdded"
argument_list|,
name|RegexTransformer
operator|.
name|SRC_COL_NAME
argument_list|,
literal|"lastModified"
argument_list|,
name|DateFormatTransformer
operator|.
name|DATE_TIME_FMT
argument_list|,
literal|"${xyz.myDateFormat}"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"MM/dd/yyyy"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Date
name|now
init|=
name|format
operator|.
name|parse
argument_list|(
name|format
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|createMap
argument_list|(
literal|"lastModified"
argument_list|,
name|format
operator|.
name|format
argument_list|(
name|now
argument_list|)
argument_list|)
decl_stmt|;
name|VariableResolver
name|resolver
init|=
operator|new
name|VariableResolver
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
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"xyz"
argument_list|,
name|createMap
argument_list|(
literal|"myDateFormat"
argument_list|,
literal|"MM/dd/yyyy"
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
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
operator|new
name|DateFormatTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|row
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|now
argument_list|,
name|row
operator|.
name|get
argument_list|(
literal|"dateAdded"
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
DECL|method|testTransformRow_MultipleRows
specifier|public
name|void
name|testTransformRow_MultipleRows
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
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|"lastModified"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|"dateAdded"
argument_list|,
name|RegexTransformer
operator|.
name|SRC_COL_NAME
argument_list|,
literal|"lastModified"
argument_list|,
name|DateFormatTransformer
operator|.
name|DATE_TIME_FMT
argument_list|,
literal|"MM/dd/yyyy hh:mm:ss.SSS"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"MM/dd/yyyy hh:mm:ss.SSS"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Date
name|now1
init|=
name|format
operator|.
name|parse
argument_list|(
name|format
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Date
name|now2
init|=
name|format
operator|.
name|parse
argument_list|(
name|format
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
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
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|format
operator|.
name|format
argument_list|(
name|now1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|format
operator|.
name|format
argument_list|(
name|now2
argument_list|)
argument_list|)
expr_stmt|;
name|row
operator|.
name|put
argument_list|(
literal|"lastModified"
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|VariableResolver
name|resolver
init|=
operator|new
name|VariableResolver
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
name|getContext
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
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
operator|new
name|DateFormatTransformer
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
argument_list|<
name|Object
argument_list|>
name|output
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|output
operator|.
name|add
argument_list|(
name|now1
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|now2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|row
operator|.
name|get
argument_list|(
literal|"dateAdded"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

