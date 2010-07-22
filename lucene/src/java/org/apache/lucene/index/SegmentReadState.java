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
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentReadState
specifier|public
class|class
name|SegmentReadState
block|{
DECL|field|dir
specifier|public
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|segmentInfo
specifier|public
specifier|final
name|SegmentInfo
name|segmentInfo
decl_stmt|;
DECL|field|fieldInfos
specifier|public
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|readBufferSize
specifier|public
specifier|final
name|int
name|readBufferSize
decl_stmt|;
comment|// NOTE: if this is< 0, that means "defer terms index
comment|// load until needed".  But if the codec must load the
comment|// terms index on init (preflex is the only once currently
comment|// that must do so), then it should negate this value to
comment|// get the app's terms divisor:
DECL|field|termsIndexDivisor
specifier|public
specifier|final
name|int
name|termsIndexDivisor
decl_stmt|;
DECL|method|SegmentReadState
specifier|public
name|SegmentReadState
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|int
name|termsIndexDivisor
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|segmentInfo
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|readBufferSize
operator|=
name|readBufferSize
expr_stmt|;
name|this
operator|.
name|termsIndexDivisor
operator|=
name|termsIndexDivisor
expr_stmt|;
block|}
block|}
end_class

end_unit

