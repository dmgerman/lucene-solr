begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_interface
DECL|interface|CollectionAdminParams
specifier|public
interface|interface
name|CollectionAdminParams
block|{
comment|/* Param used by DELETESTATUS call to clear all stored responses */
DECL|field|FLUSH
name|String
name|FLUSH
init|=
literal|"flush"
decl_stmt|;
DECL|field|COLLECTION
name|String
name|COLLECTION
init|=
literal|"collection"
decl_stmt|;
DECL|field|COUNT_PROP
name|String
name|COUNT_PROP
init|=
literal|"count"
decl_stmt|;
block|}
end_interface

end_unit

