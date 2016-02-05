begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene53
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene53
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
name|NormsConsumer
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
name|NormsFormat
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
name|NormsProducer
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
comment|/**  * Lucene 5.3 Score normalization format.  *<p>  * Encodes normalization values by encoding each value with the minimum  * number of bytes needed to represent the range (which can be zero).  *<p>  * Files:  *<ol>  *<li><tt>.nvd</tt>: Norms data</li>  *<li><tt>.nvm</tt>: Norms metadata</li>  *</ol>  *<ol>  *<li><a name="nvm"></a>  *<p>The Norms metadata or .nvm file.</p>  *<p>For each norms field, this stores metadata, such as the offset into the   *      Norms data (.nvd)</p>  *<p>Norms metadata (.dvm) --&gt; Header,&lt;Entry&gt;<sup>NumFields</sup>,Footer</p>  *<ul>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>Entry --&gt; FieldNumber,BytesPerValue, Address</li>  *<li>FieldNumber --&gt; {@link DataOutput#writeVInt vInt}</li>  *<li>BytesPerValue --&gt; {@link DataOutput#writeByte byte}</li>  *<li>Offset --&gt; {@link DataOutput#writeLong Int64}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *<p>FieldNumber of -1 indicates the end of metadata.</p>  *<p>Offset is the pointer to the start of the data in the norms data (.nvd), or the singleton value   *      when BytesPerValue = 0</p>  *<li><a name="nvd"></a>  *<p>The Norms data or .nvd file.</p>  *<p>For each Norms field, this stores the actual per-document data (the heavy-lifting)</p>  *<p>Norms data (.nvd) --&gt; Header,&lt; Data&gt;<sup>NumFields</sup>,Footer</p>  *<ul>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>Data --&gt; {@link DataOutput#writeByte(byte) byte}<sup>MaxDoc * BytesPerValue</sup></li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *</ol>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene53NormsFormat
specifier|public
class|class
name|Lucene53NormsFormat
extends|extends
name|NormsFormat
block|{
comment|/** Sole Constructor */
DECL|method|Lucene53NormsFormat
specifier|public
name|Lucene53NormsFormat
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|NormsConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene53NormsConsumer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
name|NormsProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene53NormsProducer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
DECL|field|DATA_CODEC
specifier|private
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene53NormsData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"nvd"
decl_stmt|;
DECL|field|METADATA_CODEC
specifier|private
specifier|static
specifier|final
name|String
name|METADATA_CODEC
init|=
literal|"Lucene53NormsMetadata"
decl_stmt|;
DECL|field|METADATA_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|METADATA_EXTENSION
init|=
literal|"nvm"
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

