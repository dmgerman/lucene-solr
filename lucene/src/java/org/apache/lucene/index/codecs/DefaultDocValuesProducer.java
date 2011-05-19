begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|FieldInfos
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
name|SegmentInfo
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
name|codecs
operator|.
name|PerDocValues
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
name|Bytes
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
name|DocValues
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
name|Floats
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
name|Ints
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/**  * Abstract base class for FieldsProducer implementations supporting  * {@link DocValues}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|DefaultDocValuesProducer
specifier|public
class|class
name|DefaultDocValuesProducer
extends|extends
name|PerDocValues
block|{
DECL|field|docValues
specifier|protected
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Creates a new {@link DefaultDocValuesProducer} instance and loads all    * {@link DocValues} instances for this segment and codec.    *     * @param si    *          the segment info to load the {@link DocValues} for.    * @param dir    *          the directory to load the {@link DocValues} from.    * @param fieldInfo    *          the {@link FieldInfos}    * @param codecId    *          the codec ID    * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|DefaultDocValuesProducer
specifier|public
name|DefaultDocValuesProducer
parameter_list|(
name|SegmentInfo
name|si
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfo
parameter_list|,
name|int
name|codecId
parameter_list|)
throws|throws
name|IOException
block|{
name|load
argument_list|(
name|fieldInfo
argument_list|,
name|si
operator|.
name|name
argument_list|,
name|si
operator|.
name|docCount
argument_list|,
name|dir
argument_list|,
name|codecId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a {@link DocValues} instance for the given field name or    *<code>null</code> if this field has no {@link DocValues}.    */
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docValues
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|// Only opens files... doesn't actually load any values
DECL|method|load
specifier|protected
name|void
name|load
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|int
name|codecId
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|codecId
operator|==
name|fieldInfo
operator|.
name|getCodecId
argument_list|()
operator|&&
name|fieldInfo
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
specifier|final
name|String
name|field
init|=
name|fieldInfo
operator|.
name|name
decl_stmt|;
comment|// TODO can we have a compound file per segment and codec for docvalues?
specifier|final
name|String
name|id
init|=
name|DefaultDocValuesConsumer
operator|.
name|docValuesId
argument_list|(
name|segment
argument_list|,
name|codecId
argument_list|,
name|fieldInfo
operator|.
name|number
argument_list|)
decl_stmt|;
name|docValues
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|loadDocValues
argument_list|(
name|docCount
argument_list|,
name|dir
argument_list|,
name|id
argument_list|,
name|fieldInfo
operator|.
name|getDocValues
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Loads a {@link DocValues} instance depending on the given {@link ValueType}.    * Codecs that use different implementations for a certain {@link ValueType} can    * simply override this method and return their custom implementations.    *     * @param docCount    *          number of documents in the segment    * @param dir    *          the {@link Directory} to load the {@link DocValues} from    * @param id    *          the unique file ID within the segment    * @param type    *          the type to load    * @return a {@link DocValues} instance for the given type    * @throws IOException    *           if an {@link IOException} occurs    * @throws IllegalArgumentException    *           if the given {@link ValueType} is not supported    */
DECL|method|loadDocValues
specifier|protected
name|DocValues
name|loadDocValues
parameter_list|(
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|ValueType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INTS
case|:
return|return
name|Ints
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
return|return
name|Floats
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|FLOAT_64
case|:
return|return
name|Floats
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|BYTES_FIXED_DEREF
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|BYTES_VAR_DEREF
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unrecognized index values mode "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Collection
argument_list|<
name|DocValues
argument_list|>
name|values
init|=
name|docValues
operator|.
name|values
argument_list|()
decl_stmt|;
name|IOException
name|ex
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DocValues
name|docValues
range|:
name|values
control|)
block|{
try|try
block|{
name|docValues
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|()
block|{
return|return
name|docValues
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

