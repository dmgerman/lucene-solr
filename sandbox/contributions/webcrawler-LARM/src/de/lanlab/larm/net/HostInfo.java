begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  ====================================================================  *  The Apache Software License, Version 1.1  *  *  Copyright (c) 2001 The Apache Software Foundation.  All rights  *  reserved.  *  *  Redistribution and use in source and binary forms, with or without  *  modification, are permitted provided that the following conditions  *  are met:  *  *  1. Redistributions of source code must retain the above copyright  *  notice, this list of conditions and the following disclaimer.  *  *  2. Redistributions in binary form must reproduce the above copyright  *  notice, this list of conditions and the following disclaimer in  *  the documentation and/or other materials provided with the  *  distribution.  *  *  3. The end-user documentation included with the redistribution,  *  if any, must include the following acknowledgment:  *  "This product includes software developed by the  *  Apache Software Foundation (http://www.apache.org/)."  *  Alternately, this acknowledgment may appear in the software itself,  *  if and wherever such third-party acknowledgments normally appear.  *  *  4. The names "Apache" and "Apache Software Foundation" and  *  "Apache Lucene" must not be used to endorse or promote products  *  derived from this software without prior written permission. For  *  written permission, please contact apache@apache.org.  *  *  5. Products derived from this software may not be called "Apache",  *  "Apache Lucene", nor may "Apache" appear in their name, without  *  prior written permission of the Apache Software Foundation.  *  *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  *  SUCH DAMAGE.  *  ====================================================================  *  *  This software consists of voluntary contributions made by many  *  individuals on behalf of the Apache Software Foundation.  For more  *  information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_package
DECL|package|de.lanlab.larm.net
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|net
package|;
end_package

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
name|net
operator|.
name|*
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
name|Queue
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
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * Contains information about a host. If a host doesn't respond too often, it's  * excluded from the crawl. This class is used by the HostManager.  * TODO: there needs to be a way to re-include the host in the crawl.  Perhaps  * all hosts marked as unhealthy should be checked periodically and marked  * healthy again, if they respond.  *  * @author    Clemens Marschner  * @created   16. Februar 2002  * @version   $Id$  */
end_comment

