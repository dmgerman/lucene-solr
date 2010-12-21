begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|MockAnalyzer
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
name|Term
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
name|junit
operator|.
name|AfterClass
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_class
DECL|class|TestRemoteSearchable
specifier|public
class|class
name|TestRemoteSearchable
extends|extends
name|RemoteTestCase
block|{
DECL|field|indexStore
specifier|private
specifier|static
name|Directory
name|indexStore
decl_stmt|;
DECL|field|local
specifier|private
specifier|static
name|Searchable
name|local
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
comment|// construct an index
name|indexStore
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"test"
argument_list|,
literal|"test text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"other"
argument_list|,
literal|"other test text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|local
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|startServer
argument_list|(
name|local
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|local
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|search
specifier|private
specifier|static
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|Exception
block|{
comment|// try to search the published index
name|Searchable
index|[]
name|searchables
init|=
block|{
name|lookupRemote
argument_list|()
block|}
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
name|searchables
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|Document
name|document
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|result
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"document is null and it shouldn't be"
argument_list|,
name|document
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test text"
argument_list|,
name|document
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document.getFields() Size: "
operator|+
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ftl
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ftl
operator|.
name|add
argument_list|(
literal|"other"
argument_list|)
expr_stmt|;
name|FieldSelector
name|fs
init|=
operator|new
name|SetBasedFieldSelector
argument_list|(
name|ftl
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|document
operator|=
name|searcher
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document is null and it shouldn't be"
argument_list|,
name|document
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document.getFields() Size: "
operator|+
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|fs
operator|=
operator|new
name|MapFieldSelector
argument_list|(
literal|"other"
argument_list|)
expr_stmt|;
name|document
operator|=
name|searcher
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document is null and it shouldn't be"
argument_list|,
name|document
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document.getFields() Size: "
operator|+
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTermQuery
specifier|public
name|void
name|testTermQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBooleanQuery
specifier|public
name|void
name|testBooleanQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPhraseQuery
specifier|public
name|void
name|testPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|// Tests bug fix at http://nagoya.apache.org/bugzilla/show_bug.cgi?id=20290
annotation|@
name|Test
DECL|method|testQueryFilter
specifier|public
name|void
name|testQueryFilter
parameter_list|()
throws|throws
name|Exception
block|{
comment|// try to search the published index
name|Searchable
index|[]
name|searchables
init|=
block|{
name|lookupRemote
argument_list|()
block|}
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
name|searchables
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|nohits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"non-existent-term"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nohits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConstantScoreQuery
specifier|public
name|void
name|testConstantScoreQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// try to search the published index
name|Searchable
index|[]
name|searchables
init|=
block|{
name|lookupRemote
argument_list|()
block|}
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
name|searchables
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

