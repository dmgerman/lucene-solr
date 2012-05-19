begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|Codec
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
name|codecs
operator|.
name|LiveDocsFormat
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
name|codecs
operator|.
name|SegmentInfosFormat
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
name|SegmentInfosReader
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
name|SegmentInfosWriter
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
name|StoredFieldsFormat
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
name|codecs
operator|.
name|TermVectorsFormat
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
name|FieldInfo
operator|.
name|IndexOptions
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
comment|/**  * Lucene 4.0 Segments format.  *<p>  * Files:  *<ul>  *<li><tt>segments.gen</tt>: described in {@link SegmentInfos}  *<li><tt>segments_N</tt>: Format, Codec, Version, NameCounter, SegCount,  *&lt;SegVersion, SegName, SegSize, DelGen, DocStoreOffset, [DocStoreSegment,  *    DocStoreIsCompoundFile], NumField, NormGen<sup>NumField</sup>,   *    IsCompoundFile, DeletionCount, HasProx, SegCodec Diagnostics,   *    HasVectors&gt;<sup>SegCount</sup>, CommitUserData, Checksum  *</ul>  *</p>  * Data types:  *<p>  *<ul>  *<li>Format, NameCounter, SegCount, SegSize, NumField, DocStoreOffset,  *       DeletionCount --&gt; {@link DataOutput#writeInt Int32}</li>  *<li>Version, DelGen, NormGen, Checksum --&gt;   *       {@link DataOutput#writeLong Int64}</li>  *<li>SegVersion, SegName, DocStoreSegment, Codec, SegCodec --&gt;   *       {@link DataOutput#writeString String}</li>  *<li>Diagnostics, CommitUserData --&gt;   *       {@link DataOutput#writeStringStringMap Map&lt;String,String&gt;}</li>  *<li>IsCompoundFile, DocStoreIsCompoundFile, HasProx,  *       HasVectors --&gt; {@link DataOutput#writeByte Int8}</li>  *</ul>  *</p>  * Field Descriptions:  *<p>  *<ul>  *<li>Format is {@link SegmentInfos#FORMAT_4_0}.</li>  *<li>Codec is "Lucene40", its the {@link Codec} that wrote this particular segments file.</li>  *<li>Version counts how often the index has been changed by adding or deleting  *       documents.</li>  *<li>NameCounter is used to generate names for new segment files.</li>  *<li>SegVersion is the code version that created the segment.</li>  *<li>SegName is the name of the segment, and is used as the file name prefix for  *       all of the files that compose the segment's index.</li>  *<li>SegSize is the number of documents contained in the segment index.</li>  *<li>DelGen is the generation count of the deletes file. If this is -1,  *       there are no deletes. Anything above zero means there are deletes   *       stored by {@link LiveDocsFormat}.</li>  *<li>NumField is the size of the array for NormGen, or -1 if there are no  *       NormGens stored.</li>  *<li>NormGen records the generation of the separate norms files. If NumField is  *       -1, there are no normGens stored and all assumed to be -1. The generation   *       then has the same meaning as delGen (above).</li>  *<li>IsCompoundFile records whether the segment is written as a compound file or  *       not. If this is -1, the segment is not a compound file. If it is 1, the segment  *       is a compound file. Else it is 0, which means we check filesystem to see if  *       _X.cfs exists.</li>  *<li>DocStoreOffset, DocStoreSegment, DocStoreIsCompoundFile: If DocStoreOffset  *       is -1, this segment has its own doc store (stored fields values and term  *       vectors) files and DocStoreSegment and DocStoreIsCompoundFile are not stored.  *       In this case all files for  {@link StoredFieldsFormat stored field values} and  *       {@link TermVectorsFormat term vectors} will be stored with this segment.   *       Otherwise, DocStoreSegment is the name of the segment that has the shared doc   *       store files; DocStoreIsCompoundFile is 1 if that segment is stored in compound   *       file format (as a<tt>.cfx</tt> file); and DocStoreOffset is the starting document   *       in the shared doc store files where this segment's documents begin. In this case,   *       this segment does not store its own doc store files but instead shares a single   *       set of these files with other segments.</li>  *<li>Checksum contains the CRC32 checksum of all bytes in the segments_N file up  *       until the checksum. This is used to verify integrity of the file on opening the  *       index.</li>  *<li>DeletionCount records the number of deleted documents in this segment.</li>  *<li>HasProx is 1 if any fields in this segment have position data  *       ({@link IndexOptions#DOCS_AND_FREQS_AND_POSITIONS DOCS_AND_FREQS_AND_POSITIONS} or   *       {@link IndexOptions#DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS});   *       else, it's 0.</li>  *<li>SegCodec is the {@link Codec#getName() name} of the Codec that encoded  *       this segment.</li>  *<li>CommitUserData stores an optional user-supplied opaque  *       Map&lt;String,String&gt; that was passed to {@link IndexWriter#commit(java.util.Map)}   *       or {@link IndexWriter#prepareCommit(java.util.Map)}.</li>  *<li>The Diagnostics Map is privately written by IndexWriter, as a debugging aid,  *       for each segment it creates. It includes metadata like the current Lucene  *       version, OS, Java version, why the segment was created (merge, flush,  *       addIndexes), etc.</li>  *<li>HasVectors is 1 if this segment stores term vectors, else it's 0.</li>  *</ul>  *</p>  *   * @see SegmentInfos  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene40SegmentInfosFormat
specifier|public
class|class
name|Lucene40SegmentInfosFormat
extends|extends
name|SegmentInfosFormat
block|{
DECL|field|reader
specifier|private
specifier|final
name|SegmentInfosReader
name|reader
init|=
operator|new
name|Lucene40SegmentInfosReader
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|SegmentInfosWriter
name|writer
init|=
operator|new
name|Lucene40SegmentInfosWriter
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getSegmentInfosReader
specifier|public
name|SegmentInfosReader
name|getSegmentInfosReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentInfosWriter
specifier|public
name|SegmentInfosWriter
name|getSegmentInfosWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
DECL|field|SI_EXTENSION
specifier|public
specifier|final
specifier|static
name|String
name|SI_EXTENSION
init|=
literal|"si"
decl_stmt|;
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SI_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

