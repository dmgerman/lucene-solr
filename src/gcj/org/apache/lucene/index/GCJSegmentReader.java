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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|GCJDirectory
import|;
end_import

begin_class
DECL|class|GCJSegmentReader
class|class
name|GCJSegmentReader
extends|extends
name|SegmentReader
block|{
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
name|directory
argument_list|()
operator|instanceof
name|GCJDirectory
condition|)
block|{
return|return
operator|new
name|GCJTermDocs
argument_list|(
name|this
argument_list|)
return|;
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

