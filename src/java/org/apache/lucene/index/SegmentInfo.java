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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
DECL|class|SegmentInfo
specifier|final
class|class
name|SegmentInfo
block|{
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
comment|// unique name in dir
DECL|field|docCount
specifier|public
name|int
name|docCount
decl_stmt|;
comment|// number of docs in seg
DECL|field|dir
specifier|public
name|Directory
name|dir
decl_stmt|;
comment|// where segment resides
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
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
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
block|}
end_class

end_unit

