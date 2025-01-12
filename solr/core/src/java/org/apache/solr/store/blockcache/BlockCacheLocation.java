begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
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
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlockCacheLocation
specifier|public
class|class
name|BlockCacheLocation
block|{
DECL|field|block
specifier|private
name|int
name|block
decl_stmt|;
DECL|field|bankId
specifier|private
name|int
name|bankId
decl_stmt|;
DECL|field|lastAccess
specifier|private
name|long
name|lastAccess
decl_stmt|;
DECL|field|accesses
specifier|private
name|long
name|accesses
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|removed
specifier|private
name|AtomicBoolean
name|removed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|BlockCacheLocation
specifier|public
name|BlockCacheLocation
parameter_list|()
block|{
name|touch
argument_list|()
expr_stmt|;
block|}
comment|/** The block within the bank.  This has no relationship to the blockId in BlockCacheKey */
DECL|method|setBlock
specifier|public
name|void
name|setBlock
parameter_list|(
name|int
name|block
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
block|}
DECL|method|setBankId
specifier|public
name|void
name|setBankId
parameter_list|(
name|int
name|bankId
parameter_list|)
block|{
name|this
operator|.
name|bankId
operator|=
name|bankId
expr_stmt|;
block|}
comment|/** The block within the bank.  This has no relationship to the blockId in BlockCacheKey */
DECL|method|getBlock
specifier|public
name|int
name|getBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
DECL|method|getBankId
specifier|public
name|int
name|getBankId
parameter_list|()
block|{
return|return
name|bankId
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis, only used by unused getLastAccess"
argument_list|)
DECL|method|touch
specifier|public
name|void
name|touch
parameter_list|()
block|{
name|lastAccess
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|accesses
operator|++
expr_stmt|;
block|}
DECL|method|getLastAccess
specifier|public
name|long
name|getLastAccess
parameter_list|()
block|{
return|return
name|lastAccess
return|;
block|}
DECL|method|getNumberOfAccesses
specifier|public
name|long
name|getNumberOfAccesses
parameter_list|()
block|{
return|return
name|accesses
return|;
block|}
DECL|method|isRemoved
specifier|public
name|boolean
name|isRemoved
parameter_list|()
block|{
return|return
name|removed
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setRemoved
specifier|public
name|void
name|setRemoved
parameter_list|(
name|boolean
name|removed
parameter_list|)
block|{
name|this
operator|.
name|removed
operator|.
name|set
argument_list|(
name|removed
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

