begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_comment
comment|/**  *  *  */
end_comment

begin_class
DECL|class|MultiThreadEntryStub
specifier|public
class|class
name|MultiThreadEntryStub
extends|extends
name|ServerBaseEntry
block|{
DECL|field|getEntryVisitor
specifier|private
name|Visitor
name|getEntryVisitor
decl_stmt|;
DECL|field|getVersionVisitor
specifier|private
name|Visitor
name|getVersionVisitor
decl_stmt|;
comment|/**      *       */
DECL|method|MultiThreadEntryStub
specifier|public
name|MultiThreadEntryStub
parameter_list|()
block|{                    }
comment|/**      * @param arg0      */
DECL|method|MultiThreadEntryStub
specifier|public
name|MultiThreadEntryStub
parameter_list|(
name|BaseEntry
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
DECL|method|acceptGetEntryVisitor
specifier|public
name|void
name|acceptGetEntryVisitor
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|this
operator|.
name|getEntryVisitor
operator|=
name|visitor
expr_stmt|;
block|}
DECL|method|acceptGetVersionVisitor
specifier|public
name|void
name|acceptGetVersionVisitor
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|this
operator|.
name|getVersionVisitor
operator|=
name|visitor
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.data.ServerBaseEntry#getEntry()      */
annotation|@
name|Override
DECL|method|getEntry
specifier|public
name|BaseEntry
name|getEntry
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|getEntryVisitor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|getEntryVisitor
operator|.
name|execute
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getEntry
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.data.ServerBaseEntry#getVersion()      */
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|getVersionVisitor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|getVersionVisitor
operator|.
name|execute
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getVersion
argument_list|()
return|;
block|}
block|}
end_class

end_unit

