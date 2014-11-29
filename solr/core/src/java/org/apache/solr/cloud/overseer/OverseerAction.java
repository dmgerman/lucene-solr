begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud.overseer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|overseer
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|Overseer
import|;
end_import

begin_comment
comment|/**  * Enum of actions supported by the overseer only.  *  * There are other actions supported which are public and defined  * in {@link org.apache.solr.common.params.CollectionParams.CollectionAction}  */
end_comment

begin_enum
DECL|enum|OverseerAction
specifier|public
enum|enum
name|OverseerAction
block|{
DECL|enum constant|LEADER
name|LEADER
block|,
DECL|enum constant|DELETECORE
name|DELETECORE
block|,
DECL|enum constant|ADDROUTINGRULE
name|ADDROUTINGRULE
block|,
DECL|enum constant|REMOVEROUTINGRULE
name|REMOVEROUTINGRULE
block|,
DECL|enum constant|UPDATESHARDSTATE
name|UPDATESHARDSTATE
block|,
DECL|enum constant|STATE
name|STATE
block|,
DECL|enum constant|QUIT
name|QUIT
block|;
DECL|method|get
specifier|public
specifier|static
name|OverseerAction
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
name|OverseerAction
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
block|{       }
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
return|return
name|s
operator|!=
literal|null
operator|&&
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
end_enum

end_unit

