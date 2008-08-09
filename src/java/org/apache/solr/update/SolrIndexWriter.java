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
name|IndexWriter
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
name|index
operator|.
name|MergePolicy
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
name|index
operator|.
name|MergeScheduler
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
name|index
operator|.
name|LogMergePolicy
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
name|schema
operator|.
name|IndexSchema
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
name|io
operator|.
name|IOException
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|SolrIndexWriter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|schema
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|fine
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
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|setSimilarity
argument_list|(
name|schema
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// setUseCompoundFile(false);
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|setUseCompoundFile
argument_list|(
name|config
operator|.
name|useCompoundFile
argument_list|)
expr_stmt|;
comment|//only set maxBufferedDocs
if|if
condition|(
name|config
operator|.
name|maxBufferedDocs
operator|!=
operator|-
literal|1
condition|)
block|{
name|setMaxBufferedDocs
argument_list|(
name|config
operator|.
name|maxBufferedDocs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|ramBufferSizeMB
operator|!=
operator|-
literal|1
condition|)
block|{
name|setRAMBufferSizeMB
argument_list|(
name|config
operator|.
name|ramBufferSizeMB
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|maxMergeDocs
operator|!=
operator|-
literal|1
condition|)
name|setMaxMergeDocs
argument_list|(
name|config
operator|.
name|maxMergeDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|maxFieldLength
operator|!=
operator|-
literal|1
condition|)
name|setMaxFieldLength
argument_list|(
name|config
operator|.
name|maxFieldLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|mergePolicyClassName
operator|!=
literal|null
operator|&&
name|SolrIndexConfig
operator|.
name|DEFAULT_MERGE_POLICY_CLASSNAME
operator|.
name|equals
argument_list|(
name|config
operator|.
name|mergePolicyClassName
argument_list|)
operator|==
literal|false
condition|)
block|{
name|MergePolicy
name|policy
init|=
operator|(
name|MergePolicy
operator|)
name|schema
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|config
operator|.
name|mergePolicyClassName
argument_list|)
decl_stmt|;
name|setMergePolicy
argument_list|(
name|policy
argument_list|)
expr_stmt|;
comment|///hmm, is this really the best way to get a newInstance?
block|}
if|if
condition|(
name|config
operator|.
name|mergeFactor
operator|!=
operator|-
literal|1
operator|&&
name|getMergePolicy
argument_list|()
operator|instanceof
name|LogMergePolicy
condition|)
block|{
name|setMergeFactor
argument_list|(
name|config
operator|.
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|mergeSchedulerClassname
operator|!=
literal|null
operator|&&
name|SolrIndexConfig
operator|.
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
operator|.
name|equals
argument_list|(
name|config
operator|.
name|mergeSchedulerClassname
argument_list|)
operator|==
literal|false
condition|)
block|{
name|MergeScheduler
name|scheduler
init|=
operator|(
name|MergeScheduler
operator|)
name|schema
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|config
operator|.
name|mergeSchedulerClassname
argument_list|)
decl_stmt|;
name|setMergeScheduler
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
block|}
comment|//if (config.commitLockTimeout != -1) setWriteLockTimeout(config.commitLockTimeout);
block|}
block|}
DECL|method|getDirectory
specifier|public
specifier|static
name|Directory
name|getDirectory
parameter_list|(
name|String
name|path
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
name|FSDirectory
operator|.
name|getDirectory
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
comment|// we default to "simple" for backwards compatiblitiy
name|log
operator|.
name|warning
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
argument_list|()
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
comment|// recipie for disaster
name|log
operator|.
name|severe
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
operator|new
name|NoLockFactory
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
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
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
literal|null
argument_list|)
argument_list|,
literal|false
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
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
name|config
argument_list|)
argument_list|,
name|config
operator|.
name|luceneAutoCommit
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * use DocumentBuilder now...    * private final void addField(Document doc, String name, String val) {    * SchemaField ftype = schema.getField(name);    *<p/>    * // we don't check for a null val ourselves because a solr.FieldType    * // might actually want to map it to something.  If createField()    * // returns null, then we don't store the field.    *<p/>    * Field field = ftype.createField(val, boost);    * if (field != null) doc.add(field);    * }    *<p/>    *<p/>    * public void addRecord(String[] fieldNames, String[] fieldValues) throws IOException {    * Document doc = new Document();    * for (int i=0; i<fieldNames.length; i++) {    * String name = fieldNames[i];    * String val = fieldNames[i];    *<p/>    * // first null is end of list.  client can reuse arrays if they want    * // and just write a single null if there is unused space.    * if (name==null) break;    *<p/>    * addField(doc,name,val);    * }    * addDocument(doc);    * }    * ****    */
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
name|fine
argument_list|(
literal|"Closing Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

