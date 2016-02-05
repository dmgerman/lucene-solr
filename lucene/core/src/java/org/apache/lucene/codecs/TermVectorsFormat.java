begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FieldInfos
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
comment|/**  * Controls the format of term vectors  */
end_comment

begin_class
DECL|class|TermVectorsFormat
specifier|public
specifier|abstract
class|class
name|TermVectorsFormat
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|TermVectorsFormat
specifier|protected
name|TermVectorsFormat
parameter_list|()
block|{   }
comment|/** Returns a {@link TermVectorsReader} to read term    *  vectors. */
DECL|method|vectorsReader
specifier|public
specifier|abstract
name|TermVectorsReader
name|vectorsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a {@link TermVectorsWriter} to write term    *  vectors. */
DECL|method|vectorsWriter
specifier|public
specifier|abstract
name|TermVectorsWriter
name|vectorsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
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

