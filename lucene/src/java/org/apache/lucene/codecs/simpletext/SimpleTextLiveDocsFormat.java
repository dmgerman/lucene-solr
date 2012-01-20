begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|codecs
operator|.
name|LiveDocsFormat
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
name|IndexFileNames
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
name|SegmentInfo
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
name|util
operator|.
name|ArrayUtil
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
name|CharsRef
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
name|IOUtils
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
name|MutableBits
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
name|StringHelper
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/**  * reads/writes plaintext live docs  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextLiveDocsFormat
specifier|public
class|class
name|SimpleTextLiveDocsFormat
extends|extends
name|LiveDocsFormat
block|{
DECL|field|LIVEDOCS_EXTENSION
specifier|static
specifier|final
name|String
name|LIVEDOCS_EXTENSION
init|=
literal|"liv"
decl_stmt|;
DECL|field|SIZE
specifier|final
specifier|static
name|BytesRef
name|SIZE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"size "
argument_list|)
decl_stmt|;
DECL|field|DOC
specifier|final
specifier|static
name|BytesRef
name|DOC
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  doc "
argument_list|)
decl_stmt|;
DECL|field|END
specifier|final
specifier|static
name|BytesRef
name|END
init|=
operator|new
name|BytesRef
argument_list|(
literal|"END"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextBits
argument_list|(
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readLiveDocs
specifier|public
name|Bits
name|readLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|info
operator|.
name|hasDeletions
argument_list|()
assert|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|CharsRef
name|scratchUTF16
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|LIVEDOCS_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
decl_stmt|;
name|IndexInput
name|in
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|in
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SIZE
argument_list|)
assert|;
name|int
name|size
init|=
name|parseIntAt
argument_list|(
name|scratch
argument_list|,
name|SIZE
operator|.
name|length
argument_list|,
name|scratchUTF16
argument_list|)
decl_stmt|;
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|scratch
operator|.
name|equals
argument_list|(
name|END
argument_list|)
condition|)
block|{
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|DOC
argument_list|)
assert|;
name|int
name|docid
init|=
name|parseIntAt
argument_list|(
name|scratch
argument_list|,
name|DOC
operator|.
name|length
argument_list|,
name|scratchUTF16
argument_list|)
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|docid
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|SimpleTextBits
argument_list|(
name|bits
argument_list|,
name|size
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseIntAt
specifier|private
name|int
name|parseIntAt
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|CharsRef
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|bytes
operator|.
name|length
operator|-
name|offset
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
return|return
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
name|scratch
operator|.
name|chars
argument_list|,
literal|0
argument_list|,
name|scratch
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeLiveDocs
specifier|public
name|void
name|writeLiveDocs
parameter_list|(
name|MutableBits
name|bits
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BitSet
name|set
init|=
operator|(
operator|(
name|SimpleTextBits
operator|)
name|bits
operator|)
operator|.
name|bits
decl_stmt|;
name|int
name|size
init|=
name|bits
operator|.
name|length
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|LIVEDOCS_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|out
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|SIZE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|size
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|set
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|set
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|DOC
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|END
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|separateFiles
specifier|public
name|void
name|separateFiles
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|info
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|LIVEDOCS_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SimpleTextBits
specifier|static
class|class
name|SimpleTextBits
implements|implements
name|MutableBits
block|{
DECL|field|bits
specifier|final
name|BitSet
name|bits
decl_stmt|;
DECL|field|size
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|SimpleTextBits
name|SimpleTextBits
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|bits
operator|=
operator|new
name|BitSet
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleTextBits
name|SimpleTextBits
parameter_list|(
name|BitSet
name|bits
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|bits
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
name|bits
operator|.
name|clear
argument_list|(
name|bit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|bits
operator|.
name|cardinality
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAndClear
specifier|public
name|boolean
name|getAndClear
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
name|boolean
name|v
init|=
name|bits
operator|.
name|get
argument_list|(
name|bit
argument_list|)
decl_stmt|;
name|bits
operator|.
name|clear
argument_list|(
name|bit
argument_list|)
expr_stmt|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SimpleTextBits
name|clone
parameter_list|()
block|{
name|BitSet
name|clonedBits
init|=
operator|(
name|BitSet
operator|)
name|bits
operator|.
name|clone
argument_list|()
decl_stmt|;
return|return
operator|new
name|SimpleTextBits
argument_list|(
name|clonedBits
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

