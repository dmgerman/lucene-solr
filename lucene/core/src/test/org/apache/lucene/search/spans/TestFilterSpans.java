begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestFilterSpans
specifier|public
class|class
name|TestFilterSpans
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testOverrides
specifier|public
name|void
name|testOverrides
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify that all methods of Spans are overridden by FilterSpans,
for|for
control|(
name|Method
name|m
range|:
name|FilterSpans
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|Spans
operator|.
name|class
condition|)
block|{
name|fail
argument_list|(
literal|"method "
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|" not overridden!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

