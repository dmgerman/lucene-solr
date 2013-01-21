begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.sampling
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|sampling
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
name|MultiFields
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
name|index
operator|.
name|DocsEnum
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
name|DocIdSetIterator
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
name|Bits
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
name|facet
operator|.
name|search
operator|.
name|DrillDown
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
name|facet
operator|.
name|search
operator|.
name|ScoredDocIDs
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
name|facet
operator|.
name|search
operator|.
name|ScoredDocIDsIterator
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResult
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResultNode
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Fix sampling results by counting the intersection between two lists: a  * TermDocs (list of documents in a certain category) and a DocIdSetIterator  * (list of documents matching the query).  *   *   * @lucene.experimental  */
end_comment

begin_comment
comment|// TODO (Facet): implement also an estimated fixing by ratio (taking into
end_comment

begin_comment
comment|// account "translation" of counts!)
end_comment

begin_class
DECL|class|TakmiSampleFixer
class|class
name|TakmiSampleFixer
implements|implements
name|SampleFixer
block|{
DECL|field|taxonomyReader
specifier|private
name|TaxonomyReader
name|taxonomyReader
decl_stmt|;
DECL|field|indexReader
specifier|private
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|searchParams
specifier|private
name|FacetSearchParams
name|searchParams
decl_stmt|;
DECL|method|TakmiSampleFixer
specifier|public
name|TakmiSampleFixer
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetSearchParams
name|searchParams
parameter_list|)
block|{
name|this
operator|.
name|indexReader
operator|=
name|indexReader
expr_stmt|;
name|this
operator|.
name|taxonomyReader
operator|=
name|taxonomyReader
expr_stmt|;
name|this
operator|.
name|searchParams
operator|=
name|searchParams
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fixResult
specifier|public
name|void
name|fixResult
parameter_list|(
name|ScoredDocIDs
name|origDocIds
parameter_list|,
name|FacetResult
name|fres
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetResultNode
name|topRes
init|=
name|fres
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|fixResultNode
argument_list|(
name|topRes
argument_list|,
name|origDocIds
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fix result node count, and, recursively, fix all its children    *     * @param facetResNode    *          result node to be fixed    * @param docIds    *          docids in effect    * @throws IOException If there is a low-level I/O error.    */
DECL|method|fixResultNode
specifier|private
name|void
name|fixResultNode
parameter_list|(
name|FacetResultNode
name|facetResNode
parameter_list|,
name|ScoredDocIDs
name|docIds
parameter_list|)
throws|throws
name|IOException
block|{
name|recount
argument_list|(
name|facetResNode
argument_list|,
name|docIds
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|frn
range|:
name|facetResNode
operator|.
name|subResults
control|)
block|{
name|fixResultNode
argument_list|(
name|frn
argument_list|,
name|docIds
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Internal utility: recount for a facet result node    *     * @param fresNode    *          result node to be recounted    * @param docIds    *          full set of matching documents.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|recount
specifier|private
name|void
name|recount
parameter_list|(
name|FacetResultNode
name|fresNode
parameter_list|,
name|ScoredDocIDs
name|docIds
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO (Facet): change from void to return the new, smaller docSet, and use
comment|// that for the children, as this will make their intersection ops faster.
comment|// can do this only when the new set is "sufficiently" smaller.
comment|/* We need the category's path name in order to do its recounting.      * If it is missing, because the option to label only part of the      * facet results was exercise, we need to calculate them anyway, so      * in essence sampling with recounting spends some extra cycles for      * labeling results for which labels are not required. */
if|if
condition|(
name|fresNode
operator|.
name|label
operator|==
literal|null
condition|)
block|{
name|fresNode
operator|.
name|label
operator|=
name|taxonomyReader
operator|.
name|getPath
argument_list|(
name|fresNode
operator|.
name|ordinal
argument_list|)
expr_stmt|;
block|}
name|CategoryPath
name|catPath
init|=
name|fresNode
operator|.
name|label
decl_stmt|;
name|Term
name|drillDownTerm
init|=
name|DrillDown
operator|.
name|term
argument_list|(
name|searchParams
argument_list|,
name|catPath
argument_list|)
decl_stmt|;
comment|// TODO (Facet): avoid Multi*?
name|Bits
name|liveDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|int
name|updatedCount
init|=
name|countIntersection
argument_list|(
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|indexReader
argument_list|,
name|liveDocs
argument_list|,
name|drillDownTerm
operator|.
name|field
argument_list|()
argument_list|,
name|drillDownTerm
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|docIds
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
name|fresNode
operator|.
name|value
operator|=
name|updatedCount
expr_stmt|;
block|}
comment|/**    * Count the size of the intersection between two lists: a TermDocs (list of    * documents in a certain category) and a DocIdSetIterator (list of documents    * matching a query).    */
DECL|method|countIntersection
specifier|private
specifier|static
name|int
name|countIntersection
parameter_list|(
name|DocsEnum
name|p1
parameter_list|,
name|ScoredDocIDsIterator
name|p2
parameter_list|)
throws|throws
name|IOException
block|{
comment|// The documentation of of both TermDocs and DocIdSetIterator claim
comment|// that we must do next() before doc(). So we do, and if one of the
comment|// lists is empty, obviously return 0;
if|if
condition|(
name|p1
operator|==
literal|null
operator|||
name|p1
operator|.
name|nextDoc
argument_list|()
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
operator|!
name|p2
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|d1
init|=
name|p1
operator|.
name|docID
argument_list|()
decl_stmt|;
name|int
name|d2
init|=
name|p2
operator|.
name|getDocID
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|d1
operator|==
name|d2
condition|)
block|{
operator|++
name|count
expr_stmt|;
if|if
condition|(
name|p1
operator|.
name|nextDoc
argument_list|()
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
comment|// end of list 1, nothing more in intersection
block|}
name|d1
operator|=
name|p1
operator|.
name|docID
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|advance
argument_list|(
name|p2
argument_list|,
name|d1
argument_list|)
condition|)
block|{
break|break;
comment|// end of list 2, nothing more in intersection
block|}
name|d2
operator|=
name|p2
operator|.
name|getDocID
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|d1
operator|<
name|d2
condition|)
block|{
if|if
condition|(
name|p1
operator|.
name|advance
argument_list|(
name|d2
argument_list|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
comment|// end of list 1, nothing more in intersection
block|}
name|d1
operator|=
name|p1
operator|.
name|docID
argument_list|()
expr_stmt|;
block|}
else|else
comment|/* d1>d2 */
block|{
if|if
condition|(
operator|!
name|advance
argument_list|(
name|p2
argument_list|,
name|d1
argument_list|)
condition|)
block|{
break|break;
comment|// end of list 2, nothing more in intersection
block|}
name|d2
operator|=
name|p2
operator|.
name|getDocID
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**    * utility: advance the iterator until finding (or exceeding) specific    * document    *     * @param iterator    *          iterator being advanced    * @param targetDoc    *          target of advancing    * @return false if iterator exhausted, true otherwise.    */
DECL|method|advance
specifier|private
specifier|static
name|boolean
name|advance
parameter_list|(
name|ScoredDocIDsIterator
name|iterator
parameter_list|,
name|int
name|targetDoc
parameter_list|)
block|{
while|while
condition|(
name|iterator
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|iterator
operator|.
name|getDocID
argument_list|()
operator|>=
name|targetDoc
condition|)
block|{
return|return
literal|true
return|;
comment|// target reached
block|}
block|}
return|return
literal|false
return|;
comment|// exhausted
block|}
block|}
end_class

end_unit

