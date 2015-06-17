begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Field
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
name|common
operator|.
name|SolrDocument
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
name|schema
operator|.
name|FieldType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|BaseEditorialTransformer
specifier|public
specifier|abstract
class|class
name|BaseEditorialTransformer
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|idFieldName
specifier|final
name|String
name|idFieldName
decl_stmt|;
DECL|field|ft
specifier|final
name|FieldType
name|ft
decl_stmt|;
DECL|method|BaseEditorialTransformer
specifier|public
name|BaseEditorialTransformer
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|idFieldName
parameter_list|,
name|FieldType
name|ft
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|idFieldName
operator|=
name|idFieldName
expr_stmt|;
name|this
operator|.
name|ft
operator|=
name|ft
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|)
block|{
comment|//this only gets added if QueryElevationParams.MARK_EXCLUDED is true
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|getIdSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|ids
operator|!=
literal|null
operator|&&
name|ids
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|String
name|key
init|=
name|getKey
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|ids
operator|.
name|contains
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//if we have no ids, that means we weren't marking, but the user still asked for the field to be added, so just mark everything as false
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getIdSet
specifier|protected
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getIdSet
parameter_list|()
function_decl|;
DECL|method|getKey
specifier|protected
name|String
name|getKey
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
block|{
name|String
name|key
decl_stmt|;
name|Object
name|field
init|=
name|doc
operator|.
name|get
argument_list|(
name|idFieldName
argument_list|)
decl_stmt|;
specifier|final
name|Number
name|n
decl_stmt|;
if|if
condition|(
name|field
operator|instanceof
name|Field
condition|)
block|{
name|n
operator|=
operator|(
operator|(
name|Field
operator|)
name|field
operator|)
operator|.
name|numericValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|n
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|key
operator|=
name|n
operator|.
name|toString
argument_list|()
expr_stmt|;
name|key
operator|=
name|ft
operator|.
name|readableToIndexed
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|instanceof
name|Field
condition|)
block|{
name|key
operator|=
operator|(
operator|(
name|Field
operator|)
name|field
operator|)
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|key
operator|=
name|field
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|key
return|;
block|}
block|}
end_class

end_unit

