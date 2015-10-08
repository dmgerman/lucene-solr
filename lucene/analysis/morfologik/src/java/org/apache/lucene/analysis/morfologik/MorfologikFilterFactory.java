begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.morfologik
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|morfologik
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|morfologik
operator|.
name|stemming
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|morfologik
operator|.
name|stemming
operator|.
name|DictionaryMetadata
import|;
end_import

begin_import
import|import
name|morfologik
operator|.
name|stemming
operator|.
name|polish
operator|.
name|PolishStemmer
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
name|util
operator|.
name|ResourceLoader
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
name|util
operator|.
name|ResourceLoaderAware
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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import

begin_comment
comment|/**  * Filter factory for {@link MorfologikFilter}.   *   *<p>An explicit resource name of the dictionary ({@code ".dict"}) can be   * provided via the<code>dictionary</code> attribute, as the example below demonstrates:  *<pre class="prettyprint">  *&lt;fieldType name="text_mylang" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.MorfologikFilterFactory" dictionary="mylang.dict" /&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   *<p>If the dictionary attribute is not provided, the Polish dictionary is loaded  * and used by default.   *   * @see<a href="http://morfologik.blogspot.com/">Morfologik web site</a>  */
end_comment

begin_class
DECL|class|MorfologikFilterFactory
specifier|public
class|class
name|MorfologikFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
comment|/** Dictionary resource attribute (should have {@code ".dict"} suffix), loaded from {@link ResourceLoader}. */
DECL|field|DICTIONARY_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|DICTIONARY_ATTRIBUTE
init|=
literal|"dictionary"
decl_stmt|;
comment|/** {@link #DICTIONARY_ATTRIBUTE} value passed to {@link #inform}. */
DECL|field|resourceName
specifier|private
name|String
name|resourceName
decl_stmt|;
comment|/** Loaded {@link Dictionary}, initialized on {@link #inform(ResourceLoader)}. */
DECL|field|dictionary
specifier|private
name|Dictionary
name|dictionary
decl_stmt|;
comment|/** Creates a new MorfologikFilterFactory */
DECL|method|MorfologikFilterFactory
specifier|public
name|MorfologikFilterFactory
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
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// Be specific about no-longer-supported dictionary attribute.
specifier|final
name|String
name|DICTIONARY_RESOURCE_ATTRIBUTE
init|=
literal|"dictionary-resource"
decl_stmt|;
name|String
name|dictionaryResource
init|=
name|get
argument_list|(
name|args
argument_list|,
name|DICTIONARY_RESOURCE_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|dictionaryResource
operator|!=
literal|null
operator|&&
operator|!
name|dictionaryResource
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The "
operator|+
name|DICTIONARY_RESOURCE_ATTRIBUTE
operator|+
literal|" attribute is no "
operator|+
literal|"longer supported. Use the '"
operator|+
name|DICTIONARY_ATTRIBUTE
operator|+
literal|"' attribute instead (see LUCENE-6833)."
argument_list|)
throw|;
block|}
name|resourceName
operator|=
name|get
argument_list|(
name|args
argument_list|,
name|DICTIONARY_ATTRIBUTE
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|resourceName
operator|==
literal|null
condition|)
block|{
comment|// Get the dictionary lazily, does not hold up memory.
name|this
operator|.
name|dictionary
operator|=
operator|new
name|PolishStemmer
argument_list|()
operator|.
name|getDictionary
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
name|InputStream
name|dict
init|=
name|loader
operator|.
name|openResource
argument_list|(
name|resourceName
argument_list|)
init|;            InputStream meta = loader.openResource(DictionaryMetadata.getExpectedMetadataFileName(resourceName)
block|)
block|)
block|{
name|this
operator|.
name|dictionary
operator|=
name|Dictionary
operator|.
name|read
argument_list|(
name|dict
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_function
unit|}    @
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|ts
parameter_list|)
block|{
return|return
operator|new
name|MorfologikFilter
argument_list|(
name|ts
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|dictionary
argument_list|,
literal|"MorfologikFilterFactory was not fully initialized."
argument_list|)
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

