begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|search
operator|.
name|BooleanQuery
import|;
end_import

begin_comment
comment|/**  * Prepares and restores {@link LuceneTestCase} at instance level   * (fine grained junk that doesn't fit anywhere else).  */
end_comment

begin_class
DECL|class|TestRuleSetupAndRestoreInstanceEnv
specifier|final
class|class
name|TestRuleSetupAndRestoreInstanceEnv
extends|extends
name|AbstractBeforeAfterRule
block|{
DECL|field|savedBoolMaxClauseCount
specifier|private
name|int
name|savedBoolMaxClauseCount
decl_stmt|;
annotation|@
name|Override
DECL|method|before
specifier|protected
name|void
name|before
parameter_list|()
block|{
name|savedBoolMaxClauseCount
operator|=
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|after
specifier|protected
name|void
name|after
parameter_list|()
block|{
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
name|savedBoolMaxClauseCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

