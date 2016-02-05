begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|DocumentStoredFieldVisitor
import|;
end_import

begin_comment
comment|/**  * Expert: provides a low-level means of accessing the stored field  * values in an index.  See {@link IndexReader#document(int,  * StoredFieldVisitor)}.  *  *<p><b>NOTE</b>: a {@code StoredFieldVisitor} implementation  * should not try to load or visit other stored documents in  * the same reader because the implementation of stored  * fields for most codecs is not reeentrant and you will see  * strange exceptions as a result.  *  *<p>See {@link DocumentStoredFieldVisitor}, which is a  *<code>StoredFieldVisitor</code> that builds the  * {@link Document} containing all stored fields.  This is  * used by {@link IndexReader#document(int)}.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|StoredFieldVisitor
specifier|public
specifier|abstract
class|class
name|StoredFieldVisitor
block|{
comment|/** Sole constructor. (For invocation by subclass     * constructors, typically implicit.) */
DECL|method|StoredFieldVisitor
specifier|protected
name|StoredFieldVisitor
parameter_list|()
block|{   }
comment|/** Process a binary field.     * @param value newly allocated byte array with the binary contents.     */
DECL|method|binaryField
specifier|public
name|void
name|binaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/** Process a string field; the provided byte[] value is a UTF-8 encoded string value. */
DECL|method|stringField
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/** Process a int numeric field. */
DECL|method|intField
specifier|public
name|void
name|intField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/** Process a long numeric field. */
DECL|method|longField
specifier|public
name|void
name|longField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/** Process a float numeric field. */
DECL|method|floatField
specifier|public
name|void
name|floatField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/** Process a double numeric field. */
DECL|method|doubleField
specifier|public
name|void
name|doubleField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * Hook before processing a field.    * Before a field is processed, this method is invoked so that    * subclasses can return a {@link Status} representing whether    * they need that particular field or not, or to stop processing    * entirely.    */
DECL|method|needsField
specifier|public
specifier|abstract
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Enumeration of possible return values for {@link #needsField}.    */
DECL|enum|Status
specifier|public
specifier|static
enum|enum
name|Status
block|{
comment|/** YES: the field should be visited. */
DECL|enum constant|YES
name|YES
block|,
comment|/** NO: don't visit this field, but continue processing fields for this document. */
DECL|enum constant|NO
name|NO
block|,
comment|/** STOP: don't visit this field and stop processing any other fields for this document. */
DECL|enum constant|STOP
name|STOP
block|}
block|}
end_class

end_unit

