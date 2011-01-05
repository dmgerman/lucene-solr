begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
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
name|FieldInfo
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
name|FieldInfos
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
name|util
operator|.
name|automaton
operator|.
name|fst
operator|.
name|Builder
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
name|automaton
operator|.
name|fst
operator|.
name|FST
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
name|automaton
operator|.
name|fst
operator|.
name|PositiveIntOutputs
import|;
end_import

begin_comment
comment|/**  * Selects index terms according to provided pluggable  * IndexTermPolicy, and stores them in a prefix trie that's  * loaded entirely in RAM stored as an FST.  This terms  * index only supports unsigned byte term sort order  * (unicode codepoint order when the bytes are UTF8).  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|VariableGapTermsIndexWriter
specifier|public
class|class
name|VariableGapTermsIndexWriter
extends|extends
name|TermsIndexWriterBase
block|{
DECL|field|out
specifier|protected
specifier|final
name|IndexOutput
name|out
decl_stmt|;
comment|/** Extension of terms index file */
DECL|field|TERMS_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_INDEX_EXTENSION
init|=
literal|"tiv"
decl_stmt|;
DECL|field|CODEC_NAME
specifier|final
specifier|static
name|String
name|CODEC_NAME
init|=
literal|"VARIABLE_GAP_TERMS_INDEX"
decl_stmt|;
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|FSTFieldWriter
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|FSTFieldWriter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
comment|// unread
DECL|field|termsOut
specifier|private
name|IndexOutput
name|termsOut
decl_stmt|;
DECL|field|policy
specifier|private
specifier|final
name|IndexTermSelector
name|policy
decl_stmt|;
comment|/** @lucene.experimental */
DECL|class|IndexTermSelector
specifier|public
specifier|static
specifier|abstract
class|class
name|IndexTermSelector
block|{
comment|// Called sequentially on every term being written,
comment|// returning true if this term should be indexed
DECL|method|isIndexTerm
specifier|public
specifier|abstract
name|boolean
name|isIndexTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|docFreq
parameter_list|)
function_decl|;
block|}
comment|/** Same policy as {@link FixedGapTermsIndexWriter} */
DECL|class|EveryNTermSelector
specifier|public
specifier|static
specifier|final
class|class
name|EveryNTermSelector
extends|extends
name|IndexTermSelector
block|{
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|interval
specifier|private
specifier|final
name|int
name|interval
decl_stmt|;
DECL|method|EveryNTermSelector
specifier|public
name|EveryNTermSelector
parameter_list|(
name|int
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
comment|// First term is first indexed term:
name|count
operator|=
name|interval
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isIndexTerm
specifier|public
name|boolean
name|isIndexTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|docFreq
parameter_list|)
block|{
if|if
condition|(
name|count
operator|>=
name|interval
condition|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|count
operator|++
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/** Sets an index term when docFreq>= docFreqThresh, or    *  every interval terms.  This should reduce seek time    *  to high docFreq terms.  */
DECL|class|EveryNOrDocFreqTermSelector
specifier|public
specifier|static
specifier|final
class|class
name|EveryNOrDocFreqTermSelector
extends|extends
name|IndexTermSelector
block|{
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|docFreqThresh
specifier|private
specifier|final
name|int
name|docFreqThresh
decl_stmt|;
DECL|field|interval
specifier|private
specifier|final
name|int
name|interval
decl_stmt|;
DECL|method|EveryNOrDocFreqTermSelector
specifier|public
name|EveryNOrDocFreqTermSelector
parameter_list|(
name|int
name|docFreqThresh
parameter_list|,
name|int
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|docFreqThresh
operator|=
name|docFreqThresh
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isIndexTerm
specifier|public
name|boolean
name|isIndexTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|docFreq
parameter_list|)
block|{
if|if
condition|(
name|docFreq
operator|>=
name|docFreqThresh
operator|||
name|count
operator|>=
name|interval
condition|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|count
operator|++
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
comment|// TODO: it'd be nice to let the FST builder prune based
comment|// on term count of each node (the prune1/prune2 that it
comment|// accepts), and build the index based on that.  This
comment|// should result in a more compact terms index, more like
comment|// a prefix trie than the other selectors, because it
comment|// only stores enough leading bytes to get down to N
comment|// terms that may complete that prefix.  It becomes
comment|// "deeper" when terms are dense, and "shallow" when they
comment|// are less dense.
comment|//
comment|// However, it's not easy to make that work this this
comment|// API, because that pruning doesn't immediately know on
comment|// seeing each term whether that term will be a seek point
comment|// or not.  It requires some non-causality in the API, ie
comment|// only on seeing some number of future terms will the
comment|// builder decide which past terms are seek points.
comment|// Somehow the API'd need to be able to return a "I don't
comment|// know" value, eg like a Future, which only later on is
comment|// flipped (frozen) to true or false.
comment|//
comment|// We could solve this with a 2-pass approach, where the
comment|// first pass would build an FSA (no outputs) solely to
comment|// determine which prefixes are the 'leaves' in the
comment|// pruning. The 2nd pass would then look at this prefix
comment|// trie to mark the seek points and build the FST mapping
comment|// to the true output.
comment|//
comment|// But, one downside to this approach is that it'd result
comment|// in uneven index term selection.  EG with prune1=10, the
comment|// resulting index terms could be as frequent as every 10
comment|// terms or as rare as every<maxArcCount> * 10 (eg 2560),
comment|// in the extremes.
DECL|method|VariableGapTermsIndexWriter
specifier|public
name|VariableGapTermsIndexWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|IndexTermSelector
name|policy
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|indexFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
name|state
operator|.
name|codecId
argument_list|,
name|TERMS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|indexFileName
argument_list|)
expr_stmt|;
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|indexFileName
argument_list|)
expr_stmt|;
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
name|writeHeader
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeHeader
specifier|protected
name|void
name|writeHeader
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|// Placeholder for dir offset
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTermsOutput
specifier|public
name|void
name|setTermsOutput
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
block|{
name|this
operator|.
name|termsOut
operator|=
name|termsOut
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|FieldWriter
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("VGW: field=" + field.name);
name|FSTFieldWriter
name|writer
init|=
operator|new
name|FSTFieldWriter
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
comment|/** NOTE: if your codec does not sort in unicode code    *  point order, you must override this method, to simply    *  return indexedTerm.length. */
DECL|method|indexedTermPrefixLength
specifier|protected
name|int
name|indexedTermPrefixLength
parameter_list|(
specifier|final
name|BytesRef
name|priorTerm
parameter_list|,
specifier|final
name|BytesRef
name|indexedTerm
parameter_list|)
block|{
comment|// As long as codec sorts terms in unicode codepoint
comment|// order, we can safely strip off the non-distinguishing
comment|// suffix to save RAM in the loaded terms index.
specifier|final
name|int
name|idxTermOffset
init|=
name|indexedTerm
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|priorTermOffset
init|=
name|priorTerm
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|priorTerm
operator|.
name|length
argument_list|,
name|indexedTerm
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|byteIdx
init|=
literal|0
init|;
name|byteIdx
operator|<
name|limit
condition|;
name|byteIdx
operator|++
control|)
block|{
if|if
condition|(
name|priorTerm
operator|.
name|bytes
index|[
name|priorTermOffset
operator|+
name|byteIdx
index|]
operator|!=
name|indexedTerm
operator|.
name|bytes
index|[
name|idxTermOffset
operator|+
name|byteIdx
index|]
condition|)
block|{
return|return
name|byteIdx
operator|+
literal|1
return|;
block|}
block|}
return|return
name|Math
operator|.
name|min
argument_list|(
literal|1
operator|+
name|priorTerm
operator|.
name|length
argument_list|,
name|indexedTerm
operator|.
name|length
argument_list|)
return|;
block|}
DECL|class|FSTFieldWriter
specifier|private
class|class
name|FSTFieldWriter
extends|extends
name|FieldWriter
block|{
DECL|field|fstBuilder
specifier|private
specifier|final
name|Builder
argument_list|<
name|Long
argument_list|>
name|fstBuilder
decl_stmt|;
DECL|field|fstOutputs
specifier|private
specifier|final
name|PositiveIntOutputs
name|fstOutputs
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numIndexTerms
name|int
name|numIndexTerms
decl_stmt|;
DECL|field|fst
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
decl_stmt|;
DECL|field|indexStart
specifier|final
name|long
name|indexStart
decl_stmt|;
DECL|field|lastTerm
specifier|private
specifier|final
name|BytesRef
name|lastTerm
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|first
specifier|private
name|boolean
name|first
init|=
literal|true
decl_stmt|;
DECL|method|FSTFieldWriter
specifier|public
name|FSTFieldWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|fstOutputs
operator|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fstBuilder
operator|=
operator|new
name|Builder
argument_list|<
name|Long
argument_list|>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|fstOutputs
argument_list|)
expr_stmt|;
name|indexStart
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|//System.out.println("VGW: field=" + fieldInfo.name);
comment|// Always put empty string in
name|fstBuilder
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|,
name|fstOutputs
operator|.
name|get
argument_list|(
name|termsOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIndexTerm
specifier|public
name|boolean
name|checkIndexTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|int
name|docFreq
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|policy
operator|.
name|isIndexTerm
argument_list|(
name|text
argument_list|,
name|docFreq
argument_list|)
operator|||
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
comment|//System.out.println("VGW: index term=" + text.utf8ToString() + " fp=" + termsOut.getFilePointer());
specifier|final
name|int
name|lengthSave
init|=
name|text
operator|.
name|length
decl_stmt|;
name|text
operator|.
name|length
operator|=
name|indexedTermPrefixLength
argument_list|(
name|lastTerm
argument_list|,
name|text
argument_list|)
expr_stmt|;
try|try
block|{
name|fstBuilder
operator|.
name|add
argument_list|(
name|text
argument_list|,
name|fstOutputs
operator|.
name|get
argument_list|(
name|termsOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|text
operator|.
name|length
operator|=
name|lengthSave
expr_stmt|;
block|}
name|lastTerm
operator|.
name|copy
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|//System.out.println("VGW: not index term=" + text.utf8ToString() + " fp=" + termsOut.getFilePointer());
name|lastTerm
operator|.
name|copy
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|fst
operator|=
name|fstBuilder
operator|.
name|finish
argument_list|()
expr_stmt|;
if|if
condition|(
name|fst
operator|!=
literal|null
condition|)
block|{
name|fst
operator|.
name|save
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|dirStart
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|nonNullFieldCount
init|=
literal|0
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|FSTFieldWriter
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|fst
operator|!=
literal|null
condition|)
block|{
name|nonNullFieldCount
operator|++
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|nonNullFieldCount
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|FSTFieldWriter
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|fst
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|indexStart
argument_list|)
expr_stmt|;
block|}
block|}
name|writeTrailer
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTrailer
specifier|protected
name|void
name|writeTrailer
parameter_list|(
name|long
name|dirStart
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

