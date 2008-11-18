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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|util
operator|.
name|AttributeSource
import|;
end_import

begin_comment
comment|/** This is a DocFieldConsumer that inverts each field,  *  separately, from a Document, and accepts a  *  InvertedTermsConsumer to process those terms. */
end_comment

begin_class
DECL|class|DocInverter
specifier|final
class|class
name|DocInverter
extends|extends
name|DocFieldConsumer
block|{
DECL|field|consumer
specifier|final
name|InvertedDocConsumer
name|consumer
decl_stmt|;
DECL|field|endConsumer
specifier|final
name|InvertedDocEndConsumer
name|endConsumer
decl_stmt|;
DECL|method|DocInverter
specifier|public
name|DocInverter
parameter_list|(
name|InvertedDocConsumer
name|consumer
parameter_list|,
name|InvertedDocEndConsumer
name|endConsumer
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|endConsumer
operator|=
name|endConsumer
expr_stmt|;
block|}
DECL|method|setFieldInfos
name|void
name|setFieldInfos
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
name|super
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|endConsumer
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
name|void
name|flush
parameter_list|(
name|Map
name|threadsAndFields
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
name|childThreadsAndFields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Map
name|endChildThreadsAndFields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Iterator
name|it
init|=
name|threadsAndFields
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|DocInverterPerThread
name|perThread
init|=
operator|(
name|DocInverterPerThread
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Collection
name|fields
init|=
operator|(
name|Collection
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Iterator
name|fieldsIt
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Collection
name|childFields
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|Collection
name|endChildFields
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldsIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocInverterPerField
name|perField
init|=
operator|(
name|DocInverterPerField
operator|)
name|fieldsIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|childFields
operator|.
name|add
argument_list|(
name|perField
operator|.
name|consumer
argument_list|)
expr_stmt|;
name|endChildFields
operator|.
name|add
argument_list|(
name|perField
operator|.
name|endConsumer
argument_list|)
expr_stmt|;
block|}
name|childThreadsAndFields
operator|.
name|put
argument_list|(
name|perThread
operator|.
name|consumer
argument_list|,
name|childFields
argument_list|)
expr_stmt|;
name|endChildThreadsAndFields
operator|.
name|put
argument_list|(
name|perThread
operator|.
name|endConsumer
argument_list|,
name|endChildFields
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|flush
argument_list|(
name|childThreadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|endConsumer
operator|.
name|flush
argument_list|(
name|endChildThreadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
DECL|method|closeDocStore
specifier|public
name|void
name|closeDocStore
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|endConsumer
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
name|endConsumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
DECL|method|freeRAM
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
return|return
name|consumer
operator|.
name|freeRAM
argument_list|()
return|;
block|}
DECL|method|addThread
specifier|public
name|DocFieldConsumerPerThread
name|addThread
parameter_list|(
name|DocFieldProcessorPerThread
name|docFieldProcessorPerThread
parameter_list|)
block|{
return|return
operator|new
name|DocInverterPerThread
argument_list|(
name|docFieldProcessorPerThread
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

