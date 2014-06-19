begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Holds one transition from an {@link Automaton}.  This is typically  *  used temporarily when iterating through transitions by invoking  *  {@link Automaton#initTransition} and {@link Automaton#getNextTransition}. */
end_comment

begin_class
DECL|class|Transition
specifier|public
class|class
name|Transition
block|{
comment|/** Sole constructor. */
DECL|method|Transition
specifier|public
name|Transition
parameter_list|()
block|{   }
comment|/** Source state. */
DECL|field|source
specifier|public
name|int
name|source
decl_stmt|;
comment|/** Destination state. */
DECL|field|dest
specifier|public
name|int
name|dest
decl_stmt|;
comment|/** Minimum accepted label (inclusive). */
DECL|field|min
specifier|public
name|int
name|min
decl_stmt|;
comment|/** Maximum accepted label (inclusive). */
DECL|field|max
specifier|public
name|int
name|max
decl_stmt|;
comment|/** Remembers where we are in the iteration; init to -1 to provoke    *  exception if nextTransition is called without first initTransition. */
DECL|field|transitionUpto
name|int
name|transitionUpto
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|source
operator|+
literal|" --> "
operator|+
name|dest
operator|+
literal|" "
operator|+
operator|(
name|char
operator|)
name|min
operator|+
literal|"-"
operator|+
operator|(
name|char
operator|)
name|max
return|;
block|}
block|}
end_class

end_unit

