begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|tokenattributes
operator|.
name|FlagsAttribute
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|tokenattributes
operator|.
name|TypeAttribute
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
name|document
operator|.
name|Document
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
name|document
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
name|index
operator|.
name|IndexWriter
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
name|Payload
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
name|Attribute
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
name|AttributeImpl
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
name|AttributeSource
import|;
end_import

begin_comment
comment|/**  * A<code>TokenStream</code> enumerates the sequence of tokens, either from  * {@link Field}s of a {@link Document} or from query text.  *<p>  * This is an abstract class. Concrete subclasses are:  *<ul>  *<li>{@link Tokenizer}, a<code>TokenStream</code> whose input is a Reader; and  *<li>{@link TokenFilter}, a<code>TokenStream</code> whose input is another  *<code>TokenStream</code>.  *</ul>  * A new<code>TokenStream</code> API has been introduced with Lucene 2.9. This API  * has moved from being {@link Token} based to {@link Attribute} based. While  * {@link Token} still exists in 2.9 as a convenience class, the preferred way  * to store the information of a {@link Token} is to use {@link AttributeImpl}s.  *<p>  *<code>TokenStream</code> now extends {@link AttributeSource}, which provides  * access to all of the token {@link Attribute}s for the<code>TokenStream</code>.  * Note that only one instance per {@link AttributeImpl} is created and reused  * for every token. This approach reduces object creation and allows local  * caching of references to the {@link AttributeImpl}s. See  * {@link #incrementToken()} for further details.  *<p>  *<b>The workflow of the new<code>TokenStream</code> API is as follows:</b>  *<ol>  *<li>Instantiation of<code>TokenStream</code>/{@link TokenFilter}s which add/get  * attributes to/from the {@link AttributeSource}.  *<li>The consumer calls {@link TokenStream#reset()}.  *<li>the consumer retrieves attributes from the stream and stores local  * references to all attributes it wants to access  *<li>The consumer calls {@link #incrementToken()} until it returns false and  * consumes the attributes after each call.  *<li>The consumer calls {@link #end()} so that any end-of-stream operations  * can be performed.  *<li>The consumer calls {@link #close()} to release any resource when finished  * using the<code>TokenStream</code>  *</ol>  * To make sure that filters and consumers know which attributes are available,  * the attributes must be added during instantiation. Filters and consumers are  * not required to check for availability of attributes in  * {@link #incrementToken()}.  *<p>  * You can find some example code for the new API in the analysis package level  * Javadoc.  *<p>  * Sometimes it is desirable to capture a current state of a<code>TokenStream</code>  * , e. g. for buffering purposes (see {@link CachingTokenFilter},  * {@link TeeSinkTokenFilter}). For this usecase  * {@link AttributeSource#captureState} and {@link AttributeSource#restoreState}  * can be used.  */
end_comment

