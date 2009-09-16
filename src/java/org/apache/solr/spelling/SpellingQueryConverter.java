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

begin_comment
comment|/**  * Converts the query string to a Collection of Lucene tokens using a regular expression.  * Boolean operators AND and OR are skipped.  *  * @since solr 1.3  **/
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
literal|":|\\d+)))[^\\s]+"
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
comment|//TODO: Extract the words using a simple regex, but not query stuff, and then analyze them to produce the token stream
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
name|TokenStream
name|stream
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|word
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|word
operator|.
name|equals
argument_list|(
literal|"AND"
argument_list|)
operator|==
literal|false
operator|&&
name|word
operator|.
name|equals
argument_list|(
literal|"OR"
argument_list|)
operator|==
literal|false
condition|)
block|{
try|try
block|{
name|stream
operator|=
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
literal|""
argument_list|,
operator|new
name|StringReader
argument_list|(
name|word
argument_list|)
argument_list|)
expr_stmt|;
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|setStartOffset
argument_list|(
name|matcher
operator|.
name|start
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setEndOffset
argument_list|(
name|matcher
operator|.
name|end
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

