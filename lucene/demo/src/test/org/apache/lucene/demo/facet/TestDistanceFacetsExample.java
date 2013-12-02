begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetResult
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
name|search
operator|.
name|TopDocs
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
operator|.
name|SuppressCodecs
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

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
literal|"Lucene3x"
argument_list|)
DECL|class|TestDistanceFacetsExample
specifier|public
class|class
name|TestDistanceFacetsExample
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|DistanceFacetsExample
name|example
init|=
operator|new
name|DistanceFacetsExample
argument_list|()
decl_stmt|;
name|example
operator|.
name|index
argument_list|()
expr_stmt|;
name|FacetResult
name|result
init|=
name|example
operator|.
name|search
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dim=field path=[] value=3 childCount=4\n< 1 km (1)\n< 2 km (2)\n< 5 km (2)\n< 10 km (3)\n"
argument_list|,
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|example
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDrillDown
specifier|public
name|void
name|testDrillDown
parameter_list|()
throws|throws
name|Exception
block|{
name|DistanceFacetsExample
name|example
init|=
operator|new
name|DistanceFacetsExample
argument_list|()
decl_stmt|;
name|example
operator|.
name|index
argument_list|()
expr_stmt|;
name|TopDocs
name|hits
init|=
name|example
operator|.
name|drillDown
argument_list|(
name|example
operator|.
name|FIVE_KM
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|example
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

