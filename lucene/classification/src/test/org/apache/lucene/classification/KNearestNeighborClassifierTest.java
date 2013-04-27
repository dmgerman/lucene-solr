begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
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
name|MockAnalyzer
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
name|BytesRef
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

begin_comment
comment|/**  * Testcase for {@link KNearestNeighborClassifier}  */
end_comment

begin_class
DECL|class|KNearestNeighborClassifierTest
specifier|public
class|class
name|KNearestNeighborClassifierTest
extends|extends
name|ClassificationTestBase
argument_list|<
name|BytesRef
argument_list|>
block|{
annotation|@
name|Test
DECL|method|testBasicUsage
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|checkCorrectClassification
argument_list|(
operator|new
name|KNearestNeighborClassifier
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TECHNOLOGY_INPUT
argument_list|,
name|TECHNOLOGY_RESULT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|categoryFieldName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

