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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|regex
operator|.
name|Pattern
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
name|FieldInfosReader
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
name|Constants
import|;
end_import

begin_comment
comment|/**  * Information about a segment such as it's name, directory, and files related  * to the segment.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentInfo
specifier|public
class|class
name|SegmentInfo
implements|implements
name|Cloneable
block|{
comment|// TODO: remove these from this class, for now this is the representation
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|int
name|NO
init|=
operator|-
literal|1
decl_stmt|;
comment|// e.g. no norms; no deletes;
DECL|field|YES
specifier|public
specifier|static
specifier|final
name|int
name|YES
init|=
literal|1
decl_stmt|;
comment|// e.g. have norms; have deletes;
DECL|field|WITHOUT_GEN
specifier|public
specifier|static
specifier|final
name|int
name|WITHOUT_GEN
init|=
literal|0
decl_stmt|;
comment|// a file name that has no GEN in it.
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|// unique name in dir
comment|// nocommit make me final:
DECL|field|docCount
specifier|public
name|int
name|docCount
decl_stmt|;
comment|// number of docs in seg
DECL|field|dir
specifier|public
specifier|final
name|Directory
name|dir
decl_stmt|;
comment|// where segment resides
comment|// nocommit what other members can we make final?
comment|/*    * Current generation of del file:    * - NO if there are no deletes    * - YES or higher if there are deletes at generation N    */
comment|// nocommit explain that codec need not save this....:
DECL|field|delGen
specifier|private
name|long
name|delGen
decl_stmt|;
comment|/*    * Current generation of each field's norm file. If this array is null,    * means no separate norms. If this array is not null, its values mean:    * - NO says this field has no separate norms    *>= YES says this field has separate norms with the specified generation    */
DECL|field|normGen
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
decl_stmt|;
DECL|field|isCompoundFile
specifier|private
name|boolean
name|isCompoundFile
decl_stmt|;
DECL|field|files
specifier|private
specifier|volatile
name|List
argument_list|<
name|String
argument_list|>
name|files
decl_stmt|;
comment|// Cached list of files that this segment uses
comment|// in the Directory
DECL|field|sizeInBytes
specifier|private
specifier|volatile
name|long
name|sizeInBytes
init|=
operator|-
literal|1
decl_stmt|;
comment|// total byte size of all files (computed on demand)
comment|//TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
DECL|field|docStoreOffset
specifier|private
name|int
name|docStoreOffset
decl_stmt|;
comment|// if this segment shares stored fields& vectors, this
comment|// offset is where in that file this segment's docs begin
comment|//TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
DECL|field|docStoreSegment
specifier|private
name|String
name|docStoreSegment
decl_stmt|;
comment|// name used to derive fields/vectors file we share with
comment|// other segments
comment|//TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
DECL|field|docStoreIsCompoundFile
specifier|private
name|boolean
name|docStoreIsCompoundFile
decl_stmt|;
comment|// whether doc store files are stored in compound file (*.cfx)
comment|// nocommit explain that codec need not save this....:
DECL|field|delCount
specifier|private
name|int
name|delCount
decl_stmt|;
comment|// How many deleted docs in this segment
DECL|field|codec
specifier|private
name|Codec
name|codec
decl_stmt|;
DECL|field|diagnostics
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
decl_stmt|;
comment|// Tracks the Lucene version this segment was created with, since 3.1. Null
comment|// indicates an older than 3.0 index, and it's used to detect a too old index.
comment|// The format expected is "x.y" - "2.x" for pre-3.0 indexes (or null), and
comment|// specific versions afterwards ("3.0", "3.1" etc.).
comment|// see Constants.LUCENE_MAIN_VERSION.
DECL|field|version
specifier|private
name|String
name|version
decl_stmt|;
comment|// NOTE: only used in-RAM by IW to track buffered deletes;
comment|// this is never written to/read from the Directory
DECL|field|bufferedDeletesGen
specifier|private
name|long
name|bufferedDeletesGen
decl_stmt|;
comment|// nocommit why do we have this wimpy ctor...?
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|boolean
name|isCompoundFile
parameter_list|,
name|Codec
name|codec
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|delGen
operator|=
name|NO
expr_stmt|;
name|this
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|this
operator|.
name|docStoreOffset
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|docStoreSegment
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
name|delCount
operator|=
literal|0
expr_stmt|;
name|version
operator|=
name|Constants
operator|.
name|LUCENE_MAIN_VERSION
expr_stmt|;
block|}
DECL|method|setDiagnostics
name|void
name|setDiagnostics
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
parameter_list|)
block|{
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
block|}
DECL|method|getDiagnostics
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
comment|/**    * Construct a new complete SegmentInfo instance from input.    *<p>Note: this is public only to allow access from    * the codecs package.</p>    */
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|version
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|docCount
parameter_list|,
name|int
name|docStoreOffset
parameter_list|,
name|String
name|docStoreSegment
parameter_list|,
name|boolean
name|docStoreIsCompoundFile
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
parameter_list|,
name|boolean
name|isCompoundFile
parameter_list|,
name|int
name|delCount
parameter_list|,
name|Codec
name|codec
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|delGen
operator|=
name|NO
expr_stmt|;
name|this
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|this
operator|.
name|docStoreSegment
operator|=
name|docStoreSegment
expr_stmt|;
name|this
operator|.
name|docStoreIsCompoundFile
operator|=
name|docStoreIsCompoundFile
expr_stmt|;
name|this
operator|.
name|normGen
operator|=
name|normGen
expr_stmt|;
name|this
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|this
operator|.
name|delCount
operator|=
name|delCount
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
block|}
comment|/**    * Returns total size in bytes of all of files used by this segment    */
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sizeInBytes
operator|==
operator|-
literal|1
condition|)
block|{
name|long
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fileName
range|:
name|files
argument_list|()
control|)
block|{
name|sum
operator|+=
name|dir
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|sizeInBytes
operator|=
name|sum
expr_stmt|;
block|}
return|return
name|sizeInBytes
return|;
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
comment|// Cases:
comment|//
comment|//   delGen == NO: this means this segment does not have deletions yet
comment|//   delGen>= YES: this means this segment has deletions
comment|//
return|return
name|delGen
operator|!=
name|NO
return|;
block|}
DECL|method|advanceDelGen
name|void
name|advanceDelGen
parameter_list|()
block|{
if|if
condition|(
name|delGen
operator|==
name|NO
condition|)
block|{
name|delGen
operator|=
name|YES
expr_stmt|;
block|}
else|else
block|{
name|delGen
operator|++
expr_stmt|;
block|}
name|clearFilesCache
argument_list|()
expr_stmt|;
block|}
DECL|method|getNextDelGen
specifier|public
name|long
name|getNextDelGen
parameter_list|()
block|{
if|if
condition|(
name|delGen
operator|==
name|NO
condition|)
block|{
return|return
name|YES
return|;
block|}
else|else
block|{
return|return
name|delGen
operator|+
literal|1
return|;
block|}
block|}
DECL|method|clearDelGen
name|void
name|clearDelGen
parameter_list|()
block|{
name|delGen
operator|=
name|NO
expr_stmt|;
name|clearFilesCache
argument_list|()
expr_stmt|;
block|}
comment|// nocommit this is dangerous... because we lose the codec's customzied class...
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SegmentInfo
name|clone
parameter_list|()
block|{
specifier|final
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|clonedNormGen
decl_stmt|;
if|if
condition|(
name|normGen
operator|!=
literal|null
condition|)
block|{
name|clonedNormGen
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|normGen
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|clonedNormGen
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|clonedNormGen
operator|=
literal|null
expr_stmt|;
block|}
name|SegmentInfo
name|newInfo
init|=
operator|new
name|SegmentInfo
argument_list|(
name|dir
argument_list|,
name|version
argument_list|,
name|name
argument_list|,
name|docCount
argument_list|,
name|docStoreOffset
argument_list|,
name|docStoreSegment
argument_list|,
name|docStoreIsCompoundFile
argument_list|,
name|clonedNormGen
argument_list|,
name|isCompoundFile
argument_list|,
name|delCount
argument_list|,
name|codec
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|diagnostics
argument_list|)
argument_list|)
decl_stmt|;
name|newInfo
operator|.
name|setDelGen
argument_list|(
name|delGen
argument_list|)
expr_stmt|;
return|return
name|newInfo
return|;
block|}
comment|/**    * @deprecated separate norms are not supported in>= 4.0    */
annotation|@
name|Deprecated
DECL|method|hasSeparateNorms
name|boolean
name|hasSeparateNorms
parameter_list|()
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
else|else
block|{
for|for
control|(
name|long
name|fieldNormGen
range|:
name|normGen
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|fieldNormGen
operator|>=
name|YES
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Mark whether this segment is stored as a compound file.    *    * @param isCompoundFile true if this is a compound file;    * else, false    */
DECL|method|setUseCompoundFile
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|isCompoundFile
parameter_list|)
block|{
name|this
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|clearFilesCache
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns true if this segment is stored as a compound    * file; else, false.    */
DECL|method|getUseCompoundFile
specifier|public
name|boolean
name|getUseCompoundFile
parameter_list|()
block|{
return|return
name|isCompoundFile
return|;
block|}
DECL|method|getDelCount
specifier|public
name|int
name|getDelCount
parameter_list|()
block|{
return|return
name|delCount
return|;
block|}
DECL|method|setDelCount
name|void
name|setDelCount
parameter_list|(
name|int
name|delCount
parameter_list|)
block|{
name|this
operator|.
name|delCount
operator|=
name|delCount
expr_stmt|;
assert|assert
name|delCount
operator|<=
name|docCount
assert|;
block|}
DECL|method|setDelGen
specifier|public
name|void
name|setDelGen
parameter_list|(
name|long
name|delGen
parameter_list|)
block|{
name|this
operator|.
name|delGen
operator|=
name|delGen
expr_stmt|;
block|}
comment|/**    * @deprecated shared doc stores are not supported in>= 4.0    */
annotation|@
name|Deprecated
DECL|method|getDocStoreOffset
specifier|public
name|int
name|getDocStoreOffset
parameter_list|()
block|{
comment|// TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
return|return
name|docStoreOffset
return|;
block|}
comment|/**    * @deprecated shared doc stores are not supported in>= 4.0    */
annotation|@
name|Deprecated
DECL|method|getDocStoreIsCompoundFile
specifier|public
name|boolean
name|getDocStoreIsCompoundFile
parameter_list|()
block|{
comment|// TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
return|return
name|docStoreIsCompoundFile
return|;
block|}
comment|/**    * @deprecated shared doc stores are not supported in>= 4.0    */
annotation|@
name|Deprecated
DECL|method|setDocStore
name|void
name|setDocStore
parameter_list|(
name|int
name|offset
parameter_list|,
name|String
name|segment
parameter_list|,
name|boolean
name|isCompoundFile
parameter_list|)
block|{
comment|// TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
name|docStoreOffset
operator|=
name|offset
expr_stmt|;
name|docStoreSegment
operator|=
name|segment
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|clearFilesCache
argument_list|()
expr_stmt|;
block|}
comment|/**    * @deprecated shared doc stores are not supported in>= 4.0    */
annotation|@
name|Deprecated
DECL|method|getDocStoreSegment
specifier|public
name|String
name|getDocStoreSegment
parameter_list|()
block|{
comment|// TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
return|return
name|docStoreSegment
return|;
block|}
comment|/** Can only be called once. */
DECL|method|setCodec
specifier|public
name|void
name|setCodec
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|codec
operator|==
literal|null
assert|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"segmentCodecs must be non-null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
block|}
DECL|method|getCodec
specifier|public
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
comment|// noocmmit nuke this and require, once again, that a codec puts PRECISELY the files that exist into the file set...
DECL|method|findMatchingFiles
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|findMatchingFiles
parameter_list|(
name|String
name|segmentName
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|namesOrPatterns
parameter_list|)
block|{
comment|// nocommit need more efficient way to do this?
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|existingFiles
decl_stmt|;
try|try
block|{
name|existingFiles
operator|=
name|dir
operator|.
name|listAll
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// nocommit maybe just throw IOE...? not sure how far up we'd have to change sigs...
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Pattern
argument_list|>
name|compiledPatterns
init|=
operator|new
name|ArrayList
argument_list|<
name|Pattern
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nameOrPattern
range|:
name|namesOrPatterns
control|)
block|{
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
comment|// nocommit hack -- remove (needed now because si's -1 gen will return null file name):
if|if
condition|(
name|nameOrPattern
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|exists
operator|=
name|dir
operator|.
name|fileExists
argument_list|(
name|nameOrPattern
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// nocommit maybe just throw IOE...?
comment|// Ignore
block|}
if|if
condition|(
name|exists
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|nameOrPattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// nocommit can i test whether the regexp matches only 1 string...?  maybe... make into autamaton and union them all....?
name|compiledPatterns
operator|.
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|nameOrPattern
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// nocommit this is DOG SLOW: try TestBoolean2 w/ seed 1F7F3638C719C665
for|for
control|(
name|String
name|file
range|:
name|existingFiles
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|segmentName
argument_list|)
condition|)
block|{
for|for
control|(
name|Pattern
name|pattern
range|:
name|compiledPatterns
control|)
block|{
if|if
condition|(
name|pattern
operator|.
name|matcher
argument_list|(
name|file
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|files
return|;
block|}
comment|/*    * Return all files referenced by this SegmentInfo.  The    * returns List is a locally cached List so you should not    * modify it.    */
DECL|method|files
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|files
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|files
operator|==
literal|null
condition|)
block|{
comment|// nocommit can we remove this again....?
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fileSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|codec
operator|.
name|files
argument_list|(
name|this
argument_list|,
name|fileSet
argument_list|)
expr_stmt|;
name|files
operator|=
name|findMatchingFiles
argument_list|(
name|name
argument_list|,
name|dir
argument_list|,
name|fileSet
argument_list|)
expr_stmt|;
block|}
return|return
name|files
return|;
block|}
comment|/* Called whenever any change is made that affects which    * files this segment has. */
comment|// nocommit make private again
DECL|method|clearFilesCache
name|void
name|clearFilesCache
parameter_list|()
block|{
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
name|files
operator|=
literal|null
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
name|dir
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/** Used for debugging.  Format may suddenly change.    *    *<p>Current format looks like    *<code>_a(3.1):c45/4->_1</code>, which means the segment's    *  name is<code>_a</code>; it was created with Lucene 3.1 (or    *  '?' if it's unknown); it's using compound file    *  format (would be<code>C</code> if not compound); it    *  has 45 documents; it has 4 deletions (this part is    *  left off when there are no deletions); it's using the    *  shared doc stores named<code>_1</code> (this part is    *  left off if doc stores are private).</p>    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|pendingDelCount
parameter_list|)
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|version
operator|==
literal|null
condition|?
literal|"?"
else|:
name|version
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|char
name|cfs
init|=
name|getUseCompoundFile
argument_list|()
condition|?
literal|'c'
else|:
literal|'C'
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|cfs
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|dir
operator|!=
name|dir
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'x'
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|int
name|delCount
init|=
name|getDelCount
argument_list|()
operator|+
name|pendingDelCount
decl_stmt|;
if|if
condition|(
name|delCount
operator|!=
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|delCount
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|"->"
argument_list|)
operator|.
name|append
argument_list|(
name|docStoreSegment
argument_list|)
expr_stmt|;
if|if
condition|(
name|docStoreIsCompoundFile
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'c'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|.
name|append
argument_list|(
literal|'C'
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
operator|.
name|append
argument_list|(
name|docStoreOffset
argument_list|)
expr_stmt|;
block|}
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** We consider another SegmentInfo instance equal if it    *  has the same dir and same name. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|instanceof
name|SegmentInfo
condition|)
block|{
specifier|final
name|SegmentInfo
name|other
init|=
operator|(
name|SegmentInfo
operator|)
name|obj
decl_stmt|;
return|return
name|other
operator|.
name|dir
operator|==
name|dir
operator|&&
name|other
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|dir
operator|.
name|hashCode
argument_list|()
operator|+
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Used by DefaultSegmentInfosReader to upgrade a 3.0 segment to record its    * version is "3.0". This method can be removed when we're not required to    * support 3x indexes anymore, e.g. in 5.0.    *<p>    *<b>NOTE:</b> this method is used for internal purposes only - you should    * not modify the version of a SegmentInfo, or it may result in unexpected    * exceptions thrown when you attempt to open the index.    *    * @lucene.internal    */
DECL|method|setVersion
specifier|public
name|void
name|setVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|/** Returns the version of the code which wrote the segment. */
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|getBufferedDeletesGen
name|long
name|getBufferedDeletesGen
parameter_list|()
block|{
return|return
name|bufferedDeletesGen
return|;
block|}
DECL|method|setBufferedDeletesGen
name|void
name|setBufferedDeletesGen
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|bufferedDeletesGen
operator|=
name|v
expr_stmt|;
block|}
comment|/** @lucene.internal */
DECL|method|getDelGen
specifier|public
name|long
name|getDelGen
parameter_list|()
block|{
return|return
name|delGen
return|;
block|}
comment|/** @lucene.internal */
DECL|method|getNormGen
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|getNormGen
parameter_list|()
block|{
return|return
name|normGen
return|;
block|}
block|}
end_class

end_unit

