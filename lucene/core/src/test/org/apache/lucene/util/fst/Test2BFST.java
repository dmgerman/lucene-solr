begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
package|;
end_package

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
name|Random
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
name|store
operator|.
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|MMapDirectory
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
name|IntsRef
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
name|TimeUnits
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
name|packed
operator|.
name|PackedInts
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

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|TimeoutSuite
import|;
end_import

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Requires tons of heap to run (30 GB hits OOME but 35 GB passes after ~4.5 hours)"
argument_list|)
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|100
operator|*
name|TimeUnits
operator|.
name|HOUR
argument_list|)
DECL|class|Test2BFST
specifier|public
class|class
name|Test2BFST
extends|extends
name|LuceneTestCase
block|{
DECL|field|LIMIT
specifier|private
specifier|static
name|long
name|LIMIT
init|=
literal|3L
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|ints
init|=
operator|new
name|int
index|[
literal|7
index|]
decl_stmt|;
name|IntsRef
name|input
init|=
operator|new
name|IntsRef
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|,
name|ints
operator|.
name|length
argument_list|)
decl_stmt|;
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BFST"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doPackIter
init|=
literal|0
init|;
name|doPackIter
operator|<
literal|2
condition|;
name|doPackIter
operator|++
control|)
block|{
name|boolean
name|doPack
init|=
name|doPackIter
operator|==
literal|1
decl_stmt|;
comment|// Build FST w/ NoOutputs and stop when nodeCount> 2.2B
if|if
condition|(
operator|!
name|doPack
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: 3B nodes; doPack=false output=NO_OUTPUTS"
argument_list|)
expr_stmt|;
name|Outputs
argument_list|<
name|Object
argument_list|>
name|outputs
init|=
name|NoOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|Object
name|NO_OUTPUT
init|=
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|Object
argument_list|>
name|b
init|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|outputs
argument_list|,
name|doPack
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|,
literal|true
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|int
index|[]
name|ints2
init|=
operator|new
name|int
index|[
literal|200
index|]
decl_stmt|;
name|IntsRef
name|input2
init|=
operator|new
name|IntsRef
argument_list|(
name|ints2
argument_list|,
literal|0
argument_list|,
name|ints2
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("add: " + input + " -> " + output);
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
name|ints2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ints2
index|[
name|i
index|]
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|add
argument_list|(
name|input2
argument_list|,
name|NO_OUTPUT
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|count
operator|+
literal|": "
operator|+
name|b
operator|.
name|fstRamBytesUsed
argument_list|()
operator|+
literal|" bytes; "
operator|+
name|b
operator|.
name|getNodeCount
argument_list|()
operator|+
literal|" nodes"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|.
name|getNodeCount
argument_list|()
operator|>
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|100L
operator|*
literal|1024
operator|*
literal|1024
condition|)
block|{
break|break;
block|}
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints2
argument_list|)
expr_stmt|;
block|}
name|FST
argument_list|<
name|Object
argument_list|>
name|fst
init|=
name|b
operator|.
name|finish
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|verify
init|=
literal|0
init|;
name|verify
operator|<
literal|2
condition|;
name|verify
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: now verify [fst size="
operator|+
name|fst
operator|.
name|ramBytesUsed
argument_list|()
operator|+
literal|"; nodeCount="
operator|+
name|b
operator|.
name|getNodeCount
argument_list|()
operator|+
literal|"; arcCount="
operator|+
name|b
operator|.
name|getArcCount
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|1000000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|"...: "
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|10
init|;
name|j
operator|<
name|ints2
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ints2
index|[
name|j
index|]
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|NO_OUTPUT
argument_list|,
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
name|input2
argument_list|)
argument_list|)
expr_stmt|;
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints2
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: enum all input/outputs"
argument_list|)
expr_stmt|;
name|IntsRefFSTEnum
argument_list|<
name|Object
argument_list|>
name|fstEnum
init|=
operator|new
name|IntsRefFSTEnum
argument_list|<>
argument_list|(
name|fst
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|IntsRefFSTEnum
operator|.
name|InputOutput
argument_list|<
name|Object
argument_list|>
name|pair
init|=
name|fstEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
block|{
break|break;
block|}
for|for
control|(
name|int
name|j
init|=
literal|10
init|;
name|j
operator|<
name|ints2
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ints2
index|[
name|j
index|]
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|input2
argument_list|,
name|pair
operator|.
name|input
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NO_OUTPUT
argument_list|,
name|pair
operator|.
name|output
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints2
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|verify
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: save/load FST and re-verify"
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"fst"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|fst
operator|.
name|save
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"fst"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|fst
operator|=
operator|new
name|FST
argument_list|<>
argument_list|(
name|in
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"fst"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Build FST w/ ByteSequenceOutputs and stop when FST
comment|// size = 3GB
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: 3 GB size; doPack="
operator|+
name|doPack
operator|+
literal|" outputs=bytes"
argument_list|)
expr_stmt|;
name|Outputs
argument_list|<
name|BytesRef
argument_list|>
name|outputs
init|=
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|BytesRef
argument_list|>
name|b
init|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|outputs
argument_list|,
name|doPack
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|,
literal|true
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|byte
index|[]
name|outputBytes
init|=
operator|new
name|byte
index|[
literal|20
index|]
decl_stmt|;
name|BytesRef
name|output
init|=
operator|new
name|BytesRef
argument_list|(
name|outputBytes
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|r
operator|.
name|nextBytes
argument_list|(
name|outputBytes
argument_list|)
expr_stmt|;
comment|//System.out.println("add: " + input + " -> " + output);
name|b
operator|.
name|add
argument_list|(
name|input
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|1000000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|count
operator|+
literal|"...: "
operator|+
name|b
operator|.
name|fstRamBytesUsed
argument_list|()
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|.
name|fstRamBytesUsed
argument_list|()
operator|>
name|LIMIT
condition|)
block|{
break|break;
block|}
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints
argument_list|)
expr_stmt|;
block|}
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|fst
init|=
name|b
operator|.
name|finish
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|verify
init|=
literal|0
init|;
name|verify
operator|<
literal|2
condition|;
name|verify
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: now verify [fst size="
operator|+
name|fst
operator|.
name|ramBytesUsed
argument_list|()
operator|+
literal|"; nodeCount="
operator|+
name|b
operator|.
name|getNodeCount
argument_list|()
operator|+
literal|"; arcCount="
operator|+
name|b
operator|.
name|getArcCount
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|1000000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|"...: "
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|nextBytes
argument_list|(
name|outputBytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: enum all input/outputs"
argument_list|)
expr_stmt|;
name|IntsRefFSTEnum
argument_list|<
name|BytesRef
argument_list|>
name|fstEnum
init|=
operator|new
name|IntsRefFSTEnum
argument_list|<>
argument_list|(
name|fst
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|IntsRefFSTEnum
operator|.
name|InputOutput
argument_list|<
name|BytesRef
argument_list|>
name|pair
init|=
name|fstEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|assertEquals
argument_list|(
name|input
argument_list|,
name|pair
operator|.
name|input
argument_list|)
expr_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|outputBytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|pair
operator|.
name|output
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|verify
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: save/load FST and re-verify"
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"fst"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|fst
operator|.
name|save
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"fst"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|fst
operator|=
operator|new
name|FST
argument_list|<>
argument_list|(
name|in
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"fst"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Build FST w/ PositiveIntOutputs and stop when FST
comment|// size = 3GB
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: 3 GB size; doPack="
operator|+
name|doPack
operator|+
literal|" outputs=long"
argument_list|)
expr_stmt|;
name|Outputs
argument_list|<
name|Long
argument_list|>
name|outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|Long
argument_list|>
name|b
init|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|outputs
argument_list|,
name|doPack
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|,
literal|true
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|long
name|output
init|=
literal|1
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("add: " + input + " -> " + output);
name|b
operator|.
name|add
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|output
operator|+=
literal|1
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|1000000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|count
operator|+
literal|"...: "
operator|+
name|b
operator|.
name|fstRamBytesUsed
argument_list|()
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|.
name|fstRamBytesUsed
argument_list|()
operator|>
name|LIMIT
condition|)
block|{
break|break;
block|}
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints
argument_list|)
expr_stmt|;
block|}
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
init|=
name|b
operator|.
name|finish
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|verify
init|=
literal|0
init|;
name|verify
operator|<
literal|2
condition|;
name|verify
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: now verify [fst size="
operator|+
name|fst
operator|.
name|ramBytesUsed
argument_list|()
operator|+
literal|"; nodeCount="
operator|+
name|b
operator|.
name|getNodeCount
argument_list|()
operator|+
literal|"; arcCount="
operator|+
name|b
operator|.
name|getArcCount
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|output
operator|=
literal|1
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|1000000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|"...: "
argument_list|)
expr_stmt|;
block|}
comment|// forward lookup:
name|assertEquals
argument_list|(
name|output
argument_list|,
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
name|input
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// reverse lookup:
name|assertEquals
argument_list|(
name|input
argument_list|,
name|Util
operator|.
name|getByOutput
argument_list|(
name|fst
argument_list|,
name|output
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|+=
literal|1
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: enum all input/outputs"
argument_list|)
expr_stmt|;
name|IntsRefFSTEnum
argument_list|<
name|Long
argument_list|>
name|fstEnum
init|=
operator|new
name|IntsRefFSTEnum
argument_list|<>
argument_list|(
name|fst
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|output
operator|=
literal|1
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|IntsRefFSTEnum
operator|.
name|InputOutput
argument_list|<
name|Long
argument_list|>
name|pair
init|=
name|fstEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|assertEquals
argument_list|(
name|input
argument_list|,
name|pair
operator|.
name|input
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|pair
operator|.
name|output
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|+=
literal|1
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
name|nextInput
argument_list|(
name|r
argument_list|,
name|ints
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|verify
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: save/load FST and re-verify"
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"fst"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|fst
operator|.
name|save
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"fst"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|fst
operator|=
operator|new
name|FST
argument_list|<>
argument_list|(
name|in
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"fst"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|nextInput
specifier|private
name|void
name|nextInput
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
index|[]
name|ints
parameter_list|)
block|{
name|int
name|downTo
init|=
literal|6
decl_stmt|;
while|while
condition|(
name|downTo
operator|>=
literal|0
condition|)
block|{
comment|// Must add random amounts (and not just 1) because
comment|// otherwise FST outsmarts us and remains tiny:
name|ints
index|[
name|downTo
index|]
operator|+=
literal|1
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
if|if
condition|(
name|ints
index|[
name|downTo
index|]
operator|<
literal|256
condition|)
block|{
break|break;
block|}
else|else
block|{
name|ints
index|[
name|downTo
index|]
operator|=
literal|0
expr_stmt|;
name|downTo
operator|--
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

