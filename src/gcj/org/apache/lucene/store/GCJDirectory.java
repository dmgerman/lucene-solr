begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/** Native file-based {@link Directory} implementation, using GCJ.  *  * @author Doug Cutting  */
end_comment

begin_class
DECL|class|GCJDirectory
specifier|public
class|class
name|GCJDirectory
extends|extends
name|FSDirectory
block|{
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// conserve address space by only mmapping the one index file that most
comment|// impacts performance
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".frq"
argument_list|)
condition|)
block|{
return|return
operator|new
name|GCJIndexInput
argument_list|(
operator|new
name|File
argument_list|(
name|getFile
argument_list|()
argument_list|,
name|name
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

