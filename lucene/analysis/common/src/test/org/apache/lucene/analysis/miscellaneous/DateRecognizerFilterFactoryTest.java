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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|BaseTokenStreamTestCase
import|;
end_import

begin_class
DECL|class|DateRecognizerFilterFactoryTest
specifier|public
class|class
name|DateRecognizerFilterFactoryTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testBadLanguageTagThrowsException
specifier|public
name|void
name|testBadLanguageTagThrowsException
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|DateRecognizerFilterFactory
operator|.
name|LOCALE
argument_list|,
literal|"en_US"
argument_list|)
expr_stmt|;
operator|new
name|DateRecognizerFilterFactory
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testGoodLocaleParsesWell
specifier|public
name|void
name|testGoodLocaleParsesWell
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|DateRecognizerFilterFactory
operator|.
name|LOCALE
argument_list|,
literal|"en-US"
argument_list|)
expr_stmt|;
operator|new
name|DateRecognizerFilterFactory
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

