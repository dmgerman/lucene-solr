begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|HashSet
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|PerDocConsumer
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
name|FieldInfo
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
name|IndexFileNames
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
name|PerDocWriteState
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
name|DocValues
operator|.
name|Type
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextPerDocConsumer
class|class
name|SimpleTextPerDocConsumer
extends|extends
name|PerDocConsumer
block|{
DECL|field|state
specifier|protected
specifier|final
name|PerDocWriteState
name|state
decl_stmt|;
DECL|field|segmentSuffix
specifier|protected
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
DECL|method|SimpleTextPerDocConsumer
specifier|public
name|SimpleTextPerDocConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|addValuesField
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|Type
name|type
parameter_list|,
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextDocValuesConsumer
argument_list|(
name|SimpleTextDocValuesFormat
operator|.
name|docValuesId
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
name|field
operator|.
name|number
argument_list|)
argument_list|,
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|type
argument_list|,
name|segmentSuffix
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|files
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentName
argument_list|,
name|files
argument_list|,
name|segmentSuffix
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|SegmentInfo
operator|.
name|findMatchingFiles
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
name|state
operator|.
name|directory
argument_list|,
name|files
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|files
specifier|static
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
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
name|files
argument_list|(
name|info
operator|.
name|dir
argument_list|,
name|info
operator|.
name|name
argument_list|,
name|files
argument_list|,
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
DECL|method|docValuesId
specifier|static
name|String
name|docValuesId
parameter_list|(
name|String
name|segmentsName
parameter_list|,
name|int
name|fieldId
parameter_list|)
block|{
return|return
name|segmentsName
operator|+
literal|"_"
operator|+
name|fieldId
return|;
block|}
DECL|method|docValuesIdRegexp
specifier|static
name|String
name|docValuesIdRegexp
parameter_list|(
name|String
name|segmentsName
parameter_list|)
block|{
return|return
name|segmentsName
operator|+
literal|"_\\d+"
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|files
specifier|private
specifier|static
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docValuesIdRegexp
argument_list|(
name|segmentName
argument_list|)
argument_list|,
literal|""
argument_list|,
name|segmentSuffix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

