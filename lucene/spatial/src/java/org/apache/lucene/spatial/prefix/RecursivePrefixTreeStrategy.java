begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
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
name|search
operator|.
name|Filter
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
name|SimpleSpatialFieldInfo
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
name|query
operator|.
name|UnsupportedSpatialOperation
import|;
end_import

begin_comment
comment|/**  * Based on {@link RecursivePrefixTreeFilter}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|RecursivePrefixTreeStrategy
specifier|public
class|class
name|RecursivePrefixTreeStrategy
extends|extends
name|PrefixTreeStrategy
block|{
DECL|field|prefixGridScanLevel
specifier|private
name|int
name|prefixGridScanLevel
decl_stmt|;
comment|//TODO how is this customized?
DECL|method|RecursivePrefixTreeStrategy
specifier|public
name|RecursivePrefixTreeStrategy
parameter_list|(
name|SpatialPrefixTree
name|grid
parameter_list|)
block|{
name|super
argument_list|(
name|grid
argument_list|)
expr_stmt|;
name|prefixGridScanLevel
operator|=
name|grid
operator|.
name|getMaxLevels
argument_list|()
operator|-
literal|4
expr_stmt|;
comment|//TODO this default constant is dependent on the prefix grid size
block|}
DECL|method|setPrefixGridScanLevel
specifier|public
name|void
name|setPrefixGridScanLevel
parameter_list|(
name|int
name|prefixGridScanLevel
parameter_list|)
block|{
name|this
operator|.
name|prefixGridScanLevel
operator|=
name|prefixGridScanLevel
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(prefixGridScanLevel:"
operator|+
name|prefixGridScanLevel
operator|+
literal|",SPG:("
operator|+
name|grid
operator|+
literal|"))"
return|;
block|}
annotation|@
name|Override
DECL|method|makeFilter
specifier|public
name|Filter
name|makeFilter
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|SimpleSpatialFieldInfo
name|fieldInfo
parameter_list|)
block|{
specifier|final
name|SpatialOperation
name|op
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|SpatialOperation
operator|.
name|is
argument_list|(
name|op
argument_list|,
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|SpatialOperation
operator|.
name|BBoxWithin
argument_list|,
name|SpatialOperation
operator|.
name|BBoxIntersects
argument_list|)
condition|)
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|op
argument_list|)
throw|;
name|Shape
name|shape
init|=
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getMaxLevelForPrecision
argument_list|(
name|shape
argument_list|,
name|args
operator|.
name|getDistPrecision
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RecursivePrefixTreeFilter
argument_list|(
name|fieldInfo
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|grid
argument_list|,
name|shape
argument_list|,
name|prefixGridScanLevel
argument_list|,
name|detailLevel
argument_list|)
return|;
block|}
block|}
end_class

end_unit

