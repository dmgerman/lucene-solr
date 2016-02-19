begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.util
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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Ensures that provided identifiers align with Solr's recommendations/requirements for choosing  * collection, core, etc identifiers.  *    * Identifiers are allowed to contain underscores, periods, and alphanumeric characters.   */
end_comment

begin_class
DECL|class|SolrIdentifierValidator
specifier|public
class|class
name|SolrIdentifierValidator
block|{
DECL|field|identifierPattern
specifier|final
specifier|static
name|Pattern
name|identifierPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[\\._A-Za-z0-9]*$"
argument_list|)
decl_stmt|;
DECL|method|validateShardName
specifier|public
specifier|static
name|boolean
name|validateShardName
parameter_list|(
name|String
name|shardName
parameter_list|)
block|{
return|return
name|validateIdentifier
argument_list|(
name|shardName
argument_list|)
return|;
block|}
DECL|method|validateCollectionName
specifier|public
specifier|static
name|boolean
name|validateCollectionName
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{
return|return
name|validateIdentifier
argument_list|(
name|collectionName
argument_list|)
return|;
block|}
DECL|method|validateCoreName
specifier|public
specifier|static
name|boolean
name|validateCoreName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|validateIdentifier
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|validateIdentifier
specifier|private
specifier|static
name|boolean
name|validateIdentifier
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
if|if
condition|(
name|identifier
operator|==
literal|null
operator|||
operator|!
name|identifierPattern
operator|.
name|matcher
argument_list|(
name|identifier
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit
