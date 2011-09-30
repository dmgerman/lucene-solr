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
name|Comparator
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
name|values
operator|.
name|Writer
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
name|util
operator|.
name|BytesRef
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
name|Counter
import|;
end_import

begin_comment
comment|/**  * Abstract base class for PerDocConsumer implementations  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocValuesWriterBase
specifier|public
specifier|abstract
class|class
name|DocValuesWriterBase
extends|extends
name|PerDocConsumer
block|{
DECL|field|segmentName
specifier|private
specifier|final
name|String
name|segmentName
decl_stmt|;
DECL|field|codecId
specifier|private
specifier|final
name|int
name|codecId
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|method|DocValuesWriterBase
specifier|protected
name|DocValuesWriterBase
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
block|{
name|this
operator|.
name|segmentName
operator|=
name|state
operator|.
name|segmentName
expr_stmt|;
name|this
operator|.
name|codecId
operator|=
name|state
operator|.
name|codecId
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|state
operator|.
name|bytesUsed
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|state
operator|.
name|context
expr_stmt|;
block|}
DECL|method|getDirectory
specifier|protected
specifier|abstract
name|Directory
name|getDirectory
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
DECL|method|addValuesField
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Writer
operator|.
name|create
argument_list|(
name|field
operator|.
name|getDocValues
argument_list|()
argument_list|,
name|docValuesId
argument_list|(
name|segmentName
argument_list|,
name|codecId
argument_list|,
name|field
operator|.
name|number
argument_list|)
argument_list|,
name|getDirectory
argument_list|()
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|docValuesId
specifier|public
specifier|static
name|String
name|docValuesId
parameter_list|(
name|String
name|segmentsName
parameter_list|,
name|int
name|codecID
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
name|codecID
operator|+
literal|"-"
operator|+
name|fieldId
return|;
block|}
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

