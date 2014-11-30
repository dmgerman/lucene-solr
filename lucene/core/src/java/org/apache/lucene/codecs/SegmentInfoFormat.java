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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Expert: Controls the format of the   * {@link SegmentInfo} (segment metadata file).  *<p>  *   * @see SegmentInfo  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentInfoFormat
specifier|public
specifier|abstract
class|class
name|SegmentInfoFormat
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|SegmentInfoFormat
specifier|protected
name|SegmentInfoFormat
parameter_list|()
block|{   }
comment|/**    * Read {@link SegmentInfo} data from a directory.    * @param directory directory to read from    * @param segmentName name of the segment to read    * @param segmentID expected identifier for the segment    * @return infos instance to be populated with data    * @throws IOException If an I/O error occurs    */
DECL|method|read
specifier|public
specifier|abstract
name|SegmentInfo
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|byte
name|segmentID
index|[]
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write {@link SegmentInfo} data.    * The codec must add its SegmentInfo filename(s) to {@code info} before doing i/o.     * @throws IOException If an I/O error occurs    */
DECL|method|write
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

