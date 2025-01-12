begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|search
operator|.
name|highlight
operator|.
name|Fragmenter
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
name|search
operator|.
name|highlight
operator|.
name|NullFragmenter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|HighlightParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.lucene.search.highlight.Fragmenter} that tries to produce snippets that "look" like a regular   * expression.  *  *<code>solrconfig.xml</code> parameters:  *<ul>  *<li><code>hl.regex.pattern</code>: regular expression corresponding to "nice" fragments.</li>  *<li><code>hl.regex.slop</code>: how far the fragmenter can stray from the ideal fragment size.        A slop of 0.2 means that the fragmenter can go over or under by 20%.</li>  *<li><code>hl.regex.maxAnalyzedChars</code>: how many characters to apply the        regular expression to (independent from the global highlighter setting).</li>  *</ul>  *  * NOTE: the default for<code>maxAnalyzedChars</code> is much lower for this   * fragmenter.  After this limit is exhausted, fragments are produced in the  * same way as<code>GapFragmenter</code>  */
end_comment

begin_class
DECL|class|RegexFragmenter
specifier|public
class|class
name|RegexFragmenter
extends|extends
name|HighlightingPluginBase
implements|implements
name|SolrFragmenter
block|{
DECL|field|defaultPatternRaw
specifier|protected
name|String
name|defaultPatternRaw
decl_stmt|;
DECL|field|defaultPattern
specifier|protected
name|Pattern
name|defaultPattern
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
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
name|defaultPatternRaw
operator|=
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_PATTERN_RAW
expr_stmt|;
if|if
condition|(
name|defaults
operator|!=
literal|null
condition|)
block|{
name|defaultPatternRaw
operator|=
name|defaults
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|PATTERN
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_PATTERN_RAW
argument_list|)
expr_stmt|;
block|}
name|defaultPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|defaultPatternRaw
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFragmenter
specifier|public
name|Fragmenter
name|getFragmenter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|numRequests
operator|.
name|inc
argument_list|()
expr_stmt|;
name|params
operator|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|params
argument_list|,
name|defaults
argument_list|)
expr_stmt|;
name|int
name|fragsize
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_FRAGMENT_SIZE
argument_list|)
decl_stmt|;
name|int
name|increment
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|INCREMENT
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_INCREMENT_GAP
argument_list|)
decl_stmt|;
name|float
name|slop
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SLOP
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_SLOP
argument_list|)
decl_stmt|;
name|int
name|maxchars
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|MAX_RE_CHARS
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_MAX_ANALYZED_CHARS
argument_list|)
decl_stmt|;
name|String
name|rawpat
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|PATTERN
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_PATTERN_RAW
argument_list|)
decl_stmt|;
name|Pattern
name|p
init|=
name|rawpat
operator|==
name|defaultPatternRaw
condition|?
name|defaultPattern
else|:
name|Pattern
operator|.
name|compile
argument_list|(
name|rawpat
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragsize
operator|<=
literal|0
condition|)
block|{
return|return
operator|new
name|NullFragmenter
argument_list|()
return|;
block|}
return|return
operator|new
name|LuceneRegexFragmenter
argument_list|(
name|fragsize
argument_list|,
name|increment
argument_list|,
name|slop
argument_list|,
name|maxchars
argument_list|,
name|p
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////
comment|//////////////////////// SolrInfoMBeans methods ///////////////////////
comment|///////////////////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"RegexFragmenter ("
operator|+
name|defaultPatternRaw
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

