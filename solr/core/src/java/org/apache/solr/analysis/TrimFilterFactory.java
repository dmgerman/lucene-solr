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
name|java
operator|.
name|util
operator|.
name|Map
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|miscellaneous
operator|.
name|TrimFilter
import|;
end_import

begin_comment
comment|/**  * Factory for {@link TrimFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_trm" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.NGramTokenizerFactory"/&gt;  *&lt;filter class="solr.TrimFilterFactory" updateOffsets="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  * @see TrimFilter  */
end_comment

begin_class
DECL|class|TrimFilterFactory
specifier|public
class|class
name|TrimFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|updateOffsets
specifier|protected
name|boolean
name|updateOffsets
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
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
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|args
operator|.
name|get
argument_list|(
literal|"updateOffsets"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|updateOffsets
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Error reading updateOffsets value.  Must be true or false."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|create
specifier|public
name|TrimFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|TrimFilter
argument_list|(
name|input
argument_list|,
name|updateOffsets
argument_list|)
return|;
block|}
block|}
end_class

end_unit

