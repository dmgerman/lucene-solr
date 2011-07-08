begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|index
operator|.
name|SegmentInfos
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

begin_comment
comment|/**  * Specifies an API for classes that can read {@link SegmentInfos} information.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentInfosReader
specifier|public
specifier|abstract
class|class
name|SegmentInfosReader
block|{
comment|/**    * Read {@link SegmentInfos} data from a directory.    * @param directory directory to read from    * @param segmentsFileName name of the "segments_N" file    * @param codecs current codecs    * @param infos empty instance to be populated with data    * @throws IOException    */
DECL|method|read
specifier|public
specifier|abstract
name|void
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentsFileName
parameter_list|,
name|CodecProvider
name|codecs
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

