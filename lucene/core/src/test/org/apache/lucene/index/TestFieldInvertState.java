begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|Map
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
name|CannedTokenStream
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
name|analysis
operator|.
name|Token
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
name|search
operator|.
name|CollectionStatistics
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
name|TermStatistics
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
name|IOUtils
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestFieldInvertState
specifier|public
class|class
name|TestFieldInvertState
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Similarity holds onto the FieldInvertState for subsequent verification.    */
DECL|class|NeverForgetsSimilarity
specifier|private
specifier|static
class|class
name|NeverForgetsSimilarity
extends|extends
name|Similarity
block|{
DECL|field|lastState
specifier|public
name|FieldInvertState
name|lastState
decl_stmt|;
DECL|field|INSTANCE
specifier|private
specifier|final
specifier|static
name|NeverForgetsSimilarity
name|INSTANCE
init|=
operator|new
name|NeverForgetsSimilarity
argument_list|()
decl_stmt|;
DECL|method|NeverForgetsSimilarity
specifier|private
name|NeverForgetsSimilarity
parameter_list|()
block|{
comment|// no
block|}
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
name|this
operator|.
name|lastState
operator|=
name|state
expr_stmt|;
return|return
literal|1
return|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setSimilarity
argument_list|(
name|NeverForgetsSimilarity
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"a"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|"c"
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|,
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|FieldInvertState
name|fis
init|=
name|NeverForgetsSimilarity
operator|.
name|INSTANCE
operator|.
name|lastState
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fis
operator|.
name|getMaxTermFrequency
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fis
operator|.
name|getUniqueTermCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fis
operator|.
name|getNumOverlap
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fis
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|w
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numUniqueTokens
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setSimilarity
argument_list|(
name|NeverForgetsSimilarity
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
name|int
name|numTokens
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|Token
index|[]
name|tokens
init|=
operator|new
name|Token
index|[
name|numTokens
index|]
decl_stmt|;
name|Map
argument_list|<
name|Character
argument_list|,
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numStacked
init|=
literal|0
decl_stmt|;
name|int
name|maxTermFreq
init|=
literal|0
decl_stmt|;
name|int
name|pos
init|=
operator|-
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|char
name|tokenChar
init|=
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numUniqueTokens
argument_list|)
argument_list|)
decl_stmt|;
name|Integer
name|oldCount
init|=
name|counts
operator|.
name|get
argument_list|(
name|tokenChar
argument_list|)
decl_stmt|;
name|int
name|newCount
decl_stmt|;
if|if
condition|(
name|oldCount
operator|==
literal|null
condition|)
block|{
name|newCount
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|newCount
operator|=
literal|1
operator|+
name|oldCount
expr_stmt|;
block|}
name|counts
operator|.
name|put
argument_list|(
name|tokenChar
argument_list|,
name|newCount
argument_list|)
expr_stmt|;
name|maxTermFreq
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxTermFreq
argument_list|,
name|newCount
argument_list|)
expr_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|tokenChar
argument_list|)
argument_list|,
literal|2
operator|*
name|i
argument_list|,
literal|2
operator|*
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
operator|==
literal|3
condition|)
block|{
name|token
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|numStacked
operator|++
expr_stmt|;
block|}
else|else
block|{
name|pos
operator|++
expr_stmt|;
block|}
name|tokens
index|[
name|i
index|]
operator|=
name|token
expr_stmt|;
block|}
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
operator|new
name|CannedTokenStream
argument_list|(
name|tokens
argument_list|)
argument_list|,
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|FieldInvertState
name|fis
init|=
name|NeverForgetsSimilarity
operator|.
name|INSTANCE
operator|.
name|lastState
decl_stmt|;
name|assertEquals
argument_list|(
name|maxTermFreq
argument_list|,
name|fis
operator|.
name|getMaxTermFrequency
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|counts
operator|.
name|size
argument_list|()
argument_list|,
name|fis
operator|.
name|getUniqueTermCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numStacked
argument_list|,
name|fis
operator|.
name|getNumOverlap
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numTokens
argument_list|,
name|fis
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|fis
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|w
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

