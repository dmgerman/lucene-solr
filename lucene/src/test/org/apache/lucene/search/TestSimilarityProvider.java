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
name|MultiDocValues
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
name|Norm
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
name|search
operator|.
name|similarities
operator|.
name|SimilarityProvider
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
name|TFIDFSimilarity
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
DECL|class|TestSimilarityProvider
specifier|public
class|class
name|TestSimilarityProvider
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|SimilarityProvider
name|sim
init|=
operator|new
name|ExampleSimilarityProvider
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setSimilarityProvider
argument_list|(
name|sim
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|Field
name|field2
init|=
name|newField
argument_list|(
literal|"bar"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field2
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"quick brown fox"
argument_list|)
expr_stmt|;
name|field2
operator|.
name|setValue
argument_list|(
literal|"quick brown fox"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"jumps over lazy brown dog"
argument_list|)
expr_stmt|;
name|field2
operator|.
name|setValue
argument_list|(
literal|"jumps over lazy brown dog"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
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
name|setSimilarityProvider
argument_list|(
name|sim
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
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
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sanity check of norms writer
comment|// TODO: generalize
name|byte
name|fooNorms
index|[]
init|=
operator|(
name|byte
index|[]
operator|)
name|MultiDocValues
operator|.
name|getNormDocValues
argument_list|(
name|reader
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|getSource
argument_list|()
operator|.
name|getArray
argument_list|()
decl_stmt|;
name|byte
name|barNorms
index|[]
init|=
operator|(
name|byte
index|[]
operator|)
name|MultiDocValues
operator|.
name|getNormDocValues
argument_list|(
name|reader
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|getSource
argument_list|()
operator|.
name|getArray
argument_list|()
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
name|fooNorms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|fooNorms
index|[
name|i
index|]
operator|==
name|barNorms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// sanity check of searching
name|TopDocs
name|foodocs
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
literal|"foo"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|foodocs
operator|.
name|totalHits
operator|>
literal|0
argument_list|)
expr_stmt|;
name|TopDocs
name|bardocs
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
literal|"bar"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bardocs
operator|.
name|totalHits
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|foodocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
operator|<
name|bardocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
DECL|class|ExampleSimilarityProvider
specifier|private
class|class
name|ExampleSimilarityProvider
implements|implements
name|SimilarityProvider
block|{
DECL|field|sim1
specifier|private
name|Similarity
name|sim1
init|=
operator|new
name|Sim1
argument_list|()
decl_stmt|;
DECL|field|sim2
specifier|private
name|Similarity
name|sim2
init|=
operator|new
name|Sim2
argument_list|()
decl_stmt|;
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"foo"
argument_list|)
condition|)
block|{
return|return
name|sim1
return|;
block|}
else|else
block|{
return|return
name|sim2
return|;
block|}
block|}
block|}
DECL|class|Sim1
specifier|private
class|class
name|Sim1
extends|extends
name|TFIDFSimilarity
block|{
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|void
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|,
name|Norm
name|norm
parameter_list|)
block|{
name|norm
operator|.
name|setByte
argument_list|(
name|encodeNormValue
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
annotation|@
name|Override
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
annotation|@
name|Override
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
annotation|@
name|Override
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
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
literal|1f
return|;
block|}
block|}
DECL|class|Sim2
specifier|private
class|class
name|Sim2
extends|extends
name|TFIDFSimilarity
block|{
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|void
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|,
name|Norm
name|norm
parameter_list|)
block|{
name|norm
operator|.
name|setByte
argument_list|(
name|encodeNormValue
argument_list|(
literal|10f
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|10f
return|;
block|}
annotation|@
name|Override
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
literal|10f
return|;
block|}
annotation|@
name|Override
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
return|return
literal|10f
return|;
block|}
annotation|@
name|Override
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
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
literal|1f
return|;
block|}
block|}
block|}
end_class

end_unit

