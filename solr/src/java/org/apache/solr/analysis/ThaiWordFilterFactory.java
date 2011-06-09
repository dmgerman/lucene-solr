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
name|th
operator|.
name|ThaiWordFilter
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

begin_comment
comment|/**   * Factory for {@link ThaiWordFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_thai" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;  *&lt;filter class="solr.ThaiWordFilterFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment

begin_class
DECL|class|ThaiWordFilterFactory
specifier|public
class|class
name|ThaiWordFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|method|create
specifier|public
name|ThaiWordFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
return|return
operator|new
name|ThaiWordFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
end_class

end_unit