begin_class
DECL|class|TokenStream
specifier|public
specifier|abstract
class|class
name|TokenStream
extends|extends
name|AttributeSource
block|{
comment|/** @deprecated Remove this when old API is removed! */
DECL|field|DEFAULT_TOKEN_WRAPPER_ATTRIBUTE_FACTORY
specifier|private
specifier|static
specifier|final
name|AttributeFactory
name|DEFAULT_TOKEN_WRAPPER_ATTRIBUTE_FACTORY
init|=
operator|new
name|TokenWrapperAttributeFactory
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
decl_stmt|;
comment|/** @deprecated Remove this when old API is removed! */
DECL|field|tokenWrapper
specifier|private
specifier|final
name|TokenWrapper
name|tokenWrapper
decl_stmt|;
comment|/** @deprecated Remove this when old API is removed! */
DECL|field|onlyUseNewAPI
specifier|private
specifier|static
name|boolean
name|onlyUseNewAPI
init|=
literal|false
decl_stmt|;
comment|/** @deprecated Remove this when old API is removed! */
DECL|field|supportedMethods
specifier|private
specifier|final
name|MethodSupport
name|supportedMethods
init|=
name|getSupportedMethods
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/** @deprecated Remove this when old API is removed! */
DECL|class|MethodSupport
specifier|private
specifier|static
specifier|final
class|class
name|MethodSupport
block|{
DECL|field|hasIncrementToken
DECL|field|hasReusableNext
DECL|field|hasNext
specifier|final
name|boolean
name|hasIncrementToken
decl_stmt|,
name|hasReusableNext
decl_stmt|,
name|hasNext
decl_stmt|;
DECL|method|MethodSupport
name|MethodSupport
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|hasIncrementToken
operator|=
name|isMethodOverridden
argument_list|(
name|clazz
argument_list|,
literal|"incrementToken"
argument_list|,
name|METHOD_NO_PARAMS
argument_list|)
expr_stmt|;
name|hasReusableNext
operator|=
name|isMethodOverridden
argument_list|(
name|clazz
argument_list|,
literal|"next"
argument_list|,
name|METHOD_TOKEN_PARAM
argument_list|)
expr_stmt|;
name|hasNext
operator|=
name|isMethodOverridden
argument_list|(
name|clazz
argument_list|,
literal|"next"
argument_list|,
name|METHOD_NO_PARAMS
argument_list|)
expr_stmt|;
block|}
DECL|method|isMethodOverridden
specifier|private
specifier|static
name|boolean
name|isMethodOverridden
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
name|name
parameter_list|,
name|Class
index|[]
name|params
parameter_list|)
block|{
try|try
block|{
return|return
name|clazz
operator|.
name|getMethod
argument_list|(
name|name
argument_list|,
name|params
argument_list|)
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|TokenStream
operator|.
name|class
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// should not happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|METHOD_NO_PARAMS
specifier|private
specifier|static
specifier|final
name|Class
index|[]
name|METHOD_NO_PARAMS
init|=
operator|new
name|Class
index|[
literal|0
index|]
decl_stmt|;
DECL|field|METHOD_TOKEN_PARAM
specifier|private
specifier|static
specifier|final
name|Class
index|[]
name|METHOD_TOKEN_PARAM
init|=
operator|new
name|Class
index|[]
block|{
name|Token
operator|.
name|class
block|}
decl_stmt|;
block|}
comment|/** @deprecated Remove this when old API is removed! */
DECL|field|knownMethodSupport
specifier|private
specifier|static
specifier|final
name|IdentityHashMap
comment|/*<Class<? extends TokenStream>,MethodSupport>*/
name|knownMethodSupport
init|=
operator|new
name|IdentityHashMap
argument_list|()
decl_stmt|;
comment|/** @deprecated Remove this when old API is removed! */
DECL|method|getSupportedMethods
specifier|private
specifier|static
name|MethodSupport
name|getSupportedMethods
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|MethodSupport
name|supportedMethods
decl_stmt|;
synchronized|synchronized
init|(
name|knownMethodSupport
init|)
block|{
name|supportedMethods
operator|=
operator|(
name|MethodSupport
operator|)
name|knownMethodSupport
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|supportedMethods
operator|==
literal|null
condition|)
block|{
name|knownMethodSupport
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|supportedMethods
operator|=
operator|new
name|MethodSupport
argument_list|(
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|supportedMethods
return|;
block|}
comment|/** @deprecated Remove this when old API is removed! */
DECL|class|TokenWrapperAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|TokenWrapperAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeFactory
name|delegate
decl_stmt|;
DECL|method|TokenWrapperAttributeFactory
specifier|private
name|TokenWrapperAttributeFactory
parameter_list|(
name|AttributeFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
return|return
name|attClass
operator|.
name|isAssignableFrom
argument_list|(
name|TokenWrapper
operator|.
name|class
argument_list|)
condition|?
operator|new
name|TokenWrapper
argument_list|()
else|:
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
comment|// this is needed for TeeSinkTokenStream's check for compatibility of AttributeSource,
comment|// so two TokenStreams using old API have the same AttributeFactory wrapped by this one.
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|other
operator|instanceof
name|TokenWrapperAttributeFactory
condition|)
block|{
specifier|final
name|TokenWrapperAttributeFactory
name|af
init|=
operator|(
name|TokenWrapperAttributeFactory
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|delegate
operator|.
name|equals
argument_list|(
name|af
operator|.
name|delegate
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x0a45ff31
return|;
block|}
block|}
comment|/**    * A TokenStream using the default attribute factory.    */
DECL|method|TokenStream
specifier|protected
name|TokenStream
parameter_list|()
block|{
name|super
argument_list|(
name|onlyUseNewAPI
condition|?
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
else|:
name|TokenStream
operator|.
name|DEFAULT_TOKEN_WRAPPER_ATTRIBUTE_FACTORY
argument_list|)
expr_stmt|;
name|tokenWrapper
operator|=
name|initTokenWrapper
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|check
argument_list|()
expr_stmt|;
block|}
comment|/**    * A TokenStream that uses the same attributes as the supplied one.    */
DECL|method|TokenStream
specifier|protected
name|TokenStream
parameter_list|(
name|AttributeSource
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|tokenWrapper
operator|=
name|initTokenWrapper
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|check
argument_list|()
expr_stmt|;
block|}
comment|/**    * A TokenStream using the supplied AttributeFactory for creating new {@link Attribute} instances.    */
DECL|method|TokenStream
specifier|protected
name|TokenStream
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|onlyUseNewAPI
condition|?
name|factory
else|:
operator|new
name|TokenWrapperAttributeFactory
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
name|tokenWrapper
operator|=
name|initTokenWrapper
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** @deprecated Remove this when old API is removed! */
DECL|method|initTokenWrapper
specifier|private
name|TokenWrapper
name|initTokenWrapper
parameter_list|(
name|AttributeSource
name|input
parameter_list|)
block|{
if|if
condition|(
name|onlyUseNewAPI
condition|)
block|{
comment|// no wrapper needed
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// if possible get the wrapper from the filter's input stream
if|if
condition|(
name|input
operator|instanceof
name|TokenStream
operator|&&
operator|(
operator|(
name|TokenStream
operator|)
name|input
operator|)
operator|.
name|tokenWrapper
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
operator|(
name|TokenStream
operator|)
name|input
operator|)
operator|.
name|tokenWrapper
return|;
block|}
comment|// check that all attributes are implemented by the same TokenWrapper instance
specifier|final
name|Attribute
name|att
init|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|att
operator|instanceof
name|TokenWrapper
operator|&&
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
operator|==
name|att
operator|&&
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
operator|==
name|att
operator|&&
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
operator|==
name|att
operator|&&
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|==
name|att
operator|&&
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
operator|==
name|att
condition|)
block|{
return|return
operator|(
name|TokenWrapper
operator|)
name|att
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"If onlyUseNewAPI is disabled, all basic Attributes must be implemented by the internal class "
operator|+
literal|"TokenWrapper. Please make sure, that all TokenStreams/TokenFilters in this chain have been "
operator|+
literal|"instantiated with this flag disabled and do not add any custom instances for the basic Attributes!"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** @deprecated Remove this when old API is removed! */
DECL|method|check
specifier|private
name|void
name|check
parameter_list|()
block|{
if|if
condition|(
name|onlyUseNewAPI
operator|&&
operator|!
name|supportedMethods
operator|.
name|hasIncrementToken
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" does not implement incrementToken() which is needed for onlyUseNewAPI."
argument_list|)
throw|;
block|}
comment|// a TokenStream subclass must at least implement one of the methods!
if|if
condition|(
operator|!
operator|(
name|supportedMethods
operator|.
name|hasIncrementToken
operator|||
name|supportedMethods
operator|.
name|hasNext
operator|||
name|supportedMethods
operator|.
name|hasReusableNext
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" does not implement any of incrementToken(), next(Token), next()."
argument_list|)
throw|;
block|}
block|}
comment|/**    * For extra performance you can globally enable the new    * {@link #incrementToken} API using {@link Attribute}s. There will be a    * small, but in most cases negligible performance increase by enabling this,    * but it only works if<b>all</b><code>TokenStream</code>s use the new API and    * implement {@link #incrementToken}. This setting can only be enabled    * globally.    *<P>    * This setting only affects<code>TokenStream</code>s instantiated after this    * call. All<code>TokenStream</code>s already created use the other setting.    *<P>    * All core {@link Analyzer}s are compatible with this setting, if you have    * your own<code>TokenStream</code>s that are also compatible, you should enable    * this.    *<P>    * When enabled, tokenization may throw {@link UnsupportedOperationException}    * s, if the whole tokenizer chain is not compatible eg one of the    *<code>TokenStream</code>s does not implement the new<code>TokenStream</code> API.    *<P>    * The default is<code>false</code>, so there is the fallback to the old API    * available.    *     * @deprecated This setting will no longer be needed in Lucene 3.0 as the old    *             API will be removed.    */
DECL|method|setOnlyUseNewAPI
specifier|public
specifier|static
name|void
name|setOnlyUseNewAPI
parameter_list|(
name|boolean
name|onlyUseNewAPI
parameter_list|)
block|{
name|TokenStream
operator|.
name|onlyUseNewAPI
operator|=
name|onlyUseNewAPI
expr_stmt|;
block|}
comment|/**    * Returns if only the new API is used.    *     * @see #setOnlyUseNewAPI    * @deprecated This setting will no longer be needed in Lucene 3.0 as    *             the old API will be removed.    */
DECL|method|getOnlyUseNewAPI
specifier|public
specifier|static
name|boolean
name|getOnlyUseNewAPI
parameter_list|()
block|{
return|return
name|onlyUseNewAPI
return|;
block|}
comment|/**    * Consumers (ie {@link IndexWriter}) use this method to advance the stream to    * the next token. Implementing classes must implement this method and update    * the appropriate {@link AttributeImpl}s with the attributes of the next    * token.    *<P>    * The producer must make no assumptions about the attributes after the method    * has been returned: the caller may arbitrarily change it. If the producer    * needs to preserve the state for subsequent calls, it can use    * {@link #captureState} to create a copy of the current attribute state.    *<p>    * This method is called for every token of a document, so an efficient    * implementation is crucial for good performance. To avoid calls to    * {@link #addAttribute(Class)} and {@link #getAttribute(Class)} or downcasts,    * references to all {@link AttributeImpl}s that this stream uses should be    * retrieved during instantiation.    *<p>    * To ensure that filters and consumers know which attributes are available,    * the attributes must be added during instantiation. Filters and consumers    * are not required to check for availability of attributes in    * {@link #incrementToken()}.    *     * @return false for end of stream; true otherwise    *     *<p>    *<b>Note that this method will be defined abstract in Lucene    *         3.0.</b>    */
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|tokenWrapper
operator|!=
literal|null
assert|;
specifier|final
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|supportedMethods
operator|.
name|hasReusableNext
condition|)
block|{
name|token
operator|=
name|next
argument_list|(
name|tokenWrapper
operator|.
name|delegate
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|supportedMethods
operator|.
name|hasNext
assert|;
name|token
operator|=
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|tokenWrapper
operator|.
name|delegate
operator|=
name|token
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * This method is called by the consumer after the last token has been    * consumed, after {@link #incrementToken()} returned<code>false</code>    * (using the new<code>TokenStream</code> API). Streams implementing the old API    * should upgrade to use this feature.    *<p/>    * This method can be used to perform any end-of-stream operations, such as    * setting the final offset of a stream. The final offset of a stream might    * differ from the offset of the last token eg in case one or more whitespaces    * followed after the last token, but a {@link WhitespaceTokenizer} was used.    *     * @throws IOException    */
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing by default
block|}
comment|/**    * Returns the next token in the stream, or null at EOS. When possible, the    * input Token should be used as the returned Token (this gives fastest    * tokenization performance), but this is not required and a new Token may be    * returned. Callers may re-use a single Token instance for successive calls    * to this method.    *<p>    * This implicitly defines a "contract" between consumers (callers of this    * method) and producers (implementations of this method that are the source    * for tokens):    *<ul>    *<li>A consumer must fully consume the previously returned {@link Token}    * before calling this method again.</li>    *<li>A producer must call {@link Token#clear()} before setting the fields in    * it and returning it</li>    *</ul>    * Also, the producer must make no assumptions about a {@link Token} after it    * has been returned: the caller may arbitrarily change it. If the producer    * needs to hold onto the {@link Token} for subsequent calls, it must clone()    * it before storing it. Note that a {@link TokenFilter} is considered a    * consumer.    *     * @param reusableToken a {@link Token} that may or may not be used to return;    *        this parameter should never be null (the callee is not required to    *        check for null before using it, but it is a good idea to assert that    *        it is not null.)    * @return next {@link Token} in the stream or null if end-of-stream was hit    * @deprecated The new {@link #incrementToken()} and {@link AttributeSource}    *             APIs should be used instead.    */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
if|if
condition|(
name|tokenWrapper
operator|==
literal|null
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This TokenStream only supports the new Attributes API."
argument_list|)
throw|;
if|if
condition|(
name|supportedMethods
operator|.
name|hasIncrementToken
condition|)
block|{
name|tokenWrapper
operator|.
name|delegate
operator|=
name|reusableToken
expr_stmt|;
return|return
name|incrementToken
argument_list|()
condition|?
name|tokenWrapper
operator|.
name|delegate
else|:
literal|null
return|;
block|}
else|else
block|{
assert|assert
name|supportedMethods
operator|.
name|hasNext
assert|;
return|return
name|next
argument_list|()
return|;
block|}
block|}
comment|/**    * Returns the next {@link Token} in the stream, or null at EOS.    *     * @deprecated The returned Token is a "full private copy" (not re-used across    *             calls to {@link #next()}) but will be slower than calling    *             {@link #next(Token)} or using the new {@link #incrementToken()}    *             method with the new {@link AttributeSource} API.    */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tokenWrapper
operator|==
literal|null
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This TokenStream only supports the new Attributes API."
argument_list|)
throw|;
specifier|final
name|Token
name|nextToken
decl_stmt|;
if|if
condition|(
name|supportedMethods
operator|.
name|hasIncrementToken
condition|)
block|{
specifier|final
name|Token
name|savedDelegate
init|=
name|tokenWrapper
operator|.
name|delegate
decl_stmt|;
name|tokenWrapper
operator|.
name|delegate
operator|=
operator|new
name|Token
argument_list|()
expr_stmt|;
name|nextToken
operator|=
name|incrementToken
argument_list|()
condition|?
name|tokenWrapper
operator|.
name|delegate
else|:
literal|null
expr_stmt|;
name|tokenWrapper
operator|.
name|delegate
operator|=
name|savedDelegate
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|supportedMethods
operator|.
name|hasReusableNext
assert|;
name|nextToken
operator|=
name|next
argument_list|(
operator|new
name|Token
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nextToken
operator|!=
literal|null
condition|)
block|{
name|Payload
name|p
init|=
name|nextToken
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|nextToken
operator|.
name|setPayload
argument_list|(
operator|(
name|Payload
operator|)
name|p
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nextToken
return|;
block|}
comment|/**    * Resets this stream to the beginning. This is an optional operation, so    * subclasses may or may not implement this method. {@link #reset()} is not needed for    * the standard indexing process. However, if the tokens of a    *<code>TokenStream</code> are intended to be consumed more than once, it is    * necessary to implement {@link #reset()}. Note that if your TokenStream    * caches tokens and feeds them back again after a reset, it is imperative    * that you clone the tokens when you store them away (on the first pass) as    * well as when you return them (on future passes after {@link #reset()}).    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/** Releases resources associated with this stream. */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

