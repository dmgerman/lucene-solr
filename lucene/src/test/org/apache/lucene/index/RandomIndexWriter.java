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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
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
name|document
operator|.
name|Document
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
name|codecs
operator|.
name|intblock
operator|.
name|IntBlockCodec
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
name|codecs
operator|.
name|preflex
operator|.
name|PreFlexCodec
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
name|codecs
operator|.
name|pulsing
operator|.
name|PulsingCodec
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
name|codecs
operator|.
name|sep
operator|.
name|SepCodec
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
name|codecs
operator|.
name|standard
operator|.
name|StandardCodec
import|;
end_import

begin_comment
comment|/** Silly class that randomizes the indexing experience.  EG  *  it may swap in a different merge policy/scheduler; may  *  commit periodically; may or may not optimize in the end,  *  may flush by doc count instead of RAM, etc.   */
end_comment

begin_class
DECL|class|RandomIndexWriter
specifier|public
class|class
name|RandomIndexWriter
implements|implements
name|Closeable
block|{
DECL|field|w
specifier|public
name|IndexWriter
name|w
decl_stmt|;
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
DECL|field|flushAt
name|int
name|flushAt
decl_stmt|;
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|r
operator|=
name|r
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setTermIndexInterval
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|LogMergePolicy
condition|)
block|{
name|LogMergePolicy
name|logmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|c
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|logmp
operator|.
name|setUseCompoundDocStore
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|logmp
operator|.
name|setUseCompoundFile
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|logmp
operator|.
name|setCalibrateSizeByDeletes
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|setReaderPooling
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|setCodecProvider
argument_list|(
operator|new
name|RandomCodecProvider
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|flushAt
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|docCount
operator|++
operator|==
name|flushAt
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|flushAt
operator|+=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addIndexes
specifier|public
name|void
name|addIndexes
parameter_list|(
name|Directory
modifier|...
name|dirs
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|w
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDocuments
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|w
operator|.
name|maxDoc
argument_list|()
return|;
block|}
DECL|method|getReader
specifier|public
name|IndexReader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
name|w
operator|.
name|getReader
argument_list|()
return|;
block|}
else|else
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|IndexReader
operator|.
name|open
argument_list|(
name|w
operator|.
name|getDirectory
argument_list|()
argument_list|,
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|2
condition|)
block|{
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|optimize
specifier|public
name|void
name|optimize
parameter_list|()
throws|throws
name|IOException
block|{
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
DECL|class|RandomCodecProvider
class|class
name|RandomCodecProvider
extends|extends
name|CodecProvider
block|{
DECL|field|codec
specifier|final
name|String
name|codec
decl_stmt|;
DECL|method|RandomCodecProvider
name|RandomCodecProvider
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|register
argument_list|(
operator|new
name|StandardCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|IntBlockCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PreFlexCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PulsingCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|SepCodec
argument_list|()
argument_list|)
expr_stmt|;
name|codec
operator|=
name|CodecProvider
operator|.
name|CORE_CODECS
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|CodecProvider
operator|.
name|CORE_CODECS
operator|.
name|length
argument_list|)
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriter
specifier|public
name|Codec
name|getWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
block|{
return|return
name|lookup
argument_list|(
name|codec
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

