begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|search
operator|.
name|IndexSearcher
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
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Utility for query time joining using TermsQuery and TermsCollector.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|JoinUtil
specifier|public
specifier|final
class|class
name|JoinUtil
block|{
comment|// No instances allowed
DECL|method|JoinUtil
specifier|private
name|JoinUtil
parameter_list|()
block|{   }
comment|/**    * Method for query time joining.    *<p/>    * Execute the returned query with a {@link IndexSearcher} to retrieve all documents that have the same terms in the    * to field that match with documents matching the specified fromQuery and have the same terms in the from field.    *<p/>    * In the case a single document relates to more than one document the<code>multipleValuesPerDocument</code> option    * should be set to true. When the<code>multipleValuesPerDocument</code> is set to<code>true</code> only the    * the score from the first encountered join value originating from the 'from' side is mapped into the 'to' side.    * Even in the case when a second join value related to a specific document yields a higher score. Obviously this    * doesn't apply in the case that {@link ScoreMode#None} is used, since no scores are computed at all.    *</p>    * Memory considerations: During joining all unique join values are kept in memory. On top of that when the scoreMode    * isn't set to {@link ScoreMode#None} a float value per unique join value is kept in memory for computing scores.    * When scoreMode is set to {@link ScoreMode#Avg} also an additional integer value is kept in memory per unique    * join value.    *    * @param fromField                 The from field to join from    * @param multipleValuesPerDocument Whether the from field has multiple terms per document    * @param toField                   The to field to join to    * @param fromQuery                 The query to match documents on the from side    * @param fromSearcher              The searcher that executed the specified fromQuery    * @param scoreMode                 Instructs how scores from the fromQuery are mapped to the returned query    * @return a {@link Query} instance that can be used to join documents based on the    *         terms in the from and to field    * @throws IOException If I/O related errors occur    */
DECL|method|createJoinQuery
specifier|public
specifier|static
name|Query
name|createJoinQuery
parameter_list|(
name|String
name|fromField
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|,
name|String
name|toField
parameter_list|,
name|Query
name|fromQuery
parameter_list|,
name|IndexSearcher
name|fromSearcher
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|None
case|:
name|TermsCollector
name|termsCollector
init|=
name|TermsCollector
operator|.
name|create
argument_list|(
name|fromField
argument_list|,
name|multipleValuesPerDocument
argument_list|)
decl_stmt|;
name|fromSearcher
operator|.
name|search
argument_list|(
name|fromQuery
argument_list|,
name|termsCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|TermsQuery
argument_list|(
name|toField
argument_list|,
name|termsCollector
operator|.
name|getCollectorTerms
argument_list|()
argument_list|)
return|;
case|case
name|Total
case|:
case|case
name|Max
case|:
case|case
name|Avg
case|:
name|TermsWithScoreCollector
name|termsWithScoreCollector
init|=
name|TermsWithScoreCollector
operator|.
name|create
argument_list|(
name|fromField
argument_list|,
name|multipleValuesPerDocument
argument_list|,
name|scoreMode
argument_list|)
decl_stmt|;
name|fromSearcher
operator|.
name|search
argument_list|(
name|fromQuery
argument_list|,
name|termsWithScoreCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|TermsIncludingScoreQuery
argument_list|(
name|toField
argument_list|,
name|multipleValuesPerDocument
argument_list|,
name|termsWithScoreCollector
operator|.
name|getCollectedTerms
argument_list|()
argument_list|,
name|termsWithScoreCollector
operator|.
name|getScoresPerTerm
argument_list|()
argument_list|,
name|fromQuery
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Score mode %s isn't supported."
argument_list|,
name|scoreMode
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

