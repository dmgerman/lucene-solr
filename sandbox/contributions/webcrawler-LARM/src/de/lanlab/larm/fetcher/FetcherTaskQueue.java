begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
operator|.
name|TaskQueue
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|CachingQueue
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|HashedCircularLinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * this special kind of task queue reorders the incoming tasks so that every subsequent  * task is for a different host.  * This is done by a "HashedCircularLinkedList" which allows random adding while  * a differnet thread iterates through the collection circularly.  *  * @author    Clemens Marschner  * @created   23. November 2001  * @version $Id$  */
end_comment

begin_class
DECL|class|FetcherTaskQueue
specifier|public
class|class
name|FetcherTaskQueue
extends|extends
name|TaskQueue
block|{
comment|/**      * this is a hash that contains an entry for each server, which by itself is a      * CachingQueue that stores all tasks for this server      * @TODO probably link this to the host info structure      */
DECL|field|servers
specifier|private
name|HashedCircularLinkedList
name|servers
init|=
operator|new
name|HashedCircularLinkedList
argument_list|(
literal|100
argument_list|,
literal|0.75f
argument_list|)
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
literal|0
decl_stmt|;
comment|/**      * Constructor for the FetcherTaskQueue object. Does nothing      */
DECL|method|FetcherTaskQueue
specifier|public
name|FetcherTaskQueue
parameter_list|(
name|HostManager
name|manager
parameter_list|)
block|{
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
block|}
comment|/**      * true if no task is queued      *      * @return   The empty value      */
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
operator|(
name|size
operator|==
literal|0
operator|)
return|;
block|}
comment|/**      * clear the queue. not synchronized.      */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|servers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * puts task into Queue.      * Warning: not synchronized      *      * @param t  the task to be added. must be a FetcherTask      */
DECL|method|insert
specifier|public
name|void
name|insert
parameter_list|(
name|Object
name|t
parameter_list|)
block|{
comment|// assert (t != null&& t.getURL() != null)
name|URLMessage
name|um
init|=
operator|(
operator|(
name|FetcherTask
operator|)
name|t
operator|)
operator|.
name|getActURLMessage
argument_list|()
decl_stmt|;
name|URL
name|act
init|=
name|um
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|act
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|Queue
name|q
decl_stmt|;
name|q
operator|=
operator|(
operator|(
name|Queue
operator|)
name|servers
operator|.
name|get
argument_list|(
name|host
argument_list|)
operator|)
expr_stmt|;
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
comment|// add a new host to the queue
comment|//String host2 = host.replace(':', '_').replace('/', '_').replace('\\', '_');
comment|// make it file system ready
comment|// FIXME: put '100' in properties.  This is block size (the number of objects/block)
name|q
operator|=
operator|new
name|CachingQueue
argument_list|(
name|host
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|servers
operator|.
name|put
argument_list|(
name|host
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
comment|// assert((q != null)&& (q instanceof FetcherTaskQueue));
name|q
operator|.
name|insert
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
comment|/**      * the size of the queue. make sure that insert() and size() calls are synchronized      * if the exact number matters.      *      * @return   Description of the Return Value      */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * the number of different hosts queued at the moment      */
DECL|method|getNumHosts
specifier|public
name|int
name|getNumHosts
parameter_list|()
block|{
return|return
name|servers
operator|.
name|size
argument_list|()
return|;
block|}
DECL|field|manager
name|HostManager
name|manager
decl_stmt|;
comment|/**      * get the next task. warning: not synchronized      *      * @return   Description of the Return Value      */
DECL|method|remove
specifier|public
name|Object
name|remove
parameter_list|()
block|{
name|FetcherTask
name|t
init|=
literal|null
decl_stmt|;
name|String
name|start
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|servers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//            while(true)
comment|//            {
name|Queue
name|q
init|=
operator|(
name|Queue
operator|)
name|servers
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|host
init|=
operator|(
name|String
operator|)
name|servers
operator|.
name|getCurrentKey
argument_list|()
decl_stmt|;
comment|//                if(start == null)
comment|//                {
comment|//                    start = host;
comment|//                }
comment|//                else if(host.equals(start))
comment|//                {
comment|//                    System.out.println("FetcherTaskQueue: all hosts busy. waiting 1sec");
comment|//                    try
comment|//                    {
comment|//                        Thread.sleep(1000);
comment|//                    }
comment|//                    catch(InterruptedException e)
comment|//                    {
comment|//                        break;
comment|//                    }
comment|//                }
comment|//                HostInfo hInfo = manager.getHostInfo(host);
comment|//                System.out.println("getting sync on " + hInfo.getHostName());
comment|//                synchronized(hInfo.getLockMonitor())
comment|//                {
comment|//                    if(!hInfo.isBusy())
comment|//                    {
comment|//                        System.out.println("FetcherTaskQueue: host " + host + " ok");
comment|//                        hInfo.obtainLock(); // decreased in FetcherTask
comment|// assert(q != null&& q.size()> 0)
name|t
operator|=
operator|(
name|FetcherTask
operator|)
name|q
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|q
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|servers
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
name|q
operator|=
literal|null
expr_stmt|;
block|}
name|size
operator|--
expr_stmt|;
comment|//                        break;
comment|//                    }
comment|//                    else
comment|//                    {
comment|//                        System.out.println("FetcherTaskQueue: host " + host + " is busy. next...");
comment|//                    }
comment|//                }
comment|//            }
block|}
return|return
name|t
return|;
block|}
comment|/**      * tests      *      * @param args  Description of the Parameter      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
comment|// FIXME: put that into a JUnit test case
comment|//        FetcherTaskQueue q = new FetcherTaskQueue();
comment|//        de.lanlab.larm.net.HostResolver hm = new de.lanlab.larm.net.HostResolver();
comment|//        System.out.println("Test 1. put in 4 yahoos and 3 lmus. pull out LMU/Yahoo/LMU/Yahoo/LMU/Yahoo/Yahoo");
comment|//        try
comment|//        {
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.lmu.de/1"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.lmu.de/2"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/1"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/2"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/3"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/4"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.lmu.de/3"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//        }
comment|//        catch (Throwable t)
comment|//        {
comment|//            t.printStackTrace();
comment|//        }
comment|//
comment|//        System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//        System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//        System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//        System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//        System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//        System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//        System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//
comment|//        System.out.println("Test 2. new Queue");
comment|//        q = new FetcherTaskQueue();
comment|//        System.out.println("size [0]:");
comment|//        System.out.println(q.size());
comment|//        try
comment|//        {
comment|//            System.out.println("put 3 lmus.");
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.lmu.de/1"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.lmu.de/2"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.lmu.de/3"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            System.out.print("pull out 1st element [lmu/1]: ");
comment|//            System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//            System.out.println("size now [2]: " + q.size());
comment|//            System.out.print("pull out 2nd element [lmu/2]: ");
comment|//            System.out.println(((FetcherTask) q.remove()).getInfo());
comment|//            System.out.println("size now [1]: " + q.size());
comment|//            System.out.println("put in 3 yahoos");
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/1"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/2"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/3"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            System.out.println("remove [?]: " + ((FetcherTask) q.remove()).getInfo());
comment|//            System.out.println("Size now [3]: " + q.size());
comment|//            System.out.println("remove [?]: " + ((FetcherTask) q.remove()).getInfo());
comment|//            System.out.println("Size now [2]: " + q.size());
comment|//            System.out.println("remove [?]: " + ((FetcherTask) q.remove()).getInfo());
comment|//            System.out.println("Size now [1]: " + q.size());
comment|//            System.out.println("put in another Yahoo");
comment|//            q.insert(new FetcherTask(new URLMessage(new URL("http://www.yahoo.de/4"), null, URLMessage.LINKTYPE_ANCHOR, null, hm)));
comment|//            System.out.println("remove [?]: " + ((FetcherTask) q.remove()).getInfo());
comment|//            System.out.println("Size now [1]: " + q.size());
comment|//            System.out.println("remove [?]: " + ((FetcherTask) q.remove()).getInfo());
comment|//            System.out.println("Size now [0]: " + q.size());
comment|//        }
comment|//        catch (Throwable t)
comment|//        {
comment|//            t.printStackTrace();
comment|//        }
block|}
block|}
end_class

end_unit

