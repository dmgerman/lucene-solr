begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|Closeable
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
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

begin_comment
comment|/** Minimal port of contrib/benchmark's LneDocSource +  * DocMaker, so tests can enum docs from a line file created  * by contrib/benchmark's WriteLineDoc task */
end_comment

begin_class
DECL|class|LineFileDocs
specifier|public
class|class
name|LineFileDocs
implements|implements
name|Closeable
block|{
DECL|field|reader
specifier|private
name|BufferedReader
name|reader
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|BUFFER_SIZE
init|=
literal|1
operator|<<
literal|16
decl_stmt|;
comment|// 64K
DECL|field|id
specifier|private
specifier|final
name|AtomicInteger
name|id
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
comment|/** If forever is true, we rewind the file at EOF (repeat    * the docs over and over) */
DECL|method|LineFileDocs
specifier|public
name|LineFileDocs
parameter_list|(
name|Random
name|random
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|open
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
DECL|method|LineFileDocs
specifier|public
name|LineFileDocs
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|random
argument_list|,
name|LuceneTestCase
operator|.
name|TEST_LINE_DOCS_FILE
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|open
specifier|private
specifier|synchronized
name|void
name|open
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
comment|// if its not in classpath, we load it as absolute filesystem path (e.g. Hudson's home dir)
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|long
name|size
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|size
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|size
operator|=
name|is
operator|.
name|available
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|".gz"
argument_list|)
condition|)
block|{
name|is
operator|=
operator|new
name|GZIPInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|// guestimate:
name|size
operator|*=
literal|2.8
expr_stmt|;
block|}
specifier|final
name|InputStream
name|in
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|,
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
comment|// Override sizes for currently "known" line files:
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"europarl.lines.txt.gz"
argument_list|)
condition|)
block|{
name|size
operator|=
literal|15129506L
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"/home/hudson/lucene-data/enwiki.random.lines.txt.gz"
argument_list|)
condition|)
block|{
name|size
operator|=
literal|3038178822L
expr_stmt|;
block|}
comment|// Randomly seek to starting point:
if|if
condition|(
name|random
operator|!=
literal|null
operator|&&
name|size
operator|>
literal|3
condition|)
block|{
specifier|final
name|long
name|seekTo
init|=
operator|(
name|random
operator|.
name|nextLong
argument_list|()
operator|&
name|Long
operator|.
name|MAX_VALUE
operator|)
operator|%
operator|(
name|size
operator|/
literal|3
operator|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: LineFileDocs: seek to fp="
operator|+
name|seekTo
operator|+
literal|" on open"
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|skip
argument_list|(
name|seekTo
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|reset
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|close
argument_list|()
expr_stmt|;
name|open
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|id
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|field|SEP
specifier|private
specifier|final
specifier|static
name|char
name|SEP
init|=
literal|'\t'
decl_stmt|;
DECL|class|DocState
specifier|private
specifier|static
specifier|final
class|class
name|DocState
block|{
DECL|field|doc
specifier|final
name|Document
name|doc
decl_stmt|;
DECL|field|titleTokenized
specifier|final
name|Field
name|titleTokenized
decl_stmt|;
DECL|field|title
specifier|final
name|Field
name|title
decl_stmt|;
DECL|field|body
specifier|final
name|Field
name|body
decl_stmt|;
DECL|field|id
specifier|final
name|Field
name|id
decl_stmt|;
DECL|field|date
specifier|final
name|Field
name|date
decl_stmt|;
DECL|method|DocState
specifier|public
name|DocState
parameter_list|()
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|title
operator|=
operator|new
name|Field
argument_list|(
literal|"title"
argument_list|,
literal|""
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
name|NOT_ANALYZED_NO_NORMS
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|titleTokenized
operator|=
operator|new
name|Field
argument_list|(
literal|"titleTokenized"
argument_list|,
literal|""
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
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|titleTokenized
argument_list|)
expr_stmt|;
name|body
operator|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
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
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|id
operator|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|""
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
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|date
operator|=
operator|new
name|Field
argument_list|(
literal|"date"
argument_list|,
literal|""
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
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|date
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|threadDocs
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|DocState
argument_list|>
name|threadDocs
init|=
operator|new
name|ThreadLocal
argument_list|<
name|DocState
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Note: Document instance is re-used per-thread */
DECL|method|nextDoc
specifier|public
name|Document
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|line
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
comment|// Always rewind at end:
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: LineFileDocs: now rewind file..."
argument_list|)
expr_stmt|;
block|}
name|close
argument_list|()
expr_stmt|;
name|open
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
name|DocState
name|docState
init|=
name|threadDocs
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|docState
operator|==
literal|null
condition|)
block|{
name|docState
operator|=
operator|new
name|DocState
argument_list|()
expr_stmt|;
name|threadDocs
operator|.
name|set
argument_list|(
name|docState
argument_list|)
expr_stmt|;
block|}
name|int
name|spot
init|=
name|line
operator|.
name|indexOf
argument_list|(
name|SEP
argument_list|)
decl_stmt|;
if|if
condition|(
name|spot
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"line: ["
operator|+
name|line
operator|+
literal|"] is in an invalid format !"
argument_list|)
throw|;
block|}
name|int
name|spot2
init|=
name|line
operator|.
name|indexOf
argument_list|(
name|SEP
argument_list|,
literal|1
operator|+
name|spot
argument_list|)
decl_stmt|;
if|if
condition|(
name|spot2
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"line: ["
operator|+
name|line
operator|+
literal|"] is in an invalid format !"
argument_list|)
throw|;
block|}
name|docState
operator|.
name|body
operator|.
name|setValue
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|spot2
argument_list|,
name|line
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|title
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|spot
argument_list|)
decl_stmt|;
name|docState
operator|.
name|title
operator|.
name|setValue
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|docState
operator|.
name|titleTokenized
operator|.
name|setValue
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|docState
operator|.
name|date
operator|.
name|setValue
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|spot
argument_list|,
name|spot2
argument_list|)
argument_list|)
expr_stmt|;
name|docState
operator|.
name|id
operator|.
name|setValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|id
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|docState
operator|.
name|doc
return|;
block|}
block|}
end_class

end_unit

