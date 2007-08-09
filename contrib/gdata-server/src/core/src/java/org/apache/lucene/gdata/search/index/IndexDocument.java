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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_comment
comment|/**  * IndexDocument encapsulates the acual entity to store, update or delete. All  * infomation to process the action on this document are provided via this  * interface.  *<p>  * This enables the GDataIndexer to index every kind of document. All the  * processing of the original document happens somewhere behind this facade.  * {@link org.apache.lucene.gdata.search.index.IndexDocumentBuilderTask} passed  * to the {@link org.apache.lucene.gdata.search.index.GDataIndexer} task queue  * produce instances of this interface concurrently.  *</p>  *   *  *   *   */
end_comment

begin_interface
DECL|interface|IndexDocument
specifier|public
interface|interface
name|IndexDocument
block|{
comment|/**      * the index field to identify a document in the index. This acts as a      * primary key to fetch the entire entry from the storage      */
DECL|field|FIELD_ENTRY_ID
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_ENTRY_ID
init|=
literal|"enryId"
decl_stmt|;
comment|/**      * the index field to associate a document with a specific feed       */
DECL|field|FIELD_FEED_ID
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_FEED_ID
init|=
literal|"feedId"
decl_stmt|;
DECL|field|GDATA_MANDATORY_FIELD_UPDATED
specifier|public
specifier|static
specifier|final
name|String
name|GDATA_MANDATORY_FIELD_UPDATED
init|=
literal|"updated"
decl_stmt|;
DECL|field|GDATA_MANDATORY_FIELD_CATEGORY
specifier|public
specifier|static
specifier|final
name|String
name|GDATA_MANDATORY_FIELD_CATEGORY
init|=
literal|"category"
decl_stmt|;
comment|/**      * @return<code>true</code> if and only if this document is an update,      *         otherwise<code>false</code>      */
DECL|method|isUpdate
specifier|public
specifier|abstract
name|boolean
name|isUpdate
parameter_list|()
function_decl|;
comment|/**      * @return<code>true</code> if and only if this document is a delete,      *         otherwise<code>false</code>      */
DECL|method|isDelete
specifier|public
specifier|abstract
name|boolean
name|isDelete
parameter_list|()
function_decl|;
comment|/**      * @return<code>true</code> if and only if this document is an insert,      *         otherwise<code>false</code>      */
DECL|method|isInsert
specifier|public
specifier|abstract
name|boolean
name|isInsert
parameter_list|()
function_decl|;
comment|/**      *       * @return - the lucene document to write to the index if the action is      *         insert or updated, otherwise it will return<code>null</code>;      */
DECL|method|getWriteable
specifier|public
specifier|abstract
name|Document
name|getWriteable
parameter_list|()
function_decl|;
comment|/**      * @return - a term that identifies this document in the index to delete      *         this document on a update or delete      */
DECL|method|getDeletealbe
specifier|public
specifier|abstract
name|Term
name|getDeletealbe
parameter_list|()
function_decl|;
comment|/**      * Indicates that the index should be commited after this document has been      * processed      *       * @return<code>true</code> if the index should be commited after this      *         document, otherwise<code>false</code>      */
DECL|method|commitAfter
specifier|public
specifier|abstract
name|boolean
name|commitAfter
parameter_list|()
function_decl|;
comment|/**      * Indicates that the index should be optimized after this document has been      * processed      *       *       * @return<code>true</code> if the index should be optimized after this      *         document, otherwise<code>false</code>      */
DECL|method|optimizeAfter
specifier|public
specifier|abstract
name|boolean
name|optimizeAfter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

