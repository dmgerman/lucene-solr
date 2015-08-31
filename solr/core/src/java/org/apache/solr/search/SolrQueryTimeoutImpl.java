begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|QueryTimeout
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link QueryTimeout} that is used by Solr.   * It uses a ThreadLocal variable to track the timeoutAt value  * for each request thread.  */
end_comment

begin_class
DECL|class|SolrQueryTimeoutImpl
specifier|public
class|class
name|SolrQueryTimeoutImpl
implements|implements
name|QueryTimeout
block|{
comment|/**    * The ThreadLocal variable to store the time beyond which, the processing should exit.    */
DECL|field|timeoutAt
specifier|public
specifier|static
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|timeoutAt
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SolrQueryTimeoutImpl
specifier|private
name|SolrQueryTimeoutImpl
parameter_list|()
block|{ }
DECL|field|instance
specifier|private
specifier|static
name|SolrQueryTimeoutImpl
name|instance
init|=
operator|new
name|SolrQueryTimeoutImpl
argument_list|()
decl_stmt|;
comment|/** Return singleton instance */
DECL|method|getInstance
specifier|public
specifier|static
name|SolrQueryTimeoutImpl
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
comment|/**    * Get the current value of timeoutAt.    */
DECL|method|get
specifier|public
specifier|static
name|Long
name|get
parameter_list|()
block|{
return|return
name|timeoutAt
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isTimeoutEnabled
specifier|public
name|boolean
name|isTimeoutEnabled
parameter_list|()
block|{
return|return
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
comment|/**    * Return true if a timeoutAt value is set and the current time has exceeded the set timeOut.    */
annotation|@
name|Override
DECL|method|shouldExit
specifier|public
name|boolean
name|shouldExit
parameter_list|()
block|{
name|Long
name|timeoutAt
init|=
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeoutAt
operator|==
literal|null
condition|)
block|{
comment|// timeout unset
return|return
literal|false
return|;
block|}
return|return
name|timeoutAt
operator|-
name|nanoTime
argument_list|()
operator|<
literal|0L
return|;
block|}
comment|/**    * Method to set the time at which the timeOut should happen.    * @param timeAllowed set the time at which this thread should timeout.    */
DECL|method|set
specifier|public
specifier|static
name|void
name|set
parameter_list|(
name|Long
name|timeAllowed
parameter_list|)
block|{
name|long
name|time
init|=
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
decl_stmt|;
name|timeoutAt
operator|.
name|set
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cleanup the ThreadLocal timeout value.    */
DECL|method|reset
specifier|public
specifier|static
name|void
name|reset
parameter_list|()
block|{
name|timeoutAt
operator|.
name|remove
argument_list|()
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
name|get
argument_list|()
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

