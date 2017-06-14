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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|TimeoutSuite
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|MockDirectoryWrapper
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
operator|.
name|Monster
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
operator|.
name|SuppressCodecs
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
operator|.
name|SuppressSysoutChecks
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
name|TimeUnits
import|;
end_import

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|,
literal|"Direct"
block|}
argument_list|)
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|80
operator|*
name|TimeUnits
operator|.
name|HOUR
argument_list|)
comment|// effectively no limit
annotation|@
name|Monster
argument_list|(
literal|"Takes ~30min"
argument_list|)
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"Stuff gets printed"
argument_list|)
DECL|class|Test2BDocs
specifier|public
class|class
name|Test2BDocs
extends|extends
name|LuceneTestCase
block|{
comment|// indexes Integer.MAX_VALUE docs with indexed field(s)
DECL|method|test2BDocs
specifier|public
name|void
name|test2BDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BDocs"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
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
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|256.0
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|10
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|getDefaultCodec
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
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"a"
argument_list|,
name|StringField
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|IndexWriter
operator|.
name|MAX_DOCS
condition|;
name|i
operator|++
control|)
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
name|i
operator|%
operator|(
literal|10
operator|*
literal|1000
operator|*
literal|1000
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"indexed: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"verifying..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|term
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
literal|'a'
expr_stmt|;
name|term
operator|.
name|length
operator|=
literal|1
expr_stmt|;
name|long
name|skips
init|=
literal|0
decl_stmt|;
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|r
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|int
name|lim
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
literal|"f1"
argument_list|)
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|TermsEnum
name|te
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|PostingsEnum
name|docs
init|=
name|te
operator|.
name|postings
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// skip randomly through the term
for|for
control|(
name|int
name|target
init|=
operator|-
literal|1
init|;
condition|;
control|)
block|{
name|int
name|maxSkipSize
init|=
name|lim
operator|-
name|target
operator|+
literal|1
decl_stmt|;
comment|// do a smaller skip half of the time
if|if
condition|(
name|rnd
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|maxSkipSize
operator|=
name|Math
operator|.
name|min
argument_list|(
literal|256
argument_list|,
name|maxSkipSize
argument_list|)
expr_stmt|;
block|}
name|int
name|newTarget
init|=
name|target
operator|+
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxSkipSize
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|newTarget
operator|>=
name|lim
condition|)
block|{
if|if
condition|(
name|target
operator|+
literal|1
operator|>=
name|lim
condition|)
break|break;
comment|// we already skipped to end, so break.
name|newTarget
operator|=
name|lim
operator|-
literal|1
expr_stmt|;
comment|// skip to end
block|}
name|target
operator|=
name|newTarget
expr_stmt|;
name|int
name|res
init|=
name|docs
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
name|PostingsEnum
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
name|assertTrue
argument_list|(
name|res
operator|>=
name|target
argument_list|)
expr_stmt|;
name|skips
operator|++
expr_stmt|;
name|target
operator|=
name|res
expr_stmt|;
block|}
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Skip count="
operator|+
name|skips
operator|+
literal|" seconds="
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toSeconds
argument_list|(
name|end
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|skips
operator|>
literal|0
assert|;
block|}
block|}
end_class

end_unit

