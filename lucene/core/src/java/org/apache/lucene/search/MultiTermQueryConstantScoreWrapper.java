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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Objects
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
name|index
operator|.
name|LeafReaderContext
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
name|PostingsEnum
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
name|util
operator|.
name|BitDocIdSet
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
comment|/**  * A wrapper for {@link MultiTermQuery}, that exposes its  * functionality as a {@link Filter}.  *<P>  *<code>MultiTermQueryWrapperFilter</code> is not designed to  * be used by itself. Normally you subclass it to provide a Filter  * counterpart for a {@link MultiTermQuery} subclass.  *<P>  * This class also provides the functionality behind  * {@link MultiTermQuery#CONSTANT_SCORE_REWRITE};  * this is why it is not abstract.  */
end_comment

begin_class
DECL|class|MultiTermQueryConstantScoreWrapper
specifier|final
class|class
name|MultiTermQueryConstantScoreWrapper
parameter_list|<
name|Q
extends|extends
name|MultiTermQuery
parameter_list|>
extends|extends
name|Query
block|{
DECL|field|query
specifier|protected
specifier|final
name|Q
name|query
decl_stmt|;
comment|/**    * Wrap a {@link MultiTermQuery} as a Filter.    */
DECL|method|MultiTermQueryConstantScoreWrapper
specifier|protected
name|MultiTermQueryConstantScoreWrapper
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
parameter_list|(
name|String
name|field
parameter_list|)
block|{
comment|// query.toString should be ok for the filter, too, if the query boost is 1.0f
return|return
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|}
argument_list|)
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
specifier|final
name|MultiTermQueryConstantScoreWrapper
name|that
init|=
operator|(
name|MultiTermQueryConstantScoreWrapper
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|that
operator|.
name|query
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|that
operator|.
name|getBoost
argument_list|()
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
name|Objects
operator|.
name|hash
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|query
argument_list|,
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
comment|// no-op
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
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
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
literal|null
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
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|PostingsEnum
name|docs
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BitDocIdSet
name|set
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Scorer
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|disi
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|disi
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

