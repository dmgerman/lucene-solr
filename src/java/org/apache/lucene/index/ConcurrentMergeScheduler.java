begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|List
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

begin_comment
comment|/** A {@link MergeScheduler} that runs each merge using a  *  separate thread, up until a maximum number of threads  *  ({@link #setMaxThreadCount}) at which when a merge is  *  needed, the thread(s) that are updating the index will  *  pause until one or more merges completes.  This is a  *  simple way to use concurrency in the indexing process  *  without having to create and manage application level  *  threads. */
end_comment

begin_class
DECL|class|ConcurrentMergeScheduler
specifier|public
class|class
name|ConcurrentMergeScheduler
extends|extends
name|MergeScheduler
block|{
DECL|field|mergeThreadPriority
specifier|private
name|int
name|mergeThreadPriority
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|mergeThreads
specifier|protected
name|List
name|mergeThreads
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|// Max number of threads allowed to be merging at once
DECL|field|maxThreadCount
specifier|private
name|int
name|maxThreadCount
init|=
literal|3
decl_stmt|;
DECL|field|exceptions
specifier|private
name|List
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|dir
specifier|protected
name|Directory
name|dir
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|writer
specifier|protected
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|mergeThreadCount
specifier|protected
name|int
name|mergeThreadCount
decl_stmt|;
DECL|method|ConcurrentMergeScheduler
specifier|public
name|ConcurrentMergeScheduler
parameter_list|()
block|{
if|if
condition|(
name|allInstances
operator|!=
literal|null
condition|)
block|{
comment|// Only for testing
name|addMyself
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Sets the max # simultaneous threads that may be    *  running.  If a merge is necessary yet we already have    *  this many threads running, the incoming thread (that    *  is calling add/updateDocument) will block until    *  a merge thread has completed. */
DECL|method|setMaxThreadCount
specifier|public
name|void
name|setMaxThreadCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"count should be at least 1"
argument_list|)
throw|;
name|maxThreadCount
operator|=
name|count
expr_stmt|;
block|}
comment|/** Get the max # simultaneous threads that may be    *  running. @see #setMaxThreadCount. */
DECL|method|getMaxThreadCount
specifier|public
name|int
name|getMaxThreadCount
parameter_list|()
block|{
return|return
name|maxThreadCount
return|;
block|}
comment|/** Return the priority that merge threads run at.  By    *  default the priority is 1 plus the priority of (ie,    *  slightly higher priority than) the first thread that    *  calls merge. */
DECL|method|getMergeThreadPriority
specifier|public
specifier|synchronized
name|int
name|getMergeThreadPriority
parameter_list|()
block|{
name|initMergeThreadPriority
argument_list|()
expr_stmt|;
return|return
name|mergeThreadPriority
return|;
block|}
comment|/** Return the priority that merge threads run at. */
DECL|method|setMergeThreadPriority
specifier|public
specifier|synchronized
name|void
name|setMergeThreadPriority
parameter_list|(
name|int
name|pri
parameter_list|)
block|{
if|if
condition|(
name|pri
operator|>
name|Thread
operator|.
name|MAX_PRIORITY
operator|||
name|pri
operator|<
name|Thread
operator|.
name|MIN_PRIORITY
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"priority must be in range "
operator|+
name|Thread
operator|.
name|MIN_PRIORITY
operator|+
literal|" .. "
operator|+
name|Thread
operator|.
name|MAX_PRIORITY
operator|+
literal|" inclusive"
argument_list|)
throw|;
name|mergeThreadPriority
operator|=
name|pri
expr_stmt|;
specifier|final
name|int
name|numThreads
init|=
name|mergeThreadCount
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|MergeThread
name|merge
init|=
operator|(
name|MergeThread
operator|)
name|mergeThreads
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|merge
operator|.
name|setThreadPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verbose
specifier|private
name|boolean
name|verbose
parameter_list|()
block|{
return|return
name|writer
operator|!=
literal|null
operator|&&
name|writer
operator|.
name|verbose
argument_list|()
return|;
block|}
DECL|method|message
specifier|private
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|writer
operator|.
name|message
argument_list|(
literal|"CMS: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|initMergeThreadPriority
specifier|private
specifier|synchronized
name|void
name|initMergeThreadPriority
parameter_list|()
block|{
if|if
condition|(
name|mergeThreadPriority
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Default to slightly higher priority than our
comment|// calling thread
name|mergeThreadPriority
operator|=
literal|1
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
if|if
condition|(
name|mergeThreadPriority
operator|>
name|Thread
operator|.
name|MAX_PRIORITY
condition|)
name|mergeThreadPriority
operator|=
name|Thread
operator|.
name|MAX_PRIORITY
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|sync
specifier|public
specifier|synchronized
name|void
name|sync
parameter_list|()
block|{
while|while
condition|(
name|mergeThreadCount
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"now wait for threads; currently "
operator|+
name|mergeThreads
operator|.
name|size
argument_list|()
operator|+
literal|" still running"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|mergeThreads
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|verbose
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
name|message
argument_list|(
literal|"    "
operator|+
name|i
operator|+
literal|": "
operator|+
operator|(
operator|(
name|MergeThread
operator|)
name|mergeThreads
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// In 3.0 we will change this to throw
comment|// InterruptedException instead
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|mergeThreadCount
specifier|private
specifier|synchronized
name|int
name|mergeThreadCount
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|numThreads
init|=
name|mergeThreads
operator|.
name|size
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
name|numThreads
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|(
operator|(
name|MergeThread
operator|)
name|mergeThreads
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|isAlive
argument_list|()
condition|)
name|count
operator|++
expr_stmt|;
return|return
name|count
return|;
block|}
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
comment|// TODO: enable this once we are on JRE 1.5
comment|// assert !Thread.holdsLock(writer);
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|initMergeThreadPriority
argument_list|()
expr_stmt|;
name|dir
operator|=
name|writer
operator|.
name|getDirectory
argument_list|()
expr_stmt|;
comment|// First, quickly run through the newly proposed merges
comment|// and add any orthogonal merges (ie a merge not
comment|// involving segments already pending to be merged) to
comment|// the queue.  If we are way behind on merging, many of
comment|// these newly proposed merges will likely already be
comment|// registered.
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"now merge"
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"  index: "
operator|+
name|writer
operator|.
name|segString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Iterate, pulling from the IndexWriter's queue of
comment|// pending merges, until it's empty:
while|while
condition|(
literal|true
condition|)
block|{
comment|// TODO: we could be careful about which merges to do in
comment|// the BG (eg maybe the "biggest" ones) vs FG, which
comment|// merges to do first (the easiest ones?), etc.
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|writer
operator|.
name|getNextMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  no more merges pending; now return"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// We do this w/ the primary thread to keep
comment|// deterministic assignment of segment names
name|writer
operator|.
name|mergeInit
argument_list|(
name|merge
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
specifier|final
name|MergeThread
name|merger
decl_stmt|;
while|while
condition|(
name|mergeThreadCount
argument_list|()
operator|>=
name|maxThreadCount
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"    too many merge threads running; stalling..."
argument_list|)
expr_stmt|;
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// In 3.0 we will change this to throw
comment|// InterruptedException instead
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  consider merge "
operator|+
name|merge
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|mergeThreadCount
argument_list|()
operator|<
name|maxThreadCount
assert|;
comment|// OK to spawn a new merge thread to handle this
comment|// merge:
name|merger
operator|=
name|getMergeThread
argument_list|(
name|writer
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|mergeThreads
operator|.
name|add
argument_list|(
name|merger
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"    launch new thread ["
operator|+
name|merger
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|merger
operator|.
name|start
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|writer
operator|.
name|mergeFinish
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Does the actual merge, by calling {@link IndexWriter#merge} */
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
comment|/** Create and return a new MergeThread */
DECL|method|getMergeThread
specifier|protected
specifier|synchronized
name|MergeThread
name|getMergeThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MergeThread
name|thread
init|=
operator|new
name|MergeThread
argument_list|(
name|writer
argument_list|,
name|merge
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setThreadPriority
argument_list|(
name|mergeThreadPriority
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"Lucene Merge Thread #"
operator|+
name|mergeThreadCount
operator|++
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
DECL|class|MergeThread
specifier|protected
class|class
name|MergeThread
extends|extends
name|Thread
block|{
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|startMerge
name|MergePolicy
operator|.
name|OneMerge
name|startMerge
decl_stmt|;
DECL|field|runningMerge
name|MergePolicy
operator|.
name|OneMerge
name|runningMerge
decl_stmt|;
DECL|method|MergeThread
specifier|public
name|MergeThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergePolicy
operator|.
name|OneMerge
name|startMerge
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|startMerge
operator|=
name|startMerge
expr_stmt|;
block|}
DECL|method|setRunningMerge
specifier|public
specifier|synchronized
name|void
name|setRunningMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
block|{
name|runningMerge
operator|=
name|merge
expr_stmt|;
block|}
DECL|method|getRunningMerge
specifier|public
specifier|synchronized
name|MergePolicy
operator|.
name|OneMerge
name|getRunningMerge
parameter_list|()
block|{
return|return
name|runningMerge
return|;
block|}
DECL|method|setThreadPriority
specifier|public
name|void
name|setThreadPriority
parameter_list|(
name|int
name|pri
parameter_list|)
block|{
try|try
block|{
name|setPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// Strangely, Sun's JDK 1.5 on Linux sometimes
comment|// throws NPE out of here...
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
comment|// Ignore this because we will still run fine with
comment|// normal thread priority
block|}
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// First time through the while loop we do the merge
comment|// that we were started with:
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|this
operator|.
name|startMerge
decl_stmt|;
try|try
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  merge thread: start"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|setRunningMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
name|doMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
comment|// Subsequent times through the loop we do any new
comment|// merge that writer says is necessary:
name|merge
operator|=
name|writer
operator|.
name|getNextMerge
argument_list|()
expr_stmt|;
if|if
condition|(
name|merge
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|mergeInit
argument_list|(
name|merge
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  merge thread: do another merge "
operator|+
name|merge
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
break|break;
block|}
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  merge thread: done"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|exc
parameter_list|)
block|{
comment|// Ignore the exception if it was due to abort:
if|if
condition|(
operator|!
operator|(
name|exc
operator|instanceof
name|MergePolicy
operator|.
name|MergeAbortedException
operator|)
condition|)
block|{
synchronized|synchronized
init|(
name|ConcurrentMergeScheduler
operator|.
name|this
init|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|suppressExceptions
condition|)
block|{
comment|// suppressExceptions is normally only set during
comment|// testing.
name|anyExceptions
operator|=
literal|true
expr_stmt|;
name|handleMergeException
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|ConcurrentMergeScheduler
operator|.
name|this
init|)
block|{
name|ConcurrentMergeScheduler
operator|.
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
name|boolean
name|removed
init|=
name|mergeThreads
operator|.
name|remove
argument_list|(
name|this
argument_list|)
decl_stmt|;
assert|assert
name|removed
assert|;
block|}
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|getRunningMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|==
literal|null
condition|)
name|merge
operator|=
name|startMerge
expr_stmt|;
return|return
literal|"merge thread: "
operator|+
name|merge
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
return|;
block|}
block|}
comment|/** Called when an exception is hit in a background merge    *  thread */
DECL|method|handleMergeException
specifier|protected
name|void
name|handleMergeException
parameter_list|(
name|Throwable
name|exc
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// In 3.0 this will throw InterruptedException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|MergePolicy
operator|.
name|MergeException
argument_list|(
name|exc
argument_list|,
name|dir
argument_list|)
throw|;
block|}
DECL|field|anyExceptions
specifier|static
name|boolean
name|anyExceptions
init|=
literal|false
decl_stmt|;
comment|/** Used for testing */
DECL|method|anyUnhandledExceptions
specifier|public
specifier|static
name|boolean
name|anyUnhandledExceptions
parameter_list|()
block|{
if|if
condition|(
name|allInstances
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"setTestMode() was not called; often this is because your test case's setUp method fails to call super.setUp in LuceneTestCase"
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|allInstances
init|)
block|{
specifier|final
name|int
name|count
init|=
name|allInstances
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Make sure all outstanding threads are done so we see
comment|// any exceptions they may produce:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|allInstances
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|sync
argument_list|()
expr_stmt|;
name|boolean
name|v
init|=
name|anyExceptions
decl_stmt|;
name|anyExceptions
operator|=
literal|false
expr_stmt|;
return|return
name|v
return|;
block|}
block|}
DECL|method|clearUnhandledExceptions
specifier|public
specifier|static
name|void
name|clearUnhandledExceptions
parameter_list|()
block|{
synchronized|synchronized
init|(
name|allInstances
init|)
block|{
name|anyExceptions
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/** Used for testing */
DECL|method|addMyself
specifier|private
name|void
name|addMyself
parameter_list|()
block|{
synchronized|synchronized
init|(
name|allInstances
init|)
block|{
specifier|final
name|int
name|size
init|=
name|allInstances
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|upto
init|=
literal|0
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
name|size
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ConcurrentMergeScheduler
name|other
init|=
operator|(
name|ConcurrentMergeScheduler
operator|)
name|allInstances
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|other
operator|.
name|closed
operator|&&
literal|0
operator|==
name|other
operator|.
name|mergeThreadCount
argument_list|()
operator|)
condition|)
comment|// Keep this one for now: it still has threads or
comment|// may spawn new threads
name|allInstances
operator|.
name|set
argument_list|(
name|upto
operator|++
argument_list|,
name|other
argument_list|)
expr_stmt|;
block|}
name|allInstances
operator|.
name|subList
argument_list|(
name|upto
argument_list|,
name|allInstances
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
name|allInstances
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|suppressExceptions
specifier|private
name|boolean
name|suppressExceptions
decl_stmt|;
comment|/** Used for testing */
DECL|method|setSuppressExceptions
name|void
name|setSuppressExceptions
parameter_list|()
block|{
name|suppressExceptions
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Used for testing */
DECL|method|clearSuppressExceptions
name|void
name|clearSuppressExceptions
parameter_list|()
block|{
name|suppressExceptions
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Used for testing */
DECL|field|allInstances
specifier|private
specifier|static
name|List
name|allInstances
decl_stmt|;
DECL|method|setTestMode
specifier|public
specifier|static
name|void
name|setTestMode
parameter_list|()
block|{
name|allInstances
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

