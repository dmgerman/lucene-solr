begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Similar to a {@link java.io.FileFilter}, the FieldSelector allows one to make decisions about  * what Fields get loaded on a {@link Document} by {@link org.apache.lucene.index.IndexReader#document(int,org.apache.lucene.document.FieldSelector)}  *  **/
end_comment

begin_interface
DECL|interface|FieldSelector
specifier|public
interface|interface
name|FieldSelector
extends|extends
name|Serializable
block|{
comment|/**    *     * @param fieldName    * @return true if the {@link Field} with<code>fieldName</code> should be loaded or not    */
DECL|method|accept
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

