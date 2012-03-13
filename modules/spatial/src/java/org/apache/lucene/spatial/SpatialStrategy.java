begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|query
operator|.
name|SpatialArgs
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
name|index
operator|.
name|IndexableField
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|Query
import|;
end_import

begin_comment
comment|/**  * must be thread safe  */
end_comment

begin_class
DECL|class|SpatialStrategy
specifier|public
specifier|abstract
class|class
name|SpatialStrategy
parameter_list|<
name|T
extends|extends
name|SpatialFieldInfo
parameter_list|>
block|{
DECL|field|ignoreIncompatibleGeometry
specifier|protected
name|boolean
name|ignoreIncompatibleGeometry
init|=
literal|false
decl_stmt|;
DECL|field|ctx
specifier|protected
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
DECL|method|SpatialStrategy
specifier|public
name|SpatialStrategy
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
block|}
DECL|method|getSpatialContext
specifier|public
name|SpatialContext
name|getSpatialContext
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
comment|/** Corresponds with Solr's  FieldType.isPolyField(). */
DECL|method|isPolyField
specifier|public
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Corresponds with Solr's FieldType.createField().    *    * This may return a null field if it does not want to make anything.    * This is reasonable behavior if 'ignoreIncompatibleGeometry=true' and the    * geometry is incompatible    */
DECL|method|createField
specifier|public
specifier|abstract
name|IndexableField
name|createField
parameter_list|(
name|T
name|fieldInfo
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|store
parameter_list|)
function_decl|;
comment|/** Corresponds with Solr's FieldType.createFields(). */
DECL|method|createFields
specifier|public
name|IndexableField
index|[]
name|createFields
parameter_list|(
name|T
name|fieldInfo
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|store
parameter_list|)
block|{
return|return
operator|new
name|IndexableField
index|[]
block|{
name|createField
argument_list|(
name|fieldInfo
argument_list|,
name|shape
argument_list|,
name|index
argument_list|,
name|store
argument_list|)
block|}
return|;
block|}
DECL|method|makeValueSource
specifier|public
specifier|abstract
name|ValueSource
name|makeValueSource
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|T
name|fieldInfo
parameter_list|)
function_decl|;
comment|/**    * Make a query    */
DECL|method|makeQuery
specifier|public
specifier|abstract
name|Query
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|T
name|fieldInfo
parameter_list|)
function_decl|;
comment|/**    * Make a Filter    */
DECL|method|makeFilter
specifier|public
specifier|abstract
name|Filter
name|makeFilter
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|T
name|fieldInfo
parameter_list|)
function_decl|;
DECL|method|isIgnoreIncompatibleGeometry
specifier|public
name|boolean
name|isIgnoreIncompatibleGeometry
parameter_list|()
block|{
return|return
name|ignoreIncompatibleGeometry
return|;
block|}
DECL|method|setIgnoreIncompatibleGeometry
specifier|public
name|void
name|setIgnoreIncompatibleGeometry
parameter_list|(
name|boolean
name|ignoreIncompatibleGeometry
parameter_list|)
block|{
name|this
operator|.
name|ignoreIncompatibleGeometry
operator|=
name|ignoreIncompatibleGeometry
expr_stmt|;
block|}
block|}
end_class

end_unit

