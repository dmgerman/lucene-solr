begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.messages
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|messages
package|;
end_package

begin_class
DECL|class|MessagesTestBundle
specifier|public
class|class
name|MessagesTestBundle
extends|extends
name|NLS
block|{
DECL|field|BUNDLE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|BUNDLE_NAME
init|=
name|MessagesTestBundle
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|method|MessagesTestBundle
specifier|private
name|MessagesTestBundle
parameter_list|()
block|{
comment|// should never be instantiated
block|}
static|static
block|{
comment|// register all string ids with NLS class and initialize static string
comment|// values
name|NLS
operator|.
name|initializeMessages
argument_list|(
name|BUNDLE_NAME
argument_list|,
name|MessagesTestBundle
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// static string must match the strings in the property files.
DECL|field|Q0001E_INVALID_SYNTAX
specifier|public
specifier|static
name|String
name|Q0001E_INVALID_SYNTAX
decl_stmt|;
DECL|field|Q0004E_INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION
specifier|public
specifier|static
name|String
name|Q0004E_INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION
decl_stmt|;
comment|// this message is missing from the properties file
DECL|field|Q0005E_MESSAGE_NOT_IN_BUNDLE
specifier|public
specifier|static
name|String
name|Q0005E_MESSAGE_NOT_IN_BUNDLE
decl_stmt|;
block|}
end_class

end_unit

