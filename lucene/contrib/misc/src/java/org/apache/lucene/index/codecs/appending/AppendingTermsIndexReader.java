begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.appending
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
operator|.
name|appending
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
name|codecs
operator|.
name|FixedGapTermsIndexReader
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
name|IndexInput
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
name|CodecUtil
import|;
end_import

begin_class
DECL|class|AppendingTermsIndexReader
specifier|public
class|class
name|AppendingTermsIndexReader
extends|extends
name|FixedGapTermsIndexReader
block|{
DECL|method|AppendingTermsIndexReader
specifier|public
name|AppendingTermsIndexReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|int
name|indexDivisor
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|fieldInfos
argument_list|,
name|segment
argument_list|,
name|indexDivisor
argument_list|,
name|termComp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readHeader
specifier|protected
name|void
name|readHeader
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|AppendingTermsIndexWriter
operator|.
name|CODEC_NAME
argument_list|,
name|AppendingTermsIndexWriter
operator|.
name|VERSION_START
argument_list|,
name|AppendingTermsIndexWriter
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekDir
specifier|protected
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|input
operator|.
name|length
argument_list|()
operator|-
name|Long
operator|.
name|SIZE
operator|/
literal|8
argument_list|)
expr_stmt|;
name|long
name|offset
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

