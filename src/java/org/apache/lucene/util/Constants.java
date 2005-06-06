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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Name of the index segment file */
DECL|field|INDEX_SEGMENTS_FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_SEGMENTS_FILENAME
init|=
literal|"segments"
decl_stmt|;
comment|/** Name of the index deletable file */
DECL|field|INDEX_DELETABLE_FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_DELETABLE_FILENAME
init|=
literal|"deletable"
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

