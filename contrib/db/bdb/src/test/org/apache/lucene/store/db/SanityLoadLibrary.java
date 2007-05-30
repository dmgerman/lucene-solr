begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.db
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|db
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|EnvironmentConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Environment
import|;
end_import

begin_comment
comment|/**  * Simple sanity testing application to verify that the underlying   * native library can be loaded cleanly.  *  * For use in the build.xml of this contrib, to determine if tests   * should be skipped.  */
end_comment

begin_class
DECL|class|SanityLoadLibrary
specifier|public
class|class
name|SanityLoadLibrary
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|ignored
parameter_list|)
throws|throws
name|Exception
block|{
name|EnvironmentConfig
name|envConfig
init|=
name|EnvironmentConfig
operator|.
name|DEFAULT
decl_stmt|;
name|envConfig
operator|.
name|setAllowCreate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Environment
name|env
init|=
operator|new
name|Environment
argument_list|(
literal|null
argument_list|,
name|envConfig
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

