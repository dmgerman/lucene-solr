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
name|Arrays
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
name|store
operator|.
name|MockDirectoryWrapper
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
name|IntsRefBuilder
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|UpToTwoPositiveIntOutputs
operator|.
name|TwoLongs
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|FSTTester
operator|.
name|getRandomString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|FSTTester
operator|.
name|toIntsRef
import|;
end_import

begin_class
DECL|class|TestFSTsMisc
specifier|public
class|class
name|TestFSTsMisc
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|MockDirectoryWrapper
name|dir
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
name|dir
operator|=
name|newMockDirectory
argument_list|()
expr_stmt|;
name|dir
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
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
comment|// can be null if we force simpletext (funky, some kind of bug in test runner maybe)
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
name|dir
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
DECL|method|testRandomWords
specifier|public
name|void
name|testRandomWords
parameter_list|()
throws|throws
name|IOException
block|{
name|testRandomWords
argument_list|(
literal|1000
argument_list|,
name|LuceneTestCase
operator|.
name|atLeast
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//testRandomWords(100, 1);
block|}
DECL|method|testRandomWords
specifier|private
name|void
name|testRandomWords
parameter_list|(
name|int
name|maxNumWords
parameter_list|,
name|int
name|numIter
parameter_list|)
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|numIter
condition|;
name|iter
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter "
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|inputMode
init|=
literal|0
init|;
name|inputMode
operator|<
literal|2
condition|;
name|inputMode
operator|++
control|)
block|{
specifier|final
name|int
name|numWords
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxNumWords
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|termsSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|IntsRef
index|[]
name|terms
init|=
operator|new
name|IntsRef
index|[
name|numWords
index|]
decl_stmt|;
while|while
condition|(
name|termsSet
operator|.
name|size
argument_list|()
operator|<
name|numWords
condition|)
block|{
specifier|final
name|String
name|term
init|=
name|getRandomString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|termsSet
operator|.
name|add
argument_list|(
name|toIntsRef
argument_list|(
name|term
argument_list|,
name|inputMode
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doTest
argument_list|(
name|inputMode
argument_list|,
name|termsSet
operator|.
name|toArray
argument_list|(
operator|new
name|IntsRef
index|[
name|termsSet
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
name|int
name|inputMode
parameter_list|,
name|IntsRef
index|[]
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
comment|// Up to two positive ints, shared, generally but not
comment|// monotonically increasing
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now test UpToTwoPositiveIntOutputs"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|UpToTwoPositiveIntOutputs
name|outputs
init|=
name|UpToTwoPositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FSTTester
operator|.
name|InputOutput
argument_list|<
name|Object
argument_list|>
argument_list|>
name|pairs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
operator|.
name|length
argument_list|)
decl_stmt|;
name|long
name|lastOutput
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|terms
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
comment|// Sometimes go backwards
name|long
name|value
init|=
name|lastOutput
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
while|while
condition|(
name|value
operator|<
literal|0
condition|)
block|{
name|value
operator|=
name|lastOutput
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Object
name|output
decl_stmt|;
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
literal|3
condition|)
block|{
name|long
name|value2
init|=
name|lastOutput
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
while|while
condition|(
name|value2
operator|<
literal|0
condition|)
block|{
name|value2
operator|=
name|lastOutput
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value2
argument_list|)
expr_stmt|;
name|output
operator|=
name|values
expr_stmt|;
block|}
else|else
block|{
name|output
operator|=
name|outputs
operator|.
name|get
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|pairs
operator|.
name|add
argument_list|(
operator|new
name|FSTTester
operator|.
name|InputOutput
argument_list|<>
argument_list|(
name|terms
index|[
name|idx
index|]
argument_list|,
name|output
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|new
name|FSTTester
argument_list|<
name|Object
argument_list|>
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|inputMode
argument_list|,
name|pairs
argument_list|,
name|outputs
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|outputsEqual
parameter_list|(
name|Object
name|output1
parameter_list|,
name|Object
name|output2
parameter_list|)
block|{
if|if
condition|(
name|output1
operator|instanceof
name|TwoLongs
operator|&&
name|output2
operator|instanceof
name|List
condition|)
block|{
name|TwoLongs
name|twoLongs1
init|=
operator|(
name|TwoLongs
operator|)
name|output1
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|twoLongs1
operator|.
name|first
block|,
name|twoLongs1
operator|.
name|second
block|}
argument_list|)
operator|.
name|equals
argument_list|(
name|output2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|output2
operator|instanceof
name|TwoLongs
operator|&&
name|output1
operator|instanceof
name|List
condition|)
block|{
name|TwoLongs
name|twoLongs2
init|=
operator|(
name|TwoLongs
operator|)
name|output2
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|twoLongs2
operator|.
name|first
block|,
name|twoLongs2
operator|.
name|second
block|}
argument_list|)
operator|.
name|equals
argument_list|(
name|output1
argument_list|)
return|;
block|}
return|return
name|output1
operator|.
name|equals
argument_list|(
name|output2
argument_list|)
return|;
block|}
block|}
operator|.
name|doTest
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// ListOfOutputs(PositiveIntOutputs), generally but not
comment|// monotonically increasing
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now test OneOrMoreOutputs"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PositiveIntOutputs
name|_outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
specifier|final
name|ListOfOutputs
argument_list|<
name|Long
argument_list|>
name|outputs
init|=
operator|new
name|ListOfOutputs
argument_list|<>
argument_list|(
name|_outputs
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FSTTester
operator|.
name|InputOutput
argument_list|<
name|Object
argument_list|>
argument_list|>
name|pairs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
operator|.
name|length
argument_list|)
decl_stmt|;
name|long
name|lastOutput
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|terms
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|int
name|outputCount
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
literal|7
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|values
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
name|outputCount
condition|;
name|i
operator|++
control|)
block|{
comment|// Sometimes go backwards
name|long
name|value
init|=
name|lastOutput
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
while|while
condition|(
name|value
operator|<
literal|0
condition|)
block|{
name|value
operator|=
name|lastOutput
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|lastOutput
operator|=
name|value
expr_stmt|;
block|}
specifier|final
name|Object
name|output
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|output
operator|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|=
name|values
expr_stmt|;
block|}
name|pairs
operator|.
name|add
argument_list|(
operator|new
name|FSTTester
operator|.
name|InputOutput
argument_list|<>
argument_list|(
name|terms
index|[
name|idx
index|]
argument_list|,
name|output
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|new
name|FSTTester
argument_list|<>
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|inputMode
argument_list|,
name|pairs
argument_list|,
name|outputs
argument_list|,
literal|false
argument_list|)
operator|.
name|doTest
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testListOfOutputs
specifier|public
name|void
name|testListOfOutputs
parameter_list|()
throws|throws
name|Exception
block|{
name|PositiveIntOutputs
name|_outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|ListOfOutputs
argument_list|<
name|Long
argument_list|>
name|outputs
init|=
operator|new
name|ListOfOutputs
argument_list|<>
argument_list|(
name|_outputs
argument_list|)
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|Object
argument_list|>
name|builder
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
name|outputs
argument_list|)
decl_stmt|;
specifier|final
name|IntsRefBuilder
name|scratch
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
comment|// Add the same input more than once and the outputs
comment|// are merged:
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|17L
argument_list|)
expr_stmt|;
specifier|final
name|FST
argument_list|<
name|Object
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|Object
name|output
init|=
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|outputList
init|=
name|outputs
operator|.
name|asList
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|outputList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|=
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|outputList
operator|=
name|outputs
operator|.
name|asList
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|outputList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|17L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testListOfOutputsEmptyString
specifier|public
name|void
name|testListOfOutputsEmptyString
parameter_list|()
throws|throws
name|Exception
block|{
name|PositiveIntOutputs
name|_outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|ListOfOutputs
argument_list|<
name|Long
argument_list|>
name|outputs
init|=
operator|new
name|ListOfOutputs
argument_list|<>
argument_list|(
name|_outputs
argument_list|)
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|Object
argument_list|>
name|builder
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
name|outputs
argument_list|)
decl_stmt|;
specifier|final
name|IntsRefBuilder
name|scratch
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
literal|17L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|scratch
argument_list|)
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
specifier|final
name|FST
argument_list|<
name|Object
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|Object
name|output
init|=
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|outputList
init|=
name|outputs
operator|.
name|asList
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|outputList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|17L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|=
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|outputList
operator|=
name|outputs
operator|.
name|asList
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|outputList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|=
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|outputList
operator|=
name|outputs
operator|.
name|asList
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|outputList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|outputList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

