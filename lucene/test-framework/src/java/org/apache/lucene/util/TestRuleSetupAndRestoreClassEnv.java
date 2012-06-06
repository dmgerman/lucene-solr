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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

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
name|util
operator|.
name|HashSet
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
name|Random
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
name|TimeZone
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|appending
operator|.
name|AppendingCodec
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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40Codec
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
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextCodec
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
name|index
operator|.
name|RandomCodec
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
name|search
operator|.
name|RandomSimilarityProvider
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
name|search
operator|.
name|similarities
operator|.
name|DefaultSimilarity
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressCodecs
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Setup and restore suite-level environment (fine grained junk that   * doesn't fit anywhere else).  */
end_comment

begin_class
DECL|class|TestRuleSetupAndRestoreClassEnv
specifier|final
class|class
name|TestRuleSetupAndRestoreClassEnv
extends|extends
name|AbstractBeforeAfterRule
block|{
comment|/**    * Restore these system property values.    */
DECL|field|restoreProperties
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|restoreProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|savedCodec
specifier|private
name|Codec
name|savedCodec
decl_stmt|;
DECL|field|savedLocale
specifier|private
name|Locale
name|savedLocale
decl_stmt|;
DECL|field|savedTimeZone
specifier|private
name|TimeZone
name|savedTimeZone
decl_stmt|;
DECL|field|savedInfoStream
specifier|private
name|InfoStream
name|savedInfoStream
decl_stmt|;
DECL|field|locale
name|Locale
name|locale
decl_stmt|;
DECL|field|timeZone
name|TimeZone
name|timeZone
decl_stmt|;
DECL|field|similarity
name|Similarity
name|similarity
decl_stmt|;
DECL|field|codec
name|Codec
name|codec
decl_stmt|;
comment|/**    * @see SuppressCodecs    */
DECL|field|avoidCodecs
name|HashSet
argument_list|<
name|String
argument_list|>
name|avoidCodecs
decl_stmt|;
annotation|@
name|Override
DECL|method|before
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
comment|// enable this by default, for IDE consistency with ant tests (as its the default from ant)
comment|// TODO: really should be in solr base classes, but some extend LTC directly.
comment|// we do this in beforeClass, because some tests currently disable it
name|restoreProperties
operator|.
name|put
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"org.apache.solr.core.MockDirectoryFactory"
argument_list|)
expr_stmt|;
block|}
comment|// enable the Lucene 3.x PreflexRW codec explicitly, to work around bugs in IBM J9 / Harmony ServiceLoader:
try|try
block|{
specifier|final
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|spiLoaderField
init|=
name|Codec
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"loader"
argument_list|)
decl_stmt|;
name|spiLoaderField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|spiLoader
init|=
name|spiLoaderField
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|modifiableServicesField
init|=
name|NamedSPILoader
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"modifiableServices"
argument_list|)
decl_stmt|;
name|modifiableServicesField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|/* note: re-enable this if we make a Lucene4x impersonator        @SuppressWarnings({"unchecked","rawtypes"}) final Map<String,Codec> serviceMap =         (Map) modifiableServicesField.get(spiLoader);       if (!(Codec.forName("Lucene3x") instanceof PreFlexRWCodec)) {         if (Constants.JAVA_VENDOR.startsWith("IBM")) {           // definitely a buggy version           System.err.println("ERROR: Your VM's java.util.ServiceLoader implementation is buggy"+             " and does not respect classpath order, please report this to the vendor.");         } else {           // could just be a classpath issue           System.err.println("ERROR: fix your classpath to have tests-framework.jar before lucene-core.jar!"+               " If you have already done this, then your VM's java.util.ServiceLoader implementation is buggy"+               " and does not respect classpath order, please report this to the vendor.");         }         serviceMap.put("Lucene3x", new PreFlexRWCodec());       } */
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot access internals of Codec and NamedSPILoader classes"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// if verbose: print some debugging stuff about which codecs are loaded
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|codecs
init|=
name|Codec
operator|.
name|availableCodecs
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|codec
range|:
name|codecs
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Loaded codec: '"
operator|+
name|codec
operator|+
literal|"': "
operator|+
name|Codec
operator|.
name|forName
argument_list|(
name|codec
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|postingsFormats
init|=
name|PostingsFormat
operator|.
name|availablePostingsFormats
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|postingsFormat
range|:
name|postingsFormats
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Loaded postingsFormat: '"
operator|+
name|postingsFormat
operator|+
literal|"': "
operator|+
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|postingsFormat
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|savedInfoStream
operator|=
name|InfoStream
operator|.
name|getDefault
argument_list|()
expr_stmt|;
specifier|final
name|Random
name|random
init|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRandom
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|v
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|INFOSTREAM
condition|)
block|{
name|InfoStream
operator|.
name|setDefault
argument_list|(
operator|new
name|PrintStreamInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|message
parameter_list|(
name|String
name|component
parameter_list|,
name|String
name|message
parameter_list|)
block|{
specifier|final
name|String
name|name
decl_stmt|;
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"TEST-"
argument_list|)
condition|)
block|{
comment|// The name of the main thread is way too
comment|// long when looking at IW verbose output...:
name|name
operator|=
literal|"main"
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|stream
operator|.
name|println
argument_list|(
name|component
operator|+
literal|" "
operator|+
name|messageID
operator|+
literal|" ["
operator|+
operator|new
name|Date
argument_list|()
operator|+
literal|"; "
operator|+
name|name
operator|+
literal|"]: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
condition|)
block|{
name|InfoStream
operator|.
name|setDefault
argument_list|(
operator|new
name|NullInfoStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|targetClass
init|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getTargetClass
argument_list|()
decl_stmt|;
name|avoidCodecs
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|targetClass
operator|.
name|isAnnotationPresent
argument_list|(
name|SuppressCodecs
operator|.
name|class
argument_list|)
condition|)
block|{
name|SuppressCodecs
name|a
init|=
name|targetClass
operator|.
name|getAnnotation
argument_list|(
name|SuppressCodecs
operator|.
name|class
argument_list|)
decl_stmt|;
name|avoidCodecs
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|a
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: Suppressing codecs "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|a
operator|.
name|value
argument_list|()
argument_list|)
operator|+
literal|" for "
operator|+
name|targetClass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
name|PREFLEX_IMPERSONATION_IS_ACTIVE
operator|=
literal|false
expr_stmt|;
name|savedCodec
operator|=
name|Codec
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|int
name|randomVal
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|/* note: re-enable this if we make a 4.x impersonator       if ("Lucene3x".equals(TEST_CODEC) || ("random".equals(TEST_CODEC)&&                                           "random".equals(TEST_POSTINGSFORMAT)&&                                           randomVal< 2&&                                           !shouldAvoidCodec("Lucene3x"))) { // preflex-only setup       codec = Codec.forName("Lucene3x");       assert (codec instanceof PreFlexRWCodec) : "fix your classpath to have tests-framework.jar before lucene-core.jar";       PREFLEX_IMPERSONATION_IS_ACTIVE = true;     } else */
if|if
condition|(
operator|!
literal|"random"
operator|.
name|equals
argument_list|(
name|TEST_POSTINGSFORMAT
argument_list|)
condition|)
block|{
name|codec
operator|=
operator|new
name|Lucene40Codec
argument_list|()
block|{
specifier|private
specifier|final
name|PostingsFormat
name|format
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|TEST_POSTINGSFORMAT
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|format
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|format
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"SimpleText"
operator|.
name|equals
argument_list|(
name|TEST_CODEC
argument_list|)
operator|||
operator|(
literal|"random"
operator|.
name|equals
argument_list|(
name|TEST_CODEC
argument_list|)
operator|&&
name|randomVal
operator|==
literal|9
operator|&&
operator|!
name|shouldAvoidCodec
argument_list|(
literal|"SimpleText"
argument_list|)
operator|)
condition|)
block|{
name|codec
operator|=
operator|new
name|SimpleTextCodec
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Appending"
operator|.
name|equals
argument_list|(
name|TEST_CODEC
argument_list|)
operator|||
operator|(
literal|"random"
operator|.
name|equals
argument_list|(
name|TEST_CODEC
argument_list|)
operator|&&
name|randomVal
operator|==
literal|8
operator|&&
operator|!
name|shouldAvoidCodec
argument_list|(
literal|"Appending"
argument_list|)
operator|)
condition|)
block|{
name|codec
operator|=
operator|new
name|AppendingCodec
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
literal|"random"
operator|.
name|equals
argument_list|(
name|TEST_CODEC
argument_list|)
condition|)
block|{
name|codec
operator|=
name|Codec
operator|.
name|forName
argument_list|(
name|TEST_CODEC
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"random"
operator|.
name|equals
argument_list|(
name|TEST_POSTINGSFORMAT
argument_list|)
condition|)
block|{
name|codec
operator|=
operator|new
name|RandomCodec
argument_list|(
name|random
argument_list|,
name|avoidCodecs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
literal|false
assert|;
block|}
name|Codec
operator|.
name|setDefault
argument_list|(
name|codec
argument_list|)
expr_stmt|;
comment|// Initialize locale/ timezone.
name|String
name|testLocale
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.locale"
argument_list|,
literal|"random"
argument_list|)
decl_stmt|;
name|String
name|testTimeZone
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.timezone"
argument_list|,
literal|"random"
argument_list|)
decl_stmt|;
comment|// Always pick a random one for consistency (whether tests.locale was specified or not).
name|savedLocale
operator|=
name|Locale
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|Locale
name|randomLocale
init|=
name|randomLocale
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|locale
operator|=
name|testLocale
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|?
name|randomLocale
else|:
name|localeForName
argument_list|(
name|testLocale
argument_list|)
expr_stmt|;
name|Locale
operator|.
name|setDefault
argument_list|(
name|locale
argument_list|)
expr_stmt|;
comment|// TimeZone.getDefault will set user.timezone to the default timezone of the user's locale.
comment|// So store the original property value and restore it at end.
name|restoreProperties
operator|.
name|put
argument_list|(
literal|"user.timezone"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.timezone"
argument_list|)
argument_list|)
expr_stmt|;
name|savedTimeZone
operator|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|TimeZone
name|randomTimeZone
init|=
name|randomTimeZone
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|timeZone
operator|=
name|testTimeZone
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|?
name|randomTimeZone
else|:
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|testTimeZone
argument_list|)
expr_stmt|;
name|TimeZone
operator|.
name|setDefault
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
name|similarity
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|new
name|DefaultSimilarity
argument_list|()
else|:
operator|new
name|RandomSimilarityProvider
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * After suite cleanup (always invoked).    */
annotation|@
name|Override
DECL|method|after
specifier|protected
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|restoreProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|restoreProperties
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Codec
operator|.
name|setDefault
argument_list|(
name|savedCodec
argument_list|)
expr_stmt|;
name|InfoStream
operator|.
name|setDefault
argument_list|(
name|savedInfoStream
argument_list|)
expr_stmt|;
name|Locale
operator|.
name|setDefault
argument_list|(
name|savedLocale
argument_list|)
expr_stmt|;
name|TimeZone
operator|.
name|setDefault
argument_list|(
name|savedTimeZone
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solr.home"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.data.dir"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Should a given codec be avoided for the currently executing suite?    */
DECL|method|shouldAvoidCodec
specifier|public
name|boolean
name|shouldAvoidCodec
parameter_list|(
name|String
name|codec
parameter_list|)
block|{
return|return
operator|!
name|avoidCodecs
operator|.
name|isEmpty
argument_list|()
operator|&&
name|avoidCodecs
operator|.
name|contains
argument_list|(
name|codec
argument_list|)
return|;
block|}
block|}
end_class

end_unit

