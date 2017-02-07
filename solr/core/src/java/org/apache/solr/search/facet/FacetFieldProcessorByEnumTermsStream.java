begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

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
name|Iterator
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
name|MultiPostingsEnum
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
name|search
operator|.
name|TermQuery
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
name|StringHelper
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
name|SolrException
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
name|schema
operator|.
name|TrieField
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
name|DocSet
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
name|HashDocSet
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
name|search
operator|.
name|SortedIntDocSet
import|;
end_import

begin_comment
comment|/**  * Enumerates indexed terms in order in a streaming fashion.  * It's able to stream since no data needs to be accumulated so long as it's index order.  */
end_comment

begin_class
DECL|class|FacetFieldProcessorByEnumTermsStream
class|class
name|FacetFieldProcessorByEnumTermsStream
extends|extends
name|FacetFieldProcessor
implements|implements
name|Closeable
block|{
DECL|field|bucketsToSkip
name|long
name|bucketsToSkip
decl_stmt|;
DECL|field|bucketsReturned
name|long
name|bucketsReturned
decl_stmt|;
DECL|field|closed
name|boolean
name|closed
decl_stmt|;
DECL|field|countOnly
name|boolean
name|countOnly
decl_stmt|;
DECL|field|hasSubFacets
name|boolean
name|hasSubFacets
decl_stmt|;
comment|// true if there are subfacets
DECL|field|minDfFilterCache
name|int
name|minDfFilterCache
decl_stmt|;
DECL|field|docs
name|DocSet
name|docs
decl_stmt|;
DECL|field|fastForRandomSet
name|DocSet
name|fastForRandomSet
decl_stmt|;
DECL|field|termsEnum
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
DECL|field|deState
name|SolrIndexSearcher
operator|.
name|DocsEnumState
name|deState
init|=
literal|null
decl_stmt|;
DECL|field|postingsEnum
name|PostingsEnum
name|postingsEnum
decl_stmt|;
DECL|field|startTermBytes
name|BytesRef
name|startTermBytes
decl_stmt|;
DECL|field|term
name|BytesRef
name|term
decl_stmt|;
DECL|field|leaves
name|LeafReaderContext
index|[]
name|leaves
decl_stmt|;
DECL|method|FacetFieldProcessorByEnumTermsStream
name|FacetFieldProcessorByEnumTermsStream
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetField
name|freq
parameter_list|,
name|SchemaField
name|sf
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|freq
argument_list|,
name|sf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
comment|// fcontext.base.decref();  // OFF-HEAP
block|}
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|process
argument_list|()
expr_stmt|;
comment|// We need to keep the fcontext open after processing is done (since we will be streaming in the response writer).
comment|// But if the connection is broken, we want to clean up.
comment|// fcontext.base.incref();  // OFF-HEAP
name|fcontext
operator|.
name|qcontext
operator|.
name|addCloseHook
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|setup
argument_list|()
expr_stmt|;
name|response
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"buckets"
argument_list|,
operator|new
name|Iterator
argument_list|()
block|{
name|boolean
name|retrieveNext
init|=
literal|true
decl_stmt|;
name|Object
name|val
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|retrieveNext
condition|)
block|{
name|val
operator|=
name|nextBucket
argument_list|()
expr_stmt|;
block|}
name|retrieveNext
operator|=
literal|false
expr_stmt|;
return|return
name|val
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|retrieveNext
condition|)
block|{
name|val
operator|=
name|nextBucket
argument_list|()
expr_stmt|;
block|}
name|retrieveNext
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
comment|// Last value, so clean up.  In the case that we are doing streaming facets within streaming facets,
comment|// the number of close hooks could grow very large, so we want to remove ourselves.
name|boolean
name|removed
init|=
name|fcontext
operator|.
name|qcontext
operator|.
name|removeCloseHook
argument_list|(
name|FacetFieldProcessorByEnumTermsStream
operator|.
name|this
argument_list|)
decl_stmt|;
assert|assert
name|removed
assert|;
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Error during facet streaming close"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|val
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|setup
specifier|private
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|countOnly
operator|=
name|freq
operator|.
name|facetStats
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
name|freq
operator|.
name|facetStats
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|instanceof
name|CountAgg
expr_stmt|;
name|hasSubFacets
operator|=
name|freq
operator|.
name|subFacets
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
name|bucketsToSkip
operator|=
name|freq
operator|.
name|offset
expr_stmt|;
name|createAccs
argument_list|(
operator|-
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Minimum term docFreq in order to use the filterCache for that term.
if|if
condition|(
name|freq
operator|.
name|cacheDf
operator|==
operator|-
literal|1
condition|)
block|{
comment|// -1 means never cache
name|minDfFilterCache
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|freq
operator|.
name|cacheDf
operator|==
literal|0
condition|)
block|{
comment|// default; compute as fraction of maxDoc
name|minDfFilterCache
operator|=
name|Math
operator|.
name|max
argument_list|(
name|fcontext
operator|.
name|searcher
operator|.
name|maxDoc
argument_list|()
operator|>>
literal|4
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// (minimum of 3 is for test coverage purposes)
block|}
else|else
block|{
name|minDfFilterCache
operator|=
name|freq
operator|.
name|cacheDf
expr_stmt|;
block|}
name|docs
operator|=
name|fcontext
operator|.
name|base
expr_stmt|;
name|fastForRandomSet
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|freq
operator|.
name|prefix
operator|!=
literal|null
condition|)
block|{
name|String
name|indexedPrefix
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toInternal
argument_list|(
name|freq
operator|.
name|prefix
argument_list|)
decl_stmt|;
name|startTermBytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|indexedPrefix
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getNumberType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|triePrefix
init|=
name|TrieField
operator|.
name|getMainValuePrefix
argument_list|(
name|sf
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|triePrefix
operator|!=
literal|null
condition|)
block|{
name|startTermBytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|triePrefix
argument_list|)
expr_stmt|;
block|}
block|}
name|Fields
name|fields
init|=
name|fcontext
operator|.
name|searcher
operator|.
name|getSlowAtomicReader
argument_list|()
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
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|termsEnum
operator|=
literal|null
expr_stmt|;
name|deState
operator|=
literal|null
expr_stmt|;
name|term
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
comment|// TODO: OPT: if seek(ord) is supported for this termsEnum, then we could use it for
comment|// facet.offset when sorting by index order.
if|if
condition|(
name|startTermBytes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|startTermBytes
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
name|termsEnum
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// position termsEnum on first term
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leafList
init|=
name|fcontext
operator|.
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|leaves
operator|=
name|leafList
operator|.
name|toArray
argument_list|(
operator|new
name|LeafReaderContext
index|[
name|leafList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|nextBucket
specifier|private
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|nextBucket
parameter_list|()
block|{
try|try
block|{
return|return
name|_nextBucket
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Error during facet streaming"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|_nextBucket
specifier|private
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|_nextBucket
parameter_list|()
throws|throws
name|IOException
block|{
name|DocSet
name|termSet
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|startTermBytes
operator|!=
literal|null
operator|&&
operator|!
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|startTermBytes
argument_list|)
condition|)
block|{
break|break;
block|}
name|int
name|df
init|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|df
operator|<
name|effectiveMincount
condition|)
block|{
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|termSet
operator|!=
literal|null
condition|)
block|{
comment|// termSet.decref(); // OFF-HEAP
name|termSet
operator|=
literal|null
expr_stmt|;
block|}
name|int
name|c
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|hasSubFacets
operator|||
name|df
operator|>=
name|minDfFilterCache
condition|)
block|{
comment|// use the filter cache
if|if
condition|(
name|deState
operator|==
literal|null
condition|)
block|{
name|deState
operator|=
operator|new
name|SolrIndexSearcher
operator|.
name|DocsEnumState
argument_list|()
expr_stmt|;
name|deState
operator|.
name|fieldName
operator|=
name|sf
operator|.
name|getName
argument_list|()
expr_stmt|;
name|deState
operator|.
name|liveDocs
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getSlowAtomicReader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|deState
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
name|deState
operator|.
name|postingsEnum
operator|=
name|postingsEnum
expr_stmt|;
name|deState
operator|.
name|minSetSizeCached
operator|=
name|minDfFilterCache
expr_stmt|;
block|}
if|if
condition|(
name|hasSubFacets
operator|||
operator|!
name|countOnly
condition|)
block|{
name|DocSet
name|termsAll
init|=
name|fcontext
operator|.
name|searcher
operator|.
name|getDocSet
argument_list|(
name|deState
argument_list|)
decl_stmt|;
name|termSet
operator|=
name|docs
operator|.
name|intersection
argument_list|(
name|termsAll
argument_list|)
expr_stmt|;
comment|// termsAll.decref(); // OFF-HEAP
name|c
operator|=
name|termSet
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|c
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|numDocs
argument_list|(
name|docs
argument_list|,
name|deState
argument_list|)
expr_stmt|;
block|}
name|postingsEnum
operator|=
name|deState
operator|.
name|postingsEnum
expr_stmt|;
name|resetStats
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|countOnly
condition|)
block|{
name|collect
argument_list|(
name|termSet
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// We don't need the docset here (meaning no sub-facets).
comment|// if countOnly, then we are calculating some other stats...
name|resetStats
argument_list|()
expr_stmt|;
comment|// lazy convert to fastForRandomSet
if|if
condition|(
name|fastForRandomSet
operator|==
literal|null
condition|)
block|{
name|fastForRandomSet
operator|=
name|docs
expr_stmt|;
if|if
condition|(
name|docs
operator|instanceof
name|SortedIntDocSet
condition|)
block|{
comment|// OFF-HEAP todo: also check for native version
name|SortedIntDocSet
name|sset
init|=
operator|(
name|SortedIntDocSet
operator|)
name|docs
decl_stmt|;
name|fastForRandomSet
operator|=
operator|new
name|HashDocSet
argument_list|(
name|sset
operator|.
name|getDocs
argument_list|()
argument_list|,
literal|0
argument_list|,
name|sset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// iterate over TermDocs to calculate the intersection
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
if|if
condition|(
name|postingsEnum
operator|instanceof
name|MultiPostingsEnum
condition|)
block|{
name|MultiPostingsEnum
operator|.
name|EnumWithSlice
index|[]
name|subs
init|=
operator|(
operator|(
name|MultiPostingsEnum
operator|)
name|postingsEnum
operator|)
operator|.
name|getSubs
argument_list|()
decl_stmt|;
name|int
name|numSubs
init|=
operator|(
operator|(
name|MultiPostingsEnum
operator|)
name|postingsEnum
operator|)
operator|.
name|getNumSubs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|subindex
init|=
literal|0
init|;
name|subindex
operator|<
name|numSubs
condition|;
name|subindex
operator|++
control|)
block|{
name|MultiPostingsEnum
operator|.
name|EnumWithSlice
name|sub
init|=
name|subs
index|[
name|subindex
index|]
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|postingsEnum
operator|==
literal|null
condition|)
continue|continue;
name|int
name|base
init|=
name|sub
operator|.
name|slice
operator|.
name|start
decl_stmt|;
name|int
name|docid
decl_stmt|;
if|if
condition|(
name|countOnly
condition|)
block|{
while|while
condition|(
operator|(
name|docid
operator|=
name|sub
operator|.
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|fastForRandomSet
operator|.
name|exists
argument_list|(
name|docid
operator|+
name|base
argument_list|)
condition|)
name|c
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|setNextReader
argument_list|(
name|leaves
index|[
name|sub
operator|.
name|slice
operator|.
name|readerIndex
index|]
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|docid
operator|=
name|sub
operator|.
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|fastForRandomSet
operator|.
name|exists
argument_list|(
name|docid
operator|+
name|base
argument_list|)
condition|)
block|{
name|c
operator|++
expr_stmt|;
name|collect
argument_list|(
name|docid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
name|int
name|docid
decl_stmt|;
if|if
condition|(
name|countOnly
condition|)
block|{
while|while
condition|(
operator|(
name|docid
operator|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|fastForRandomSet
operator|.
name|exists
argument_list|(
name|docid
argument_list|)
condition|)
name|c
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|setNextReader
argument_list|(
name|leaves
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|docid
operator|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|fastForRandomSet
operator|.
name|exists
argument_list|(
name|docid
argument_list|)
condition|)
block|{
name|c
operator|++
expr_stmt|;
name|collect
argument_list|(
name|docid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|c
operator|<
name|effectiveMincount
condition|)
block|{
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
comment|// handle offset and limit
if|if
condition|(
name|bucketsToSkip
operator|>
literal|0
condition|)
block|{
name|bucketsToSkip
operator|--
expr_stmt|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|freq
operator|.
name|limit
operator|>=
literal|0
operator|&&
operator|++
name|bucketsReturned
operator|>
name|freq
operator|.
name|limit
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// set count in case other stats depend on it
name|countAcc
operator|.
name|incrementCount
argument_list|(
literal|0
argument_list|,
name|c
argument_list|)
expr_stmt|;
comment|// OK, we have a good bucket to return... first get bucket value before moving to next term
name|Object
name|bucketVal
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toObject
argument_list|(
name|sf
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|TermQuery
name|bucketQuery
init|=
name|hasSubFacets
condition|?
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|freq
operator|.
name|field
argument_list|,
name|term
argument_list|)
argument_list|)
else|:
literal|null
decl_stmt|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|bucket
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|bucket
operator|.
name|add
argument_list|(
literal|"val"
argument_list|,
name|bucketVal
argument_list|)
expr_stmt|;
name|addStats
argument_list|(
name|bucket
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasSubFacets
condition|)
block|{
name|processSubs
argument_list|(
name|bucket
argument_list|,
name|bucketQuery
argument_list|,
name|termSet
argument_list|)
expr_stmt|;
block|}
comment|// TODO... termSet needs to stick around for streaming sub-facets?
return|return
name|bucket
return|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|termSet
operator|!=
literal|null
condition|)
block|{
comment|// termSet.decref();  // OFF-HEAP
name|termSet
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// end of the iteration
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

