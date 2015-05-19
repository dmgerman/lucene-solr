begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|TermContext
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
name|TermState
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
name|util
operator|.
name|ToStringUtils
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
name|Map
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

begin_comment
comment|/** Matches spans containing a term.  * This should not be used for terms that are indexed at position Integer.MAX_VALUE.  */
end_comment

begin_class
DECL|class|SpanTermQuery
specifier|public
class|class
name|SpanTermQuery
extends|extends
name|SpanQuery
block|{
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
comment|/** Construct a SpanTermQuery matching the named term's spans. */
DECL|method|SpanTermQuery
specifier|public
name|SpanTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|/** Return the term whose spans are matched. */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|term
operator|.
name|field
argument_list|()
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
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
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
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
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
name|term
operator|.
name|hashCode
argument_list|()
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SpanTermQuery
name|other
init|=
operator|(
name|SpanTermQuery
operator|)
name|obj
decl_stmt|;
return|return
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|,
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|TermContext
name|termContext
init|=
name|termContexts
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|final
name|TermState
name|state
decl_stmt|;
if|if
condition|(
name|termContext
operator|==
literal|null
condition|)
block|{
comment|// this happens with span-not query, as it doesn't include the NOT side in extractTerms()
comment|// so we seek to the term now in this segment..., this sucks because it's ugly mostly!
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
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|terms
operator|.
name|hasPositions
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field \""
operator|+
name|term
operator|.
name|field
argument_list|()
operator|+
literal|"\" was indexed without position data; cannot run SpanTermQuery (term="
operator|+
name|term
operator|.
name|text
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
name|state
operator|=
name|termsEnum
operator|.
name|termState
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|state
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|state
operator|=
name|termContext
operator|.
name|get
argument_list|(
name|context
operator|.
name|ord
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// term is not present in that reader
return|return
literal|null
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
specifier|final
name|PostingsEnum
name|postings
init|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|acceptDocs
argument_list|,
literal|null
argument_list|,
name|collector
operator|.
name|requiredPostings
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TermSpans
argument_list|(
name|postings
argument_list|,
name|term
argument_list|)
return|;
block|}
block|}
end_class

end_unit

