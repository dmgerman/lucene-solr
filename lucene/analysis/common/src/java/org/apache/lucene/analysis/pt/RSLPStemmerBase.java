begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.pt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pt
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|util
operator|.
name|CharArraySet
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
name|Version
import|;
end_import

begin_import
import|import static
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
name|StemmerUtil
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Base class for stemmers that use a set of RSLP-like stemming steps.  *<p>  * RSLP (Removedor de Sufixos da Lingua Portuguesa) is an algorithm designed  * originally for stemming the Portuguese language, described in the paper  *<i>A Stemming Algorithm for the Portuguese Language</i>, Orengo et. al.  *<p>  * Since this time a plural-only modification (RSLP-S) as well as a modification  * for the Galician language have been implemented. This class parses a configuration  * file that describes {@link Step}s, where each Step contains a set of {@link Rule}s.  *<p>  * The general rule format is:   *<blockquote>{ "suffix", N, "replacement", { "exception1", "exception2", ...}}</blockquote>  * where:  *<ul>  *<li><code>suffix</code> is the suffix to be removed (such as "inho").  *<li><code>N</code> is the min stem size, where stem is defined as the candidate stem   *       after removing the suffix (but before appending the replacement!)  *<li><code>replacement</code> is an optimal string to append after removing the suffix.  *       This can be the empty string.  *<li><code>exceptions</code> is an optional list of exceptions, patterns that should   *       not be stemmed. These patterns can be specified as whole word or suffix (ends-with)   *       patterns, depending upon the exceptions format flag in the step header.  *</ul>  *<p>  * A step is an ordered list of rules, with a structure in this format:  *<blockquote>{ "name", N, B, { "cond1", "cond2", ... }  *               ... rules ... };  *</blockquote>  * where:  *<ul>  *<li><code>name</code> is a name for the step (such as "Plural").  *<li><code>N</code> is the min word size. Words that are less than this length bypass  *       the step completely, as an optimization. Note: N can be zero, in this case this   *       implementation will automatically calculate the appropriate value from the underlying   *       rules.  *<li><code>B</code> is a "boolean" flag specifying how exceptions in the rules are matched.  *       A value of 1 indicates whole-word pattern matching, a value of 0 indicates that   *       exceptions are actually suffixes and should be matched with ends-with.  *<li><code>conds</code> are an optional list of conditions to enter the step at all. If  *       the list is non-empty, then a word must end with one of these conditions or it will  *       bypass the step completely as an optimization.  *</ul>  *<p>  * @see<a href="http://www.inf.ufrgs.br/~viviane/rslp/index.htm">RSLP description</a>  * @lucene.internal  */
end_comment

