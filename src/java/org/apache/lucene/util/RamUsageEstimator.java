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
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Estimates the size of a given Object using a given MemoryModel for primitive  * size information.  *   * Resource Usage:   *   * Internally uses a Map to temporally hold a reference to every  * object seen.   *   * If checkIntered, all Strings checked will be interned, but those  * that were not already interned will be released for GC when the  * estimate is complete.  */
end_comment

begin_class
DECL|class|RamUsageEstimator
specifier|public
specifier|final
class|class
name|RamUsageEstimator
block|{
DECL|field|memoryModel
specifier|private
name|MemoryModel
name|memoryModel
decl_stmt|;
DECL|field|seen
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|seen
decl_stmt|;
DECL|field|refSize
specifier|private
name|int
name|refSize
decl_stmt|;
DECL|field|arraySize
specifier|private
name|int
name|arraySize
decl_stmt|;
DECL|field|classSize
specifier|private
name|int
name|classSize
decl_stmt|;
DECL|field|NUM_BYTES_OBJECT_REF
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_OBJECT_REF
init|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
literal|8
else|:
literal|4
decl_stmt|;
DECL|field|NUM_BYTES_CHAR
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_CHAR
init|=
literal|2
decl_stmt|;
DECL|field|NUM_BYTES_SHORT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_SHORT
init|=
literal|2
decl_stmt|;
DECL|field|NUM_BYTES_INT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_INT
init|=
literal|4
decl_stmt|;
DECL|field|NUM_BYTES_LONG
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_LONG
init|=
literal|8
decl_stmt|;
DECL|field|NUM_BYTES_FLOAT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_FLOAT
init|=
literal|4
decl_stmt|;
DECL|field|NUM_BYTES_DOUBLE
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_DOUBLE
init|=
literal|8
decl_stmt|;
DECL|field|checkInterned
specifier|private
name|boolean
name|checkInterned
decl_stmt|;
comment|/**    * Constructs this object with an AverageGuessMemoryModel and    * checkInterned = true.    */
DECL|method|RamUsageEstimator
specifier|public
name|RamUsageEstimator
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|AverageGuessMemoryModel
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param checkInterned check if Strings are interned and don't add to size    * if they are. Defaults to true but if you know the objects you are checking    * won't likely contain many interned Strings, it will be faster to turn off    * intern checking.    */
DECL|method|RamUsageEstimator
specifier|public
name|RamUsageEstimator
parameter_list|(
name|boolean
name|checkInterned
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|AverageGuessMemoryModel
argument_list|()
argument_list|,
name|checkInterned
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param memoryModel MemoryModel to use for primitive object sizes.    */
DECL|method|RamUsageEstimator
specifier|public
name|RamUsageEstimator
parameter_list|(
name|MemoryModel
name|memoryModel
parameter_list|)
block|{
name|this
argument_list|(
name|memoryModel
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param memoryModel MemoryModel to use for primitive object sizes.    * @param checkInterned check if Strings are interned and don't add to size    * if they are. Defaults to true but if you know the objects you are checking    * won't likely contain many interned Strings, it will be faster to turn off    * intern checking.    */
DECL|method|RamUsageEstimator
specifier|public
name|RamUsageEstimator
parameter_list|(
name|MemoryModel
name|memoryModel
parameter_list|,
name|boolean
name|checkInterned
parameter_list|)
block|{
name|this
operator|.
name|memoryModel
operator|=
name|memoryModel
expr_stmt|;
name|this
operator|.
name|checkInterned
operator|=
name|checkInterned
expr_stmt|;
comment|// Use Map rather than Set so that we can use an IdentityHashMap - not
comment|// seeing an IdentityHashSet
name|seen
operator|=
operator|new
name|IdentityHashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|64
argument_list|)
expr_stmt|;
name|this
operator|.
name|refSize
operator|=
name|memoryModel
operator|.
name|getReferenceSize
argument_list|()
expr_stmt|;
name|this
operator|.
name|arraySize
operator|=
name|memoryModel
operator|.
name|getArraySize
argument_list|()
expr_stmt|;
name|this
operator|.
name|classSize
operator|=
name|memoryModel
operator|.
name|getClassSize
argument_list|()
expr_stmt|;
block|}
DECL|method|estimateRamUsage
specifier|public
name|long
name|estimateRamUsage
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|long
name|size
init|=
name|size
argument_list|(
name|obj
argument_list|)
decl_stmt|;
name|seen
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
DECL|method|size
specifier|private
name|long
name|size
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// interned not part of this object
if|if
condition|(
name|checkInterned
operator|&&
name|obj
operator|instanceof
name|String
operator|&&
name|obj
operator|==
operator|(
operator|(
name|String
operator|)
name|obj
operator|)
operator|.
name|intern
argument_list|()
condition|)
block|{
comment|// interned string will be eligible
comment|// for GC on
comment|// estimateRamUsage(Object) return
return|return
literal|0
return|;
block|}
comment|// skip if we have seen before
if|if
condition|(
name|seen
operator|.
name|containsKey
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// add to seen
name|seen
operator|.
name|put
argument_list|(
name|obj
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Class
name|clazz
init|=
name|obj
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|sizeOfArray
argument_list|(
name|obj
argument_list|)
return|;
block|}
name|long
name|size
init|=
literal|0
decl_stmt|;
comment|// walk type hierarchy
while|while
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
name|Field
index|[]
name|fields
init|=
name|clazz
operator|.
name|getDeclaredFields
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
name|size
operator|+=
name|memoryModel
operator|.
name|getPrimitiveSize
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|size
operator|+=
name|refSize
expr_stmt|;
name|fields
index|[
name|i
index|]
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|value
init|=
name|fields
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|obj
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|size
operator|+=
name|size
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|ex
parameter_list|)
block|{
comment|// ignore for now?
block|}
block|}
block|}
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
name|size
operator|+=
name|classSize
expr_stmt|;
return|return
name|size
return|;
block|}
DECL|method|sizeOfArray
specifier|private
name|long
name|sizeOfArray
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|int
name|len
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|obj
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|size
init|=
name|arraySize
decl_stmt|;
name|Class
name|arrayElementClazz
init|=
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|arrayElementClazz
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
name|size
operator|+=
name|len
operator|*
name|memoryModel
operator|.
name|getPrimitiveSize
argument_list|(
name|arrayElementClazz
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|size
operator|+=
name|refSize
operator|+
name|size
argument_list|(
name|Array
operator|.
name|get
argument_list|(
name|obj
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
DECL|field|ONE_KB
specifier|private
specifier|static
specifier|final
name|long
name|ONE_KB
init|=
literal|1024
decl_stmt|;
DECL|field|ONE_MB
specifier|private
specifier|static
specifier|final
name|long
name|ONE_MB
init|=
name|ONE_KB
operator|*
name|ONE_KB
decl_stmt|;
DECL|field|ONE_GB
specifier|private
specifier|static
specifier|final
name|long
name|ONE_GB
init|=
name|ONE_KB
operator|*
name|ONE_MB
decl_stmt|;
comment|/**    * Return good default units based on byte size.    */
DECL|method|humanReadableUnits
specifier|public
specifier|static
name|String
name|humanReadableUnits
parameter_list|(
name|long
name|bytes
parameter_list|,
name|DecimalFormat
name|df
parameter_list|)
block|{
name|String
name|newSizeAndUnits
decl_stmt|;
if|if
condition|(
name|bytes
operator|/
name|ONE_GB
operator|>
literal|0
condition|)
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_GB
argument_list|)
argument_list|)
operator|+
literal|" GB"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_MB
operator|>
literal|0
condition|)
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_MB
argument_list|)
argument_list|)
operator|+
literal|" MB"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_KB
operator|>
literal|0
condition|)
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_KB
argument_list|)
argument_list|)
operator|+
literal|" KB"
expr_stmt|;
block|}
else|else
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|bytes
argument_list|)
operator|+
literal|" bytes"
expr_stmt|;
block|}
return|return
name|newSizeAndUnits
return|;
block|}
block|}
end_class

end_unit

