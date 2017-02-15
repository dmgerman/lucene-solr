begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|util
operator|.
name|Collection
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
name|Histogram
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
name|Meter
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
name|Directory
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
name|FilterDirectory
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
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|LockFactory
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
name|metrics
operator|.
name|SolrMetricManager
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

begin_comment
comment|/**  * An implementation of {@link DirectoryFactory} that decorates provided factory by  * adding metrics for directory IO operations.  */
end_comment

begin_class
DECL|class|MetricsDirectoryFactory
specifier|public
class|class
name|MetricsDirectoryFactory
extends|extends
name|DirectoryFactory
implements|implements
name|SolrCoreAware
block|{
DECL|field|metricManager
specifier|private
specifier|final
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|String
name|registry
decl_stmt|;
DECL|field|in
specifier|private
specifier|final
name|DirectoryFactory
name|in
decl_stmt|;
DECL|field|directoryDetails
specifier|private
name|boolean
name|directoryDetails
init|=
literal|false
decl_stmt|;
DECL|field|directoryTotals
specifier|private
name|boolean
name|directoryTotals
init|=
literal|false
decl_stmt|;
DECL|method|MetricsDirectoryFactory
specifier|public
name|MetricsDirectoryFactory
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|String
name|registry
parameter_list|,
name|DirectoryFactory
name|in
parameter_list|)
block|{
name|this
operator|.
name|metricManager
operator|=
name|metricManager
expr_stmt|;
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|getDelegate
specifier|public
name|DirectoryFactory
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
comment|/**    * Currently the following arguments are supported:    *<ul>    *<li><code>directory</code> - (optional bool, default false) when true then coarse-grained metrics will be collected.</li>    *<li><code>directoryDetails</code> - (optional bool, default false) when true then additional detailed metrics    *   will be collected. These include eg. IO size histograms and per-file counters and histograms</li>    *</ul>    * NOTE: please be aware that collecting even coarse-grained metrics can have significant performance impact    * (see SOLR-10130).    * @param args init args    */
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
comment|// should be already inited
comment|// in.init(args);
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Boolean
name|td
init|=
name|args
operator|.
name|getBooleanArg
argument_list|(
literal|"directory"
argument_list|)
decl_stmt|;
if|if
condition|(
name|td
operator|!=
literal|null
condition|)
block|{
name|directoryTotals
operator|=
name|td
expr_stmt|;
block|}
else|else
block|{
name|directoryTotals
operator|=
literal|false
expr_stmt|;
block|}
name|Boolean
name|dd
init|=
name|args
operator|.
name|getBooleanArg
argument_list|(
literal|"directoryDetails"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dd
operator|!=
literal|null
condition|)
block|{
name|directoryDetails
operator|=
name|dd
expr_stmt|;
block|}
else|else
block|{
name|directoryDetails
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|directoryDetails
condition|)
block|{
name|directoryTotals
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Unwrap just one level if the argument is a {@link MetricsDirectory}    * @param dir directory    * @return delegate if the instance was a {@link MetricsDirectory}, otherwise unchanged.    */
DECL|method|unwrap
specifier|private
specifier|static
name|Directory
name|unwrap
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|instanceof
name|MetricsDirectory
condition|)
block|{
return|return
operator|(
operator|(
name|MetricsDirectory
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|dir
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doneWithDirectory
specifier|public
name|void
name|doneWithDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|in
operator|.
name|doneWithDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addCloseListener
specifier|public
name|void
name|addCloseListener
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|CachingDirectoryFactory
operator|.
name|CloseListener
name|closeListener
parameter_list|)
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|in
operator|.
name|addCloseListener
argument_list|(
name|dir
argument_list|,
name|closeListener
argument_list|)
expr_stmt|;
block|}
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|,
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|in
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|,
name|dirContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|MetricsDirectory
argument_list|(
name|metricManager
argument_list|,
name|registry
argument_list|,
name|dir
argument_list|,
name|directoryTotals
argument_list|,
name|directoryDetails
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createLockFactory
specifier|protected
name|LockFactory
name|createLockFactory
parameter_list|(
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|createLockFactory
argument_list|(
name|rawLockType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|exists
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|in
operator|.
name|remove
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|boolean
name|afterCoreClose
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|in
operator|.
name|remove
argument_list|(
name|dir
argument_list|,
name|afterCoreClose
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isSharedStorage
specifier|public
name|boolean
name|isSharedStorage
parameter_list|()
block|{
return|return
name|in
operator|.
name|isSharedStorage
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isAbsolute
specifier|public
name|boolean
name|isAbsolute
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|in
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|searchersReserveCommitPoints
specifier|public
name|boolean
name|searchersReserveCommitPoints
parameter_list|()
block|{
return|return
name|in
operator|.
name|searchersReserveCommitPoints
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDataHome
specifier|public
name|String
name|getDataHome
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getDataHome
argument_list|(
name|cd
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|size
argument_list|(
name|dir
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|size
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|offerMBeans
specifier|public
name|Collection
argument_list|<
name|SolrInfoMBean
argument_list|>
name|offerMBeans
parameter_list|()
block|{
return|return
name|in
operator|.
name|offerMBeans
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cleanupOldIndexDirectories
specifier|public
name|void
name|cleanupOldIndexDirectories
parameter_list|(
name|String
name|dataDirPath
parameter_list|,
name|String
name|currentIndexDirPath
parameter_list|,
name|boolean
name|reload
parameter_list|)
block|{
name|in
operator|.
name|cleanupOldIndexDirectories
argument_list|(
name|dataDirPath
argument_list|,
name|currentIndexDirPath
argument_list|,
name|reload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|afterCoreClose
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|remove
argument_list|(
name|path
argument_list|,
name|afterCoreClose
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|move
specifier|public
name|void
name|move
parameter_list|(
name|Directory
name|fromDir
parameter_list|,
name|Directory
name|toDir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
name|fromDir
operator|=
name|unwrap
argument_list|(
name|fromDir
argument_list|)
expr_stmt|;
name|toDir
operator|=
name|unwrap
argument_list|(
name|toDir
argument_list|)
expr_stmt|;
name|in
operator|.
name|move
argument_list|(
name|fromDir
argument_list|,
name|toDir
argument_list|,
name|fileName
argument_list|,
name|ioContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Directory
name|get
parameter_list|(
name|String
name|path
parameter_list|,
name|DirContext
name|dirContext
parameter_list|,
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|in
operator|.
name|get
argument_list|(
name|path
argument_list|,
name|dirContext
argument_list|,
name|rawLockType
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MetricsDirectory
condition|)
block|{
return|return
name|dir
return|;
block|}
else|else
block|{
return|return
operator|new
name|MetricsDirectory
argument_list|(
name|metricManager
argument_list|,
name|registry
argument_list|,
name|dir
argument_list|,
name|directoryTotals
argument_list|,
name|directoryDetails
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|renameWithOverwrite
specifier|public
name|void
name|renameWithOverwrite
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|toName
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|in
operator|.
name|renameWithOverwrite
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|toName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|String
name|normalize
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|normalize
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteOldIndexDirectory
specifier|protected
name|boolean
name|deleteOldIndexDirectory
parameter_list|(
name|String
name|oldDirPath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|deleteOldIndexDirectory
argument_list|(
name|oldDirPath
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initCoreContainer
specifier|public
name|void
name|initCoreContainer
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|in
operator|.
name|initCoreContainer
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incRef
specifier|public
name|void
name|incRef
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|in
operator|.
name|incRef
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isPersistent
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|in
operator|.
name|isPersistent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|in
operator|instanceof
name|SolrCoreAware
condition|)
block|{
operator|(
operator|(
name|SolrCoreAware
operator|)
name|in
operator|)
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|in
operator|.
name|release
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|field|SEGMENTS
specifier|private
specifier|static
specifier|final
name|String
name|SEGMENTS
init|=
literal|"segments"
decl_stmt|;
DECL|field|SEGMENTS_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|SEGMENTS_PREFIX
init|=
literal|"segments_"
decl_stmt|;
DECL|field|PENDING_SEGMENTS_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PENDING_SEGMENTS_PREFIX
init|=
literal|"pending_segments_"
decl_stmt|;
DECL|field|TEMP
specifier|private
specifier|static
specifier|final
name|String
name|TEMP
init|=
literal|"temp"
decl_stmt|;
DECL|field|OTHER
specifier|private
specifier|static
specifier|final
name|String
name|OTHER
init|=
literal|"other"
decl_stmt|;
DECL|class|MetricsDirectory
specifier|public
specifier|static
class|class
name|MetricsDirectory
extends|extends
name|FilterDirectory
block|{
DECL|field|in
specifier|private
specifier|final
name|Directory
name|in
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|String
name|registry
decl_stmt|;
DECL|field|metricManager
specifier|private
specifier|final
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|field|totalReads
specifier|private
specifier|final
name|Meter
name|totalReads
decl_stmt|;
DECL|field|totalReadSizes
specifier|private
specifier|final
name|Histogram
name|totalReadSizes
decl_stmt|;
DECL|field|totalWrites
specifier|private
specifier|final
name|Meter
name|totalWrites
decl_stmt|;
DECL|field|totalWriteSizes
specifier|private
specifier|final
name|Histogram
name|totalWriteSizes
decl_stmt|;
DECL|field|directoryDetails
specifier|private
specifier|final
name|boolean
name|directoryDetails
decl_stmt|;
DECL|field|directoryTotals
specifier|private
specifier|final
name|boolean
name|directoryTotals
decl_stmt|;
DECL|field|PREFIX
specifier|private
specifier|final
name|String
name|PREFIX
init|=
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|DIRECTORY
operator|.
name|toString
argument_list|()
operator|+
literal|"."
decl_stmt|;
DECL|method|MetricsDirectory
specifier|public
name|MetricsDirectory
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|String
name|registry
parameter_list|,
name|Directory
name|in
parameter_list|,
name|boolean
name|directoryTotals
parameter_list|,
name|boolean
name|directoryDetails
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|metricManager
operator|=
name|metricManager
expr_stmt|;
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|directoryDetails
operator|=
name|directoryDetails
expr_stmt|;
name|this
operator|.
name|directoryTotals
operator|=
name|directoryTotals
expr_stmt|;
if|if
condition|(
name|directoryTotals
condition|)
block|{
name|this
operator|.
name|totalReads
operator|=
name|metricManager
operator|.
name|meter
argument_list|(
name|registry
argument_list|,
literal|"reads"
argument_list|,
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|DIRECTORY
operator|.
name|toString
argument_list|()
argument_list|,
literal|"total"
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalWrites
operator|=
name|metricManager
operator|.
name|meter
argument_list|(
name|registry
argument_list|,
literal|"writes"
argument_list|,
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|DIRECTORY
operator|.
name|toString
argument_list|()
argument_list|,
literal|"total"
argument_list|)
expr_stmt|;
if|if
condition|(
name|directoryDetails
condition|)
block|{
name|this
operator|.
name|totalReadSizes
operator|=
name|metricManager
operator|.
name|histogram
argument_list|(
name|registry
argument_list|,
literal|"readSizes"
argument_list|,
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|DIRECTORY
operator|.
name|toString
argument_list|()
argument_list|,
literal|"total"
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalWriteSizes
operator|=
name|metricManager
operator|.
name|histogram
argument_list|(
name|registry
argument_list|,
literal|"writeSizes"
argument_list|,
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|DIRECTORY
operator|.
name|toString
argument_list|()
argument_list|,
literal|"total"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|totalReadSizes
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|totalWriteSizes
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|totalReads
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|totalWrites
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|totalReadSizes
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|totalWriteSizes
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getMetricName
specifier|private
name|String
name|getMetricName
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|output
parameter_list|)
block|{
if|if
condition|(
operator|!
name|directoryDetails
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|lastName
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|SEGMENTS_PREFIX
argument_list|)
operator|||
name|name
operator|.
name|startsWith
argument_list|(
name|PENDING_SEGMENTS_PREFIX
argument_list|)
condition|)
block|{
name|lastName
operator|=
name|SEGMENTS
expr_stmt|;
block|}
else|else
block|{
name|int
name|pos
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|!=
operator|-
literal|1
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|>
name|pos
operator|+
literal|1
condition|)
block|{
name|lastName
operator|=
name|name
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lastName
operator|=
name|OTHER
expr_stmt|;
block|}
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|PREFIX
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|lastName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
if|if
condition|(
name|output
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"write"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"read"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|output
init|=
name|in
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|directoryTotals
condition|)
block|{
return|return
name|output
return|;
block|}
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|MetricsOutput
argument_list|(
name|totalWrites
argument_list|,
name|totalWriteSizes
argument_list|,
name|metricManager
argument_list|,
name|registry
argument_list|,
name|getMetricName
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
argument_list|,
name|output
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createTempOutput
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|output
init|=
name|in
operator|.
name|createTempOutput
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|directoryTotals
condition|)
block|{
return|return
name|output
return|;
block|}
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|MetricsOutput
argument_list|(
name|totalWrites
argument_list|,
name|totalWriteSizes
argument_list|,
name|metricManager
argument_list|,
name|registry
argument_list|,
name|getMetricName
argument_list|(
name|TEMP
argument_list|,
literal|true
argument_list|)
argument_list|,
name|output
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
name|in
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|directoryTotals
condition|)
block|{
return|return
name|input
return|;
block|}
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|MetricsInput
argument_list|(
name|totalReads
argument_list|,
name|totalReadSizes
argument_list|,
name|metricManager
argument_list|,
name|registry
argument_list|,
name|getMetricName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
argument_list|,
name|input
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
DECL|class|MetricsOutput
specifier|public
specifier|static
class|class
name|MetricsOutput
extends|extends
name|IndexOutput
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexOutput
name|in
decl_stmt|;
DECL|field|histogram
specifier|private
specifier|final
name|Histogram
name|histogram
decl_stmt|;
DECL|field|meter
specifier|private
specifier|final
name|Meter
name|meter
decl_stmt|;
DECL|field|totalMeter
specifier|private
specifier|final
name|Meter
name|totalMeter
decl_stmt|;
DECL|field|totalHistogram
specifier|private
specifier|final
name|Histogram
name|totalHistogram
decl_stmt|;
DECL|field|withDetails
specifier|private
specifier|final
name|boolean
name|withDetails
decl_stmt|;
DECL|method|MetricsOutput
specifier|public
name|MetricsOutput
parameter_list|(
name|Meter
name|totalMeter
parameter_list|,
name|Histogram
name|totalHistogram
parameter_list|,
name|SolrMetricManager
name|metricManager
parameter_list|,
name|String
name|registry
parameter_list|,
name|String
name|metricName
parameter_list|,
name|IndexOutput
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|toString
argument_list|()
argument_list|,
name|in
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|totalMeter
operator|=
name|totalMeter
expr_stmt|;
name|this
operator|.
name|totalHistogram
operator|=
name|totalHistogram
expr_stmt|;
if|if
condition|(
name|metricName
operator|!=
literal|null
operator|&&
name|totalHistogram
operator|!=
literal|null
condition|)
block|{
name|withDetails
operator|=
literal|true
expr_stmt|;
name|String
name|histName
init|=
name|metricName
operator|+
literal|"Sizes"
decl_stmt|;
name|String
name|meterName
init|=
name|metricName
operator|+
literal|"s"
decl_stmt|;
name|this
operator|.
name|histogram
operator|=
name|metricManager
operator|.
name|histogram
argument_list|(
name|registry
argument_list|,
name|histName
argument_list|)
expr_stmt|;
name|this
operator|.
name|meter
operator|=
name|metricManager
operator|.
name|meter
argument_list|(
name|registry
argument_list|,
name|meterName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|withDetails
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|histogram
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|meter
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|totalMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
if|if
condition|(
name|withDetails
condition|)
block|{
name|totalHistogram
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|meter
operator|.
name|mark
argument_list|()
expr_stmt|;
name|histogram
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|totalMeter
operator|.
name|mark
argument_list|(
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|withDetails
condition|)
block|{
name|totalHistogram
operator|.
name|update
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|meter
operator|.
name|mark
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|histogram
operator|.
name|update
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
block|}
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|in
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChecksum
specifier|public
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getChecksum
argument_list|()
return|;
block|}
block|}
DECL|class|MetricsInput
specifier|public
specifier|static
class|class
name|MetricsInput
extends|extends
name|IndexInput
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|totalMeter
specifier|private
specifier|final
name|Meter
name|totalMeter
decl_stmt|;
DECL|field|totalHistogram
specifier|private
specifier|final
name|Histogram
name|totalHistogram
decl_stmt|;
DECL|field|histogram
specifier|private
specifier|final
name|Histogram
name|histogram
decl_stmt|;
DECL|field|meter
specifier|private
specifier|final
name|Meter
name|meter
decl_stmt|;
DECL|field|withDetails
specifier|private
specifier|final
name|boolean
name|withDetails
decl_stmt|;
DECL|method|MetricsInput
specifier|public
name|MetricsInput
parameter_list|(
name|Meter
name|totalMeter
parameter_list|,
name|Histogram
name|totalHistogram
parameter_list|,
name|SolrMetricManager
name|metricManager
parameter_list|,
name|String
name|registry
parameter_list|,
name|String
name|metricName
parameter_list|,
name|IndexInput
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|totalMeter
operator|=
name|totalMeter
expr_stmt|;
name|this
operator|.
name|totalHistogram
operator|=
name|totalHistogram
expr_stmt|;
if|if
condition|(
name|metricName
operator|!=
literal|null
operator|&&
name|totalHistogram
operator|!=
literal|null
condition|)
block|{
name|withDetails
operator|=
literal|true
expr_stmt|;
name|String
name|histName
init|=
name|metricName
operator|+
literal|"Sizes"
decl_stmt|;
name|String
name|meterName
init|=
name|metricName
operator|+
literal|"s"
decl_stmt|;
name|this
operator|.
name|histogram
operator|=
name|metricManager
operator|.
name|histogram
argument_list|(
name|registry
argument_list|,
name|histName
argument_list|)
expr_stmt|;
name|this
operator|.
name|meter
operator|=
name|metricManager
operator|.
name|meter
argument_list|(
name|registry
argument_list|,
name|meterName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|withDetails
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|histogram
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|meter
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|MetricsInput
specifier|public
name|MetricsInput
parameter_list|(
name|Meter
name|totalMeter
parameter_list|,
name|Histogram
name|totalHistogram
parameter_list|,
name|Histogram
name|histogram
parameter_list|,
name|Meter
name|meter
parameter_list|,
name|IndexInput
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|totalMeter
operator|=
name|totalMeter
expr_stmt|;
name|this
operator|.
name|totalHistogram
operator|=
name|totalHistogram
expr_stmt|;
name|this
operator|.
name|histogram
operator|=
name|histogram
expr_stmt|;
name|this
operator|.
name|meter
operator|=
name|meter
expr_stmt|;
if|if
condition|(
name|totalHistogram
operator|!=
literal|null
operator|&&
name|meter
operator|!=
literal|null
operator|&&
name|histogram
operator|!=
literal|null
condition|)
block|{
name|withDetails
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|withDetails
operator|=
literal|false
expr_stmt|;
block|}
block|}
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|in
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|in
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|IndexInput
name|clone
parameter_list|()
block|{
return|return
operator|new
name|MetricsInput
argument_list|(
name|totalMeter
argument_list|,
name|totalHistogram
argument_list|,
name|histogram
argument_list|,
name|meter
argument_list|,
name|in
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|slice
specifier|public
name|IndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|slice
init|=
name|in
operator|.
name|slice
argument_list|(
name|sliceDescription
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|MetricsInput
argument_list|(
name|totalMeter
argument_list|,
name|totalHistogram
argument_list|,
name|histogram
argument_list|,
name|meter
argument_list|,
name|slice
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
name|totalMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
if|if
condition|(
name|withDetails
condition|)
block|{
name|totalHistogram
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|meter
operator|.
name|mark
argument_list|()
expr_stmt|;
name|histogram
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|in
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|totalMeter
operator|.
name|mark
argument_list|(
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|withDetails
condition|)
block|{
name|totalHistogram
operator|.
name|update
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|meter
operator|.
name|mark
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|histogram
operator|.
name|update
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

