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
name|*
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
name|store
operator|.
name|*
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
name|core
operator|.
name|DirectoryFactory
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
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * An IndexWriter that is configured via Solr config mechanisms.  * * @version $Id$ * @since solr 0.9 */
end_comment

begin_class
DECL|class|SolrIndexWriter
specifier|public
class|class
name|SolrIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrIndexWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|infoStream
specifier|private
name|PrintStream
name|infoStream
decl_stmt|;
DECL|method|getDirectory
specifier|public
specifier|static
name|Directory
name|getDirectory
parameter_list|(
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|directoryFactory
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|d
init|=
name|directoryFactory
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|rawLockType
init|=
operator|(
literal|null
operator|==
name|config
operator|)
condition|?
literal|null
else|:
name|config
operator|.
name|lockType
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rawLockType
condition|)
block|{
comment|// we default to "simple" for backwards compatibility
name|log
operator|.
name|warn
argument_list|(
literal|"No lockType configured for "
operator|+
name|path
operator|+
literal|" assuming 'simple'"
argument_list|)
expr_stmt|;
name|rawLockType
operator|=
literal|"simple"
expr_stmt|;
block|}
specifier|final
name|String
name|lockType
init|=
name|rawLockType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"simple"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
comment|// multiple SimpleFSLockFactory instances should be OK
name|d
operator|.
name|setLockFactory
argument_list|(
operator|new
name|SimpleFSLockFactory
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"native"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
name|d
operator|.
name|setLockFactory
argument_list|(
operator|new
name|NativeFSLockFactory
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"single"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|d
operator|.
name|getLockFactory
argument_list|()
operator|instanceof
name|SingleInstanceLockFactory
operator|)
condition|)
name|d
operator|.
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
comment|// Recipe for disaster
name|log
operator|.
name|error
argument_list|(
literal|"CONFIGURATION WARNING: locks are disabled on "
operator|+
name|path
argument_list|)
expr_stmt|;
name|d
operator|.
name|setLockFactory
argument_list|(
name|NoLockFactory
operator|.
name|getNoLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"Unrecognized lockType: "
operator|+
name|rawLockType
argument_list|)
throw|;
block|}
return|return
name|d
return|;
block|}
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|dirFactory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|,
name|IndexDeletionPolicy
name|delPolicy
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|getDirectory
argument_list|(
name|path
argument_list|,
name|dirFactory
argument_list|,
name|config
argument_list|)
argument_list|,
name|config
operator|.
name|toIndexWriterConfig
argument_list|(
name|schema
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|create
condition|?
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
else|:
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|delPolicy
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Opened Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|String
name|infoStreamFile
init|=
name|config
operator|.
name|infoStreamFile
decl_stmt|;
if|if
condition|(
name|infoStreamFile
operator|!=
literal|null
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|infoStreamFile
argument_list|)
decl_stmt|;
name|File
name|parent
init|=
name|f
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|parent
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|infoStream
operator|=
operator|new
name|TimeLoggingPrintStream
argument_list|(
name|fos
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setInfoStream
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * use DocumentBuilder now...    * private final void addField(Document doc, String name, String val) {    * SchemaField ftype = schema.getField(name);    *<p/>    * // we don't check for a null val ourselves because a solr.FieldType    * // might actually want to map it to something.  If createField()    * // returns null, then we don't store the field.    *<p/>    * Field field = ftype.createField(val, boost);    * if (field != null) doc.add(field);    * }    *<p/>    *<p/>    * public void addRecord(String[] fieldNames, String[] fieldValues) throws IOException {    * Document doc = new Document();    * for (int i=0; i<fieldNames.length; i++) {    * String name = fieldNames[i];    * String val = fieldNames[i];    *<p/>    * // first null is end of list.  client can reuse arrays if they want    * // and just write a single null if there is unused space.    * if (name==null) break;    *<p/>    * addField(doc,name,val);    * }    * addDocument(doc);    * }    * ****    */
DECL|field|isClosed
specifier|private
specifier|volatile
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Closing Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|isClosed
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|isClosed
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
operator|!
name|isClosed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"SolrIndexWriter was not closed prior to finalize(), indicates a bug -- POSSIBLE RESOURCE LEAK!!!"
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Helper class for adding timestamps to infoStream logging
DECL|class|TimeLoggingPrintStream
class|class
name|TimeLoggingPrintStream
extends|extends
name|PrintStream
block|{
DECL|field|dateFormat
specifier|private
name|DateFormat
name|dateFormat
decl_stmt|;
DECL|method|TimeLoggingPrintStream
specifier|public
name|TimeLoggingPrintStream
parameter_list|(
name|OutputStream
name|underlyingOutputStream
parameter_list|,
name|boolean
name|autoFlush
parameter_list|)
block|{
name|super
argument_list|(
name|underlyingOutputStream
argument_list|,
name|autoFlush
argument_list|)
expr_stmt|;
name|this
operator|.
name|dateFormat
operator|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|()
expr_stmt|;
block|}
comment|// We might ideally want to override print(String) as well, but
comment|// looking through the code that writes to infoStream, it appears
comment|// that all the classes except CheckIndex just use println.
annotation|@
name|Override
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|String
name|x
parameter_list|)
block|{
name|print
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|" "
argument_list|)
expr_stmt|;
name|super
operator|.
name|println
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

