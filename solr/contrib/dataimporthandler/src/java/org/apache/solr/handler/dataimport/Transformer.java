begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<p>  * Use this API to implement a custom transformer for any given entity  *</p>  *<p>  * Implementations of this abstract class must provide a public no-args constructor.  *</p>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p>  *<b>This API is experimental and may change in the future.</b>  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|Transformer
specifier|public
specifier|abstract
class|class
name|Transformer
block|{
comment|/**    * The input is a row of data and the output has to be a new row.    *    * @param context The current context    * @param row     A row of data    * @return The changed data. It must be a {@link Map}&lt;{@link String}, {@link Object}&gt; if it returns    *         only one row or if there are multiple rows to be returned it must    *         be a {@link java.util.List}&lt;{@link Map}&lt;{@link String}, {@link Object}&gt;&gt;    */
DECL|method|transformRow
specifier|public
specifier|abstract
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
function_decl|;
block|}
end_class

end_unit

