begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
DECL|class|TestFilterWeight
specifier|public
class|class
name|TestFilterWeight
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testDeclaredMethodsOverridden
specifier|public
name|void
name|testDeclaredMethodsOverridden
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|subClass
init|=
name|FilterWeight
operator|.
name|class
decl_stmt|;
name|implTestDeclaredMethodsOverridden
argument_list|(
name|subClass
operator|.
name|getSuperclass
argument_list|()
argument_list|,
name|subClass
argument_list|)
expr_stmt|;
block|}
DECL|method|implTestDeclaredMethodsOverridden
specifier|private
name|void
name|implTestDeclaredMethodsOverridden
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|superClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|subClass
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
specifier|final
name|Method
name|superClassMethod
range|:
name|superClass
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
specifier|final
name|int
name|modifiers
init|=
name|superClassMethod
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isFinal
argument_list|(
name|modifiers
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|modifiers
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|superClassMethod
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"bulkScorer"
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|Method
name|subClassMethod
init|=
name|subClass
operator|.
name|getDeclaredMethod
argument_list|(
name|superClassMethod
operator|.
name|getName
argument_list|()
argument_list|,
name|superClassMethod
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
name|subClass
operator|+
literal|" must not override\n'"
operator|+
name|superClassMethod
operator|+
literal|"'"
operator|+
literal|" but it does override\n'"
operator|+
name|subClassMethod
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|/* FilterWeight must not override the bulkScorer method            * since as of July 2016 not all deriving classes use the            * {code}return in.bulkScorer(content);{code}            * implementation that FilterWeight.bulkScorer would use.            */
continue|continue;
block|}
block|}
try|try
block|{
specifier|final
name|Method
name|subClassMethod
init|=
name|subClass
operator|.
name|getDeclaredMethod
argument_list|(
name|superClassMethod
operator|.
name|getName
argument_list|()
argument_list|,
name|superClassMethod
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getReturnType() difference"
argument_list|,
name|superClassMethod
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|subClassMethod
operator|.
name|getReturnType
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|subClass
operator|+
literal|" needs to override '"
operator|+
name|superClassMethod
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

