begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|TermQuery
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
name|IndexReader
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

begin_class
DECL|class|SrndTermQuery
specifier|public
class|class
name|SrndTermQuery
extends|extends
name|SimpleTerm
block|{
DECL|method|SrndTermQuery
specifier|public
name|SrndTermQuery
parameter_list|(
name|String
name|termText
parameter_list|,
name|boolean
name|quoted
parameter_list|)
block|{
name|super
argument_list|(
name|quoted
argument_list|)
expr_stmt|;
name|this
operator|.
name|termText
operator|=
name|termText
expr_stmt|;
block|}
DECL|field|termText
specifier|private
specifier|final
name|String
name|termText
decl_stmt|;
DECL|method|getTermText
specifier|public
name|String
name|getTermText
parameter_list|()
block|{
return|return
name|termText
return|;
block|}
DECL|method|getLuceneTerm
specifier|public
name|Term
name|getLuceneTerm
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|getTermText
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toStringUnquoted
specifier|public
name|String
name|toStringUnquoted
parameter_list|()
block|{
return|return
name|getTermText
argument_list|()
return|;
block|}
DECL|method|visitMatchingTerms
specifier|public
name|void
name|visitMatchingTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|MatchingTermVisitor
name|mtv
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* check term presence in index here for symmetry with other SimpleTerm's */
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
name|getLuceneTerm
argument_list|(
name|fieldName
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Term
name|it
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
comment|/* same or following index term */
if|if
condition|(
operator|(
name|it
operator|!=
literal|null
operator|)
operator|&&
name|it
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
name|getTermText
argument_list|()
argument_list|)
operator|&&
name|it
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|mtv
operator|.
name|visitMatchingTerm
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No term in "
operator|+
name|fieldName
operator|+
literal|" field for: "
operator|+
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

