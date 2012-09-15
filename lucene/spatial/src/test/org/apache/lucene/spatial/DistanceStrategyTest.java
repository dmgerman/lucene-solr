begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Name
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ParametersFactory
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
name|context
operator|.
name|SpatialContext
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
name|Shape
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
name|document
operator|.
name|Document
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
name|bbox
operator|.
name|BBoxStrategy
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
name|prefix
operator|.
name|RecursivePrefixTreeStrategy
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
name|prefix
operator|.
name|TermQueryPrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|GeohashPrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|QuadPrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|vector
operator|.
name|TwoDoublesStrategy
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * @author David Smiley - dsmiley@mitre.org  */
end_comment

begin_class
DECL|class|DistanceStrategyTest
specifier|public
class|class
name|DistanceStrategyTest
extends|extends
name|StrategyTestCase
block|{
annotation|@
name|ParametersFactory
DECL|method|parameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|ctorArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
name|SpatialPrefixTree
name|grid
decl_stmt|;
name|SpatialStrategy
name|strategy
decl_stmt|;
name|grid
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"recursive_quad"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"termquery_geohash"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|TwoDoublesStrategy
argument_list|(
name|ctx
argument_list|,
literal|"twodoubles"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|BBoxStrategy
argument_list|(
name|ctx
argument_list|,
literal|"bbox"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
return|return
name|ctorArgs
return|;
block|}
comment|// this is a hack for clover!
DECL|class|Param
specifier|static
class|class
name|Param
block|{
DECL|field|strategy
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|method|Param
name|Param
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|strategy
operator|.
name|getFieldName
argument_list|()
return|;
block|}
block|}
comment|//  private String fieldName;
DECL|method|DistanceStrategyTest
specifier|public
name|DistanceStrategyTest
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"strategy"
argument_list|)
name|Param
name|param
parameter_list|)
block|{
name|SpatialStrategy
name|strategy
init|=
name|param
operator|.
name|strategy
decl_stmt|;
name|this
operator|.
name|ctx
operator|=
name|strategy
operator|.
name|getSpatialContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDistanceOrder
specifier|public
name|void
name|testDistanceOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|adoc
argument_list|(
literal|"100"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"101"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"103"
argument_list|,
operator|(
name|Shape
operator|)
literal|null
argument_list|)
expr_stmt|;
comment|//test score for nothing
name|commit
argument_list|()
expr_stmt|;
comment|//FYI distances are in docid order
name|checkDistValueSource
argument_list|(
literal|"3,4"
argument_list|,
literal|2.8274937f
argument_list|,
literal|5.0898066f
argument_list|,
literal|180f
argument_list|)
expr_stmt|;
name|checkDistValueSource
argument_list|(
literal|"4,0"
argument_list|,
literal|3.6043684f
argument_list|,
literal|0.9975641f
argument_list|,
literal|180f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecipScore
specifier|public
name|void
name|testRecipScore
parameter_list|()
throws|throws
name|IOException
block|{
name|Point
name|p100
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|adoc
argument_list|(
literal|"100"
argument_list|,
name|p100
argument_list|)
expr_stmt|;
name|Point
name|p101
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|adoc
argument_list|(
literal|"101"
argument_list|,
name|p101
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"103"
argument_list|,
operator|(
name|Shape
operator|)
literal|null
argument_list|)
expr_stmt|;
comment|//test score for nothing
name|commit
argument_list|()
expr_stmt|;
name|double
name|dist
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|p100
argument_list|,
name|p101
argument_list|)
decl_stmt|;
name|Shape
name|queryShape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
literal|2.01
argument_list|,
literal|0.99
argument_list|,
name|dist
argument_list|)
decl_stmt|;
name|checkValueSource
argument_list|(
name|strategy
operator|.
name|makeRecipDistanceValueSource
argument_list|(
name|queryShape
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1.00f
block|,
literal|0.10f
block|,
literal|0f
block|}
argument_list|,
literal|0.09f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newDoc
specifier|protected
name|Document
name|newDoc
parameter_list|(
name|String
name|id
parameter_list|,
name|Shape
name|shape
parameter_list|)
block|{
comment|//called by adoc().  Make compatible with BBoxStrategy.
if|if
condition|(
name|shape
operator|!=
literal|null
operator|&&
name|strategy
operator|instanceof
name|BBoxStrategy
condition|)
name|shape
operator|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|shape
operator|.
name|getCenter
argument_list|()
argument_list|,
name|shape
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|newDoc
argument_list|(
name|id
argument_list|,
name|shape
argument_list|)
return|;
block|}
DECL|method|checkDistValueSource
name|void
name|checkDistValueSource
parameter_list|(
name|String
name|ptStr
parameter_list|,
name|float
modifier|...
name|distances
parameter_list|)
throws|throws
name|IOException
block|{
name|Point
name|pt
init|=
operator|(
name|Point
operator|)
name|ctx
operator|.
name|readShape
argument_list|(
name|ptStr
argument_list|)
decl_stmt|;
name|checkValueSource
argument_list|(
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|pt
argument_list|)
argument_list|,
name|distances
argument_list|,
literal|1.0e-4f
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

