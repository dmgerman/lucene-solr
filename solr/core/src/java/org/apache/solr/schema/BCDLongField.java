begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|index
operator|.
name|GeneralField
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BCDLongField
specifier|public
class|class
name|BCDLongField
extends|extends
name|BCDIntField
block|{
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Long
name|toObject
parameter_list|(
name|GeneralField
name|f
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|toExternal
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

