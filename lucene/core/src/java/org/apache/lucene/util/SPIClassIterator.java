begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceConfigurationError
import|;
end_import

begin_comment
comment|/**  * Helper class for loading SPI classes from classpath (META-INF files).  * This is a light impl of {@link java.util.ServiceLoader} but is guaranteed to  * be bug-free regarding classpath order and does not instantiate or initialize  * the classes found.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|SPIClassIterator
specifier|public
specifier|final
class|class
name|SPIClassIterator
parameter_list|<
name|S
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
block|{
DECL|field|META_INF_SERVICES
specifier|private
specifier|static
specifier|final
name|String
name|META_INF_SERVICES
init|=
literal|"META-INF/services/"
decl_stmt|;
DECL|field|clazz
specifier|private
specifier|final
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
decl_stmt|;
DECL|field|loader
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
DECL|field|profilesEnum
specifier|private
specifier|final
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|profilesEnum
decl_stmt|;
DECL|field|linesIterator
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|linesIterator
decl_stmt|;
comment|/** Creates a new SPI iterator to lookup services of type {@code clazz} using    * the same {@link ClassLoader} as the argument. */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|S
parameter_list|>
name|SPIClassIterator
argument_list|<
name|S
argument_list|>
name|get
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
operator|new
name|SPIClassIterator
argument_list|<>
argument_list|(
name|clazz
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|clazz
operator|.
name|getClassLoader
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|clazz
operator|+
literal|" has no classloader."
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a new SPI iterator to lookup services of type {@code clazz} using the given classloader. */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|S
parameter_list|>
name|SPIClassIterator
argument_list|<
name|S
argument_list|>
name|get
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
block|{
return|return
operator|new
name|SPIClassIterator
argument_list|<>
argument_list|(
name|clazz
argument_list|,
name|loader
argument_list|)
return|;
block|}
comment|/**    * Utility method to check if some class loader is a (grand-)parent of or the same as another one.    * This means the child will be able to load all classes from the parent, too.    *<p>    * If caller's codesource doesn't have enough permissions to do the check, {@code false} is returned    * (this is fine, because if we get a {@code SecurityException} it is for sure no parent).    */
DECL|method|isParentClassLoader
specifier|public
specifier|static
name|boolean
name|isParentClassLoader
parameter_list|(
specifier|final
name|ClassLoader
name|parent
parameter_list|,
specifier|final
name|ClassLoader
name|child
parameter_list|)
block|{
try|try
block|{
name|ClassLoader
name|cl
init|=
name|child
decl_stmt|;
while|while
condition|(
name|cl
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|cl
operator|==
name|parent
condition|)
block|{
return|return
literal|true
return|;
block|}
name|cl
operator|=
name|cl
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|SPIClassIterator
specifier|private
name|SPIClassIterator
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|clazz
argument_list|,
literal|"clazz"
argument_list|)
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|loader
argument_list|,
literal|"loader"
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|String
name|fullName
init|=
name|META_INF_SERVICES
operator|+
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
name|this
operator|.
name|profilesEnum
operator|=
name|loader
operator|.
name|getResources
argument_list|(
name|fullName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceConfigurationError
argument_list|(
literal|"Error loading SPI profiles for type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" from classpath"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|this
operator|.
name|linesIterator
operator|=
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|loadNextProfile
specifier|private
name|boolean
name|loadNextProfile
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|lines
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|profilesEnum
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
if|if
condition|(
name|lines
operator|!=
literal|null
condition|)
block|{
name|lines
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|lines
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
specifier|final
name|URL
name|url
init|=
name|profilesEnum
operator|.
name|nextElement
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|InputStream
name|in
init|=
name|url
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|pos
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|>=
literal|0
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceConfigurationError
argument_list|(
literal|"Error loading SPI class list from URL: "
operator|+
name|url
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|lines
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|linesIterator
operator|=
name|lines
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|linesIterator
operator|.
name|hasNext
argument_list|()
operator|||
name|loadNextProfile
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|next
parameter_list|()
block|{
comment|// hasNext() implicitely loads the next profile, so it is essential to call this here!
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|linesIterator
operator|.
name|hasNext
argument_list|()
assert|;
specifier|final
name|String
name|c
init|=
name|linesIterator
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
comment|// don't initialize the class (pass false as 2nd parameter):
return|return
name|Class
operator|.
name|forName
argument_list|(
name|c
argument_list|,
literal|false
argument_list|,
name|loader
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|clazz
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceConfigurationError
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"An SPI class of type %s with classname %s does not exist, "
operator|+
literal|"please fix the file '%s%1$s' in your classpath."
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|c
argument_list|,
name|META_INF_SERVICES
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

