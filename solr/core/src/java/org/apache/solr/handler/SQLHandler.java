begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|Lex
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|StreamComparator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|ExceptionStream
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|JDBCStream
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamContext
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|TupleStream
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExplanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|params
operator|.
name|ModifiableSolrParams
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
name|CoreContainer
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
name|handler
operator|.
name|sql
operator|.
name|CalciteSolrDriver
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
name|security
operator|.
name|AuthorizationContext
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
name|security
operator|.
name|PermissionNameProvider
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
name|SolrCoreAware
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

begin_class
DECL|class|SQLHandler
specifier|public
class|class
name|SQLHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
implements|,
name|PermissionNameProvider
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
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
DECL|field|defaultZkhost
specifier|private
specifier|static
name|String
name|defaultZkhost
init|=
literal|null
decl_stmt|;
DECL|field|defaultWorkerCollection
specifier|private
specifier|static
name|String
name|defaultWorkerCollection
init|=
literal|null
decl_stmt|;
DECL|field|sqlNonCloudErrorMsg
specifier|static
specifier|final
name|String
name|sqlNonCloudErrorMsg
init|=
literal|"/sql handler only works in Solr Cloud mode"
decl_stmt|;
DECL|field|isCloud
specifier|private
name|boolean
name|isCloud
init|=
literal|false
decl_stmt|;
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|CoreContainer
name|coreContainer
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|defaultZkhost
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkServerAddress
argument_list|()
expr_stmt|;
name|defaultWorkerCollection
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
expr_stmt|;
name|isCloud
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPermissionName
specifier|public
name|PermissionNameProvider
operator|.
name|Name
name|getPermissionName
parameter_list|(
name|AuthorizationContext
name|request
parameter_list|)
block|{
return|return
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|READ_PERM
return|;
block|}
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
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
name|params
operator|.
name|get
argument_list|(
literal|"stmt"
argument_list|)
decl_stmt|;
comment|// Set defaults for parameters
name|params
operator|.
name|set
argument_list|(
literal|"numWorkers"
argument_list|,
name|params
operator|.
name|getInt
argument_list|(
literal|"numWorkers"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"workerCollection"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"workerCollection"
argument_list|,
name|defaultWorkerCollection
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"workerZkhost"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"workerZkhost"
argument_list|,
name|defaultZkhost
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"aggregationMode"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"aggregationMode"
argument_list|,
literal|"map_reduce"
argument_list|)
argument_list|)
expr_stmt|;
name|TupleStream
name|tupleStream
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|isCloud
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|sqlNonCloudErrorMsg
argument_list|)
throw|;
block|}
if|if
condition|(
name|sql
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"stmt parameter cannot be null"
argument_list|)
throw|;
block|}
name|String
name|url
init|=
name|CalciteSolrDriver
operator|.
name|CONNECT_STRING_PREFIX
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|// Add all query parameters
name|Iterator
argument_list|<
name|String
argument_list|>
name|parameterNamesIterator
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|parameterNamesIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|param
init|=
name|parameterNamesIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|param
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Set these last to ensure that they are set properly
name|properties
operator|.
name|setProperty
argument_list|(
literal|"lex"
argument_list|,
name|Lex
operator|.
name|MYSQL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"zk"
argument_list|,
name|defaultZkhost
argument_list|)
expr_stmt|;
name|String
name|driverClass
init|=
name|CalciteSolrDriver
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|// JDBC driver requires metadata from the SQLHandler. Default to false since this adds a new Metadata stream.
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
literal|"includeMetadata"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|/*          * Would be great to replace this with the JDBCStream. Can't do that currently since need to have metadata          * added to the stream for the JDBC driver. This could be fixed by using the Calcite Avatica server and client.          */
name|tupleStream
operator|=
operator|new
name|SqlHandlerStream
argument_list|(
name|url
argument_list|,
name|sql
argument_list|,
name|properties
argument_list|,
name|driverClass
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tupleStream
operator|=
operator|new
name|JDBCStream
argument_list|(
name|url
argument_list|,
name|sql
argument_list|,
literal|null
argument_list|,
name|properties
argument_list|,
name|driverClass
argument_list|)
expr_stmt|;
block|}
name|tupleStream
operator|=
operator|new
name|StreamHandler
operator|.
name|TimerStream
argument_list|(
operator|new
name|ExceptionStream
argument_list|(
name|tupleStream
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
name|tupleStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//Catch the SQL parsing and query transformation exceptions.
if|if
condition|(
name|tupleStream
operator|!=
literal|null
condition|)
block|{
name|tupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|SolrException
operator|.
name|log
argument_list|(
name|logger
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|StreamHandler
operator|.
name|DummyErrorStream
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"SQLHandler"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/*    * Only necessary for SolrJ JDBC driver since metadata has to be passed back    */
DECL|class|SqlHandlerStream
specifier|private
class|class
name|SqlHandlerStream
extends|extends
name|TupleStream
block|{
DECL|field|url
specifier|private
specifier|final
name|String
name|url
decl_stmt|;
DECL|field|sql
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
DECL|field|properties
specifier|private
specifier|final
name|Properties
name|properties
decl_stmt|;
DECL|field|driverClass
specifier|private
specifier|final
name|String
name|driverClass
decl_stmt|;
DECL|field|firstTuple
specifier|private
name|boolean
name|firstTuple
init|=
literal|true
decl_stmt|;
DECL|field|connection
specifier|private
name|Connection
name|connection
decl_stmt|;
DECL|field|statement
specifier|private
name|Statement
name|statement
decl_stmt|;
DECL|field|resultSet
specifier|private
name|ResultSet
name|resultSet
decl_stmt|;
DECL|field|resultSetMetaData
specifier|private
name|ResultSetMetaData
name|resultSetMetaData
decl_stmt|;
DECL|field|numColumns
specifier|private
name|int
name|numColumns
decl_stmt|;
DECL|method|SqlHandlerStream
name|SqlHandlerStream
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|sql
parameter_list|,
name|Properties
name|properties
parameter_list|,
name|String
name|driverClass
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|this
operator|.
name|driverClass
operator|=
name|driverClass
expr_stmt|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|driverClass
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|connection
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|statement
operator|=
name|connection
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|resultSetMetaData
operator|=
name|this
operator|.
name|resultSet
operator|.
name|getMetaData
argument_list|()
expr_stmt|;
name|numColumns
operator|=
name|resultSetMetaData
operator|.
name|getColumnCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"SQL Handler"
argument_list|)
operator|.
name|withExpression
argument_list|(
literal|"--non-expressible--"
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|Explanation
operator|.
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
return|;
block|}
comment|// Return a metadata tuple as the first tuple and then pass through to the underlying stream.
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstTuple
condition|)
block|{
name|firstTuple
operator|=
literal|false
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|metadataFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadataAliases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numColumns
condition|;
name|i
operator|++
control|)
block|{
name|String
name|columnName
init|=
name|resultSetMetaData
operator|.
name|getColumnName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|columnLabel
init|=
name|resultSetMetaData
operator|.
name|getColumnLabel
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|metadataFields
operator|.
name|add
argument_list|(
name|columnName
argument_list|)
expr_stmt|;
name|metadataAliases
operator|.
name|put
argument_list|(
name|columnName
argument_list|,
name|columnLabel
argument_list|)
expr_stmt|;
block|}
name|fields
operator|.
name|put
argument_list|(
literal|"isMetadata"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
literal|"fields"
argument_list|,
name|metadataFields
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
literal|"aliases"
argument_list|,
name|metadataAliases
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|this
operator|.
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numColumns
condition|;
name|i
operator|++
control|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|resultSetMetaData
operator|.
name|getColumnLabel
argument_list|(
name|i
argument_list|)
argument_list|,
name|this
operator|.
name|resultSet
operator|.
name|getObject
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fields
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Tuple
argument_list|(
name|fields
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|closeQuietly
specifier|private
name|void
name|closeQuietly
parameter_list|(
name|AutoCloseable
name|closeable
parameter_list|)
block|{
if|if
condition|(
name|closeable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{         }
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|closeQuietly
argument_list|(
name|this
operator|.
name|resultSet
argument_list|)
expr_stmt|;
name|this
operator|.
name|closeQuietly
argument_list|(
name|this
operator|.
name|statement
argument_list|)
expr_stmt|;
name|this
operator|.
name|closeQuietly
argument_list|(
name|this
operator|.
name|connection
argument_list|)
expr_stmt|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

