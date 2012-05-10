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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**  *<p>  * This class provides a {@link Field} that enables storing  * of a per-document byte value for scoring, sorting or value retrieval. Here's an  * example usage:  *   *<pre>  *   document.add(new ByteDocValuesField(name, (byte) 22));  *</pre>  *   *<p>  * If you also need to store the value, you should add a  * separate {@link StoredField} instance.  * */
end_comment

begin_class
DECL|class|ByteDocValuesField
specifier|public
class|class
name|ByteDocValuesField
extends|extends
name|Field
block|{
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
name|setDocValueType
argument_list|(
name|DocValues
operator|.
name|Type
operator|.
name|FIXED_INTS_8
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|method|ByteDocValuesField
specifier|public
name|ByteDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
name|value
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
name|Byte
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

