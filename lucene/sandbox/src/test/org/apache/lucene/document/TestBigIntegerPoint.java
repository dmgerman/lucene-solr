begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
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

begin_comment
comment|/** Simple tests for {@link BigIntegerPoint} */
end_comment

begin_class
DECL|class|TestBigIntegerPoint
specifier|public
class|class
name|TestBigIntegerPoint
extends|extends
name|LuceneTestCase
block|{
comment|/** Add a single 1D point and search for it */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with a large biginteger value
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|BigInteger
name|large
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|multiply
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|64
argument_list|)
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|BigIntegerPoint
argument_list|(
literal|"field"
argument_list|,
name|large
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|BigIntegerPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
name|large
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|BigIntegerPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
name|large
operator|.
name|subtract
argument_list|(
name|BigInteger
operator|.
name|ONE
argument_list|)
argument_list|,
name|large
operator|.
name|add
argument_list|(
name|BigInteger
operator|.
name|ONE
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|BigIntegerPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
name|large
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|BigIntegerPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
name|large
operator|.
name|subtract
argument_list|(
name|BigInteger
operator|.
name|ONE
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|BigIntegerPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Add a negative 1D point and search for it */
DECL|method|testNegative
specifier|public
name|void
name|testNegative
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with a large biginteger value
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|BigInteger
name|negative
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|multiply
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|64
argument_list|)
argument_list|)
operator|.
name|negate
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|BigIntegerPoint
argument_list|(
literal|"field"
argument_list|,
name|negative
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|BigIntegerPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
name|negative
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|BigIntegerPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
name|negative
operator|.
name|subtract
argument_list|(
name|BigInteger
operator|.
name|ONE
argument_list|)
argument_list|,
name|negative
operator|.
name|add
argument_list|(
name|BigInteger
operator|.
name|ONE
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test if we add a too-large value */
DECL|method|testTooLarge
specifier|public
name|void
name|testTooLarge
parameter_list|()
throws|throws
name|Exception
block|{
name|BigInteger
name|tooLarge
init|=
name|BigInteger
operator|.
name|ONE
operator|.
name|shiftLeft
argument_list|(
literal|128
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|BigIntegerPoint
argument_list|(
literal|"field"
argument_list|,
name|tooLarge
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"requires more than 16 bytes storage"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"BigIntegerPoint<field:1>"
argument_list|,
operator|new
name|BigIntegerPoint
argument_list|(
literal|"field"
argument_list|,
name|BigInteger
operator|.
name|ONE
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BigIntegerPoint<field:1,-2>"
argument_list|,
operator|new
name|BigIntegerPoint
argument_list|(
literal|"field"
argument_list|,
name|BigInteger
operator|.
name|ONE
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:[1 TO 1]"
argument_list|,
name|BigIntegerPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
name|BigInteger
operator|.
name|ONE
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:[1 TO 17]"
argument_list|,
name|BigIntegerPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
name|BigInteger
operator|.
name|ONE
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|17
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:[1 TO 17],[0 TO 42]"
argument_list|,
name|BigIntegerPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BigInteger
index|[]
block|{
name|BigInteger
operator|.
name|ONE
block|,
name|BigInteger
operator|.
name|ZERO
block|}
argument_list|,
operator|new
name|BigInteger
index|[]
block|{
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|17
argument_list|)
block|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|)
block|}
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:{1}"
argument_list|,
name|BigIntegerPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
name|BigInteger
operator|.
name|ONE
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueryEquals
specifier|public
name|void
name|testQueryEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|BigIntegerPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|BigIntegerPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q
operator|.
name|equals
argument_list|(
name|BigIntegerPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|BigIntegerPoint
operator|.
name|newExactQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|BigIntegerPoint
operator|.
name|newExactQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q
operator|.
name|equals
argument_list|(
name|BigIntegerPoint
operator|.
name|newExactQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|BigIntegerPoint
operator|.
name|newSetQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|17
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|BigIntegerPoint
operator|.
name|newSetQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|17
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q
operator|.
name|equals
argument_list|(
name|BigIntegerPoint
operator|.
name|newSetQuery
argument_list|(
literal|"a"
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|17
argument_list|)
argument_list|,
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

