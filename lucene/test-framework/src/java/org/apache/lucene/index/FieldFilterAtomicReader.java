begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FilterIterator
import|;
end_import

begin_comment
comment|/**  * A {@link FilterAtomicReader} that exposes only a subset  * of fields from the underlying wrapped reader.  */
end_comment

begin_class
DECL|class|FieldFilterAtomicReader
specifier|public
specifier|final
class|class
name|FieldFilterAtomicReader
extends|extends
name|FilterAtomicReader
block|{
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|field|negate
specifier|private
specifier|final
name|boolean
name|negate
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|method|FieldFilterAtomicReader
specifier|public
name|FieldFilterAtomicReader
parameter_list|(
name|AtomicReader
name|in
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|boolean
name|negate
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|negate
operator|=
name|negate
expr_stmt|;
name|ArrayList
argument_list|<
name|FieldInfo
argument_list|>
name|filteredInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|in
operator|.
name|getFieldInfos
argument_list|()
control|)
block|{
if|if
condition|(
name|hasField
argument_list|(
name|fi
operator|.
name|name
argument_list|)
condition|)
block|{
name|filteredInfos
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
block|}
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
name|filteredInfos
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|filteredInfos
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|hasField
name|boolean
name|hasField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|negate
operator|^
name|fields
operator|.
name|contains
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|f
init|=
name|super
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|f
operator|=
operator|new
name|FieldFilterFields
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// we need to check for emptyness, so we can return
comment|// null:
return|return
name|f
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|?
name|f
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
specifier|final
name|int
name|docID
parameter_list|,
specifier|final
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|document
argument_list|(
name|docID
argument_list|,
operator|new
name|StoredFieldVisitor
argument_list|()
block|{
annotation|@
name|Override
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
block|{
name|visitor
operator|.
name|binaryField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|stringField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|visitor
operator|.
name|intField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|visitor
operator|.
name|longField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|visitor
operator|.
name|floatField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|visitor
operator|.
name|doubleField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|?
name|visitor
operator|.
name|needsField
argument_list|(
name|fieldInfo
argument_list|)
else|:
name|Status
operator|.
name|NO
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|f
init|=
name|super
operator|.
name|fields
argument_list|()
decl_stmt|;
return|return
operator|(
name|f
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|FieldFilterFields
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getNormValues
specifier|public
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|getNormValues
argument_list|(
name|field
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"FieldFilterAtomicReader(reader="
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|in
argument_list|)
operator|.
name|append
argument_list|(
literal|", fields="
argument_list|)
expr_stmt|;
if|if
condition|(
name|negate
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'!'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
name|fields
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|FieldFilterFields
specifier|private
class|class
name|FieldFilterFields
extends|extends
name|FilterFields
block|{
DECL|method|FieldFilterFields
specifier|public
name|FieldFilterFields
parameter_list|(
name|Fields
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
comment|// this information is not cheap, return -1 like MultiFields does:
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|FilterIterator
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|super
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|predicateFunction
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|terms
argument_list|(
name|field
argument_list|)
else|:
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

