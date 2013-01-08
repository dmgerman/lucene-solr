begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|compound
operator|.
name|CompoundWordTokenFilterBase
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
name|compound
operator|.
name|HyphenationCompoundWordTokenFilter
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
name|compound
operator|.
name|hyphenation
operator|.
name|HyphenationTree
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
name|*
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
name|util
operator|.
name|IOUtils
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_comment
comment|/**  * Factory for {@link HyphenationCompoundWordTokenFilter}.  *<p>  * This factory accepts the following parameters:  *<ul>  *<li><code>hyphenator</code> (mandatory): path to the FOP xml hyphenation pattern.   *  See<a href="http://offo.sourceforge.net/hyphenation/">http://offo.sourceforge.net/hyphenation/</a>.  *<li><code>encoding</code> (optional): encoding of the xml hyphenation file. defaults to UTF-8.  *<li><code>dictionary</code> (optional): dictionary of words. defaults to no dictionary.  *<li><code>minWordSize</code> (optional): minimal word length that gets decomposed. defaults to 5.  *<li><code>minSubwordSize</code> (optional): minimum length of subwords. defaults to 2.  *<li><code>maxSubwordSize</code> (optional): maximum length of subwords. defaults to 15.  *<li><code>onlyLongestMatch</code> (optional): if true, adds only the longest matching subword   *    to the stream. defaults to false.  *</ul>  *<p>  *<pre class="prettyprint">  *&lt;fieldType name="text_hyphncomp" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.HyphenationCompoundWordTokenFilterFactory" hyphenator="hyphenator.xml" encoding="UTF-8"  *         dictionary="dictionary.txt" minWordSize="5" minSubwordSize="2" maxSubwordSize="15" onlyLongestMatch="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  * @see HyphenationCompoundWordTokenFilter  */
end_comment

begin_class
DECL|class|HyphenationCompoundWordTokenFilterFactory
specifier|public
class|class
name|HyphenationCompoundWordTokenFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|dictionary
specifier|private
name|CharArraySet
name|dictionary
decl_stmt|;
DECL|field|hyphenator
specifier|private
name|HyphenationTree
name|hyphenator
decl_stmt|;
DECL|field|dictFile
specifier|private
name|String
name|dictFile
decl_stmt|;
DECL|field|hypFile
specifier|private
name|String
name|hypFile
decl_stmt|;
DECL|field|encoding
specifier|private
name|String
name|encoding
decl_stmt|;
DECL|field|minWordSize
specifier|private
name|int
name|minWordSize
decl_stmt|;
DECL|field|minSubwordSize
specifier|private
name|int
name|minSubwordSize
decl_stmt|;
DECL|field|maxSubwordSize
specifier|private
name|int
name|maxSubwordSize
decl_stmt|;
DECL|field|onlyLongestMatch
specifier|private
name|boolean
name|onlyLongestMatch
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
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|dictFile
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"dictionary"
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|containsKey
argument_list|(
literal|"encoding"
argument_list|)
condition|)
name|encoding
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"encoding"
argument_list|)
expr_stmt|;
name|hypFile
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"hyphenator"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|hypFile
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing required parameter: hyphenator"
argument_list|)
throw|;
block|}
name|minWordSize
operator|=
name|getInt
argument_list|(
literal|"minWordSize"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|)
expr_stmt|;
name|minSubwordSize
operator|=
name|getInt
argument_list|(
literal|"minSubwordSize"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|)
expr_stmt|;
name|maxSubwordSize
operator|=
name|getInt
argument_list|(
literal|"maxSubwordSize"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|)
expr_stmt|;
name|onlyLongestMatch
operator|=
name|getBoolean
argument_list|(
literal|"onlyLongestMatch"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|dictFile
operator|!=
literal|null
condition|)
comment|// the dictionary can be empty.
name|dictionary
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|dictFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// TODO: Broken, because we cannot resolve real system id
comment|// ResourceLoader should also supply method like ClassLoader to get resource URL
name|stream
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|hypFile
argument_list|)
expr_stmt|;
specifier|final
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|is
operator|.
name|setEncoding
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
comment|// if it's null let xml parser decide
name|is
operator|.
name|setSystemId
argument_list|(
name|hypFile
argument_list|)
expr_stmt|;
name|hyphenator
operator|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|HyphenationCompoundWordTokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|,
name|hyphenator
argument_list|,
name|dictionary
argument_list|,
name|minWordSize
argument_list|,
name|minSubwordSize
argument_list|,
name|maxSubwordSize
argument_list|,
name|onlyLongestMatch
argument_list|)
return|;
block|}
block|}
end_class

end_unit

