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
name|junit
operator|.
name|Assert
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
comment|/**  * Make sure {@link LuceneTestCase#setUp()} and {@link LuceneTestCase#tearDown()} were invoked even if they  * have been overriden. We assume nobody will call these out of non-overriden  * methods (they have to be public by contract, unfortunately). The top-level  * methods just set a flag that is checked upon successful execution of each test  * case.  */
end_comment

begin_class
DECL|class|TestRuleSetupTeardownChained
class|class
name|TestRuleSetupTeardownChained
implements|implements
name|TestRule
block|{
comment|/**    * @see TestRuleSetupTeardownChained      */
DECL|field|setupCalled
specifier|public
name|boolean
name|setupCalled
decl_stmt|;
comment|/**    * @see TestRuleSetupTeardownChained    */
DECL|field|teardownCalled
specifier|public
name|boolean
name|teardownCalled
decl_stmt|;
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|base
parameter_list|,
name|Description
name|description
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|setupCalled
operator|=
literal|false
expr_stmt|;
name|teardownCalled
operator|=
literal|false
expr_stmt|;
name|base
operator|.
name|evaluate
argument_list|()
expr_stmt|;
comment|// I assume we don't want to check teardown chaining if something happens in the
comment|// test because this would obscure the original exception?
if|if
condition|(
operator|!
name|setupCalled
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"One of the overrides of setUp does not propagate the call."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|teardownCalled
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"One of the overrides of tearDown does not propagate the call."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

