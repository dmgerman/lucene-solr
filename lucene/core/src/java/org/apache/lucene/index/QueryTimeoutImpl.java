begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|nanoTime
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link QueryTimeout} that can be used by  * the {@link ExitableDirectoryReader} class to time out and exit out  * when a query takes a long time to rewrite.  */
end_comment

begin_class
DECL|class|QueryTimeoutImpl
specifier|public
class|class
name|QueryTimeoutImpl
implements|implements
name|QueryTimeout
block|{
comment|/**    * The local variable to store the time beyond which, the processing should exit.    */
DECL|field|timeoutAt
specifier|private
name|Long
name|timeoutAt
decl_stmt|;
comment|/**     * Sets the time at which to time out by adding the given timeAllowed to the current time.    *     * @param timeAllowed Number of milliseconds after which to time out. Use {@code Long.MAX_VALUE}    *                    to effectively never time out.    */
DECL|method|QueryTimeoutImpl
specifier|public
name|QueryTimeoutImpl
parameter_list|(
name|long
name|timeAllowed
parameter_list|)
block|{
if|if
condition|(
name|timeAllowed
operator|<
literal|0L
condition|)
block|{
name|timeAllowed
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|timeoutAt
operator|=
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeAllowed
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns time at which to time out, in nanoseconds relative to the (JVM-specific)    * epoch for {@link System#nanoTime()}, to compare with the value returned by    * {@code nanoTime()}.    */
DECL|method|getTimeoutAt
specifier|public
name|Long
name|getTimeoutAt
parameter_list|()
block|{
return|return
name|timeoutAt
return|;
block|}
comment|/**    * Return true if {@link #reset()} has not been called    * and the elapsed time has exceeded the time allowed.    */
annotation|@
name|Override
DECL|method|shouldExit
specifier|public
name|boolean
name|shouldExit
parameter_list|()
block|{
return|return
name|timeoutAt
operator|!=
literal|null
operator|&&
name|nanoTime
argument_list|()
operator|-
name|timeoutAt
operator|>
literal|0
return|;
block|}
comment|/**    * Reset the timeout value.    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|timeoutAt
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"timeoutAt: "
operator|+
name|timeoutAt
operator|+
literal|" (System.nanoTime(): "
operator|+
name|nanoTime
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

