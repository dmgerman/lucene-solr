begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|search.contenthandler
package|package
name|search
operator|.
name|contenthandler
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
name|log4j
operator|.
name|Category
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|search
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Factory responsible for obtaining ContentHandlers.  *  * @author<a href="mailto:kelvin@relevanz.com">Kelvin Tan</a>  */
end_comment

begin_class
DECL|class|FileContentHandlerFactory
specifier|public
specifier|abstract
class|class
name|FileContentHandlerFactory
block|{
DECL|field|DEFAULT_HANDLER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_HANDLER_KEY
init|=
literal|"DEFAULT"
decl_stmt|;
DECL|field|cat
specifier|static
name|Category
name|cat
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|FileContentHandlerFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|handlerRegistry
specifier|private
specifier|static
name|Map
name|handlerRegistry
decl_stmt|;
DECL|method|getContentHandler
specifier|public
specifier|static
name|FileContentHandler
name|getContentHandler
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|String
name|extension
init|=
name|IOUtils
operator|.
name|getFileExtension
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|handlerRegistry
operator|.
name|containsKey
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|String
name|handlerClassname
init|=
operator|(
name|String
operator|)
name|handlerRegistry
operator|.
name|get
argument_list|(
name|extension
argument_list|)
decl_stmt|;
return|return
operator|(
name|FileContentHandler
operator|)
name|generateObject
argument_list|(
name|handlerClassname
argument_list|,
operator|new
name|Class
index|[]
block|{
name|File
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|f
block|}
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|handlerRegistry
operator|.
name|containsKey
argument_list|(
name|DEFAULT_HANDLER_KEY
argument_list|)
condition|)
block|{
name|String
name|handlerClassname
init|=
operator|(
name|String
operator|)
name|handlerRegistry
operator|.
name|get
argument_list|(
name|DEFAULT_HANDLER_KEY
argument_list|)
decl_stmt|;
return|return
operator|(
name|FileContentHandler
operator|)
name|generateObject
argument_list|(
name|handlerClassname
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|NullHandler
operator|.
name|getInstance
argument_list|()
return|;
block|}
block|}
DECL|method|setHandlerRegistry
specifier|public
specifier|static
name|void
name|setHandlerRegistry
parameter_list|(
name|Map
name|handlerRegistry
parameter_list|)
block|{
name|FileContentHandlerFactory
operator|.
name|handlerRegistry
operator|=
name|handlerRegistry
expr_stmt|;
block|}
comment|/**      * Utility method to return an object based on its class name.      * The object needs to have a constructor which accepts no parameters.      *      * @param className  Class name of object to be generated      * @return Object      */
DECL|method|generateObject
specifier|private
specifier|static
name|Object
name|generateObject
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|Object
name|o
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Class
name|c
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|o
operator|=
name|c
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|cnfe
operator|.
name|getMessage
argument_list|()
operator|+
literal|" No class named '"
operator|+
name|className
operator|+
literal|"' was found."
argument_list|,
name|cnfe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|ie
operator|.
name|getMessage
argument_list|()
operator|+
literal|" Class named '"
operator|+
name|className
operator|+
literal|"' could not be  instantiated."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|iae
operator|.
name|getMessage
argument_list|()
operator|+
literal|" No access to class named '"
operator|+
name|className
operator|+
literal|"'."
argument_list|,
name|iae
argument_list|)
expr_stmt|;
block|}
return|return
name|o
return|;
block|}
comment|/**      * Utility method to return an object based on its class name.      *      * @param type  Class name of object to be generated      * @param clazz Class array of parameters.      * @param args Object array of arguments.      * @return Object      */
DECL|method|generateObject
specifier|private
specifier|static
name|Object
name|generateObject
parameter_list|(
name|String
name|className
parameter_list|,
name|Class
index|[]
name|clazz
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
block|{
name|Object
name|o
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Class
name|c
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|Constructor
name|con
init|=
name|c
operator|.
name|getConstructor
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|con
operator|!=
literal|null
condition|)
block|{
name|o
operator|=
name|con
operator|.
name|newInstance
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|InstantiationException
argument_list|(
literal|"Constructor with arguments:"
operator|+
name|clazz
operator|.
name|toString
argument_list|()
operator|+
literal|" non-existent."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|cnfe
operator|.
name|getMessage
argument_list|()
operator|+
literal|" No class named '"
operator|+
name|className
operator|+
literal|"' was found."
argument_list|,
name|cnfe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|ie
operator|.
name|getMessage
argument_list|()
operator|+
literal|" Class named '"
operator|+
name|className
operator|+
literal|"' could not be  instantiated."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|iae
operator|.
name|getMessage
argument_list|()
operator|+
literal|" No access to class named '"
operator|+
name|className
operator|+
literal|"'."
argument_list|,
name|iae
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|nsme
operator|.
name|getMessage
argument_list|()
operator|+
literal|" No method in class named '"
operator|+
name|className
operator|+
literal|"'."
argument_list|,
name|nsme
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
name|ite
operator|.
name|getMessage
argument_list|()
operator|+
literal|" in class named '"
operator|+
name|className
operator|+
literal|"'."
argument_list|,
name|ite
argument_list|)
expr_stmt|;
block|}
return|return
name|o
return|;
block|}
block|}
end_class

end_unit

