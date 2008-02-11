begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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

begin_comment
comment|/**<p>Expert: {@link IndexWriter} uses an instance  *  implementing this interface to execute the merges  *  selected by a {@link MergePolicy}.  The default  *  MergeScheduler is {@link ConcurrentMergeScheduler}.</p>  *<p><b>NOTE:</b> This API is new and still experimental  * (subject to change suddenly in the next release)</p> */
end_comment

begin_class
DECL|class|MergeScheduler
specifier|public
specifier|abstract
class|class
name|MergeScheduler
block|{
comment|/** Run the merges provided by {@link IndexWriter#getNextMerge()}. */
DECL|method|merge
specifier|abstract
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
comment|/** Close this MergeScheduler. */
DECL|method|close
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
block|}
end_class

end_unit