begin_class
DECL|class|HostInfo
specifier|public
class|class
name|HostInfo
block|{
DECL|field|emptyKeepOutDirectories
specifier|final
specifier|static
name|String
index|[]
name|emptyKeepOutDirectories
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|field|healthyCount
name|int
name|healthyCount
init|=
literal|8
decl_stmt|;
DECL|field|locks
name|int
name|locks
init|=
literal|2
decl_stmt|;
comment|// max. concurrent requests
DECL|field|lockObtained
name|int
name|lockObtained
init|=
literal|0
decl_stmt|;
comment|// for debugging
DECL|field|lockMonitor
name|Object
name|lockMonitor
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|getLockMonitor
specifier|public
name|Object
name|getLockMonitor
parameter_list|()
block|{
return|return
name|lockMonitor
return|;
block|}
DECL|method|releaseLock
specifier|public
name|void
name|releaseLock
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lockMonitor
init|)
block|{
if|if
condition|(
name|lockObtained
operator|>=
literal|0
condition|)
block|{
name|locks
operator|++
expr_stmt|;
name|lockObtained
operator|--
expr_stmt|;
comment|//                try
comment|//                {
comment|//                    throw new Exception();
comment|//
comment|//                }
comment|//                catch(Exception e)
comment|//                {
comment|//                    System.out.println("HostInfo: release called at: " + e.getStackTrace()[1]);
comment|//                }
comment|//                System.out.println("HostInfo " + hostName + ": releaseing Lock. now " + lockObtained + " locks obtained, " + locks + " available");
block|}
comment|//            else
comment|//            {
comment|//                System.out.println("HostInfo: lock released although no lock acquired!?");
comment|//            }
block|}
block|}
comment|// must be synchronized
DECL|method|obtainLock
specifier|public
name|void
name|obtainLock
parameter_list|()
block|{
name|locks
operator|--
expr_stmt|;
name|lockObtained
operator|++
expr_stmt|;
comment|//        try
comment|//        {
comment|//            throw new Exception();
comment|//
comment|//        }
comment|//        catch(Exception e)
comment|//        {
comment|//            System.out.println("obtain called at: " + e.getStackTrace()[1]);
comment|//        }
comment|//        System.out.println("HostInfo " + hostName + ": obtaining Lock. now " + lockObtained + " locks obtained, " + locks + " available");
block|}
comment|// must be synchronized
DECL|method|isBusy
specifier|public
name|boolean
name|isBusy
parameter_list|()
block|{
return|return
name|locks
operator|<=
literal|0
return|;
block|}
comment|// five strikes, and you're out
DECL|field|isReachable
specifier|private
name|boolean
name|isReachable
init|=
literal|true
decl_stmt|;
DECL|field|robotTxtChecked
specifier|private
name|boolean
name|robotTxtChecked
init|=
literal|false
decl_stmt|;
DECL|field|disallows
specifier|private
name|String
index|[]
name|disallows
decl_stmt|;
comment|// robot exclusion
DECL|field|isLoadingRobotsTxt
specifier|private
name|boolean
name|isLoadingRobotsTxt
init|=
literal|false
decl_stmt|;
DECL|field|queuedRequests
specifier|private
name|Queue
name|queuedRequests
init|=
literal|null
decl_stmt|;
comment|// robot exclusion
DECL|field|hostName
specifier|private
name|String
name|hostName
decl_stmt|;
comment|//LinkedList synonyms = new LinkedList();
comment|/**      * Constructor for the HostInfo object      *      * @param hostName  Description of the Parameter      * @param id        Description of the Parameter      */
DECL|method|HostInfo
specifier|public
name|HostInfo
parameter_list|(
name|String
name|hostName
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|disallows
operator|=
name|HostInfo
operator|.
name|emptyKeepOutDirectories
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
block|}
comment|/**      * Description of the Method      */
DECL|method|removeQueue
specifier|public
name|void
name|removeQueue
parameter_list|()
block|{
name|queuedRequests
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Gets the id attribute of the HostInfo object      *      * @return   The id value      */
DECL|method|getId
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Description of the Method      *      * @param message  Description of the Parameter      */
DECL|method|insertIntoQueue
specifier|public
name|void
name|insertIntoQueue
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|queuedRequests
operator|.
name|insert
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the hostName attribute of the HostInfo object      *      * @return   The hostName value      */
DECL|method|getHostName
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
comment|/**      * Gets the queueSize. No error checking is done when the queue is null      *      * @return   The queueSize value      */
DECL|method|getQueueSize
specifier|public
name|int
name|getQueueSize
parameter_list|()
block|{
return|return
name|queuedRequests
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * gets last entry from queue. No error checking is done when the queue is null      *      * @return   Description of the Return Value      */
DECL|method|removeFromQueue
specifier|public
name|Message
name|removeFromQueue
parameter_list|()
block|{
return|return
operator|(
name|Message
operator|)
name|queuedRequests
operator|.
name|remove
argument_list|()
return|;
block|}
comment|/**      * is this host reachable and responding?      *      * @return   The healthy value      */
DECL|method|isHealthy
specifier|public
name|boolean
name|isHealthy
parameter_list|()
block|{
return|return
operator|(
name|healthyCount
operator|>
literal|0
operator|)
operator|&&
name|isReachable
return|;
block|}
comment|/**      * signals that the host returned with a bad request of whatever type      */
DECL|method|badRequest
specifier|public
name|void
name|badRequest
parameter_list|()
block|{
name|healthyCount
operator|--
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"HostInfo: "
operator|+
name|this
operator|.
name|hostName
operator|+
literal|": badRequest. "
operator|+
name|healthyCount
operator|+
literal|" left"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the reachable attribute of the HostInfo object      *      * @param reachable  The new reachable value      */
DECL|method|setReachable
specifier|public
name|void
name|setReachable
parameter_list|(
name|boolean
name|reachable
parameter_list|)
block|{
name|isReachable
operator|=
name|reachable
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"HostInfo: "
operator|+
name|this
operator|.
name|hostName
operator|+
literal|": setting to "
operator|+
operator|(
name|reachable
condition|?
literal|"reachable"
else|:
literal|"unreachable"
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the reachable attribute of the HostInfo object      *      * @return   The reachable value      */
DECL|method|isReachable
specifier|public
name|boolean
name|isReachable
parameter_list|()
block|{
return|return
name|isReachable
return|;
block|}
comment|/**      * Gets the robotTxtChecked attribute of the HostInfo object      *      * @return   The robotTxtChecked value      */
DECL|method|isRobotTxtChecked
specifier|public
name|boolean
name|isRobotTxtChecked
parameter_list|()
block|{
return|return
name|robotTxtChecked
return|;
block|}
comment|/**      * must be synchronized externally      *      * @return   The loadingRobotsTxt value      */
DECL|method|isLoadingRobotsTxt
specifier|public
name|boolean
name|isLoadingRobotsTxt
parameter_list|()
block|{
return|return
name|this
operator|.
name|isLoadingRobotsTxt
return|;
block|}
comment|/**      * Sets the loadingRobotsTxt attribute of the HostInfo object      *      * @param isLoading  The new loadingRobotsTxt value      */
DECL|method|setLoadingRobotsTxt
specifier|public
name|void
name|setLoadingRobotsTxt
parameter_list|(
name|boolean
name|isLoading
parameter_list|)
block|{
name|this
operator|.
name|isLoadingRobotsTxt
operator|=
name|isLoading
expr_stmt|;
if|if
condition|(
name|isLoading
condition|)
block|{
comment|// FIXME: move '100' to properties
name|this
operator|.
name|queuedRequests
operator|=
operator|new
name|CachingQueue
argument_list|(
literal|"HostInfo_"
operator|+
name|id
operator|+
literal|"_QueuedRequests"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the robotsChecked attribute of the HostInfo object      *      * @param isChecked  The new robotsChecked value      * @param disallows  The new robotsChecked value      */
DECL|method|setRobotsChecked
specifier|public
name|void
name|setRobotsChecked
parameter_list|(
name|boolean
name|isChecked
parameter_list|,
name|String
index|[]
name|disallows
parameter_list|)
block|{
name|this
operator|.
name|robotTxtChecked
operator|=
name|isChecked
expr_stmt|;
if|if
condition|(
name|disallows
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|disallows
operator|=
name|disallows
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|disallows
operator|=
name|emptyKeepOutDirectories
expr_stmt|;
block|}
block|}
comment|/**      * Gets the allowed attribute of the HostInfo object      *      * @param path  Description of the Parameter      * @return      The allowed value      */
DECL|method|isAllowed
specifier|public
specifier|synchronized
name|boolean
name|isAllowed
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// assume keepOutDirectories is pretty short
comment|// assert disallows != null
name|int
name|length
init|=
name|disallows
operator|.
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|disallows
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

