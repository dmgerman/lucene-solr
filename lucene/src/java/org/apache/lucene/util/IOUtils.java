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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_comment
comment|/** This class emulates the new Java 7 "Try-With-Resources" statement.  * Remove once Lucene is on Java 7.  * @lucene.internal */
end_comment

begin_class
DECL|class|IOUtils
specifier|public
specifier|final
class|class
name|IOUtils
block|{
DECL|method|IOUtils
specifier|private
name|IOUtils
parameter_list|()
block|{}
comment|// no instance
comment|/**    *<p>Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions. Some of the<tt>Closeable</tt>s    * may be null, they are ignored. After everything is closed, method either throws<tt>priorException</tt>,    * if one is supplied, or the first of suppressed exceptions, or completes normally.</p>    *<p>Sample usage:<br/>    *<pre>    * Closeable resource1 = null, resource2 = null, resource3 = null;    * ExpectedException priorE = null;    * try {    *   resource1 = ...; resource2 = ...; resource3 = ...; // Acquisition may throw ExpectedException    *   ..do..stuff.. // May throw ExpectedException    * } catch (ExpectedException e) {    *   priorE = e;    * } finally {    *   closeSafely(priorE, resource1, resource2, resource3);    * }    *</pre>    *</p>    * @param priorException<tt>null</tt> or an exception that will be rethrown after method completion    * @param objects         objects to call<tt>close()</tt> on    */
DECL|method|closeSafely
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|void
name|closeSafely
parameter_list|(
name|E
name|priorException
parameter_list|,
name|Closeable
modifier|...
name|objects
parameter_list|)
throws|throws
name|E
throws|,
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
operator|(
name|priorException
operator|==
literal|null
operator|)
condition|?
name|th
else|:
name|priorException
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|priorException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|priorException
throw|;
block|}
elseif|else
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/** @see #closeSafely(Exception, Closeable...) */
DECL|method|closeSafely
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|void
name|closeSafely
parameter_list|(
name|E
name|priorException
parameter_list|,
name|Iterable
argument_list|<
name|Closeable
argument_list|>
name|objects
parameter_list|)
throws|throws
name|E
throws|,
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
operator|(
name|priorException
operator|==
literal|null
operator|)
condition|?
name|th
else|:
name|priorException
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|priorException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|priorException
throw|;
block|}
elseif|else
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/**    * Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions.    * Some of the<tt>Closeable</tt>s may be null, they are ignored. After    * everything is closed, and if {@code suppressExceptions} is {@code false},    * method either throws the first of suppressed exceptions, or completes    * normally.    *     * @param suppressExceptions    *          if true then exceptions that occur during close() are suppressed    * @param objects    *          objects to call<tt>close()</tt> on    */
DECL|method|closeSafely
specifier|public
specifier|static
name|void
name|closeSafely
parameter_list|(
name|boolean
name|suppressExceptions
parameter_list|,
name|Closeable
modifier|...
name|objects
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
name|th
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
if|if
condition|(
name|th
operator|!=
literal|null
operator|&&
operator|!
name|suppressExceptions
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/**    * @see #closeSafely(boolean, Closeable...)    */
DECL|method|closeSafely
specifier|public
specifier|static
name|void
name|closeSafely
parameter_list|(
name|boolean
name|suppressExceptions
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|objects
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
name|th
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
if|if
condition|(
name|th
operator|!=
literal|null
operator|&&
operator|!
name|suppressExceptions
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/** This reflected {@link Method} is {@code null} before Java 7 */
DECL|field|SUPPRESS_METHOD
specifier|private
specifier|static
specifier|final
name|Method
name|SUPPRESS_METHOD
decl_stmt|;
static|static
block|{
name|Method
name|m
decl_stmt|;
try|try
block|{
name|m
operator|=
name|Throwable
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"addSuppressed"
argument_list|,
name|Throwable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|m
operator|=
literal|null
expr_stmt|;
block|}
name|SUPPRESS_METHOD
operator|=
name|m
expr_stmt|;
block|}
comment|/** adds a Throwable to the list of suppressed Exceptions of the first Throwable (if Java 7 is detected)    * @param exception this exception should get the suppressed one added    * @param suppressed the suppressed exception    */
DECL|method|addSuppressed
specifier|private
specifier|static
specifier|final
name|void
name|addSuppressed
parameter_list|(
name|Throwable
name|exception
parameter_list|,
name|Throwable
name|suppressed
parameter_list|)
block|{
if|if
condition|(
name|SUPPRESS_METHOD
operator|!=
literal|null
operator|&&
name|exception
operator|!=
literal|null
operator|&&
name|suppressed
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|SUPPRESS_METHOD
operator|.
name|invoke
argument_list|(
name|exception
argument_list|,
name|suppressed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore any exceptions caused by invoking (e.g. security constraints)
block|}
block|}
block|}
block|}
end_class

end_unit

