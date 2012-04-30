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
name|BlockTreeTermsReader
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
name|BlockTreeTermsWriter
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
name|FieldsConsumer
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
name|FieldsProducer
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
name|PostingsFormat
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
name|PostingsReaderBase
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
name|PostingsWriterBase
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
name|DocsEnum
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
name|FieldInfos
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
name|util
operator|.
name|CodecUtil
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
name|util
operator|.
name|fst
operator|.
name|FST
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**   * Lucene 4.0 Postings format.  *<p>  * Files:  *<ul>  *<li><tt>.tim</tt>:<a href="#Termdictionary">Term Dictionary</a></li>  *<li><tt>.tip</tt>:<a href="#Termindex">Term Index</a></li>  *<li><tt>.frq</tt>:<a href="#Frequencies">Frequencies</a></li>  *<li><tt>.prx</tt>:<a href="#Positions">Positions</a></li>  *</ul>  *</p>  *<p>  *<a name="Termdictionary" id="Termdictionary"></a>  *<h3>Term Dictionary</h3>  *<p>The .tim file contains the list of terms in each field, in UTF-8 order,  * along with per-term statistics (such as docfreq) and pointers to the frequencies,  * positions, and skip data in the .frq and .prx files.  *</p>  *<p>The .tim is arranged in blocks: with blocks containing either terms or  * sub-blocks.</p>  *<p>NOTE: The term dictionary can plug into different postings implementations:  * for example the postings writer/reader are actually responsible for encoding   * and decoding the MetadataBlock.</p>  *<ul>  *<!-- TODO: expand on this, its not really correct and doesnt explain sub-blocks etc -->  *<li>TermsDict (.tim) --&gt; Header, DirOffset, PostingsHeader, SkipInterval,  *                               MaxSkipLevels, SkipMinimum, Block<sup>NumBlocks</sup>,  *                               FieldSummary</li>  *<li>Block --&gt; SuffixBlock, StatsBlock, MetadataBlock</li>  *<li>SuffixBlock --&gt; EntryCount, SuffixLength, Byte<sup>SuffixLength</sup></li>  *<li>StatsBlock --&gt; StatsLength,&lt;DocFreq, TotalTermFreq&gt;<sup>EntryCount</sup></li>  *<li>MetadataBlock --&gt; MetaLength,&lt;FreqDelta, SkipDelta?, ProxDelta?&gt;<sup>EntryCount</sup></li>  *<li>FieldSummary --&gt; NumFields,&lt;FieldNumber, NumTerms, RootCodeLength, Byte<sup>RootCodeLength</sup>,  *                            SumDocFreq, DocCount&gt;<sup>NumFields</sup></li>  *<li>Header,PostingsHeader --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *<li>DirOffset --&gt; {@link DataOutput#writeLong Uint64}</li>  *<li>SkipInterval,MaxSkipLevels,SkipMinimum --&gt; {@link DataOutput#writeInt Uint32}</li>  *<li>EntryCount,SuffixLength,StatsLength,DocFreq,MetaLength,SkipDelta,NumFields,  *        FieldNumber,RootCodeLength,DocCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>TotalTermFreq,FreqDelta,ProxDelta,NumTerms,SumTotalTermFreq,SumDocFreq --&gt;   *        {@link DataOutput#writeVLong VLong}</li>  *</ul>  *<p>Notes:</p>  *<ul>  *<li>Header is a {@link CodecUtil#writeHeader CodecHeader} storing the version information  *        for the BlockTree implementation. On the other hand, PostingsHeader stores the version  *        information for the postings reader/writer.</li>  *<li>DirOffset is a pointer to the FieldSummary section.</li>  *<li>SkipInterval is the fraction of TermDocs stored in skip tables. It is used to accelerate   *        {@link DocsEnum#advance(int)}. Larger values result in smaller indexes, greater   *        acceleration, but fewer accelerable cases, while smaller values result in bigger indexes,   *        less acceleration (in case of a small value for MaxSkipLevels) and more accelerable cases.  *</li>  *<li>MaxSkipLevels is the max. number of skip levels stored for each term in the .frq file. A   *        low value results in smaller indexes but less acceleration, a larger value results in   *        slightly larger indexes but greater acceleration. See format of .frq file for more   *        information about skip levels.</li>  *<li>SkipMinimum is the minimum document frequency a term must have in order to write any   *        skip data at all.</li>  *<li>DocFreq is the count of documents which contain the term.</li>  *<li>TotalTermFreq is the total number of occurrences of the term.</li>  *<li>FreqDelta determines the position of this term's TermFreqs within the .frq  *        file. In particular, it is the difference between the position of this term's  *        data in that file and the position of the previous term's data (or zero, for  *        the first term in the block).</li>  *<li>ProxDelta determines the position of this term's TermPositions within the  *        .prx file. In particular, it is the difference between the position of this  *        term's data in that file and the position of the previous term's data (or zero,  *        for the first term in the block. For fields that omit position data, this will  *        be 0 since prox information is not stored.</li>  *<li>SkipDelta determines the position of this term's SkipData within the .frq  *        file. In particular, it is the number of bytes after TermFreqs that the  *        SkipData starts. In other words, it is the length of the TermFreq data.  *        SkipDelta is only stored if DocFreq is not smaller than SkipMinimum.</li>  *<li>FieldNumber is the fields number from {@link FieldInfos}. (.fnm)</li>  *<li>NumTerms is the number of unique terms for the field.</li>  *<li>RootCode points to the root block for the field.</li>  *<li>SumDocFreq is the total number of postings, the number of term-document pairs across  *        the entire field.</li>  *<li>DocCount is the number of documents that have at least one posting for this field.</li>  *</ul>  *<a name="Termindex" id="Termindex"></a>  *<h3>Term Index</h3>  *<p>The .tip file contains an index into the term dictionary, so that it can be   * accessed randomly.</p>  *<ul>  *<li>TermsIndex (.tip) --&gt; Header,&lt;IndexStartFP&gt;<sup>NumFields</sup>,   *                                FSTIndex<sup>NumFields</sup></li>  *<li>Header --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *<li>IndexStartFP --&gt; {@link DataOutput#writeVLong VLong}</li>  *<!-- TODO: better describe FST output here -->  *<li>FSTIndex --&gt; {@link FST FST&lt;byte[]&gt;}</li>  *</ul>  *<p>Notes:</p>  *<ul>  *<li>The .tip file contains a separate FST for each field. Each field's IndexStartFP points  *       to its FST.</li>  *</ul>  *<a name="Frequencies" id="Frequencies"></a>  *<h3>Frequencies</h3>  *<p>The .frq file contains the lists of documents which contain each term, along  * with the frequency of the term in that document (except when frequencies are  * omitted: {@link IndexOptions#DOCS_ONLY}).</p>  *<ul>  *<li>FreqFile (.frq) --&gt;&lt;TermFreqs, SkipData&gt;<sup>TermCount</sup></li>  *<li>TermFreqs --&gt;&lt;TermFreq&gt;<sup>DocFreq</sup></li>  *<li>TermFreq --&gt; DocDelta[, Freq?]</li>  *<li>SkipData --&gt;&lt;&lt;SkipLevelLength, SkipLevel&gt;  *<sup>NumSkipLevels-1</sup>, SkipLevel&gt;&lt;SkipDatum&gt;</li>  *<li>SkipLevel --&gt;&lt;SkipDatum&gt;<sup>DocFreq/(SkipInterval^(Level +  *       1))</sup></li>  *<li>SkipDatum --&gt;  *       DocSkip,PayloadLength?,OffsetLength?,FreqSkip,ProxSkip,SkipChildLevelPointer?</li>  *<li>DocDelta,Freq,DocSkip,PayloadLength,OffsetLength,FreqSkip,ProxSkip --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>SkipChildLevelPointer --&gt; {@link DataOutput#writeVLong VLong}</li>  *</ul>  *<p>TermFreqs are ordered by term (the term is implicit, from the term dictionary).</p>  *<p>TermFreq entries are ordered by increasing document number.</p>  *<p>DocDelta: if frequencies are indexed, this determines both the document  * number and the frequency. In particular, DocDelta/2 is the difference between  * this document number and the previous document number (or zero when this is the  * first document in a TermFreqs). When DocDelta is odd, the frequency is one.  * When DocDelta is even, the frequency is read as another VInt. If frequencies  * are omitted, DocDelta contains the gap (not multiplied by 2) between document  * numbers and no frequency information is stored.</p>  *<p>For example, the TermFreqs for a term which occurs once in document seven  * and three times in document eleven, with frequencies indexed, would be the  * following sequence of VInts:</p>  *<p>15, 8, 3</p>  *<p>If frequencies were omitted ({@link IndexOptions#DOCS_ONLY}) it would be this  * sequence of VInts instead:</p>  *<p>7,4</p>  *<p>DocSkip records the document number before every SkipInterval<sup>th</sup>  * document in TermFreqs. If payloads are disabled for the term's field, then  * DocSkip represents the difference from the previous value in the sequence. If  * payloads are enabled for the term's field, then DocSkip/2 represents the  * difference from the previous value in the sequence. If payloads are enabled and  * DocSkip is odd, then PayloadLength is stored indicating the length of the last  * payload before the SkipInterval<sup>th</sup> document in TermPositions. If offsets  * are enabled, then OffsetLength is stored indicating the length of the last offset  * (endOffset-startOffset).  * FreqSkip and ProxSkip record the position of every SkipInterval<sup>th</sup>  * entry in FreqFile and ProxFile, respectively. File positions are relative to  * the start of TermFreqs and Positions, to the previous SkipDatum in the  * sequence.</p>  *<p>For example, if DocFreq=35 and SkipInterval=16, then there are two SkipData  * entries, containing the 15<sup>th</sup> and 31<sup>st</sup> document numbers  * in TermFreqs. The first FreqSkip names the number of bytes after the beginning  * of TermFreqs that the 16<sup>th</sup> SkipDatum starts, and the second the  * number of bytes after that that the 32<sup>nd</sup> starts. The first ProxSkip  * names the number of bytes after the beginning of Positions that the 16  *<sup>th</sup> SkipDatum starts, and the second the number of bytes after that  * that the 32<sup>nd</sup> starts.</p>  *<p>Each term can have multiple skip levels. The amount of skip levels for a  * term is NumSkipLevels = Min(MaxSkipLevels,  * floor(log(DocFreq/log(SkipInterval)))). The number of SkipData entries for a  * skip level is DocFreq/(SkipInterval^(Level + 1)), whereas the lowest skip level  * is Level=0.<br>  * Example: SkipInterval = 4, MaxSkipLevels = 2, DocFreq = 35. Then skip level 0  * has 8 SkipData entries, containing the 3<sup>rd</sup>, 7<sup>th</sup>,  * 11<sup>th</sup>, 15<sup>th</sup>, 19<sup>th</sup>, 23<sup>rd</sup>,  * 27<sup>th</sup>, and 31<sup>st</sup> document numbers in TermFreqs. Skip level  * 1 has 2 SkipData entries, containing the 15<sup>th</sup> and 31<sup>st</sup>  * document numbers in TermFreqs.<br>  * The SkipData entries on all upper levels&gt; 0 contain a SkipChildLevelPointer  * referencing the corresponding SkipData entry in level-1. In the example has  * entry 15 on level 1 a pointer to entry 15 on level 0 and entry 31 on level 1 a  * pointer to entry 31 on level 0.  *</p>  *<a name="Positions" id="Positions"></a>  *<h3>Positions</h3>  *<p>The .prx file contains the lists of positions that each term occurs at  * within documents. Note that fields omitting positional data do not store  * anything into this file, and if all fields in the index omit positional data  * then the .prx file will not exist.</p>  *<ul>  *<li>ProxFile (.prx) --&gt;&lt;TermPositions&gt;<sup>TermCount</sup></li>  *<li>TermPositions --&gt;&lt;Positions&gt;<sup>DocFreq</sup></li>  *<li>Positions --&gt;&lt;PositionDelta,PayloadLength?,OffsetDelta?,OffsetLength?,PayloadData?&gt;<sup>Freq</sup></li>  *<li>PositionDelta,OffsetDelta,OffsetLength,PayloadLength --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>PayloadData --&gt; {@link DataOutput#writeByte byte}<sup>PayloadLength</sup></li>  *</ul>  *<p>TermPositions are ordered by term (the term is implicit, from the term dictionary).</p>  *<p>Positions entries are ordered by increasing document number (the document  * number is implicit from the .frq file).</p>  *<p>PositionDelta is, if payloads are disabled for the term's field, the  * difference between the position of the current occurrence in the document and  * the previous occurrence (or zero, if this is the first occurrence in this  * document). If payloads are enabled for the term's field, then PositionDelta/2  * is the difference between the current and the previous position. If payloads  * are enabled and PositionDelta is odd, then PayloadLength is stored, indicating  * the length of the payload at the current term position.</p>  *<p>For example, the TermPositions for a term which occurs as the fourth term in  * one document, and as the fifth and ninth term in a subsequent document, would  * be the following sequence of VInts (payloads disabled):</p>  *<p>4, 5, 4</p>  *<p>PayloadData is metadata associated with the current term position. If  * PayloadLength is stored at the current position, then it indicates the length  * of this Payload. If PayloadLength is not stored, then this Payload has the same  * length as the Payload at the previous position.</p>  *<p>OffsetDelta/2 is the difference between this position's startOffset from the  * previous occurrence (or zero, if this is the first occurrence in this document).  * If OffsetDelta is odd, then the length (endOffset-startOffset) differs from the  * previous occurrence and an OffsetLength follows. Offset data is only written for  * {@link IndexOptions#DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS}.</p>  *   *  @lucene.experimental */
end_comment

begin_comment
comment|// TODO: this class could be created by wrapping
end_comment

begin_comment
comment|// BlockTreeTermsDict around Lucene40PostingsBaseFormat; ie
end_comment

begin_comment
comment|// we should not duplicate the code from that class here:
end_comment

begin_class
DECL|class|Lucene40PostingsFormat
specifier|public
class|class
name|Lucene40PostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|minBlockSize
specifier|private
specifier|final
name|int
name|minBlockSize
decl_stmt|;
DECL|field|maxBlockSize
specifier|private
specifier|final
name|int
name|maxBlockSize
decl_stmt|;
DECL|method|Lucene40PostingsFormat
specifier|public
name|Lucene40PostingsFormat
parameter_list|()
block|{
name|this
argument_list|(
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|Lucene40PostingsFormat
specifier|public
name|Lucene40PostingsFormat
parameter_list|(
name|int
name|minBlockSize
parameter_list|,
name|int
name|maxBlockSize
parameter_list|)
block|{
name|super
argument_list|(
literal|"Lucene40"
argument_list|)
expr_stmt|;
name|this
operator|.
name|minBlockSize
operator|=
name|minBlockSize
expr_stmt|;
assert|assert
name|minBlockSize
operator|>
literal|1
assert|;
name|this
operator|.
name|maxBlockSize
operator|=
name|maxBlockSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsWriterBase
name|docs
init|=
operator|new
name|Lucene40PostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|// TODO: should we make the terms index more easily
comment|// pluggable?  Ie so that this codec would record which
comment|// index impl was used, and switch on loading?
comment|// Or... you must make a new Codec for this?
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|docs
argument_list|,
name|minBlockSize
argument_list|,
name|maxBlockSize
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsReaderBase
name|postings
init|=
operator|new
name|Lucene40PostingsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|BlockTreeTermsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|postings
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postings
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Extension of freq postings file */
DECL|field|FREQ_EXTENSION
specifier|static
specifier|final
name|String
name|FREQ_EXTENSION
init|=
literal|"frq"
decl_stmt|;
comment|/** Extension of prox postings file */
DECL|field|PROX_EXTENSION
specifier|static
specifier|final
name|String
name|PROX_EXTENSION
init|=
literal|"prx"
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
name|String
name|segmentSuffix
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
name|Lucene40PostingsReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|BlockTreeTermsReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|+
literal|"(minBlockSize="
operator|+
name|minBlockSize
operator|+
literal|" maxBlockSize="
operator|+
name|maxBlockSize
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

