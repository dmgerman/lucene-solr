begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Represents the state of an asynchronous request.  *   * @see org.apache.solr.client.solrj.request.CollectionAdminRequest.RequestStatus  */
end_comment

begin_enum
DECL|enum|RequestStatusState
specifier|public
enum|enum
name|RequestStatusState
block|{
comment|/** The request was completed. */
DECL|enum constant|COMPLETED
name|COMPLETED
argument_list|(
literal|"completed"
argument_list|)
block|,
comment|/** The request has failed. */
DECL|enum constant|FAILED
name|FAILED
argument_list|(
literal|"failed"
argument_list|)
block|,
comment|/** The request is in progress. */
DECL|enum constant|RUNNING
name|RUNNING
argument_list|(
literal|"running"
argument_list|)
block|,
comment|/** The request was submitted, but has not yet started. */
DECL|enum constant|SUBMITTED
name|SUBMITTED
argument_list|(
literal|"submitted"
argument_list|)
block|,
comment|/** The request Id was not found. */
DECL|enum constant|NOT_FOUND
name|NOT_FOUND
argument_list|(
literal|"notfound"
argument_list|)
block|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|method|RequestStatusState
specifier|private
name|RequestStatusState
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
comment|/**    * Returns the string representation of this state, for using as a key. For backward compatibility, it returns the    * lowercase form of the state's name.    */
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/**    * Resolves a key that was returned from {@link #getKey()} to a {@link RequestStatusState}. For backwards    * compatibility, it resolves the key "notfound" to {@link #NOT_FOUND}.    */
DECL|method|fromKey
specifier|public
specifier|static
name|RequestStatusState
name|fromKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
try|try
block|{
return|return
name|RequestStatusState
operator|.
name|valueOf
argument_list|(
name|key
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|equalsIgnoreCase
argument_list|(
name|RequestStatusState
operator|.
name|NOT_FOUND
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|RequestStatusState
operator|.
name|NOT_FOUND
return|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
end_enum

end_unit

