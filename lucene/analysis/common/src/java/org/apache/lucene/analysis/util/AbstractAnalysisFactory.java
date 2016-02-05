begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
name|core
operator|.
name|StopFilter
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

begin_comment
comment|/**  * Abstract parent class for analysis factories {@link TokenizerFactory},  * {@link TokenFilterFactory} and {@link CharFilterFactory}.  *<p>  * The typical lifecycle for a factory consumer is:  *<ol>  *<li>Create factory via its constructor (or via XXXFactory.forName)  *<li>(Optional) If the factory uses resources such as files, {@link ResourceLoaderAware#inform(ResourceLoader)} is called to initialize those resources.  *<li>Consumer calls create() to obtain instances.  *</ol>  */
end_comment

begin_class
DECL|class|AbstractAnalysisFactory
specifier|public
specifier|abstract
class|class
name|AbstractAnalysisFactory
block|{
DECL|field|LUCENE_MATCH_VERSION_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|LUCENE_MATCH_VERSION_PARAM
init|=
literal|"luceneMatchVersion"
decl_stmt|;
comment|/** The original args, before any processing */
DECL|field|originalArgs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|originalArgs
decl_stmt|;
comment|/** the luceneVersion arg */
DECL|field|luceneMatchVersion
specifier|protected
specifier|final
name|Version
name|luceneMatchVersion
decl_stmt|;
comment|/** whether the luceneMatchVersion arg is explicitly specified in the serialized schema */
DECL|field|isExplicitLuceneMatchVersion
specifier|private
name|boolean
name|isExplicitLuceneMatchVersion
init|=
literal|false
decl_stmt|;
comment|/**    * Initialize this factory via a set of key-value pairs.    */
DECL|method|AbstractAnalysisFactory
specifier|protected
name|AbstractAnalysisFactory
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
name|originalArgs
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|version
init|=
name|get
argument_list|(
name|args
argument_list|,
name|LUCENE_MATCH_VERSION_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|luceneMatchVersion
operator|=
name|Version
operator|.
name|LATEST
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|luceneMatchVersion
operator|=
name|Version
operator|.
name|parseLeniently
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|pe
argument_list|)
throw|;
block|}
block|}
name|args
operator|.
name|remove
argument_list|(
name|CLASS_NAME
argument_list|)
expr_stmt|;
comment|// consume the class arg
block|}
DECL|method|getOriginalArgs
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getOriginalArgs
parameter_list|()
block|{
return|return
name|originalArgs
return|;
block|}
DECL|method|getLuceneMatchVersion
specifier|public
specifier|final
name|Version
name|getLuceneMatchVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|luceneMatchVersion
return|;
block|}
DECL|method|require
specifier|public
name|String
name|require
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|s
return|;
block|}
DECL|method|require
specifier|public
name|String
name|require
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|)
block|{
return|return
name|require
argument_list|(
name|args
argument_list|,
name|name
argument_list|,
name|allowedValues
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|require
specifier|public
name|String
name|require
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
else|else
block|{
for|for
control|(
name|String
name|allowedValue
range|:
name|allowedValues
control|)
block|{
if|if
condition|(
name|caseSensitive
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|allowedValue
argument_list|)
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|s
operator|.
name|equalsIgnoreCase
argument_list|(
name|allowedValue
argument_list|)
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: '"
operator|+
name|name
operator|+
literal|"' value must be one of "
operator|+
name|allowedValues
argument_list|)
throw|;
block|}
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
comment|// defaultVal = null
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|defaultVal
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|defaultVal
else|:
name|s
return|;
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|args
argument_list|,
name|name
argument_list|,
name|allowedValues
argument_list|,
literal|null
argument_list|)
return|;
comment|// defaultVal = null
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|,
name|String
name|defaultVal
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|args
argument_list|,
name|name
argument_list|,
name|allowedValues
argument_list|,
name|defaultVal
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|,
name|String
name|defaultVal
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
name|defaultVal
return|;
block|}
else|else
block|{
for|for
control|(
name|String
name|allowedValue
range|:
name|allowedValues
control|)
block|{
if|if
condition|(
name|caseSensitive
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|allowedValue
argument_list|)
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|s
operator|.
name|equalsIgnoreCase
argument_list|(
name|allowedValue
argument_list|)
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: '"
operator|+
name|name
operator|+
literal|"' value must be one of "
operator|+
name|allowedValues
argument_list|)
throw|;
block|}
block|}
DECL|method|requireInt
specifier|protected
specifier|final
name|int
name|requireInt
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|require
argument_list|(
name|args
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|protected
specifier|final
name|int
name|getInt
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|defaultVal
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|requireBoolean
specifier|protected
specifier|final
name|boolean
name|requireBoolean
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|require
argument_list|(
name|args
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getBoolean
specifier|protected
specifier|final
name|boolean
name|getBoolean
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|defaultVal
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|defaultVal
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|requireFloat
specifier|protected
specifier|final
name|float
name|requireFloat
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|require
argument_list|(
name|args
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getFloat
specifier|protected
specifier|final
name|float
name|getFloat
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|float
name|defaultVal
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|defaultVal
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|requireChar
specifier|public
name|char
name|requireChar
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|require
argument_list|(
name|args
argument_list|,
name|name
argument_list|)
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getChar
specifier|public
name|char
name|getChar
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|,
name|char
name|defaultValue
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
else|else
block|{
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|name
operator|+
literal|" should be a char. \""
operator|+
name|s
operator|+
literal|"\" is invalid"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
block|}
DECL|field|ITEM_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|ITEM_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^,\\s]+"
argument_list|)
decl_stmt|;
comment|/** Returns whitespace- and/or comma-separated set of values, or null if none are found */
DECL|method|getSet
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSet
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
literal|null
decl_stmt|;
name|Matcher
name|matcher
init|=
name|ITEM_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
block|}
comment|/**    * Compiles a pattern for the value of the specified argument key<code>name</code>     */
DECL|method|getPattern
specifier|protected
specifier|final
name|Pattern
name|getPattern
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|require
argument_list|(
name|args
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: '"
operator|+
name|name
operator|+
literal|"' can not be parsed in "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns as {@link CharArraySet} from wordFiles, which    * can be a comma-separated list of filenames    */
DECL|method|getWordSet
specifier|protected
specifier|final
name|CharArraySet
name|getWordSet
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|wordFiles
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|wordFiles
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// default stopwords list has 35 or so words, but maybe don't make it that
comment|// big to start
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|*
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
name|getLines
argument_list|(
name|loader
argument_list|,
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|words
operator|.
name|addAll
argument_list|(
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|wlist
argument_list|,
name|ignoreCase
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|words
return|;
block|}
comment|/**    * Returns the resource's lines (with content treated as UTF-8)    */
DECL|method|getLines
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|WordlistLoader
operator|.
name|getLines
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|resource
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
comment|/** same as {@link #getWordSet(ResourceLoader, String, boolean)},    * except the input is in snowball format. */
DECL|method|getSnowballWordSet
specifier|protected
specifier|final
name|CharArraySet
name|getSnowballWordSet
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|wordFiles
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|wordFiles
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// default stopwords list has 35 or so words, but maybe don't make it that
comment|// big to start
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|*
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|file
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|newDecoder
argument_list|()
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|reader
argument_list|,
name|words
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|words
return|;
block|}
comment|/**    * Splits file names separated by comma character.    * File names can contain comma characters escaped by backslash '\'    *    * @param fileNames the string containing file names    * @return a list of file names with the escaping backslashed removed    */
DECL|method|splitFileNames
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|splitFileNames
parameter_list|(
name|String
name|fileNames
parameter_list|)
block|{
if|if
condition|(
name|fileNames
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
return|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|fileNames
operator|.
name|split
argument_list|(
literal|"(?<!\\\\),"
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|file
operator|.
name|replaceAll
argument_list|(
literal|"\\\\(?=,)"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|field|CLASS_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CLASS_NAME
init|=
literal|"class"
decl_stmt|;
comment|/**    * @return the string used to specify the concrete class name in a serialized representation: the class arg.      *         If the concrete class name was not specified via a class arg, returns {@code getClass().getName()}.    */
DECL|method|getClassArg
specifier|public
name|String
name|getClassArg
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|originalArgs
condition|)
block|{
name|String
name|className
init|=
name|originalArgs
operator|.
name|get
argument_list|(
name|CLASS_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|className
condition|)
block|{
return|return
name|className
return|;
block|}
block|}
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|isExplicitLuceneMatchVersion
specifier|public
name|boolean
name|isExplicitLuceneMatchVersion
parameter_list|()
block|{
return|return
name|isExplicitLuceneMatchVersion
return|;
block|}
DECL|method|setExplicitLuceneMatchVersion
specifier|public
name|void
name|setExplicitLuceneMatchVersion
parameter_list|(
name|boolean
name|isExplicitLuceneMatchVersion
parameter_list|)
block|{
name|this
operator|.
name|isExplicitLuceneMatchVersion
operator|=
name|isExplicitLuceneMatchVersion
expr_stmt|;
block|}
block|}
end_class

end_unit

