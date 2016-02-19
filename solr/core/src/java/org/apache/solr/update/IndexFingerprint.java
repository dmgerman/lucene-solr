begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LinkedHashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|util
operator|.
name|Bits
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
name|Hash
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
name|util
operator|.
name|RTimer
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
name|RefCounted
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
comment|/** @lucene.internal */
end_comment

begin_class
DECL|class|IndexFingerprint
specifier|public
class|class
name|IndexFingerprint
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
DECL|field|maxVersionSpecified
specifier|private
name|long
name|maxVersionSpecified
decl_stmt|;
DECL|field|maxVersionEncountered
specifier|private
name|long
name|maxVersionEncountered
decl_stmt|;
DECL|field|maxInHash
specifier|private
name|long
name|maxInHash
decl_stmt|;
DECL|field|versionsHash
specifier|private
name|long
name|versionsHash
decl_stmt|;
DECL|field|numVersions
specifier|private
name|long
name|numVersions
decl_stmt|;
DECL|field|numDocs
specifier|private
name|long
name|numDocs
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|long
name|maxDoc
decl_stmt|;
DECL|method|getMaxVersionSpecified
specifier|public
name|long
name|getMaxVersionSpecified
parameter_list|()
block|{
return|return
name|maxVersionSpecified
return|;
block|}
DECL|method|getMaxVersionEncountered
specifier|public
name|long
name|getMaxVersionEncountered
parameter_list|()
block|{
return|return
name|maxVersionEncountered
return|;
block|}
DECL|method|getMaxInHash
specifier|public
name|long
name|getMaxInHash
parameter_list|()
block|{
return|return
name|maxInHash
return|;
block|}
DECL|method|getVersionsHash
specifier|public
name|long
name|getVersionsHash
parameter_list|()
block|{
return|return
name|versionsHash
return|;
block|}
DECL|method|getNumVersions
specifier|public
name|long
name|getNumVersions
parameter_list|()
block|{
return|return
name|numVersions
return|;
block|}
DECL|method|getNumDocs
specifier|public
name|long
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
DECL|method|getMaxDoc
specifier|public
name|long
name|getMaxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
comment|/** Opens a new realtime searcher and returns it's fingerprint */
DECL|method|getFingerprint
specifier|public
specifier|static
name|IndexFingerprint
name|getFingerprint
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|long
name|maxVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|openRealtimeSearcher
argument_list|()
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|newestSearcher
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|uhandler
operator|.
name|core
operator|.
name|getRealtimeSearcher
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|getFingerprint
argument_list|(
name|newestSearcher
operator|.
name|get
argument_list|()
argument_list|,
name|maxVersion
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|newestSearcher
operator|!=
literal|null
condition|)
block|{
name|newestSearcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getFingerprint
specifier|public
specifier|static
name|IndexFingerprint
name|getFingerprint
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|long
name|maxVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|RTimer
name|timer
init|=
operator|new
name|RTimer
argument_list|()
decl_stmt|;
name|SchemaField
name|versionField
init|=
name|VersionInfo
operator|.
name|getAndCheckVersionField
argument_list|(
name|searcher
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|IndexFingerprint
name|f
init|=
operator|new
name|IndexFingerprint
argument_list|()
decl_stmt|;
name|f
operator|.
name|maxVersionSpecified
operator|=
name|maxVersion
expr_stmt|;
name|f
operator|.
name|maxDoc
operator|=
name|searcher
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
comment|// TODO: this could be parallelized, or even cached per-segment if performance becomes an issue
name|ValueSource
name|vs
init|=
name|versionField
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|versionField
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
name|funcContext
init|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|vs
operator|.
name|createWeight
argument_list|(
name|funcContext
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
control|)
block|{
name|int
name|maxDoc
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|f
operator|.
name|numDocs
operator|+=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|Bits
name|liveDocs
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|FunctionValues
name|fv
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|funcContext
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
continue|continue;
name|long
name|v
init|=
name|fv
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|f
operator|.
name|maxVersionEncountered
operator|=
name|Math
operator|.
name|max
argument_list|(
name|v
argument_list|,
name|f
operator|.
name|maxVersionEncountered
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|<=
name|f
operator|.
name|maxVersionSpecified
condition|)
block|{
name|f
operator|.
name|maxInHash
operator|=
name|Math
operator|.
name|max
argument_list|(
name|v
argument_list|,
name|f
operator|.
name|maxInHash
argument_list|)
expr_stmt|;
name|f
operator|.
name|versionsHash
operator|+=
name|Hash
operator|.
name|fmix64
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|f
operator|.
name|numVersions
operator|++
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|double
name|duration
init|=
name|timer
operator|.
name|stop
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"IndexFingerprint millis:"
operator|+
name|duration
operator|+
literal|" result:"
operator|+
name|f
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/** returns 0 for equal, negative if f1 is less recent than f2, positive if more recent */
DECL|method|compare
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|IndexFingerprint
name|f1
parameter_list|,
name|IndexFingerprint
name|f2
parameter_list|)
block|{
name|int
name|cmp
decl_stmt|;
comment|// NOTE: some way want number of docs in index to take precedence over highest version (add-only systems for sure)
comment|// if we're comparing all of the versions in the index, then go by the highest encountered.
if|if
condition|(
name|f1
operator|.
name|maxVersionSpecified
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|cmp
operator|=
name|Long
operator|.
name|compare
argument_list|(
name|f1
operator|.
name|maxVersionEncountered
argument_list|,
name|f2
operator|.
name|maxVersionEncountered
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
return|return
name|cmp
return|;
block|}
comment|// Go by the highest version under the requested max.
name|cmp
operator|=
name|Long
operator|.
name|compare
argument_list|(
name|f1
operator|.
name|maxInHash
argument_list|,
name|f2
operator|.
name|maxInHash
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
return|return
name|cmp
return|;
comment|// go by who has the most documents in the index
name|cmp
operator|=
name|Long
operator|.
name|compare
argument_list|(
name|f1
operator|.
name|numVersions
argument_list|,
name|f2
operator|.
name|numVersions
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
return|return
name|cmp
return|;
comment|// both have same number of documents, so go by hash
name|cmp
operator|=
name|Long
operator|.
name|compare
argument_list|(
name|f1
operator|.
name|versionsHash
argument_list|,
name|f2
operator|.
name|versionsHash
argument_list|)
expr_stmt|;
return|return
name|cmp
return|;
block|}
comment|/**    * Create a generic object suitable for serializing with ResponseWriters    */
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"maxVersionSpecified"
argument_list|,
name|maxVersionSpecified
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"maxVersionEncountered"
argument_list|,
name|maxVersionEncountered
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"maxInHash"
argument_list|,
name|maxInHash
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"versionsHash"
argument_list|,
name|versionsHash
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"numVersions"
argument_list|,
name|numVersions
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"numDocs"
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"maxDoc"
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
DECL|method|getLong
specifier|private
specifier|static
name|long
name|getLong
parameter_list|(
name|Object
name|o
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|def
parameter_list|)
block|{
name|long
name|v
init|=
name|def
decl_stmt|;
name|Object
name|oval
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|oval
operator|=
operator|(
operator|(
name|Map
operator|)
name|o
operator|)
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|oval
operator|=
operator|(
operator|(
name|NamedList
operator|)
name|o
operator|)
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|oval
operator|!=
literal|null
condition|?
operator|(
operator|(
name|Number
operator|)
name|oval
operator|)
operator|.
name|longValue
argument_list|()
else|:
name|def
return|;
block|}
comment|/**    * Create an IndexFingerprint object from a deserialized generic object (Map or NamedList)    */
DECL|method|fromObject
specifier|public
specifier|static
name|IndexFingerprint
name|fromObject
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IndexFingerprint
name|f
init|=
operator|new
name|IndexFingerprint
argument_list|()
decl_stmt|;
name|f
operator|.
name|maxVersionSpecified
operator|=
name|getLong
argument_list|(
name|o
argument_list|,
literal|"maxVersionSpecified"
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|f
operator|.
name|maxVersionEncountered
operator|=
name|getLong
argument_list|(
name|o
argument_list|,
literal|"maxVersionEncountered"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|maxInHash
operator|=
name|getLong
argument_list|(
name|o
argument_list|,
literal|"maxInHash"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|versionsHash
operator|=
name|getLong
argument_list|(
name|o
argument_list|,
literal|"versionsHash"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|numVersions
operator|=
name|getLong
argument_list|(
name|o
argument_list|,
literal|"numVersions"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|numDocs
operator|=
name|getLong
argument_list|(
name|o
argument_list|,
literal|"numDocs"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|maxDoc
operator|=
name|getLong
argument_list|(
name|o
argument_list|,
literal|"maxDoc"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toObject
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
