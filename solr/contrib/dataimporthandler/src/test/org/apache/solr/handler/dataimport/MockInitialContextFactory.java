begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|spi
operator|.
name|InitialContextFactory
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_class
DECL|class|MockInitialContextFactory
specifier|public
class|class
name|MockInitialContextFactory
implements|implements
name|InitialContextFactory
block|{
DECL|field|objects
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|objects
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|javax
operator|.
name|naming
operator|.
name|Context
name|context
decl_stmt|;
DECL|method|MockInitialContextFactory
specifier|public
name|MockInitialContextFactory
parameter_list|()
block|{
name|context
operator|=
name|mock
argument_list|(
name|javax
operator|.
name|naming
operator|.
name|Context
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|when
argument_list|(
name|context
operator|.
name|lookup
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocation
lambda|->
name|objects
operator|.
name|get
argument_list|(
name|invocation
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getInitialContext
specifier|public
name|javax
operator|.
name|naming
operator|.
name|Context
name|getInitialContext
parameter_list|(
name|Hashtable
name|env
parameter_list|)
block|{
return|return
name|context
return|;
block|}
DECL|method|bind
specifier|public
specifier|static
name|void
name|bind
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|objects
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

