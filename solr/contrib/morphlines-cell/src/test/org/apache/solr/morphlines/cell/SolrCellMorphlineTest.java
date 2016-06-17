begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.morphlines.cell
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|cell
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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|util
operator|.
name|Constants
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
name|common
operator|.
name|SolrInputDocument
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
name|common
operator|.
name|params
operator|.
name|MapSolrParams
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
name|handler
operator|.
name|extraction
operator|.
name|ExtractionDateUtil
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
name|handler
operator|.
name|extraction
operator|.
name|SolrContentHandler
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
name|morphlines
operator|.
name|solr
operator|.
name|AbstractSolrMorphlineTestBase
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
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

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

begin_class
DECL|class|SolrCellMorphlineTest
specifier|public
class|class
name|SolrCellMorphlineTest
extends|extends
name|AbstractSolrMorphlineTestBase
block|{
DECL|field|expectedRecords
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expectedRecords
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|expectedRecordContents
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|expectedRecordContents
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass2
specifier|public
specifier|static
name|void
name|beforeClass2
parameter_list|()
block|{
name|assumeFalse
argument_list|(
literal|"FIXME: Morphlines currently has issues with Windows paths"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
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
name|String
name|path
init|=
name|RESOURCES_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-documents"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"sample-statuses-20120906-141433.avro"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"sample-statuses-20120906-141433"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"sample-statuses-20120906-141433.gz"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"sample-statuses-20120906-141433.bz2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"cars.csv"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"cars.csv.gz"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"cars.tar.gz"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"cars.tsv"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"cars.ssv"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"test-documents.7z"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"test-documents.cpio"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"test-documents.tar"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"test-documents.tbz2"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"test-documents.tgz"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"test-documents.zip"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|expectedRecords
operator|.
name|put
argument_list|(
name|path
operator|+
literal|"multiline-stacktrace.log"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored__attachment_mimetype"
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_exif_isospeedratings"
argument_list|,
literal|"400"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_meta_creation_date"
argument_list|,
literal|"2009-08-11T09:09:45"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_tiff_model"
argument_list|,
literal|"Canon EOS 40D"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|NON_EMPTY_FIELD
argument_list|)
expr_stmt|;
name|expectedRecordContents
operator|.
name|put
argument_list|(
literal|"/testJPEG_EXIF.jpg"
argument_list|,
name|record
argument_list|)
expr_stmt|;
name|expectedRecordContents
operator|.
name|put
argument_list|(
literal|"/testJPEG_EXIF.jpg.tar"
argument_list|,
name|record
argument_list|)
expr_stmt|;
name|expectedRecordContents
operator|.
name|put
argument_list|(
literal|"/testJPEG_EXIF.jpg.tar.gz"
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|file
init|=
name|path
operator|+
literal|"testWORD_various.doc"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored__attachment_mimetype"
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_author"
argument_list|,
literal|"Michael McCandless"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_creation_date"
argument_list|,
literal|"2011-09-02T10:11:00Z"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_title"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_keywords"
argument_list|,
literal|"Keyword1 Keyword2"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_subject"
argument_list|,
literal|"Subject is here"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|NON_EMPTY_FIELD
argument_list|)
expr_stmt|;
name|expectedRecordContents
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|file
init|=
name|path
operator|+
literal|"testPDF.pdf"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored__attachment_mimetype"
argument_list|,
literal|"application/pdf"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_author"
argument_list|,
literal|"Bertrand DelacrÃ©taz"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_creation_date"
argument_list|,
literal|"2007-09-15T09:02:31Z"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_title"
argument_list|,
literal|"Apache Tika - Apache Tika"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_xmp_creatortool"
argument_list|,
literal|"Firefox"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|NON_EMPTY_FIELD
argument_list|)
expr_stmt|;
name|expectedRecordContents
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|file
init|=
name|path
operator|+
literal|"email.eml"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|"Patrick Foo<foo@cloudera.com>"
decl_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored__attachment_mimetype"
argument_list|,
literal|"message/rfc822"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_author"
argument_list|,
name|name
argument_list|)
expr_stmt|;
comment|//record.put("ignored_content_length", "1068");
name|record
operator|.
name|put
argument_list|(
literal|"ignored_creation_date"
argument_list|,
literal|"2013-11-27T20:01:23Z"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_message_from"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_message_to"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_creator"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_dc_creator"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_dc_title"
argument_list|,
literal|"Test EML"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_dcterms_created"
argument_list|,
literal|"2013-11-27T20:01:23Z"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_meta_author"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_meta_creation_date"
argument_list|,
literal|"2013-11-27T20:01:23Z"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_subject"
argument_list|,
literal|"Test EML"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|NON_EMPTY_FIELD
argument_list|)
expr_stmt|;
name|expectedRecordContents
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|file
init|=
name|path
operator|+
literal|"testEXCEL.xlsx"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored__attachment_mimetype"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_author"
argument_list|,
literal|"Keith Bennett"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_creation_date"
argument_list|,
literal|"2007-10-01T16:13:56Z"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"ignored_title"
argument_list|,
literal|"Simple Excel document"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|NON_EMPTY_FIELD
argument_list|)
expr_stmt|;
name|expectedRecordContents
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/custom-mimetypes.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|tempDir
operator|+
literal|"/custom-mimetypes.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-6489"
argument_list|)
DECL|method|testSolrCellJPGCompressed
specifier|public
name|void
name|testSolrCellJPGCompressed
parameter_list|()
throws|throws
name|Exception
block|{
name|morphline
operator|=
name|createMorphline
argument_list|(
literal|"test-morphlines"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrCellJPGCompressed"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|RESOURCES_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-documents"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
name|String
index|[]
name|files
init|=
operator|new
name|String
index|[]
block|{
name|path
operator|+
literal|"testJPEG_EXIF.jpg"
block|,
name|path
operator|+
literal|"testJPEG_EXIF.jpg.gz"
block|,
name|path
operator|+
literal|"testJPEG_EXIF.jpg.tar.gz"
block|,
comment|//path + "jpeg2000.jp2",
block|}
decl_stmt|;
name|testDocumentTypesInternal
argument_list|(
name|files
argument_list|,
name|expectedRecords
argument_list|,
name|expectedRecordContents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSolrCellXML
specifier|public
name|void
name|testSolrCellXML
parameter_list|()
throws|throws
name|Exception
block|{
name|morphline
operator|=
name|createMorphline
argument_list|(
literal|"test-morphlines"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrCellXML"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|RESOURCES_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-documents"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
name|String
index|[]
name|files
init|=
operator|new
name|String
index|[]
block|{
name|path
operator|+
literal|"testXML2.xml"
block|,     }
decl_stmt|;
name|testDocumentTypesInternal
argument_list|(
name|files
argument_list|,
name|expectedRecords
argument_list|,
name|expectedRecordContents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-6489"
argument_list|)
DECL|method|testSolrCellDocumentTypes
specifier|public
name|void
name|testSolrCellDocumentTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractSolrMorphlineTestBase
operator|.
name|setupMorphline
argument_list|(
name|tempDir
argument_list|,
literal|"test-morphlines/solrCellDocumentTypes"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|morphline
operator|=
name|createMorphline
argument_list|(
operator|new
name|File
argument_list|(
name|tempDir
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/test-morphlines/solrCellDocumentTypes"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|RESOURCES_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-documents"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
name|String
index|[]
name|files
init|=
operator|new
name|String
index|[]
block|{
name|path
operator|+
literal|"testBMPfp.txt"
block|,
name|path
operator|+
literal|"boilerplate.html"
block|,
name|path
operator|+
literal|"NullHeader.docx"
block|,
name|path
operator|+
literal|"testWORD_various.doc"
block|,
name|path
operator|+
literal|"testPDF.pdf"
block|,
name|path
operator|+
literal|"testJPEG_EXIF.jpg"
block|,
name|path
operator|+
literal|"testJPEG_EXIF.jpg.gz"
block|,
name|path
operator|+
literal|"testJPEG_EXIF.jpg.tar.gz"
block|,
name|path
operator|+
literal|"testXML.xml"
block|,
name|path
operator|+
literal|"cars.csv"
block|,
comment|//        path + "cars.tsv",
comment|//        path + "cars.ssv",
name|path
operator|+
literal|"cars.csv.gz"
block|,
name|path
operator|+
literal|"cars.tar.gz"
block|,
name|path
operator|+
literal|"sample-statuses-20120906-141433.avro"
block|,
name|path
operator|+
literal|"sample-statuses-20120906-141433"
block|,
name|path
operator|+
literal|"sample-statuses-20120906-141433.gz"
block|,
name|path
operator|+
literal|"sample-statuses-20120906-141433.bz2"
block|,
name|path
operator|+
literal|"email.eml"
block|,     }
decl_stmt|;
name|testDocumentTypesInternal
argument_list|(
name|files
argument_list|,
name|expectedRecords
argument_list|,
name|expectedRecordContents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSolrCellDocumentTypes2
specifier|public
name|void
name|testSolrCellDocumentTypes2
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractSolrMorphlineTestBase
operator|.
name|setupMorphline
argument_list|(
name|tempDir
argument_list|,
literal|"test-morphlines/solrCellDocumentTypes"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|morphline
operator|=
name|createMorphline
argument_list|(
operator|new
name|File
argument_list|(
name|tempDir
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/test-morphlines/solrCellDocumentTypes"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|RESOURCES_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-documents"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
name|String
index|[]
name|files
init|=
operator|new
name|String
index|[]
block|{
name|path
operator|+
literal|"testPPT_various.ppt"
block|,
name|path
operator|+
literal|"testPPT_various.pptx"
block|,
name|path
operator|+
literal|"testEXCEL.xlsx"
block|,
name|path
operator|+
literal|"testEXCEL.xls"
block|,
name|path
operator|+
literal|"testPages.pages"
block|,
comment|//path + "testNumbers.numbers",
comment|//path + "testKeynote.key",
name|path
operator|+
literal|"testRTFVarious.rtf"
block|,
name|path
operator|+
literal|"complex.mbox"
block|,
name|path
operator|+
literal|"test-outlook.msg"
block|,
name|path
operator|+
literal|"testEMLX.emlx"
block|,
name|path
operator|+
literal|"testRFC822"
block|,
name|path
operator|+
literal|"rsstest.rss"
block|,
comment|//        path + "testDITA.dita",
name|path
operator|+
literal|"testMP3i18n.mp3"
block|,
name|path
operator|+
literal|"testAIFF.aif"
block|,
name|path
operator|+
literal|"testFLAC.flac"
block|,
comment|//        path + "testFLAC.oga",
comment|//        path + "testVORBIS.ogg",
name|path
operator|+
literal|"testMP4.m4a"
block|,
name|path
operator|+
literal|"testWAV.wav"
block|,
comment|//        path + "testWMA.wma",
name|path
operator|+
literal|"testFLV.flv"
block|,
comment|//        path + "testWMV.wmv",
name|path
operator|+
literal|"testBMP.bmp"
block|,
name|path
operator|+
literal|"testPNG.png"
block|,
name|path
operator|+
literal|"testPSD.psd"
block|,
name|path
operator|+
literal|"testSVG.svg"
block|,
name|path
operator|+
literal|"testTIFF.tif"
block|,
comment|//        path + "test-documents.7z",
comment|//        path + "test-documents.cpio",
comment|//        path + "test-documents.tar",
comment|//        path + "test-documents.tbz2",
comment|//        path + "test-documents.tgz",
comment|//        path + "test-documents.zip",
comment|//        path + "test-zip-of-zip.zip",
comment|//        path + "testJAR.jar",
comment|//        path + "testKML.kml",
comment|//        path + "testRDF.rdf",
name|path
operator|+
literal|"testVISIO.vsd"
block|,
comment|//        path + "testWAR.war",
comment|//        path + "testWindows-x86-32.exe",
comment|//        path + "testWINMAIL.dat",
comment|//        path + "testWMF.wmf",
block|}
decl_stmt|;
name|testDocumentTypesInternal
argument_list|(
name|files
argument_list|,
name|expectedRecords
argument_list|,
name|expectedRecordContents
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the ContentHandler properly strips the illegal characters    */
annotation|@
name|Test
DECL|method|testTransformValue
specifier|public
name|void
name|testTransformValue
parameter_list|()
block|{
name|String
name|fieldName
init|=
literal|"user_name"
decl_stmt|;
name|assertFalse
argument_list|(
literal|"foobar"
operator|.
name|equals
argument_list|(
name|getFoobarWithNonChars
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// load illegal char string into a metadata field and generate a new document,
comment|// which will cause the ContentHandler to be invoked.
name|metadata
operator|.
name|set
argument_list|(
name|fieldName
argument_list|,
name|getFoobarWithNonChars
argument_list|()
argument_list|)
expr_stmt|;
name|StripNonCharSolrContentHandlerFactory
name|contentHandlerFactory
init|=
operator|new
name|StripNonCharSolrContentHandlerFactory
argument_list|(
name|ExtractionDateUtil
operator|.
name|DEFAULT_DATE_FORMATS
argument_list|)
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SolrContentHandler
name|contentHandler
init|=
name|contentHandlerFactory
operator|.
name|createSolrContentHandler
argument_list|(
name|metadata
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|contentHandler
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|String
name|foobar
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"foobar"
operator|.
name|equals
argument_list|(
name|foobar
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns string "foobar" with illegal characters interspersed.    */
DECL|method|getFoobarWithNonChars
specifier|private
name|String
name|getFoobarWithNonChars
parameter_list|()
block|{
name|char
name|illegalChar
init|=
literal|'\uffff'
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|illegalChar
argument_list|)
operator|.
name|append
argument_list|(
name|illegalChar
argument_list|)
operator|.
name|append
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|append
argument_list|(
name|illegalChar
argument_list|)
operator|.
name|append
argument_list|(
name|illegalChar
argument_list|)
operator|.
name|append
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|append
argument_list|(
name|illegalChar
argument_list|)
operator|.
name|append
argument_list|(
name|illegalChar
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

