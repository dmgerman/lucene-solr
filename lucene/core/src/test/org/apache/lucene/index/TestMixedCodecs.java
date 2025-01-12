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
name|util
operator|.
name|HashSet
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
name|codecs
operator|.
name|Codec
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
DECL|class|TestMixedCodecs
specifier|public
class|class
name|TestMixedCodecs
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
literal|null
decl_stmt|;
name|int
name|docsLeftInThisSegment
init|=
literal|0
decl_stmt|;
name|int
name|docUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|docUpto
operator|<
name|NUM_DOCS
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|docUpto
operator|+
literal|" of "
operator|+
name|NUM_DOCS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docsLeftInThisSegment
operator|==
literal|0
condition|)
block|{
specifier|final
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Make sure we aggressively mix in SimpleText
comment|// since it has different impls for all codec
comment|// formats...
name|iwc
operator|.
name|setCodec
argument_list|(
name|Codec
operator|.
name|forName
argument_list|(
literal|"SimpleText"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|w
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
name|docsLeftInThisSegment
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
specifier|final
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|docUpto
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|docUpto
operator|++
expr_stmt|;
name|docsLeftInThisSegment
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: now delete..."
argument_list|)
expr_stmt|;
block|}
comment|// Random delete half the docs:
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|deleted
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|deleted
operator|.
name|size
argument_list|()
operator|<
name|NUM_DOCS
operator|/
literal|2
condition|)
block|{
specifier|final
name|Integer
name|toDelete
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|NUM_DOCS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deleted
operator|.
name|contains
argument_list|(
name|toDelete
argument_list|)
condition|)
block|{
name|deleted
operator|.
name|add
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|toDelete
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|6
condition|)
block|{
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
name|deleted
operator|.
name|size
argument_list|()
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|w
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
block|}
end_class

end_unit

