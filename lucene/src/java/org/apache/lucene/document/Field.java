begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|Reader
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
name|index
operator|.
name|IndexableFieldType
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
name|IndexableField
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
name|values
operator|.
name|PerDocFieldValues
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
name|values
operator|.
name|ValueType
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

begin_comment
comment|/**  * A field is a section of a Document. Each field has two parts, a name and a  * value. Values may be free text, provided as a String or as a Reader, or they  * may be atomic keywords, which are not further processed. Such keywords may be  * used to represent dates, urls, etc. Fields are optionally stored in the  * index, so that they may be returned with hits on the document.  *<p/>  * Note, Field instances are instantiated with a {@link IndexableFieldType}.  Making changes  * to the state of the FieldType will impact any Field it is used in, therefore  * it is strongly recommended that no changes are made after Field instantiation.  */
end_comment

begin_class
DECL|class|Field
specifier|public
class|class
name|Field
implements|implements
name|IndexableField
block|{
DECL|field|type
specifier|protected
name|IndexableFieldType
name|type
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
init|=
literal|"body"
decl_stmt|;
comment|// the data object for all different kind of field values
DECL|field|fieldsData
specifier|protected
name|Object
name|fieldsData
decl_stmt|;
comment|// pre-analyzed tokenStream for indexed fields
DECL|field|tokenStream
specifier|protected
name|TokenStream
name|tokenStream
decl_stmt|;
comment|// length/offset for all primitive types
DECL|field|docValues
specifier|protected
name|PerDocFieldValues
name|docValues
decl_stmt|;
DECL|field|boost
specifier|protected
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"reader cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|tokenStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"tokenStream cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|tokenStream
operator|=
name|tokenStream
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|fieldsData
operator|=
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|fieldsData
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|type
operator|.
name|stored
argument_list|()
operator|&&
operator|!
name|type
operator|.
name|indexed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"it doesn't make sense to have a field that "
operator|+
literal|"is neither indexed nor stored"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|type
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|type
operator|.
name|tokenized
argument_list|()
operator|&&
operator|(
name|type
operator|.
name|storeTermVectors
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot store term vector information "
operator|+
literal|"for a field that is not indexed"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * The value of the field as a String, or null. If null, the Reader value or    * binary value is used. Exactly one of stringValue(), readerValue(), and    * getBinaryValue() must be set.    */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|String
condition|?
operator|(
name|String
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/**    * The value of the field as a Reader, or null. If null, the String value or    * binary value is used. Exactly one of stringValue(), readerValue(), and    * getBinaryValue() must be set.    */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|Reader
condition|?
operator|(
name|Reader
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/**    * The TokesStream for this field to be used when indexing, or null. If null,    * the Reader value or String value is analyzed to produce the indexed tokens.    */
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
return|return
name|tokenStream
return|;
block|}
comment|/**    *<p>    * Expert: change the value of this field. This can be used during indexing to    * re-use a single Field instance to improve indexing speed by avoiding GC    * cost of new'ing and reclaiming Field instances. Typically a single    * {@link Document} instance is re-used as well. This helps most on small    * documents.    *</p>    *     *<p>    * Each Field instance should only be used once within a single    * {@link Document} instance. See<a    * href="http://wiki.apache.org/lucene-java/ImproveIndexingSpeed"    *>ImproveIndexingSpeed</a> for details.    *</p>    */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|isBinary
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a String value on a binary field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See<a    * href="#setValue(java.lang.String)">setValue(String)</a>.    */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|Reader
name|value
parameter_list|)
block|{
if|if
condition|(
name|isBinary
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a Reader value on a binary field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|stored
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a Reader value on a stored field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Expert: change the value of this field. See<a    * href="#setValue(java.lang.String)">setValue(String)</a>.    */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isBinary
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a byte[] value on a non-binary field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: sets the token stream to be used for indexing and causes    * isIndexed() and isTokenized() to return true. May be combined with stored    * values from stringValue() or getBinaryValue()    */
DECL|method|setTokenStream
specifier|public
name|void
name|setTokenStream
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
if|if
condition|(
operator|!
name|type
operator|.
name|indexed
argument_list|()
operator|||
operator|!
name|type
operator|.
name|tokenized
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set token stream on non indexed and tokenized field"
argument_list|)
throw|;
block|}
name|this
operator|.
name|tokenStream
operator|=
name|tokenStream
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** Sets the boost factor hits on this field.  This value will be    * multiplied into the score of all hits on this this field of this    * document.    *    *<p>The boost is used to compute the norm factor for the field.  By    * default, in the {@link org.apache.lucene.search.similarities.Similarity#computeNorm(FieldInvertState)} method,     * the boost value is multiplied by the length normalization factor and then    * rounded by {@link org.apache.lucene.search.similarities.DefaultSimilarity#encodeNormValue(float)} before it is stored in the    * index.  One should attempt to ensure that this product does not overflow    * the range of that encoding.    *    * @see org.apache.lucene.search.similarities.Similarity#computeNorm(FieldInvertState)    * @see org.apache.lucene.search.similarities.DefaultSimilarity#encodeNormValue(float)    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
DECL|method|numeric
specifier|public
name|boolean
name|numeric
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|numericDataType
specifier|public
name|NumericField
operator|.
name|DataType
name|numericDataType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isBinary
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|(
name|BytesRef
operator|)
name|fieldsData
return|;
block|}
block|}
comment|/** methods from inner IndexableFieldType */
DECL|method|isBinary
specifier|public
name|boolean
name|isBinary
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|BytesRef
return|;
block|}
comment|/** Prints a Field for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsData
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|fieldsData
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setDocValues
specifier|public
name|void
name|setDocValues
parameter_list|(
name|PerDocFieldValues
name|docValues
parameter_list|)
block|{
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|PerDocFieldValues
name|docValues
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|docValuesType
specifier|public
name|ValueType
name|docValuesType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns FieldType for this field. */
DECL|method|fieldType
specifier|public
name|IndexableFieldType
name|fieldType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

