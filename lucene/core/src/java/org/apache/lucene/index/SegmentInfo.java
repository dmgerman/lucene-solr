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
name|Collection
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
name|TrackingDirectoryWrapper
import|;
end_import

begin_comment
comment|/**  * Information about a segment such as it's name, directory, and files related  * to the segment.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentInfo
specifier|public
specifier|final
class|class
name|SegmentInfo
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
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|// unique name in dir
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
comment|/*    * Current generation of each field's norm file. If this array is null,    * means no separate norms. If this array is not null, its values mean:    * - NO says this field has no separate norms    *>= YES says this field has separate norms with the specified generation    */
DECL|field|normGen
specifier|private
specifier|final
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
specifier|final
name|int
name|docStoreOffset
decl_stmt|;
comment|// if this segment shares stored fields& vectors, this
comment|// offset is where in that file this segment's docs begin
comment|//TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
DECL|field|docStoreSegment
specifier|private
specifier|final
name|String
name|docStoreSegment
decl_stmt|;
comment|// name used to derive fields/vectors file we share with
comment|// other segments
comment|//TODO: LUCENE-2555: remove once we don't need to support shared doc stores (pre 4.0)
DECL|field|docStoreIsCompoundFile
specifier|private
specifier|final
name|boolean
name|docStoreIsCompoundFile
decl_stmt|;
comment|// whether doc store files are stored in compound file (*.cfx)
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
DECL|field|attributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
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
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
block|{
assert|assert
operator|!
operator|(
name|dir
operator|instanceof
name|TrackingDirectoryWrapper
operator|)
assert|;
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
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
block|}
comment|/**    * Returns total size in bytes of all of files used by    * this segment.  Note that this will not include any live    * docs for the segment; to include that use {@link    * SegmentInfoPerCommit.sizeInBytes} instead.    */
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
comment|/*    * Return all files referenced by this SegmentInfo.  The    * returns List is a locally cached List so you should not    * modify it.    */
DECL|method|files
specifier|public
name|Set
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
name|setFiles
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"files were not computed yet"
argument_list|)
throw|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|setFiles
argument_list|)
return|;
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
name|delCount
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
DECL|field|setFiles
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|setFiles
decl_stmt|;
DECL|method|setFiles
specifier|public
name|void
name|setFiles
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|setFiles
operator|=
name|files
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|addFiles
specifier|public
name|void
name|addFiles
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|setFiles
operator|.
name|addAll
argument_list|(
name|files
argument_list|)
expr_stmt|;
block|}
DECL|method|addFile
specifier|public
name|void
name|addFile
parameter_list|(
name|String
name|file
parameter_list|)
block|{
name|setFiles
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get a codec attribute value, or null if it does not exist    */
DECL|method|getAttribute
specifier|public
name|String
name|getAttribute
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * Puts a codec attribute value.    *<p>    * This is a key-value mapping for the field that the codec can use    * to store additional metadata, and will be available to the codec    * when reading the segment via {@link #getAttribute(String)}    *<p>    * If a value already exists for the field, it will be replaced with     * the new value.    */
DECL|method|putAttribute
specifier|public
name|String
name|putAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|attributes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * @return internal codec attributes map. May be null if no mappings exist.    */
DECL|method|attributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
block|}
end_class

end_unit

