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

begin_class
DECL|class|ReadOnlySegmentReader
class|class
name|ReadOnlySegmentReader
extends|extends
name|SegmentReader
block|{
DECL|method|noWrite
specifier|static
name|void
name|noWrite
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexReader cannot make any changes to the index (it was opened with readOnly = true)"
argument_list|)
throw|;
block|}
DECL|method|acquireWriteLock
specifier|protected
name|void
name|acquireWriteLock
parameter_list|()
block|{
name|noWrite
argument_list|()
expr_stmt|;
block|}
comment|// Not synchronized
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|deletedDocs
operator|!=
literal|null
operator|&&
name|deletedDocs
operator|.
name|get
argument_list|(
name|n
argument_list|)
return|;
block|}
block|}
end_class

end_unit

