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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|TermAttribute
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
comment|/**  * Base class for decomposition token filters.<a name="version"/>  *<p>  * You must specify the required {@link Version} compatibility when creating  * CompoundWordTokenFilterBase:  *<ul>  *<li>As of 3.1, CompoundWordTokenFilterBase correctly handles Unicode 4.0  * supplementary characters in strings and char arrays provided as compound word  * dictionaries.  *</ul>  */
end_comment

begin_class
DECL|class|CompoundWordTokenFilterBase
specifier|public
specifier|abstract
class|class
name|CompoundWordTokenFilterBase
extends|extends
name|TokenFilter
block|{
comment|/**    * The default for minimal word length that gets decomposed    */
DECL|field|DEFAULT_MIN_WORD_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_WORD_SIZE
init|=
literal|5
decl_stmt|;
comment|/**    * The default for minimal length of subwords that get propagated to the output of this filter    */
DECL|field|DEFAULT_MIN_SUBWORD_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_SUBWORD_SIZE
init|=
literal|2
decl_stmt|;
comment|/**    * The default for maximal length of subwords that get propagated to the output of this filter    */
DECL|field|DEFAULT_MAX_SUBWORD_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_SUBWORD_SIZE
init|=
literal|15
decl_stmt|;
DECL|field|dictionary
specifier|protected
specifier|final
name|CharArraySet
name|dictionary
decl_stmt|;
DECL|field|tokens
specifier|protected
specifier|final
name|LinkedList
argument_list|<
name|Token
argument_list|>
name|tokens
decl_stmt|;
DECL|field|minWordSize
specifier|protected
specifier|final
name|int
name|minWordSize
decl_stmt|;
DECL|field|minSubwordSize
specifier|protected
specifier|final
name|int
name|minSubwordSize
decl_stmt|;
DECL|field|maxSubwordSize
specifier|protected
specifier|final
name|int
name|maxSubwordSize
decl_stmt|;
DECL|field|onlyLongestMatch
specifier|protected
specifier|final
name|boolean
name|onlyLongestMatch
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|flagsAtt
specifier|private
name|FlagsAttribute
name|flagsAtt
decl_stmt|;
DECL|field|posIncAtt
specifier|private
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|field|payloadAtt
specifier|private
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|field|wrapper
specifier|private
specifier|final
name|Token
name|wrapper
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
comment|/**    * @deprecated use {@link #CompoundWordTokenFilterBase(Version, TokenStream, String[], int, int, int, boolean)} instead    */
annotation|@
name|Deprecated
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|dictionary
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|,
name|makeDictionary
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|minWordSize
argument_list|,
name|minSubwordSize
argument_list|,
name|maxSubwordSize
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated use {@link #CompoundWordTokenFilterBase(Version, TokenStream, String[], boolean)} instead    */
annotation|@
name|Deprecated
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|dictionary
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|,
name|makeDictionary
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated use {@link #CompoundWordTokenFilterBase(Version, TokenStream, Set, boolean)} instead    */
annotation|@
name|Deprecated
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|dictionary
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated use {@link #CompoundWordTokenFilterBase(Version, TokenStream, String[])} instead    */
annotation|@
name|Deprecated
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|,
name|makeDictionary
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated use {@link #CompoundWordTokenFilterBase(Version, TokenStream, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated use {@link #CompoundWordTokenFilterBase(Version, TokenStream, Set, int, int, int, boolean)} instead    */
annotation|@
name|Deprecated
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|dictionary
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
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
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|dictionary
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|makeDictionary
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|minWordSize
argument_list|,
name|minSubwordSize
argument_list|,
name|maxSubwordSize
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|dictionary
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|makeDictionary
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Set
name|dictionary
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|makeDictionary
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Set
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Set
name|dictionary
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
operator|new
name|LinkedList
argument_list|<
name|Token
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|minWordSize
operator|=
name|minWordSize
expr_stmt|;
name|this
operator|.
name|minSubwordSize
operator|=
name|minSubwordSize
expr_stmt|;
name|this
operator|.
name|maxSubwordSize
operator|=
name|maxSubwordSize
expr_stmt|;
name|this
operator|.
name|onlyLongestMatch
operator|=
name|onlyLongestMatch
expr_stmt|;
if|if
condition|(
name|dictionary
operator|instanceof
name|CharArraySet
condition|)
block|{
name|this
operator|.
name|dictionary
operator|=
operator|(
name|CharArraySet
operator|)
name|dictionary
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|dictionary
operator|=
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|dictionary
operator|.
name|size
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addAllLowerCase
argument_list|(
name|this
operator|.
name|dictionary
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
block|}
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|flagsAtt
operator|=
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a set of words from an array    * The resulting Set does case insensitive matching    * TODO We should look for a faster dictionary lookup approach.    * @param dictionary     * @return {@link Set} of lowercased terms     */
DECL|method|makeDictionary
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|makeDictionary
parameter_list|(
specifier|final
name|String
index|[]
name|dictionary
parameter_list|)
block|{
return|return
name|makeDictionary
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|dictionary
argument_list|)
return|;
block|}
DECL|method|makeDictionary
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|makeDictionary
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|String
index|[]
name|dictionary
parameter_list|)
block|{
comment|// is the below really case insensitive?
name|CharArraySet
name|dict
init|=
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|dictionary
operator|.
name|length
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|addAllLowerCase
argument_list|(
name|dict
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|dictionary
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|dict
return|;
block|}
DECL|method|setToken
specifier|private
specifier|final
name|void
name|setToken
parameter_list|(
specifier|final
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
name|token
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|token
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|token
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|tokens
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|setToken
argument_list|(
name|tokens
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|wrapper
operator|.
name|setTermBuffer
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setStartOffset
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setEndOffset
argument_list|(
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setFlags
argument_list|(
name|flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setType
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setPositionIncrement
argument_list|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setPayload
argument_list|(
name|payloadAtt
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
name|decompose
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokens
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|setToken
argument_list|(
name|tokens
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|addAllLowerCase
specifier|protected
specifier|static
specifier|final
name|void
name|addAllLowerCase
parameter_list|(
name|Set
argument_list|<
name|Object
argument_list|>
name|target
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|col
parameter_list|)
block|{
for|for
control|(
name|String
name|string
range|:
name|col
control|)
block|{
name|target
operator|.
name|add
argument_list|(
name|string
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeLowerCaseCopy
specifier|protected
specifier|static
name|char
index|[]
name|makeLowerCaseCopy
parameter_list|(
specifier|final
name|char
index|[]
name|buffer
parameter_list|)
block|{
name|char
index|[]
name|result
init|=
operator|new
name|char
index|[
name|buffer
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
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
name|buffer
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|createToken
specifier|protected
specifier|final
name|Token
name|createToken
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|,
specifier|final
name|Token
name|prototype
parameter_list|)
block|{
name|int
name|newStart
init|=
name|prototype
operator|.
name|startOffset
argument_list|()
operator|+
name|offset
decl_stmt|;
name|Token
name|t
init|=
name|prototype
operator|.
name|clone
argument_list|(
name|prototype
operator|.
name|termBuffer
argument_list|()
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|newStart
argument_list|,
name|newStart
operator|+
name|length
argument_list|)
decl_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
DECL|method|decompose
specifier|protected
name|void
name|decompose
parameter_list|(
specifier|final
name|Token
name|token
parameter_list|)
block|{
comment|// In any case we give the original token back
name|tokens
operator|.
name|add
argument_list|(
operator|(
name|Token
operator|)
name|token
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
comment|// Only words longer than minWordSize get processed
if|if
condition|(
name|token
operator|.
name|termLength
argument_list|()
operator|<
name|this
operator|.
name|minWordSize
condition|)
block|{
return|return;
block|}
name|decomposeInternal
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
DECL|method|decomposeInternal
specifier|protected
specifier|abstract
name|void
name|decomposeInternal
parameter_list|(
specifier|final
name|Token
name|token
parameter_list|)
function_decl|;
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
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|tokens
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

