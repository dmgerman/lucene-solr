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
name|Parameter
import|;
end_import

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** A clause in a BooleanQuery. */
end_comment

begin_class
DECL|class|BooleanClause
specifier|public
class|class
name|BooleanClause
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** Specifies how terms may occur in matching documents. */
DECL|class|Occur
specifier|public
specifier|static
specifier|final
class|class
name|Occur
extends|extends
name|Parameter
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|method|Occur
specifier|private
name|Occur
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// typesafe enum pattern, no public constructor
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|MUST
condition|)
return|return
literal|"+"
return|;
if|if
condition|(
name|this
operator|==
name|MUST_NOT
condition|)
return|return
literal|"-"
return|;
return|return
literal|""
return|;
block|}
comment|/** Use this operator for terms that<i>must</i> appear in the matching documents. */
DECL|field|MUST
specifier|public
specifier|static
specifier|final
name|Occur
name|MUST
init|=
operator|new
name|Occur
argument_list|(
literal|"MUST"
argument_list|)
decl_stmt|;
comment|/** Use this operator for terms that<i>should</i> appear in the       * matching documents. For a BooleanQuery with two<code>SHOULD</code>       * subqueries, at least one of the queries must appear in the matching documents. */
DECL|field|SHOULD
specifier|public
specifier|static
specifier|final
name|Occur
name|SHOULD
init|=
operator|new
name|Occur
argument_list|(
literal|"SHOULD"
argument_list|)
decl_stmt|;
comment|/** Use this operator for terms that<i>must not</i> appear in the matching documents.      * Note that it is not possible to search for queries that only consist      * of a<code>MUST_NOT</code> query. */
DECL|field|MUST_NOT
specifier|public
specifier|static
specifier|final
name|Occur
name|MUST_NOT
init|=
operator|new
name|Occur
argument_list|(
literal|"MUST_NOT"
argument_list|)
decl_stmt|;
block|}
comment|/** The query whose matching documents are combined by the boolean query.    */
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
comment|// TODO: decrease visibility for Lucene 2.0
DECL|field|occur
specifier|private
name|Occur
name|occur
init|=
name|Occur
operator|.
name|SHOULD
decl_stmt|;
comment|/** Constructs a BooleanClause.   */
DECL|method|BooleanClause
specifier|public
name|BooleanClause
parameter_list|(
name|Query
name|query
parameter_list|,
name|Occur
name|occur
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|occur
operator|=
name|occur
expr_stmt|;
block|}
DECL|method|getOccur
specifier|public
name|Occur
name|getOccur
parameter_list|()
block|{
return|return
name|occur
return|;
block|}
DECL|method|setOccur
specifier|public
name|void
name|setOccur
parameter_list|(
name|Occur
name|occur
parameter_list|)
block|{
name|this
operator|.
name|occur
operator|=
name|occur
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|setQuery
specifier|public
name|void
name|setQuery
parameter_list|(
name|Query
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
DECL|method|isProhibited
specifier|public
name|boolean
name|isProhibited
parameter_list|()
block|{
return|return
name|Occur
operator|.
name|MUST_NOT
operator|.
name|equals
argument_list|(
name|occur
argument_list|)
return|;
block|}
DECL|method|isRequired
specifier|public
name|boolean
name|isRequired
parameter_list|()
block|{
return|return
name|Occur
operator|.
name|MUST
operator|.
name|equals
argument_list|(
name|occur
argument_list|)
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
name|BooleanClause
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanClause
name|other
init|=
operator|(
name|BooleanClause
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
name|other
operator|.
name|query
argument_list|)
operator|&&
name|this
operator|.
name|occur
operator|.
name|equals
argument_list|(
name|other
operator|.
name|occur
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
name|query
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|Occur
operator|.
name|MUST
operator|.
name|equals
argument_list|(
name|occur
argument_list|)
condition|?
literal|1
else|:
literal|0
operator|)
operator|^
operator|(
name|Occur
operator|.
name|MUST_NOT
operator|.
name|equals
argument_list|(
name|occur
argument_list|)
condition|?
literal|2
else|:
literal|0
operator|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|occur
operator|.
name|toString
argument_list|()
operator|+
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

