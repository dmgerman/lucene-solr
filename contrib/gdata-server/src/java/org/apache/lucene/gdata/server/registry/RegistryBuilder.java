begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.registry
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
package|;
end_package

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
name|ExtensionProfile
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
name|Feed
import|;
end_import

begin_comment
comment|/**   * @author Simon Willnauer   *   */
end_comment

begin_class
DECL|class|RegistryBuilder
specifier|public
class|class
name|RegistryBuilder
block|{
comment|/**       *        */
DECL|method|buildRegistry
specifier|public
specifier|static
name|void
name|buildRegistry
parameter_list|()
block|{
comment|// TODO Implement this!! -- just for develping purposes
name|GDataServerRegistry
name|reg
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
name|FeedInstanceConfigurator
name|configurator
init|=
operator|new
name|FeedInstanceConfigurator
argument_list|()
decl_stmt|;
name|configurator
operator|.
name|setFeedType
argument_list|(
name|Feed
operator|.
name|class
argument_list|)
expr_stmt|;
name|configurator
operator|.
name|setFeedId
argument_list|(
literal|"weblog"
argument_list|)
expr_stmt|;
name|configurator
operator|.
name|setExtensionProfileClass
argument_list|(
name|ExtensionProfile
operator|.
name|class
argument_list|)
expr_stmt|;
name|reg
operator|.
name|registerFeed
argument_list|(
name|configurator
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

