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
name|codecs
operator|.
name|DimensionalFormat
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
name|DimensionalReader
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
name|DimensionalWriter
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
name|SegmentReadState
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
name|SegmentWriteState
import|;
end_import

begin_comment
comment|/** For debugging, curiosity, transparency only!!  Do not  *  use this codec in production.  *  *<p>This codec stores all dimensional data in a single  *  human-readable text file (_N.dim).  You can view this in  *  any text editor, and even edit it to alter your index.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|SimpleTextDimensionalFormat
specifier|public
specifier|final
class|class
name|SimpleTextDimensionalFormat
extends|extends
name|DimensionalFormat
block|{
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|DimensionalWriter
name|fieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextDimensionalWriter
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|DimensionalReader
name|fieldsReader
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextDimensionalReader
argument_list|(
name|state
argument_list|)
return|;
block|}
comment|/** Extension of dimensional data file */
DECL|field|DIMENSIONAL_EXTENSION
specifier|static
specifier|final
name|String
name|DIMENSIONAL_EXTENSION
init|=
literal|"dim"
decl_stmt|;
comment|/** Extension of dimensional index file */
DECL|field|DIMENSIONAL_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|DIMENSIONAL_INDEX_EXTENSION
init|=
literal|"dii"
decl_stmt|;
block|}
end_class

end_unit

