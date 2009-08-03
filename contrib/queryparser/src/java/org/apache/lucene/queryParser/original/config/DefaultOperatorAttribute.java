begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.original.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|original
operator|.
name|config
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
name|QueryConfigHandler
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
name|original
operator|.
name|processors
operator|.
name|GroupQueryNodeProcessor
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
name|util
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * This attribute is used by {@link GroupQueryNodeProcessor} processor and must  * be defined in the {@link QueryConfigHandler}. This attribute tells the  * processor which is the default boolean operator when no operator is defined  * between terms.<br/>  *  */
end_comment

begin_interface
DECL|interface|DefaultOperatorAttribute
specifier|public
interface|interface
name|DefaultOperatorAttribute
extends|extends
name|Attribute
block|{
DECL|enum|Operator
specifier|public
specifier|static
enum|enum
name|Operator
block|{
DECL|enum constant|AND
DECL|enum constant|OR
name|AND
block|,
name|OR
block|; 	  }
DECL|method|setOperator
specifier|public
name|void
name|setOperator
parameter_list|(
name|Operator
name|operator
parameter_list|)
function_decl|;
DECL|method|getOperator
specifier|public
name|Operator
name|getOperator
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

