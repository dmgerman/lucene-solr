begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|search
package|package
name|search
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|torque
operator|.
name|util
operator|.
name|Criteria
import|;
end_import

begin_comment
comment|/**  * Thrown when a Torque-persisted object can't be found when it is expected.  *  * @author<a href="mailto:soonping@relevanz.com">Phang Soon-Ping</a>  */
end_comment

begin_class
DECL|class|ObjectNotFoundException
specifier|public
class|class
name|ObjectNotFoundException
extends|extends
name|Exception
block|{
DECL|method|ObjectNotFoundException
specifier|public
name|ObjectNotFoundException
parameter_list|(
name|Class
name|objectClass
parameter_list|,
name|String
name|objectId
parameter_list|)
block|{
name|super
argument_list|(
name|generateMessage
argument_list|(
name|objectClass
argument_list|,
name|objectId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ObjectNotFoundException
specifier|public
name|ObjectNotFoundException
parameter_list|(
name|Criteria
name|crit
parameter_list|)
block|{
name|super
argument_list|(
literal|"Criteria :'"
operator|+
name|crit
operator|.
name|toString
argument_list|()
operator|+
literal|"' did not result in any object "
operator|+
literal|" when one was expected."
argument_list|)
expr_stmt|;
block|}
DECL|method|generateMessage
specifier|private
specifier|static
name|String
name|generateMessage
parameter_list|(
name|Class
name|c
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|String
name|cName
init|=
name|c
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|lastDot
init|=
name|cName
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDot
operator|<
literal|0
condition|)
block|{
return|return
name|cName
operator|+
literal|" with id "
operator|+
name|id
operator|+
literal|" not found."
return|;
block|}
else|else
block|{
return|return
name|cName
operator|.
name|substring
argument_list|(
name|lastDot
operator|+
literal|1
argument_list|)
operator|+
literal|" with ID \""
operator|+
name|id
operator|+
literal|"\" not found."
return|;
block|}
block|}
block|}
end_class

end_unit

