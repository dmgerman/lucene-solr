begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
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
name|DocIdSet
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
name|Cell
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
name|util
operator|.
name|DocIdSetBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
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
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|SpatialRelation
import|;
end_import

begin_comment
comment|/**  * A Query matching documents that have an {@link SpatialRelation#INTERSECTS}  * (i.e. not DISTINCT) relationship with a provided query shape.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|IntersectsPrefixTreeQuery
specifier|public
class|class
name|IntersectsPrefixTreeQuery
extends|extends
name|AbstractVisitingPrefixTreeQuery
block|{
DECL|method|IntersectsPrefixTreeQuery
specifier|public
name|IntersectsPrefixTreeQuery
parameter_list|(
name|Shape
name|queryShape
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|int
name|prefixGridScanLevel
parameter_list|)
block|{
name|super
argument_list|(
name|queryShape
argument_list|,
name|fieldName
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|protected
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* Possible optimizations (in IN ADDITION TO THOSE LISTED IN VISITORTEMPLATE):      * If docFreq is 1 (or< than some small threshold), then check to see if we've already       collected it; if so short-circuit. Don't do this just for point data, as there is       no benefit, or only marginal benefit when multi-valued.      * Point query shape optimization when the only indexed data is a point (no leaves).  Result is a term query.       */
return|return
operator|new
name|VisitorTemplate
argument_list|(
name|context
argument_list|)
block|{
specifier|private
name|DocIdSetBuilder
name|results
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|results
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocIdSet
name|finish
parameter_list|()
block|{
return|return
name|results
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|visitPrefix
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cell
operator|.
name|getShapeRel
argument_list|()
operator|==
name|SpatialRelation
operator|.
name|WITHIN
operator|||
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
block|{
name|collectDocs
argument_list|(
name|results
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitLeaf
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
name|collectDocs
argument_list|(
name|results
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|getDocIdSet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
literal|"fieldName="
operator|+
name|fieldName
operator|+
literal|","
operator|+
literal|"queryShape="
operator|+
name|queryShape
operator|+
literal|","
operator|+
literal|"detailLevel="
operator|+
name|detailLevel
operator|+
literal|","
operator|+
literal|"prefixGridScanLevel="
operator|+
name|prefixGridScanLevel
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

