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
name|FunctionQuery
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
name|FilteredQuery
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
name|query
operator|.
name|SpatialArgs
import|;
end_import

begin_comment
comment|/**  * The SpatialStrategy encapsulates an approach to indexing and searching based  * on shapes.  *<p/>  * Different implementations will support different features. A strategy should  * document these common elements:  *<ul>  *<li>Can it index more than one shape per field?</li>  *<li>What types of shapes can be indexed?</li>  *<li>What types of query shapes can be used?</li>  *<li>What types of query operations are supported?  *   This might vary per shape.</li>  *<li>Are there caches?  Under what circumstances are they used?  *   Roughly how big are they?  Is it segmented by Lucene segments, such as is  *   done by the Lucene {@link org.apache.lucene.search.FieldCache} and  *   {@link org.apache.lucene.index.DocValues} (ideal) or is it for the entire  *   index?  *</ul>  *<p/>  * Note that a SpatialStrategy is not involved with the Lucene stored field  * values of shapes, which is immaterial to indexing& search.  *<p/>  * Thread-safe.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialStrategy
specifier|public
specifier|abstract
class|class
name|SpatialStrategy
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
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
comment|/**    * Constructs the spatial strategy with its mandatory arguments.    */
DECL|method|SpatialStrategy
specifier|public
name|SpatialStrategy
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ctx is required"
argument_list|)
throw|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
if|if
condition|(
name|fieldName
operator|==
literal|null
operator|||
name|fieldName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fieldName is required"
argument_list|)
throw|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
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
comment|/**    * The name of the field or the prefix of them if there are multiple    * fields needed internally.    * @return Not null.    */
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
comment|/**    * Returns the IndexableField(s) from the<code>shape</code> that are to be    * added to the {@link org.apache.lucene.document.Document}.  These fields    * are expected to be marked as indexed and not stored.    *<p/>    * Note: If you want to<i>store</i> the shape as a string for retrieval in    * search results, you could add it like this:    *<pre>document.add(new StoredField(fieldName,ctx.toString(shape)));</pre>    * The particular string representation used doesn't matter to the Strategy    * since it doesn't use it.    *    * @return Not null nor will it have null elements.    */
DECL|method|createIndexableFields
specifier|public
specifier|abstract
name|IndexableField
index|[]
name|createIndexableFields
parameter_list|(
name|Shape
name|shape
parameter_list|)
function_decl|;
comment|/**    * The value source yields a number that is proportional to the distance between the query shape and indexed data.    */
DECL|method|makeValueSource
specifier|public
specifier|abstract
name|ValueSource
name|makeValueSource
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
function_decl|;
comment|/**    * Make a query which has a score based on the distance from the data to the query shape.    * The default implementation constructs a {@link FilteredQuery} based on    * {@link #makeFilter(org.apache.lucene.spatial.query.SpatialArgs)} and    * {@link #makeValueSource(org.apache.lucene.spatial.query.SpatialArgs)}.    */
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
name|Filter
name|filter
init|=
name|makeFilter
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|ValueSource
name|vs
init|=
name|makeValueSource
argument_list|(
name|args
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilteredQuery
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
name|vs
argument_list|)
argument_list|,
name|filter
argument_list|)
return|;
block|}
comment|/**    * Make a Filter    */
DECL|method|makeFilter
specifier|public
specifier|abstract
name|Filter
name|makeFilter
parameter_list|(
name|SpatialArgs
name|args
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
literal|" field:"
operator|+
name|fieldName
operator|+
literal|" ctx="
operator|+
name|ctx
return|;
block|}
block|}
end_class

end_unit

