begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/** The abstract base class for queries.<p>Instantiable subclasses are:<ul><li> {@link TermQuery}<li> {@link BooleanQuery}<li> {@link WildcardQuery}<li> {@link PhraseQuery}<li> {@link PrefixQuery}<li> {@link MultiPhraseQuery}<li> {@link FuzzyQuery}<li> {@link RegexpQuery}<li> {@link TermRangeQuery}<li> {@link PointRangeQuery}<li> {@link ConstantScoreQuery}<li> {@link DisjunctionMaxQuery}<li> {@link MatchAllDocsQuery}</ul><p>See also the family of {@link org.apache.lucene.search.spans Span Queries}        and additional queries available in the<a href="{@docRoot}/../queries/overview-summary.html">Queries module</a> */
end_comment

begin_class
DECL|class|Query
specifier|public
specifier|abstract
class|class
name|Query
block|{
comment|/** Prints a query to a string, with<code>field</code> assumed to be the     * default field and omitted.    */
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** Prints a query to a string. */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|""
argument_list|)
return|;
block|}
comment|/**    * Expert: Constructs an appropriate Weight implementation for this query.    *<p>    * Only implemented by primitive queries, which re-write to themselves.    *    * @param needsScores   True if document scores ({@link Scorer#score}) or match    *                      frequencies ({@link Scorer#freq}) are needed.    */
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Query "
operator|+
name|this
operator|+
literal|" does not implement createWeight"
argument_list|)
throw|;
block|}
comment|/** Expert: called to re-write queries into primitive queries. For example,    * a PrefixQuery will be rewritten into a BooleanQuery that consists    * of TermQuerys.    */
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
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
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
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|getClass
argument_list|()
operator|==
name|obj
operator|.
name|getClass
argument_list|()
return|;
block|}
block|}
end_class

end_unit

