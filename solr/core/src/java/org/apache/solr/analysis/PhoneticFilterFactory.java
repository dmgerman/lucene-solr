begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Encoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|*
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|phonetic
operator|.
name|PhoneticFilter
import|;
end_import

begin_comment
comment|/**  * Factory for {@link PhoneticFilter}.  *   * Create tokens based on phonetic encoders  *   * http://jakarta.apache.org/commons/codec/api-release/org/apache/commons/codec/language/package-summary.html  *   * This takes two arguments:  *  "encoder" required, one of "DoubleMetaphone", "Metaphone", "Soundex", "RefinedSoundex"  *   * "inject" (default=true) add tokens to the stream with the offset=0  *  *<pre class="prettyprint">  *&lt;fieldType name="text_phonetic" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.PhoneticFilterFactory" encoder="DoubleMetaphone" inject="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   *  * @see PhoneticFilter  */
end_comment

begin_class
DECL|class|PhoneticFilterFactory
specifier|public
class|class
name|PhoneticFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|ENCODER
specifier|public
specifier|static
specifier|final
name|String
name|ENCODER
init|=
literal|"encoder"
decl_stmt|;
DECL|field|INJECT
specifier|public
specifier|static
specifier|final
name|String
name|INJECT
init|=
literal|"inject"
decl_stmt|;
comment|// boolean
DECL|field|PACKAGE_CONTAINING_ENCODERS
specifier|private
specifier|static
specifier|final
name|String
name|PACKAGE_CONTAINING_ENCODERS
init|=
literal|"org.apache.commons.codec.language."
decl_stmt|;
DECL|field|registry
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
argument_list|>
name|registry
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"DoubleMetaphone"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|DoubleMetaphone
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"Metaphone"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|Metaphone
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"Soundex"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|Soundex
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"RefinedSoundex"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|RefinedSoundex
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"Caverphone"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|Caverphone2
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"ColognePhonetic"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|ColognePhonetic
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|field|lock
specifier|private
specifier|static
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|inject
specifier|protected
name|boolean
name|inject
init|=
literal|true
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
init|=
literal|null
decl_stmt|;
DECL|field|encoder
specifier|protected
name|Encoder
name|encoder
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inject
operator|=
name|getBoolean
argument_list|(
name|INJECT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|args
operator|.
name|get
argument_list|(
name|ENCODER
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Missing required parameter: "
operator|+
name|ENCODER
operator|+
literal|" ["
operator|+
name|registry
operator|.
name|keySet
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|clazz
init|=
name|registry
operator|.
name|get
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|clazz
operator|=
name|resolveEncoder
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|encoder
operator|=
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
comment|// Try to set the maxCodeLength
name|String
name|v
init|=
name|args
operator|.
name|get
argument_list|(
literal|"maxCodeLength"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|Method
name|setter
init|=
name|encoder
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"setMaxCodeLen"
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|setter
operator|.
name|invoke
argument_list|(
name|encoder
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Error initializing: "
operator|+
name|name
operator|+
literal|"/"
operator|+
name|clazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|resolveEncoder
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|resolveEncoder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|clazz
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|lookupEncoder
argument_list|(
name|PACKAGE_CONTAINING_ENCODERS
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
try|try
block|{
name|clazz
operator|=
name|lookupEncoder
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Unknown encoder: "
operator|+
name|name
operator|+
literal|" ["
operator|+
name|registry
operator|.
name|keySet
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Not an encoder: "
operator|+
name|name
operator|+
literal|" ["
operator|+
name|registry
operator|.
name|keySet
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|clazz
return|;
block|}
DECL|method|lookupEncoder
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|lookupEncoder
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Encoder
operator|.
name|class
argument_list|)
decl_stmt|;
name|registry
operator|.
name|put
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
return|return
name|clazz
return|;
block|}
DECL|method|create
specifier|public
name|PhoneticFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|PhoneticFilter
argument_list|(
name|input
argument_list|,
name|encoder
argument_list|,
name|inject
argument_list|)
return|;
block|}
block|}
end_class

end_unit

