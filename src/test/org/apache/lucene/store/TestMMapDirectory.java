begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
DECL|class|TestMMapDirectory
specifier|public
class|class
name|TestMMapDirectory
extends|extends
name|TestCase
block|{
comment|// Simply verify that if there is a method in FSDirectory
comment|// that returns IndexInput or a subclass, that
comment|// MMapDirectory overrides it.
DECL|method|testIndexInputMethods
specifier|public
name|void
name|testIndexInputMethods
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
name|Class
name|FSDirectory
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.lucene.store.FSDirectory"
argument_list|)
decl_stmt|;
name|Class
name|IndexInput
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.lucene.store.IndexInput"
argument_list|)
decl_stmt|;
name|Class
name|MMapDirectory
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.lucene.store.MMapDirectory"
argument_list|)
decl_stmt|;
name|Method
index|[]
name|methods
init|=
name|FSDirectory
operator|.
name|getDeclaredMethods
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Method
name|method
init|=
name|methods
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|IndexInput
operator|.
name|isAssignableFrom
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
condition|)
block|{
comment|// There is a method that returns IndexInput or a
comment|// subclass of IndexInput
try|try
block|{
name|Method
name|m
init|=
name|MMapDirectory
operator|.
name|getMethod
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|MMapDirectory
condition|)
block|{
name|fail
argument_list|(
literal|"FSDirectory has method "
operator|+
name|method
operator|+
literal|" but MMapDirectory does not override"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Should not happen
name|fail
argument_list|(
literal|"unexpected NoSuchMethodException"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

