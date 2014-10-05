begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|util
operator|.
name|Collection
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
name|CodecUtil
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
name|CorruptIndexException
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
name|SegmentCommitInfo
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
name|ChecksumIndexInput
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
name|DataOutput
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
name|MutableBits
import|;
end_import

begin_comment
comment|/**   * Lucene 5.0 live docs format   *<p>  *<p>The .liv file is optional, and only exists when a segment contains  * deletions.</p>  *<p>Although per-segment, this file is maintained exterior to compound segment  * files.</p>  *<p>Deletions (.liv) --&gt; SegmentHeader,Generation,Bits</p>  *<ul>  *<li>SegmentHeader --&gt; {@link CodecUtil#writeSegmentHeader SegmentHeader}</li>  *<li>Generation --&gt; {@link DataOutput#writeLong Int64}  *<li>Bits --&gt;&lt;{@link DataOutput#writeLong Int64}&gt;<sup>LongCount</sup></li>  *</ul>  */
end_comment

begin_class
DECL|class|Lucene50LiveDocsFormat
specifier|public
specifier|final
class|class
name|Lucene50LiveDocsFormat
extends|extends
name|LiveDocsFormat
block|{
comment|/** Sole constructor. */
DECL|method|Lucene50LiveDocsFormat
specifier|public
name|Lucene50LiveDocsFormat
parameter_list|()
block|{   }
comment|/** extension of live docs */
DECL|field|EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|"liv"
decl_stmt|;
comment|/** codec of live docs */
DECL|field|CODEC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene50LiveDocs"
decl_stmt|;
comment|/** supported version range */
DECL|field|VERSION_START
specifier|private
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|private
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
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
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|bits
return|;
block|}
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|Bits
name|existing
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|fbs
init|=
operator|(
name|FixedBitSet
operator|)
name|existing
decl_stmt|;
return|return
name|fbs
operator|.
name|clone
argument_list|()
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
name|SegmentCommitInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|gen
init|=
name|info
operator|.
name|getDelGen
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|info
operator|.
name|name
argument_list|,
name|EXTENSION
argument_list|,
name|gen
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|input
init|=
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkSegmentHeader
argument_list|(
name|input
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|info
operator|.
name|info
operator|.
name|getId
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|long
name|filegen
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|gen
operator|!=
name|filegen
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"file mismatch, expected generation="
operator|+
name|gen
operator|+
literal|", got="
operator|+
name|filegen
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|long
name|data
index|[]
init|=
operator|new
name|long
index|[
name|FixedBitSet
operator|.
name|bits2words
argument_list|(
name|length
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
name|FixedBitSet
name|fbs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|data
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|fbs
operator|.
name|length
argument_list|()
operator|-
name|fbs
operator|.
name|cardinality
argument_list|()
operator|!=
name|info
operator|.
name|getDelCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bits.deleted="
operator|+
operator|(
name|fbs
operator|.
name|length
argument_list|()
operator|-
name|fbs
operator|.
name|cardinality
argument_list|()
operator|)
operator|+
literal|" info.delcount="
operator|+
name|info
operator|.
name|getDelCount
argument_list|()
argument_list|,
name|input
argument_list|)
throw|;
block|}
return|return
name|fbs
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
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
name|SegmentCommitInfo
name|info
parameter_list|,
name|int
name|newDelCount
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|gen
init|=
name|info
operator|.
name|getNextDelGen
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|info
operator|.
name|name
argument_list|,
name|EXTENSION
argument_list|,
name|gen
argument_list|)
decl_stmt|;
name|FixedBitSet
name|fbs
init|=
operator|(
name|FixedBitSet
operator|)
name|bits
decl_stmt|;
if|if
condition|(
name|fbs
operator|.
name|length
argument_list|()
operator|-
name|fbs
operator|.
name|cardinality
argument_list|()
operator|!=
name|info
operator|.
name|getDelCount
argument_list|()
operator|+
name|newDelCount
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bits.deleted="
operator|+
operator|(
name|fbs
operator|.
name|length
argument_list|()
operator|-
name|fbs
operator|.
name|cardinality
argument_list|()
operator|)
operator|+
literal|" info.delcount="
operator|+
name|info
operator|.
name|getDelCount
argument_list|()
operator|+
literal|" newdelcount="
operator|+
name|newDelCount
argument_list|,
name|name
argument_list|)
throw|;
block|}
name|long
name|data
index|[]
init|=
name|fbs
operator|.
name|getBits
argument_list|()
decl_stmt|;
try|try
init|(
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|writeSegmentHeader
argument_list|(
name|output
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|info
operator|.
name|info
operator|.
name|getId
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|gen
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeLong
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|,
name|Collection
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
name|info
operator|.
name|name
argument_list|,
name|EXTENSION
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
block|}
end_class

end_unit

