begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|AtomicInteger
import|;
end_import

begin_comment
comment|/** Keep track of a reference count on a resource and close it when  * the count hits zero.  *  * By itself, this class could have some race conditions  * since there is no synchronization between the refcount  * check and the close.  Solr's use in reference counting searchers  * is safe since the count can only hit zero if it's unregistered (and  * hence incref() will not be called again on it).  *  *  */
end_comment

begin_class
DECL|class|RefCounted
specifier|public
specifier|abstract
class|class
name|RefCounted
parameter_list|<
name|Type
parameter_list|>
block|{
DECL|field|resource
specifier|protected
specifier|final
name|Type
name|resource
decl_stmt|;
DECL|field|refcount
specifier|protected
specifier|final
name|AtomicInteger
name|refcount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|RefCounted
specifier|public
name|RefCounted
parameter_list|(
name|Type
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
DECL|method|getRefcount
specifier|public
name|int
name|getRefcount
parameter_list|()
block|{
return|return
name|refcount
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|incref
specifier|public
specifier|final
name|RefCounted
argument_list|<
name|Type
argument_list|>
name|incref
parameter_list|()
block|{
name|refcount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|get
specifier|public
specifier|final
name|Type
name|get
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
DECL|method|decref
specifier|public
name|void
name|decref
parameter_list|()
block|{
if|if
condition|(
name|refcount
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|protected
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_class

end_unit

