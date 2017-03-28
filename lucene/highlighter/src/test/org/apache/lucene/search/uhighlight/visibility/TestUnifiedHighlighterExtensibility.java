begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight.visibility
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
operator|.
name|visibility
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
name|text
operator|.
name|BreakIterator
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
name|IndexSearcher
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
name|Query
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
name|spans
operator|.
name|SpanQuery
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
name|uhighlight
operator|.
name|FieldHighlighter
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
name|uhighlight
operator|.
name|FieldOffsetStrategy
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
name|uhighlight
operator|.
name|OffsetsEnum
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
name|uhighlight
operator|.
name|Passage
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
name|uhighlight
operator|.
name|PassageFormatter
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
name|uhighlight
operator|.
name|PassageScorer
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
name|uhighlight
operator|.
name|PhraseHelper
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
name|uhighlight
operator|.
name|SplittingBreakIterator
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
name|uhighlight
operator|.
name|UnifiedHighlighter
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
name|automaton
operator|.
name|CharacterRunAutomaton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Helps us be aware of visibility/extensibility concerns.  */
end_comment

begin_class
DECL|class|TestUnifiedHighlighterExtensibility
specifier|public
class|class
name|TestUnifiedHighlighterExtensibility
extends|extends
name|LuceneTestCase
block|{
comment|/**    * This test is for maintaining the extensibility of the FieldOffsetStrategy    * for customizations out of package.    */
annotation|@
name|Test
DECL|method|testFieldOffsetStrategyExtensibility
specifier|public
name|void
name|testFieldOffsetStrategyExtensibility
parameter_list|()
block|{
specifier|final
name|UnifiedHighlighter
operator|.
name|OffsetSource
name|offsetSource
init|=
name|UnifiedHighlighter
operator|.
name|OffsetSource
operator|.
name|NONE_NEEDED
decl_stmt|;
name|FieldOffsetStrategy
name|strategy
init|=
operator|new
name|FieldOffsetStrategy
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BytesRef
index|[
literal|0
index|]
argument_list|,
name|PhraseHelper
operator|.
name|NONE
argument_list|,
operator|new
name|CharacterRunAutomaton
index|[
literal|0
index|]
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|UnifiedHighlighter
operator|.
name|OffsetSource
name|getOffsetSource
parameter_list|()
block|{
return|return
name|offsetSource
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|getOffsetsEnums
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|createOffsetsEnumsFromReader
parameter_list|(
name|LeafReader
name|leafReader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|createOffsetsEnumsFromReader
argument_list|(
name|leafReader
argument_list|,
name|doc
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|offsetSource
argument_list|,
name|strategy
operator|.
name|getOffsetSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test is for maintaining the extensibility of the UnifiedHighlighter    * for customizations out of package.    */
annotation|@
name|Test
DECL|method|testUnifiedHighlighterExtensibility
specifier|public
name|void
name|testUnifiedHighlighterExtensibility
parameter_list|()
block|{
specifier|final
name|int
name|maxLength
init|=
literal|1000
decl_stmt|;
name|UnifiedHighlighter
name|uh
init|=
operator|new
name|UnifiedHighlighter
argument_list|(
literal|null
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
index|[]
argument_list|>
name|highlightFieldsAsObjects
parameter_list|(
name|String
index|[]
name|fieldsIn
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
index|[]
name|docIdsIn
parameter_list|,
name|int
index|[]
name|maxPassagesIn
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|highlightFieldsAsObjects
argument_list|(
name|fieldsIn
argument_list|,
name|query
argument_list|,
name|docIdsIn
argument_list|,
name|maxPassagesIn
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|OffsetSource
name|getOffsetSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|super
operator|.
name|getOffsetSource
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|BreakIterator
name|getBreakIterator
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|super
operator|.
name|getBreakIterator
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PassageScorer
name|getScorer
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|super
operator|.
name|getScorer
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PassageFormatter
name|getFormatter
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|super
operator|.
name|getFormatter
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Analyzer
name|getIndexAnalyzer
parameter_list|()
block|{
return|return
name|super
operator|.
name|getIndexAnalyzer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexSearcher
name|getIndexSearcher
parameter_list|()
block|{
return|return
name|super
operator|.
name|getIndexSearcher
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|getMaxNoHighlightPassages
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|super
operator|.
name|getMaxNoHighlightPassages
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Boolean
name|requiresRewrite
parameter_list|(
name|SpanQuery
name|spanQuery
parameter_list|)
block|{
return|return
name|super
operator|.
name|requiresRewrite
argument_list|(
name|spanQuery
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|LimitedStoredFieldVisitor
name|newLimitedStoredFieldsVisitor
parameter_list|(
name|String
index|[]
name|fields
parameter_list|)
block|{
return|return
name|super
operator|.
name|newLimitedStoredFieldsVisitor
argument_list|(
name|fields
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|CharSequence
index|[]
argument_list|>
name|loadFieldValues
parameter_list|(
name|String
index|[]
name|fields
parameter_list|,
name|DocIdSetIterator
name|docIter
parameter_list|,
name|int
name|cacheCharsThreshold
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|loadFieldValues
argument_list|(
name|fields
argument_list|,
name|docIter
argument_list|,
name|cacheCharsThreshold
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|FieldHighlighter
name|getFieldHighlighter
parameter_list|(
name|String
name|field
parameter_list|,
name|Query
name|query
parameter_list|,
name|Set
argument_list|<
name|Term
argument_list|>
name|allTerms
parameter_list|,
name|int
name|maxPassages
parameter_list|)
block|{
comment|// THIS IS A COPY of the superclass impl; but use CustomFieldHighlighter
name|BytesRef
index|[]
name|terms
init|=
name|filterExtractedTerms
argument_list|(
name|getFieldMatcher
argument_list|(
name|field
argument_list|)
argument_list|,
name|allTerms
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|HighlightFlag
argument_list|>
name|highlightFlags
init|=
name|getFlags
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|PhraseHelper
name|phraseHelper
init|=
name|getPhraseHelper
argument_list|(
name|field
argument_list|,
name|query
argument_list|,
name|highlightFlags
argument_list|)
decl_stmt|;
name|CharacterRunAutomaton
index|[]
name|automata
init|=
name|getAutomata
argument_list|(
name|field
argument_list|,
name|query
argument_list|,
name|highlightFlags
argument_list|)
decl_stmt|;
name|OffsetSource
name|offsetSource
init|=
name|getOptimizedOffsetSource
argument_list|(
name|field
argument_list|,
name|terms
argument_list|,
name|phraseHelper
argument_list|,
name|automata
argument_list|)
decl_stmt|;
return|return
operator|new
name|CustomFieldHighlighter
argument_list|(
name|field
argument_list|,
name|getOffsetStrategy
argument_list|(
name|offsetSource
argument_list|,
name|field
argument_list|,
name|terms
argument_list|,
name|phraseHelper
argument_list|,
name|automata
argument_list|,
name|highlightFlags
argument_list|)
argument_list|,
operator|new
name|SplittingBreakIterator
argument_list|(
name|getBreakIterator
argument_list|(
name|field
argument_list|)
argument_list|,
name|UnifiedHighlighter
operator|.
name|MULTIVAL_SEP_CHAR
argument_list|)
argument_list|,
name|getScorer
argument_list|(
name|field
argument_list|)
argument_list|,
name|maxPassages
argument_list|,
name|getMaxNoHighlightPassages
argument_list|(
name|field
argument_list|)
argument_list|,
name|getFormatter
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|FieldOffsetStrategy
name|getOffsetStrategy
parameter_list|(
name|OffsetSource
name|offsetSource
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
index|[]
name|terms
parameter_list|,
name|PhraseHelper
name|phraseHelper
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|automata
parameter_list|,
name|Set
argument_list|<
name|HighlightFlag
argument_list|>
name|highlightFlags
parameter_list|)
block|{
return|return
name|super
operator|.
name|getOffsetStrategy
argument_list|(
name|offsetSource
argument_list|,
name|field
argument_list|,
name|terms
argument_list|,
name|phraseHelper
argument_list|,
name|automata
argument_list|,
name|highlightFlags
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxLength
parameter_list|()
block|{
return|return
name|maxLength
return|;
block|}
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|uh
operator|.
name|getMaxLength
argument_list|()
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPassageFormatterExtensibility
specifier|public
name|void
name|testPassageFormatterExtensibility
parameter_list|()
block|{
specifier|final
name|Object
name|formattedResponse
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
name|PassageFormatter
name|formatter
init|=
operator|new
name|PassageFormatter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|format
parameter_list|(
name|Passage
index|[]
name|passages
parameter_list|,
name|String
name|content
parameter_list|)
block|{
return|return
name|formattedResponse
return|;
block|}
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|formattedResponse
argument_list|,
name|formatter
operator|.
name|format
argument_list|(
operator|new
name|Passage
index|[
literal|0
index|]
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFieldHiglighterExtensibility
specifier|public
name|void
name|testFieldHiglighterExtensibility
parameter_list|()
block|{
specifier|final
name|String
name|fieldName
init|=
literal|"fieldName"
decl_stmt|;
name|FieldHighlighter
name|fieldHighlighter
init|=
operator|new
name|FieldHighlighter
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Passage
index|[]
name|highlightOffsetsEnums
parameter_list|(
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|offsetsEnums
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|highlightOffsetsEnums
argument_list|(
name|offsetsEnums
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|fieldHighlighter
operator|.
name|getField
argument_list|()
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
comment|/** Tests maintaining extensibility/visibility of {@link org.apache.lucene.search.uhighlight.FieldHighlighter} out of package. */
DECL|class|CustomFieldHighlighter
specifier|private
specifier|static
class|class
name|CustomFieldHighlighter
extends|extends
name|FieldHighlighter
block|{
DECL|method|CustomFieldHighlighter
name|CustomFieldHighlighter
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldOffsetStrategy
name|fieldOffsetStrategy
parameter_list|,
name|BreakIterator
name|breakIterator
parameter_list|,
name|PassageScorer
name|passageScorer
parameter_list|,
name|int
name|maxPassages
parameter_list|,
name|int
name|maxNoHighlightPassages
parameter_list|,
name|PassageFormatter
name|passageFormatter
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|fieldOffsetStrategy
argument_list|,
name|breakIterator
argument_list|,
name|passageScorer
argument_list|,
name|maxPassages
argument_list|,
name|maxNoHighlightPassages
argument_list|,
name|passageFormatter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|highlightFieldForDoc
specifier|public
name|Object
name|highlightFieldForDoc
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|highlightFieldForDoc
argument_list|(
name|reader
argument_list|,
name|docId
argument_list|,
name|content
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|highlightOffsetsEnums
specifier|protected
name|Passage
index|[]
name|highlightOffsetsEnums
parameter_list|(
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|offsetsEnums
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TEST OffsetsEnums& Passage visibility
comment|// this code never runs; just for compilation
name|OffsetsEnum
name|oe
init|=
operator|new
name|OffsetsEnum
argument_list|(
literal|null
argument_list|,
name|EMPTY
argument_list|)
decl_stmt|;
name|oe
operator|.
name|getTerm
argument_list|()
expr_stmt|;
name|oe
operator|.
name|getPostingsEnum
argument_list|()
expr_stmt|;
name|oe
operator|.
name|freq
argument_list|()
expr_stmt|;
name|oe
operator|.
name|hasMorePositions
argument_list|()
expr_stmt|;
name|oe
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|oe
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|oe
operator|.
name|endOffset
argument_list|()
expr_stmt|;
name|oe
operator|.
name|getWeight
argument_list|()
expr_stmt|;
name|oe
operator|.
name|setWeight
argument_list|(
literal|2f
argument_list|)
expr_stmt|;
name|Passage
name|p
init|=
operator|new
name|Passage
argument_list|()
decl_stmt|;
name|p
operator|.
name|setStartOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|p
operator|.
name|setEndOffset
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|p
operator|.
name|setScore
argument_list|(
literal|1f
argument_list|)
expr_stmt|;
name|p
operator|.
name|addMatch
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|reset
argument_list|()
expr_stmt|;
name|p
operator|.
name|sort
argument_list|()
expr_stmt|;
comment|//... getters are all exposed; custom PassageFormatter impls uses them
return|return
name|super
operator|.
name|highlightOffsetsEnums
argument_list|(
name|offsetsEnums
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

