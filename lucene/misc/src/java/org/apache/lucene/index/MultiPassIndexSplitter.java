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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|List
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
operator|.
name|OpenMode
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
name|FixedBitSet
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
name|Bits
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
name|Version
import|;
end_import

begin_comment
comment|/**  * This tool splits input index into multiple equal parts. The method employed  * here uses {@link IndexWriter#addIndexes(CodecReader[])} where the input data  * comes from the input index with artificially applied deletes to the document  * id-s that fall outside the selected partition.  *<p>Note 1: Deletes are only applied to a buffered list of deleted docs and  * don't affect the source index - this tool works also with read-only indexes.  *<p>Note 2: the disadvantage of this tool is that source index needs to be  * read as many times as there are parts to be created, hence the name of this  * tool.  *  *<p><b>NOTE</b>: this tool is unaware of documents added  * atomically via {@link IndexWriter#addDocuments} or {@link  * IndexWriter#updateDocuments}, which means it can easily  * break up such document groups.  */
end_comment

begin_class
DECL|class|MultiPassIndexSplitter
specifier|public
class|class
name|MultiPassIndexSplitter
block|{
comment|/**    * Split source index into multiple parts.    * @param in source index, can have deletions, can have    * multiple segments (or multiple readers).    * @param outputs list of directories where the output parts will be stored.    * @param seq if true, then the source index will be split into equal    * increasing ranges of document id-s. If false, source document id-s will be    * assigned in a deterministic round-robin fashion to one of the output splits.    * @throws IOException If there is a low-level I/O error    */
DECL|method|split
specifier|public
name|void
name|split
parameter_list|(
name|IndexReader
name|in
parameter_list|,
name|Directory
index|[]
name|outputs
parameter_list|,
name|boolean
name|seq
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|outputs
operator|==
literal|null
operator|||
name|outputs
operator|.
name|length
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid number of outputs."
argument_list|)
throw|;
block|}
if|if
condition|(
name|in
operator|==
literal|null
operator|||
name|in
operator|.
name|numDocs
argument_list|()
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not enough documents for splitting"
argument_list|)
throw|;
block|}
name|int
name|numParts
init|=
name|outputs
operator|.
name|length
decl_stmt|;
comment|// wrap a potentially read-only input
comment|// this way we don't have to preserve original deletions because neither
comment|// deleteDocument(int) or undeleteAll() is applied to the wrapped input index.
name|FakeDeleteIndexReader
name|input
init|=
operator|new
name|FakeDeleteIndexReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|input
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
name|partLen
init|=
name|maxDoc
operator|/
name|numParts
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
name|numParts
condition|;
name|i
operator|++
control|)
block|{
name|input
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
if|if
condition|(
name|seq
condition|)
block|{
comment|// sequential range
name|int
name|lo
init|=
name|partLen
operator|*
name|i
decl_stmt|;
name|int
name|hi
init|=
name|lo
operator|+
name|partLen
decl_stmt|;
comment|// below range
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|lo
condition|;
name|j
operator|++
control|)
block|{
name|input
operator|.
name|deleteDocument
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
comment|// above range - last part collects all id-s that remained due to
comment|// integer rounding errors
if|if
condition|(
name|i
operator|<
name|numParts
operator|-
literal|1
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
name|hi
init|;
name|j
operator|<
name|maxDoc
condition|;
name|j
operator|++
control|)
block|{
name|input
operator|.
name|deleteDocument
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// round-robin
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|maxDoc
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|j
operator|+
name|numParts
operator|-
name|i
operator|)
operator|%
name|numParts
operator|!=
literal|0
condition|)
block|{
name|input
operator|.
name|deleteDocument
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|outputs
index|[
name|i
index|]
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Writing part "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
comment|// pass the subreaders directly, as our wrapper's numDocs/hasDeletetions are not up-to-date
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|FakeDeleteLeafIndexReader
argument_list|>
name|sr
init|=
name|input
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
name|w
operator|.
name|addIndexes
argument_list|(
name|sr
operator|.
name|toArray
argument_list|(
operator|new
name|CodecReader
index|[
name|sr
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: maybe take List<IR> here?
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Done."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|5
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: MultiPassIndexSplitter -out<outputDir> -num<numParts> [-seq]<inputIndex1> [<inputIndex2 ...]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\tinputIndex\tpath to input index, multiple values are ok"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-out ouputDir\tpath to output directory to contain partial indexes"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-num numParts\tnumber of parts to produce"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-seq\tsequential docid-range split (default is round-robin)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|IndexReader
argument_list|>
name|indexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|outDir
init|=
literal|null
decl_stmt|;
name|int
name|numParts
init|=
operator|-
literal|1
decl_stmt|;
name|boolean
name|seq
init|=
literal|false
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-out"
argument_list|)
condition|)
block|{
name|outDir
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-num"
argument_list|)
condition|)
block|{
name|numParts
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-seq"
argument_list|)
condition|)
block|{
name|seq
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|Path
name|file
init|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid input path - skipping: "
operator|+
name|file
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid input index - skipping: "
operator|+
name|file
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid input index - skipping: "
operator|+
name|file
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|indexes
operator|.
name|add
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|outDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Required argument missing: -out outputDir"
argument_list|)
throw|;
block|}
if|if
condition|(
name|numParts
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Invalid value of required argument: -num numParts"
argument_list|)
throw|;
block|}
if|if
condition|(
name|indexes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"No input indexes to process"
argument_list|)
throw|;
block|}
name|Path
name|out
init|=
name|Paths
operator|.
name|get
argument_list|(
name|outDir
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[
name|numParts
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
name|numParts
condition|;
name|i
operator|++
control|)
block|{
name|dirs
index|[
name|i
index|]
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|out
operator|.
name|resolve
argument_list|(
literal|"part-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MultiPassIndexSplitter
name|splitter
init|=
operator|new
name|MultiPassIndexSplitter
argument_list|()
decl_stmt|;
name|IndexReader
name|input
decl_stmt|;
if|if
condition|(
name|indexes
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|input
operator|=
name|indexes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|input
operator|=
operator|new
name|MultiReader
argument_list|(
name|indexes
operator|.
name|toArray
argument_list|(
operator|new
name|IndexReader
index|[
name|indexes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|splitter
operator|.
name|split
argument_list|(
name|input
argument_list|,
name|dirs
argument_list|,
name|seq
argument_list|)
expr_stmt|;
block|}
comment|/**    * This class emulates deletions on the underlying index.    */
DECL|class|FakeDeleteIndexReader
specifier|private
specifier|static
specifier|final
class|class
name|FakeDeleteIndexReader
extends|extends
name|BaseCompositeReader
argument_list|<
name|FakeDeleteLeafIndexReader
argument_list|>
block|{
DECL|method|FakeDeleteIndexReader
specifier|public
name|FakeDeleteIndexReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|initSubReaders
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initSubReaders
specifier|private
specifier|static
name|FakeDeleteLeafIndexReader
index|[]
name|initSubReaders
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|FakeDeleteLeafIndexReader
index|[]
name|subs
init|=
operator|new
name|FakeDeleteLeafIndexReader
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|LeafReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
name|subs
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|FakeDeleteLeafIndexReader
argument_list|(
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|subs
return|;
block|}
DECL|method|deleteDocument
specifier|public
name|void
name|deleteDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|getSequentialSubReaders
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|deleteDocument
argument_list|(
name|docID
operator|-
name|readerBase
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|undeleteAll
specifier|public
name|void
name|undeleteAll
parameter_list|()
block|{
for|for
control|(
name|FakeDeleteLeafIndexReader
name|r
range|:
name|getSequentialSubReaders
argument_list|()
control|)
block|{
name|r
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{}
comment|// no need to override numDocs/hasDeletions,
comment|// as we pass the subreaders directly to IW.addIndexes().
block|}
DECL|class|FakeDeleteLeafIndexReader
specifier|private
specifier|static
specifier|final
class|class
name|FakeDeleteLeafIndexReader
extends|extends
name|FilterCodecReader
block|{
DECL|field|liveDocs
name|FixedBitSet
name|liveDocs
decl_stmt|;
DECL|method|FakeDeleteLeafIndexReader
specifier|public
name|FakeDeleteLeafIndexReader
parameter_list|(
name|CodecReader
name|reader
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|undeleteAll
argument_list|()
expr_stmt|;
comment|// initialize main bitset
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|liveDocs
operator|.
name|cardinality
argument_list|()
return|;
block|}
DECL|method|undeleteAll
specifier|public
name|void
name|undeleteAll
parameter_list|()
block|{
specifier|final
name|int
name|maxDoc
init|=
name|in
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|liveDocs
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|in
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
specifier|final
name|Bits
name|oldLiveDocs
init|=
name|in
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
assert|assert
name|oldLiveDocs
operator|!=
literal|null
assert|;
comment|// this loop is a little bit ineffective, as Bits has no nextSetBit():
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|oldLiveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
name|liveDocs
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// mark all docs as valid
name|liveDocs
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteDocument
specifier|public
name|void
name|deleteDocument
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|liveDocs
operator|.
name|clear
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
name|liveDocs
return|;
block|}
block|}
block|}
end_class

end_unit

