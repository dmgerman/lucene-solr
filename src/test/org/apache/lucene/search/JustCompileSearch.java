begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|FieldSelector
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
name|CorruptIndexException
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
name|TermPositions
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
name|PriorityQueue
import|;
end_import

begin_comment
comment|/**  * Holds all implementations of classes in the o.a.l.search package as a  * back-compatibility test. It does not run any tests per-se, however if   * someone adds a method to an interface or abstract method to an abstract  * class, one of the implementations here will fail to compile and so we know  * back-compat policy was violated.  */
end_comment

begin_class
DECL|class|JustCompileSearch
specifier|final
class|class
name|JustCompileSearch
block|{
DECL|field|UNSUPPORTED_MSG
specifier|private
specifier|static
specifier|final
name|String
name|UNSUPPORTED_MSG
init|=
literal|"unsupported: used for back-compat testing only !"
decl_stmt|;
DECL|class|JustCompileSearcher
specifier|static
specifier|final
class|class
name|JustCompileSearcher
extends|extends
name|Searcher
block|{
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|docFreqs
specifier|public
name|int
index|[]
name|docFreqs
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Collector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Collector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|setSimilarity
specifier|public
name|void
name|setSimilarity
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Collector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileCollector
specifier|static
specifier|final
class|class
name|JustCompileCollector
extends|extends
name|Collector
block|{
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileDocIdSet
specifier|static
specifier|final
class|class
name|JustCompileDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileDocIdSetIterator
specifier|static
specifier|final
class|class
name|JustCompileDocIdSetIterator
extends|extends
name|DocIdSetIterator
block|{
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileExtendedFieldCacheLongParser
specifier|static
specifier|final
class|class
name|JustCompileExtendedFieldCacheLongParser
implements|implements
name|FieldCache
operator|.
name|LongParser
block|{
DECL|method|parseLong
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|string
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileExtendedFieldCacheDoubleParser
specifier|static
specifier|final
class|class
name|JustCompileExtendedFieldCacheDoubleParser
implements|implements
name|FieldCache
operator|.
name|DoubleParser
block|{
DECL|method|parseDouble
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|string
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFieldComparator
specifier|static
specifier|final
class|class
name|JustCompileFieldComparator
extends|extends
name|FieldComparator
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|value
specifier|public
name|Comparable
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFieldComparatorSource
specifier|static
specifier|final
class|class
name|JustCompileFieldComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|method|newComparator
specifier|public
name|FieldComparator
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFilter
specifier|static
specifier|final
class|class
name|JustCompileFilter
extends|extends
name|Filter
block|{
comment|// Filter is just an abstract class with no abstract methods. However it is
comment|// still added here in case someone will add abstract methods in the future.
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|JustCompileFilteredDocIdSet
specifier|static
specifier|final
class|class
name|JustCompileFilteredDocIdSet
extends|extends
name|FilteredDocIdSet
block|{
DECL|method|JustCompileFilteredDocIdSet
specifier|public
name|JustCompileFilteredDocIdSet
parameter_list|(
name|DocIdSet
name|innerSet
parameter_list|)
block|{
name|super
argument_list|(
name|innerSet
argument_list|)
expr_stmt|;
block|}
DECL|method|match
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|docid
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFilteredDocIdSetIterator
specifier|static
specifier|final
class|class
name|JustCompileFilteredDocIdSetIterator
extends|extends
name|FilteredDocIdSetIterator
block|{
DECL|method|JustCompileFilteredDocIdSetIterator
specifier|public
name|JustCompileFilteredDocIdSetIterator
parameter_list|(
name|DocIdSetIterator
name|innerIter
parameter_list|)
block|{
name|super
argument_list|(
name|innerIter
argument_list|)
expr_stmt|;
block|}
DECL|method|match
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFilteredTermEnum
specifier|static
specifier|final
class|class
name|JustCompileFilteredTermEnum
extends|extends
name|FilteredTermEnum
block|{
DECL|method|difference
specifier|public
name|float
name|difference
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|endEnum
specifier|protected
name|boolean
name|endEnum
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|termCompare
specifier|protected
name|boolean
name|termCompare
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompilePhraseScorer
specifier|static
specifier|final
class|class
name|JustCompilePhraseScorer
extends|extends
name|PhraseScorer
block|{
DECL|method|JustCompilePhraseScorer
name|JustCompilePhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermPositions
index|[]
name|tps
parameter_list|,
name|int
index|[]
name|offsets
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|tps
argument_list|,
name|offsets
argument_list|,
name|similarity
argument_list|,
name|norms
argument_list|)
expr_stmt|;
block|}
DECL|method|phraseFreq
specifier|protected
name|float
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileQuery
specifier|static
specifier|final
class|class
name|JustCompileQuery
extends|extends
name|Query
block|{
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileScorer
specifier|static
specifier|final
class|class
name|JustCompileScorer
extends|extends
name|Scorer
block|{
DECL|method|JustCompileScorer
specifier|protected
name|JustCompileScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileSimilarity
specifier|static
specifier|final
class|class
name|JustCompileSimilarity
extends|extends
name|Similarity
block|{
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|numTokens
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileSpanFilter
specifier|static
specifier|final
class|class
name|JustCompileSpanFilter
extends|extends
name|SpanFilter
block|{
DECL|method|bitSpans
specifier|public
name|SpanFilterResult
name|bitSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|JustCompileTopDocsCollector
specifier|static
specifier|final
class|class
name|JustCompileTopDocsCollector
extends|extends
name|TopDocsCollector
block|{
DECL|method|JustCompileTopDocsCollector
specifier|protected
name|JustCompileTopDocsCollector
parameter_list|(
name|PriorityQueue
name|pq
parameter_list|)
block|{
name|super
argument_list|(
name|pq
argument_list|)
expr_stmt|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileWeight
specifier|static
specifier|final
class|class
name|JustCompileWeight
extends|extends
name|Weight
block|{
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

