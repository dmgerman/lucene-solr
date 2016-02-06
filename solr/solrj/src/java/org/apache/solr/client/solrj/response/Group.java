begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
import|;
end_import

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
comment|/**  * Represents a group. A group contains a common group value that all documents inside the group share and  * documents that belong to this group.  *  * A group value can be a field value, function result or a query string depending on the {@link GroupCommand}.  * In case of a field value or a function result the value is always a indexed value.  *  * @since solr 3.4  */
end_comment

begin_class
DECL|class|Group
specifier|public
class|class
name|Group
implements|implements
name|Serializable
block|{
DECL|field|_groupValue
specifier|private
specifier|final
name|String
name|_groupValue
decl_stmt|;
DECL|field|_result
specifier|private
specifier|final
name|SolrDocumentList
name|_result
decl_stmt|;
comment|/**    * Creates a Group instance.    *    * @param groupValue The common group value (indexed value) that all documents share.    * @param result The documents to be displayed that belong to this group    */
DECL|method|Group
specifier|public
name|Group
parameter_list|(
name|String
name|groupValue
parameter_list|,
name|SolrDocumentList
name|result
parameter_list|)
block|{
name|_groupValue
operator|=
name|groupValue
expr_stmt|;
name|_result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Returns the common group value that all documents share inside this group.    * This is an indexed value, not a stored value.    *    * @return the common group value    */
DECL|method|getGroupValue
specifier|public
name|String
name|getGroupValue
parameter_list|()
block|{
return|return
name|_groupValue
return|;
block|}
comment|/**    * Returns the documents to be displayed that belong to this group.    * How many documents are returned depend on the<code>group.offset</code> and<code>group.limit</code> parameters.    *    * @return the documents to be displayed that belong to this group    */
DECL|method|getResult
specifier|public
name|SolrDocumentList
name|getResult
parameter_list|()
block|{
return|return
name|_result
return|;
block|}
block|}
end_class

end_unit

