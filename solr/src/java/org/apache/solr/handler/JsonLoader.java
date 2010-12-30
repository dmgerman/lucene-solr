begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
name|noggit
operator|.
name|JSONParser
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
name|SolrInputField
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
name|response
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
name|RollbackUpdateCommand
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
name|processor
operator|.
name|UpdateRequestProcessor
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
comment|/**  * @since solr 4.0  */
end_comment

begin_class
DECL|class|JsonLoader
class|class
name|JsonLoader
extends|extends
name|ContentStreamLoader
block|{
DECL|field|log
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JsonLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|processor
specifier|protected
name|UpdateRequestProcessor
name|processor
decl_stmt|;
DECL|method|JsonLoader
specifier|public
name|JsonLoader
parameter_list|(
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ContentStream
name|stream
parameter_list|)
throws|throws
name|Exception
block|{
name|errHeader
operator|=
literal|"JSONLoader: "
operator|+
name|stream
operator|.
name|getSourceInfo
argument_list|()
expr_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|stream
operator|.
name|getReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|String
name|body
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"body"
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|this
operator|.
name|processUpdate
argument_list|(
name|req
argument_list|,
name|processor
argument_list|,
name|parser
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
DECL|method|processUpdate
name|void
name|processUpdate
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|JSONParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ev
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
while|while
condition|(
name|ev
operator|!=
name|JSONParser
operator|.
name|EOF
condition|)
block|{
switch|switch
condition|(
name|ev
condition|)
block|{
case|case
name|JSONParser
operator|.
name|STRING
case|:
if|if
condition|(
name|parser
operator|.
name|wasKey
argument_list|()
condition|)
block|{
name|String
name|v
init|=
name|parser
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|ADD
argument_list|)
condition|)
block|{
name|processor
operator|.
name|processAdd
argument_list|(
name|parseAdd
argument_list|(
name|req
argument_list|,
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|COMMIT
argument_list|)
condition|)
block|{
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|waitFlush
operator|=
name|cmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|parseCommitOptions
argument_list|(
name|parser
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|OPTIMIZE
argument_list|)
condition|)
block|{
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|waitFlush
operator|=
name|cmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|parseCommitOptions
argument_list|(
name|parser
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|DELETE
argument_list|)
condition|)
block|{
name|processor
operator|.
name|processDelete
argument_list|(
name|parseDelete
argument_list|(
name|req
argument_list|,
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|ROLLBACK
argument_list|)
condition|)
block|{
name|processor
operator|.
name|processRollback
argument_list|(
name|parseRollback
argument_list|(
name|req
argument_list|,
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown command: "
operator|+
name|v
operator|+
literal|" ["
operator|+
name|parser
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
break|break;
block|}
comment|// fall through
case|case
name|JSONParser
operator|.
name|LONG
case|:
case|case
name|JSONParser
operator|.
name|NUMBER
case|:
case|case
name|JSONParser
operator|.
name|BIGNUMBER
case|:
case|case
name|JSONParser
operator|.
name|BOOLEAN
case|:
name|log
operator|.
name|info
argument_list|(
literal|"can't have a value here! "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
operator|+
literal|" "
operator|+
name|parser
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
case|case
name|JSONParser
operator|.
name|OBJECT_END
case|:
case|case
name|JSONParser
operator|.
name|ARRAY_START
case|:
case|case
name|JSONParser
operator|.
name|ARRAY_END
case|:
break|break;
default|default:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"UNKNOWN_EVENT_ID:"
operator|+
name|ev
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// read the next event
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|parseDelete
name|DeleteUpdateCommand
name|parseDelete
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|JSONParser
name|js
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNextEvent
argument_list|(
name|js
argument_list|,
name|JSONParser
operator|.
name|OBJECT_START
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|cmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|ev
init|=
name|js
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|STRING
condition|)
block|{
name|String
name|key
init|=
name|js
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|.
name|wasKey
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|id
operator|=
name|js
operator|.
name|getString
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
name|key
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|query
operator|=
name|js
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown key: "
operator|+
name|key
operator|+
literal|" ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid string: "
operator|+
name|key
operator|+
literal|" at ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_END
condition|)
block|{
if|if
condition|(
name|cmd
operator|.
name|id
operator|==
literal|null
operator|&&
name|cmd
operator|.
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Missing id or query for delete ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|cmd
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Got: "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
operator|+
literal|" at ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|parseRollback
name|RollbackUpdateCommand
name|parseRollback
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|JSONParser
name|js
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNextEvent
argument_list|(
name|js
argument_list|,
name|JSONParser
operator|.
name|OBJECT_START
argument_list|)
expr_stmt|;
name|assertNextEvent
argument_list|(
name|js
argument_list|,
name|JSONParser
operator|.
name|OBJECT_END
argument_list|)
expr_stmt|;
return|return
operator|new
name|RollbackUpdateCommand
argument_list|(
name|req
argument_list|)
return|;
block|}
DECL|method|parseCommitOptions
name|void
name|parseCommitOptions
parameter_list|(
name|JSONParser
name|js
parameter_list|,
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNextEvent
argument_list|(
name|js
argument_list|,
name|JSONParser
operator|.
name|OBJECT_START
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|ev
init|=
name|js
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|STRING
condition|)
block|{
name|String
name|key
init|=
name|js
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|.
name|wasKey
argument_list|()
condition|)
block|{
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|WAIT_SEARCHER
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitSearcher
operator|=
name|js
operator|.
name|getBoolean
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|WAIT_FLUSH
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitFlush
operator|=
name|js
operator|.
name|getBoolean
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown key: "
operator|+
name|key
operator|+
literal|" ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid string: "
operator|+
name|key
operator|+
literal|" at ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_END
condition|)
block|{
return|return;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Got: "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
operator|+
literal|" at ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|parseAdd
name|AddUpdateCommand
name|parseAdd
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|JSONParser
name|js
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNextEvent
argument_list|(
name|js
argument_list|,
name|JSONParser
operator|.
name|OBJECT_START
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|ev
init|=
name|js
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|STRING
condition|)
block|{
if|if
condition|(
name|js
operator|.
name|wasKey
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|js
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
if|if
condition|(
name|cmd
operator|.
name|solrDoc
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"multiple docs in same add command"
argument_list|)
throw|;
block|}
name|ev
operator|=
name|assertNextEvent
argument_list|(
name|js
argument_list|,
name|JSONParser
operator|.
name|OBJECT_START
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|parseDoc
argument_list|(
name|ev
argument_list|,
name|js
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|OVERWRITE
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|overwrite
operator|=
name|js
operator|.
name|getBoolean
argument_list|()
expr_stmt|;
comment|// reads next boolean
block|}
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|COMMIT_WITHIN
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|commitWithin
operator|=
operator|(
name|int
operator|)
name|js
operator|.
name|getLong
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|js
operator|.
name|getNumberChars
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown key: "
operator|+
name|key
operator|+
literal|" ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Should be a key "
operator|+
literal|" at ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_END
condition|)
block|{
if|if
condition|(
name|cmd
operator|.
name|solrDoc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"missing solr document. "
operator|+
name|js
operator|.
name|getPosition
argument_list|()
argument_list|)
throw|;
block|}
name|cmd
operator|.
name|solrDoc
operator|.
name|setDocumentBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|cmd
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Got: "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
operator|+
literal|" at ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|assertNextEvent
name|int
name|assertNextEvent
parameter_list|(
name|JSONParser
name|parser
parameter_list|,
name|int
name|ev
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|got
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|!=
name|got
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expected: "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
operator|+
literal|" but got "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|got
argument_list|)
operator|+
literal|" at ["
operator|+
name|parser
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|got
return|;
block|}
DECL|method|parseDoc
name|SolrInputDocument
name|parseDoc
parameter_list|(
name|int
name|ev
parameter_list|,
name|JSONParser
name|js
parameter_list|)
throws|throws
name|IOException
block|{
name|Stack
argument_list|<
name|Object
argument_list|>
name|stack
init|=
operator|new
name|Stack
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Object
name|obj
init|=
literal|null
decl_stmt|;
name|boolean
name|inArray
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|ev
operator|!=
name|JSONParser
operator|.
name|OBJECT_START
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"object should already be started"
argument_list|)
throw|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println( ev + "["+JSONParser.getEventString(ev)+"] "+js.wasKey() ); //+ js.getString() );
switch|switch
condition|(
name|ev
condition|)
block|{
case|case
name|JSONParser
operator|.
name|STRING
case|:
if|if
condition|(
name|js
operator|.
name|wasKey
argument_list|()
condition|)
block|{
name|obj
operator|=
name|stack
operator|.
name|peek
argument_list|()
expr_stmt|;
name|String
name|v
init|=
name|js
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|SolrInputField
condition|)
block|{
name|SolrInputField
name|field
init|=
operator|(
name|SolrInputField
operator|)
name|obj
decl_stmt|;
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
name|ev
operator|=
name|js
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
if|if
condition|(
name|ev
operator|!=
name|JSONParser
operator|.
name|NUMBER
operator|&&
name|ev
operator|!=
name|JSONParser
operator|.
name|LONG
operator|&&
name|ev
operator|!=
name|JSONParser
operator|.
name|BIGNUMBER
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"boost should have number! "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
argument_list|)
throw|;
block|}
name|field
operator|.
name|setBoost
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
name|js
operator|.
name|getNumberChars
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"value"
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
comment|// nothing special...
name|stack
operator|.
name|push
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// so it can be popped
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid key: "
operator|+
name|v
operator|+
literal|" ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|SolrInputDocument
condition|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|(
name|SolrInputDocument
operator|)
name|obj
decl_stmt|;
name|SolrInputField
name|f
init|=
name|doc
operator|.
name|get
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
name|f
operator|=
operator|new
name|SolrInputField
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
name|stack
operator|.
name|push
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"hymmm ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|addValToField
argument_list|(
name|stack
argument_list|,
name|js
operator|.
name|getString
argument_list|()
argument_list|,
name|inArray
argument_list|,
name|js
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|JSONParser
operator|.
name|LONG
case|:
case|case
name|JSONParser
operator|.
name|NUMBER
case|:
case|case
name|JSONParser
operator|.
name|BIGNUMBER
case|:
name|addValToField
argument_list|(
name|stack
argument_list|,
name|js
operator|.
name|getNumberChars
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|inArray
argument_list|,
name|js
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSONParser
operator|.
name|BOOLEAN
case|:
name|addValToField
argument_list|(
name|stack
argument_list|,
name|js
operator|.
name|getBoolean
argument_list|()
argument_list|,
name|inArray
argument_list|,
name|js
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
if|if
condition|(
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|stack
operator|.
name|push
argument_list|(
operator|new
name|SolrInputDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|obj
operator|=
name|stack
operator|.
name|peek
argument_list|()
expr_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|SolrInputField
condition|)
block|{
comment|// should alreay be pushed...
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"should not start new object with: "
operator|+
name|obj
operator|+
literal|" ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
break|break;
case|case
name|JSONParser
operator|.
name|OBJECT_END
case|:
name|obj
operator|=
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|SolrInputDocument
condition|)
block|{
return|return
operator|(
name|SolrInputDocument
operator|)
name|obj
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|SolrInputField
condition|)
block|{
comment|// should already be pushed...
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"should not start new object with: "
operator|+
name|obj
operator|+
literal|" ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_START
case|:
name|inArray
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_END
case|:
name|inArray
operator|=
literal|false
expr_stmt|;
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
comment|// the val should have done it...
break|break;
default|default:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"UNKNOWN_EVENT_ID:"
operator|+
name|ev
argument_list|)
expr_stmt|;
break|break;
block|}
name|ev
operator|=
name|js
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|EOF
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"should finish doc first!"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|addValToField
specifier|static
name|void
name|addValToField
parameter_list|(
name|Stack
name|stack
parameter_list|,
name|Object
name|val
parameter_list|,
name|boolean
name|inArray
parameter_list|,
name|JSONParser
name|js
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|obj
init|=
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|SolrInputField
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"hymmm ["
operator|+
name|js
operator|.
name|getPosition
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|SolrInputField
name|f
init|=
name|inArray
condition|?
operator|(
name|SolrInputField
operator|)
name|obj
else|:
operator|(
name|SolrInputField
operator|)
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|float
name|boost
init|=
operator|(
name|f
operator|.
name|getValue
argument_list|()
operator|==
literal|null
operator|)
condition|?
name|f
operator|.
name|getBoost
argument_list|()
else|:
literal|1.0f
decl_stmt|;
name|f
operator|.
name|addValue
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

