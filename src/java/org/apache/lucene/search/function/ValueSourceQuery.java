begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|search
operator|.
name|*
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Expert: A Query that sets the scores of document to the  * values obtained from a {@link org.apache.lucene.search.function.ValueSource ValueSource}.  *<p>  * This query provides a score for<em>each and every</em> undeleted document in the index.      *<p>  * The value source can be based on a (cached) value of an indexed field, but it  * can also be based on an external source, e.g. values read from an external database.   *<p>  * Score is set as: Score(doc,query) = query.getBoost()<sup>2</sup> * valueSource(doc).    *  *<p><font color="#FF0000">  * WARNING: The status of the<b>search.function</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  */
end_comment

begin_class
DECL|class|ValueSourceQuery
specifier|public
class|class
name|ValueSourceQuery
extends|extends
name|Query
block|{
DECL|field|valSrc
name|ValueSource
name|valSrc
decl_stmt|;
comment|/**    * Create a value source query    * @param valSrc provides the values defines the function to be used for scoring    */
DECL|method|ValueSourceQuery
specifier|public
name|ValueSourceQuery
parameter_list|(
name|ValueSource
name|valSrc
parameter_list|)
block|{
name|this
operator|.
name|valSrc
operator|=
name|valSrc
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#rewrite(org.apache.lucene.index.IndexReader) */
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
return|return
name|this
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#extractTerms(java.util.Set) */
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
name|terms
parameter_list|)
block|{
comment|// no terms involved here
block|}
DECL|class|ValueSourceWeight
class|class
name|ValueSourceWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
name|Similarity
name|similarity
decl_stmt|;
DECL|field|queryNorm
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
name|float
name|queryWeight
decl_stmt|;
DECL|method|ValueSourceWeight
specifier|public
name|ValueSourceWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#getQuery() */
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|ValueSourceQuery
operator|.
name|this
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#getValue() */
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|queryWeight
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#sumOfSquaredWeights() */
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
name|queryWeight
operator|=
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#normalize(float) */
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|norm
expr_stmt|;
name|queryWeight
operator|*=
name|this
operator|.
name|queryNorm
expr_stmt|;
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|similarity
argument_list|,
name|reader
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#explain(org.apache.lucene.index.IndexReader, int) */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Searcher
name|searcher
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|similarity
argument_list|,
name|reader
argument_list|,
name|this
argument_list|)
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
comment|/**    * A scorer that (simply) matches all documents, and scores each document with     * the value of the value soure in effect. As an example, if the value source     * is a (cached) field source, then value of that field in that document will     * be used. (assuming field is indexed for this doc, with a single token.)       */
DECL|class|ValueSourceScorer
specifier|private
class|class
name|ValueSourceScorer
extends|extends
name|Scorer
block|{
DECL|field|weight
specifier|private
specifier|final
name|ValueSourceWeight
name|weight
decl_stmt|;
DECL|field|qWeight
specifier|private
specifier|final
name|float
name|qWeight
decl_stmt|;
DECL|field|vals
specifier|private
specifier|final
name|DocValues
name|vals
decl_stmt|;
DECL|field|termDocs
specifier|private
specifier|final
name|TermDocs
name|termDocs
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|// constructor
DECL|method|ValueSourceScorer
specifier|private
name|ValueSourceScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|ValueSourceWeight
name|w
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|w
expr_stmt|;
name|this
operator|.
name|qWeight
operator|=
name|w
operator|.
name|getValue
argument_list|()
expr_stmt|;
comment|// this is when/where the values are first created.
name|vals
operator|=
name|valSrc
operator|.
name|getValues
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|termDocs
operator|=
name|reader
operator|.
name|termDocs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated use {@link #nextDoc()} instead. */
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|termDocs
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doc
operator|=
name|termDocs
operator|.
name|next
argument_list|()
condition|?
name|termDocs
operator|.
name|doc
argument_list|()
else|:
name|NO_MORE_DOCS
return|;
block|}
comment|/** @deprecated use {@link #docID()} instead. */
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|termDocs
operator|.
name|doc
argument_list|()
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Scorer#score() */
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|qWeight
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
return|;
block|}
comment|/** @deprecated use {@link #advance(int)} instead. */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|termDocs
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
return|;
block|}
DECL|method|advance
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
name|doc
operator|=
name|termDocs
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|?
name|termDocs
operator|.
name|doc
argument_list|()
else|:
name|NO_MORE_DOCS
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Scorer#explain(int) */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|sc
init|=
name|qWeight
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|Explanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|sc
argument_list|,
name|ValueSourceQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|vals
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|weight
operator|.
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
return|return
operator|new
name|ValueSourceQuery
operator|.
name|ValueSourceWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
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
name|valSrc
operator|.
name|toString
argument_list|()
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
comment|/** Returns true if<code>o</code> is equal to this. */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ValueSourceQuery
name|other
init|=
operator|(
name|ValueSourceQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|&&
name|this
operator|.
name|valSrc
operator|.
name|equals
argument_list|(
name|other
operator|.
name|valSrc
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object. */
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|valSrc
operator|.
name|hashCode
argument_list|()
operator|)
operator|^
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

