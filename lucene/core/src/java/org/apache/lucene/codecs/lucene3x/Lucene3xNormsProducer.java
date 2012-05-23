begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|IdentityHashMap
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
name|Map
operator|.
name|Entry
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
name|PerDocProducer
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
name|DocValues
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
name|DocValues
operator|.
name|Source
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
name|DocValues
operator|.
name|Type
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
name|IndexInput
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
name|StringHelper
import|;
end_import

begin_comment
comment|/**  * Reads Lucene 3.x norms format and exposes it via DocValues API  * @lucene.experimental  * @deprecated  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene3xNormsProducer
class|class
name|Lucene3xNormsProducer
extends|extends
name|PerDocProducer
block|{
comment|/** norms header placeholder */
DECL|field|NORMS_HEADER
specifier|static
specifier|final
name|byte
index|[]
name|NORMS_HEADER
init|=
operator|new
name|byte
index|[]
block|{
literal|'N'
block|,
literal|'R'
block|,
literal|'M'
block|,
operator|-
literal|1
block|}
decl_stmt|;
comment|/** Extension of norms file */
DECL|field|NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|NORMS_EXTENSION
init|=
literal|"nrm"
decl_stmt|;
comment|/** Extension of separate norms file */
DECL|field|SEPARATE_NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|SEPARATE_NORMS_EXTENSION
init|=
literal|"s"
decl_stmt|;
DECL|field|norms
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NormsDocValues
argument_list|>
name|norms
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NormsDocValues
argument_list|>
argument_list|()
decl_stmt|;
comment|// any .nrm or .sNN files we have open at any time.
comment|// TODO: just a list, and double-close() separate norms files?
DECL|field|openFiles
specifier|final
name|Set
argument_list|<
name|IndexInput
argument_list|>
name|openFiles
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|IndexInput
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// points to a singleNormFile
DECL|field|singleNormStream
name|IndexInput
name|singleNormStream
decl_stmt|;
DECL|field|maxdoc
specifier|final
name|int
name|maxdoc
decl_stmt|;
comment|// note: just like segmentreader in 3.x, we open up all the files here (including separate norms) up front.
comment|// but we just don't do any seeks or reading yet.
DECL|method|Lucene3xNormsProducer
specifier|public
name|Lucene3xNormsProducer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|FieldInfos
name|fields
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|separateNormsDir
init|=
name|info
operator|.
name|dir
decl_stmt|;
comment|// separate norms are never inside CFS
name|maxdoc
operator|=
name|info
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|String
name|segmentName
init|=
name|info
operator|.
name|name
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
init|=
name|info
operator|.
name|getNormGen
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|long
name|nextNormSeek
init|=
name|NORMS_HEADER
operator|.
name|length
decl_stmt|;
comment|//skip header (header unused for now)
for|for
control|(
name|FieldInfo
name|fi
range|:
name|fields
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|String
name|fileName
init|=
name|getNormFilename
argument_list|(
name|segmentName
argument_list|,
name|normGen
argument_list|,
name|fi
operator|.
name|number
argument_list|)
decl_stmt|;
name|Directory
name|d
init|=
name|hasSeparateNorms
argument_list|(
name|normGen
argument_list|,
name|fi
operator|.
name|number
argument_list|)
condition|?
name|separateNormsDir
else|:
name|dir
decl_stmt|;
comment|// singleNormFile means multiple norms share this file
name|boolean
name|singleNormFile
init|=
name|IndexFileNames
operator|.
name|matchesExtension
argument_list|(
name|fileName
argument_list|,
name|NORMS_EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|normInput
init|=
literal|null
decl_stmt|;
name|long
name|normSeek
decl_stmt|;
if|if
condition|(
name|singleNormFile
condition|)
block|{
name|normSeek
operator|=
name|nextNormSeek
expr_stmt|;
if|if
condition|(
name|singleNormStream
operator|==
literal|null
condition|)
block|{
name|singleNormStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|add
argument_list|(
name|singleNormStream
argument_list|)
expr_stmt|;
block|}
comment|// All norms in the .nrm file can share a single IndexInput since
comment|// they are only used in a synchronized context.
comment|// If this were to change in the future, a clone could be done here.
name|normInput
operator|=
name|singleNormStream
expr_stmt|;
block|}
else|else
block|{
name|normInput
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|add
argument_list|(
name|normInput
argument_list|)
expr_stmt|;
comment|// if the segment was created in 3.2 or after, we wrote the header for sure,
comment|// and don't need to do the sketchy file size check. otherwise, we check
comment|// if the size is exactly equal to maxDoc to detect a headerless file.
comment|// NOTE: remove this check in Lucene 5.0!
name|String
name|version
init|=
name|info
operator|.
name|getVersion
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|isUnversioned
init|=
operator|(
name|version
operator|==
literal|null
operator|||
name|StringHelper
operator|.
name|getVersionComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|version
argument_list|,
literal|"3.2"
argument_list|)
operator|<
literal|0
operator|)
operator|&&
name|normInput
operator|.
name|length
argument_list|()
operator|==
name|maxdoc
decl_stmt|;
if|if
condition|(
name|isUnversioned
condition|)
block|{
name|normSeek
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|normSeek
operator|=
name|NORMS_HEADER
operator|.
name|length
expr_stmt|;
block|}
block|}
name|NormsDocValues
name|norm
init|=
operator|new
name|NormsDocValues
argument_list|(
name|normInput
argument_list|,
name|normSeek
argument_list|)
decl_stmt|;
name|norms
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|norm
argument_list|)
expr_stmt|;
name|nextNormSeek
operator|+=
name|maxdoc
expr_stmt|;
comment|// increment also if some norms are separate
block|}
block|}
comment|// TODO: change to a real check? see LUCENE-3619
assert|assert
name|singleNormStream
operator|==
literal|null
operator|||
name|nextNormSeek
operator|==
name|singleNormStream
operator|.
name|length
argument_list|()
operator|:
name|singleNormStream
operator|!=
literal|null
condition|?
literal|"len: "
operator|+
name|singleNormStream
operator|.
name|length
argument_list|()
operator|+
literal|" expected: "
operator|+
name|nextNormSeek
else|:
literal|"null"
assert|;
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|norms
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
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
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|norms
operator|.
name|clear
argument_list|()
expr_stmt|;
name|openFiles
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getNormFilename
specifier|private
specifier|static
name|String
name|getNormFilename
parameter_list|(
name|String
name|segmentName
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
parameter_list|,
name|int
name|number
parameter_list|)
block|{
if|if
condition|(
name|hasSeparateNorms
argument_list|(
name|normGen
argument_list|,
name|number
argument_list|)
condition|)
block|{
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|segmentName
argument_list|,
name|SEPARATE_NORMS_EXTENSION
operator|+
name|number
argument_list|,
name|normGen
operator|.
name|get
argument_list|(
name|number
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// single file for all norms
return|return
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|NORMS_EXTENSION
argument_list|)
return|;
block|}
block|}
DECL|method|hasSeparateNorms
specifier|private
specifier|static
name|boolean
name|hasSeparateNorms
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
parameter_list|,
name|int
name|number
parameter_list|)
block|{
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Long
name|gen
init|=
name|normGen
operator|.
name|get
argument_list|(
name|number
argument_list|)
decl_stmt|;
return|return
name|gen
operator|!=
literal|null
operator|&&
name|gen
operator|.
name|longValue
argument_list|()
operator|!=
name|SegmentInfo
operator|.
name|NO
return|;
block|}
DECL|class|NormSource
specifier|static
specifier|final
class|class
name|NormSource
extends|extends
name|Source
block|{
DECL|method|NormSource
specifier|protected
name|NormSource
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|FIXED_INTS_8
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
DECL|field|bytes
specifier|final
name|byte
name|bytes
index|[]
decl_stmt|;
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
name|ref
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
name|docID
expr_stmt|;
name|ref
operator|.
name|length
operator|=
literal|1
expr_stmt|;
return|return
name|ref
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|bytes
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
block|}
DECL|class|NormsDocValues
specifier|private
class|class
name|NormsDocValues
extends|extends
name|DocValues
block|{
DECL|field|file
specifier|private
specifier|final
name|IndexInput
name|file
decl_stmt|;
DECL|field|offset
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
DECL|method|NormsDocValues
specifier|public
name|NormsDocValues
parameter_list|(
name|IndexInput
name|normInput
parameter_list|,
name|long
name|normSeek
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|normInput
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|normSeek
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|NormSource
argument_list|(
name|bytes
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectSource
specifier|public
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getSource
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|FIXED_INTS_8
return|;
block|}
DECL|method|bytes
name|byte
index|[]
name|bytes
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|maxdoc
index|]
decl_stmt|;
comment|// some norms share fds
synchronized|synchronized
init|(
name|file
init|)
block|{
name|file
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|file
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// we are done with this file
if|if
condition|(
name|file
operator|!=
name|singleNormStream
condition|)
block|{
name|openFiles
operator|.
name|remove
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

