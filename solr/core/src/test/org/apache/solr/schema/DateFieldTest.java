begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Date
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
name|IndexableField
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
name|SolrTestCaseJ4
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
name|core
operator|.
name|SolrConfig
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
name|core
operator|.
name|SolrResourceLoader
import|;
end_import

begin_class
DECL|class|DateFieldTest
specifier|public
class|class
name|DateFieldTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|testInstanceDir
specifier|private
specifier|final
name|String
name|testInstanceDir
init|=
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
decl_stmt|;
DECL|field|testConfHome
specifier|private
specifier|final
name|String
name|testConfHome
init|=
name|testInstanceDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
DECL|field|f
specifier|private
name|TrieDateField
name|f
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|SolrConfig
name|config
init|=
operator|new
name|SolrConfig
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|testInstanceDir
argument_list|)
argument_list|)
argument_list|,
name|testConfHome
operator|+
literal|"solrconfig.xml"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|testConfHome
operator|+
literal|"schema.xml"
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|f
operator|=
operator|new
name|TrieDateField
argument_list|()
expr_stmt|;
name|f
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: Many other tests were moved to DateMathParserTest
DECL|method|testCreateField
specifier|public
name|void
name|testCreateField
parameter_list|()
block|{
name|int
name|props
init|=
name|FieldProperties
operator|.
name|INDEXED
operator|^
name|FieldProperties
operator|.
name|STORED
decl_stmt|;
name|SchemaField
name|sf
init|=
operator|new
name|SchemaField
argument_list|(
literal|"test"
argument_list|,
name|f
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// String
name|IndexableField
name|out
init|=
name|f
operator|.
name|createField
argument_list|(
name|sf
argument_list|,
literal|"1995-12-31T23:59:59Z"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|820454399000L
argument_list|,
name|f
operator|.
name|toObject
argument_list|(
name|out
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// Date obj
name|out
operator|=
name|f
operator|.
name|createField
argument_list|(
name|sf
argument_list|,
operator|new
name|Date
argument_list|(
literal|820454399000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|820454399000L
argument_list|,
name|f
operator|.
name|toObject
argument_list|(
name|out
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// Date math
name|out
operator|=
name|f
operator|.
name|createField
argument_list|(
name|sf
argument_list|,
literal|"1995-12-31T23:59:59.99Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|820454699990L
argument_list|,
name|f
operator|.
name|toObject
argument_list|(
name|out
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

