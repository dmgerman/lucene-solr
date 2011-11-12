begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Simple counter class  *   * @lucene.internal  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Counter
specifier|public
specifier|abstract
class|class
name|Counter
block|{
comment|/**    * Adds the given delta to the counters current value    *     * @param delta    *          the delta to add    * @return the counters updated value    */
DECL|method|addAndGet
specifier|public
specifier|abstract
name|long
name|addAndGet
parameter_list|(
name|long
name|delta
parameter_list|)
function_decl|;
comment|/**    * Returns the counters current value    *     * @return the counters current value    */
DECL|method|get
specifier|public
specifier|abstract
name|long
name|get
parameter_list|()
function_decl|;
comment|/**    * Returns a new counter. The returned counter is not thread-safe.    */
DECL|method|newCounter
specifier|public
specifier|static
name|Counter
name|newCounter
parameter_list|()
block|{
return|return
name|newCounter
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns a new counter.    *     * @param threadSafe    *<code>true</code> if the returned counter can be used by multiple    *          threads concurrently.    * @return a new counter.    */
DECL|method|newCounter
specifier|public
specifier|static
name|Counter
name|newCounter
parameter_list|(
name|boolean
name|threadSafe
parameter_list|)
block|{
return|return
name|threadSafe
condition|?
operator|new
name|AtomicCounter
argument_list|()
else|:
operator|new
name|SerialCounter
argument_list|()
return|;
block|}
DECL|class|SerialCounter
specifier|private
specifier|final
specifier|static
class|class
name|SerialCounter
extends|extends
name|Counter
block|{
DECL|field|count
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|addAndGet
specifier|public
name|long
name|addAndGet
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
return|return
name|count
operator|+=
name|delta
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|()
block|{
return|return
name|count
return|;
block|}
empty_stmt|;
block|}
DECL|class|AtomicCounter
specifier|private
specifier|final
specifier|static
class|class
name|AtomicCounter
extends|extends
name|Counter
block|{
DECL|field|count
specifier|private
specifier|final
name|AtomicLong
name|count
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|addAndGet
specifier|public
name|long
name|addAndGet
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
return|return
name|count
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|()
block|{
return|return
name|count
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

