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
name|IndexOutput
import|;
end_import

begin_comment
comment|/**  * Specifies an API for classes that can write out {@link SegmentInfos} data.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentInfosWriter
specifier|public
specifier|abstract
class|class
name|SegmentInfosWriter
block|{
comment|/**    * Write {@link SegmentInfos} data without closing the output. The returned    * output will become finished only after a successful completion of    * "two phase commit" that first calls {@link #prepareCommit(IndexOutput)} and    * then {@link #finishCommit(IndexOutput)}.    * @param dir directory to write data to    * @param segmentsFileName name of the "segments_N" file to create    * @param infos data to write    * @return an instance of {@link IndexOutput} to be used in subsequent "two    * phase commit" operations as described above.    * @throws IOException    */
DECL|method|writeInfos
specifier|public
specifier|abstract
name|IndexOutput
name|writeInfos
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentsFileName
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
comment|/**    * First phase of the two-phase commit - ensure that all output can be    * successfully written out.    * @param out an instance of {@link IndexOutput} returned from a previous    * call to {@link #writeInfos(Directory, String, SegmentInfos)}.    * @throws IOException    */
DECL|method|prepareCommit
specifier|public
specifier|abstract
name|void
name|prepareCommit
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Second phase of the two-phase commit. In this step the output should be    * finalized and closed.    * @param out an instance of {@link IndexOutput} returned from a previous    * call to {@link #writeInfos(Directory, String, SegmentInfos)}.    * @throws IOException    */
DECL|method|finishCommit
specifier|public
specifier|abstract
name|void
name|finishCommit
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

