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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|HashSet
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
name|Random
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
operator|.
name|Store
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
name|NumericDocValuesField
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
name|DirectoryReader
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
name|IndexWriterConfig
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
name|LogMergePolicy
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
name|NumericDocValues
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
name|RandomIndexWriter
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
name|SlowCompositeReaderWrapper
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
name|TieredMergePolicy
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
name|Sort
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
name|SortField
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
name|TestUtil
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
name|generators
operator|.
name|RandomPicks
import|;
end_import

begin_class
DECL|class|TestSortingMergePolicy
specifier|public
class|class
name|TestSortingMergePolicy
extends|extends
name|BaseMergePolicyTestCase
block|{
DECL|field|terms
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|terms
decl_stmt|;
DECL|field|dir1
DECL|field|dir2
specifier|private
name|Directory
name|dir1
decl_stmt|,
name|dir2
decl_stmt|;
DECL|field|sort
specifier|private
name|Sort
name|sort
decl_stmt|;
DECL|field|reversedSort
specifier|private
name|boolean
name|reversedSort
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|sortedReader
specifier|private
name|IndexReader
name|sortedReader
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
specifier|final
name|Boolean
name|reverse
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|Boolean
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|)
decl_stmt|;
specifier|final
name|SortField
name|sort_field
init|=
operator|(
name|reverse
operator|==
literal|null
condition|?
operator|new
name|SortField
argument_list|(
literal|"ndv"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
else|:
operator|new
name|SortField
argument_list|(
literal|"ndv"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
name|reverse
operator|.
name|booleanValue
argument_list|()
argument_list|)
operator|)
decl_stmt|;
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|sort_field
argument_list|)
expr_stmt|;
name|reversedSort
operator|=
operator|(
literal|null
operator|!=
name|reverse
operator|&&
name|reverse
operator|.
name|booleanValue
argument_list|()
operator|)
expr_stmt|;
name|createRandomIndexes
argument_list|()
expr_stmt|;
block|}
DECL|method|randomDocument
specifier|private
name|Document
name|randomDocument
parameter_list|()
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"ndv"
argument_list|,
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"s"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|testForceMergeNotNeeded
specifier|public
name|void
name|testForceMergeNotNeeded
parameter_list|()
throws|throws
name|IOException
block|{
comment|// This is a no-op until we figure out why the (super class) test fails.
comment|// https://issues.apache.org/jira/browse/LUCENE-7008
block|}
DECL|method|mergePolicy
specifier|public
name|MergePolicy
name|mergePolicy
parameter_list|()
block|{
return|return
name|newSortingMergePolicy
argument_list|(
name|sort
argument_list|)
return|;
block|}
DECL|method|newSortingMergePolicy
specifier|public
specifier|static
name|SortingMergePolicy
name|newSortingMergePolicy
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
comment|// usually create a MP with a low merge factor so that many merges happen
name|MergePolicy
name|mp
decl_stmt|;
name|int
name|thingToDo
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|0
condition|)
block|{
name|TieredMergePolicy
name|tmp
init|=
name|newTieredMergePolicy
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numSegs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|setSegmentsPerTier
argument_list|(
name|numSegs
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxMergeAtOnce
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
name|numSegs
argument_list|)
argument_list|)
expr_stmt|;
name|mp
operator|=
name|tmp
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thingToDo
operator|==
literal|1
condition|)
block|{
name|LogMergePolicy
name|lmp
init|=
name|newLogMergePolicy
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|mp
operator|=
name|lmp
expr_stmt|;
block|}
else|else
block|{
comment|// just a regular random one from LTC (could be alcoholic etc)
name|mp
operator|=
name|newMergePolicy
argument_list|()
expr_stmt|;
block|}
comment|// wrap it with a sorting mp
return|return
operator|new
name|SortingMergePolicy
argument_list|(
name|mp
argument_list|,
name|sort
argument_list|)
return|;
block|}
DECL|method|createRandomIndexes
specifier|private
name|void
name|createRandomIndexes
parameter_list|()
throws|throws
name|IOException
block|{
name|dir1
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|dir2
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|150
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
operator|/
literal|5
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|randomTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|randomTerms
operator|.
name|size
argument_list|()
operator|<
name|numTerms
condition|)
block|{
name|randomTerms
operator|.
name|add
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|terms
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|randomTerms
argument_list|)
expr_stmt|;
specifier|final
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc1
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc2
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|iwc2
operator|.
name|setMergePolicy
argument_list|(
name|mergePolicy
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|iw1
init|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|dir1
argument_list|,
name|iwc1
argument_list|)
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|iw2
init|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|dir2
argument_list|,
name|iwc2
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
operator|&&
name|i
operator|!=
name|numDocs
operator|-
literal|1
condition|)
block|{
specifier|final
name|String
name|term
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|iw1
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Document
name|doc
init|=
name|randomDocument
argument_list|()
decl_stmt|;
name|iw1
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw2
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Make sure we have something to merge
name|iw1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw2
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|Document
name|doc
init|=
name|randomDocument
argument_list|()
decl_stmt|;
comment|// NOTE: don't use RIW.addDocument directly, since it sometimes commits
comment|// which may trigger a merge, at which case forceMerge may not do anything.
comment|// With field updates this is a problem, since the updates can go into the
comment|// single segment in the index, and threefore the index won't be sorted.
comment|// This hurts the assumption of the test later on, that the index is sorted
comment|// by SortingMP.
name|iw1
operator|.
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// update NDV of docs belonging to one term (covers many documents)
specifier|final
name|long
name|value
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|String
name|term
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|iw1
operator|.
name|w
operator|.
name|updateNumericDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|term
argument_list|)
argument_list|,
literal|"ndv"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|w
operator|.
name|updateNumericDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|term
argument_list|)
argument_list|,
literal|"ndv"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw2
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|sortedReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|sortedReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSorted
specifier|private
specifier|static
name|void
name|assertSorted
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|boolean
name|reverse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NumericDocValues
name|ndv
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
literal|"ndv"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|lhs
init|=
operator|(
operator|!
name|reverse
condition|?
name|i
operator|-
literal|1
else|:
name|i
operator|)
decl_stmt|;
specifier|final
name|int
name|rhs
init|=
operator|(
operator|!
name|reverse
condition|?
name|i
else|:
name|i
operator|-
literal|1
operator|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"ndv("
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
operator|+
literal|")="
operator|+
name|ndv
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|+
literal|",ndv("
operator|+
name|i
operator|+
literal|")="
operator|+
name|ndv
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|+
literal|",reverse="
operator|+
name|reverse
argument_list|,
name|ndv
operator|.
name|get
argument_list|(
name|lhs
argument_list|)
operator|<=
name|ndv
operator|.
name|get
argument_list|(
name|rhs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortingMP
specifier|public
name|void
name|testSortingMP
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|LeafReader
name|sortedReader1
init|=
name|SortingLeafReader
operator|.
name|wrap
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
argument_list|,
name|sort
argument_list|)
decl_stmt|;
specifier|final
name|LeafReader
name|sortedReader2
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|sortedReader
argument_list|)
decl_stmt|;
name|assertSorted
argument_list|(
name|sortedReader1
argument_list|,
name|reversedSort
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|sortedReader2
argument_list|,
name|reversedSort
argument_list|)
expr_stmt|;
name|assertReaderEquals
argument_list|(
literal|""
argument_list|,
name|sortedReader1
argument_list|,
name|sortedReader2
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadSort
specifier|public
name|void
name|testBadSort
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
operator|new
name|SortingMergePolicy
argument_list|(
name|newMergePolicy
argument_list|()
argument_list|,
name|Sort
operator|.
name|RELEVANCE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Cannot sort an index with a Sort that refers to the relevance score"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMethodsOverridden
specifier|public
name|void
name|testMethodsOverridden
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Method
name|m
range|:
name|MergePolicy
operator|.
name|class
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isFinal
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
continue|continue;
try|try
block|{
name|SortingMergePolicy
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|m
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"SortingMergePolicy needs to override '"
operator|+
name|m
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

