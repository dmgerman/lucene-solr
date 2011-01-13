begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Fields
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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|Terms
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
name|TermsEnum
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
name|Bits
import|;
end_import

begin_comment
comment|/**  * A wrapper for {@link MultiTermQuery}, that exposes its  * functionality as a {@link Filter}.  *<P>  *<code>MultiTermQueryWrapperFilter</code> is not designed to  * be used by itself. Normally you subclass it to provide a Filter  * counterpart for a {@link MultiTermQuery} subclass.  *<P>  * For example, {@link TermRangeFilter} and {@link PrefixFilter} extend  *<code>MultiTermQueryWrapperFilter</code>.  * This class also provides the functionality behind  * {@link MultiTermQuery#CONSTANT_SCORE_FILTER_REWRITE};  * this is why it is not abstract.  */
end_comment

begin_class
DECL|class|MultiTermQueryWrapperFilter
specifier|public
class|class
name|MultiTermQueryWrapperFilter
parameter_list|<
name|Q
extends|extends
name|MultiTermQuery
parameter_list|>
extends|extends
name|Filter
block|{
DECL|field|query
specifier|protected
specifier|final
name|Q
name|query
decl_stmt|;
comment|/**    * Wrap a {@link MultiTermQuery} as a Filter.    */
DECL|method|MultiTermQueryWrapperFilter
specifier|protected
name|MultiTermQueryWrapperFilter
parameter_list|(
name|Q
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// query.toString should be ok for the filter, too, if the query boost is 1.0f
return|return
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MultiTermQueryWrapperFilter
operator|)
name|o
operator|)
operator|.
name|query
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Returns the field name for this query */
DECL|method|getField
specifier|public
specifier|final
name|String
name|getField
parameter_list|()
block|{
return|return
name|query
operator|.
name|getField
argument_list|()
return|;
block|}
comment|/**    * Expert: Return the number of unique terms visited during execution of the filter.    * If there are many of them, you may consider using another filter type    * or optimize your total term count in index.    *<p>This method is not thread safe, be sure to only call it when no filter is running!    * If you re-use the same filter instance for another    * search, be sure to first reset the term counter    * with {@link #clearTotalNumberOfTerms}.    * @see #clearTotalNumberOfTerms    */
DECL|method|getTotalNumberOfTerms
specifier|public
name|int
name|getTotalNumberOfTerms
parameter_list|()
block|{
return|return
name|query
operator|.
name|getTotalNumberOfTerms
argument_list|()
return|;
block|}
comment|/**    * Expert: Resets the counting of unique terms.    * Do this before executing the filter.    * @see #getTotalNumberOfTerms    */
DECL|method|clearTotalNumberOfTerms
specifier|public
name|void
name|clearTotalNumberOfTerms
parameter_list|()
block|{
name|query
operator|.
name|clearTotalNumberOfTerms
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a DocIdSet with documents that should be permitted in search    * results.    */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
name|reader
init|=
name|context
operator|.
name|reader
decl_stmt|;
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|// reader has no fields
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|query
operator|.
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// field does not exist
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
decl_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
if|if
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// fill into a OpenBitSet
specifier|final
name|OpenBitSet
name|bitSet
init|=
operator|new
name|OpenBitSet
argument_list|(
name|context
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|termCount
init|=
literal|0
decl_stmt|;
specifier|final
name|Bits
name|delDocs
init|=
name|reader
operator|.
name|getDeletedDocs
argument_list|()
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
do|do
block|{
name|termCount
operator|++
expr_stmt|;
comment|// System.out.println("  iter termCount=" + termCount + " term=" +
comment|// enumerator.term().toBytesString());
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|delDocs
argument_list|,
name|docsEnum
argument_list|)
expr_stmt|;
specifier|final
name|DocsEnum
operator|.
name|BulkReadResult
name|result
init|=
name|docsEnum
operator|.
name|getBulkResult
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|count
init|=
name|docsEnum
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
index|[]
name|docs
init|=
name|result
operator|.
name|docs
operator|.
name|ints
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
do|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
do|;
comment|// System.out.println("  done termCount=" + termCount);
name|query
operator|.
name|incTotalNumberOfTerms
argument_list|(
name|termCount
argument_list|)
expr_stmt|;
return|return
name|bitSet
return|;
block|}
else|else
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
block|}
block|}
end_class

end_unit

