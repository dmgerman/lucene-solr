begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// -*- c-basic-offset: 2 -*-
end_comment

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
name|util
operator|.
name|*
import|;
end_import

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
name|morfologik
operator|.
name|stemming
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
name|analysis
operator|.
name|TokenFilter
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|KeywordAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|CharacterUtils
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
name|*
import|;
end_import

begin_comment
comment|/**  * {@link TokenFilter} using Morfologik library to transform input tokens into lemma and  * morphosyntactic (POS) tokens. Applies to Polish only.    *  *<p>MorfologikFilter contains a {@link MorphosyntacticTagsAttribute}, which provides morphosyntactic  * annotations for produced lemmas. See the Morfologik documentation for details.</p>  *   * @see<a href="http://morfologik.blogspot.com/">Morfologik project page</a>  */
end_comment

begin_class
DECL|class|MorfologikFilter
specifier|public
class|class
name|MorfologikFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tagsAtt
specifier|private
specifier|final
name|MorphosyntacticTagsAttribute
name|tagsAtt
init|=
name|addAttribute
argument_list|(
name|MorphosyntacticTagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|keywordAttr
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAttr
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|CharsRef
name|scratch
init|=
operator|new
name|CharsRef
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|charUtils
specifier|private
specifier|final
name|CharacterUtils
name|charUtils
decl_stmt|;
DECL|field|current
specifier|private
name|State
name|current
decl_stmt|;
DECL|field|input
specifier|private
specifier|final
name|TokenStream
name|input
decl_stmt|;
DECL|field|stemmer
specifier|private
specifier|final
name|IStemmer
name|stemmer
decl_stmt|;
DECL|field|lemmaList
specifier|private
name|List
argument_list|<
name|WordData
argument_list|>
name|lemmaList
decl_stmt|;
DECL|field|tagsList
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|StringBuilder
argument_list|>
name|tagsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|lemmaListIndex
specifier|private
name|int
name|lemmaListIndex
decl_stmt|;
comment|/**    * Creates MorfologikFilter    * @param in   input token stream    * @param version Lucene version compatibility for lowercasing.    */
DECL|method|MorfologikFilter
specifier|public
name|MorfologikFilter
parameter_list|(
specifier|final
name|TokenStream
name|in
parameter_list|,
specifier|final
name|Version
name|version
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|in
expr_stmt|;
comment|// SOLR-4007: temporarily substitute context class loader to allow finding dictionary resources.
name|Thread
name|me
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ClassLoader
name|cl
init|=
name|me
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
try|try
block|{
name|me
operator|.
name|setContextClassLoader
argument_list|(
name|PolishStemmer
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|stemmer
operator|=
operator|new
name|PolishStemmer
argument_list|()
expr_stmt|;
name|this
operator|.
name|charUtils
operator|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|this
operator|.
name|lemmaList
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|me
operator|.
name|setContextClassLoader
argument_list|(
name|cl
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A pattern used to split lemma forms.    */
DECL|field|lemmaSplitter
specifier|private
specifier|final
specifier|static
name|Pattern
name|lemmaSplitter
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\+|\\|"
argument_list|)
decl_stmt|;
DECL|method|popNextLemma
specifier|private
name|void
name|popNextLemma
parameter_list|()
block|{
comment|// One tag (concatenated) per lemma.
specifier|final
name|WordData
name|lemma
init|=
name|lemmaList
operator|.
name|get
argument_list|(
name|lemmaListIndex
operator|++
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|lemma
operator|.
name|getStem
argument_list|()
argument_list|)
expr_stmt|;
name|CharSequence
name|tag
init|=
name|lemma
operator|.
name|getTag
argument_list|()
decl_stmt|;
if|if
condition|(
name|tag
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|tags
init|=
name|lemmaSplitter
operator|.
name|split
argument_list|(
name|tag
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tags
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tagsList
operator|.
name|size
argument_list|()
operator|<=
name|i
condition|)
block|{
name|tagsList
operator|.
name|add
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|buffer
init|=
name|tagsList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|tags
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|tagsAtt
operator|.
name|setTags
argument_list|(
name|tagsList
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|tags
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tagsAtt
operator|.
name|setTags
argument_list|(
name|Collections
operator|.
expr|<
name|StringBuilder
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Lookup a given surface form of a token and update     * {@link #lemmaList} and {@link #lemmaListIndex} accordingly.     */
DECL|method|lookupSurfaceForm
specifier|private
name|boolean
name|lookupSurfaceForm
parameter_list|(
name|CharSequence
name|token
parameter_list|)
block|{
name|lemmaList
operator|=
name|this
operator|.
name|stemmer
operator|.
name|lookup
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|lemmaListIndex
operator|=
literal|0
expr_stmt|;
return|return
name|lemmaList
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/** Retrieves the next token (possibly from the list of lemmas). */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lemmaListIndex
operator|<
name|lemmaList
operator|.
name|size
argument_list|()
condition|)
block|{
name|restoreState
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|popNextLemma
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|keywordAttr
operator|.
name|isKeyword
argument_list|()
operator|&&
operator|(
name|lookupSurfaceForm
argument_list|(
name|termAtt
argument_list|)
operator|||
name|lookupSurfaceForm
argument_list|(
name|toLowercase
argument_list|(
name|termAtt
argument_list|)
argument_list|)
operator|)
condition|)
block|{
name|current
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|popNextLemma
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tagsAtt
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Convert to lowercase in-place.    */
DECL|method|toLowercase
specifier|private
name|CharSequence
name|toLowercase
parameter_list|(
name|CharSequence
name|chs
parameter_list|)
block|{
specifier|final
name|int
name|length
init|=
name|scratch
operator|.
name|length
operator|=
name|chs
operator|.
name|length
argument_list|()
decl_stmt|;
name|scratch
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|char
name|buffer
index|[]
init|=
name|scratch
operator|.
name|chars
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
control|)
block|{
name|i
operator|+=
name|Character
operator|.
name|toChars
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|chs
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|,
name|buffer
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|scratch
return|;
block|}
comment|/** Resets stems accumulator and hands over to superclass. */
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|lemmaListIndex
operator|=
literal|0
expr_stmt|;
name|lemmaList
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|tagsList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

