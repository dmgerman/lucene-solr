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
name|FSDirectory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Vector
import|;
end_import

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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_class
DECL|class|TermInfosTest
class|class
name|TermInfosTest
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|test
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// FIXME: OG: remove hard-coded file names
DECL|method|test
specifier|public
specifier|static
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"words.txt"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" reading word file containing "
operator|+
name|file
operator|.
name|length
argument_list|()
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Vector
name|keys
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|FileInputStream
name|ws
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedReader
name|wr
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|ws
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
init|=
name|wr
operator|.
name|readLine
argument_list|()
init|;
name|key
operator|!=
literal|null
condition|;
name|key
operator|=
name|wr
operator|.
name|readLine
argument_list|()
control|)
name|keys
operator|.
name|addElement
argument_list|(
operator|new
name|Term
argument_list|(
literal|"word"
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|wr
operator|.
name|close
argument_list|()
expr_stmt|;
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to read "
operator|+
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|" words"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|Random
name|gen
init|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
decl_stmt|;
name|long
name|fp
init|=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
decl_stmt|;
name|long
name|pp
init|=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
decl_stmt|;
name|int
index|[]
name|docFreqs
init|=
operator|new
name|int
index|[
name|keys
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|long
index|[]
name|freqPointers
init|=
operator|new
name|long
index|[
name|keys
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|long
index|[]
name|proxPointers
init|=
operator|new
name|long
index|[
name|keys
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
literal|0
init|;
name|i
operator|<
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|docFreqs
index|[
name|i
index|]
operator|=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
expr_stmt|;
name|freqPointers
index|[
name|i
index|]
operator|=
name|fp
expr_stmt|;
name|proxPointers
index|[
name|i
index|]
operator|=
name|pp
expr_stmt|;
name|fp
operator|+=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
expr_stmt|;
empty_stmt|;
name|pp
operator|+=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
expr_stmt|;
empty_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to generate values"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|Directory
name|store
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
literal|"test.store"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldInfos
name|fis
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
name|TermInfosWriter
name|writer
init|=
operator|new
name|TermInfosWriter
argument_list|(
name|store
argument_list|,
literal|"words"
argument_list|,
name|fis
argument_list|,
name|IndexWriter
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
argument_list|)
decl_stmt|;
name|fis
operator|.
name|add
argument_list|(
literal|"word"
argument_list|,
literal|false
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
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|add
argument_list|(
operator|(
name|Term
operator|)
name|keys
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|TermInfo
argument_list|(
name|docFreqs
index|[
name|i
index|]
argument_list|,
name|freqPointers
index|[
name|i
index|]
argument_list|,
name|proxPointers
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to write table"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" table occupies "
operator|+
name|store
operator|.
name|fileLength
argument_list|(
literal|"words.tis"
argument_list|)
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|TermInfosReader
name|reader
init|=
operator|new
name|TermInfosReader
argument_list|(
name|store
argument_list|,
literal|"words"
argument_list|,
name|fis
argument_list|)
decl_stmt|;
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to open table"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|SegmentTermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
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
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|enumerator
operator|.
name|next
argument_list|()
expr_stmt|;
name|Term
name|key
init|=
operator|(
name|Term
operator|)
name|keys
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong term: "
operator|+
name|enumerator
operator|.
name|term
argument_list|()
operator|+
literal|", expected: "
operator|+
name|key
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
name|TermInfo
name|ti
init|=
name|enumerator
operator|.
name|termInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|ti
operator|.
name|docFreq
operator|!=
name|docFreqs
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|docFreq
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|docFreqs
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|freqPointer
operator|!=
name|freqPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|freqPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|freqPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|proxPointer
operator|!=
name|proxPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|proxPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|proxPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to iterate over "
operator|+
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|" words"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
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
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|key
init|=
operator|(
name|Term
operator|)
name|keys
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|TermInfo
name|ti
init|=
name|reader
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|ti
operator|.
name|docFreq
operator|!=
name|docFreqs
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|docFreq
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|docFreqs
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|freqPointer
operator|!=
name|freqPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|freqPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|freqPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|proxPointer
operator|!=
name|proxPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|proxPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|proxPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
operator|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
operator|)
operator|/
operator|(
name|float
operator|)
name|keys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" average milliseconds per lookup"
argument_list|)
expr_stmt|;
name|TermEnum
name|e
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"word"
argument_list|,
literal|"azz"
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Word after azz is "
operator|+
name|e
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

