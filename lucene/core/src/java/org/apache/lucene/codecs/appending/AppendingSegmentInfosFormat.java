begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.appending
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|appending
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SegmentInfosWriter
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
name|lucene40
operator|.
name|Lucene40SegmentInfosFormat
import|;
end_import

begin_comment
comment|/**  * Append-only SegmentInfos format.  *<p>  * Only a writer is supplied, as the format is written   * the same as {@link Lucene40SegmentInfosFormat}.  *   * @see AppendingSegmentInfosWriter  */
end_comment

begin_class
DECL|class|AppendingSegmentInfosFormat
specifier|public
class|class
name|AppendingSegmentInfosFormat
extends|extends
name|Lucene40SegmentInfosFormat
block|{
DECL|field|writer
specifier|private
specifier|final
name|SegmentInfosWriter
name|writer
init|=
operator|new
name|AppendingSegmentInfosWriter
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getSegmentInfosWriter
specifier|public
name|SegmentInfosWriter
name|getSegmentInfosWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
block|}
end_class

end_unit

