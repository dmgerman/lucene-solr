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
name|ArrayList
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|AlreadyClosedException
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
name|solr
operator|.
name|SolrTestCaseJ4
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
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
DECL|class|CachingDirectoryFactoryTest
specifier|public
class|class
name|CachingDirectoryFactoryTest
extends|extends
name|SolrTestCaseJ4
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
DECL|field|dirs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Tracker
argument_list|>
name|dirs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|stop
specifier|private
specifier|volatile
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
DECL|class|Tracker
specifier|private
specifier|static
class|class
name|Tracker
block|{
DECL|field|path
name|String
name|path
decl_stmt|;
DECL|field|refCnt
name|AtomicInteger
name|refCnt
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|stressTest
specifier|public
name|void
name|stressTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CachingDirectoryFactory
name|df
init|=
operator|new
name|RAMDirectoryFactory
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|threadCount
init|=
literal|11
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|getDirThread
init|=
operator|new
name|GetDirThread
argument_list|(
name|df
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|getDirThread
argument_list|)
expr_stmt|;
name|getDirThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|releaseDirThread
init|=
operator|new
name|ReleaseDirThread
argument_list|(
name|df
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|releaseDirThread
argument_list|)
expr_stmt|;
name|releaseDirThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|incRefThread
init|=
operator|new
name|IncRefThread
argument_list|(
name|df
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|incRefThread
argument_list|)
expr_stmt|;
name|incRefThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|30000
else|:
literal|8000
argument_list|)
expr_stmt|;
name|Thread
name|closeThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|df
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
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|closeThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|stop
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|// do any remaining releases
synchronized|synchronized
init|(
name|dirs
init|)
block|{
name|int
name|sz
init|=
name|dirs
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Tracker
name|tracker
range|:
name|dirs
operator|.
name|values
argument_list|()
control|)
block|{
name|int
name|cnt
init|=
name|tracker
operator|.
name|refCnt
operator|.
name|get
argument_list|()
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|tracker
operator|.
name|refCnt
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|df
operator|.
name|release
argument_list|(
name|tracker
operator|.
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|closeThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|class|ReleaseDirThread
specifier|private
class|class
name|ReleaseDirThread
extends|extends
name|Thread
block|{
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|field|df
specifier|private
name|CachingDirectoryFactory
name|df
decl_stmt|;
DECL|method|ReleaseDirThread
specifier|public
name|ReleaseDirThread
parameter_list|(
name|CachingDirectoryFactory
name|df
parameter_list|)
block|{
name|this
operator|.
name|df
operator|=
name|df
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|random
operator|=
name|random
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|stop
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|dirs
init|)
block|{
name|int
name|sz
init|=
name|dirs
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Tracker
argument_list|>
name|dirsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|dirsList
operator|.
name|addAll
argument_list|(
name|dirs
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
block|{
name|Tracker
name|tracker
init|=
name|dirsList
operator|.
name|get
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|dirsList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|tracker
operator|.
name|refCnt
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|>
literal|7
condition|)
block|{
name|df
operator|.
name|doneWithDirectory
argument_list|(
name|tracker
operator|.
name|dir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|df
operator|.
name|remove
argument_list|(
name|tracker
operator|.
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|df
operator|.
name|remove
argument_list|(
name|tracker
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
name|tracker
operator|.
name|refCnt
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|df
operator|.
name|release
argument_list|(
name|tracker
operator|.
name|dir
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"path:"
operator|+
name|tracker
operator|.
name|path
operator|+
literal|"ref cnt:"
operator|+
name|tracker
operator|.
name|refCnt
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|class|GetDirThread
specifier|private
class|class
name|GetDirThread
extends|extends
name|Thread
block|{
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|field|df
specifier|private
name|CachingDirectoryFactory
name|df
decl_stmt|;
DECL|method|GetDirThread
specifier|public
name|GetDirThread
parameter_list|(
name|CachingDirectoryFactory
name|df
parameter_list|)
block|{
name|this
operator|.
name|df
operator|=
name|df
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|random
operator|=
name|random
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|stop
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|350
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
try|try
block|{
name|String
name|path
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|path
operator|=
literal|"path"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|path
operator|=
literal|"path"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|+
literal|"/"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
literal|"path"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|+
literal|"/"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|+
literal|"/"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|dirs
init|)
block|{
name|Tracker
name|tracker
init|=
name|dirs
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|tracker
operator|==
literal|null
condition|)
block|{
name|tracker
operator|=
operator|new
name|Tracker
argument_list|()
expr_stmt|;
name|tracker
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|tracker
operator|.
name|dir
operator|=
name|df
operator|.
name|get
argument_list|(
name|path
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SINGLE
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|tracker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tracker
operator|.
name|dir
operator|=
name|df
operator|.
name|get
argument_list|(
name|path
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SINGLE
argument_list|)
expr_stmt|;
block|}
name|tracker
operator|.
name|refCnt
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot get dir, factory is already closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|class|IncRefThread
specifier|private
class|class
name|IncRefThread
extends|extends
name|Thread
block|{
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|field|df
specifier|private
name|CachingDirectoryFactory
name|df
decl_stmt|;
DECL|method|IncRefThread
specifier|public
name|IncRefThread
parameter_list|(
name|CachingDirectoryFactory
name|df
parameter_list|)
block|{
name|this
operator|.
name|df
operator|=
name|df
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|random
operator|=
name|random
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|stop
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|300
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
name|String
name|path
init|=
literal|"path"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|dirs
init|)
block|{
name|Tracker
name|tracker
init|=
name|dirs
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|tracker
operator|!=
literal|null
operator|&&
name|tracker
operator|.
name|refCnt
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|df
operator|.
name|incRef
argument_list|(
name|tracker
operator|.
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|tracker
operator|.
name|refCnt
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

