begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * This interface should be implemented by classes need to be notified when an  * index is commited  *   *  *   */
end_comment

begin_interface
DECL|interface|IndexEventListener
specifier|public
interface|interface
name|IndexEventListener
block|{
comment|/**      * This method will be invoked by an instance of {@link GDataIndexer} if the      * index is commited      *       * @param service -      *            the name of the service the invoking indexer runs for      */
DECL|method|commitCallBack
specifier|public
specifier|abstract
name|void
name|commitCallBack
parameter_list|(
name|String
name|service
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

