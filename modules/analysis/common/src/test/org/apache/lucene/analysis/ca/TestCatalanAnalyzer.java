begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ca
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ca
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Analyzer
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
DECL|class|TestCatalanAnalyzer
specifier|public
class|class
name|TestCatalanAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** This test fails with NPE when the     * stopwords file is missing in classpath */
DECL|method|testResourcesAvailable
specifier|public
name|void
name|testResourcesAvailable
parameter_list|()
block|{
operator|new
name|CatalanAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
comment|/** test stopwords and stemming */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|CatalanAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// stemming
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"llengÃ¼es"
argument_list|,
literal|"llengu"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"llengua"
argument_list|,
literal|"llengu"
argument_list|)
expr_stmt|;
comment|// stopword
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"un"
argument_list|,
operator|new
name|String
index|[]
block|{ }
argument_list|)
expr_stmt|;
block|}
comment|/** test use of exclusion set */
DECL|method|testExclude
specifier|public
name|void
name|testExclude
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|exclusionSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|exclusionSet
operator|.
name|add
argument_list|(
literal|"llengÃ¼es"
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|CatalanAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CatalanAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|,
name|exclusionSet
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"llengÃ¼es"
argument_list|,
literal|"llengÃ¼es"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"llengua"
argument_list|,
literal|"llengu"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

