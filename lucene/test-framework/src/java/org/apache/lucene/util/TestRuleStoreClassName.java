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
comment|/**  * Stores the suite name so you can retrieve it  * from {@link #getTestClass()}  */
end_comment

begin_class
DECL|class|TestRuleStoreClassName
specifier|public
class|class
name|TestRuleStoreClassName
implements|implements
name|TestRule
block|{
DECL|field|description
specifier|private
specifier|volatile
name|Description
name|description
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
name|s
parameter_list|,
specifier|final
name|Description
name|d
parameter_list|)
block|{
if|if
condition|(
operator|!
name|d
operator|.
name|isSuite
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This is a @ClassRule (applies to suites only)."
argument_list|)
throw|;
block|}
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
try|try
block|{
name|description
operator|=
name|d
expr_stmt|;
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|description
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Returns the test class currently executing in this rule.    */
DECL|method|getTestClass
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getTestClass
parameter_list|()
block|{
name|Description
name|localDescription
init|=
name|description
decl_stmt|;
if|if
condition|(
name|localDescription
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The rule is not currently executing."
argument_list|)
throw|;
block|}
return|return
name|localDescription
operator|.
name|getTestClass
argument_list|()
return|;
block|}
block|}
end_class

end_unit

