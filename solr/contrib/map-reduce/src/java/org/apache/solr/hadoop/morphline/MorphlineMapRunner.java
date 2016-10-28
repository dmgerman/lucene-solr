begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop.morphline
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
operator|.
name|morphline
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Mapper
operator|.
name|Context
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
name|SolrServerException
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
name|hadoop
operator|.
name|HdfsFileFieldNames
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
name|hadoop
operator|.
name|PathParts
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
name|hadoop
operator|.
name|Utils
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
name|morphlines
operator|.
name|solr
operator|.
name|DocumentLoader
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
name|morphlines
operator|.
name|solr
operator|.
name|SolrLocator
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
name|morphlines
operator|.
name|solr
operator|.
name|SolrMorphlineContext
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

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineCompilationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Compiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|FaultTolerance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Fields
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Metrics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Notifications
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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
name|annotations
operator|.
name|Beta
import|;
end_import

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|ConfigFactory
import|;
end_import

begin_comment
comment|/**  * Internal helper for {@link MorphlineMapper} and dryRun mode; This API is for *INTERNAL* use only  * and should not be considered public.  */
end_comment

begin_class
annotation|@
name|Beta
DECL|class|MorphlineMapRunner
specifier|public
specifier|final
class|class
name|MorphlineMapRunner
block|{
DECL|field|morphlineContext
specifier|private
name|MorphlineContext
name|morphlineContext
decl_stmt|;
DECL|field|morphline
specifier|private
name|Command
name|morphline
decl_stmt|;
DECL|field|schema
specifier|private
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|commandLineMorphlineHeaders
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commandLineMorphlineHeaders
decl_stmt|;
DECL|field|disableFileOpen
specifier|private
name|boolean
name|disableFileOpen
decl_stmt|;
DECL|field|morphlineFileAndId
specifier|private
name|String
name|morphlineFileAndId
decl_stmt|;
DECL|field|elapsedTime
specifier|private
specifier|final
name|Timer
name|elapsedTime
decl_stmt|;
DECL|field|MORPHLINE_FILE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|MORPHLINE_FILE_PARAM
init|=
literal|"morphlineFile"
decl_stmt|;
DECL|field|MORPHLINE_ID_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|MORPHLINE_ID_PARAM
init|=
literal|"morphlineId"
decl_stmt|;
comment|/**    * Morphline variables can be passed from the CLI to the Morphline, e.g.:    * hadoop ... -D morphlineVariable.zkHost=127.0.0.1:2181/solr    */
DECL|field|MORPHLINE_VARIABLE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|MORPHLINE_VARIABLE_PARAM
init|=
literal|"morphlineVariable"
decl_stmt|;
comment|/**    * Headers, including MIME types, can also explicitly be passed by force from the CLI to Morphline, e.g:    * hadoop ... -D morphlineField._attachment_mimetype=text/csv    */
DECL|field|MORPHLINE_FIELD_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MORPHLINE_FIELD_PREFIX
init|=
literal|"morphlineField."
decl_stmt|;
comment|/**    * Flag to disable reading of file contents if indexing just file metadata is sufficient.     * This improves performance and confidentiality.    */
DECL|field|DISABLE_FILE_OPEN
specifier|public
specifier|static
specifier|final
name|String
name|DISABLE_FILE_OPEN
init|=
literal|"morphlineDisableFileOpen"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
DECL|method|getMorphlineContext
name|MorphlineContext
name|getMorphlineContext
parameter_list|()
block|{
return|return
name|morphlineContext
return|;
block|}
DECL|method|getSchema
name|IndexSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
DECL|method|MorphlineMapRunner
specifier|public
name|MorphlineMapRunner
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|DocumentLoader
name|loader
parameter_list|,
name|String
name|solrHomeDir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"CWD is {}"
argument_list|,
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|TreeMap
name|map
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|configuration
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"Configuration:\n"
operator|+
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FaultTolerance
name|faultTolerance
init|=
operator|new
name|FaultTolerance
argument_list|(
name|configuration
operator|.
name|getBoolean
argument_list|(
name|FaultTolerance
operator|.
name|IS_PRODUCTION_MODE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|configuration
operator|.
name|getBoolean
argument_list|(
name|FaultTolerance
operator|.
name|IS_IGNORING_RECOVERABLE_EXCEPTIONS
argument_list|,
literal|false
argument_list|)
argument_list|,
name|configuration
operator|.
name|get
argument_list|(
name|FaultTolerance
operator|.
name|RECOVERABLE_EXCEPTION_CLASSES
argument_list|,
name|SolrServerException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|morphlineContext
operator|=
operator|new
name|SolrMorphlineContext
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentLoader
argument_list|(
name|loader
argument_list|)
operator|.
name|setExceptionHandler
argument_list|(
name|faultTolerance
argument_list|)
operator|.
name|setMetricRegistry
argument_list|(
operator|new
name|MetricRegistry
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
class|class
name|MySolrLocator
extends|extends
name|SolrLocator
block|{
comment|// trick to access protected ctor
specifier|public
name|MySolrLocator
parameter_list|(
name|MorphlineContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
name|SolrLocator
name|locator
init|=
operator|new
name|MySolrLocator
argument_list|(
name|morphlineContext
argument_list|)
decl_stmt|;
name|locator
operator|.
name|setSolrHomeDir
argument_list|(
name|solrHomeDir
argument_list|)
expr_stmt|;
name|schema
operator|=
name|locator
operator|.
name|getIndexSchema
argument_list|()
expr_stmt|;
comment|// rebuild context, now with schema
name|morphlineContext
operator|=
operator|new
name|SolrMorphlineContext
operator|.
name|Builder
argument_list|()
operator|.
name|setIndexSchema
argument_list|(
name|schema
argument_list|)
operator|.
name|setDocumentLoader
argument_list|(
name|loader
argument_list|)
operator|.
name|setExceptionHandler
argument_list|(
name|faultTolerance
argument_list|)
operator|.
name|setMetricRegistry
argument_list|(
name|morphlineContext
operator|.
name|getMetricRegistry
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
name|morphlineFile
init|=
name|configuration
operator|.
name|get
argument_list|(
name|MORPHLINE_FILE_PARAM
argument_list|)
decl_stmt|;
name|String
name|morphlineId
init|=
name|configuration
operator|.
name|get
argument_list|(
name|MORPHLINE_ID_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|morphlineFile
operator|==
literal|null
operator|||
name|morphlineFile
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Missing parameter: "
operator|+
name|MORPHLINE_FILE_PARAM
argument_list|,
literal|null
argument_list|)
throw|;
block|}
name|Map
name|morphlineVariables
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|configuration
control|)
block|{
name|String
name|variablePrefix
init|=
name|MORPHLINE_VARIABLE_PARAM
operator|+
literal|"."
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|variablePrefix
argument_list|)
condition|)
block|{
name|morphlineVariables
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
name|variablePrefix
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Config
name|override
init|=
name|ConfigFactory
operator|.
name|parseMap
argument_list|(
name|morphlineVariables
argument_list|)
decl_stmt|;
name|morphline
operator|=
operator|new
name|Compiler
argument_list|()
operator|.
name|compile
argument_list|(
operator|new
name|File
argument_list|(
name|morphlineFile
argument_list|)
argument_list|,
name|morphlineId
argument_list|,
name|morphlineContext
argument_list|,
literal|null
argument_list|,
name|override
argument_list|)
expr_stmt|;
name|morphlineFileAndId
operator|=
name|morphlineFile
operator|+
literal|"@"
operator|+
name|morphlineId
expr_stmt|;
name|disableFileOpen
operator|=
name|configuration
operator|.
name|getBoolean
argument_list|(
name|DISABLE_FILE_OPEN
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"disableFileOpen: {}"
argument_list|,
name|disableFileOpen
argument_list|)
expr_stmt|;
name|commandLineMorphlineHeaders
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|configuration
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|MORPHLINE_FIELD_PREFIX
argument_list|)
condition|)
block|{
name|commandLineMorphlineHeaders
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
name|MORPHLINE_FIELD_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Headers, including MIME types, passed by force from the CLI to morphline: {}"
argument_list|,
name|commandLineMorphlineHeaders
argument_list|)
expr_stmt|;
name|String
name|metricName
init|=
name|MetricRegistry
operator|.
name|name
argument_list|(
name|Utils
operator|.
name|getShortClassName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
name|Metrics
operator|.
name|ELAPSED_TIME
argument_list|)
decl_stmt|;
name|this
operator|.
name|elapsedTime
operator|=
name|morphlineContext
operator|.
name|getMetricRegistry
argument_list|()
operator|.
name|timer
argument_list|(
name|metricName
argument_list|)
expr_stmt|;
name|Notifications
operator|.
name|notifyBeginTransaction
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
block|}
comment|/**    * Extract content from the path specified in the value. Key is useless.    */
DECL|method|map
specifier|public
name|void
name|map
parameter_list|(
name|String
name|value
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing file {}"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
name|Record
name|record
init|=
literal|null
decl_stmt|;
name|Timer
operator|.
name|Context
name|timerContext
init|=
name|elapsedTime
operator|.
name|time
argument_list|()
decl_stmt|;
try|try
block|{
name|PathParts
name|parts
init|=
operator|new
name|PathParts
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|record
operator|=
name|getRecord
argument_list|(
name|parts
argument_list|)
expr_stmt|;
if|if
condition|(
name|record
operator|==
literal|null
condition|)
block|{
return|return;
comment|// ignore
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|commandLineMorphlineHeaders
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|record
operator|.
name|replaceValues
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|fileLength
init|=
name|parts
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getLen
argument_list|()
decl_stmt|;
if|if
condition|(
name|disableFileOpen
condition|)
block|{
name|in
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|parts
operator|.
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|parts
operator|.
name|getUploadPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|record
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ATTACHMENT_BODY
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|Notifications
operator|.
name|notifyStartSession
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|morphline
operator|.
name|process
argument_list|(
name|record
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Morphline {} failed to process record: {}"
argument_list|,
name|morphlineFileAndId
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getCounter
argument_list|(
name|MorphlineCounters
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|MorphlineCounters
operator|.
name|FILES_READ
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|increment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|context
operator|.
name|getCounter
argument_list|(
name|MorphlineCounters
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|MorphlineCounters
operator|.
name|FILE_BYTES_READ
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|increment
argument_list|(
name|fileLength
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to process file "
operator|+
name|value
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getCounter
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".errors"
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|increment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|morphlineContext
operator|.
name|getExceptionHandler
argument_list|()
operator|.
name|handleException
argument_list|(
name|e
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|timerContext
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRecord
specifier|protected
name|Record
name|getRecord
parameter_list|(
name|PathParts
name|parts
parameter_list|)
block|{
name|FileStatus
name|stats
decl_stmt|;
try|try
block|{
name|stats
operator|=
name|parts
operator|.
name|getFileStatus
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|stats
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring file that somehow has become unavailable since the job was submitted: {}"
argument_list|,
name|parts
operator|.
name|getUploadURL
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Record
name|headers
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
comment|//headers.put(getSchema().getUniqueKeyField().getName(), parts.getId()); // use HDFS file path as docId if no docId is specified
name|headers
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|BASE_ID
argument_list|,
name|parts
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// with sanitizeUniqueKey command, use HDFS file path as docId if no docId is specified
name|headers
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ATTACHMENT_NAME
argument_list|,
name|parts
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Tika can use the file name in guessing the right MIME type
comment|// enable indexing and storing of file meta data in Solr
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_UPLOAD_URL
argument_list|,
name|parts
operator|.
name|getUploadURL
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_DOWNLOAD_URL
argument_list|,
name|parts
operator|.
name|getDownloadURL
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_SCHEME
argument_list|,
name|parts
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_HOST
argument_list|,
name|parts
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_PORT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|parts
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_PATH
argument_list|,
name|parts
operator|.
name|getURIPath
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_NAME
argument_list|,
name|parts
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_LAST_MODIFIED
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|stats
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// FIXME also add in SpoolDirectorySource
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_LENGTH
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|stats
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// FIXME also add in SpoolDirectorySource
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_OWNER
argument_list|,
name|stats
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_GROUP
argument_list|,
name|stats
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_PERMISSIONS_USER
argument_list|,
name|stats
operator|.
name|getPermission
argument_list|()
operator|.
name|getUserAction
argument_list|()
operator|.
name|SYMBOL
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_PERMISSIONS_GROUP
argument_list|,
name|stats
operator|.
name|getPermission
argument_list|()
operator|.
name|getGroupAction
argument_list|()
operator|.
name|SYMBOL
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_PERMISSIONS_OTHER
argument_list|,
name|stats
operator|.
name|getPermission
argument_list|()
operator|.
name|getOtherAction
argument_list|()
operator|.
name|SYMBOL
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|HdfsFileFieldNames
operator|.
name|FILE_PERMISSIONS_STICKYBIT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|stats
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: consider to add stats.getAccessTime(), stats.getReplication(), stats.isSymlink(), stats.getBlockSize()
return|return
name|headers
return|;
block|}
DECL|method|cleanup
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|Notifications
operator|.
name|notifyCommitTransaction
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|Notifications
operator|.
name|notifyShutdown
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

