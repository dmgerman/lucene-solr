begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|net
operator|.
name|*
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
comment|/**  *  Description of the Class  *  *@author     cmarschn  *@created    2. Mai 2001  */
end_comment

begin_class
DECL|class|HttpTimeoutHandler
specifier|public
class|class
name|HttpTimeoutHandler
extends|extends
name|sun
operator|.
name|net
operator|.
name|www
operator|.
name|protocol
operator|.
name|http
operator|.
name|Handler
block|{
DECL|field|timeoutVal
name|int
name|timeoutVal
decl_stmt|;
DECL|field|fHUCT
name|HttpURLConnectionTimeout
name|fHUCT
decl_stmt|;
comment|/**      *  Constructor for the HttpTimeoutHandler object      *      *@param  iT  Description of Parameter      */
DECL|method|HttpTimeoutHandler
specifier|public
name|HttpTimeoutHandler
parameter_list|(
name|int
name|iT
parameter_list|)
block|{
name|timeoutVal
operator|=
name|iT
expr_stmt|;
block|}
comment|/**      *  Gets the Socket attribute of the HttpTimeoutHandler object      *      *@return    The Socket value      */
DECL|method|getSocket
specifier|public
name|Socket
name|getSocket
parameter_list|()
block|{
return|return
name|fHUCT
operator|.
name|getSocket
argument_list|()
return|;
block|}
comment|/**      *  Description of the Method      *      *@exception  Exception  Description of Exception      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|fHUCT
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  u                Description of Parameter      *@return                  Description of the Returned Value      *@exception  IOException  Description of Exception      */
DECL|method|openConnection
specifier|protected
name|java
operator|.
name|net
operator|.
name|URLConnection
name|openConnection
parameter_list|(
name|URL
name|u
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fHUCT
operator|=
operator|new
name|HttpURLConnectionTimeout
argument_list|(
name|u
argument_list|,
name|this
argument_list|,
name|timeoutVal
argument_list|)
return|;
block|}
comment|/**      *  Gets the Proxy attribute of the HttpTimeoutHandler object      *      *@return    The Proxy value      */
DECL|method|getProxy
name|String
name|getProxy
parameter_list|()
block|{
return|return
name|proxy
return|;
comment|// breaking encapsulation
block|}
comment|/**      *  Gets the ProxyPort attribute of the HttpTimeoutHandler object      *      *@return    The ProxyPort value      */
DECL|method|getProxyPort
name|int
name|getProxyPort
parameter_list|()
block|{
return|return
name|proxyPort
return|;
comment|// breaking encapsulation
block|}
block|}
end_class

end_unit

