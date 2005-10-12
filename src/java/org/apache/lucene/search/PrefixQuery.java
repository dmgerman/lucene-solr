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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/** A Query that matches documents containing terms with a specified prefix. A PrefixQuery  * is built by QueryParser for input like<code>app*</code>. */
end_comment

begin_class
DECL|class|PrefixQuery
specifier|public
class|class
name|PrefixQuery
extends|extends
name|Query
block|{
DECL|field|prefix
specifier|private
name|Term
name|prefix
decl_stmt|;
comment|/** Constructs a query for terms starting with<code>prefix</code>. */
DECL|method|PrefixQuery
specifier|public
name|PrefixQuery
parameter_list|(
name|Term
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
comment|/** Returns the prefix of this query. */
DECL|method|getPrefix
specifier|public
name|Term
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
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
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|prefixText
init|=
name|prefix
operator|.
name|text
argument_list|()
decl_stmt|;
name|String
name|prefixField
init|=
name|prefix
operator|.
name|field
argument_list|()
decl_stmt|;
do|do
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefixText
argument_list|)
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|==
name|prefixField
condition|)
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
comment|// found a match
name|tq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
comment|// set the boost
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// add to query
comment|//System.out.println("added " + term);
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
comment|/** Prints a user-readable version of this query. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|prefix
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
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
operator|!
operator|(
name|o
operator|instanceof
name|PrefixQuery
operator|)
condition|)
return|return
literal|false
return|;
name|PrefixQuery
name|other
init|=
operator|(
name|PrefixQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|)
operator|&&
name|this
operator|.
name|prefix
operator|.
name|equals
argument_list|(
name|other
operator|.
name|prefix
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object.*/
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|prefix
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

