begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|DocValues
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
name|DocValuesType
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
name|LeafReader
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
name|SortedSetDocValues
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
name|IndexOrDocValuesQuery
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  *<p>  * Field that stores  * a per-document {@link BytesRef} value, indexed for  * sorting.  Here's an example usage:  *   *<pre class="prettyprint">  *   document.add(new SortedDocValuesField(name, new BytesRef("hello")));  *</pre>  *   *<p>  * If you also need to store the value, you should add a  * separate {@link StoredField} instance.  *  *<p>  * This value can be at most 32766 bytes long.  * */
end_comment

begin_class
DECL|class|SortedDocValuesField
specifier|public
class|class
name|SortedDocValuesField
extends|extends
name|Field
block|{
comment|/**    * Type for sorted bytes DocValues    */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a new sorted DocValues field.    * @param name field name    * @param bytes binary content    * @throws IllegalArgumentException if the field name is null    */
DECL|method|SortedDocValuesField
specifier|public
name|SortedDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|bytes
expr_stmt|;
block|}
comment|/**    * Create a range query that matches all documents whose value is between    * {@code lowerValue} and {@code upperValue} included.    *<p>    * You can have half-open ranges by setting {@code lowerValue = null}    * or {@code upperValue = null}.    *<p><b>NOTE</b>: Such queries cannot efficiently advance to the next match,    * which makes them slow if they are not ANDed with a selective query. As a    * consequence, they are best used wrapped in an {@link IndexOrDocValuesQuery},    * alongside a range query that executes on points, such as    * {@link BinaryPoint#newRangeQuery}.    */
DECL|method|newRangeQuery
specifier|public
specifier|static
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|lowerValue
parameter_list|,
name|BytesRef
name|upperValue
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|)
block|{
return|return
operator|new
name|SortedSetDocValuesRangeQuery
argument_list|(
name|field
argument_list|,
name|lowerValue
argument_list|,
name|upperValue
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
block|{
annotation|@
name|Override
name|SortedSetDocValues
name|getValues
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DocValues
operator|.
name|singleton
argument_list|(
name|DocValues
operator|.
name|getSorted
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**     * Create a query for matching an exact {@link BytesRef} value.    *<p><b>NOTE</b>: Such queries cannot efficiently advance to the next match,    * which makes them slow if they are not ANDed with a selective query. As a    * consequence, they are best used wrapped in an {@link IndexOrDocValuesQuery},    * alongside a range query that executes on points, such as    * {@link BinaryPoint#newExactQuery}.    */
DECL|method|newExactQuery
specifier|public
specifier|static
name|Query
name|newExactQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

