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
name|SegmentInfoWriter
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PreFlexRWSegmentInfoFormat
class|class
name|PreFlexRWSegmentInfoFormat
extends|extends
name|Lucene3xSegmentInfoFormat
block|{
DECL|field|writer
specifier|private
specifier|final
name|SegmentInfoWriter
name|writer
init|=
operator|new
name|PreFlexRWSegmentInfoWriter
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getSegmentInfosWriter
specifier|public
name|SegmentInfoWriter
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

