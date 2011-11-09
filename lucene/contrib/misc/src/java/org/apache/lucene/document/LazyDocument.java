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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Reader
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
name|Map
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
name|Analyzer
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
name|document
operator|.
name|NumericField
operator|.
name|DataType
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
name|FieldInfo
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
name|IndexReader
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
comment|/** Defers actually loading a field's value until you ask  *  for it.  You must not use the returned Field instances  *  after the provided reader has been closed. */
end_comment

begin_class
DECL|class|LazyDocument
specifier|public
class|class
name|LazyDocument
block|{
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|docID
specifier|private
specifier|final
name|int
name|docID
decl_stmt|;
comment|// null until first field is loaded
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|fields
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|LazyDocument
specifier|public
name|LazyDocument
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docID
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|docID
operator|=
name|docID
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|IndexableField
name|getField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|Integer
name|num
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|num
operator|==
literal|null
condition|)
block|{
name|num
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|num
operator|++
expr_stmt|;
block|}
name|fields
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|num
argument_list|)
expr_stmt|;
return|return
operator|new
name|LazyField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|num
argument_list|)
return|;
block|}
DECL|method|getDocument
specifier|private
specifier|synchronized
name|Document
name|getDocument
parameter_list|()
block|{
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|doc
operator|=
name|reader
operator|.
name|document
argument_list|(
name|docID
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
name|IllegalStateException
argument_list|(
literal|"unable to load document"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|reader
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|class|LazyField
specifier|private
class|class
name|LazyField
implements|implements
name|IndexableField
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|num
specifier|private
name|int
name|num
decl_stmt|;
DECL|method|LazyField
specifier|public
name|LazyField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|num
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
name|num
operator|=
name|num
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|binaryValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|binaryValue
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|stringValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|stringValue
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|readerValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|readerValue
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|numeric
specifier|public
name|boolean
name|numeric
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|numeric
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|numeric
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|numericDataType
specifier|public
name|DataType
name|numericDataType
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|numericDataType
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|numericDataType
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|numericValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|numericValue
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|IndexableFieldType
name|fieldType
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|fieldType
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|fieldType
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|PerDocFieldValues
name|docValues
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|docValues
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|docValues
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValuesType
specifier|public
name|ValueType
name|docValuesType
parameter_list|()
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|docValuesType
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|docValuesType
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|num
operator|==
literal|0
condition|)
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|analyzer
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|(
name|name
argument_list|)
index|[
name|num
index|]
operator|.
name|tokenStream
argument_list|(
name|analyzer
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

