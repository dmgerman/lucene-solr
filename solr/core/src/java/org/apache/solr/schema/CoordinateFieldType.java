begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_comment
comment|/**  * A CoordinateFieldType is the base class for {@link org.apache.solr.schema.FieldType}s that have semantics  * related to items in a coordinate system.  *<br>  * Implementations depend on a delegating work to a sub {@link org.apache.solr.schema.FieldType}, specified by  * either the {@link #SUB_FIELD_SUFFIX} or the {@link #SUB_FIELD_TYPE} (the latter is used if both are defined.  *<br>  * Example:  *<pre>&lt;fieldType name="xy" class="solr.PointType" dimension="2" subFieldType="double"/&gt;  *</pre>  * In theory, classes deriving from this should be able to do things like represent a point, a polygon, a line, etc.  *<br>  * NOTE: There can only be one sub Field Type.  *  */
end_comment

begin_class
DECL|class|CoordinateFieldType
specifier|public
specifier|abstract
class|class
name|CoordinateFieldType
extends|extends
name|AbstractSubTypeFieldType
block|{
comment|/**    * The dimension of the coordinate system    */
DECL|field|dimension
specifier|protected
name|int
name|dimension
decl_stmt|;
comment|/**    * 2 dimensional by default    */
DECL|field|DEFAULT_DIMENSION
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DIMENSION
init|=
literal|2
decl_stmt|;
DECL|field|DIMENSION
specifier|public
specifier|static
specifier|final
name|String
name|DIMENSION
init|=
literal|"dimension"
decl_stmt|;
DECL|method|getDimension
specifier|public
name|int
name|getDimension
parameter_list|()
block|{
return|return
name|dimension
return|;
block|}
block|}
end_class

end_unit

