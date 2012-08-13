begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.uima
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|analysis
operator|.
name|TokenStream
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|TextField
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|StoredDocument
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|search
operator|.
name|TopDocs
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_comment
comment|/**  * Testcase for {@link UIMABaseAnalyzer}  */
end_comment

begin_class
DECL|class|UIMABaseAnalyzerTest
specifier|public
class|class
name|UIMABaseAnalyzerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|UIMABaseAnalyzer
name|analyzer
decl_stmt|;
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
name|analyzer
operator|=
operator|new
name|UIMABaseAnalyzer
argument_list|(
literal|"/uima/AggregateSentenceAE.xml"
argument_list|,
literal|"org.apache.uima.TokenAnnotation"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|baseUIMAAnalyzerStreamTest
specifier|public
name|void
name|baseUIMAAnalyzerStreamTest
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"the big brown fox jumped on the wood"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|,
literal|"big"
block|,
literal|"brown"
block|,
literal|"fox"
block|,
literal|"jumped"
block|,
literal|"on"
block|,
literal|"the"
block|,
literal|"wood"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|baseUIMAAnalyzerIntegrationTest
specifier|public
name|void
name|baseUIMAAnalyzerIntegrationTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
comment|// add the first doc
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|dummyTitle
init|=
literal|"this is a dummy title "
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"title"
argument_list|,
name|dummyTitle
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|dummyContent
init|=
literal|"there is some content written here"
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"contents"
argument_list|,
name|dummyContent
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// try the search over the first doc
name|DirectoryReader
name|directoryReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directoryReader
argument_list|)
decl_stmt|;
name|TopDocs
name|result
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|totalHits
operator|>
literal|0
argument_list|)
expr_stmt|;
name|StoredDocument
name|d
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|result
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dummyTitle
argument_list|,
name|d
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
operator|.
name|getField
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dummyContent
argument_list|,
name|d
operator|.
name|getField
argument_list|(
literal|"contents"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// add a second doc
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|String
name|dogmasTitle
init|=
literal|"dogmas"
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"title"
argument_list|,
name|dogmasTitle
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|dogmasContents
init|=
literal|"white men can't jump"
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"contents"
argument_list|,
name|dogmasContents
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|directoryReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directoryReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directoryReader
argument_list|)
expr_stmt|;
name|result
operator|=
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|StoredDocument
name|d1
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|result
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d1
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dogmasTitle
argument_list|,
name|d1
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d1
operator|.
name|getField
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dogmasContents
argument_list|,
name|d1
operator|.
name|getField
argument_list|(
literal|"contents"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// do a matchalldocs query to retrieve both docs
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directoryReader
argument_list|)
expr_stmt|;
name|result
operator|=
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|UIMABaseAnalyzer
argument_list|(
literal|"/uima/TestAggregateSentenceAE.xml"
argument_list|,
literal|"org.apache.lucene.uima.ts.TokenAnnotation"
argument_list|)
argument_list|,
literal|100
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

