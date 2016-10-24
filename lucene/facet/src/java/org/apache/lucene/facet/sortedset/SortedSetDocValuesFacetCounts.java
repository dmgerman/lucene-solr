begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.sortedset
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|facet
operator|.
name|FacetResult
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
name|facet
operator|.
name|Facets
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
name|facet
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|FacetsCollector
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
name|facet
operator|.
name|FacetsConfig
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
name|facet
operator|.
name|LabelAndValue
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
name|facet
operator|.
name|TopOrdAndIntQueue
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
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesReaderState
operator|.
name|OrdRange
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
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
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
name|ReaderUtil
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
name|LongValues
import|;
end_import

begin_comment
comment|/** Compute facets counts from previously  *  indexed {@link SortedSetDocValuesFacetField},  *  without require a separate taxonomy index.  Faceting is  *  a bit slower (~25%), and there is added cost on every  *  {@link IndexReader} open to create a new {@link  *  SortedSetDocValuesReaderState}.  Furthermore, this does  *  not support hierarchical facets; only flat (dimension +  *  label) facets, but it uses quite a bit less RAM to do  *  so.  *  *<p><b>NOTE</b>: this class should be instantiated and  *  then used from a single thread, because it holds a  *  thread-private instance of {@link SortedSetDocValues}.  *   *<p><b>NOTE:</b>: tie-break is by unicode sort order  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|SortedSetDocValuesFacetCounts
specifier|public
class|class
name|SortedSetDocValuesFacetCounts
extends|extends
name|Facets
block|{
DECL|field|state
specifier|final
name|SortedSetDocValuesReaderState
name|state
decl_stmt|;
DECL|field|dv
specifier|final
name|SortedSetDocValues
name|dv
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|counts
specifier|final
name|int
index|[]
name|counts
decl_stmt|;
comment|/** Sparse faceting: returns any dimension that had any    *  hits, topCount labels per dimension. */
DECL|method|SortedSetDocValuesFacetCounts
specifier|public
name|SortedSetDocValuesFacetCounts
parameter_list|(
name|SortedSetDocValuesReaderState
name|state
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|state
operator|.
name|getField
argument_list|()
expr_stmt|;
name|dv
operator|=
name|state
operator|.
name|getDocValues
argument_list|()
expr_stmt|;
name|counts
operator|=
operator|new
name|int
index|[
name|state
operator|.
name|getSize
argument_list|()
index|]
expr_stmt|;
comment|//System.out.println("field=" + field);
name|count
argument_list|(
name|hits
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTopChildren
specifier|public
name|FacetResult
name|getTopChildren
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topN
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"topN must be> 0 (got: "
operator|+
name|topN
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|path
operator|.
name|length
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path should be 0 length"
argument_list|)
throw|;
block|}
name|OrdRange
name|ordRange
init|=
name|state
operator|.
name|getOrdRange
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordRange
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dimension \""
operator|+
name|dim
operator|+
literal|"\" was not indexed"
argument_list|)
throw|;
block|}
return|return
name|getDim
argument_list|(
name|dim
argument_list|,
name|ordRange
argument_list|,
name|topN
argument_list|)
return|;
block|}
DECL|method|getDim
specifier|private
specifier|final
name|FacetResult
name|getDim
parameter_list|(
name|String
name|dim
parameter_list|,
name|OrdRange
name|ordRange
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
name|TopOrdAndIntQueue
name|q
init|=
literal|null
decl_stmt|;
name|int
name|bottomCount
init|=
literal|0
decl_stmt|;
name|int
name|dimCount
init|=
literal|0
decl_stmt|;
name|int
name|childCount
init|=
literal|0
decl_stmt|;
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
name|reuse
init|=
literal|null
decl_stmt|;
comment|//System.out.println("getDim : " + ordRange.start + " - " + ordRange.end);
for|for
control|(
name|int
name|ord
init|=
name|ordRange
operator|.
name|start
init|;
name|ord
operator|<=
name|ordRange
operator|.
name|end
condition|;
name|ord
operator|++
control|)
block|{
comment|//System.out.println("  ord=" + ord + " count=" + counts[ord]);
if|if
condition|(
name|counts
index|[
name|ord
index|]
operator|>
literal|0
condition|)
block|{
name|dimCount
operator|+=
name|counts
index|[
name|ord
index|]
expr_stmt|;
name|childCount
operator|++
expr_stmt|;
if|if
condition|(
name|counts
index|[
name|ord
index|]
operator|>
name|bottomCount
condition|)
block|{
if|if
condition|(
name|reuse
operator|==
literal|null
condition|)
block|{
name|reuse
operator|=
operator|new
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
argument_list|()
expr_stmt|;
block|}
name|reuse
operator|.
name|ord
operator|=
name|ord
expr_stmt|;
name|reuse
operator|.
name|value
operator|=
name|counts
index|[
name|ord
index|]
expr_stmt|;
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
comment|// Lazy init, so we don't create this for the
comment|// sparse case unnecessarily
name|q
operator|=
operator|new
name|TopOrdAndIntQueue
argument_list|(
name|topN
argument_list|)
expr_stmt|;
block|}
name|reuse
operator|=
name|q
operator|.
name|insertWithOverflow
argument_list|(
name|reuse
argument_list|)
expr_stmt|;
if|if
condition|(
name|q
operator|.
name|size
argument_list|()
operator|==
name|topN
condition|)
block|{
name|bottomCount
operator|=
name|q
operator|.
name|top
argument_list|()
operator|.
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|LabelAndValue
index|[]
name|labelValues
init|=
operator|new
name|LabelAndValue
index|[
name|q
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|labelValues
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
name|ordAndValue
init|=
name|q
operator|.
name|pop
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|term
init|=
name|dv
operator|.
name|lookupOrd
argument_list|(
name|ordAndValue
operator|.
name|ord
argument_list|)
decl_stmt|;
name|String
index|[]
name|parts
init|=
name|FacetsConfig
operator|.
name|stringToPath
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|labelValues
index|[
name|i
index|]
operator|=
operator|new
name|LabelAndValue
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|,
name|ordAndValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FacetResult
argument_list|(
name|dim
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|dimCount
argument_list|,
name|labelValues
argument_list|,
name|childCount
argument_list|)
return|;
block|}
comment|/** Does all the "real work" of tallying up the counts. */
DECL|method|count
specifier|private
specifier|final
name|void
name|count
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("ssdv count");
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordinalMap
decl_stmt|;
comment|// TODO: is this right?  really, we need a way to
comment|// verify that this ordinalMap "matches" the leaves in
comment|// matchingDocs...
if|if
condition|(
name|dv
operator|instanceof
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
operator|&&
name|matchingDocs
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|ordinalMap
operator|=
operator|(
operator|(
name|MultiSortedSetDocValues
operator|)
name|dv
operator|)
operator|.
name|mapping
expr_stmt|;
block|}
else|else
block|{
name|ordinalMap
operator|=
literal|null
expr_stmt|;
block|}
name|IndexReader
name|origReader
init|=
name|state
operator|.
name|getOrigReader
argument_list|()
decl_stmt|;
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|LeafReader
name|reader
init|=
name|hits
operator|.
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|//System.out.println("  reader=" + reader);
comment|// LUCENE-5090: make sure the provided reader context "matches"
comment|// the top-level reader passed to the
comment|// SortedSetDocValuesReaderState, else cryptic
comment|// AIOOBE can happen:
if|if
condition|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|hits
operator|.
name|context
argument_list|)
operator|.
name|reader
argument_list|()
operator|!=
name|origReader
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"the SortedSetDocValuesReaderState provided to this class does not match the reader being searched; you must create a new SortedSetDocValuesReaderState every time you open a new IndexReader"
argument_list|)
throw|;
block|}
name|SortedSetDocValues
name|segValues
init|=
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|segValues
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|DocIdSetIterator
name|docs
init|=
name|hits
operator|.
name|bits
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// TODO: yet another option is to count all segs
comment|// first, only in seg-ord space, and then do a
comment|// merge-sort-PQ in the end to only "resolve to
comment|// global" those seg ords that can compete, if we know
comment|// we just want top K?  ie, this is the same algo
comment|// that'd be used for merging facets across shards
comment|// (distributed faceting).  but this has much higher
comment|// temp ram req'ts (sum of number of ords across all
comment|// segs)
if|if
condition|(
name|ordinalMap
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|segOrd
init|=
name|hits
operator|.
name|context
operator|.
name|ord
decl_stmt|;
specifier|final
name|LongValues
name|ordMap
init|=
name|ordinalMap
operator|.
name|getGlobalOrds
argument_list|(
name|segOrd
argument_list|)
decl_stmt|;
name|int
name|numSegOrds
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|hits
operator|.
name|totalHits
operator|<
name|numSegOrds
operator|/
literal|10
condition|)
block|{
comment|//System.out.println("    remap as-we-go");
comment|// Remap every ord to global ord as we iterate:
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docs
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
comment|//System.out.println("    doc=" + doc);
if|if
condition|(
name|segValues
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|int
name|term
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
comment|//System.out.println("      segOrd=" + segOrd + " ord=" + term + " globalOrd=" + ordinalMap.getGlobalOrd(segOrd, term));
name|counts
index|[
operator|(
name|int
operator|)
name|ordMap
operator|.
name|get
argument_list|(
name|term
argument_list|)
index|]
operator|++
expr_stmt|;
name|term
operator|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|//System.out.println("    count in seg ord first");
comment|// First count in seg-ord space:
specifier|final
name|int
index|[]
name|segCounts
init|=
operator|new
name|int
index|[
name|numSegOrds
index|]
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docs
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
comment|//System.out.println("    doc=" + doc);
if|if
condition|(
name|segValues
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|int
name|term
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
comment|//System.out.println("      ord=" + term);
name|segCounts
index|[
name|term
index|]
operator|++
expr_stmt|;
name|term
operator|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Then, migrate to global ords:
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|numSegOrds
condition|;
name|ord
operator|++
control|)
block|{
name|int
name|count
init|=
name|segCounts
index|[
name|ord
index|]
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
comment|//System.out.println("    migrate segOrd=" + segOrd + " ord=" + ord + " globalOrd=" + ordinalMap.getGlobalOrd(segOrd, ord));
name|counts
index|[
operator|(
name|int
operator|)
name|ordMap
operator|.
name|get
argument_list|(
name|ord
argument_list|)
index|]
operator|+=
name|count
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// No ord mapping (e.g., single segment index):
comment|// just aggregate directly into counts:
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docs
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
name|segValues
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|int
name|term
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|counts
index|[
name|term
index|]
operator|++
expr_stmt|;
name|term
operator|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getSpecificValue
specifier|public
name|Number
name|getSpecificValue
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|path
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path must be length=1"
argument_list|)
throw|;
block|}
name|int
name|ord
init|=
operator|(
name|int
operator|)
name|dv
operator|.
name|lookupTerm
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|FacetsConfig
operator|.
name|pathToString
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|counts
index|[
name|ord
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getAllDims
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getAllDims
parameter_list|(
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|OrdRange
argument_list|>
name|ent
range|:
name|state
operator|.
name|getPrefixToOrdRange
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FacetResult
name|fr
init|=
name|getDim
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|,
name|topN
argument_list|)
decl_stmt|;
if|if
condition|(
name|fr
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|fr
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Sort by highest count:
name|Collections
operator|.
name|sort
argument_list|(
name|results
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResult
name|a
parameter_list|,
name|FacetResult
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|.
name|intValue
argument_list|()
operator|>
name|b
operator|.
name|value
operator|.
name|intValue
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|.
name|value
operator|.
name|intValue
argument_list|()
operator|>
name|a
operator|.
name|value
operator|.
name|intValue
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|a
operator|.
name|dim
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|dim
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
block|}
end_class

end_unit

