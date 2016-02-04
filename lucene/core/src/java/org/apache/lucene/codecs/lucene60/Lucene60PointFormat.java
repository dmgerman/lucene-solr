begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene60
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene60
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
name|PointFormat
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
name|PointReader
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
name|PointWriter
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
name|SegmentReadState
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
name|SegmentWriteState
import|;
end_import

begin_comment
comment|/**  * Lucene 6.0 point format, which encodes dimensional values in a block KD-tree structure  * for fast shape intersection filtering. See<a href="https://www.cs.duke.edu/~pankaj/publications/papers/bkd-sstd.pdf">this paper</a> for details.  *  *<p>This data structure is written as a series of blocks on disk, with an in-memory perfectly balanced  * binary tree of split values referencing those blocks at the leaves.  *  *<p>The<code>.dim</code> file has both blocks and the index split  * values, for each field.  The file starts with {@link CodecUtil#writeIndexHeader}.  *  *<p>The blocks are written like this:  *  *<ul>  *<li> count (vInt)  *<li> delta-docID (vInt)<sup>count</sup> (delta coded docIDs, in sorted order)  *<li> packedValue<sup>count</sup> (the<code>byte[]</code> value of each dimension packed into a single<code>byte[]</code>)  *</ul>  *  *<p>After all blocks for a field are written, then the index is written:  *<ul>  *<li> numDims (vInt)  *<li> maxPointsInLeafNode (vInt)  *<li> bytesPerDim (vInt)  *<li> count (vInt)  *<li> byte[bytesPerDim]<sup>count</sup> (packed<code>byte[]</code> all split values)  *<li> delta-blockFP (vLong)<sup>count</sup> (delta-coded file pointers to the on-disk leaf blocks))  *</ul>  *  *<p>After all fields blocks + index data are written, {@link CodecUtil#writeFooter} writes the checksum.  *  *<p>The<code>.dii</code> file records the file pointer in the<code>.dim</code> file where each field's  * index data was written.  It starts with {@link CodecUtil#writeIndexHeader}, then has:  *  *<ul>  *<li> fieldCount (vInt)  *<li> (fieldNumber (vInt), fieldFilePointer (vLong))<sup>fieldCount</sup>  *</ul>  *  *<p> After that, {@link CodecUtil#writeFooter} writes the checksum.  *  *<p>After all fields blocks + index data are written, {@link CodecUtil#writeFooter} writes the checksum.   * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene60PointFormat
specifier|public
specifier|final
class|class
name|Lucene60PointFormat
extends|extends
name|PointFormat
block|{
DECL|field|DATA_CODEC_NAME
specifier|static
specifier|final
name|String
name|DATA_CODEC_NAME
init|=
literal|"Lucene60PointFormatData"
decl_stmt|;
DECL|field|META_CODEC_NAME
specifier|static
specifier|final
name|String
name|META_CODEC_NAME
init|=
literal|"Lucene60PointFormatMeta"
decl_stmt|;
comment|/**    * Filename extension for the leaf blocks    */
DECL|field|DATA_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dim"
decl_stmt|;
comment|/**    * Filename extension for the index per field    */
DECL|field|INDEX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_EXTENSION
init|=
literal|"dii"
decl_stmt|;
DECL|field|DATA_VERSION_START
specifier|static
specifier|final
name|int
name|DATA_VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|DATA_VERSION_CURRENT
specifier|static
specifier|final
name|int
name|DATA_VERSION_CURRENT
init|=
name|DATA_VERSION_START
decl_stmt|;
DECL|field|INDEX_VERSION_START
specifier|static
specifier|final
name|int
name|INDEX_VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|INDEX_VERSION_CURRENT
specifier|static
specifier|final
name|int
name|INDEX_VERSION_CURRENT
init|=
name|INDEX_VERSION_START
decl_stmt|;
comment|/** Sole constructor */
DECL|method|Lucene60PointFormat
specifier|public
name|Lucene60PointFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|PointWriter
name|fieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene60PointWriter
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|PointReader
name|fieldsReader
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene60PointReader
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
end_class

end_unit

