begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|params
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|FacetSearchParamsTest
specifier|public
class|class
name|FacetSearchParamsTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testSearchParamsWithNullRequest
specifier|public
name|void
name|testSearchParamsWithNullRequest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|assertNull
argument_list|(
operator|new
name|FacetSearchParams
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"FacetSearchParams should throw IllegalArgumentException when not adding requests"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

