begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Filter
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
name|Query
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
name|ConstantScoreQuery
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|TermDocs
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
name|index
operator|.
name|TermEnum
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
name|index
operator|.
name|Term
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
name|OpenBitSet
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
name|ToStringUtils
import|;
end_import

begin_class
DECL|class|AbstractTrieRangeFilter
specifier|abstract
class|class
name|AbstractTrieRangeFilter
extends|extends
name|Filter
block|{
DECL|method|AbstractTrieRangeFilter
name|AbstractTrieRangeFilter
parameter_list|(
specifier|final
name|String
index|[]
name|fields
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Number
name|min
parameter_list|,
name|Number
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
operator|(
name|String
index|[]
operator|)
name|fields
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|minInclusive
operator|=
name|minInclusive
expr_stmt|;
name|this
operator|.
name|maxInclusive
operator|=
name|maxInclusive
expr_stmt|;
block|}
comment|//@Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|fields
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|fields
index|[
literal|0
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
name|minInclusive
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|min
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|min
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|max
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|max
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|maxInclusive
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//@Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|AbstractTrieRangeFilter
name|q
init|=
operator|(
name|AbstractTrieRangeFilter
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|Arrays
operator|.
name|equals
argument_list|(
name|fields
argument_list|,
name|q
operator|.
name|fields
argument_list|)
operator|&&
operator|(
name|q
operator|.
name|min
operator|==
literal|null
condition|?
name|min
operator|==
literal|null
else|:
name|q
operator|.
name|min
operator|.
name|equals
argument_list|(
name|min
argument_list|)
operator|)
operator|&&
operator|(
name|q
operator|.
name|max
operator|==
literal|null
condition|?
name|max
operator|==
literal|null
else|:
name|q
operator|.
name|max
operator|.
name|equals
argument_list|(
name|max
argument_list|)
operator|)
operator|&&
name|minInclusive
operator|==
name|q
operator|.
name|minInclusive
operator|&&
name|maxInclusive
operator|==
name|q
operator|.
name|maxInclusive
operator|&&
name|precisionStep
operator|==
name|q
operator|.
name|precisionStep
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//@Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|precisionStep
operator|^
literal|0x64365465
operator|)
decl_stmt|;
if|if
condition|(
name|min
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|min
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x14fa55fb
expr_stmt|;
if|if
condition|(
name|max
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|max
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x733fa5fe
expr_stmt|;
return|return
name|hash
operator|+
operator|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|minInclusive
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x14fa55fb
operator|)
operator|+
operator|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|maxInclusive
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x733fa5fe
operator|)
return|;
block|}
comment|/**    * Expert: Return the number of terms visited during the last execution of {@link #getDocIdSet}.    * This may be used for performance comparisons of different trie variants and their effectiveness.    * This method is not thread safe, be sure to only call it when no query is running!    * @throws IllegalStateException if {@link #getDocIdSet} was not yet executed.    */
DECL|method|getLastNumberOfTerms
specifier|public
name|int
name|getLastNumberOfTerms
parameter_list|()
block|{
if|if
condition|(
name|lastNumberOfTerms
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
return|return
name|lastNumberOfTerms
return|;
block|}
DECL|method|resetLastNumberOfTerms
name|void
name|resetLastNumberOfTerms
parameter_list|()
block|{
name|lastNumberOfTerms
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Returns this range filter as a query.    * Using this method, it is possible to create a Query using<code>new {Long|Int}TrieRangeFilter(....).asQuery()</code>.    * This is a synonym for wrapping with a {@link ConstantScoreQuery},    * but this query returns a better<code>toString()</code> variant.    */
DECL|method|asQuery
specifier|public
name|Query
name|asQuery
parameter_list|()
block|{
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|this
argument_list|)
block|{
comment|/** this instance return a nicer String variant than the original {@link ConstantScoreQuery} */
comment|//@Override
specifier|public
name|String
name|toString
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
comment|// return a more convenient representation of this query than ConstantScoreQuery does:
return|return
operator|(
operator|(
name|AbstractTrieRangeFilter
operator|)
name|filter
operator|)
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|fillBits
name|void
name|fillBits
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|OpenBitSet
name|bits
parameter_list|,
specifier|final
name|TermDocs
name|termDocs
parameter_list|,
name|String
name|field
parameter_list|,
specifier|final
name|String
name|lowerTerm
parameter_list|,
specifier|final
name|String
name|upperTerm
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|lowerTerm
operator|.
name|length
argument_list|()
decl_stmt|;
assert|assert
name|upperTerm
operator|.
name|length
argument_list|()
operator|==
name|len
assert|;
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// find the docs
specifier|final
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|lowerTerm
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
specifier|final
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
comment|// break out when upperTerm reached or length of term is different
specifier|final
name|String
name|t
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|t
operator|.
name|length
argument_list|()
operator|||
name|t
operator|.
name|compareTo
argument_list|(
name|upperTerm
argument_list|)
operator|>
literal|0
condition|)
break|break;
comment|// we have a good term, find the docs
name|lastNumberOfTerms
operator|++
expr_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
name|bits
operator|.
name|set
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
break|break;
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// members
DECL|field|fields
specifier|final
name|String
index|[]
name|fields
decl_stmt|;
DECL|field|precisionStep
specifier|final
name|int
name|precisionStep
decl_stmt|;
DECL|field|min
DECL|field|max
specifier|final
name|Number
name|min
decl_stmt|,
name|max
decl_stmt|;
DECL|field|minInclusive
DECL|field|maxInclusive
specifier|final
name|boolean
name|minInclusive
decl_stmt|,
name|maxInclusive
decl_stmt|;
DECL|field|lastNumberOfTerms
specifier|private
name|int
name|lastNumberOfTerms
init|=
operator|-
literal|1
decl_stmt|;
block|}
end_class

end_unit

