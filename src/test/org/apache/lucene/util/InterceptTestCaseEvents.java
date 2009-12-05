begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestWatchman
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
name|FrameworkMethod
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_class
DECL|class|InterceptTestCaseEvents
specifier|public
specifier|final
class|class
name|InterceptTestCaseEvents
extends|extends
name|TestWatchman
block|{
DECL|field|obj
specifier|private
name|Object
name|obj
decl_stmt|;
DECL|method|InterceptTestCaseEvents
specifier|public
name|InterceptTestCaseEvents
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|this
operator|.
name|obj
operator|=
name|obj
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|failed
specifier|public
name|void
name|failed
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|FrameworkMethod
name|method
parameter_list|)
block|{
try|try
block|{
name|Method
name|reporter
init|=
name|method
operator|.
name|getMethod
argument_list|()
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"reportAdditionalFailureInfo"
argument_list|,
operator|(
name|Class
argument_list|<
name|?
argument_list|>
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"InterceptTestCaseEvents.failed(). Cannot invoke reportAdditionalFailureInfo() method in"
operator|+
literal|" consuming class, is it declared and public?"
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|failed
argument_list|(
name|e
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finished
specifier|public
name|void
name|finished
parameter_list|(
name|FrameworkMethod
name|method
parameter_list|)
block|{
name|super
operator|.
name|finished
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|starting
specifier|public
name|void
name|starting
parameter_list|(
name|FrameworkMethod
name|method
parameter_list|)
block|{
name|super
operator|.
name|starting
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|succeeded
specifier|public
name|void
name|succeeded
parameter_list|(
name|FrameworkMethod
name|method
parameter_list|)
block|{
name|super
operator|.
name|succeeded
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

