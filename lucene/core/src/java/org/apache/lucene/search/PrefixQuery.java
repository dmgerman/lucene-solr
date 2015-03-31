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
name|AttributeSource
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
name|BytesRef
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|Automaton
import|;
end_import

begin_comment
comment|/** A Query that matches documents containing terms with a specified prefix. A PrefixQuery  * is built by QueryParser for input like<code>app*</code>.  *  *<p>This query uses the {@link  * MultiTermQuery#CONSTANT_SCORE_REWRITE}  * rewrite method. */
end_comment

begin_class
DECL|class|PrefixQuery
specifier|public
class|class
name|PrefixQuery
extends|extends
name|AutomatonQuery
block|{
comment|/** Constructs a query for terms starting with<code>prefix</code>. */
DECL|method|PrefixQuery
specifier|public
name|PrefixQuery
parameter_list|(
name|Term
name|prefix
parameter_list|)
block|{
comment|// It's OK to pass unlimited maxDeterminizedStates: the automaton is born small and determinized:
name|super
argument_list|(
name|prefix
argument_list|,
name|toAutomaton
argument_list|(
name|prefix
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"prefix cannot be null"
argument_list|)
throw|;
block|}
block|}
comment|/** Build an automaton accepting all terms with the specified prefix. */
DECL|method|toAutomaton
specifier|public
specifier|static
name|Automaton
name|toAutomaton
parameter_list|(
name|BytesRef
name|prefix
parameter_list|)
block|{
name|Automaton
name|automaton
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|int
name|lastState
init|=
name|automaton
operator|.
name|createState
argument_list|()
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
name|prefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|state
init|=
name|automaton
operator|.
name|createState
argument_list|()
decl_stmt|;
name|automaton
operator|.
name|addTransition
argument_list|(
name|lastState
argument_list|,
name|state
argument_list|,
name|prefix
operator|.
name|bytes
index|[
name|prefix
operator|.
name|offset
operator|+
name|i
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|lastState
operator|=
name|state
expr_stmt|;
block|}
name|automaton
operator|.
name|setAccept
argument_list|(
name|lastState
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|automaton
operator|.
name|addTransition
argument_list|(
name|lastState
argument_list|,
name|lastState
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
expr_stmt|;
name|automaton
operator|.
name|finishState
argument_list|()
expr_stmt|;
assert|assert
name|automaton
operator|.
name|isDeterministic
argument_list|()
assert|;
return|return
name|automaton
return|;
block|}
comment|/** Returns the prefix of this query. */
DECL|method|getPrefix
specifier|public
name|Term
name|getPrefix
parameter_list|()
block|{
return|return
name|term
return|;
block|}
comment|/** Prints a user-readable version of this query. */
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
operator|!
name|getField
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
name|getField
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
name|term
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
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
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
comment|// super.equals() ensures we are the same class
name|PrefixQuery
name|other
init|=
operator|(
name|PrefixQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

