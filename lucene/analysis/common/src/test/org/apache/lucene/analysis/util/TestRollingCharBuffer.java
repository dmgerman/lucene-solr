begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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

begin_class
DECL|class|TestRollingCharBuffer
specifier|public
class|class
name|TestRollingCharBuffer
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|ITERS
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|RollingCharBuffer
name|buffer
init|=
operator|new
name|RollingCharBuffer
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
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
name|ITERS
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|stringLen
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
else|:
name|random
operator|.
name|nextInt
argument_list|(
literal|20000
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
decl_stmt|;
if|if
condition|(
name|stringLen
operator|==
literal|0
condition|)
block|{
name|s
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
name|stringLen
argument_list|)
expr_stmt|;
block|}
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
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" s.length()="
operator|+
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|nextRead
init|=
literal|0
decl_stmt|;
name|int
name|availCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|nextRead
operator|<
name|s
operator|.
name|length
argument_list|()
condition|)
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
literal|"  cycle nextRead="
operator|+
name|nextRead
operator|+
literal|" avail="
operator|+
name|availCount
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|availCount
operator|==
literal|0
operator|||
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Read next char
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
literal|"    new char"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|nextRead
argument_list|)
argument_list|,
name|buffer
operator|.
name|get
argument_list|(
name|nextRead
argument_list|)
argument_list|)
expr_stmt|;
name|nextRead
operator|++
expr_stmt|;
name|availCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Read previous char
name|int
name|pos
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
name|nextRead
operator|-
name|availCount
argument_list|,
name|nextRead
operator|-
literal|1
argument_list|)
decl_stmt|;
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
literal|"    old char pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
argument_list|,
name|buffer
operator|.
name|get
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Read slice
name|int
name|length
decl_stmt|;
if|if
condition|(
name|availCount
operator|==
literal|1
condition|)
block|{
name|length
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|length
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|availCount
argument_list|)
expr_stmt|;
block|}
name|int
name|start
decl_stmt|;
if|if
condition|(
name|length
operator|==
name|availCount
condition|)
block|{
name|start
operator|=
name|nextRead
operator|-
name|availCount
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
name|nextRead
operator|-
name|availCount
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|availCount
operator|-
name|length
argument_list|)
expr_stmt|;
block|}
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
literal|"    slice start="
operator|+
name|start
operator|+
literal|" length="
operator|+
name|length
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|length
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|get
argument_list|(
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|availCount
operator|>
literal|0
operator|&&
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|17
condition|)
block|{
specifier|final
name|int
name|toFree
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|availCount
argument_list|)
decl_stmt|;
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
literal|"    free "
operator|+
name|toFree
operator|+
literal|" (avail="
operator|+
operator|(
name|availCount
operator|-
name|toFree
operator|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|freeBefore
argument_list|(
name|nextRead
operator|-
operator|(
name|availCount
operator|-
name|toFree
operator|)
argument_list|)
expr_stmt|;
name|availCount
operator|-=
name|toFree
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

