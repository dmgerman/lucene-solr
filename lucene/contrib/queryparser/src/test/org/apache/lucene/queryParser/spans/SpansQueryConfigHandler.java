begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|spans
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
import|;
end_import

begin_comment
comment|/**  * This query config handler only adds the {@link UniqueFieldAttribute} to it.<br/>  *<br/>  *   * It does not return any configuration for a field in specific.  */
end_comment

begin_class
DECL|class|SpansQueryConfigHandler
specifier|public
class|class
name|SpansQueryConfigHandler
extends|extends
name|QueryConfigHandler
block|{
DECL|method|SpansQueryConfigHandler
specifier|public
name|SpansQueryConfigHandler
parameter_list|()
block|{
name|addAttribute
argument_list|(
name|UniqueFieldAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldConfig
specifier|public
name|FieldConfig
name|getFieldConfig
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// there is no field configuration, always return null
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

