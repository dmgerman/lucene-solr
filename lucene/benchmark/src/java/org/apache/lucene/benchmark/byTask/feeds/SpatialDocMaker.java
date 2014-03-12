begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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
name|context
operator|.
name|SpatialContextFactory
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|document
operator|.
name|Field
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
name|SpatialStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTreeFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
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
name|java
operator|.
name|util
operator|.
name|Random
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

begin_comment
comment|/**  * Indexes spatial data according to a configured {@link SpatialStrategy} with optional  * shape transformation via a configured {@link ShapeConverter}. The converter can turn points into  * circles and bounding boxes, in order to vary the type of indexing performance tests.  * Unless it's subclass-ed to do otherwise, this class configures a {@link SpatialContext},  * {@link SpatialPrefixTree}, and {@link RecursivePrefixTreeStrategy}. The Strategy is made  * available to a query maker via the static method {@link #getSpatialStrategy(int)}.  * See spatial.alg for a listing of spatial parameters, in particular those starting with "spatial."  * and "doc.spatial".  */
end_comment

begin_class
DECL|class|SpatialDocMaker
specifier|public
class|class
name|SpatialDocMaker
extends|extends
name|DocMaker
block|{
DECL|field|SPATIAL_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|SPATIAL_FIELD
init|=
literal|"spatial"
decl_stmt|;
comment|//cache spatialStrategy by round number
DECL|field|spatialStrategyCache
specifier|private
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|SpatialStrategy
argument_list|>
name|spatialStrategyCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|strategy
specifier|private
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|field|shapeConverter
specifier|private
name|ShapeConverter
name|shapeConverter
decl_stmt|;
comment|/**    * Looks up the SpatialStrategy from the given round --    * {@link org.apache.lucene.benchmark.byTask.utils.Config#getRoundNumber()}. It's an error    * if it wasn't created already for this round -- when SpatialDocMaker is initialized.    */
DECL|method|getSpatialStrategy
specifier|public
specifier|static
name|SpatialStrategy
name|getSpatialStrategy
parameter_list|(
name|int
name|roundNumber
parameter_list|)
block|{
name|SpatialStrategy
name|result
init|=
name|spatialStrategyCache
operator|.
name|get
argument_list|(
name|roundNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Strategy should have been init'ed by SpatialDocMaker by now"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Builds a SpatialStrategy from configuration options.    */
DECL|method|makeSpatialStrategy
specifier|protected
name|SpatialStrategy
name|makeSpatialStrategy
parameter_list|(
specifier|final
name|Config
name|config
parameter_list|)
block|{
comment|//A Map view of Config that prefixes keys with "spatial."
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configMap
init|=
operator|new
name|AbstractMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|config
operator|.
name|get
argument_list|(
literal|"spatial."
operator|+
name|key
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|SpatialContextFactory
operator|.
name|makeSpatialContext
argument_list|(
name|configMap
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//Some day the strategy might be initialized with a factory but such a factory
comment|// is non-existent.
return|return
name|makeSpatialStrategy
argument_list|(
name|config
argument_list|,
name|configMap
argument_list|,
name|ctx
argument_list|)
return|;
block|}
DECL|method|makeSpatialStrategy
specifier|protected
name|SpatialStrategy
name|makeSpatialStrategy
parameter_list|(
specifier|final
name|Config
name|config
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configMap
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
comment|//A factory for the prefix tree grid
name|SpatialPrefixTree
name|grid
init|=
name|SpatialPrefixTreeFactory
operator|.
name|makeSPT
argument_list|(
name|configMap
argument_list|,
literal|null
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|RecursivePrefixTreeStrategy
name|strategy
init|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|SPATIAL_FIELD
argument_list|)
block|{
block|{
comment|//protected field
name|this
operator|.
name|pointsOnly
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"spatial.docPointsOnly"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|int
name|prefixGridScanLevel
init|=
name|config
operator|.
name|get
argument_list|(
literal|"query.spatial.prefixGridScanLevel"
argument_list|,
operator|-
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixGridScanLevel
operator|<
literal|0
condition|)
name|prefixGridScanLevel
operator|=
name|grid
operator|.
name|getMaxLevels
argument_list|()
operator|+
name|prefixGridScanLevel
expr_stmt|;
name|strategy
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
name|double
name|distErrPct
init|=
name|config
operator|.
name|get
argument_list|(
literal|"spatial.distErrPct"
argument_list|,
literal|.025
argument_list|)
decl_stmt|;
comment|//doc& query; a default
name|strategy
operator|.
name|setDistErrPct
argument_list|(
name|distErrPct
argument_list|)
expr_stmt|;
return|return
name|strategy
return|;
block|}
annotation|@
name|Override
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|,
name|ContentSource
name|source
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|SpatialStrategy
name|existing
init|=
name|spatialStrategyCache
operator|.
name|get
argument_list|(
name|config
operator|.
name|getRoundNumber
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|==
literal|null
condition|)
block|{
comment|//new round; we need to re-initialize
name|strategy
operator|=
name|makeSpatialStrategy
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|spatialStrategyCache
operator|.
name|put
argument_list|(
name|config
operator|.
name|getRoundNumber
argument_list|()
argument_list|,
name|strategy
argument_list|)
expr_stmt|;
comment|//TODO remove previous round config?
name|shapeConverter
operator|=
name|makeShapeConverter
argument_list|(
name|strategy
argument_list|,
name|config
argument_list|,
literal|"doc.spatial."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Spatial Strategy: "
operator|+
name|strategy
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Optionally converts points to circles, and optionally bbox'es result.    */
DECL|method|makeShapeConverter
specifier|public
specifier|static
name|ShapeConverter
name|makeShapeConverter
parameter_list|(
specifier|final
name|SpatialStrategy
name|spatialStrategy
parameter_list|,
name|Config
name|config
parameter_list|,
name|String
name|configKeyPrefix
parameter_list|)
block|{
comment|//by default does no conversion
specifier|final
name|double
name|radiusDegrees
init|=
name|config
operator|.
name|get
argument_list|(
name|configKeyPrefix
operator|+
literal|"radiusDegrees"
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
specifier|final
name|double
name|plusMinus
init|=
name|config
operator|.
name|get
argument_list|(
name|configKeyPrefix
operator|+
literal|"radiusDegreesRandPlusMinus"
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|bbox
init|=
name|config
operator|.
name|get
argument_list|(
name|configKeyPrefix
operator|+
literal|"bbox"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|ShapeConverter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Shape
name|convert
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|Point
operator|&&
operator|(
name|radiusDegrees
operator|!=
literal|0.0
operator|||
name|plusMinus
operator|!=
literal|0.0
operator|)
condition|)
block|{
name|Point
name|point
init|=
operator|(
name|Point
operator|)
name|shape
decl_stmt|;
name|double
name|radius
init|=
name|radiusDegrees
decl_stmt|;
if|if
condition|(
name|plusMinus
operator|>
literal|0.0
condition|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|point
operator|.
name|hashCode
argument_list|()
argument_list|)
decl_stmt|;
comment|//use hashCode so it's reproducibly random
name|radius
operator|+=
name|random
operator|.
name|nextDouble
argument_list|()
operator|*
literal|2
operator|*
name|plusMinus
operator|-
name|plusMinus
expr_stmt|;
name|radius
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|radius
argument_list|)
expr_stmt|;
comment|//can happen if configured plusMinus> radiusDegrees
block|}
name|shape
operator|=
name|spatialStrategy
operator|.
name|getSpatialContext
argument_list|()
operator|.
name|makeCircle
argument_list|(
name|point
argument_list|,
name|radius
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bbox
condition|)
name|shape
operator|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
expr_stmt|;
return|return
name|shape
return|;
block|}
block|}
return|;
block|}
comment|/** Converts one shape to another. Created by    * {@link #makeShapeConverter(org.apache.lucene.spatial.SpatialStrategy, org.apache.lucene.benchmark.byTask.utils.Config, String)} */
DECL|interface|ShapeConverter
specifier|public
interface|interface
name|ShapeConverter
block|{
DECL|method|convert
name|Shape
name|convert
parameter_list|(
name|Shape
name|shape
parameter_list|)
function_decl|;
block|}
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|DocState
name|docState
init|=
name|getDocState
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|super
operator|.
name|makeDocument
argument_list|()
decl_stmt|;
comment|// Set SPATIAL_FIELD from body
name|DocData
name|docData
init|=
name|docState
operator|.
name|docData
decl_stmt|;
comment|//   makeDocument() resets docState.getBody() so we can't look there; look in Document
name|String
name|shapeStr
init|=
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|Shape
name|shape
init|=
name|makeShapeFromString
argument_list|(
name|strategy
argument_list|,
name|docData
operator|.
name|getName
argument_list|()
argument_list|,
name|shapeStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
name|shape
operator|=
name|shapeConverter
operator|.
name|convert
argument_list|(
name|shape
argument_list|)
expr_stmt|;
comment|//index
for|for
control|(
name|Field
name|f
range|:
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
return|;
block|}
DECL|method|makeShapeFromString
specifier|public
specifier|static
name|Shape
name|makeShapeFromString
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|shapeStr
parameter_list|)
block|{
if|if
condition|(
name|shapeStr
operator|!=
literal|null
operator|&&
name|shapeStr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
return|return
name|strategy
operator|.
name|getSpatialContext
argument_list|()
operator|.
name|readShapeFromWkt
argument_list|(
name|shapeStr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//InvalidShapeException TODO
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Shape "
operator|+
name|name
operator|+
literal|" wasn't parseable: "
operator|+
name|e
operator|+
literal|"  (skipping it)"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
comment|//TODO consider abusing the 'size' notion to number of shapes per document
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