begin_class
DECL|class|RSLPStemmerBase
specifier|public
specifier|abstract
class|class
name|RSLPStemmerBase
block|{
comment|/**    * A basic rule, with no exceptions.    */
DECL|class|Rule
specifier|protected
specifier|static
class|class
name|Rule
block|{
DECL|field|suffix
specifier|protected
specifier|final
name|char
name|suffix
index|[]
decl_stmt|;
DECL|field|replacement
specifier|protected
specifier|final
name|char
name|replacement
index|[]
decl_stmt|;
DECL|field|min
specifier|protected
specifier|final
name|int
name|min
decl_stmt|;
comment|/**      * Create a rule.      * @param suffix suffix to remove      * @param min minimum stem length      * @param replacement replacement string      */
DECL|method|Rule
specifier|public
name|Rule
parameter_list|(
name|String
name|suffix
parameter_list|,
name|int
name|min
parameter_list|,
name|String
name|replacement
parameter_list|)
block|{
name|this
operator|.
name|suffix
operator|=
name|suffix
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
name|replacement
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
block|}
comment|/**      * @return true if the word matches this rule.      */
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
operator|(
name|len
operator|-
name|suffix
operator|.
name|length
operator|>=
name|min
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
name|suffix
argument_list|)
operator|)
return|;
block|}
comment|/**      * @return new valid length of the string after firing this rule.      */
DECL|method|replace
specifier|public
name|int
name|replace
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|replacement
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|replacement
argument_list|,
literal|0
argument_list|,
name|s
argument_list|,
name|len
operator|-
name|suffix
operator|.
name|length
argument_list|,
name|replacement
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|len
operator|-
name|suffix
operator|.
name|length
operator|+
name|replacement
operator|.
name|length
return|;
block|}
block|}
comment|/**    * A rule with a set of whole-word exceptions.    */
DECL|class|RuleWithSetExceptions
specifier|protected
specifier|static
class|class
name|RuleWithSetExceptions
extends|extends
name|Rule
block|{
DECL|field|exceptions
specifier|protected
specifier|final
name|CharArraySet
name|exceptions
decl_stmt|;
DECL|method|RuleWithSetExceptions
specifier|public
name|RuleWithSetExceptions
parameter_list|(
name|String
name|suffix
parameter_list|,
name|int
name|min
parameter_list|,
name|String
name|replacement
parameter_list|,
name|String
index|[]
name|exceptions
parameter_list|)
block|{
name|super
argument_list|(
name|suffix
argument_list|,
name|min
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exceptions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|exceptions
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"warning: useless exception '"
operator|+
name|exceptions
index|[
name|i
index|]
operator|+
literal|"' does not end with '"
operator|+
name|suffix
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|exceptions
operator|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|exceptions
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
name|super
operator|.
name|matches
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
operator|&&
operator|!
name|exceptions
operator|.
name|contains
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
comment|/**    * A rule with a set of exceptional suffixes.    */
DECL|class|RuleWithSuffixExceptions
specifier|protected
specifier|static
class|class
name|RuleWithSuffixExceptions
extends|extends
name|Rule
block|{
comment|// TODO: use a more efficient datastructure: automaton?
DECL|field|exceptions
specifier|protected
specifier|final
name|char
index|[]
index|[]
name|exceptions
decl_stmt|;
DECL|method|RuleWithSuffixExceptions
specifier|public
name|RuleWithSuffixExceptions
parameter_list|(
name|String
name|suffix
parameter_list|,
name|int
name|min
parameter_list|,
name|String
name|replacement
parameter_list|,
name|String
index|[]
name|exceptions
parameter_list|)
block|{
name|super
argument_list|(
name|suffix
argument_list|,
name|min
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exceptions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|exceptions
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"warning: useless exception '"
operator|+
name|exceptions
index|[
name|i
index|]
operator|+
literal|"' does not end with '"
operator|+
name|suffix
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|exceptions
operator|=
operator|new
name|char
index|[
name|exceptions
operator|.
name|length
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exceptions
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|this
operator|.
name|exceptions
index|[
name|i
index|]
operator|=
name|exceptions
index|[
name|i
index|]
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|matches
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exceptions
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
name|exceptions
index|[
name|i
index|]
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * A step containing a list of rules.    */
DECL|class|Step
specifier|protected
specifier|static
class|class
name|Step
block|{
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|rules
specifier|protected
specifier|final
name|Rule
name|rules
index|[]
decl_stmt|;
DECL|field|min
specifier|protected
specifier|final
name|int
name|min
decl_stmt|;
DECL|field|suffixes
specifier|protected
specifier|final
name|char
index|[]
index|[]
name|suffixes
decl_stmt|;
comment|/**      * Create a new step      * @param name Step's name.      * @param rules an ordered list of rules.      * @param min minimum word size. if this is 0 it is automatically calculated.      * @param suffixes optional list of conditional suffixes. may be null.      */
DECL|method|Step
specifier|public
name|Step
parameter_list|(
name|String
name|name
parameter_list|,
name|Rule
name|rules
index|[]
parameter_list|,
name|int
name|min
parameter_list|,
name|String
name|suffixes
index|[]
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|rules
operator|=
name|rules
expr_stmt|;
if|if
condition|(
name|min
operator|==
literal|0
condition|)
block|{
name|min
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
for|for
control|(
name|Rule
name|r
range|:
name|rules
control|)
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|r
operator|.
name|min
operator|+
name|r
operator|.
name|suffix
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
if|if
condition|(
name|suffixes
operator|==
literal|null
operator|||
name|suffixes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|suffixes
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|suffixes
operator|=
operator|new
name|char
index|[
name|suffixes
operator|.
name|length
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|suffixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|this
operator|.
name|suffixes
index|[
name|i
index|]
operator|=
name|suffixes
index|[
name|i
index|]
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return new valid length of the string after applying the entire step.      */
DECL|method|apply
specifier|public
name|int
name|apply
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|<
name|min
condition|)
return|return
name|len
return|;
if|if
condition|(
name|suffixes
operator|!=
literal|null
condition|)
block|{
name|boolean
name|found
init|=
literal|false
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
name|suffixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
name|suffixes
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|found
condition|)
return|return
name|len
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rules
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|rules
index|[
name|i
index|]
operator|.
name|matches
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
condition|)
return|return
name|rules
index|[
name|i
index|]
operator|.
name|replace
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
return|return
name|len
return|;
block|}
block|}
comment|/**    * Parse a resource file into an RSLP stemmer description.    * @return a Map containing the named Steps in this description.    */
DECL|method|parse
specifier|protected
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Step
argument_list|>
name|parse
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RSLPStemmerBase
argument_list|>
name|clazz
parameter_list|,
name|String
name|resource
parameter_list|)
block|{
comment|// TODO: this parser is ugly, but works. use a jflex grammar instead.
try|try
block|{
name|InputStream
name|is
init|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|LineNumberReader
name|r
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Step
argument_list|>
name|steps
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Step
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|step
decl_stmt|;
while|while
condition|(
operator|(
name|step
operator|=
name|readLine
argument_list|(
name|r
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|Step
name|s
init|=
name|parseStep
argument_list|(
name|r
argument_list|,
name|step
argument_list|)
decl_stmt|;
name|steps
operator|.
name|put
argument_list|(
name|s
operator|.
name|name
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|steps
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|headerPattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|headerPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*(0|1),\\s*\\{(.*)\\},\\s*$"
argument_list|)
decl_stmt|;
DECL|field|stripPattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|stripPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\{\\s*\"([^\"]*)\",\\s*([0-9]+)\\s*\\}\\s*(,|(\\}\\s*;))$"
argument_list|)
decl_stmt|;
DECL|field|repPattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|repPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*\"([^\"]*)\"\\}\\s*(,|(\\}\\s*;))$"
argument_list|)
decl_stmt|;
DECL|field|excPattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|excPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*\"([^\"]*)\",\\s*\\{(.*)\\}\\s*\\}\\s*(,|(\\}\\s*;))$"
argument_list|)
decl_stmt|;
DECL|method|parseStep
specifier|private
specifier|static
name|Step
name|parseStep
parameter_list|(
name|LineNumberReader
name|r
parameter_list|,
name|String
name|header
parameter_list|)
throws|throws
name|IOException
block|{
name|Matcher
name|matcher
init|=
name|headerPattern
operator|.
name|matcher
argument_list|(
name|header
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Illegal Step header specified at line "
operator|+
name|r
operator|.
name|getLineNumber
argument_list|()
argument_list|)
throw|;
block|}
assert|assert
name|matcher
operator|.
name|groupCount
argument_list|()
operator|==
literal|4
assert|;
name|String
name|name
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|min
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|type
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|suffixes
index|[]
init|=
name|parseList
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|Rule
name|rules
index|[]
init|=
name|parseRules
argument_list|(
name|r
argument_list|,
name|type
argument_list|)
decl_stmt|;
return|return
operator|new
name|Step
argument_list|(
name|name
argument_list|,
name|rules
argument_list|,
name|min
argument_list|,
name|suffixes
argument_list|)
return|;
block|}
DECL|method|parseRules
specifier|private
specifier|static
name|Rule
index|[]
name|parseRules
parameter_list|(
name|LineNumberReader
name|r
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Rule
argument_list|>
name|rules
init|=
operator|new
name|ArrayList
argument_list|<
name|Rule
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|readLine
argument_list|(
name|r
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|stripPattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|rules
operator|.
name|add
argument_list|(
operator|new
name|Rule
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matcher
operator|=
name|repPattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|rules
operator|.
name|add
argument_list|(
operator|new
name|Rule
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matcher
operator|=
name|excPattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
name|rules
operator|.
name|add
argument_list|(
operator|new
name|RuleWithSuffixExceptions
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|parseList
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rules
operator|.
name|add
argument_list|(
operator|new
name|RuleWithSetExceptions
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|parseList
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Illegal Step rule specified at line "
operator|+
name|r
operator|.
name|getLineNumber
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|line
operator|.
name|endsWith
argument_list|(
literal|";"
argument_list|)
condition|)
return|return
name|rules
operator|.
name|toArray
argument_list|(
operator|new
name|Rule
index|[
name|rules
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|parseList
specifier|private
specifier|static
name|String
index|[]
name|parseList
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|String
name|list
index|[]
init|=
name|s
operator|.
name|split
argument_list|(
literal|","
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
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|list
index|[
name|i
index|]
operator|=
name|parseString
argument_list|(
name|list
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
DECL|method|parseString
specifier|private
specifier|static
name|String
name|parseString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|readLine
specifier|private
specifier|static
name|String
name|readLine
parameter_list|(
name|LineNumberReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|line
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'#'
condition|)
return|return
name|line
return|;
block|}
return|return
name|line
return|;
block|}
block|}
end_class

end_unit

