begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|analysis
operator|.
name|CharStream
import|;
end_import

begin_comment
comment|/**  * Factory for {@link LegacyHTMLStripCharFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_html_legacy" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;charFilter class="solr.LegacyHTMLStripCharFilterFactory"/&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;  *</pre>  *<p>  * This factory is<b>NOT</b> recommended for new users and should be  * considered<b>UNSUPPORTED</b>.  *</p>  *<p>  * In Solr version 3.5 and earlier,<tt>HTMLStripCharFilter(Factory)</tt>  * had known bugs in the offsets it provided, triggering e.g. exceptions in  * highlighting.  *</p>  *<p>  * This class is provided as possible alternative for people who depend on  * the "broken" behavior of<tt>HTMLStripCharFilter</tt> in Solr version 3.5  * and earlier, and/or who don't like the changes introduced by the Solr 3.6+  * version of<tt>HTMLStripCharFilterFactory</tt>.  (See the 3.6.0 release  * section of lucene/CHANGES.txt for a list of differences in behavior.)  *</p>  * @deprecated use {@link HTMLStripCharFilterFactory}  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|LegacyHTMLStripCharFilterFactory
specifier|public
class|class
name|LegacyHTMLStripCharFilterFactory
extends|extends
name|BaseCharFilterFactory
block|{
DECL|method|create
specifier|public
name|LegacyHTMLStripCharFilter
name|create
parameter_list|(
name|CharStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
end_class

end_unit

