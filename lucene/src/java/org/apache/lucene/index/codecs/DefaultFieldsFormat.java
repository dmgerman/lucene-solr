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
comment|/** @lucene.experimental */
end_comment

begin_class
DECL|class|DefaultFieldsFormat
specifier|public
class|class
name|DefaultFieldsFormat
extends|extends
name|FieldsFormat
block|{
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|FieldsReader
name|fieldsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|int
name|docStoreOffset
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DefaultFieldsReader
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fn
argument_list|,
name|context
argument_list|,
name|docStoreOffset
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|FieldsWriter
name|fieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DefaultFieldsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO!
block|}
block|}
end_class

end_unit

