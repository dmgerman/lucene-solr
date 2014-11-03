begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_interface
DECL|interface|CollectionParams
specifier|public
interface|interface
name|CollectionParams
block|{
comment|/** What action **/
DECL|field|ACTION
specifier|public
specifier|final
specifier|static
name|String
name|ACTION
init|=
literal|"action"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|final
specifier|static
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
DECL|enum|CollectionAction
specifier|public
enum|enum
name|CollectionAction
block|{
DECL|enum constant|CREATE
name|CREATE
block|,
DECL|enum constant|DELETE
name|DELETE
block|,
DECL|enum constant|RELOAD
name|RELOAD
block|,
DECL|enum constant|SYNCSHARD
name|SYNCSHARD
block|,
DECL|enum constant|CREATEALIAS
name|CREATEALIAS
block|,
DECL|enum constant|DELETEALIAS
name|DELETEALIAS
block|,
DECL|enum constant|SPLITSHARD
name|SPLITSHARD
block|,
DECL|enum constant|DELETESHARD
name|DELETESHARD
block|,
DECL|enum constant|CREATESHARD
name|CREATESHARD
block|,
DECL|enum constant|DELETEREPLICA
name|DELETEREPLICA
block|,
DECL|enum constant|MIGRATE
name|MIGRATE
block|,
DECL|enum constant|ADDROLE
name|ADDROLE
block|,
DECL|enum constant|REMOVEROLE
name|REMOVEROLE
block|,
DECL|enum constant|CLUSTERPROP
name|CLUSTERPROP
block|,
DECL|enum constant|REQUESTSTATUS
name|REQUESTSTATUS
block|,
DECL|enum constant|ADDREPLICA
name|ADDREPLICA
block|,
DECL|enum constant|OVERSEERSTATUS
name|OVERSEERSTATUS
block|,
DECL|enum constant|LIST
name|LIST
block|,
DECL|enum constant|CLUSTERSTATUS
name|CLUSTERSTATUS
block|,
DECL|enum constant|ADDREPLICAPROP
name|ADDREPLICAPROP
block|,
DECL|enum constant|DELETEREPLICAPROP
name|DELETEREPLICAPROP
block|,
DECL|enum constant|BALANCESHARDUNIQUE
name|BALANCESHARDUNIQUE
block|,
DECL|enum constant|REBALANCELEADERS
name|REBALANCELEADERS
block|;
DECL|method|get
specifier|public
specifier|static
name|CollectionAction
name|get
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|CollectionAction
operator|.
name|valueOf
argument_list|(
name|p
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
return|return
literal|null
return|;
block|}
DECL|method|isEqual
specifier|public
name|boolean
name|isEqual
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toLower
specifier|public
name|String
name|toLower
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

