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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Base test class for Lucene test classes that test Locale-sensitive behavior.  *<p>  * This class will run tests under the default Locale, but then will also run  * tests under all available JVM locales. This is helpful to ensure tests will  * not fail under a different environment.  *</p>  */
end_comment

begin_class
DECL|class|LocalizedTestCase
specifier|public
specifier|abstract
class|class
name|LocalizedTestCase
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Before changing the default Locale, save the default Locale here so that it    * can be restored.    */
DECL|field|defaultLocale
specifier|private
specifier|final
name|Locale
name|defaultLocale
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|/**    * The locale being used as the system default Locale    */
DECL|field|locale
specifier|private
name|Locale
name|locale
decl_stmt|;
comment|/**    * An optional limited set of testcases that will run under different Locales.    */
DECL|field|testWithDifferentLocales
specifier|private
specifier|final
name|Set
name|testWithDifferentLocales
decl_stmt|;
DECL|method|LocalizedTestCase
specifier|public
name|LocalizedTestCase
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|testWithDifferentLocales
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|LocalizedTestCase
specifier|public
name|LocalizedTestCase
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|testWithDifferentLocales
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|LocalizedTestCase
specifier|public
name|LocalizedTestCase
parameter_list|(
name|Set
name|testWithDifferentLocales
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|testWithDifferentLocales
operator|=
name|testWithDifferentLocales
expr_stmt|;
block|}
DECL|method|LocalizedTestCase
specifier|public
name|LocalizedTestCase
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
name|testWithDifferentLocales
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|testWithDifferentLocales
operator|=
name|testWithDifferentLocales
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|Locale
operator|.
name|setDefault
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|defaultLocale
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|runBare
specifier|public
name|void
name|runBare
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// Do the test with the default Locale (default)
try|try
block|{
name|locale
operator|=
name|defaultLocale
expr_stmt|;
name|super
operator|.
name|runBare
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test failure of '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' occurred with the default Locale "
operator|+
name|locale
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
if|if
condition|(
name|testWithDifferentLocales
operator|==
literal|null
operator|||
name|testWithDifferentLocales
operator|.
name|contains
argument_list|(
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Do the test again under different Locales
name|Locale
name|systemLocales
index|[]
init|=
name|Locale
operator|.
name|getAvailableLocales
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
name|systemLocales
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|locale
operator|=
name|systemLocales
index|[
name|i
index|]
expr_stmt|;
name|super
operator|.
name|runBare
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test failure of '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' occurred under a different Locale "
operator|+
name|locale
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

