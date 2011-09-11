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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
operator|.
name|IndexOptions
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

begin_class
DECL|class|FieldType
specifier|public
class|class
name|FieldType
implements|implements
name|IndexableFieldType
block|{
DECL|field|indexed
specifier|private
name|boolean
name|indexed
decl_stmt|;
DECL|field|stored
specifier|private
name|boolean
name|stored
decl_stmt|;
DECL|field|tokenized
specifier|private
name|boolean
name|tokenized
decl_stmt|;
DECL|field|storeTermVectors
specifier|private
name|boolean
name|storeTermVectors
decl_stmt|;
DECL|field|storeTermVectorOffsets
specifier|private
name|boolean
name|storeTermVectorOffsets
decl_stmt|;
DECL|field|storeTermVectorPositions
specifier|private
name|boolean
name|storeTermVectorPositions
decl_stmt|;
DECL|field|omitNorms
specifier|private
name|boolean
name|omitNorms
decl_stmt|;
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
init|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
decl_stmt|;
DECL|field|frozen
specifier|private
name|boolean
name|frozen
decl_stmt|;
DECL|method|FieldType
specifier|public
name|FieldType
parameter_list|(
name|IndexableFieldType
name|ref
parameter_list|)
block|{
name|this
operator|.
name|indexed
operator|=
name|ref
operator|.
name|indexed
argument_list|()
expr_stmt|;
name|this
operator|.
name|stored
operator|=
name|ref
operator|.
name|stored
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokenized
operator|=
name|ref
operator|.
name|tokenized
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectors
operator|=
name|ref
operator|.
name|storeTermVectors
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorOffsets
operator|=
name|ref
operator|.
name|storeTermVectorOffsets
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorPositions
operator|=
name|ref
operator|.
name|storeTermVectorPositions
argument_list|()
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|ref
operator|.
name|omitNorms
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|ref
operator|.
name|indexOptions
argument_list|()
expr_stmt|;
comment|// Do not copy frozen!
block|}
DECL|method|FieldType
specifier|public
name|FieldType
parameter_list|()
block|{   }
DECL|method|checkIfFrozen
specifier|private
name|void
name|checkIfFrozen
parameter_list|()
block|{
if|if
condition|(
name|frozen
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Prevents future changes. Note, it is recommended that this is called once    * the FieldTypes's properties have been set, to prevent unintential state    * changes.    */
DECL|method|freeze
specifier|public
name|void
name|freeze
parameter_list|()
block|{
name|this
operator|.
name|frozen
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|indexed
specifier|public
name|boolean
name|indexed
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexed
return|;
block|}
DECL|method|setIndexed
specifier|public
name|void
name|setIndexed
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexed
operator|=
name|value
expr_stmt|;
block|}
DECL|method|stored
specifier|public
name|boolean
name|stored
parameter_list|()
block|{
return|return
name|this
operator|.
name|stored
return|;
block|}
DECL|method|setStored
specifier|public
name|void
name|setStored
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|stored
operator|=
name|value
expr_stmt|;
block|}
DECL|method|tokenized
specifier|public
name|boolean
name|tokenized
parameter_list|()
block|{
return|return
name|this
operator|.
name|tokenized
return|;
block|}
DECL|method|setTokenized
specifier|public
name|void
name|setTokenized
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokenized
operator|=
name|value
expr_stmt|;
block|}
DECL|method|storeTermVectors
specifier|public
name|boolean
name|storeTermVectors
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeTermVectors
return|;
block|}
DECL|method|setStoreTermVectors
specifier|public
name|void
name|setStoreTermVectors
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectors
operator|=
name|value
expr_stmt|;
block|}
DECL|method|storeTermVectorOffsets
specifier|public
name|boolean
name|storeTermVectorOffsets
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeTermVectorOffsets
return|;
block|}
DECL|method|setStoreTermVectorOffsets
specifier|public
name|void
name|setStoreTermVectorOffsets
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorOffsets
operator|=
name|value
expr_stmt|;
block|}
DECL|method|storeTermVectorPositions
specifier|public
name|boolean
name|storeTermVectorPositions
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeTermVectorPositions
return|;
block|}
DECL|method|setStoreTermVectorPositions
specifier|public
name|void
name|setStoreTermVectorPositions
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorPositions
operator|=
name|value
expr_stmt|;
block|}
DECL|method|omitNorms
specifier|public
name|boolean
name|omitNorms
parameter_list|()
block|{
return|return
name|this
operator|.
name|omitNorms
return|;
block|}
DECL|method|setOmitNorms
specifier|public
name|void
name|setOmitNorms
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|value
expr_stmt|;
block|}
DECL|method|indexOptions
specifier|public
name|IndexOptions
name|indexOptions
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexOptions
return|;
block|}
DECL|method|setIndexOptions
specifier|public
name|void
name|setIndexOptions
parameter_list|(
name|IndexOptions
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|value
expr_stmt|;
block|}
comment|/** Prints a Field for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
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
if|if
condition|(
name|stored
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"stored"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexed
argument_list|()
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"indexed"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tokenized
argument_list|()
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"tokenized"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVectors
argument_list|()
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"termVector"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"termVectorOffsets"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"termVectorPosition"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omitNorms
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",omitNorms"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",indexOptions="
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|indexOptions
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

