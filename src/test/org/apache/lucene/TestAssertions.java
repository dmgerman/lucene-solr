begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestAssertions
specifier|public
class|class
name|TestAssertions
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
try|try
block|{
assert|assert
name|Boolean
operator|.
name|FALSE
operator|.
name|booleanValue
argument_list|()
assert|;
name|fail
argument_list|(
literal|"assertions are not enabled!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

