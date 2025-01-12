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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Bits
import|;
end_import

begin_comment
comment|/**  * Filters the incoming reader and makes all documents appear deleted.  */
end_comment

begin_class
DECL|class|AllDeletedFilterReader
specifier|public
class|class
name|AllDeletedFilterReader
extends|extends
name|FilterLeafReader
block|{
DECL|field|liveDocs
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|method|AllDeletedFilterReader
specifier|public
name|AllDeletedFilterReader
parameter_list|(
name|LeafReader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|liveDocs
operator|=
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|in
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|maxDoc
argument_list|()
operator|==
literal|0
operator|||
name|hasDeletions
argument_list|()
assert|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
name|liveDocs
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheHelper
specifier|public
name|CacheHelper
name|getCoreCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheHelper
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

