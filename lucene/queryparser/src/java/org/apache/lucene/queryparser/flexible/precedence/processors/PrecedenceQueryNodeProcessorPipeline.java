begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.precedence.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|precedence
operator|.
name|processors
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
name|queryparser
operator|.
name|flexible
operator|.
name|precedence
operator|.
name|PrecedenceQueryParser
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|processors
operator|.
name|BooleanQuery2ModifierNodeProcessor
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|processors
operator|.
name|StandardQueryNodeProcessorPipeline
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
import|;
end_import

begin_comment
comment|/**  *<p>  * This processor pipeline extends {@link StandardQueryNodeProcessorPipeline} and enables  * boolean precedence on it.  *</p>  *<p>  * EXPERT: the precedence is enabled by removing {@link BooleanQuery2ModifierNodeProcessor} from the  * {@link StandardQueryNodeProcessorPipeline} and appending {@link BooleanModifiersQueryNodeProcessor}  * to the pipeline.  *</p>  *   * @see PrecedenceQueryParser  *  @see StandardQueryNodeProcessorPipeline  */
end_comment

begin_class
DECL|class|PrecedenceQueryNodeProcessorPipeline
specifier|public
class|class
name|PrecedenceQueryNodeProcessorPipeline
extends|extends
name|StandardQueryNodeProcessorPipeline
block|{
comment|/**    * @see StandardQueryNodeProcessorPipeline#StandardQueryNodeProcessorPipeline(QueryConfigHandler)    */
DECL|method|PrecedenceQueryNodeProcessorPipeline
specifier|public
name|PrecedenceQueryNodeProcessorPipeline
parameter_list|(
name|QueryConfigHandler
name|queryConfig
parameter_list|)
block|{
name|super
argument_list|(
name|queryConfig
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|BooleanQuery2ModifierNodeProcessor
operator|.
name|class
argument_list|)
condition|)
block|{
name|remove
argument_list|(
name|i
operator|--
argument_list|)
expr_stmt|;
block|}
block|}
name|add
argument_list|(
operator|new
name|BooleanModifiersQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

