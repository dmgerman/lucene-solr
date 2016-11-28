begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

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
name|DocIDMerger
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
name|MergeState
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
name|NumericDocValues
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
comment|/**   * Abstract API that consumes normalization values.    * Concrete implementations of this  * actually do "something" with the norms (write it into  * the index in a specific format).  *<p>  * The lifecycle is:  *<ol>  *<li>NormsConsumer is created by   *       {@link NormsFormat#normsConsumer(SegmentWriteState)}.  *<li>{@link #addNormsField} is called for each field with  *       normalization values. The API is a "pull" rather  *       than "push", and the implementation is free to iterate over the   *       values multiple times ({@link Iterable#iterator()}).  *<li>After all fields are added, the consumer is {@link #close}d.  *</ol>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NormsConsumer
specifier|public
specifier|abstract
class|class
name|NormsConsumer
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|NormsConsumer
specifier|protected
name|NormsConsumer
parameter_list|()
block|{}
comment|/**    * Writes normalization values for a field.    * @param field field information    * @param normsProducer NormsProducer of the numeric norm values    * @throws IOException if an I/O error occurred.    */
DECL|method|addNormsField
specifier|public
specifier|abstract
name|void
name|addNormsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|NormsProducer
name|normsProducer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Merges in the fields from the readers in     *<code>mergeState</code>. The default implementation     *  calls {@link #mergeNormsField} for each field,    *  filling segments with missing norms for the field with zeros.     *  Implementations can override this method     *  for more sophisticated merging (bulk-byte copying, etc). */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|NormsProducer
name|normsProducer
range|:
name|mergeState
operator|.
name|normsProducers
control|)
block|{
if|if
condition|(
name|normsProducer
operator|!=
literal|null
condition|)
block|{
name|normsProducer
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|FieldInfo
name|mergeFieldInfo
range|:
name|mergeState
operator|.
name|mergeFieldInfos
control|)
block|{
if|if
condition|(
name|mergeFieldInfo
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|mergeNormsField
argument_list|(
name|mergeFieldInfo
argument_list|,
name|mergeState
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Tracks state of one numeric sub-reader that we are merging */
DECL|class|NumericDocValuesSub
specifier|private
specifier|static
class|class
name|NumericDocValuesSub
extends|extends
name|DocIDMerger
operator|.
name|Sub
block|{
DECL|field|values
specifier|private
specifier|final
name|NumericDocValues
name|values
decl_stmt|;
DECL|method|NumericDocValuesSub
specifier|public
name|NumericDocValuesSub
parameter_list|(
name|MergeState
operator|.
name|DocMap
name|docMap
parameter_list|,
name|NumericDocValues
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|docMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
assert|assert
name|values
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
assert|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|values
operator|.
name|nextDoc
argument_list|()
return|;
block|}
block|}
comment|/**    * Merges the norms from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addNormsField}, passing    * an Iterable that merges and filters deleted documents on the fly.    */
DECL|method|mergeNormsField
specifier|public
name|void
name|mergeNormsField
parameter_list|(
specifier|final
name|FieldInfo
name|mergeFieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: try to share code with default merge of DVConsumer by passing MatchAllBits ?
name|addNormsField
argument_list|(
name|mergeFieldInfo
argument_list|,
operator|new
name|NormsProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldInfo
operator|!=
name|mergeFieldInfo
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"wrong fieldInfo"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|NumericDocValuesSub
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
assert|assert
name|mergeState
operator|.
name|docMaps
operator|.
name|length
operator|==
name|mergeState
operator|.
name|docValuesProducers
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mergeState
operator|.
name|docValuesProducers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|NumericDocValues
name|norms
init|=
literal|null
decl_stmt|;
name|NormsProducer
name|normsProducer
init|=
name|mergeState
operator|.
name|normsProducers
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|normsProducer
operator|!=
literal|null
condition|)
block|{
name|FieldInfo
name|readerFieldInfo
init|=
name|mergeState
operator|.
name|fieldInfos
index|[
name|i
index|]
operator|.
name|fieldInfo
argument_list|(
name|mergeFieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerFieldInfo
operator|!=
literal|null
operator|&&
name|readerFieldInfo
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|norms
operator|=
name|normsProducer
operator|.
name|getNorms
argument_list|(
name|readerFieldInfo
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|norms
operator|!=
literal|null
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesSub
argument_list|(
name|mergeState
operator|.
name|docMaps
index|[
name|i
index|]
argument_list|,
name|norms
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|DocIDMerger
argument_list|<
name|NumericDocValuesSub
argument_list|>
name|docIDMerger
init|=
operator|new
name|DocIDMerger
argument_list|<>
argument_list|(
name|subs
argument_list|,
name|mergeState
operator|.
name|needsIndexSort
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|NumericDocValuesSub
name|current
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|current
operator|=
name|docIDMerger
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
else|else
block|{
name|docID
operator|=
name|current
operator|.
name|mappedDocID
expr_stmt|;
block|}
return|return
name|docID
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|values
operator|.
name|longValue
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
block|{                     }
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{                     }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

