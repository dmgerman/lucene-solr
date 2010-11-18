begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|Random
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
name|Version
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
name|_TestUtil
import|;
end_import

begin_comment
comment|/** Silly class that randomizes the indexing experience.  EG  *  it may swap in a different merge policy/scheduler; may  *  commit periodically; may or may not optimize in the end,  *  may flush by doc count instead of RAM, etc.   */
end_comment

begin_class
DECL|class|RandomIndexWriter
specifier|public
class|class
name|RandomIndexWriter
implements|implements
name|Closeable
block|{
DECL|field|w
specifier|public
name|IndexWriter
name|w
decl_stmt|;
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
DECL|field|flushAt
name|int
name|flushAt
decl_stmt|;
DECL|field|getReaderCalled
specifier|private
name|boolean
name|getReaderCalled
decl_stmt|;
comment|// Randomly calls Thread.yield so we mixup thread scheduling
DECL|class|MockIndexWriter
specifier|private
specifier|static
specifier|final
class|class
name|MockIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
DECL|method|MockIndexWriter
specifier|public
name|MockIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|r
operator|=
name|r
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testPoint
name|boolean
name|testPoint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|2
condition|)
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/** create a RandomIndexWriter with a random config: Uses TEST_VERSION_CURRENT and MockAnalyzer */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with a random config: Uses TEST_VERSION_CURRENT */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with a random config */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Version
name|v
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|v
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with the provided config */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|r
operator|=
name|r
expr_stmt|;
name|w
operator|=
operator|new
name|MockIndexWriter
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|flushAt
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW config="
operator|+
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"codec default="
operator|+
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getCodecProvider
argument_list|()
operator|.
name|getDefaultFieldCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|docCount
operator|++
operator|==
name|flushAt
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.addDocument: now doing a commit"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|flushAt
operator|+=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addIndexes
specifier|public
name|void
name|addIndexes
parameter_list|(
name|Directory
modifier|...
name|dirs
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|w
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDocuments
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|w
operator|.
name|numDocs
argument_list|()
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|w
operator|.
name|maxDoc
argument_list|()
return|;
block|}
DECL|method|deleteAll
specifier|public
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|w
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|getReader
specifier|public
name|IndexReader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|getReaderCalled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|2
condition|)
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// If we are writing with PreFlexRW, force a full
comment|// IndexReader.open so terms are sorted in codepoint
comment|// order during searching:
if|if
condition|(
operator|!
name|w
operator|.
name|codecs
operator|.
name|getDefaultFieldCodec
argument_list|()
operator|.
name|equals
argument_list|(
literal|"PreFlex"
argument_list|)
operator|&&
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.getReader: use NRT reader"
argument_list|)
expr_stmt|;
block|}
return|return
name|w
operator|.
name|getReader
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.getReader: open new reader"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|IndexReader
operator|.
name|open
argument_list|(
name|w
operator|.
name|getDirectory
argument_list|()
argument_list|,
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if someone isn't using getReader() API, we want to be sure to
comment|// maybeOptimize since presumably they might open a reader on the dir.
if|if
condition|(
name|getReaderCalled
operator|==
literal|false
operator|&&
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|2
condition|)
block|{
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|optimize
specifier|public
name|void
name|optimize
parameter_list|()
throws|throws
name|IOException
block|{
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

