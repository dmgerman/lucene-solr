begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Fieldable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|HitCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|java
operator|.
name|util
operator|.
name|Vector
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|schema
operator|.
name|FieldType
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
name|util
operator|.
name|DOMUtil
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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_comment
comment|/**  *<code>UpdateHandler</code> handles requests to change the index  * (adds, deletes, commits, optimizes, etc).  *  * @version $Id$  * @since solr 0.9  */
end_comment

begin_class
DECL|class|UpdateHandler
specifier|public
specifier|abstract
class|class
name|UpdateHandler
implements|implements
name|SolrInfoMBean
block|{
DECL|field|log
specifier|protected
specifier|final
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|UpdateHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|core
specifier|protected
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|idField
specifier|protected
specifier|final
name|SchemaField
name|idField
decl_stmt|;
DECL|field|idFieldType
specifier|protected
specifier|final
name|FieldType
name|idFieldType
decl_stmt|;
DECL|field|commitCallbacks
specifier|protected
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
name|commitCallbacks
init|=
operator|new
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|optimizeCallbacks
specifier|protected
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
name|optimizeCallbacks
init|=
operator|new
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|parseEventListeners
specifier|private
name|void
name|parseEventListeners
parameter_list|()
block|{
specifier|final
name|SolrConfig
name|solrConfig
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
decl_stmt|;
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|solrConfig
operator|.
name|evaluate
argument_list|(
literal|"updateHandler/listener[@event=\"postCommit\"]"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|className
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
name|SolrEventListener
name|listener
init|=
name|core
operator|.
name|createEventListener
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|listener
operator|.
name|init
argument_list|(
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
comment|// listener.init(DOMUtil.toMapExcept(node.getAttributes(),"class","synchronized"));
name|commitCallbacks
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"added SolrEventListener for postCommit: "
operator|+
name|listener
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"error parsing event listevers"
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
block|}
name|nodes
operator|=
operator|(
name|NodeList
operator|)
name|solrConfig
operator|.
name|evaluate
argument_list|(
literal|"updateHandler/listener[@event=\"postOptimize\"]"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|className
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
name|SolrEventListener
name|listener
init|=
name|core
operator|.
name|createEventListener
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|listener
operator|.
name|init
argument_list|(
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|optimizeCallbacks
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"added SolarEventListener for postOptimize: "
operator|+
name|listener
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"error parsing event listeners"
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|callPostCommitCallbacks
specifier|protected
name|void
name|callPostCommitCallbacks
parameter_list|()
block|{
for|for
control|(
name|SolrEventListener
name|listener
range|:
name|commitCallbacks
control|)
block|{
name|listener
operator|.
name|postCommit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|callPostOptimizeCallbacks
specifier|protected
name|void
name|callPostOptimizeCallbacks
parameter_list|()
block|{
for|for
control|(
name|SolrEventListener
name|listener
range|:
name|optimizeCallbacks
control|)
block|{
name|listener
operator|.
name|postCommit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|UpdateHandler
specifier|public
name|UpdateHandler
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|schema
operator|=
name|core
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|idField
operator|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
name|idFieldType
operator|=
name|idField
operator|!=
literal|null
condition|?
name|idField
operator|.
name|getType
argument_list|()
else|:
literal|null
expr_stmt|;
name|parseEventListeners
argument_list|()
expr_stmt|;
name|core
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|put
argument_list|(
literal|"updateHandler"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|createMainIndexWriter
specifier|protected
name|SolrIndexWriter
name|createMainIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|removeAllExisting
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrIndexWriter
name|writer
init|=
operator|new
name|SolrIndexWriter
argument_list|(
name|name
argument_list|,
name|core
operator|.
name|getIndexDir
argument_list|()
argument_list|,
name|removeAllExisting
argument_list|,
name|schema
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|mainIndexConfig
argument_list|)
decl_stmt|;
return|return
name|writer
return|;
block|}
DECL|method|idTerm
specifier|protected
specifier|final
name|Term
name|idTerm
parameter_list|(
name|String
name|readableId
parameter_list|)
block|{
comment|// to correctly create the Term, the string needs to be run
comment|// through the Analyzer for that field.
return|return
operator|new
name|Term
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|,
name|idFieldType
operator|.
name|toInternal
argument_list|(
name|readableId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getIndexedId
specifier|protected
specifier|final
name|String
name|getIndexedId
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
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
literal|"Operation requires schema to have a unique key field"
argument_list|)
throw|;
comment|// Right now, single valued fields that require value transformation from external to internal (indexed)
comment|// form have that transformation already performed and stored as the field value.
name|Fieldable
index|[]
name|id
init|=
name|doc
operator|.
name|getFieldables
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|id
operator|.
name|length
operator|<
literal|1
condition|)
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
literal|"Document is missing uniqueKey field "
operator|+
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|id
operator|.
name|length
operator|>
literal|1
condition|)
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
literal|"Document specifies multiple unique ids! "
operator|+
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
return|return
name|idFieldType
operator|.
name|storedToIndexed
argument_list|(
name|id
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|getIndexedIdOptional
specifier|protected
specifier|final
name|String
name|getIndexedIdOptional
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Field
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|idFieldType
operator|.
name|storedToIndexed
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|addDoc
specifier|public
specifier|abstract
name|int
name|addDoc
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|delete
specifier|public
specifier|abstract
name|void
name|delete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|deleteByQuery
specifier|public
specifier|abstract
name|void
name|deleteByQuery
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|commit
specifier|public
specifier|abstract
name|void
name|commit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|class|DeleteHitCollector
class|class
name|DeleteHitCollector
extends|extends
name|HitCollector
block|{
DECL|field|deleted
specifier|public
name|int
name|deleted
init|=
literal|0
decl_stmt|;
DECL|field|searcher
specifier|public
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|method|DeleteHitCollector
specifier|public
name|DeleteHitCollector
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
try|try
block|{
name|searcher
operator|.
name|getReader
argument_list|()
operator|.
name|deleteDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|deleted
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// don't try to close the searcher on failure for now...
comment|// try { closeSearcher(); } catch (Exception ee) { SolrException.log(log,ee); }
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error deleting doc# "
operator|+
name|doc
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

