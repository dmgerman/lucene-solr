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

begin_comment
comment|/** @deprecated Use {@link IndexInput} or {@link BufferedIndexInput} instead.*/
end_comment

begin_class
DECL|class|InputStream
specifier|public
specifier|abstract
class|class
name|InputStream
extends|extends
name|BufferedIndexInput
block|{
DECL|field|length
specifier|protected
name|long
name|length
decl_stmt|;
comment|// set by subclasses
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
end_class

end_unit

