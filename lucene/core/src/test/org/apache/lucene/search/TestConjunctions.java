begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Analyzer
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
name|StringField
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
name|document
operator|.
name|Field
operator|.
name|Store
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
name|LeafReaderContext
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
name|FieldInvertState
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
name|RandomIndexWriter
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|util
operator|.
name|BytesRef
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestConjunctions
specifier|public
class|class
name|TestConjunctions
extends|extends
name|LuceneTestCase
block|{
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|F1
specifier|static
specifier|final
name|String
name|F1
init|=
literal|"title"
decl_stmt|;
DECL|field|F2
specifier|static
specifier|final
name|String
name|F2
init|=
literal|"body"
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
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|config
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// we will use docids to validate
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
literal|"lucene"
argument_list|,
literal|"lucene is a very popular search engine library"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
literal|"solr"
argument_list|,
literal|"solr is a very popular search server and is using lucene"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
literal|"nutch"
argument_list|,
literal|"nutch is an internet search engine with web crawler and is using lucene and hadoop"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|TFSimilarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doc
specifier|static
name|Document
name|doc
parameter_list|(
name|String
name|v1
parameter_list|,
name|String
name|v2
parameter_list|)
block|{
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
operator|new
name|StringField
argument_list|(
name|F1
argument_list|,
name|v1
argument_list|,
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
name|F2
argument_list|,
name|v2
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|testTermConjunctionsWithOmitTF
specifier|public
name|void
name|testTermConjunctionsWithOmitTF
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F1
argument_list|,
literal|"nutch"
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
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"is"
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
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3F
argument_list|,
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|,
literal|0.001F
argument_list|)
expr_stmt|;
comment|// f1:nutch + f2:is + f2:is
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
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
comment|// Similarity that returns the TF as score
DECL|class|TFSimilarity
specifier|private
specifier|static
class|class
name|TFSimilarity
extends|extends
name|Similarity
block|{
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|long
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
literal|1
return|;
comment|// we dont care
block|}
annotation|@
name|Override
DECL|method|computeWeight
specifier|public
name|SimWeight
name|computeWeight
parameter_list|(
name|float
name|boost
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
return|return
operator|new
name|SimWeight
argument_list|()
block|{}
return|;
block|}
annotation|@
name|Override
DECL|method|simScorer
specifier|public
name|SimScorer
name|simScorer
parameter_list|(
name|SimWeight
name|weight
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|)
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|computeSlopFactor
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1F
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|computePayloadFactor
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
literal|1F
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

