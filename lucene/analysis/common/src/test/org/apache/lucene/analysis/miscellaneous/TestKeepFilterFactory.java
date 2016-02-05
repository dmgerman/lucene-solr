begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
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
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
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
name|analysis
operator|.
name|util
operator|.
name|ClasspathResourceLoader
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
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
import|;
end_import

begin_class
DECL|class|TestKeepFilterFactory
specifier|public
class|class
name|TestKeepFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testInform
specifier|public
name|void
name|testInform
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceLoader
name|loader
init|=
operator|new
name|ClasspathResourceLoader
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"loader is null and it shouldn't be"
argument_list|,
name|loader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|KeepWordFilterFactory
name|factory
init|=
operator|(
name|KeepWordFilterFactory
operator|)
name|tokenFilterFactory
argument_list|(
literal|"KeepWord"
argument_list|,
literal|"words"
argument_list|,
literal|"keep-1.txt"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
name|factory
operator|.
name|getWords
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|(
name|KeepWordFilterFactory
operator|)
name|tokenFilterFactory
argument_list|(
literal|"KeepWord"
argument_list|,
literal|"words"
argument_list|,
literal|"keep-1.txt, keep-2.txt"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|words
operator|=
name|factory
operator|.
name|getWords
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|tokenFilterFactory
argument_list|(
literal|"KeepWord"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

