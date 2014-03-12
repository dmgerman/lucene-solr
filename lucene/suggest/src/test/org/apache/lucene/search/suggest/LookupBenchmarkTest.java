begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|Arrays
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
name|Locale
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
name|concurrent
operator|.
name|Callable
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
name|analysis
operator|.
name|MockTokenizer
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
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingInfixSuggester
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
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingSuggester
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
name|suggest
operator|.
name|analyzing
operator|.
name|FuzzySuggester
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
name|suggest
operator|.
name|fst
operator|.
name|FSTCompletionLookup
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
name|suggest
operator|.
name|fst
operator|.
name|WFSTCompletionLookup
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
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellLookup
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
name|suggest
operator|.
name|tst
operator|.
name|TSTLookup
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
name|FSDirectory
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/**  * Benchmarks tests for implementations of {@link Lookup} interface.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"COMMENT ME TO RUN BENCHMARKS!"
argument_list|)
DECL|class|LookupBenchmarkTest
specifier|public
class|class
name|LookupBenchmarkTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|benchmarkClasses
specifier|private
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
argument_list|>
name|benchmarkClasses
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|FuzzySuggester
operator|.
name|class
argument_list|,
name|AnalyzingSuggester
operator|.
name|class
argument_list|,
name|AnalyzingInfixSuggester
operator|.
name|class
argument_list|,
name|JaspellLookup
operator|.
name|class
argument_list|,
name|TSTLookup
operator|.
name|class
argument_list|,
name|FSTCompletionLookup
operator|.
name|class
argument_list|,
name|WFSTCompletionLookup
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rounds
specifier|private
specifier|final
specifier|static
name|int
name|rounds
init|=
literal|15
decl_stmt|;
DECL|field|warmup
specifier|private
specifier|final
specifier|static
name|int
name|warmup
init|=
literal|5
decl_stmt|;
DECL|field|num
specifier|private
specifier|final
name|int
name|num
init|=
literal|7
decl_stmt|;
DECL|field|onlyMorePopular
specifier|private
specifier|final
name|boolean
name|onlyMorePopular
init|=
literal|false
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
specifier|static
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|0xdeadbeef
argument_list|)
decl_stmt|;
comment|/**    * Input term/weight pairs.    */
DECL|field|dictionaryInput
specifier|private
specifier|static
name|Input
index|[]
name|dictionaryInput
decl_stmt|;
comment|/**    * Benchmark term/weight pairs (randomized order).    */
DECL|field|benchmarkInput
specifier|private
specifier|static
name|List
argument_list|<
name|Input
argument_list|>
name|benchmarkInput
decl_stmt|;
comment|/**    * Loads terms and frequencies from Wikipedia (cached).    */
annotation|@
name|BeforeClass
DECL|method|setup
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
assert|assert
literal|false
operator|:
literal|"disable assertions before running benchmarks!"
assert|;
name|List
argument_list|<
name|Input
argument_list|>
name|input
init|=
name|readTop50KWiki
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|input
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|LookupBenchmarkTest
operator|.
name|dictionaryInput
operator|=
name|input
operator|.
name|toArray
argument_list|(
operator|new
name|Input
index|[
name|input
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|input
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|LookupBenchmarkTest
operator|.
name|benchmarkInput
operator|=
name|input
expr_stmt|;
block|}
DECL|field|UTF_8
specifier|static
specifier|final
name|Charset
name|UTF_8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|/**    * Collect the multilingual input for benchmarks/ tests.    */
DECL|method|readTop50KWiki
specifier|public
specifier|static
name|List
argument_list|<
name|Input
argument_list|>
name|readTop50KWiki
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Input
argument_list|>
name|input
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|URL
name|resource
init|=
name|LookupBenchmarkTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"Top50KWiki.utf8"
argument_list|)
decl_stmt|;
assert|assert
name|resource
operator|!=
literal|null
operator|:
literal|"Resource missing: Top50KWiki.utf8"
assert|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|resource
operator|.
name|openStream
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|int
name|tab
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'|'
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No | separator?: "
operator|+
name|line
argument_list|,
name|tab
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|int
name|weight
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|tab
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|tab
argument_list|)
decl_stmt|;
name|input
operator|.
name|add
argument_list|(
operator|new
name|Input
argument_list|(
name|key
argument_list|,
name|weight
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|input
return|;
block|}
comment|/**    * Test construction time.    */
DECL|method|testConstructionTime
specifier|public
name|void
name|testConstructionTime
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"-- construction time"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
name|cls
range|:
name|benchmarkClasses
control|)
block|{
name|BenchmarkResult
name|result
init|=
name|measure
argument_list|(
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Lookup
name|lookup
init|=
name|buildLookup
argument_list|(
name|cls
argument_list|,
name|dictionaryInput
argument_list|)
decl_stmt|;
return|return
name|lookup
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%-15s input: %d, time[ms]: %s"
argument_list|,
name|cls
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|dictionaryInput
operator|.
name|length
argument_list|,
name|result
operator|.
name|average
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test memory required for the storage.    */
DECL|method|testStorageNeeds
specifier|public
name|void
name|testStorageNeeds
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"-- RAM consumption"
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
name|cls
range|:
name|benchmarkClasses
control|)
block|{
name|Lookup
name|lookup
init|=
name|buildLookup
argument_list|(
name|cls
argument_list|,
name|dictionaryInput
argument_list|)
decl_stmt|;
name|long
name|sizeInBytes
init|=
name|lookup
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%-15s size[B]:%,13d"
argument_list|,
name|lookup
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|sizeInBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create {@link Lookup} instance and populate it.     */
DECL|method|buildLookup
specifier|private
name|Lookup
name|buildLookup
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
name|cls
parameter_list|,
name|Input
index|[]
name|input
parameter_list|)
throws|throws
name|Exception
block|{
name|Lookup
name|lookup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|lookup
operator|=
name|cls
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|cls
operator|==
name|AnalyzingInfixSuggester
operator|.
name|class
condition|)
block|{
name|lookup
operator|=
operator|new
name|AnalyzingInfixSuggester
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|FSDirectory
operator|.
name|open
argument_list|(
name|TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"LookupBenchmarkTest"
argument_list|)
argument_list|)
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
name|ctor
init|=
name|cls
operator|.
name|getConstructor
argument_list|(
name|Analyzer
operator|.
name|class
argument_list|)
decl_stmt|;
name|lookup
operator|=
name|ctor
operator|.
name|newInstance
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|lookup
return|;
block|}
comment|/**    * Test performance of lookup on full hits.    */
DECL|method|testPerformanceOnFullHits
specifier|public
name|void
name|testPerformanceOnFullHits
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|minPrefixLen
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|maxPrefixLen
init|=
literal|200
decl_stmt|;
name|runPerformanceTest
argument_list|(
name|minPrefixLen
argument_list|,
name|maxPrefixLen
argument_list|,
name|num
argument_list|,
name|onlyMorePopular
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test performance of lookup on longer term prefixes (6-9 letters or shorter).    */
DECL|method|testPerformanceOnPrefixes6_9
specifier|public
name|void
name|testPerformanceOnPrefixes6_9
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|minPrefixLen
init|=
literal|6
decl_stmt|;
specifier|final
name|int
name|maxPrefixLen
init|=
literal|9
decl_stmt|;
name|runPerformanceTest
argument_list|(
name|minPrefixLen
argument_list|,
name|maxPrefixLen
argument_list|,
name|num
argument_list|,
name|onlyMorePopular
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test performance of lookup on short term prefixes (2-4 letters or shorter).    */
DECL|method|testPerformanceOnPrefixes2_4
specifier|public
name|void
name|testPerformanceOnPrefixes2_4
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|minPrefixLen
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|maxPrefixLen
init|=
literal|4
decl_stmt|;
name|runPerformanceTest
argument_list|(
name|minPrefixLen
argument_list|,
name|maxPrefixLen
argument_list|,
name|num
argument_list|,
name|onlyMorePopular
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run the actual benchmark.     */
DECL|method|runPerformanceTest
specifier|public
name|void
name|runPerformanceTest
parameter_list|(
specifier|final
name|int
name|minPrefixLen
parameter_list|,
specifier|final
name|int
name|maxPrefixLen
parameter_list|,
specifier|final
name|int
name|num
parameter_list|,
specifier|final
name|boolean
name|onlyMorePopular
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"-- prefixes: %d-%d, num: %d, onlyMorePopular: %s"
argument_list|,
name|minPrefixLen
argument_list|,
name|maxPrefixLen
argument_list|,
name|num
argument_list|,
name|onlyMorePopular
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
name|cls
range|:
name|benchmarkClasses
control|)
block|{
specifier|final
name|Lookup
name|lookup
init|=
name|buildLookup
argument_list|(
name|cls
argument_list|,
name|dictionaryInput
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|input
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|benchmarkInput
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Input
name|tf
range|:
name|benchmarkInput
control|)
block|{
name|String
name|s
init|=
name|tf
operator|.
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|String
name|sub
init|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|minPrefixLen
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|maxPrefixLen
operator|-
name|minPrefixLen
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|input
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
name|BenchmarkResult
name|result
init|=
name|measure
argument_list|(
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|v
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|term
range|:
name|input
control|)
block|{
name|v
operator|+=
name|lookup
operator|.
name|lookup
argument_list|(
name|term
argument_list|,
name|onlyMorePopular
argument_list|,
name|num
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%-15s queries: %d, time[ms]: %s, ~kQPS: %.0f"
argument_list|,
name|lookup
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|input
operator|.
name|size
argument_list|()
argument_list|,
name|result
operator|.
name|average
operator|.
name|toString
argument_list|()
argument_list|,
name|input
operator|.
name|size
argument_list|()
operator|/
name|result
operator|.
name|average
operator|.
name|avg
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Do the measurements.    */
DECL|method|measure
specifier|private
name|BenchmarkResult
name|measure
parameter_list|(
name|Callable
argument_list|<
name|Integer
argument_list|>
name|callable
parameter_list|)
block|{
specifier|final
name|double
name|NANOS_PER_MS
init|=
literal|1000000
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|Double
argument_list|>
name|times
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|warmup
operator|+
name|rounds
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|guard
operator|=
name|callable
operator|.
name|call
argument_list|()
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|times
operator|.
name|add
argument_list|(
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|)
operator|/
name|NANOS_PER_MS
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BenchmarkResult
argument_list|(
name|times
argument_list|,
name|warmup
argument_list|,
name|rounds
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Guard against opts. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|field|guard
specifier|private
specifier|static
specifier|volatile
name|int
name|guard
decl_stmt|;
DECL|class|BenchmarkResult
specifier|private
specifier|static
class|class
name|BenchmarkResult
block|{
comment|/** Average time per round (ms). */
DECL|field|average
specifier|public
specifier|final
name|Average
name|average
decl_stmt|;
DECL|method|BenchmarkResult
specifier|public
name|BenchmarkResult
parameter_list|(
name|List
argument_list|<
name|Double
argument_list|>
name|times
parameter_list|,
name|int
name|warmup
parameter_list|,
name|int
name|rounds
parameter_list|)
block|{
name|this
operator|.
name|average
operator|=
name|Average
operator|.
name|from
argument_list|(
name|times
operator|.
name|subList
argument_list|(
name|warmup
argument_list|,
name|times
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

