begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.mlt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|mlt
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
name|Analyzer
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|Arrays
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

begin_comment
comment|/**  * A simple wrapper for MoreLikeThis for use in scenarios where a Query object is required eg  * in custom QueryParser extensions. At query.rewrite() time the reader is used to construct the  * actual MoreLikeThis object and obtain the real Query object.  */
end_comment

begin_class
DECL|class|MoreLikeThisQuery
specifier|public
class|class
name|MoreLikeThisQuery
extends|extends
name|Query
block|{
DECL|field|likeText
specifier|private
name|String
name|likeText
decl_stmt|;
DECL|field|moreLikeFields
specifier|private
name|String
index|[]
name|moreLikeFields
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|percentTermsToMatch
specifier|private
name|float
name|percentTermsToMatch
init|=
literal|0.3f
decl_stmt|;
DECL|field|minTermFrequency
specifier|private
name|int
name|minTermFrequency
init|=
literal|1
decl_stmt|;
DECL|field|maxQueryTerms
specifier|private
name|int
name|maxQueryTerms
init|=
literal|5
decl_stmt|;
DECL|field|stopWords
specifier|private
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
init|=
literal|null
decl_stmt|;
DECL|field|minDocFreq
specifier|private
name|int
name|minDocFreq
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * @param moreLikeFields fields used for similarity measure    */
DECL|method|MoreLikeThisQuery
specifier|public
name|MoreLikeThisQuery
parameter_list|(
name|String
name|likeText
parameter_list|,
name|String
index|[]
name|moreLikeFields
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|likeText
operator|=
name|likeText
expr_stmt|;
name|this
operator|.
name|moreLikeFields
operator|=
name|moreLikeFields
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|moreLikeFields
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
name|minTermFrequency
argument_list|)
expr_stmt|;
if|if
condition|(
name|minDocFreq
operator|>=
literal|0
condition|)
block|{
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|minDocFreq
argument_list|)
expr_stmt|;
block|}
name|mlt
operator|.
name|setMaxQueryTerms
argument_list|(
name|maxQueryTerms
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setStopWords
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|likeText
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|newBq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|newBq
operator|.
name|setDisableCoord
argument_list|(
name|bq
operator|.
name|isCoordDisabled
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
control|)
block|{
name|newBq
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
comment|//make at least half the terms match
name|newBq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
call|(
name|int
call|)
argument_list|(
name|bq
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
operator|*
name|percentTermsToMatch
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|newBq
operator|.
name|build
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)   * @see org.apache.lucene.search.Query#toString(java.lang.String)   */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"like:"
operator|+
name|likeText
return|;
block|}
DECL|method|getPercentTermsToMatch
specifier|public
name|float
name|getPercentTermsToMatch
parameter_list|()
block|{
return|return
name|percentTermsToMatch
return|;
block|}
DECL|method|setPercentTermsToMatch
specifier|public
name|void
name|setPercentTermsToMatch
parameter_list|(
name|float
name|percentTermsToMatch
parameter_list|)
block|{
name|this
operator|.
name|percentTermsToMatch
operator|=
name|percentTermsToMatch
expr_stmt|;
block|}
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|getLikeText
specifier|public
name|String
name|getLikeText
parameter_list|()
block|{
return|return
name|likeText
return|;
block|}
DECL|method|setLikeText
specifier|public
name|void
name|setLikeText
parameter_list|(
name|String
name|likeText
parameter_list|)
block|{
name|this
operator|.
name|likeText
operator|=
name|likeText
expr_stmt|;
block|}
DECL|method|getMaxQueryTerms
specifier|public
name|int
name|getMaxQueryTerms
parameter_list|()
block|{
return|return
name|maxQueryTerms
return|;
block|}
DECL|method|setMaxQueryTerms
specifier|public
name|void
name|setMaxQueryTerms
parameter_list|(
name|int
name|maxQueryTerms
parameter_list|)
block|{
name|this
operator|.
name|maxQueryTerms
operator|=
name|maxQueryTerms
expr_stmt|;
block|}
DECL|method|getMinTermFrequency
specifier|public
name|int
name|getMinTermFrequency
parameter_list|()
block|{
return|return
name|minTermFrequency
return|;
block|}
DECL|method|setMinTermFrequency
specifier|public
name|void
name|setMinTermFrequency
parameter_list|(
name|int
name|minTermFrequency
parameter_list|)
block|{
name|this
operator|.
name|minTermFrequency
operator|=
name|minTermFrequency
expr_stmt|;
block|}
DECL|method|getMoreLikeFields
specifier|public
name|String
index|[]
name|getMoreLikeFields
parameter_list|()
block|{
return|return
name|moreLikeFields
return|;
block|}
DECL|method|setMoreLikeFields
specifier|public
name|void
name|setMoreLikeFields
parameter_list|(
name|String
index|[]
name|moreLikeFields
parameter_list|)
block|{
name|this
operator|.
name|moreLikeFields
operator|=
name|moreLikeFields
expr_stmt|;
block|}
DECL|method|getStopWords
specifier|public
name|Set
argument_list|<
name|?
argument_list|>
name|getStopWords
parameter_list|()
block|{
return|return
name|stopWords
return|;
block|}
DECL|method|setStopWords
specifier|public
name|void
name|setStopWords
parameter_list|(
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
block|}
DECL|method|getMinDocFreq
specifier|public
name|int
name|getMinDocFreq
parameter_list|()
block|{
return|return
name|minDocFreq
return|;
block|}
DECL|method|setMinDocFreq
specifier|public
name|void
name|setMinDocFreq
parameter_list|(
name|int
name|minDocFreq
parameter_list|)
block|{
name|this
operator|.
name|minDocFreq
operator|=
name|minDocFreq
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|analyzer
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|analyzer
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|fieldName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|fieldName
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|likeText
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|likeText
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|maxQueryTerms
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|minDocFreq
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|minTermFrequency
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|moreLikeFields
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|percentTermsToMatch
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|stopWords
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|stopWords
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MoreLikeThisQuery
name|other
init|=
operator|(
name|MoreLikeThisQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|analyzer
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|analyzer
operator|.
name|equals
argument_list|(
name|other
operator|.
name|analyzer
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|fieldName
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|likeText
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|likeText
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|likeText
operator|.
name|equals
argument_list|(
name|other
operator|.
name|likeText
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|maxQueryTerms
operator|!=
name|other
operator|.
name|maxQueryTerms
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|minDocFreq
operator|!=
name|other
operator|.
name|minDocFreq
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|minTermFrequency
operator|!=
name|other
operator|.
name|minTermFrequency
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|moreLikeFields
argument_list|,
name|other
operator|.
name|moreLikeFields
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|percentTermsToMatch
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|percentTermsToMatch
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|stopWords
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|stopWords
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|stopWords
operator|.
name|equals
argument_list|(
name|other
operator|.
name|stopWords
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
end_class

end_unit

