begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|index
operator|.
name|IndexDeletionPolicy
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
name|NamedList
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FakeDeletionPolicy
specifier|public
class|class
name|FakeDeletionPolicy
implements|implements
name|IndexDeletionPolicy
implements|,
name|NamedListInitializedPlugin
block|{
DECL|field|var1
specifier|private
name|String
name|var1
decl_stmt|;
DECL|field|var2
specifier|private
name|String
name|var2
decl_stmt|;
comment|//@Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|var1
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"var1"
argument_list|)
expr_stmt|;
name|var2
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"var2"
argument_list|)
expr_stmt|;
block|}
DECL|method|getVar1
specifier|public
name|String
name|getVar1
parameter_list|()
block|{
return|return
name|var1
return|;
block|}
DECL|method|getVar2
specifier|public
name|String
name|getVar2
parameter_list|()
block|{
return|return
name|var2
return|;
block|}
comment|//  @Override
DECL|method|onCommit
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"onCommit"
argument_list|,
literal|"test.org.apache.solr.core.FakeDeletionPolicy.onCommit"
argument_list|)
expr_stmt|;
block|}
comment|//  @Override
DECL|method|onInit
specifier|public
name|void
name|onInit
parameter_list|(
name|List
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"onInit"
argument_list|,
literal|"test.org.apache.solr.core.FakeDeletionPolicy.onInit"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

