begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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

begin_comment
comment|/**  * Defines span collection for eager Span implementations, such as  * {@link org.apache.lucene.search.spans.NearSpansOrdered}  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|BufferedSpanCollector
specifier|public
interface|interface
name|BufferedSpanCollector
block|{
comment|/**    * Collect information from a possible candidate    * @param spans the candidate Spans    * @throws IOException on error    */
DECL|method|collectCandidate
specifier|public
name|void
name|collectCandidate
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Confirm that the last candidate Spans has been accepted by the parent algorithm    */
DECL|method|accept
specifier|public
name|void
name|accept
parameter_list|()
function_decl|;
comment|/**    * Replay buffered information back to the parent SpanCollector    */
DECL|method|replay
specifier|public
name|void
name|replay
parameter_list|()
function_decl|;
comment|/**    * A default No-op BufferedSpanCollector    */
DECL|field|NO_OP
specifier|public
specifier|static
specifier|final
name|BufferedSpanCollector
name|NO_OP
init|=
operator|new
name|BufferedSpanCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|collectCandidate
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|()
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|replay
parameter_list|()
block|{      }
block|}
decl_stmt|;
block|}
end_interface

end_unit

