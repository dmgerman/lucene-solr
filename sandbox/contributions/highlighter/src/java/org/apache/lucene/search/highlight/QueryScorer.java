begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package

begin_comment
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Query
import|;
end_import

begin_comment
comment|/**  * {@link Scorer} implementation which scores text fragments by the number of unique query terms found.  * This class uses the {@link QueryTermExtractor} class to process determine the query terms and   * their boosts to be used.   * @author mark@searcharea.co.uk  */
end_comment

begin_comment
comment|//TODO: provide option to boost score of fragments near beginning of document
end_comment

begin_comment
comment|// based on fragment.getFragNum()
end_comment

begin_class
DECL|class|QueryScorer
specifier|public
class|class
name|QueryScorer
implements|implements
name|Scorer
block|{
DECL|field|currentTextFragment
name|TextFragment
name|currentTextFragment
init|=
literal|null
decl_stmt|;
DECL|field|uniqueTermsInFragment
name|HashSet
name|uniqueTermsInFragment
decl_stmt|;
DECL|field|totalScore
name|float
name|totalScore
init|=
literal|0
decl_stmt|;
DECL|field|termsToFind
specifier|private
name|HashMap
name|termsToFind
decl_stmt|;
comment|/** 	 *  	 * @param query a Lucene query (ideally rewritten using query.rewrite  	 * before being passed to this class and the searcher) 	 */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
argument_list|(
name|QueryTermExtractor
operator|.
name|getTerms
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  	 * @param query a Lucene query (ideally rewritten using query.rewrite  	 * before being passed to this class and the searcher) 	 * @param reader used to compute IDF which can be used to a) score selected fragments better  	 * b) use graded highlights eg set font color intensity 	 * @param fieldName the field on which Inverse Document Frequency (IDF) calculations are based 	 */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|this
argument_list|(
name|QueryTermExtractor
operator|.
name|getIdfWeightedTerms
argument_list|(
name|query
argument_list|,
name|reader
argument_list|,
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|WeightedTerm
index|[]
name|weightedTerms
parameter_list|)
block|{
name|termsToFind
operator|=
operator|new
name|HashMap
argument_list|()
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
name|weightedTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|termsToFind
operator|.
name|put
argument_list|(
name|weightedTerms
index|[
name|i
index|]
operator|.
name|term
argument_list|,
name|weightedTerms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.lucene.search.highlight.FragmentScorer#startFragment(org.apache.lucene.search.highlight.TextFragment) 	 */
DECL|method|startFragment
specifier|public
name|void
name|startFragment
parameter_list|(
name|TextFragment
name|newFragment
parameter_list|)
block|{
name|uniqueTermsInFragment
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|currentTextFragment
operator|=
name|newFragment
expr_stmt|;
name|totalScore
operator|=
literal|0
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.lucene.search.highlight.FragmentScorer#scoreToken(org.apache.lucene.analysis.Token) 	 */
DECL|method|getTokenScore
specifier|public
name|float
name|getTokenScore
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|String
name|termText
init|=
name|token
operator|.
name|termText
argument_list|()
decl_stmt|;
name|WeightedTerm
name|queryTerm
init|=
operator|(
name|WeightedTerm
operator|)
name|termsToFind
operator|.
name|get
argument_list|(
name|termText
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryTerm
operator|==
literal|null
condition|)
block|{
comment|//not a query term - return
return|return
literal|0
return|;
block|}
comment|//found a query term - is it unique in this doc?
if|if
condition|(
operator|!
name|uniqueTermsInFragment
operator|.
name|contains
argument_list|(
name|termText
argument_list|)
condition|)
block|{
name|totalScore
operator|+=
name|queryTerm
operator|.
name|getWeight
argument_list|()
expr_stmt|;
name|uniqueTermsInFragment
operator|.
name|add
argument_list|(
name|termText
argument_list|)
expr_stmt|;
block|}
return|return
name|queryTerm
operator|.
name|getWeight
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.lucene.search.highlight.FragmentScorer#endFragment(org.apache.lucene.search.highlight.TextFragment) 	 */
DECL|method|getFragmentScore
specifier|public
name|float
name|getFragmentScore
parameter_list|()
block|{
return|return
name|totalScore
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.lucene.search.highlight.FragmentScorer#allFragmentsProcessed() 	 */
DECL|method|allFragmentsProcessed
specifier|public
name|void
name|allFragmentsProcessed
parameter_list|()
block|{
comment|//this class has no special operations to perform at end of processing
block|}
block|}
end_class

end_unit

