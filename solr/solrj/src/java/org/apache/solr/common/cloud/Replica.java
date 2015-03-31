begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
operator|.
name|BASE_URL_PROP
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
import|;
end_import

begin_class
DECL|class|Replica
specifier|public
class|class
name|Replica
extends|extends
name|ZkNodeProps
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|nodeName
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|method|Replica
specifier|public
name|Replica
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propMap
parameter_list|)
block|{
name|super
argument_list|(
name|propMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|nodeName
operator|=
operator|(
name|String
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
expr_stmt|;
block|}
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
DECL|method|getCoreUrl
specifier|public
name|String
name|getCoreUrl
parameter_list|()
block|{
return|return
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|getStr
argument_list|(
name|BASE_URL_PROP
argument_list|)
argument_list|,
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
argument_list|)
return|;
block|}
comment|/** The name of the node this replica resides on */
DECL|method|getNodeName
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|':'
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|propMap
argument_list|,
operator|-
literal|1
argument_list|)
return|;
comment|// small enough, keep it on one line (i.e. no indent)
block|}
block|}
end_class

end_unit

