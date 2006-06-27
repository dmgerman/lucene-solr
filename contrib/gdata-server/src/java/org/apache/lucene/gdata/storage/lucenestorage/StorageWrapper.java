begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_comment
comment|/**  * A interface to be implemented by<tt>StorageWrapper</tt> sub classes to  * provide a lucene document for each entity wrapped.  *   * @see org.apache.lucene.gdata.storage.lucenestorage.StorageEntryWrapper  * @see org.apache.lucene.gdata.storage.lucenestorage.StorageAccountWrapper  * @see org.apache.lucene.gdata.storage.lucenestorage.StorageFeedWrapper  * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|StorageWrapper
specifier|public
interface|interface
name|StorageWrapper
block|{
comment|/**      * Returns a Lucene document representing the Wrapped Entry      *       * @return a Lucene Document      */
DECL|method|getLuceneDocument
specifier|public
specifier|abstract
name|Document
name|getLuceneDocument
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

