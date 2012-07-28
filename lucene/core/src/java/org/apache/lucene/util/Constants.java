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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|LucenePackage
import|;
end_import

begin_comment
comment|/**  * Some useful constants.  **/
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
comment|/** JVM vendor info. */
DECL|field|JVM_VENDOR
specifier|public
specifier|static
specifier|final
name|String
name|JVM_VENDOR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.vendor"
argument_list|)
decl_stmt|;
DECL|field|JVM_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|JVM_VERSION
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.version"
argument_list|)
decl_stmt|;
DECL|field|JVM_NAME
specifier|public
specifier|static
specifier|final
name|String
name|JVM_NAME
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.name"
argument_list|)
decl_stmt|;
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
comment|/** True iff running on Mac OS X */
DECL|field|MAC_OS_X
specifier|public
specifier|static
specifier|final
name|boolean
name|MAC_OS_X
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"Mac OS X"
argument_list|)
decl_stmt|;
DECL|field|OS_ARCH
specifier|public
specifier|static
specifier|final
name|String
name|OS_ARCH
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
decl_stmt|;
DECL|field|OS_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|OS_VERSION
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.version"
argument_list|)
decl_stmt|;
DECL|field|JAVA_VENDOR
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_VENDOR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
decl_stmt|;
comment|/** @deprecated With Lucene 4.0, we are always on Java 6 */
annotation|@
name|Deprecated
DECL|field|JRE_IS_MINIMUM_JAVA6
specifier|public
specifier|static
specifier|final
name|boolean
name|JRE_IS_MINIMUM_JAVA6
init|=
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
comment|// prevent inlining in foreign class files
DECL|field|JRE_IS_MINIMUM_JAVA7
specifier|public
specifier|static
specifier|final
name|boolean
name|JRE_IS_MINIMUM_JAVA7
decl_stmt|;
comment|/** True iff running on a 64bit JVM */
DECL|field|JRE_IS_64BIT
specifier|public
specifier|static
specifier|final
name|boolean
name|JRE_IS_64BIT
decl_stmt|;
static|static
block|{
name|boolean
name|is64Bit
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|unsafeClass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Unsafe"
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|unsafeField
init|=
name|unsafeClass
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|unsafeField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|unsafe
init|=
name|unsafeField
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|int
name|addressSize
init|=
operator|(
operator|(
name|Number
operator|)
name|unsafeClass
operator|.
name|getMethod
argument_list|(
literal|"addressSize"
argument_list|)
operator|.
name|invoke
argument_list|(
name|unsafe
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
comment|//System.out.println("Address size: " + addressSize);
name|is64Bit
operator|=
name|addressSize
operator|>=
literal|8
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|String
name|x
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|!=
literal|null
condition|)
block|{
name|is64Bit
operator|=
name|x
operator|.
name|indexOf
argument_list|(
literal|"64"
argument_list|)
operator|!=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|OS_ARCH
operator|!=
literal|null
operator|&&
name|OS_ARCH
operator|.
name|indexOf
argument_list|(
literal|"64"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|is64Bit
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|is64Bit
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
name|JRE_IS_64BIT
operator|=
name|is64Bit
expr_stmt|;
comment|// this method only exists in Java 7:
name|boolean
name|v7
init|=
literal|true
decl_stmt|;
try|try
block|{
name|Throwable
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"getSuppressed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
name|v7
operator|=
literal|false
expr_stmt|;
block|}
name|JRE_IS_MINIMUM_JAVA7
operator|=
name|v7
expr_stmt|;
block|}
comment|// this method prevents inlining the final version constant in compiled classes,
comment|// see: http://www.javaworld.com/community/node/3400
DECL|method|ident
specifier|private
specifier|static
name|String
name|ident
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// NOTE: we track per-segment version as a String with the "X.Y" format, e.g.
comment|// "4.0", "3.1", "3.0". Therefore when we change this constant, we should keep
comment|// the format.
comment|/**    * This is the internal Lucene version, recorded into each segment.    */
DECL|field|LUCENE_MAIN_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|LUCENE_MAIN_VERSION
init|=
name|ident
argument_list|(
literal|"5.0"
argument_list|)
decl_stmt|;
comment|/**    * This is the Lucene version for display purposes.    */
DECL|field|LUCENE_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|LUCENE_VERSION
decl_stmt|;
static|static
block|{
name|Package
name|pkg
init|=
name|LucenePackage
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|v
init|=
operator|(
name|pkg
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|pkg
operator|.
name|getImplementationVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|String
name|parts
index|[]
init|=
name|LUCENE_MAIN_VERSION
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|4
condition|)
block|{
comment|// alpha/beta
assert|assert
name|parts
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
assert|;
name|v
operator|=
name|parts
index|[
literal|0
index|]
operator|+
literal|"."
operator|+
name|parts
index|[
literal|1
index|]
operator|+
literal|"-SNAPSHOT"
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
name|LUCENE_MAIN_VERSION
operator|+
literal|"-SNAPSHOT"
expr_stmt|;
block|}
block|}
name|LUCENE_VERSION
operator|=
name|ident
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

