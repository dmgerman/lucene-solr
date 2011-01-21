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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IndexWriter
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
name|TermsEnum
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
name|_TestUtil
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
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_class
DECL|class|TestFieldCache
specifier|public
class|class
name|TestFieldCache
extends|extends
name|LuceneTestCase
block|{
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|NUM_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS
init|=
literal|1000
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
DECL|field|unicodeStrings
specifier|private
name|String
index|[]
name|unicodeStrings
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|long
name|theLong
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|double
name|theDouble
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
name|byte
name|theByte
init|=
name|Byte
operator|.
name|MAX_VALUE
decl_stmt|;
name|short
name|theShort
init|=
name|Short
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|theInt
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|float
name|theFloat
init|=
name|Float
operator|.
name|MAX_VALUE
decl_stmt|;
name|unicodeStrings
operator|=
operator|new
name|String
index|[
name|NUM_DOCS
index|]
expr_stmt|;
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
literal|"TEST: setUp"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|w
operator|.
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
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
name|newField
argument_list|(
literal|"theLong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theLong
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"theDouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theDouble
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"theByte"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theByte
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"theShort"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theShort
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"theInt"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theInt
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"theFloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theFloat
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// sometimes skip the field:
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|40
argument_list|)
operator|!=
literal|17
condition|)
block|{
name|String
name|s
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|==
literal|1
condition|)
block|{
comment|// reuse past string -- try to find one that's not null
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
operator|&&
name|s
operator|==
literal|null
condition|;
name|iter
operator|++
control|)
block|{
name|s
operator|=
name|unicodeStrings
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|i
argument_list|)
index|]
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|s
operator|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
literal|250
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|s
operator|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
literal|250
argument_list|)
expr_stmt|;
block|}
name|unicodeStrings
index|[
name|i
index|]
operator|=
name|s
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"theRandomUnicodeString"
argument_list|,
name|unicodeStrings
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
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
name|directory
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
DECL|method|testInfoStream
specifier|public
name|void
name|testInfoStream
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|cache
operator|.
name|setInfoStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bos
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
literal|"theDouble"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
literal|"theDouble"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bos
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"WARNING"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|double
index|[]
name|doubles
init|=
name|cache
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
literal|"theDouble"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|doubles
argument_list|,
name|cache
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
literal|"theDouble"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Second request with explicit parser return same array"
argument_list|,
name|doubles
argument_list|,
name|cache
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
literal|"theDouble"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_DOUBLE_PARSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"doubles Size: "
operator|+
name|doubles
operator|.
name|length
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|doubles
operator|.
name|length
operator|==
name|NUM_DOCS
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
name|doubles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|doubles
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
operator|(
name|Double
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|,
name|doubles
index|[
name|i
index|]
operator|==
operator|(
name|Double
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
name|long
index|[]
name|longs
init|=
name|cache
operator|.
name|getLongs
argument_list|(
name|reader
argument_list|,
literal|"theLong"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|longs
argument_list|,
name|cache
operator|.
name|getLongs
argument_list|(
name|reader
argument_list|,
literal|"theLong"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Second request with explicit parser return same array"
argument_list|,
name|longs
argument_list|,
name|cache
operator|.
name|getLongs
argument_list|(
name|reader
argument_list|,
literal|"theLong"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_LONG_PARSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"longs Size: "
operator|+
name|longs
operator|.
name|length
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|longs
operator|.
name|length
operator|==
name|NUM_DOCS
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
name|longs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|longs
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
operator|(
name|Long
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
operator|+
literal|" i="
operator|+
name|i
argument_list|,
name|longs
index|[
name|i
index|]
operator|==
operator|(
name|Long
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|bytes
init|=
name|cache
operator|.
name|getBytes
argument_list|(
name|reader
argument_list|,
literal|"theByte"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|bytes
argument_list|,
name|cache
operator|.
name|getBytes
argument_list|(
name|reader
argument_list|,
literal|"theByte"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Second request with explicit parser return same array"
argument_list|,
name|bytes
argument_list|,
name|cache
operator|.
name|getBytes
argument_list|(
name|reader
argument_list|,
literal|"theByte"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_BYTE_PARSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"bytes Size: "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|bytes
operator|.
name|length
operator|==
name|NUM_DOCS
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|bytes
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
operator|(
name|Byte
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|,
name|bytes
index|[
name|i
index|]
operator|==
call|(
name|byte
call|)
argument_list|(
name|Byte
operator|.
name|MAX_VALUE
operator|-
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|short
index|[]
name|shorts
init|=
name|cache
operator|.
name|getShorts
argument_list|(
name|reader
argument_list|,
literal|"theShort"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|shorts
argument_list|,
name|cache
operator|.
name|getShorts
argument_list|(
name|reader
argument_list|,
literal|"theShort"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Second request with explicit parser return same array"
argument_list|,
name|shorts
argument_list|,
name|cache
operator|.
name|getShorts
argument_list|(
name|reader
argument_list|,
literal|"theShort"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_SHORT_PARSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"shorts Size: "
operator|+
name|shorts
operator|.
name|length
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|shorts
operator|.
name|length
operator|==
name|NUM_DOCS
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
name|shorts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|shorts
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
operator|(
name|Short
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|,
name|shorts
index|[
name|i
index|]
operator|==
call|(
name|short
call|)
argument_list|(
name|Short
operator|.
name|MAX_VALUE
operator|-
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
index|[]
name|ints
init|=
name|cache
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
literal|"theInt"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|ints
argument_list|,
name|cache
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
literal|"theInt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Second request with explicit parser return same array"
argument_list|,
name|ints
argument_list|,
name|cache
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
literal|"theInt"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_INT_PARSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ints Size: "
operator|+
name|ints
operator|.
name|length
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|ints
operator|.
name|length
operator|==
name|NUM_DOCS
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|ints
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|,
name|ints
index|[
name|i
index|]
operator|==
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
name|float
index|[]
name|floats
init|=
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
literal|"theFloat"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|floats
argument_list|,
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
literal|"theFloat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Second request with explicit parser return same array"
argument_list|,
name|floats
argument_list|,
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
literal|"theFloat"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_FLOAT_PARSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"floats Size: "
operator|+
name|floats
operator|.
name|length
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|floats
operator|.
name|length
operator|==
name|NUM_DOCS
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
name|floats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|floats
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
operator|(
name|Float
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|,
name|floats
index|[
name|i
index|]
operator|==
operator|(
name|Float
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// getTermsIndex
name|FieldCache
operator|.
name|DocTermsIndex
name|termsIndex
init|=
name|cache
operator|.
name|getTermsIndex
argument_list|(
name|reader
argument_list|,
literal|"theRandomUnicodeString"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|termsIndex
argument_list|,
name|cache
operator|.
name|getTermsIndex
argument_list|(
name|reader
argument_list|,
literal|"theRandomUnicodeString"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"doubles Size: "
operator|+
name|termsIndex
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|termsIndex
operator|.
name|size
argument_list|()
operator|==
name|NUM_DOCS
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|termsIndex
operator|.
name|getTerm
argument_list|(
name|i
argument_list|,
name|br
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|term
operator|==
literal|null
condition|?
literal|null
else|:
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"for doc "
operator|+
name|i
operator|+
literal|": "
operator|+
name|s
operator|+
literal|" does not equal: "
operator|+
name|unicodeStrings
index|[
name|i
index|]
argument_list|,
name|unicodeStrings
index|[
name|i
index|]
operator|==
literal|null
operator|||
name|unicodeStrings
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|nTerms
init|=
name|termsIndex
operator|.
name|numOrd
argument_list|()
decl_stmt|;
comment|// System.out.println("nTerms="+nTerms);
name|TermsEnum
name|tenum
init|=
name|termsIndex
operator|.
name|getTermsEnum
argument_list|()
decl_stmt|;
name|BytesRef
name|val
init|=
operator|new
name|BytesRef
argument_list|()
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
name|nTerms
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|val1
init|=
name|tenum
operator|.
name|next
argument_list|()
decl_stmt|;
name|BytesRef
name|val2
init|=
name|termsIndex
operator|.
name|lookup
argument_list|(
name|i
argument_list|,
name|val
argument_list|)
decl_stmt|;
comment|// System.out.println("i="+i);
name|assertEquals
argument_list|(
name|val2
argument_list|,
name|val1
argument_list|)
expr_stmt|;
block|}
comment|// seek the enum around (note this isn't a great test here)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|i
operator|++
control|)
block|{
name|int
name|k
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|nTerms
operator|-
literal|1
argument_list|)
decl_stmt|;
name|BytesRef
name|val1
init|=
name|termsIndex
operator|.
name|lookup
argument_list|(
name|k
argument_list|,
name|val
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|tenum
operator|.
name|seek
argument_list|(
name|val1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val1
argument_list|,
name|tenum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test bad field
name|termsIndex
operator|=
name|cache
operator|.
name|getTermsIndex
argument_list|(
name|reader
argument_list|,
literal|"bogusfield"
argument_list|)
expr_stmt|;
comment|// getTerms
name|FieldCache
operator|.
name|DocTerms
name|terms
init|=
name|cache
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"theRandomUnicodeString"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Second request to cache return same array"
argument_list|,
name|terms
argument_list|,
name|cache
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"theRandomUnicodeString"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"doubles Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|NUM_DOCS
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
name|NUM_DOCS
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|terms
operator|.
name|getTerm
argument_list|(
name|i
argument_list|,
name|br
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|term
operator|==
literal|null
condition|?
literal|null
else|:
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"for doc "
operator|+
name|i
operator|+
literal|": "
operator|+
name|s
operator|+
literal|" does not equal: "
operator|+
name|unicodeStrings
index|[
name|i
index|]
argument_list|,
name|unicodeStrings
index|[
name|i
index|]
operator|==
literal|null
operator|||
name|unicodeStrings
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test bad field
name|terms
operator|=
name|cache
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"bogusfield"
argument_list|)
expr_stmt|;
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|purge
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
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
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|500
argument_list|)
argument_list|)
decl_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|FieldCache
operator|.
name|DocTerms
name|terms
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"foobar"
argument_list|)
decl_stmt|;
name|FieldCache
operator|.
name|DocTermsIndex
name|termsIndex
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|r
argument_list|,
literal|"foobar"
argument_list|)
decl_stmt|;
name|r
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
block|}
end_class

end_unit

