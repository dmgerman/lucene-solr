begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  *<p>  * Field that stores  * a per-document {@link BytesRef} value.  The values are  * stored indirectly, such that many documents sharing the  * same value all point to a single copy of the value, which  * is a good fit when the fields share values.  If values  * are (mostly) unique it's better to use {@link  * StraightBytesDocValuesField}.  Here's an example usage:   *   *<pre class="prettyprint">  *   document.add(new DerefBytesDocValuesField(name, new BytesRef("hello")));  *</pre>  *   *<p>  * If you also need to store the value, you should add a  * separate {@link StoredField} instance.  *   * */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|DerefBytesDocValuesField
specifier|public
class|class
name|DerefBytesDocValuesField
extends|extends
name|SortedBytesDocValuesField
block|{
comment|/**    * Create a new variable-length indirect DocValues field.    *<p>    * This calls     * {@link DerefBytesDocValuesField#DerefBytesDocValuesField(String, BytesRef, boolean)    *  DerefBytesDocValuesField(name, bytes, false}, meaning by default    * it allows for values of different lengths. If your values are all     * the same length, use that constructor instead.    * @param name field name    * @param bytes binary content    * @throws IllegalArgumentException if the field name is null    */
DECL|method|DerefBytesDocValuesField
specifier|public
name|DerefBytesDocValuesField
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
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new fixed or variable length indirect DocValues field.    *<p>    * @param name field name    * @param bytes binary content    * @param isFixedLength true if all values have the same length.    * @throws IllegalArgumentException if the field name is null    */
DECL|method|DerefBytesDocValuesField
specifier|public
name|DerefBytesDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|bytes
parameter_list|,
name|boolean
name|isFixedLength
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

