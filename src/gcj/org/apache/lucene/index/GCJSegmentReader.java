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
name|GCJIndexInput
import|;
end_import

begin_class
DECL|class|GCJSegmentReader
class|class
name|GCJSegmentReader
extends|extends
name|SegmentReader
block|{
comment|/** Try to use an optimized native implementation of TermDocs.  The optimized    * implementation can only be used when the segment's directory is a    * GCJDirectory and it is not in compound format.  */
DECL|method|termDocs
specifier|public
specifier|final
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|freqStream
operator|instanceof
name|GCJIndexInput
condition|)
block|{
comment|// it's a GCJIndexInput
return|return
operator|new
name|GCJTermDocs
argument_list|(
name|this
argument_list|)
return|;
comment|// so can use GCJTermDocs
block|}
else|else
block|{
return|return
name|super
operator|.
name|termDocs
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

