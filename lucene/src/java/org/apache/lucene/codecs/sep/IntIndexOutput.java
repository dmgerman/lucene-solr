begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.sep
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|sep
package|;
end_package

begin_comment
comment|/**  * LICENSED to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|// TODO: we may want tighter integration w/ IndexOutput --
end_comment

begin_comment
comment|// may give better perf:
end_comment

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
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/** Defines basic API for writing ints to an IndexOutput.  *  IntBlockCodec interacts with this API. @see  *  IntBlockReader.  *  *<p>NOTE: block sizes could be variable  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|IntIndexOutput
specifier|public
specifier|abstract
class|class
name|IntIndexOutput
implements|implements
name|Closeable
block|{
comment|/** Write an int to the primary file.  The value must be    *>= 0.  */
DECL|method|write
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|class|Index
specifier|public
specifier|abstract
specifier|static
class|class
name|Index
block|{
comment|/** Internally records the current location */
DECL|method|mark
specifier|public
specifier|abstract
name|void
name|mark
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Copies index from other */
DECL|method|copyFrom
specifier|public
specifier|abstract
name|void
name|copyFrom
parameter_list|(
name|Index
name|other
parameter_list|,
name|boolean
name|copyLast
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Writes "location" of current output pointer of primary      *  output to different output (out) */
DECL|method|write
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|IndexOutput
name|indexOut
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/** If you are indexing the primary output file, call    *  this and interact with the returned IndexWriter. */
DECL|method|index
specifier|public
specifier|abstract
name|Index
name|index
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

