begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
package|;
end_package

begin_comment
comment|/**  * This exception is thrown when determinizing an automaton would result in one  * has too many states.  */
end_comment

begin_class
DECL|class|TooComplexToDeterminizeException
specifier|public
class|class
name|TooComplexToDeterminizeException
extends|extends
name|RuntimeException
block|{
DECL|field|automaton
specifier|private
specifier|transient
specifier|final
name|Automaton
name|automaton
decl_stmt|;
DECL|field|regExp
specifier|private
specifier|transient
specifier|final
name|RegExp
name|regExp
decl_stmt|;
DECL|field|maxDeterminizedStates
specifier|private
specifier|transient
specifier|final
name|int
name|maxDeterminizedStates
decl_stmt|;
comment|/** Use this constructor when the RegExp failed to convert to an automaton. */
DECL|method|TooComplexToDeterminizeException
specifier|public
name|TooComplexToDeterminizeException
parameter_list|(
name|RegExp
name|regExp
parameter_list|,
name|TooComplexToDeterminizeException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
literal|"Determinizing "
operator|+
name|regExp
operator|.
name|getOriginalString
argument_list|()
operator|+
literal|" would result in more than "
operator|+
name|cause
operator|.
name|maxDeterminizedStates
operator|+
literal|" states."
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|regExp
operator|=
name|regExp
expr_stmt|;
name|this
operator|.
name|automaton
operator|=
name|cause
operator|.
name|automaton
expr_stmt|;
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|cause
operator|.
name|maxDeterminizedStates
expr_stmt|;
block|}
comment|/** Use this constructor when the automaton failed to determinize. */
DECL|method|TooComplexToDeterminizeException
specifier|public
name|TooComplexToDeterminizeException
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|super
argument_list|(
literal|"Determinizing automaton with "
operator|+
name|automaton
operator|.
name|getNumStates
argument_list|()
operator|+
literal|" states and "
operator|+
name|automaton
operator|.
name|getNumTransitions
argument_list|()
operator|+
literal|" transitions would result in more than "
operator|+
name|maxDeterminizedStates
operator|+
literal|" states."
argument_list|)
expr_stmt|;
name|this
operator|.
name|automaton
operator|=
name|automaton
expr_stmt|;
name|this
operator|.
name|regExp
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|maxDeterminizedStates
expr_stmt|;
block|}
comment|/** Returns the automaton that caused this exception, if any. */
DECL|method|getAutomaton
specifier|public
name|Automaton
name|getAutomaton
parameter_list|()
block|{
return|return
name|automaton
return|;
block|}
comment|/**    * Return the RegExp that caused this exception if any.    */
DECL|method|getRegExp
specifier|public
name|RegExp
name|getRegExp
parameter_list|()
block|{
return|return
name|regExp
return|;
block|}
comment|/** Get the maximum number of allowed determinized states. */
DECL|method|getMaxDeterminizedStates
specifier|public
name|int
name|getMaxDeterminizedStates
parameter_list|()
block|{
return|return
name|maxDeterminizedStates
return|;
block|}
block|}
end_class

end_unit

