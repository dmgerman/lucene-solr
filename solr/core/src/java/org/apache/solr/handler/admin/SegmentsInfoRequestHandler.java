begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|MergePolicy
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
name|MergePolicy
operator|.
name|MergeSpecification
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
name|MergePolicy
operator|.
name|OneMerge
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
name|MergeTrigger
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
name|SegmentCommitInfo
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
name|SegmentInfos
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
name|SimpleOrderedMap
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
name|handler
operator|.
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import

begin_comment
comment|/**  * This handler exposes information about last commit generation segments  */
end_comment

begin_class
DECL|class|SegmentsInfoRequestHandler
specifier|public
class|class
name|SegmentsInfoRequestHandler
extends|extends
name|RequestHandlerBase
block|{
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"segments"
argument_list|,
name|getSegmentsInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getSegmentsInfo
specifier|private
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getSegmentsInfo
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SegmentInfos
name|infos
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|directory
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|mergeCandidates
init|=
name|getMergeCandidatesNames
argument_list|(
name|req
argument_list|,
name|infos
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|segmentInfos
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|segmentInfo
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|segmentCommitInfo
range|:
name|infos
control|)
block|{
name|segmentInfo
operator|=
name|getSegmentInfo
argument_list|(
name|segmentCommitInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeCandidates
operator|.
name|contains
argument_list|(
name|segmentCommitInfo
operator|.
name|info
operator|.
name|name
argument_list|)
condition|)
block|{
name|segmentInfo
operator|.
name|add
argument_list|(
literal|"mergeCandidate"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|segmentInfos
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|segmentInfo
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
argument_list|,
name|segmentInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|segmentInfos
return|;
block|}
DECL|method|getSegmentInfo
specifier|private
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getSegmentInfo
parameter_list|(
name|SegmentCommitInfo
name|segmentCommitInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|segmentInfoMap
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|segmentInfoMap
operator|.
name|add
argument_list|(
name|NAME
argument_list|,
name|segmentCommitInfo
operator|.
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
name|segmentInfoMap
operator|.
name|add
argument_list|(
literal|"delCount"
argument_list|,
name|segmentCommitInfo
operator|.
name|getDelCount
argument_list|()
argument_list|)
expr_stmt|;
name|segmentInfoMap
operator|.
name|add
argument_list|(
literal|"sizeInBytes"
argument_list|,
name|segmentCommitInfo
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
name|segmentInfoMap
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|segmentCommitInfo
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|Long
name|timestamp
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|segmentCommitInfo
operator|.
name|info
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|get
argument_list|(
literal|"timestamp"
argument_list|)
argument_list|)
decl_stmt|;
name|segmentInfoMap
operator|.
name|add
argument_list|(
literal|"age"
argument_list|,
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
argument_list|)
expr_stmt|;
name|segmentInfoMap
operator|.
name|add
argument_list|(
literal|"source"
argument_list|,
name|segmentCommitInfo
operator|.
name|info
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|get
argument_list|(
literal|"source"
argument_list|)
argument_list|)
expr_stmt|;
name|segmentInfoMap
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|segmentCommitInfo
operator|.
name|info
operator|.
name|getVersion
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|segmentInfoMap
return|;
block|}
DECL|method|getMergeCandidatesNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getMergeCandidatesNames
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|refCounted
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|getIndexWriter
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexWriter
name|indexWriter
init|=
name|refCounted
operator|.
name|get
argument_list|()
decl_stmt|;
comment|//get chosen merge policy
name|MergePolicy
name|mp
init|=
name|indexWriter
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
comment|//Find merges
name|MergeSpecification
name|findMerges
init|=
name|mp
operator|.
name|findMerges
argument_list|(
name|MergeTrigger
operator|.
name|EXPLICIT
argument_list|,
name|infos
argument_list|,
name|indexWriter
argument_list|)
decl_stmt|;
if|if
condition|(
name|findMerges
operator|!=
literal|null
operator|&&
name|findMerges
operator|.
name|merges
operator|!=
literal|null
operator|&&
name|findMerges
operator|.
name|merges
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|OneMerge
name|merge
range|:
name|findMerges
operator|.
name|merges
control|)
block|{
comment|//TODO: add merge grouping
for|for
control|(
name|SegmentCommitInfo
name|mergeSegmentInfo
range|:
name|merge
operator|.
name|segments
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|mergeSegmentInfo
operator|.
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
finally|finally
block|{
name|refCounted
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Lucene segments info."
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|ADMIN
return|;
block|}
block|}
end_class

end_unit

