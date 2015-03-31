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
name|BeforeClass
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
name|Locale
import|;
end_import

begin_comment
comment|/**Testcase for TikaEntityProcessor  *  * @since solr 3.1  */
end_comment

begin_class
DECL|class|TestTikaEntityProcessor
specifier|public
class|class
name|TestTikaEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|conf
specifier|private
name|String
name|conf
init|=
literal|"<dataConfig>"
operator|+
literal|"<dataSource type=\"BinFileDataSource\"/>"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"Tika\" processor=\"TikaEntityProcessor\" url=\""
operator|+
name|getFile
argument_list|(
literal|"dihextras/solr-word.pdf"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\">"
operator|+
literal|"<field column=\"Author\" meta=\"true\" name=\"author\"/>"
operator|+
literal|"<field column=\"title\" meta=\"true\" name=\"title\"/>"
operator|+
literal|"<field column=\"text\"/>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|skipOnErrConf
specifier|private
name|String
name|skipOnErrConf
init|=
literal|"<dataConfig>"
operator|+
literal|"<dataSource type=\"BinFileDataSource\"/>"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"Tika\" onError=\"skip\"  processor=\"TikaEntityProcessor\" url=\""
operator|+
name|getFile
argument_list|(
literal|"dihextras/bad.doc"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\">"
operator|+
literal|"<field column=\"content\" name=\"text\"/>"
operator|+
literal|"</entity>"
operator|+
literal|"<entity name=\"Tika\" processor=\"TikaEntityProcessor\" url=\""
operator|+
name|getFile
argument_list|(
literal|"dihextras/solr-word.pdf"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\">"
operator|+
literal|"<field column=\"text\"/>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|tests
specifier|private
name|String
index|[]
name|tests
init|=
block|{
literal|"//*[@numFound='1']"
block|,
literal|"//str[@name='author'][.='Grant Ingersoll']"
block|,
literal|"//str[@name='title'][.='solr-word']"
block|,
literal|"//str[@name='text']"
block|}
decl_stmt|;
DECL|field|testsHTMLDefault
specifier|private
name|String
index|[]
name|testsHTMLDefault
init|=
block|{
literal|"//*[@numFound='1']"
block|,
literal|"//str[@name='text'][contains(.,'Basic div')]"
block|,
literal|"//str[@name='text'][contains(.,'<h1>')]"
block|,
literal|"//str[@name='text'][not(contains(.,'<div>'))]"
comment|//default mapper lower-cases elements as it maps
block|,
literal|"//str[@name='text'][not(contains(.,'<DIV>'))]"
block|}
decl_stmt|;
DECL|field|testsHTMLIdentity
specifier|private
name|String
index|[]
name|testsHTMLIdentity
init|=
block|{
literal|"//*[@numFound='1']"
block|,
literal|"//str[@name='text'][contains(.,'Basic div')]"
block|,
literal|"//str[@name='text'][contains(.,'<h1>')]"
block|,
literal|"//str[@name='text'][contains(.,'<div>')]"
block|,
literal|"//str[@name='text'][contains(.,'class=\"classAttribute\"')]"
comment|//attributes are lower-cased
block|}
decl_stmt|;
DECL|field|testsEmbedded
specifier|private
name|String
index|[]
name|testsEmbedded
init|=
block|{
literal|"//*[@numFound='1']"
block|,
literal|"//str[@name='text'][contains(.,'When in the Course')]"
block|}
decl_stmt|;
DECL|field|testsIgnoreEmbedded
specifier|private
name|String
index|[]
name|testsIgnoreEmbedded
init|=
block|{
literal|"//*[@numFound='1']"
block|,
literal|"//str[@name='text'][not(contains(.,'When in the Course'))]"
block|}
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"This test fails on UNIX with Turkish default locale (https://issues.apache.org/jira/browse/SOLR-6387)"
argument_list|,
operator|new
name|Locale
argument_list|(
literal|"tr"
argument_list|)
operator|.
name|getLanguage
argument_list|()
operator|.
name|equals
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema-no-unique-key.xml"
argument_list|,
name|getFile
argument_list|(
literal|"dihextras/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndexingWithTikaEntityProcessor
specifier|public
name|void
name|testIndexingWithTikaEntityProcessor
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkip
specifier|public
name|void
name|testSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|skipOnErrConf
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTikaHTMLMapperEmpty
specifier|public
name|void
name|testTikaHTMLMapperEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|getConfigHTML
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|testsHTMLDefault
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTikaHTMLMapperDefault
specifier|public
name|void
name|testTikaHTMLMapperDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|getConfigHTML
argument_list|(
literal|"default"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|testsHTMLDefault
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTikaHTMLMapperIdentity
specifier|public
name|void
name|testTikaHTMLMapperIdentity
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|getConfigHTML
argument_list|(
literal|"identity"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|testsHTMLIdentity
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfigHTML
specifier|private
name|String
name|getConfigHTML
parameter_list|(
name|String
name|htmlMapper
parameter_list|)
block|{
return|return
literal|"<dataConfig>"
operator|+
literal|"<dataSource type='BinFileDataSource'/>"
operator|+
literal|"<document>"
operator|+
literal|"<entity name='Tika' format='xml' processor='TikaEntityProcessor' "
operator|+
literal|"       url='"
operator|+
name|getFile
argument_list|(
literal|"dihextras/structured.html"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"' "
operator|+
operator|(
operator|(
name|htmlMapper
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
operator|(
literal|" htmlMapper='"
operator|+
name|htmlMapper
operator|+
literal|"'"
operator|)
operator|)
operator|+
literal|">"
operator|+
literal|"<field column='text'/>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
return|;
block|}
annotation|@
name|Test
DECL|method|testEmbeddedDocsLegacy
specifier|public
name|void
name|testEmbeddedDocsLegacy
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test legacy behavior: ignore embedded docs
name|runFullImport
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|testsIgnoreEmbedded
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmbeddedDocsTrue
specifier|public
name|void
name|testEmbeddedDocsTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|getConfigEmbedded
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|testsEmbedded
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmbeddedDocsFalse
specifier|public
name|void
name|testEmbeddedDocsFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|getConfigEmbedded
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|testsIgnoreEmbedded
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfigEmbedded
specifier|private
name|String
name|getConfigEmbedded
parameter_list|(
name|boolean
name|extractEmbedded
parameter_list|)
block|{
return|return
literal|"<dataConfig>"
operator|+
literal|"<dataSource type=\"BinFileDataSource\"/>"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"Tika\" processor=\"TikaEntityProcessor\" url=\""
operator|+
name|getFile
argument_list|(
literal|"dihextras/test_recursive_embedded.docx"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"       extractEmbedded=\""
operator|+
name|extractEmbedded
operator|+
literal|"\">"
operator|+
literal|"<field column=\"Author\" meta=\"true\" name=\"author\"/>"
operator|+
literal|"<field column=\"title\" meta=\"true\" name=\"title\"/>"
operator|+
literal|"<field column=\"text\"/>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
return|;
block|}
block|}
end_class

end_unit

