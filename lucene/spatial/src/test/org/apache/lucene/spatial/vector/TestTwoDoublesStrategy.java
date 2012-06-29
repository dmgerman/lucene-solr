begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.vector
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|vector
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|simple
operator|.
name|SimpleSpatialContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|exception
operator|.
name|InvalidShapeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Circle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|simple
operator|.
name|CircleImpl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|simple
operator|.
name|PointImpl
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
name|FieldCache
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
name|Query
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
name|spatial
operator|.
name|SpatialMatchConcern
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
name|spatial
operator|.
name|StrategyTestCase
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
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
name|spatial
operator|.
name|util
operator|.
name|NumericFieldInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestTwoDoublesStrategy
specifier|public
class|class
name|TestTwoDoublesStrategy
extends|extends
name|StrategyTestCase
argument_list|<
name|TwoDoublesFieldInfo
argument_list|>
block|{
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
name|this
operator|.
name|ctx
operator|=
name|SimpleSpatialContext
operator|.
name|GEO_KM
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|TwoDoublesStrategy
argument_list|(
name|ctx
argument_list|,
operator|new
name|NumericFieldInfo
argument_list|()
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_DOUBLE_PARSER
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
operator|new
name|TwoDoublesFieldInfo
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCircleShapeSupport
specifier|public
name|void
name|testCircleShapeSupport
parameter_list|()
block|{
name|Circle
name|circle
init|=
operator|new
name|CircleImpl
argument_list|(
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|10
argument_list|,
name|this
operator|.
name|ctx
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|circle
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|this
operator|.
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|,
name|this
operator|.
name|fieldInfo
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidShapeException
operator|.
name|class
argument_list|)
DECL|method|testInvalidQueryShape
specifier|public
name|void
name|testInvalidQueryShape
parameter_list|()
block|{
name|Point
name|point
init|=
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|point
argument_list|)
decl_stmt|;
name|this
operator|.
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|,
name|this
operator|.
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCitiesWithinBBox
specifier|public
name|void
name|testCitiesWithinBBox
parameter_list|()
throws|throws
name|IOException
block|{
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_WORLD_CITIES_POINTS
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_Cities_IsWithin_BBox
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

