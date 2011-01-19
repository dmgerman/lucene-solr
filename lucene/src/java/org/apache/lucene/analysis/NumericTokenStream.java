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
name|AttributeReflector
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
name|BytesRef
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
name|NumericUtils
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
name|NumericField
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|NumericRangeQuery
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|NumericRangeFilter
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|CharTermAttribute
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
name|TermToBytesRefAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
import|;
end_import

begin_comment
comment|/**  *<b>Expert:</b> This class provides a {@link TokenStream}  * for indexing numeric values that can be used by {@link  * NumericRangeQuery} or {@link NumericRangeFilter}.  *  *<p>Note that for simple usage, {@link NumericField} is  * recommended.  {@link NumericField} disables norms and  * term freqs, as they are not usually needed during  * searching.  If you need to change these settings, you  * should use this class.  *  *<p>See {@link NumericField} for capabilities of fields  * indexed numerically.</p>  *  *<p>Here's an example usage, for an<code>int</code> field:  *  *<pre>  *  Field field = new Field(name, new NumericTokenStream(precisionStep).setIntValue(value));  *  field.setOmitNorms(true);  *  field.setOmitTermFreqAndPositions(true);  *  document.add(field);  *</pre>  *  *<p>For optimal performance, re-use the TokenStream and Field instance  * for more than one document:  *  *<pre>  *  NumericTokenStream stream = new NumericTokenStream(precisionStep);  *  Field field = new Field(name, stream);  *  field.setOmitNorms(true);  *  field.setOmitTermFreqAndPositions(true);  *  Document document = new Document();  *  document.add(field);  *  *  for(all documents) {  *    stream.setIntValue(value)  *    writer.addDocument(document);  *  }  *</pre>  *  *<p>This stream is not intended to be used in analyzers;  * it's more for iterating the different precisions during  * indexing a specific numeric value.</p>   *<p><b>NOTE</b>: as token streams are only consumed once  * the document is added to the index, if you index more  * than one numeric field, use a separate<code>NumericTokenStream</code>  * instance for each.</p>  *  *<p>See {@link NumericRangeQuery} for more details on the  *<a  * href="../search/NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>  * parameter as well as how numeric fields work under the hood.</p>  *  * @lucene.experimental  *  * @since 2.9  */
end_comment

