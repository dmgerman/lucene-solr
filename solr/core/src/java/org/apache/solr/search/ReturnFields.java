begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
operator|.
name|DocTransformer
import|;
end_import

begin_comment
comment|/**  * A class representing the return fields  *  *  * @since solr 4.0  */
end_comment

begin_class
DECL|class|ReturnFields
specifier|public
specifier|abstract
class|class
name|ReturnFields
block|{
comment|/**    * Set of field names with their exact names from the lucene index.    *<p>    * Class such as ResponseWriters pass this to {@link SolrIndexSearcher#doc(int, Set)}.    * @return Set of field names or<code>null</code> (all fields).    */
DECL|method|getLuceneFieldNames
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getLuceneFieldNames
parameter_list|()
function_decl|;
comment|/**    * Set of field names with their exact names from the lucene index.    *    * @param ignoreWantsAll If true, it returns any additional specified field names, in spite of    *                       also wanting all fields. Example: when fl=*,field1, returns ["field1"].    *                       If false, the method returns null when all fields are wanted. Example: when fl=*,field1, returns null.    *                       Note that this method returns null regardless of ignoreWantsAll if all fields    *                       are requested and no explicit field names are specified.    */
DECL|method|getLuceneFieldNames
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getLuceneFieldNames
parameter_list|(
name|boolean
name|ignoreWantsAll
parameter_list|)
function_decl|;
comment|/**    * The requested field names (includes pseudo fields)    *<p>    * @return Set of field names or<code>null</code> (all fields).    */
DECL|method|getRequestedFieldNames
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getRequestedFieldNames
parameter_list|()
function_decl|;
comment|/** Returns<code>true</code> if the specified field should be returned. */
DECL|method|wantsField
specifier|public
specifier|abstract
name|boolean
name|wantsField
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/** Returns<code>true</code> if all fields should be returned. */
DECL|method|wantsAllFields
specifier|public
specifier|abstract
name|boolean
name|wantsAllFields
parameter_list|()
function_decl|;
comment|/** Returns<code>true</code> if the score should be returned. */
DECL|method|wantsScore
specifier|public
specifier|abstract
name|boolean
name|wantsScore
parameter_list|()
function_decl|;
comment|/** Returns<code>true</code> if the fieldnames should be picked with a pattern */
DECL|method|hasPatternMatching
specifier|public
specifier|abstract
name|boolean
name|hasPatternMatching
parameter_list|()
function_decl|;
comment|/** Returns the DocTransformer used to modify documents, or<code>null</code> */
DECL|method|getTransformer
specifier|public
specifier|abstract
name|DocTransformer
name|getTransformer
parameter_list|()
function_decl|;
block|}
end_class

end_unit

