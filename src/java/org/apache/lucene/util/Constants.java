begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_comment
comment|/**  * Some useful constants.  *  * @author  Doug Cutting  * @version $Id$  **/
end_comment

begin_class
DECL|class|Constants
specifier|public
specifier|final
class|class
name|Constants
block|{
DECL|method|Constants
specifier|private
name|Constants
parameter_list|()
block|{}
comment|// can't construct
comment|/** The value of<tt>System.getProperty("java.version")<tt>. **/
DECL|field|JAVA_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_VERSION
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
decl_stmt|;
comment|/** True iff this is Java version 1.1. */
DECL|field|JAVA_1_1
specifier|public
specifier|static
specifier|final
name|boolean
name|JAVA_1_1
init|=
name|JAVA_VERSION
operator|.
name|startsWith
argument_list|(
literal|"1.1."
argument_list|)
decl_stmt|;
comment|/** True iff this is Java version 1.2. */
DECL|field|JAVA_1_2
specifier|public
specifier|static
specifier|final
name|boolean
name|JAVA_1_2
init|=
name|JAVA_VERSION
operator|.
name|startsWith
argument_list|(
literal|"1.2."
argument_list|)
decl_stmt|;
comment|/** True iff this is Java version 1.3. */
DECL|field|JAVA_1_3
specifier|public
specifier|static
specifier|final
name|boolean
name|JAVA_1_3
init|=
name|JAVA_VERSION
operator|.
name|startsWith
argument_list|(
literal|"1.3."
argument_list|)
decl_stmt|;
comment|/** The value of<tt>System.getProperty("os.name")<tt>. **/
DECL|field|OS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|OS_NAME
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
comment|/** True iff running on Linux. */
DECL|field|LINUX
specifier|public
specifier|static
specifier|final
name|boolean
name|LINUX
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"Linux"
argument_list|)
decl_stmt|;
comment|/** True iff running on Windows. */
DECL|field|WINDOWS
specifier|public
specifier|static
specifier|final
name|boolean
name|WINDOWS
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
decl_stmt|;
comment|/** True iff running on SunOS. */
DECL|field|SUN_OS
specifier|public
specifier|static
specifier|final
name|boolean
name|SUN_OS
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"SunOS"
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

