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

begin_comment
comment|/**  * Syntactic sugar for encoding floats as NumericDocValues  * via {@link Float#floatToRawIntBits(float)}.  *<p>  * Per-document floating point values can be retrieved via  * {@link org.apache.lucene.index.LeafReader#getNumericDocValues(String)}.  *<p>  *<b>NOTE</b>: In most all cases this will be rather inefficient,  * requiring four bytes per document. Consider encoding floating  * point values yourself with only as much precision as you require.  */
end_comment

begin_class
DECL|class|FloatDocValuesField
specifier|public
class|class
name|FloatDocValuesField
extends|extends
name|NumericDocValuesField
block|{
comment|/**     * Creates a new DocValues field with the specified 32-bit float value     * @param name field name    * @param value 32-bit float value    * @throws IllegalArgumentException if the field name is null    */
DECL|method|FloatDocValuesField
specifier|public
name|FloatDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setFloatValue
specifier|public
name|void
name|setFloatValue
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|super
operator|.
name|setLongValue
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLongValue
specifier|public
name|void
name|setLongValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from Float to Long"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

