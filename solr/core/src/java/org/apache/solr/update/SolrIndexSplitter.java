begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
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
name|index
operator|.
name|CodecReader
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
name|FilterCodecReader
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
name|LeafReader
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
name|PostingsEnum
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
name|Fields
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
name|SlowCodecReaderWrapper
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
name|Terms
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
name|TermsEnum
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
name|CharsRefBuilder
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
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|CompositeIdRouter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|DocRouter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|HashBasedRouter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SuppressForbidden
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|BitsFilteredPostingsEnum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|RefCounted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|SolrIndexSplitter
specifier|public
class|class
name|SolrIndexSplitter
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|searcher
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|field
name|SchemaField
name|field
decl_stmt|;
DECL|field|ranges
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
decl_stmt|;
DECL|field|rangesArr
name|DocRouter
operator|.
name|Range
index|[]
name|rangesArr
decl_stmt|;
comment|// same as ranges list, but an array for extra speed in inner loops
DECL|field|paths
name|List
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
DECL|field|cores
name|List
argument_list|<
name|SolrCore
argument_list|>
name|cores
decl_stmt|;
DECL|field|router
name|DocRouter
name|router
decl_stmt|;
DECL|field|hashRouter
name|HashBasedRouter
name|hashRouter
decl_stmt|;
DECL|field|numPieces
name|int
name|numPieces
decl_stmt|;
DECL|field|currPartition
name|int
name|currPartition
init|=
literal|0
decl_stmt|;
DECL|field|routeFieldName
name|String
name|routeFieldName
decl_stmt|;
DECL|field|splitKey
name|String
name|splitKey
decl_stmt|;
DECL|method|SolrIndexSplitter
specifier|public
name|SolrIndexSplitter
parameter_list|(
name|SplitIndexCommand
name|cmd
parameter_list|)
block|{
name|searcher
operator|=
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
name|ranges
operator|=
name|cmd
operator|.
name|ranges
expr_stmt|;
name|paths
operator|=
name|cmd
operator|.
name|paths
expr_stmt|;
name|cores
operator|=
name|cmd
operator|.
name|cores
expr_stmt|;
name|router
operator|=
name|cmd
operator|.
name|router
expr_stmt|;
name|hashRouter
operator|=
name|router
operator|instanceof
name|HashBasedRouter
condition|?
operator|(
name|HashBasedRouter
operator|)
name|router
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|ranges
operator|==
literal|null
condition|)
block|{
name|numPieces
operator|=
name|paths
operator|!=
literal|null
condition|?
name|paths
operator|.
name|size
argument_list|()
else|:
name|cores
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numPieces
operator|=
name|ranges
operator|.
name|size
argument_list|()
expr_stmt|;
name|rangesArr
operator|=
name|ranges
operator|.
name|toArray
argument_list|(
operator|new
name|DocRouter
operator|.
name|Range
index|[
name|ranges
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
name|routeFieldName
operator|=
name|cmd
operator|.
name|routeFieldName
expr_stmt|;
if|if
condition|(
name|routeFieldName
operator|==
literal|null
condition|)
block|{
name|field
operator|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|field
operator|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|routeFieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|splitKey
operator|!=
literal|null
condition|)
block|{
name|splitKey
operator|=
name|getRouteKey
argument_list|(
name|cmd
operator|.
name|splitKey
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|split
specifier|public
name|void
name|split
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getRawReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FixedBitSet
index|[]
argument_list|>
name|segmentDocSets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SolrIndexSplitter: partitions="
operator|+
name|numPieces
operator|+
literal|" segments="
operator|+
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LeafReaderContext
name|readerContext
range|:
name|leaves
control|)
block|{
assert|assert
name|readerContext
operator|.
name|ordInParent
operator|==
name|segmentDocSets
operator|.
name|size
argument_list|()
assert|;
comment|// make sure we're going in order
name|FixedBitSet
index|[]
name|docSets
init|=
name|split
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
name|segmentDocSets
operator|.
name|add
argument_list|(
name|docSets
argument_list|)
expr_stmt|;
block|}
comment|// would it be more efficient to write segment-at-a-time to each new index?
comment|// - need to worry about number of open descriptors
comment|// - need to worry about if IW.addIndexes does a sync or not...
comment|// - would be more efficient on the read side, but prob less efficient merging
for|for
control|(
name|int
name|partitionNumber
init|=
literal|0
init|;
name|partitionNumber
operator|<
name|numPieces
condition|;
name|partitionNumber
operator|++
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrIndexSplitter: partition #"
operator|+
name|partitionNumber
operator|+
literal|" partitionCount="
operator|+
name|numPieces
operator|+
operator|(
name|ranges
operator|!=
literal|null
condition|?
literal|" range="
operator|+
name|ranges
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|iwRef
init|=
literal|null
decl_stmt|;
name|IndexWriter
name|iw
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|SolrCore
name|subCore
init|=
name|cores
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
decl_stmt|;
name|iwRef
operator|=
name|subCore
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|getIndexWriter
argument_list|(
name|subCore
argument_list|)
expr_stmt|;
name|iw
operator|=
name|iwRef
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
name|core
init|=
name|searcher
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|paths
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
decl_stmt|;
name|iw
operator|=
name|SolrIndexWriter
operator|.
name|create
argument_list|(
name|core
argument_list|,
literal|"SplittingIndexWriter"
operator|+
name|partitionNumber
operator|+
operator|(
name|ranges
operator|!=
literal|null
condition|?
literal|" "
operator|+
name|ranges
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
else|:
literal|""
operator|)
argument_list|,
name|path
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|,
literal|true
argument_list|,
name|core
operator|.
name|getLatestSchema
argument_list|()
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
argument_list|,
name|core
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|,
name|core
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// This removes deletions but optimize might still be needed because sub-shards will have the same number of segments as the parent shard.
for|for
control|(
name|int
name|segmentNumber
init|=
literal|0
init|;
name|segmentNumber
operator|<
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|segmentNumber
operator|++
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrIndexSplitter: partition #"
operator|+
name|partitionNumber
operator|+
literal|" partitionCount="
operator|+
name|numPieces
operator|+
operator|(
name|ranges
operator|!=
literal|null
condition|?
literal|" range="
operator|+
name|ranges
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
else|:
literal|""
operator|)
operator|+
literal|" segment #"
operator|+
name|segmentNumber
operator|+
literal|" segmentCount="
operator|+
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CodecReader
name|subReader
init|=
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
name|leaves
operator|.
name|get
argument_list|(
name|segmentNumber
argument_list|)
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
name|iw
operator|.
name|addIndexes
argument_list|(
operator|new
name|LiveDocsReader
argument_list|(
name|subReader
argument_list|,
name|segmentDocSets
operator|.
name|get
argument_list|(
name|segmentNumber
argument_list|)
index|[
name|partitionNumber
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// we commit explicitly instead of sending a CommitUpdateCommand through the processor chain
comment|// because the sub-shard cores will just ignore such a commit because the update log is not
comment|// in active state at this time.
name|setCommitData
argument_list|(
name|iw
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|iwRef
operator|!=
literal|null
condition|)
block|{
name|iwRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|success
condition|)
block|{
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|iw
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis, commit time should be used only for debugging purposes, "
operator|+
literal|" but currently suspiciously used for replication as well"
argument_list|)
DECL|method|setCommitData
specifier|private
name|void
name|setCommitData
parameter_list|(
name|IndexWriter
name|iw
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|commitData
operator|.
name|put
argument_list|(
name|SolrIndexWriter
operator|.
name|COMMIT_TIME_MSEC_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|setLiveCommitData
argument_list|(
name|commitData
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|split
name|FixedBitSet
index|[]
name|split
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|readerContext
operator|.
name|reader
argument_list|()
decl_stmt|;
name|FixedBitSet
index|[]
name|docSets
init|=
operator|new
name|FixedBitSet
index|[
name|numPieces
index|]
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
name|docSets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docSets
index|[
name|i
index|]
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|fields
operator|==
literal|null
condition|?
literal|null
else|:
name|fields
operator|.
name|terms
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|==
literal|null
condition|?
literal|null
else|:
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
return|return
name|docSets
return|;
name|BytesRef
name|term
init|=
literal|null
decl_stmt|;
name|PostingsEnum
name|postingsEnum
init|=
literal|null
decl_stmt|;
name|int
index|[]
name|docsMatchingRanges
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ranges
operator|!=
literal|null
condition|)
block|{
comment|// +1 because documents can belong to *zero*, one, several or all ranges in rangesArr
name|docsMatchingRanges
operator|=
operator|new
name|int
index|[
name|rangesArr
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
block|}
name|CharsRefBuilder
name|idRef
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
break|break;
comment|// figure out the hash for the term
comment|// FUTURE: if conversion to strings costs too much, we could
comment|// specialize and use the hash function that can work over bytes.
name|field
operator|.
name|getType
argument_list|()
operator|.
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|idRef
argument_list|)
expr_stmt|;
name|String
name|idString
init|=
name|idRef
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|splitKey
operator|!=
literal|null
condition|)
block|{
comment|// todo have composite routers support these kind of things instead
name|String
name|part1
init|=
name|getRouteKey
argument_list|(
name|idString
argument_list|)
decl_stmt|;
if|if
condition|(
name|part1
operator|==
literal|null
condition|)
continue|continue;
if|if
condition|(
operator|!
name|splitKey
operator|.
name|equals
argument_list|(
name|part1
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
name|int
name|hash
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|hashRouter
operator|!=
literal|null
condition|)
block|{
name|hash
operator|=
name|hashRouter
operator|.
name|sliceHash
argument_list|(
name|idString
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|postingsEnum
operator|=
name|BitsFilteredPostingsEnum
operator|.
name|wrap
argument_list|(
name|postingsEnum
argument_list|,
name|liveDocs
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|doc
init|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
if|if
condition|(
name|ranges
operator|==
literal|null
condition|)
block|{
name|docSets
index|[
name|currPartition
index|]
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|currPartition
operator|=
operator|(
name|currPartition
operator|+
literal|1
operator|)
operator|%
name|numPieces
expr_stmt|;
block|}
else|else
block|{
name|int
name|matchingRangesCount
init|=
literal|0
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
name|rangesArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// inner-loop: use array here for extra speed.
if|if
condition|(
name|rangesArr
index|[
name|i
index|]
operator|.
name|includes
argument_list|(
name|hash
argument_list|)
condition|)
block|{
name|docSets
index|[
name|i
index|]
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
operator|++
name|matchingRangesCount
expr_stmt|;
block|}
block|}
name|docsMatchingRanges
index|[
name|matchingRangesCount
index|]
operator|++
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|docsMatchingRanges
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|docsMatchingRanges
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
if|if
condition|(
literal|0
operator|==
name|docsMatchingRanges
index|[
name|ii
index|]
condition|)
continue|continue;
switch|switch
condition|(
name|ii
condition|)
block|{
case|case
literal|0
case|:
comment|// document loss
name|log
operator|.
name|error
argument_list|(
literal|"Splitting {}: {} documents belong to no shards and will be dropped"
argument_list|,
name|reader
argument_list|,
name|docsMatchingRanges
index|[
name|ii
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
comment|// normal case, each document moves to one of the sub-shards
name|log
operator|.
name|info
argument_list|(
literal|"Splitting {}: {} documents will move into a sub-shard"
argument_list|,
name|reader
argument_list|,
name|docsMatchingRanges
index|[
name|ii
index|]
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// document duplication
name|log
operator|.
name|error
argument_list|(
literal|"Splitting {}: {} documents will be moved to multiple ({}) sub-shards"
argument_list|,
name|reader
argument_list|,
name|docsMatchingRanges
index|[
name|ii
index|]
argument_list|,
name|ii
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|docSets
return|;
block|}
DECL|method|getRouteKey
specifier|public
specifier|static
name|String
name|getRouteKey
parameter_list|(
name|String
name|idString
parameter_list|)
block|{
name|int
name|idx
init|=
name|idString
operator|.
name|indexOf
argument_list|(
name|CompositeIdRouter
operator|.
name|SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<=
literal|0
condition|)
return|return
literal|null
return|;
name|String
name|part1
init|=
name|idString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|int
name|commaIdx
init|=
name|part1
operator|.
name|indexOf
argument_list|(
name|CompositeIdRouter
operator|.
name|bitsSeparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|commaIdx
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|commaIdx
operator|+
literal|1
operator|<
name|part1
operator|.
name|length
argument_list|()
condition|)
block|{
name|char
name|ch
init|=
name|part1
operator|.
name|charAt
argument_list|(
name|commaIdx
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
condition|)
block|{
name|part1
operator|=
name|part1
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|commaIdx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|part1
return|;
block|}
comment|// change livedocs on the reader to delete those docs we don't want
DECL|class|LiveDocsReader
specifier|static
class|class
name|LiveDocsReader
extends|extends
name|FilterCodecReader
block|{
DECL|field|liveDocs
specifier|final
name|FixedBitSet
name|liveDocs
decl_stmt|;
DECL|field|numDocs
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|method|LiveDocsReader
specifier|public
name|LiveDocsReader
parameter_list|(
name|CodecReader
name|in
parameter_list|,
name|FixedBitSet
name|liveDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|liveDocs
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