begin_class
DECL|class|NumericTokenStream
specifier|public
specifier|final
class|class
name|NumericTokenStream
extends|extends
name|TokenStream
block|{
comment|/** The full precision token gets this token type assigned. */
DECL|field|TOKEN_TYPE_FULL_PREC
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_FULL_PREC
init|=
literal|"fullPrecNumeric"
decl_stmt|;
comment|/** The lower precision tokens gets this token type assigned. */
DECL|field|TOKEN_TYPE_LOWER_PREC
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_LOWER_PREC
init|=
literal|"lowerPrecNumeric"
decl_stmt|;
comment|/**<b>Expert:</b> Use this attribute to get the details of the currently generated token.    * @lucene.experimental    * @since 4.0    */
DECL|interface|NumericTermAttribute
specifier|public
interface|interface
name|NumericTermAttribute
extends|extends
name|Attribute
block|{
comment|/** Returns current shift value, undefined before first token */
DECL|method|getShift
name|int
name|getShift
parameter_list|()
function_decl|;
comment|/** Returns current token's raw value as {@code long} with all {@link #getShift} applied, undefined before first token */
DECL|method|getRawValue
name|long
name|getRawValue
parameter_list|()
function_decl|;
comment|/** Returns value size in bits (32 for {@code float}, {@code int}; 64 for {@code double}, {@code long}) */
DECL|method|getValueSize
name|int
name|getValueSize
parameter_list|()
function_decl|;
comment|/**<em>Don't call this method!</em>       * @lucene.internal */
DECL|method|init
name|void
name|init
parameter_list|(
name|long
name|value
parameter_list|,
name|int
name|valSize
parameter_list|,
name|int
name|precisionStep
parameter_list|,
name|int
name|shift
parameter_list|)
function_decl|;
comment|/**<em>Don't call this method!</em>       * @lucene.internal */
DECL|method|setShift
name|void
name|setShift
parameter_list|(
name|int
name|shift
parameter_list|)
function_decl|;
comment|/**<em>Don't call this method!</em>       * @lucene.internal */
DECL|method|incShift
name|int
name|incShift
parameter_list|()
function_decl|;
block|}
comment|// just a wrapper to prevent adding CTA
DECL|class|NumericAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|NumericAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeFactory
name|delegate
decl_stmt|;
DECL|method|NumericAttributeFactory
name|NumericAttributeFactory
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
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
if|if
condition|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NumericTokenStream does not support CharTermAttribute."
argument_list|)
throw|;
return|return
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
comment|/** Implementatation of {@link NumericTermAttribute}.    * @lucene.internal    * @since 4.0    */
DECL|class|NumericTermAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|NumericTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|NumericTermAttribute
implements|,
name|TermToBytesRefAttribute
block|{
DECL|field|value
specifier|private
name|long
name|value
init|=
literal|0L
decl_stmt|;
DECL|field|valueSize
DECL|field|shift
DECL|field|precisionStep
specifier|private
name|int
name|valueSize
init|=
literal|0
decl_stmt|,
name|shift
init|=
literal|0
decl_stmt|,
name|precisionStep
init|=
literal|0
decl_stmt|;
DECL|method|toBytesRef
specifier|public
name|int
name|toBytesRef
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
try|try
block|{
assert|assert
name|valueSize
operator|==
literal|64
operator|||
name|valueSize
operator|==
literal|32
assert|;
return|return
operator|(
name|valueSize
operator|==
literal|64
operator|)
condition|?
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|,
name|bytes
argument_list|)
else|:
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|,
name|shift
argument_list|,
name|bytes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// return empty token before first or after last
name|bytes
operator|.
name|length
operator|=
literal|0
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
DECL|method|getShift
specifier|public
name|int
name|getShift
parameter_list|()
block|{
return|return
name|shift
return|;
block|}
DECL|method|setShift
specifier|public
name|void
name|setShift
parameter_list|(
name|int
name|shift
parameter_list|)
block|{
name|this
operator|.
name|shift
operator|=
name|shift
expr_stmt|;
block|}
DECL|method|incShift
specifier|public
name|int
name|incShift
parameter_list|()
block|{
return|return
operator|(
name|shift
operator|+=
name|precisionStep
operator|)
return|;
block|}
DECL|method|getRawValue
specifier|public
name|long
name|getRawValue
parameter_list|()
block|{
return|return
name|value
operator|&
operator|~
operator|(
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1L
operator|)
return|;
block|}
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
name|valueSize
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|long
name|value
parameter_list|,
name|int
name|valueSize
parameter_list|,
name|int
name|precisionStep
parameter_list|,
name|int
name|shift
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|valueSize
operator|=
name|valueSize
expr_stmt|;
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
name|this
operator|.
name|shift
operator|=
name|shift
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// this attribute has no contents to clear!
comment|// we keep it untouched as it's fully controlled by outer class.
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|toBytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|,
literal|"bytes"
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|,
literal|"shift"
argument_list|,
name|shift
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|,
literal|"rawValue"
argument_list|,
name|getRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|,
literal|"valueSize"
argument_list|,
name|valueSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
specifier|final
name|NumericTermAttribute
name|a
init|=
operator|(
name|NumericTermAttribute
operator|)
name|target
decl_stmt|;
name|a
operator|.
name|init
argument_list|(
name|value
argument_list|,
name|valueSize
argument_list|,
name|precisionStep
argument_list|,
name|shift
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a token stream for numeric values using the default<code>precisionStep</code>    * {@link NumericUtils#PRECISION_STEP_DEFAULT} (4). The stream is not yet initialized,    * before using set a value using the various set<em>???</em>Value() methods.    */
DECL|method|NumericTokenStream
specifier|public
name|NumericTokenStream
parameter_list|()
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a token stream for numeric values with the specified    *<code>precisionStep</code>. The stream is not yet initialized,    * before using set a value using the various set<em>???</em>Value() methods.    */
DECL|method|NumericTokenStream
specifier|public
name|NumericTokenStream
parameter_list|(
specifier|final
name|int
name|precisionStep
parameter_list|)
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|,
name|precisionStep
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: Creates a token stream for numeric values with the specified    *<code>precisionStep</code> using the given    * {@link org.apache.lucene.util.AttributeSource.AttributeFactory}.    * The stream is not yet initialized,    * before using set a value using the various set<em>???</em>Value() methods.    */
DECL|method|NumericTokenStream
specifier|public
name|NumericTokenStream
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|NumericAttributeFactory
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|precisionStep
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep must be>=1"
argument_list|)
throw|;
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
name|numericAtt
operator|.
name|setShift
argument_list|(
operator|-
name|precisionStep
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>long</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setLongValue(value))</code>    */
DECL|method|setLongValue
specifier|public
name|NumericTokenStream
name|setLongValue
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
name|numericAtt
operator|.
name|init
argument_list|(
name|value
argument_list|,
name|valSize
operator|=
literal|64
argument_list|,
name|precisionStep
argument_list|,
operator|-
name|precisionStep
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>int</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setIntValue(value))</code>    */
DECL|method|setIntValue
specifier|public
name|NumericTokenStream
name|setIntValue
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
name|numericAtt
operator|.
name|init
argument_list|(
name|value
argument_list|,
name|valSize
operator|=
literal|32
argument_list|,
name|precisionStep
argument_list|,
operator|-
name|precisionStep
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>double</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setDoubleValue(value))</code>    */
DECL|method|setDoubleValue
specifier|public
name|NumericTokenStream
name|setDoubleValue
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
name|numericAtt
operator|.
name|init
argument_list|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|value
argument_list|)
argument_list|,
name|valSize
operator|=
literal|64
argument_list|,
name|precisionStep
argument_list|,
operator|-
name|precisionStep
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>float</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setFloatValue(value))</code>    */
DECL|method|setFloatValue
specifier|public
name|NumericTokenStream
name|setFloatValue
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
name|numericAtt
operator|.
name|init
argument_list|(
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|value
argument_list|)
argument_list|,
name|valSize
operator|=
literal|32
argument_list|,
name|precisionStep
argument_list|,
operator|-
name|precisionStep
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|valSize
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call set???Value() before usage"
argument_list|)
throw|;
name|numericAtt
operator|.
name|setShift
argument_list|(
operator|-
name|precisionStep
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|valSize
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call set???Value() before usage"
argument_list|)
throw|;
comment|// this will only clear all other attributes in this TokenStream
name|clearAttributes
argument_list|()
expr_stmt|;
specifier|final
name|int
name|shift
init|=
name|numericAtt
operator|.
name|incShift
argument_list|()
decl_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
name|TOKEN_TYPE_FULL_PREC
else|:
name|TOKEN_TYPE_LOWER_PREC
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
return|return
operator|(
name|shift
operator|<
name|valSize
operator|)
return|;
block|}
comment|// members
DECL|field|numericAtt
specifier|private
specifier|final
name|NumericTermAttribute
name|numericAtt
init|=
name|addAttribute
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|valSize
specifier|private
name|int
name|valSize
init|=
literal|0
decl_stmt|;
comment|// valSize==0 means not initialized
DECL|field|precisionStep
specifier|private
specifier|final
name|int
name|precisionStep
decl_stmt|;
block|}
end_class

end_unit

