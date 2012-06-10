begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Matcher
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
name|FlagsAttribute
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
name|OffsetAttribute
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
name|PayloadAttribute
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
name|TypeAttribute
import|;
end_import

begin_comment
comment|/**  * Converts the query string to a Collection of Lucene tokens using a regular expression.  * Boolean operators AND, OR, NOT are skipped.   *   * Each term is checked to determine if it is optional, required or prohibited.  Required  * terms output a {@link Token} with the {@link QueryConverter#REQUIRED_TERM_FLAG} set.  * Prohibited terms output a {@link Token} with the {@link QueryConverter#PROHIBITED_TERM_FLAG}   * set. If the query uses the plus (+) and minus (-) to denote required and prohibited, this  * determination will be accurate.  In the case boolean AND/OR/NOTs are used, this  * converter makes an uninformed guess as to whether the term would likely behave as if it  * is Required or Prohibited and sets the flags accordingly.  These flags are used downstream  * to generate collations for {@link WordBreakSolrSpellChecker}, in cases where an original   * term is split up into multiple Tokens.  *   * @since solr 1.3  **/
end_comment

begin_class
DECL|class|SpellingQueryConverter
specifier|public
class|class
name|SpellingQueryConverter
extends|extends
name|QueryConverter
block|{
comment|/*   * The following builds up a regular expression that matches productions   * of the syntax for NMTOKEN as per the W3C XML Recommendation - with one   * important exception (see below).   *   * http://www.w3.org/TR/2008/REC-xml-20081126/ - version used as reference   *   * http://www.w3.org/TR/REC-xml/#NT-Nmtoken   *   * An NMTOKEN is a series of one or more NAMECHAR characters, which is an   * extension of the NAMESTARTCHAR character class.   *   * The EXCEPTION referred to above concerns the colon, which is legal in an   * NMTOKEN, but cannot currently be used as a valid field name within Solr,   * as it is used to delimit the field name from the query string.   */
DECL|field|NAMESTARTCHAR_PARTS
specifier|final
specifier|static
name|String
index|[]
name|NAMESTARTCHAR_PARTS
init|=
block|{
literal|"A-Z_a-z"
block|,
literal|"\\xc0-\\xd6"
block|,
literal|"\\xd8-\\xf6"
block|,
literal|"\\xf8-\\u02ff"
block|,
literal|"\\u0370-\\u037d"
block|,
literal|"\\u037f-\\u1fff"
block|,
literal|"\\u200c-\\u200d"
block|,
literal|"\\u2070-\\u218f"
block|,
literal|"\\u2c00-\\u2fef"
block|,
literal|"\\u2001-\\ud7ff"
block|,
literal|"\\uf900-\\ufdcf"
block|,
literal|"\\ufdf0-\\ufffd"
block|}
decl_stmt|;
DECL|field|ADDITIONAL_NAMECHAR_PARTS
specifier|final
specifier|static
name|String
index|[]
name|ADDITIONAL_NAMECHAR_PARTS
init|=
block|{
literal|"\\-.0-9\\xb7"
block|,
literal|"\\u0300-\\u036f"
block|,
literal|"\\u203f-\\u2040"
block|}
decl_stmt|;
DECL|field|SURROGATE_PAIR
specifier|final
specifier|static
name|String
name|SURROGATE_PAIR
init|=
literal|"\\p{Cs}{2}"
decl_stmt|;
DECL|field|NMTOKEN
specifier|final
specifier|static
name|String
name|NMTOKEN
decl_stmt|;
static|static
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|NAMESTARTCHAR_PARTS
control|)
name|sb
operator|.
name|append
argument_list|(
name|part
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|part
range|:
name|ADDITIONAL_NAMECHAR_PARTS
control|)
name|sb
operator|.
name|append
argument_list|(
name|part
argument_list|)
expr_stmt|;
name|NMTOKEN
operator|=
literal|"(["
operator|+
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|"]|"
operator|+
name|SURROGATE_PAIR
operator|+
literal|")+"
expr_stmt|;
block|}
DECL|field|PATTERN
specifier|final
specifier|static
name|String
name|PATTERN
init|=
literal|"(?:(?!("
operator|+
name|NMTOKEN
operator|+
literal|":|\\d+)))[\\p{L}_\\-0-9]+"
decl_stmt|;
comment|// previous version: Pattern.compile("(?:(?!(\\w+:|\\d+)))\\w+");
DECL|field|QUERY_REGEX
specifier|protected
name|Pattern
name|QUERY_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|PATTERN
argument_list|)
decl_stmt|;
comment|/**    * Converts the original query string to a collection of Lucene Tokens.    * @param original the original query string    * @return a Collection of Lucene Tokens    */
annotation|@
name|Override
DECL|method|convert
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|>
name|convert
parameter_list|(
name|String
name|original
parameter_list|)
block|{
if|if
condition|(
name|original
operator|==
literal|null
condition|)
block|{
comment|// this can happen with q.alt = and no query
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|Collection
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|QUERY_REGEX
operator|.
name|matcher
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|String
name|nextWord
init|=
literal|null
decl_stmt|;
name|int
name|nextStartIndex
init|=
literal|0
decl_stmt|;
name|String
name|lastBooleanOp
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|nextWord
operator|!=
literal|null
operator|||
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|word
init|=
literal|null
decl_stmt|;
name|int
name|startIndex
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|nextWord
operator|!=
literal|null
condition|)
block|{
name|word
operator|=
name|nextWord
expr_stmt|;
name|startIndex
operator|=
name|nextStartIndex
expr_stmt|;
name|nextWord
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|word
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|startIndex
operator|=
name|matcher
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|nextWord
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|nextStartIndex
operator|=
name|matcher
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|"AND"
operator|.
name|equals
argument_list|(
name|word
argument_list|)
operator|||
literal|"OR"
operator|.
name|equals
argument_list|(
name|word
argument_list|)
operator|||
literal|"NOT"
operator|.
name|equals
argument_list|(
name|word
argument_list|)
condition|)
block|{
name|lastBooleanOp
operator|=
name|word
expr_stmt|;
continue|continue;
block|}
comment|// treat "AND NOT" as "NOT"...
if|if
condition|(
literal|"AND"
operator|.
name|equals
argument_list|(
name|nextWord
argument_list|)
operator|&&
name|original
operator|.
name|length
argument_list|()
operator|>
name|nextStartIndex
operator|+
literal|7
operator|&&
name|original
operator|.
name|substring
argument_list|(
name|nextStartIndex
argument_list|,
name|nextStartIndex
operator|+
literal|7
argument_list|)
operator|.
name|equals
argument_list|(
literal|"AND NOT"
argument_list|)
condition|)
block|{
name|nextWord
operator|=
literal|"NOT"
expr_stmt|;
block|}
name|int
name|flagValue
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|word
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
operator|||
operator|(
name|startIndex
operator|>
literal|0
operator|&&
name|original
operator|.
name|charAt
argument_list|(
name|startIndex
operator|-
literal|1
argument_list|)
operator|==
literal|'-'
operator|)
condition|)
block|{
name|flagValue
operator|=
name|PROHIBITED_TERM_FLAG
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|word
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'+'
operator|||
operator|(
name|startIndex
operator|>
literal|0
operator|&&
name|original
operator|.
name|charAt
argument_list|(
name|startIndex
operator|-
literal|1
argument_list|)
operator|==
literal|'+'
operator|)
condition|)
block|{
name|flagValue
operator|=
name|REQUIRED_TERM_FLAG
expr_stmt|;
comment|//we don't know the default operator so just assume the first operator isn't new.
block|}
elseif|else
if|if
condition|(
name|nextWord
operator|!=
literal|null
operator|&&
name|lastBooleanOp
operator|!=
literal|null
operator|&&
operator|!
name|nextWord
operator|.
name|equals
argument_list|(
name|lastBooleanOp
argument_list|)
operator|&&
operator|(
literal|"AND"
operator|.
name|equals
argument_list|(
name|nextWord
argument_list|)
operator|||
literal|"OR"
operator|.
name|equals
argument_list|(
name|nextWord
argument_list|)
operator|||
literal|"NOT"
operator|.
name|equals
argument_list|(
name|nextWord
argument_list|)
operator|)
condition|)
block|{
name|flagValue
operator|=
name|TERM_PRECEDES_NEW_BOOLEAN_OPERATOR_FLAG
expr_stmt|;
comment|//...unless the 1st boolean operator is a NOT, because only AND/OR can be default.
block|}
elseif|else
if|if
condition|(
name|nextWord
operator|!=
literal|null
operator|&&
name|lastBooleanOp
operator|==
literal|null
operator|&&
operator|!
name|nextWord
operator|.
name|equals
argument_list|(
name|lastBooleanOp
argument_list|)
operator|&&
operator|(
literal|"NOT"
operator|.
name|equals
argument_list|(
name|nextWord
argument_list|)
operator|)
condition|)
block|{
name|flagValue
operator|=
name|TERM_PRECEDES_NEW_BOOLEAN_OPERATOR_FLAG
expr_stmt|;
block|}
try|try
block|{
name|analyze
argument_list|(
name|result
argument_list|,
operator|new
name|StringReader
argument_list|(
name|word
argument_list|)
argument_list|,
name|startIndex
argument_list|,
name|flagValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: shouldn't we log something?
block|}
block|}
if|if
condition|(
name|lastBooleanOp
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Token
name|t
range|:
name|result
control|)
block|{
name|int
name|f
init|=
name|t
operator|.
name|getFlags
argument_list|()
decl_stmt|;
name|t
operator|.
name|setFlags
argument_list|(
name|f
operator||=
name|QueryConverter
operator|.
name|TERM_IN_BOOLEAN_QUERY_FLAG
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|analyze
specifier|protected
name|void
name|analyze
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|result
parameter_list|,
name|Reader
name|text
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|flagsAttValue
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|text
argument_list|)
decl_stmt|;
comment|// TODO: support custom attributes
name|CharTermAttribute
name|termAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TypeAttribute
name|typeAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|token
operator|.
name|copyBuffer
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setOffset
argument_list|(
name|offset
operator|+
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offset
operator|+
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setFlags
argument_list|(
name|flagsAttValue
argument_list|)
expr_stmt|;
comment|//overwriting any flags already set...
name|token
operator|.
name|setType
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPayload
argument_list|(
name|payloadAtt
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPositionIncrement
argument_list|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

