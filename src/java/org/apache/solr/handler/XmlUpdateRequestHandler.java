begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

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
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|javanet
operator|.
name|staxutils
operator|.
name|BaseXMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|FactoryConfigurationError
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|SolrException
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
name|SolrInputDocument
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
name|MapSolrParams
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
name|SolrParams
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
name|ContentStream
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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|XML
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
name|core
operator|.
name|Config
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequestBase
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
name|request
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|AddUpdateCommand
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
name|update
operator|.
name|CommitUpdateCommand
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
name|update
operator|.
name|DeleteUpdateCommand
import|;
end_import

begin_comment
comment|/**  * Add documents to solr using the STAX XML parser.  *   * To change the UpdateRequestProcessor implementation, add the configuration parameter:  *   *<requestHandler name="/update" class="solr.StaxUpdateRequestHandler">  *<str name="update.processor.class">org.apache.solr.handler.UpdateRequestProcessor</str>  *<lst name="update.processor.args">  *     ... (optionally pass in arguments to the factory init method) ...  *</lst>   *</requestHandler>  */
end_comment

begin_class
DECL|class|XmlUpdateRequestHandler
specifier|public
class|class
name|XmlUpdateRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|UPDATE_PROCESSOR_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_PROCESSOR_FACTORY
init|=
literal|"update.processor.factory"
decl_stmt|;
DECL|field|UPDATE_PROCESSOR_ARGS
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_PROCESSOR_ARGS
init|=
literal|"update.processor.args"
decl_stmt|;
comment|// XML Constants
DECL|field|ADD
specifier|public
specifier|static
specifier|final
name|String
name|ADD
init|=
literal|"add"
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|String
name|DELETE
init|=
literal|"delete"
decl_stmt|;
DECL|field|OPTIMIZE
specifier|public
specifier|static
specifier|final
name|String
name|OPTIMIZE
init|=
literal|"optimize"
decl_stmt|;
DECL|field|COMMIT
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT
init|=
literal|"commit"
decl_stmt|;
DECL|field|WAIT_SEARCHER
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_SEARCHER
init|=
literal|"waitSearcher"
decl_stmt|;
DECL|field|WAIT_FLUSH
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_FLUSH
init|=
literal|"waitFlush"
decl_stmt|;
DECL|field|MODE
specifier|public
specifier|static
specifier|final
name|String
name|MODE
init|=
literal|"mode"
decl_stmt|;
DECL|field|OVERWRITE
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE
init|=
literal|"overwrite"
decl_stmt|;
DECL|field|OVERWRITE_COMMITTED
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE_COMMITTED
init|=
literal|"overwriteCommitted"
decl_stmt|;
comment|// @Deprecated
DECL|field|OVERWRITE_PENDING
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE_PENDING
init|=
literal|"overwritePending"
decl_stmt|;
comment|// @Deprecated
DECL|field|ALLOW_DUPS
specifier|public
specifier|static
specifier|final
name|String
name|ALLOW_DUPS
init|=
literal|"allowDups"
decl_stmt|;
DECL|field|inputFactory
specifier|private
name|XMLInputFactory
name|inputFactory
decl_stmt|;
DECL|field|processorFactory
specifier|private
name|UpdateRequestProcessorFactory
name|processorFactory
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inputFactory
operator|=
name|BaseXMLInputFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
comment|// Initialize the UpdateRequestProcessorFactory
name|NamedList
argument_list|<
name|Object
argument_list|>
name|factoryargs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|String
name|className
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
name|UPDATE_PROCESSOR_FACTORY
argument_list|)
decl_stmt|;
name|factoryargs
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|args
operator|.
name|get
argument_list|(
name|UPDATE_PROCESSOR_ARGS
argument_list|)
expr_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
block|{
name|processorFactory
operator|=
operator|(
name|UpdateRequestProcessorFactory
operator|)
name|Config
operator|.
name|newInstance
argument_list|(
name|className
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|processorFactory
operator|==
literal|null
condition|)
block|{
name|processorFactory
operator|=
operator|new
name|UpdateRequestProcessorFactory
argument_list|()
expr_stmt|;
block|}
name|processorFactory
operator|.
name|init
argument_list|(
name|factoryargs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|RequestHandlerUtils
operator|.
name|handleCommit
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"missing content stream"
argument_list|)
throw|;
block|}
return|return;
block|}
name|RequestHandlerUtils
operator|.
name|addExperimentalFormatWarning
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
comment|// Cycle through each stream
for|for
control|(
name|ContentStream
name|stream
range|:
name|req
operator|.
name|getContentStreams
argument_list|()
control|)
block|{
name|Reader
name|reader
init|=
name|stream
operator|.
name|getReader
argument_list|()
decl_stmt|;
try|try
block|{
name|NamedList
name|out
init|=
name|this
operator|.
name|update
argument_list|(
name|req
argument_list|,
name|req
operator|.
name|getCore
argument_list|()
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"update"
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
comment|// perhaps commit when we are done
name|RequestHandlerUtils
operator|.
name|handleCommit
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * @since solr 1.2    */
DECL|method|processUpdate
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processUpdate
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|IOException
throws|,
name|FactoryConfigurationError
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
throws|,
name|TransformerConfigurationException
block|{
name|UpdateRequestProcessor
name|processor
init|=
name|processorFactory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|addCmd
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
case|:
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|processor
operator|.
name|getResponse
argument_list|()
return|;
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|String
name|currTag
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|currTag
operator|.
name|equals
argument_list|(
name|ADD
argument_list|)
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"SolrCore.update(add)"
argument_list|)
expr_stmt|;
name|addCmd
operator|=
operator|new
name|AddUpdateCommand
argument_list|()
expr_stmt|;
name|boolean
name|overwrite
init|=
literal|true
decl_stmt|;
comment|// the default
name|Boolean
name|overwritePending
init|=
literal|null
decl_stmt|;
name|Boolean
name|overwriteCommitted
init|=
literal|null
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|OVERWRITE
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|overwrite
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
comment|//              } else if (MODE.equals(attrName)) {
comment|//                addCmd.mode = SolrPluginUtils.parseAndValidateFieldModes(attrVal,schema);
block|}
elseif|else
if|if
condition|(
name|ALLOW_DUPS
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|overwrite
operator|=
operator|!
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|OVERWRITE_PENDING
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|overwritePending
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|OVERWRITE_COMMITTED
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|overwriteCommitted
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"Unknown attribute id in add:"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check if these flags are set
if|if
condition|(
name|overwritePending
operator|!=
literal|null
operator|&&
name|overwriteCommitted
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|overwritePending
operator|!=
name|overwriteCommitted
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"can't have different values for 'overwritePending' and 'overwriteCommitted'"
argument_list|)
throw|;
block|}
name|overwrite
operator|=
name|overwritePending
expr_stmt|;
block|}
name|addCmd
operator|.
name|overwriteCommitted
operator|=
name|overwrite
expr_stmt|;
name|addCmd
operator|.
name|overwritePending
operator|=
name|overwrite
expr_stmt|;
name|addCmd
operator|.
name|allowDups
operator|=
operator|!
name|overwrite
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"adding doc..."
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
name|readDoc
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|addCmd
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|COMMIT
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
operator|||
name|OPTIMIZE
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"parsing "
operator|+
name|currTag
argument_list|)
expr_stmt|;
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|OPTIMIZE
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|sawWaitSearcher
init|=
literal|false
decl_stmt|,
name|sawWaitFlush
init|=
literal|false
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|WAIT_FLUSH
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitFlush
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|sawWaitFlush
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|WAIT_SEARCHER
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitSearcher
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|sawWaitSearcher
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected attribute commit/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If waitFlush is specified and waitSearcher wasn't, then
comment|// clear waitSearcher.
if|if
condition|(
name|sawWaitFlush
operator|&&
operator|!
name|sawWaitSearcher
condition|)
block|{
name|cmd
operator|.
name|waitSearcher
operator|=
literal|false
expr_stmt|;
block|}
name|processor
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|// end commit
elseif|else
if|if
condition|(
name|DELETE
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"parsing delete"
argument_list|)
expr_stmt|;
name|processDelete
argument_list|(
name|processor
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
comment|// end delete
break|break;
block|}
block|}
block|}
comment|/**    * @since solr 1.3    */
DECL|method|processDelete
name|void
name|processDelete
parameter_list|(
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|IOException
block|{
comment|// Parse the command
name|DeleteUpdateCommand
name|deleteCmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|()
decl_stmt|;
name|deleteCmd
operator|.
name|fromPending
operator|=
literal|true
expr_stmt|;
name|deleteCmd
operator|.
name|fromCommitted
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"fromPending"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|fromPending
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"fromCommitted"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|fromCommitted
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected attribute delete/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|String
name|mode
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
literal|"id"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
operator|||
literal|"query"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
operator|)
condition|)
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected XML tag /delete/"
operator|+
name|mode
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unexpected XML tag /delete/"
operator|+
name|mode
argument_list|)
throw|;
block|}
name|text
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
name|String
name|currTag
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|id
operator|=
name|text
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|query
operator|=
name|text
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"delete"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
return|return;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected XML tag /delete/"
operator|+
name|currTag
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unexpected XML tag /delete/"
operator|+
name|currTag
argument_list|)
throw|;
block|}
name|processor
operator|.
name|processDelete
argument_list|(
name|deleteCmd
argument_list|)
expr_stmt|;
break|break;
comment|// Add everything to the text
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|text
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**    * Given the input stream, read a document    *     * @since solr 1.3    */
DECL|method|readDoc
name|SolrInputDocument
name|readDoc
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|attrName
init|=
literal|""
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrName
operator|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|doc
operator|.
name|setDocumentBoost
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"Unknown attribute doc/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|boolean
name|isNull
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
comment|// Add everything to the text
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|text
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
elseif|else
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isNull
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|boost
operator|=
literal|1.0f
expr_stmt|;
block|}
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|text
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|localName
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"field"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"unexpected XML tag doc/"
operator|+
name|localName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unexpected XML tag doc/"
operator|+
name|localName
argument_list|)
throw|;
block|}
name|boost
operator|=
literal|1.0f
expr_stmt|;
name|String
name|attrVal
init|=
literal|""
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrName
operator|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrVal
operator|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|name
operator|=
name|attrVal
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"null"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|isNull
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"Unknown attribute doc/field/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
block|}
comment|/**    * @since solr 1.2    */
DECL|method|update
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|update
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|Exception
block|{
name|XMLStreamReader
name|parser
init|=
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
name|processUpdate
argument_list|(
name|req
argument_list|,
name|core
argument_list|,
name|parser
argument_list|)
return|;
block|}
comment|/**    * A Convenience method for getting back a simple XML string indicating    * success or failure from an XML formated Update (from the Reader)    *     * @since solr 1.2    */
annotation|@
name|Deprecated
DECL|method|doLegacyUpdate
specifier|public
name|void
name|doLegacyUpdate
parameter_list|(
name|Reader
name|input
parameter_list|,
name|Writer
name|output
parameter_list|)
block|{
try|try
block|{
name|SolrCore
name|core
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
decl_stmt|;
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{}
decl_stmt|;
name|this
operator|.
name|update
argument_list|(
name|req
argument_list|,
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
literal|"<result status=\"0\"></result>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
try|try
block|{
name|XML
operator|.
name|writeXML
argument_list|(
name|output
argument_list|,
literal|"result"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|ex
argument_list|)
argument_list|,
literal|"status"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ee
parameter_list|)
block|{
name|log
operator|.
name|severe
argument_list|(
literal|"Error writing to output stream: "
operator|+
name|ee
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Add documents with XML"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit

