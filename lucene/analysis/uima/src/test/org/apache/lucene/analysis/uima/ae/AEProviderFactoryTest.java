begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.uima.ae
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
operator|.
name|ae
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link AEProviderFactory}  */
end_comment

begin_class
DECL|class|AEProviderFactoryTest
specifier|public
class|class
name|AEProviderFactoryTest
block|{
annotation|@
name|Test
DECL|method|testCorrectCaching
specifier|public
name|void
name|testCorrectCaching
parameter_list|()
throws|throws
name|Exception
block|{
name|AEProvider
name|aeProvider
init|=
name|AEProviderFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|getAEProvider
argument_list|(
literal|"/uima/TestAggregateSentenceAE.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|aeProvider
operator|==
name|AEProviderFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|getAEProvider
argument_list|(
literal|"/uima/TestAggregateSentenceAE.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCorrectCachingWithParameters
specifier|public
name|void
name|testCorrectCachingWithParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|AEProvider
name|aeProvider
init|=
name|AEProviderFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|getAEProvider
argument_list|(
literal|"prefix"
argument_list|,
literal|"/uima/TestAggregateSentenceAE.xml"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|aeProvider
operator|==
name|AEProviderFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|getAEProvider
argument_list|(
literal|"prefix"
argument_list|,
literal|"/uima/TestAggregateSentenceAE.xml"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

