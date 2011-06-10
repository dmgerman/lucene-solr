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
name|cjk
operator|.
name|CJKTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**   * Factory for {@link CJKTokenizer}.   *<pre class="prettyprint">  *&lt;fieldType name="text_cjk" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.CJKTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment

begin_class
DECL|class|CJKTokenizerFactory
specifier|public
class|class
name|CJKTokenizerFactory
extends|extends
name|BaseTokenizerFactory
block|{
DECL|method|create
specifier|public
name|CJKTokenizer
name|create
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
return|return
operator|new
name|CJKTokenizer
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

