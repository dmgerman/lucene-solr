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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|DocValuesProducer
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
name|StoredFieldsReader
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
name|TermVectorsReader
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
name|AtomicReader
operator|.
name|CoreClosedListener
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
name|AlreadyClosedException
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
name|CompoundFileDirectory
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
name|util
operator|.
name|Accountable
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
name|CloseableThreadLocal
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/** Holds core readers that are shared (unchanged) when  * SegmentReader is cloned or reopened */
end_comment

begin_class
DECL|class|SegmentCoreReaders
specifier|final
class|class
name|SegmentCoreReaders
implements|implements
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|SegmentCoreReaders
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Counts how many other readers share the core objects
comment|// (freqStream, proxStream, tis, etc.) of this reader;
comment|// when coreRef drops to 0, these core objects may be
comment|// closed.  A given instance of SegmentReader may be
comment|// closed, even though it shares core objects with other
comment|// SegmentReaders:
DECL|field|ref
specifier|private
specifier|final
name|AtomicInteger
name|ref
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|fields
specifier|final
name|FieldsProducer
name|fields
decl_stmt|;
DECL|field|normsProducer
specifier|final
name|NormsProducer
name|normsProducer
decl_stmt|;
DECL|field|fieldsReaderOrig
specifier|final
name|StoredFieldsReader
name|fieldsReaderOrig
decl_stmt|;
DECL|field|termVectorsReaderOrig
specifier|final
name|TermVectorsReader
name|termVectorsReaderOrig
decl_stmt|;
DECL|field|cfsReader
specifier|final
name|CompoundFileDirectory
name|cfsReader
decl_stmt|;
comment|// TODO: make a single thread local w/ a
comment|// Thingy class holding fieldsReader, termVectorsReader,
comment|// normsProducer
DECL|field|fieldsReaderLocal
specifier|final
name|CloseableThreadLocal
argument_list|<
name|StoredFieldsReader
argument_list|>
name|fieldsReaderLocal
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|StoredFieldsReader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|StoredFieldsReader
name|initialValue
parameter_list|()
block|{
return|return
name|fieldsReaderOrig
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|termVectorsLocal
specifier|final
name|CloseableThreadLocal
argument_list|<
name|TermVectorsReader
argument_list|>
name|termVectorsLocal
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|TermVectorsReader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TermVectorsReader
name|initialValue
parameter_list|()
block|{
return|return
operator|(
name|termVectorsReaderOrig
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|termVectorsReaderOrig
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|normsLocal
specifier|final
name|CloseableThreadLocal
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|normsLocal
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|coreClosedListeners
specifier|private
specifier|final
name|Set
argument_list|<
name|CoreClosedListener
argument_list|>
name|coreClosedListeners
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|LinkedHashSet
argument_list|<
name|CoreClosedListener
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|SegmentCoreReaders
name|SegmentCoreReaders
parameter_list|(
name|SegmentReader
name|owner
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentCommitInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Codec
name|codec
init|=
name|si
operator|.
name|info
operator|.
name|getCodec
argument_list|()
decl_stmt|;
specifier|final
name|Directory
name|cfsDir
decl_stmt|;
comment|// confusing name: if (cfs) its the cfsdir, otherwise its the segment's directory.
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|si
operator|.
name|info
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
name|cfsDir
operator|=
name|cfsReader
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cfsReader
operator|=
literal|null
expr_stmt|;
name|cfsDir
operator|=
name|dir
expr_stmt|;
block|}
specifier|final
name|FieldInfos
name|fieldInfos
init|=
name|owner
operator|.
name|fieldInfos
decl_stmt|;
specifier|final
name|SegmentReadState
name|segmentReadState
init|=
operator|new
name|SegmentReadState
argument_list|(
name|cfsDir
argument_list|,
name|si
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|PostingsFormat
name|format
init|=
name|codec
operator|.
name|postingsFormat
argument_list|()
decl_stmt|;
comment|// Ask codec for its Fields
name|fields
operator|=
name|format
operator|.
name|fieldsProducer
argument_list|(
name|segmentReadState
argument_list|)
expr_stmt|;
assert|assert
name|fields
operator|!=
literal|null
assert|;
comment|// ask codec for its Norms:
comment|// TODO: since we don't write any norms file if there are no norms,
comment|// kinda jaky to assume the codec handles the case of no norms file at all gracefully?!
if|if
condition|(
name|fieldInfos
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|normsProducer
operator|=
name|codec
operator|.
name|normsFormat
argument_list|()
operator|.
name|normsProducer
argument_list|(
name|segmentReadState
argument_list|)
expr_stmt|;
assert|assert
name|normsProducer
operator|!=
literal|null
assert|;
block|}
else|else
block|{
name|normsProducer
operator|=
literal|null
expr_stmt|;
block|}
name|fieldsReaderOrig
operator|=
name|si
operator|.
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|storedFieldsFormat
argument_list|()
operator|.
name|fieldsReader
argument_list|(
name|cfsDir
argument_list|,
name|si
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldInfos
operator|.
name|hasVectors
argument_list|()
condition|)
block|{
comment|// open term vector files only as needed
name|termVectorsReaderOrig
operator|=
name|si
operator|.
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsReader
argument_list|(
name|cfsDir
argument_list|,
name|si
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termVectorsReaderOrig
operator|=
literal|null
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRefCount
name|int
name|getRefCount
parameter_list|()
block|{
return|return
name|ref
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|incRef
name|void
name|incRef
parameter_list|()
block|{
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|ref
operator|.
name|get
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|ref
operator|.
name|compareAndSet
argument_list|(
name|count
argument_list|,
name|count
operator|+
literal|1
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"SegmentCoreReaders is already closed"
argument_list|)
throw|;
block|}
DECL|method|getNormValues
name|NumericDocValues
name|getNormValues
parameter_list|(
name|FieldInfos
name|infos
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|normFields
init|=
name|normsLocal
operator|.
name|get
argument_list|()
decl_stmt|;
name|NumericDocValues
name|norms
init|=
operator|(
name|NumericDocValues
operator|)
name|normFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norms
operator|!=
literal|null
condition|)
block|{
return|return
name|norms
return|;
block|}
else|else
block|{
name|FieldInfo
name|fi
init|=
name|infos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
operator|||
operator|!
name|fi
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
comment|// Field does not exist or does not index norms
return|return
literal|null
return|;
block|}
assert|assert
name|normsProducer
operator|!=
literal|null
assert|;
name|norms
operator|=
name|normsProducer
operator|.
name|getNorms
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|normFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|norms
argument_list|)
expr_stmt|;
return|return
name|norms
return|;
block|}
block|}
DECL|method|decRef
name|void
name|decRef
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|ref
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//      System.err.println("--- closing core readers");
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|termVectorsLocal
argument_list|,
name|fieldsReaderLocal
argument_list|,
name|normsLocal
argument_list|,
name|fields
argument_list|,
name|termVectorsReaderOrig
argument_list|,
name|fieldsReaderOrig
argument_list|,
name|cfsReader
argument_list|,
name|normsProducer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|th
operator|=
name|throwable
expr_stmt|;
block|}
finally|finally
block|{
name|notifyCoreClosedListeners
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|notifyCoreClosedListeners
specifier|private
name|void
name|notifyCoreClosedListeners
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
synchronized|synchronized
init|(
name|coreClosedListeners
init|)
block|{
for|for
control|(
name|CoreClosedListener
name|listener
range|:
name|coreClosedListeners
control|)
block|{
comment|// SegmentReader uses our instance as its
comment|// coreCacheKey:
try|try
block|{
name|listener
operator|.
name|onClose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
else|else
block|{
name|th
operator|.
name|addSuppressed
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|IOUtils
operator|.
name|reThrowUnchecked
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addCoreClosedListener
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|coreClosedListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|removeCoreClosedListener
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|coreClosedListeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
operator|(
operator|(
name|normsProducer
operator|!=
literal|null
operator|)
condition|?
name|normsProducer
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
operator|+
operator|(
operator|(
name|fields
operator|!=
literal|null
operator|)
condition|?
name|fields
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
operator|+
operator|(
operator|(
name|fieldsReaderOrig
operator|!=
literal|null
operator|)
condition|?
name|fieldsReaderOrig
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
operator|+
operator|(
operator|(
name|termVectorsReaderOrig
operator|!=
literal|null
operator|)
condition|?
name|termVectorsReaderOrig
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
return|;
block|}
block|}
end_class

end_unit

