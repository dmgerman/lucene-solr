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
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|FieldType
operator|.
name|NumericType
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
name|DocValuesType
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
name|util
operator|.
name|LuceneTestCase
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
name|generators
operator|.
name|RandomPicks
import|;
end_import

begin_comment
comment|/** simple testcases for concrete impl of IndexableFieldType */
end_comment

begin_class
DECL|class|TestFieldType
specifier|public
class|class
name|TestFieldType
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ft
argument_list|,
name|ft
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft2
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ft
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ft
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ft2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|FieldType
name|ft3
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft3
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft3
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft4
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft4
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft4
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft5
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft5
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft5
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft6
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft6
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft6
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft7
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft7
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft7
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft8
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft8
operator|.
name|setNumericType
argument_list|(
name|NumericType
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft8
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft9
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft9
operator|.
name|setNumericPrecisionStep
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft9
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|ft10
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft10
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ft10
operator|.
name|equals
argument_list|(
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|randomValue
specifier|private
specifier|static
name|Object
name|randomValue
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isEnum
argument_list|()
condition|)
block|{
return|return
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|clazz
operator|.
name|getEnumConstants
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|boolean
operator|.
name|class
condition|)
block|{
return|return
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|int
operator|.
name|class
condition|)
block|{
return|return
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Don't know how to generate a "
operator|+
name|clazz
argument_list|)
throw|;
block|}
DECL|method|randomFieldType
specifier|private
specifier|static
name|FieldType
name|randomFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|FieldType
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|method
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|PUBLIC
operator|)
operator|!=
literal|0
operator|&&
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
condition|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
specifier|final
name|Object
index|[]
name|args
init|=
operator|new
name|Object
index|[
name|parameterTypes
operator|.
name|length
index|]
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"setDimensions"
argument_list|)
condition|)
block|{
name|args
index|[
literal|0
index|]
operator|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|args
index|[
name|i
index|]
operator|=
name|randomValue
argument_list|(
name|parameterTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|method
operator|.
name|invoke
argument_list|(
name|ft
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ft
return|;
block|}
DECL|method|testCopyConstructor
specifier|public
name|void
name|testCopyConstructor
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iters
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
name|FieldType
name|ft
init|=
name|randomFieldType
argument_list|()
decl_stmt|;
name|FieldType
name|ft2
init|=
operator|new
name|FieldType
argument_list|(
name|ft
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ft
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

