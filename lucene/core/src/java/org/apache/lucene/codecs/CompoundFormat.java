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
name|java
operator|.
name|util
operator|.
name|Collection
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
comment|/**  * Encodes/decodes compound files  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompoundFormat
specifier|public
specifier|abstract
class|class
name|CompoundFormat
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|CompoundFormat
specifier|public
name|CompoundFormat
parameter_list|()
block|{   }
comment|// TODO: this is very minimal. If we need more methods,
comment|// we can add 'producer' classes.
comment|/**    * Returns a Directory view (read-only) for the compound files in this segment    */
DECL|method|getCompoundReader
specifier|public
specifier|abstract
name|Directory
name|getCompoundReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Packs the provided files into a compound format.    */
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
name|si
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the compound file names used by this segment.    */
comment|// TODO: get this out of here, and use trackingdirwrapper. but this is really scary in IW right now...
comment|// NOTE: generally si.useCompoundFile is not even yet 'set' when this is called.
DECL|method|files
specifier|public
specifier|abstract
name|String
index|[]
name|files
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
function_decl|;
block|}
end_class

end_unit

