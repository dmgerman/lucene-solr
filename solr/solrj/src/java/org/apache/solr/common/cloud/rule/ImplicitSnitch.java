begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|rule
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|cloud
operator|.
name|ZkStateReader
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
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|//This is the client-side component of the snitch
end_comment

begin_class
DECL|class|ImplicitSnitch
specifier|public
class|class
name|ImplicitSnitch
extends|extends
name|Snitch
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|hostAndPortPattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|hostAndPortPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?:https?://)?([^:]+):(\\d+)"
argument_list|)
decl_stmt|;
comment|//well known tags
DECL|field|NODE
specifier|public
specifier|static
specifier|final
name|String
name|NODE
init|=
literal|"node"
decl_stmt|;
DECL|field|PORT
specifier|public
specifier|static
specifier|final
name|String
name|PORT
init|=
literal|"port"
decl_stmt|;
DECL|field|HOST
specifier|public
specifier|static
specifier|final
name|String
name|HOST
init|=
literal|"host"
decl_stmt|;
DECL|field|CORES
specifier|public
specifier|static
specifier|final
name|String
name|CORES
init|=
literal|"cores"
decl_stmt|;
DECL|field|DISK
specifier|public
specifier|static
specifier|final
name|String
name|DISK
init|=
literal|"freedisk"
decl_stmt|;
DECL|field|ROLE
specifier|public
specifier|static
specifier|final
name|String
name|ROLE
init|=
literal|"role"
decl_stmt|;
DECL|field|SYSPROP
specifier|public
specifier|static
specifier|final
name|String
name|SYSPROP
init|=
literal|"sysprop."
decl_stmt|;
DECL|field|IP_SNITCHES
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|IP_SNITCHES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"ip_1"
argument_list|,
literal|"ip_2"
argument_list|,
literal|"ip_3"
argument_list|,
literal|"ip_4"
argument_list|)
decl_stmt|;
DECL|field|tags
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|tags
init|=
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|NODE
argument_list|,
name|PORT
argument_list|,
name|HOST
argument_list|,
name|CORES
argument_list|,
name|DISK
argument_list|,
name|ROLE
argument_list|)
operator|.
name|addAll
argument_list|(
name|IP_SNITCHES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getTags
specifier|public
name|void
name|getTags
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedTags
parameter_list|,
name|SnitchContext
name|ctx
parameter_list|)
block|{
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|NODE
argument_list|)
condition|)
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|NODE
argument_list|,
name|solrNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|HOST
argument_list|)
condition|)
block|{
name|Matcher
name|hostAndPortMatcher
init|=
name|hostAndPortPattern
operator|.
name|matcher
argument_list|(
name|solrNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostAndPortMatcher
operator|.
name|find
argument_list|()
condition|)
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|HOST
argument_list|,
name|hostAndPortMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|PORT
argument_list|)
condition|)
block|{
name|Matcher
name|hostAndPortMatcher
init|=
name|hostAndPortPattern
operator|.
name|matcher
argument_list|(
name|solrNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostAndPortMatcher
operator|.
name|find
argument_list|()
condition|)
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|PORT
argument_list|,
name|hostAndPortMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|ROLE
argument_list|)
condition|)
name|fillRole
argument_list|(
name|solrNode
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|addIpTags
argument_list|(
name|solrNode
argument_list|,
name|requestedTags
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|CORES
argument_list|)
condition|)
name|params
operator|.
name|add
argument_list|(
name|CORES
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|DISK
argument_list|)
condition|)
name|params
operator|.
name|add
argument_list|(
name|DISK
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|requestedTags
control|)
block|{
if|if
condition|(
name|tag
operator|.
name|startsWith
argument_list|(
name|SYSPROP
argument_list|)
condition|)
name|params
operator|.
name|add
argument_list|(
name|SYSPROP
argument_list|,
name|tag
operator|.
name|substring
argument_list|(
name|SYSPROP
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|ctx
operator|.
name|invokeRemote
argument_list|(
name|solrNode
argument_list|,
name|params
argument_list|,
literal|"org.apache.solr.cloud.rule.ImplicitSnitch"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|fillRole
specifier|private
name|void
name|fillRole
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|SnitchContext
name|ctx
parameter_list|)
block|{
name|Map
name|roles
init|=
operator|(
name|Map
operator|)
name|ctx
operator|.
name|retrieve
argument_list|(
name|ZkStateReader
operator|.
name|ROLES
argument_list|)
decl_stmt|;
comment|// we don't want to hit the ZK for each node
comment|// so cache and reuse
if|if
condition|(
name|roles
operator|==
literal|null
condition|)
name|roles
operator|=
name|ctx
operator|.
name|getZkJson
argument_list|(
name|ZkStateReader
operator|.
name|ROLES
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|store
argument_list|(
name|ZkStateReader
operator|.
name|ROLES
argument_list|,
name|roles
operator|==
literal|null
condition|?
name|Collections
operator|.
name|emptyMap
argument_list|()
else|:
name|roles
argument_list|)
expr_stmt|;
if|if
condition|(
name|roles
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|roles
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|instanceof
name|List
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|List
operator|)
name|e
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|contains
argument_list|(
name|solrNode
argument_list|)
condition|)
block|{
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|ROLE
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
DECL|field|HOST_FRAG_SEPARATOR_REGEX
specifier|private
specifier|static
specifier|final
name|String
name|HOST_FRAG_SEPARATOR_REGEX
init|=
literal|"\\."
decl_stmt|;
annotation|@
name|Override
DECL|method|isKnownTag
specifier|public
name|boolean
name|isKnownTag
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
return|return
name|tags
operator|.
name|contains
argument_list|(
name|tag
argument_list|)
operator|||
name|tag
operator|.
name|startsWith
argument_list|(
name|SYSPROP
argument_list|)
return|;
block|}
DECL|method|addIpTags
specifier|private
name|void
name|addIpTags
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedTags
parameter_list|,
name|SnitchContext
name|context
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|requestedHostTags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|requestedTags
control|)
block|{
if|if
condition|(
name|IP_SNITCHES
operator|.
name|contains
argument_list|(
name|tag
argument_list|)
condition|)
block|{
name|requestedHostTags
operator|.
name|add
argument_list|(
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|requestedHostTags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
index|[]
name|ipFragments
init|=
name|getIpFragments
argument_list|(
name|solrNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|ipFragments
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|ipSnitchCount
init|=
name|IP_SNITCHES
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ipSnitchCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|currentTagValue
init|=
name|ipFragments
index|[
name|i
index|]
decl_stmt|;
name|String
name|currentTagKey
init|=
name|IP_SNITCHES
operator|.
name|get
argument_list|(
name|ipSnitchCount
operator|-
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|requestedHostTags
operator|.
name|contains
argument_list|(
name|currentTagKey
argument_list|)
condition|)
block|{
name|context
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|currentTagKey
argument_list|,
name|currentTagValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getIpFragments
specifier|private
name|String
index|[]
name|getIpFragments
parameter_list|(
name|String
name|solrNode
parameter_list|)
block|{
name|Matcher
name|hostAndPortMatcher
init|=
name|hostAndPortPattern
operator|.
name|matcher
argument_list|(
name|solrNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostAndPortMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|host
init|=
name|hostAndPortMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|!=
literal|null
condition|)
block|{
name|String
name|ip
init|=
name|getHostIp
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|ip
operator|!=
literal|null
condition|)
block|{
return|return
name|ip
operator|.
name|split
argument_list|(
name|HOST_FRAG_SEPARATOR_REGEX
argument_list|)
return|;
comment|//IPv6 support will be provided by SOLR-8523
block|}
block|}
block|}
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to match host IP address from node URL [{}] using regex [{}]"
argument_list|,
name|solrNode
argument_list|,
name|hostAndPortPattern
operator|.
name|pattern
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|getHostIp
specifier|public
name|String
name|getHostIp
parameter_list|(
name|String
name|host
parameter_list|)
block|{
try|try
block|{
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|host
argument_list|)
decl_stmt|;
return|return
name|address
operator|.
name|getHostAddress
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to get IP address from host [{}], with exception [{}] "
argument_list|,
name|host
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

