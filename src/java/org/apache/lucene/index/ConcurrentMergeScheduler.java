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
name|LinkedList
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
comment|/** A {@link MergeScheduler} that runs each merge using a  *  separate thread, up until a maximum number of threads  *  ({@link #setMaxThreadCount}) at which points merges are  *  run in the foreground, serially.  This is a simple way  *  to use concurrency in the indexing process without  *  having to create and manage application level  *  threads. */
end_comment

begin_class
DECL|class|ConcurrentMergeScheduler
specifier|public
class|class
name|ConcurrentMergeScheduler
implements|implements
name|MergeScheduler
block|{
DECL|field|VERBOSE
specifier|public
specifier|static
name|boolean
name|VERBOSE
init|=
literal|false
decl_stmt|;
DECL|field|mergeThreadPriority
specifier|private
name|int
name|mergeThreadPriority
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|mergeThreads
specifier|private
name|List
name|mergeThreads
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
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
specifier|private
name|Directory
name|dir
decl_stmt|;
comment|/** Sets the max # simultaneous threads that may be    *  running.  If a merge is necessary yet we already have    *  this many threads running, the merge is returned back    *  to IndexWriter so that it runs in the "foreground". */
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
name|mergeThreadPriority
operator|=
name|pri
expr_stmt|;
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
try|try
block|{
name|merge
operator|.
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
block|}
block|}
comment|/** Returns any exceptions that were caught in the merge    *  threads. */
DECL|method|getExceptions
specifier|public
name|List
name|getExceptions
parameter_list|()
block|{
return|return
name|exceptions
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CMS ["
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]: "
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
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
DECL|method|finishThreads
specifier|private
specifier|synchronized
name|void
name|finishThreads
parameter_list|()
block|{
while|while
condition|(
name|mergeThreads
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mergeThreads
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|MergeThread
name|mergeThread
init|=
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
decl_stmt|;
name|message
argument_list|(
literal|"    "
operator|+
name|i
operator|+
literal|": "
operator|+
name|mergeThread
operator|.
name|merge
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|e
parameter_list|)
block|{       }
block|}
block|}
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|()
block|{
name|finishThreads
argument_list|()
expr_stmt|;
block|}
comment|// Used for testing
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
name|VERBOSE
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
comment|// pending merges, until its empty:
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
name|VERBOSE
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
if|if
condition|(
name|VERBOSE
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
if|if
condition|(
name|merge
operator|.
name|isExternal
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|message
argument_list|(
literal|"    merge involves segments from an external directory; now run in foreground"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|mergeThreads
operator|.
name|size
argument_list|()
operator|<
name|maxThreadCount
condition|)
block|{
comment|// OK to spawn a new merge thread to handle this
comment|// merge:
name|MergeThread
name|merger
init|=
operator|new
name|MergeThread
argument_list|(
name|writer
argument_list|,
name|merge
argument_list|)
decl_stmt|;
name|mergeThreads
operator|.
name|add
argument_list|(
name|merger
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
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
try|try
block|{
name|merger
operator|.
name|setPriority
argument_list|(
name|mergeThreadPriority
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
name|merger
operator|.
name|start
argument_list|()
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|VERBOSE
condition|)
name|message
argument_list|(
literal|"    too many merge threads running; run merge in foreground"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Too many merge threads already running, so we do
comment|// this in the foreground of the calling thread
name|writer
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MergeThread
specifier|private
class|class
name|MergeThread
extends|extends
name|Thread
block|{
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|merge
name|MergePolicy
operator|.
name|OneMerge
name|merge
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
name|merge
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
name|merge
operator|=
name|merge
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|message
argument_list|(
literal|"  merge thread: start"
argument_list|)
expr_stmt|;
comment|// First time through the while loop we do the merge
comment|// that we were started with:
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|this
operator|.
name|merge
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|writer
operator|.
name|merge
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
name|VERBOSE
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
name|VERBOSE
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
comment|// When a merge was aborted& IndexWriter closed,
comment|// it's possible to get various IOExceptions,
comment|// NullPointerExceptions, AlreadyClosedExceptions:
name|merge
operator|.
name|setException
argument_list|(
name|exc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addMergeException
argument_list|(
name|merge
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|merge
operator|.
name|isAborted
argument_list|()
condition|)
block|{
comment|// If the merge was not aborted then the exception
comment|// is real
name|exceptions
operator|.
name|add
argument_list|(
name|exc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|suppressExceptions
condition|)
comment|// suppressExceptions is normally only set during
comment|// testing.
throw|throw
operator|new
name|MergePolicy
operator|.
name|MergeException
argument_list|(
name|exc
argument_list|)
throw|;
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
name|mergeThreads
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ConcurrentMergeScheduler
operator|.
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
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
block|}
end_class

end_unit

