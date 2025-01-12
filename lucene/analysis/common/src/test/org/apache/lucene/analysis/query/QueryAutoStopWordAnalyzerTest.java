begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|query
package|;
end_package

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
name|*
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
name|IndexReader
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
DECL|class|QueryAutoStopWordAnalyzerTest
specifier|public
class|class
name|QueryAutoStopWordAnalyzerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|variedFieldValues
name|String
name|variedFieldValues
index|[]
init|=
block|{
literal|"the"
block|,
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|,
literal|"jumped"
block|,
literal|"over"
block|,
literal|"the"
block|,
literal|"lazy"
block|,
literal|"boring"
block|,
literal|"dog"
block|}
decl_stmt|;
DECL|field|repetitiveFieldValues
name|String
name|repetitiveFieldValues
index|[]
init|=
block|{
literal|"boring"
block|,
literal|"boring"
block|,
literal|"vaguelyboring"
block|}
decl_stmt|;
DECL|field|dir
name|RAMDirectory
name|dir
decl_stmt|;
DECL|field|appAnalyzer
name|Analyzer
name|appAnalyzer
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|protectedAnalyzer
name|QueryAutoStopWordAnalyzer
name|protectedAnalyzer
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
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|appAnalyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|appAnalyzer
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
literal|200
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|variedFieldValue
init|=
name|variedFieldValues
index|[
name|i
operator|%
name|variedFieldValues
operator|.
name|length
index|]
decl_stmt|;
name|String
name|repetitiveFieldValue
init|=
name|repetitiveFieldValues
index|[
name|i
operator|%
name|repetitiveFieldValues
operator|.
name|length
index|]
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"variedField"
argument_list|,
name|variedFieldValue
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"repetitiveField"
argument_list|,
name|repetitiveFieldValue
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
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|appAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
DECL|method|testNoStopwords
specifier|public
name|void
name|testNoStopwords
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Note: an empty list of fields passed in
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|TokenStream
name|protectedTokenStream
init|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"variedField"
argument_list|,
literal|"quick"
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|}
argument_list|)
expr_stmt|;
name|protectedTokenStream
operator|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"boring"
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"boring"
block|}
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefaultStopwordsAllFields
specifier|public
name|void
name|testDefaultStopwordsAllFields
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|TokenStream
name|protectedTokenStream
init|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"boring"
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Default stop word filtering will remove boring
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testStopwordsAllFieldsMaxPercentDocs
specifier|public
name|void
name|testStopwordsAllFieldsMaxPercentDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
literal|1f
operator|/
literal|2f
argument_list|)
expr_stmt|;
name|TokenStream
name|protectedTokenStream
init|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"boring"
argument_list|)
decl_stmt|;
comment|// A filter on terms in> one half of docs remove boring
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|protectedTokenStream
operator|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"vaguelyboring"
argument_list|)
expr_stmt|;
comment|// A filter on terms in> half of docs should not remove vaguelyBoring
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"vaguelyboring"
block|}
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
literal|1f
operator|/
literal|4f
argument_list|)
expr_stmt|;
name|protectedTokenStream
operator|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"vaguelyboring"
argument_list|)
expr_stmt|;
comment|// A filter on terms in> quarter of docs should remove vaguelyBoring
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testStopwordsPerFieldMaxPercentDocs
specifier|public
name|void
name|testStopwordsPerFieldMaxPercentDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"variedField"
argument_list|)
argument_list|,
literal|1f
operator|/
literal|2f
argument_list|)
expr_stmt|;
name|TokenStream
name|protectedTokenStream
init|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"boring"
argument_list|)
decl_stmt|;
comment|// A filter on one Field should not affect queries on another
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"boring"
block|}
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"variedField"
argument_list|,
literal|"repetitiveField"
argument_list|)
argument_list|,
literal|1f
operator|/
literal|2f
argument_list|)
expr_stmt|;
name|protectedTokenStream
operator|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"boring"
argument_list|)
expr_stmt|;
comment|// A filter on the right Field should affect queries on it
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testStopwordsPerFieldMaxDocFreq
specifier|public
name|void
name|testStopwordsPerFieldMaxDocFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"repetitiveField"
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|int
name|numStopWords
init|=
name|protectedAnalyzer
operator|.
name|getStopWords
argument_list|(
literal|"repetitiveField"
argument_list|)
operator|.
name|length
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have identified stop words"
argument_list|,
name|numStopWords
operator|>
literal|0
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"variedField"
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|int
name|numNewStopWords
init|=
name|protectedAnalyzer
operator|.
name|getStopWords
argument_list|(
literal|"repetitiveField"
argument_list|)
operator|.
name|length
operator|+
name|protectedAnalyzer
operator|.
name|getStopWords
argument_list|(
literal|"variedField"
argument_list|)
operator|.
name|length
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have identified more stop words"
argument_list|,
name|numNewStopWords
operator|>
name|numStopWords
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNoFieldNamePollution
specifier|public
name|void
name|testNoFieldNamePollution
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|appAnalyzer
argument_list|,
name|reader
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"repetitiveField"
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|TokenStream
name|protectedTokenStream
init|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"boring"
argument_list|)
decl_stmt|;
comment|// Check filter set up OK
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|protectedTokenStream
operator|=
name|protectedAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"variedField"
argument_list|,
literal|"boring"
argument_list|)
expr_stmt|;
comment|// Filter should not prevent stopwords in one field being used in another
name|assertTokenStreamContents
argument_list|(
name|protectedTokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"boring"
block|}
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryAutoStopWordAnalyzer
name|a
init|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|reader
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
literal|"this boring"
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
literal|"this"
block|}
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

