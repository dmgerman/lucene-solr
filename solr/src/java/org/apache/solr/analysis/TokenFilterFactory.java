begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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

begin_comment
comment|/**  * A<code>TokenFilterFactory</code> creates a   *<code>TokenFilter</code> to transform one<code>TokenStream</code>   * into another.  *  *<p>  * TokenFilterFactories are registered for<code>FieldType</code>s with the  *<code>IndexSchema</code> through the<code>schema.xml</code> file.  *</p>  *<p>  * Example<code>schema.xml</code> entry to register a TokenFilterFactory   * implementation to transform tokens in a field of type "cool"  *</p>  *<pre>  *&lt;fieldtype name="cool" class="solr.TextField"&gt;  *&lt;analyzer&gt;  *      ...  *&lt;filter class="foo.MyTokenFilterFactory"/&gt;  *      ...  *</pre>  *<p>  * A single instance of any registered TokenFilterFactory is created  * via the default constructor and is reused for each FieldType.  *</p>  *  */
end_comment

begin_interface
DECL|interface|TokenFilterFactory
specifier|public
interface|interface
name|TokenFilterFactory
block|{
comment|/**<code>init</code> will be called just once, immediately after creation.    *<p>The args are user-level initialization parameters that    * may be specified when declaring the factory in the    * schema.xml    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
function_decl|;
comment|/**    * Accessor method for reporting the args used to initialize this factory.    *<p>    * Implementations are<strong>strongly</strong> encouraged to return     * the contents of the Map passed to to the init method    *</p>    */
DECL|method|getArgs
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getArgs
parameter_list|()
function_decl|;
comment|/** Transform the specified input TokenStream */
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

