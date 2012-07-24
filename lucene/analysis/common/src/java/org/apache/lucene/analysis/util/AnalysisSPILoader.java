begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|Locale
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
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_comment
comment|/**  * Helper class for loading named SPIs from classpath (e.g. Tokenizers, TokenStreams).  * @lucene.internal  */
end_comment

begin_class
DECL|class|AnalysisSPILoader
specifier|public
specifier|final
class|class
name|AnalysisSPILoader
parameter_list|<
name|S
extends|extends
name|AbstractAnalysisFactory
parameter_list|>
block|{
DECL|field|services
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
name|services
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
DECL|method|AnalysisSPILoader
specifier|public
name|AnalysisSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
operator|new
name|String
index|[]
block|{
name|clazz
operator|.
name|getSimpleName
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|AnalysisSPILoader
specifier|public
name|AnalysisSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|,
name|String
index|[]
name|suffixes
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
specifier|final
name|ServiceLoader
argument_list|<
name|S
argument_list|>
name|loader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
name|services
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|S
name|service
range|:
name|loader
control|)
block|{
specifier|final
name|String
name|clazzName
init|=
name|service
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|int
name|suffixIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|String
name|suffix
range|:
name|suffixes
control|)
block|{
name|suffixIndex
operator|=
name|clazzName
operator|.
name|lastIndexOf
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
if|if
condition|(
name|suffixIndex
operator|!=
operator|-
literal|1
condition|)
block|{
break|break;
block|}
block|}
specifier|final
name|String
name|name
init|=
name|clazzName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|suffixIndex
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|// only add the first one for each name, later services will be ignored
comment|// this allows to place services before others in classpath to make
comment|// them used instead of others
if|if
condition|(
operator|!
name|services
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
assert|assert
name|checkServiceName
argument_list|(
name|name
argument_list|)
assert|;
name|services
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|service
operator|.
name|getClass
argument_list|()
operator|.
name|asSubclass
argument_list|(
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|services
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates that a service name meets the requirements of {@link NamedSPI}    */
DECL|method|checkServiceName
specifier|public
specifier|static
name|boolean
name|checkServiceName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// based on harmony charset.java
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|>=
literal|128
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal service name: '"
operator|+
name|name
operator|+
literal|"' is too long (must be< 128 chars)."
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|name
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isLetter
argument_list|(
name|c
argument_list|)
operator|&&
operator|!
name|isDigit
argument_list|(
name|c
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal service name: '"
operator|+
name|name
operator|+
literal|"' must be simple ascii alphanumeric."
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/*    * Checks whether a character is a letter (ascii) which are defined in the spec.    */
DECL|method|isLetter
specifier|private
specifier|static
name|boolean
name|isLetter
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
operator|(
literal|'a'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'z'
operator|)
operator|||
operator|(
literal|'A'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'Z'
operator|)
return|;
block|}
comment|/*    * Checks whether a character is a digit (ascii) which are defined in the spec.    */
DECL|method|isDigit
specifier|private
specifier|static
name|boolean
name|isDigit
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
operator|(
literal|'0'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'9'
operator|)
return|;
block|}
DECL|method|newInstance
specifier|public
name|S
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|service
init|=
name|services
operator|.
name|get
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|service
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SPI class of type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" with name '"
operator|+
name|name
operator|+
literal|"' cannot be instantiated. "
operator|+
literal|"This is likely due to a misconfiguration of the java class '"
operator|+
name|service
operator|.
name|getName
argument_list|()
operator|+
literal|"': "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A SPI class of type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" with name '"
operator|+
name|name
operator|+
literal|"' does not exist. "
operator|+
literal|"You need to add the corresponding JAR file supporting this SPI to your classpath."
operator|+
literal|"The current classpath supports the following names: "
operator|+
name|availableServices
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|availableServices
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|availableServices
parameter_list|()
block|{
return|return
name|services
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

