begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|perfield
operator|.
name|PerFieldPostingsFormat
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

begin_comment
comment|/**  * Holder class for common parameters used during read.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentReadState
specifier|public
class|class
name|SegmentReadState
block|{
comment|/** {@link Directory} where this segment is read from. */
DECL|field|directory
specifier|public
specifier|final
name|Directory
name|directory
decl_stmt|;
comment|/** {@link SegmentInfo} describing this segment. */
DECL|field|segmentInfo
specifier|public
specifier|final
name|SegmentInfo
name|segmentInfo
decl_stmt|;
comment|/** {@link FieldInfos} describing all fields in this    *  segment. */
DECL|field|fieldInfos
specifier|public
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
comment|/** {@link IOContext} to pass to {@link    *  Directory#openInput(String,IOContext)}. */
DECL|field|context
specifier|public
specifier|final
name|IOContext
name|context
decl_stmt|;
comment|/** Unique suffix for any postings files read for this    *  segment.  {@link PerFieldPostingsFormat} sets this for    *  each of the postings formats it wraps.  If you create    *  a new {@link PostingsFormat} then any files you    *  write/read must be derived using this suffix (use    *  {@link IndexFileNames#segmentFileName(String,String,String)}). */
DECL|field|segmentSuffix
specifier|public
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
comment|/** Create a {@code SegmentReadState}. */
DECL|method|SegmentReadState
specifier|public
name|SegmentReadState
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|context
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/** Create a {@code SegmentReadState}. */
DECL|method|SegmentReadState
specifier|public
name|SegmentReadState
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|segmentInfo
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
block|}
comment|/** Create a {@code SegmentReadState}. */
DECL|method|SegmentReadState
specifier|public
name|SegmentReadState
parameter_list|(
name|SegmentReadState
name|other
parameter_list|,
name|String
name|newSegmentSuffix
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|other
operator|.
name|directory
expr_stmt|;
name|this
operator|.
name|segmentInfo
operator|=
name|other
operator|.
name|segmentInfo
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|other
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|other
operator|.
name|context
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|newSegmentSuffix
expr_stmt|;
block|}
block|}
end_class

end_unit

