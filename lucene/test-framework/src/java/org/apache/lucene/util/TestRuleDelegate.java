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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_comment
comment|/**  * A {@link TestRule} that delegates to another {@link TestRule} via a delegate  * contained in a an {@link AtomicReference}.  */
end_comment

begin_class
DECL|class|TestRuleDelegate
specifier|final
class|class
name|TestRuleDelegate
parameter_list|<
name|T
extends|extends
name|TestRule
parameter_list|>
implements|implements
name|TestRule
block|{
DECL|field|delegate
specifier|private
name|AtomicReference
argument_list|<
name|T
argument_list|>
name|delegate
decl_stmt|;
DECL|method|TestRuleDelegate
specifier|private
name|TestRuleDelegate
parameter_list|(
name|AtomicReference
argument_list|<
name|T
argument_list|>
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
name|Statement
name|s
parameter_list|,
name|Description
name|d
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|s
argument_list|,
name|d
argument_list|)
return|;
block|}
DECL|method|of
specifier|static
parameter_list|<
name|T
extends|extends
name|TestRule
parameter_list|>
name|TestRuleDelegate
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|AtomicReference
argument_list|<
name|T
argument_list|>
name|delegate
parameter_list|)
block|{
return|return
operator|new
name|TestRuleDelegate
argument_list|<>
argument_list|(
name|delegate
argument_list|)
return|;
block|}
block|}
end_class

end_unit

