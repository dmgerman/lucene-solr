begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
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
name|index
operator|.
name|DocValues
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
name|SortedSetDocValues
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
name|Collector
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
name|SimpleCollector
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
name|BitSet
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
name|BytesRefHash
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
name|solr
operator|.
name|search
operator|.
name|BitDocSet
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

begin_comment
comment|/**  * A graph hit collector.  This accumulates the edges for a given graph traversal.  * On each collect method, the collector skips edge extraction for nodes that it has  * already traversed.  * @lucene.internal  */
end_comment

begin_class
DECL|class|GraphTermsCollector
class|class
name|GraphTermsCollector
extends|extends
name|SimpleCollector
implements|implements
name|Collector
block|{
comment|// the field to collect edge ids from
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
comment|// all the collected terms
DECL|field|collectorTerms
specifier|private
name|BytesRefHash
name|collectorTerms
decl_stmt|;
DECL|field|docTermOrds
specifier|private
name|SortedSetDocValues
name|docTermOrds
decl_stmt|;
comment|// the result set that is being collected.
DECL|field|currentResult
specifier|private
name|Bits
name|currentResult
decl_stmt|;
comment|// known leaf nodes
DECL|field|leafNodes
specifier|private
name|DocSet
name|leafNodes
decl_stmt|;
comment|// number of hits discovered at this level.
DECL|field|numHits
name|int
name|numHits
init|=
literal|0
decl_stmt|;
DECL|field|bits
name|BitSet
name|bits
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|base
name|int
name|base
decl_stmt|;
DECL|field|baseInParent
name|int
name|baseInParent
decl_stmt|;
comment|// if we care to track this.
DECL|field|hasCycles
name|boolean
name|hasCycles
init|=
literal|false
decl_stmt|;
DECL|method|GraphTermsCollector
name|GraphTermsCollector
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|Bits
name|currentResult
parameter_list|,
name|DocSet
name|leafNodes
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|collectorTerms
operator|=
operator|new
name|BytesRefHash
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentResult
operator|=
name|currentResult
expr_stmt|;
name|this
operator|.
name|leafNodes
operator|=
name|leafNodes
expr_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
comment|// create a bitset at the start that will hold the graph traversal result set
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|+=
name|base
expr_stmt|;
if|if
condition|(
name|currentResult
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|// cycle detected / already been here.
comment|// knowing if your graph had a cycle might be useful and it's lightweight to implement here.
name|hasCycles
operator|=
literal|true
expr_stmt|;
return|return;
block|}
comment|// collect the docs
name|addDocToResult
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// Optimization to not look up edges for a document that is a leaf node
if|if
condition|(
operator|!
name|leafNodes
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|addEdgeIdsToResult
argument_list|(
name|doc
operator|-
name|base
argument_list|)
expr_stmt|;
block|}
comment|// Note: tracking links in for each result would be a huge memory hog... so not implementing at this time.
block|}
DECL|method|addEdgeIdsToResult
specifier|private
name|void
name|addEdgeIdsToResult
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// set the doc to pull the edges ids for.
if|if
condition|(
name|doc
operator|>
name|docTermOrds
operator|.
name|docID
argument_list|()
condition|)
block|{
name|docTermOrds
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|==
name|docTermOrds
operator|.
name|docID
argument_list|()
condition|)
block|{
name|BytesRef
name|edgeValue
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|docTermOrds
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
comment|// TODO: handle non string type fields.
name|edgeValue
operator|=
name|docTermOrds
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
expr_stmt|;
comment|// add the edge id to the collector terms.
name|collectorTerms
operator|.
name|add
argument_list|(
name|edgeValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addDocToResult
specifier|private
name|void
name|addDocToResult
parameter_list|(
name|int
name|docWithBase
parameter_list|)
block|{
comment|// this document is part of the traversal. mark it in our bitmap.
name|bits
operator|.
name|set
argument_list|(
name|docWithBase
argument_list|)
expr_stmt|;
comment|// increment the hit count so we know how many docs we traversed this time.
name|numHits
operator|++
expr_stmt|;
block|}
DECL|method|getDocSet
specifier|public
name|BitDocSet
name|getDocSet
parameter_list|()
block|{
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
comment|// TODO: this shouldn't happen
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BitDocSet
argument_list|(
operator|(
name|FixedBitSet
operator|)
name|bits
argument_list|,
name|numHits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|public
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Grab the updated doc values.
name|docTermOrds
operator|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|base
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
name|baseInParent
operator|=
name|context
operator|.
name|docBaseInParent
expr_stmt|;
block|}
DECL|method|getCollectorTerms
specifier|public
name|BytesRefHash
name|getCollectorTerms
parameter_list|()
block|{
return|return
name|collectorTerms
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

