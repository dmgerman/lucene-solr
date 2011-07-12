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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|Filter
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
name|TermRangeFilter
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
name|Bits
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
name|FixedBitSet
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Split an index based on a {@link Filter}.  */
end_comment

begin_class
DECL|class|PKIndexSplitter
specifier|public
class|class
name|PKIndexSplitter
block|{
DECL|field|docsInFirstIndex
specifier|private
specifier|final
name|Filter
name|docsInFirstIndex
decl_stmt|;
DECL|field|input
specifier|private
specifier|final
name|Directory
name|input
decl_stmt|;
DECL|field|dir1
specifier|private
specifier|final
name|Directory
name|dir1
decl_stmt|;
DECL|field|dir2
specifier|private
specifier|final
name|Directory
name|dir2
decl_stmt|;
DECL|field|config1
specifier|private
specifier|final
name|IndexWriterConfig
name|config1
decl_stmt|;
DECL|field|config2
specifier|private
specifier|final
name|IndexWriterConfig
name|config2
decl_stmt|;
comment|/**    * Split an index based on a {@link Filter}. All documents that match the filter    * are sent to dir1, remaining ones to dir2.    */
DECL|method|PKIndexSplitter
specifier|public
name|PKIndexSplitter
parameter_list|(
name|Version
name|version
parameter_list|,
name|Directory
name|input
parameter_list|,
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
parameter_list|,
name|Filter
name|docsInFirstIndex
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|dir1
argument_list|,
name|dir2
argument_list|,
name|docsInFirstIndex
argument_list|,
name|newDefaultConfig
argument_list|(
name|version
argument_list|)
argument_list|,
name|newDefaultConfig
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newDefaultConfig
specifier|private
specifier|static
name|IndexWriterConfig
name|newDefaultConfig
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
operator|new
name|IndexWriterConfig
argument_list|(
name|version
argument_list|,
literal|null
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
return|;
block|}
DECL|method|PKIndexSplitter
specifier|public
name|PKIndexSplitter
parameter_list|(
name|Directory
name|input
parameter_list|,
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
parameter_list|,
name|Filter
name|docsInFirstIndex
parameter_list|,
name|IndexWriterConfig
name|config1
parameter_list|,
name|IndexWriterConfig
name|config2
parameter_list|)
block|{
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|dir1
operator|=
name|dir1
expr_stmt|;
name|this
operator|.
name|dir2
operator|=
name|dir2
expr_stmt|;
name|this
operator|.
name|docsInFirstIndex
operator|=
name|docsInFirstIndex
expr_stmt|;
name|this
operator|.
name|config1
operator|=
name|config1
expr_stmt|;
name|this
operator|.
name|config2
operator|=
name|config2
expr_stmt|;
block|}
comment|/**    * Split an index based on a  given primary key term     * and a 'middle' term.  If the middle term is present, it's    * sent to dir2.    */
DECL|method|PKIndexSplitter
specifier|public
name|PKIndexSplitter
parameter_list|(
name|Version
name|version
parameter_list|,
name|Directory
name|input
parameter_list|,
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
parameter_list|,
name|Term
name|midTerm
parameter_list|)
block|{
name|this
argument_list|(
name|version
argument_list|,
name|input
argument_list|,
name|dir1
argument_list|,
name|dir2
argument_list|,
operator|new
name|TermRangeFilter
argument_list|(
name|midTerm
operator|.
name|field
argument_list|()
argument_list|,
literal|null
argument_list|,
name|midTerm
operator|.
name|bytes
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|PKIndexSplitter
specifier|public
name|PKIndexSplitter
parameter_list|(
name|Directory
name|input
parameter_list|,
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
parameter_list|,
name|Term
name|midTerm
parameter_list|,
name|IndexWriterConfig
name|config1
parameter_list|,
name|IndexWriterConfig
name|config2
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|dir1
argument_list|,
name|dir2
argument_list|,
operator|new
name|TermRangeFilter
argument_list|(
name|midTerm
operator|.
name|field
argument_list|()
argument_list|,
literal|null
argument_list|,
name|midTerm
operator|.
name|bytes
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|,
name|config1
argument_list|,
name|config2
argument_list|)
expr_stmt|;
block|}
DECL|method|split
specifier|public
name|void
name|split
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|input
argument_list|)
decl_stmt|;
try|try
block|{
comment|// pass an individual config in here since one config can not be reused!
name|createIndex
argument_list|(
name|config1
argument_list|,
name|dir1
argument_list|,
name|reader
argument_list|,
name|docsInFirstIndex
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
name|config2
argument_list|,
name|dir2
argument_list|,
name|reader
argument_list|,
name|docsInFirstIndex
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
operator|!
name|success
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createIndex
specifier|private
name|void
name|createIndex
parameter_list|(
name|IndexWriterConfig
name|config
parameter_list|,
name|Directory
name|target
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|Filter
name|preserveFilter
parameter_list|,
name|boolean
name|negateFilter
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|target
argument_list|,
name|config
argument_list|)
decl_stmt|;
try|try
block|{
name|w
operator|.
name|addIndexes
argument_list|(
operator|new
name|DocumentFilteredIndexReader
argument_list|(
name|reader
argument_list|,
name|preserveFilter
argument_list|,
name|negateFilter
argument_list|)
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
operator|!
name|success
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DocumentFilteredIndexReader
specifier|public
specifier|static
class|class
name|DocumentFilteredIndexReader
extends|extends
name|FilterIndexReader
block|{
DECL|field|liveDocs
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|numDocs
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|method|DocumentFilteredIndexReader
specifier|public
name|DocumentFilteredIndexReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Filter
name|preserveFilter
parameter_list|,
name|boolean
name|negateFilter
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|in
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSet
name|docs
init|=
name|preserveFilter
operator|.
name|getDocIdSet
argument_list|(
operator|(
name|AtomicReaderContext
operator|)
name|in
operator|.
name|getTopReaderContext
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docs
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|!=
literal|null
condition|)
block|{
name|bits
operator|.
name|or
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|negateFilter
condition|)
block|{
name|bits
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
specifier|final
name|Bits
name|oldLiveDocs
init|=
name|in
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
assert|assert
name|oldLiveDocs
operator|!=
literal|null
assert|;
specifier|final
name|DocIdSetIterator
name|it
init|=
name|bits
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|it
operator|.
name|nextDoc
argument_list|()
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|oldLiveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
comment|// we can safely modify the current bit, as the iterator already stepped over it:
name|bits
operator|.
name|clear
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|this
operator|.
name|liveDocs
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
operator|(
name|int
operator|)
name|bits
operator|.
name|cardinality
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
operator|(
name|in
operator|.
name|maxDoc
argument_list|()
operator|!=
name|numDocs
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
name|liveDocs
return|;
block|}
block|}
block|}
end_class

end_unit

