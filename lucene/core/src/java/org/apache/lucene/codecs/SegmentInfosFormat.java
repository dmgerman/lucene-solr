begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|index
operator|.
name|SegmentInfos
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**  * Expert: Controls the format of the   * {@link SegmentInfos} (segments file).  *<p>  * NOTE: This isn't a per-segment file. If you change the format, other versions  * of lucene won't be able to read it.  *   * @see SegmentInfos  * @lucene.experimental  */
end_comment

begin_comment
comment|// nocommit rename (remove the s?)
end_comment

begin_comment
comment|// TODO: would be great to handle this situation better.
end_comment

begin_comment
comment|// ideally a custom implementation could implement two-phase commit differently,
end_comment

begin_comment
comment|// (e.g. atomic rename), and ideally all versions of lucene could still read it.
end_comment

begin_comment
comment|// but this is just reflecting reality as it is today...
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// also, perhaps the name should change (to cover all global files like .fnx?)
end_comment

begin_comment
comment|// then again, maybe we can just remove that file...
end_comment

begin_class
DECL|class|SegmentInfosFormat
specifier|public
specifier|abstract
class|class
name|SegmentInfosFormat
block|{
DECL|method|getSegmentInfosReader
specifier|public
specifier|abstract
name|SegmentInfosReader
name|getSegmentInfosReader
parameter_list|()
function_decl|;
DECL|method|getSegmentInfosWriter
specifier|public
specifier|abstract
name|SegmentInfosWriter
name|getSegmentInfosWriter
parameter_list|()
function_decl|;
DECL|method|files
specifier|public
specifier|abstract
name|void
name|files
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
function_decl|;
block|}
end_class

end_unit

