begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|URLUtil
specifier|public
class|class
name|URLUtil
block|{
DECL|field|URL_PREFIX
specifier|public
specifier|final
specifier|static
name|Pattern
name|URL_PREFIX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^([a-z]*?://).*"
argument_list|)
decl_stmt|;
DECL|method|removeScheme
specifier|public
specifier|static
name|String
name|removeScheme
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|URL_PREFIX
operator|.
name|matcher
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|url
operator|.
name|substring
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
name|url
return|;
block|}
DECL|method|hasScheme
specifier|public
specifier|static
name|boolean
name|hasScheme
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|URL_PREFIX
operator|.
name|matcher
argument_list|(
name|url
argument_list|)
decl_stmt|;
return|return
name|matcher
operator|.
name|matches
argument_list|()
return|;
block|}
DECL|method|getScheme
specifier|public
specifier|static
name|String
name|getScheme
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|URL_PREFIX
operator|.
name|matcher
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

