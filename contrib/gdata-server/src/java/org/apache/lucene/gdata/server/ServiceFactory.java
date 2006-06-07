begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server
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
package|;
end_package

begin_comment
comment|/**   * The {@link ServiceFactory} creates {@link Service} implementations to access   * the GData - Server components.   *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|ServiceFactory
specifier|public
class|class
name|ServiceFactory
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
name|ServiceFactory
name|INSTANCE
init|=
literal|null
decl_stmt|;
comment|/**       * @return - a Singleton Instance of the factory       */
DECL|method|getInstance
specifier|public
specifier|static
specifier|synchronized
name|ServiceFactory
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|INSTANCE
operator|==
literal|null
condition|)
name|INSTANCE
operator|=
operator|new
name|ServiceFactory
argument_list|()
expr_stmt|;
return|return
name|INSTANCE
return|;
block|}
DECL|method|ServiceFactory
specifier|private
name|ServiceFactory
parameter_list|()
block|{
comment|// private constructor --> singleton
block|}
comment|/**       * Creates a {@link Service} implementation.       *        * @return a Service Implementation       */
DECL|method|getService
specifier|public
name|Service
name|getService
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|GDataService
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

