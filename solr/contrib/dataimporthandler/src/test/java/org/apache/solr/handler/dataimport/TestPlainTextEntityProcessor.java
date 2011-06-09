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
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Test for PlainTextEntityProcessor  *  *  * @see org.apache.solr.handler.dataimport.PlainTextEntityProcessor  * @since solr 1.4  */
end_comment

begin_class
DECL|class|TestPlainTextEntityProcessor
specifier|public
class|class
name|TestPlainTextEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|DATA_CONFIG
argument_list|)
expr_stmt|;
name|TestDocBuilder
operator|.
name|SolrWriterImpl
name|sw
init|=
operator|new
name|TestDocBuilder
operator|.
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|DataImporter
operator|.
name|RequestParams
name|rp
init|=
operator|new
name|DataImporter
operator|.
name|RequestParams
argument_list|(
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|)
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|sw
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DS
operator|.
name|s
argument_list|,
name|sw
operator|.
name|docs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|DS
specifier|public
specifier|static
class|class
name|DS
extends|extends
name|DataSource
block|{
DECL|field|s
specifier|static
name|String
name|s
init|=
literal|"hello world"
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|getData
specifier|public
name|Object
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{      }
block|}
DECL|field|DATA_CONFIG
specifier|static
name|String
name|DATA_CONFIG
init|=
literal|"<dataConfig>\n"
operator|+
literal|"\t<dataSource type=\"TestPlainTextEntityProcessor$DS\" />\n"
operator|+
literal|"\t<document>\n"
operator|+
literal|"\t\t<entity processor=\"PlainTextEntityProcessor\" name=\"x\" query=\"x\">\n"
operator|+
literal|"\t\t\t<field column=\"plainText\" name=\"x\" />\n"
operator|+
literal|"\t\t</entity>\n"
operator|+
literal|"\t</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
block|}
end_class

end_unit

