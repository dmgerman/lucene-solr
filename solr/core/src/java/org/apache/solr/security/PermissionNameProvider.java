begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
operator|.
name|identity
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toMap
import|;
end_import

begin_comment
comment|/**  * A requestHandler should implement this interface to provide the well known permission  * at request time  */
end_comment

begin_interface
DECL|interface|PermissionNameProvider
specifier|public
interface|interface
name|PermissionNameProvider
block|{
DECL|enum|Name
enum|enum
name|Name
block|{
DECL|enum constant|COLL_EDIT_PERM
name|COLL_EDIT_PERM
argument_list|(
literal|"collection-admin-edit"
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|COLL_READ_PERM
name|COLL_READ_PERM
argument_list|(
literal|"collection-admin-read"
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|CORE_READ_PERM
name|CORE_READ_PERM
argument_list|(
literal|"core-admin-read"
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|CORE_EDIT_PERM
name|CORE_EDIT_PERM
argument_list|(
literal|"core-admin-edit"
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|READ_PERM
name|READ_PERM
argument_list|(
literal|"read"
argument_list|,
literal|"*"
argument_list|)
block|,
DECL|enum constant|UPDATE_PERM
name|UPDATE_PERM
argument_list|(
literal|"update"
argument_list|,
literal|"*"
argument_list|)
block|,
DECL|enum constant|CONFIG_EDIT_PERM
name|CONFIG_EDIT_PERM
argument_list|(
literal|"config-edit"
argument_list|,
literal|"*"
argument_list|)
block|,
DECL|enum constant|CONFIG_READ_PERM
name|CONFIG_READ_PERM
argument_list|(
literal|"config-read"
argument_list|,
literal|"*"
argument_list|)
block|,
DECL|enum constant|SCHEMA_READ_PERM
name|SCHEMA_READ_PERM
argument_list|(
literal|"schema-read"
argument_list|,
literal|"*"
argument_list|)
block|,
DECL|enum constant|SCHEMA_EDIT_PERM
name|SCHEMA_EDIT_PERM
argument_list|(
literal|"schema-edit"
argument_list|,
literal|"*"
argument_list|)
block|,
DECL|enum constant|SECURITY_EDIT_PERM
name|SECURITY_EDIT_PERM
argument_list|(
literal|"security-edit"
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|SECURITY_READ_PERM
name|SECURITY_READ_PERM
argument_list|(
literal|"security-read"
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|ALL
name|ALL
argument_list|(
literal|"all"
argument_list|,
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
literal|"*"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
argument_list|)
block|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|collName
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|collName
decl_stmt|;
DECL|method|Name
name|Name
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|collName
parameter_list|)
block|{
name|name
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|collName
operator|=
name|collName
operator|instanceof
name|Set
condition|?
operator|(
name|Set
argument_list|<
name|String
argument_list|>
operator|)
name|collName
else|:
name|singleton
argument_list|(
operator|(
name|String
operator|)
name|collName
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
specifier|static
name|Name
name|get
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|getPermissionName
specifier|public
name|String
name|getPermissionName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
DECL|field|NULL
name|Set
argument_list|<
name|String
argument_list|>
name|NULL
init|=
name|singleton
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|ANY
name|Set
argument_list|<
name|String
argument_list|>
name|ANY
init|=
name|singleton
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
DECL|field|values
name|Map
argument_list|<
name|String
argument_list|,
name|Name
argument_list|>
name|values
init|=
name|unmodifiableMap
argument_list|(
name|asList
argument_list|(
name|Name
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|toMap
argument_list|(
name|Name
operator|::
name|getPermissionName
argument_list|,
name|identity
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|getPermissionName
name|Name
name|getPermissionName
parameter_list|(
name|AuthorizationContext
name|request
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

