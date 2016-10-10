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
comment|/**  * Automaton representation for matching UTF-8 byte[].  */
end_comment

begin_class
DECL|class|ByteRunAutomaton
specifier|public
class|class
name|ByteRunAutomaton
extends|extends
name|RunAutomaton
block|{
comment|/** Converts incoming automaton to byte-based (UTF32ToUTF8) first */
DECL|method|ByteRunAutomaton
specifier|public
name|ByteRunAutomaton
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
name|this
argument_list|(
name|a
argument_list|,
literal|false
argument_list|,
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
block|}
comment|/** expert: if utf8 is true, the input is already byte-based */
DECL|method|ByteRunAutomaton
specifier|public
name|ByteRunAutomaton
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|boolean
name|isBinary
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|super
argument_list|(
name|isBinary
condition|?
name|a
else|:
operator|new
name|UTF32ToUTF8
argument_list|()
operator|.
name|convert
argument_list|(
name|a
argument_list|)
argument_list|,
literal|256
argument_list|,
literal|true
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true if the given byte array is accepted by this automaton    */
DECL|method|run
specifier|public
name|boolean
name|run
parameter_list|(
name|byte
index|[]
name|s
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|p
init|=
literal|0
decl_stmt|;
name|int
name|l
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|l
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
name|step
argument_list|(
name|p
argument_list|,
name|s
index|[
name|i
index|]
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|==
operator|-
literal|1
condition|)
return|return
literal|false
return|;
block|}
return|return
name|accept
index|[
name|p
index|]
return|;
block|}
block|}
end_class

end_unit

