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
name|SegmentInfoFormat
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
name|SegmentInfoReader
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
name|SegmentInfoWriter
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

begin_comment
comment|// javadocs
end_comment

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

begin_comment
comment|// javadocs
end_comment

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
name|SegmentInfos
import|;
end_import

begin_comment
comment|// javadocs
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
name|DataOutput
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**  * Lucene 5.0 Segment info format.  *<p>  * Files:  *<ul>  *<li><tt>.si</tt>: Header, SegVersion, SegSize, IsCompoundFile, Diagnostics, Files, Id, Footer  *</ul>  *</p>  * Data types:  *<p>  *<ul>  *<li>Header --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *<li>SegSize --&gt; {@link DataOutput#writeInt Int32}</li>  *<li>SegVersion --&gt; {@link DataOutput#writeString String}</li>  *<li>Files --&gt; {@link DataOutput#writeStringSet Set&lt;String&gt;}</li>  *<li>Diagnostics --&gt; {@link DataOutput#writeStringStringMap Map&lt;String,String&gt;}</li>  *<li>IsCompoundFile --&gt; {@link DataOutput#writeByte Int8}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *<li>Id --&gt; {@link DataOutput#writeString String}</li>  *</ul>  *</p>  * Field Descriptions:  *<p>  *<ul>  *<li>SegVersion is the code version that created the segment.</li>  *<li>SegSize is the number of documents contained in the segment index.</li>  *<li>IsCompoundFile records whether the segment is written as a compound file or  *       not. If this is -1, the segment is not a compound file. If it is 1, the segment  *       is a compound file.</li>  *<li>The Diagnostics Map is privately written by {@link IndexWriter}, as a debugging aid,  *       for each segment it creates. It includes metadata like the current Lucene  *       version, OS, Java version, why the segment was created (merge, flush,  *       addIndexes), etc.</li>  *<li>Files is a list of files referred to by this segment.</li>  *</ul>  *</p>  *   * @see SegmentInfos  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene50SegmentInfoFormat
specifier|public
class|class
name|Lucene50SegmentInfoFormat
extends|extends
name|SegmentInfoFormat
block|{
DECL|field|reader
specifier|private
specifier|final
name|SegmentInfoReader
name|reader
init|=
operator|new
name|Lucene50SegmentInfoReader
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|SegmentInfoWriter
name|writer
init|=
operator|new
name|Lucene50SegmentInfoWriter
argument_list|()
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Lucene50SegmentInfoFormat
specifier|public
name|Lucene50SegmentInfoFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getSegmentInfoReader
specifier|public
name|SegmentInfoReader
name|getSegmentInfoReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentInfoWriter
specifier|public
name|SegmentInfoWriter
name|getSegmentInfoWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
comment|/** File extension used to store {@link SegmentInfo}. */
DECL|field|SI_EXTENSION
specifier|public
specifier|final
specifier|static
name|String
name|SI_EXTENSION
init|=
literal|"si"
decl_stmt|;
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene50SegmentInfo"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
block|}
end_class

end_unit

