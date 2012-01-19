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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Lucene3x ReadOnly SegmentInfosFormat implementation  * @deprecated (4.0) This is only used to read indexes created  * before 4.0.  * @lucene.experimental  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene3xSegmentInfosFormat
specifier|public
class|class
name|Lucene3xSegmentInfosFormat
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
name|Lucene3xSegmentInfosReader
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

