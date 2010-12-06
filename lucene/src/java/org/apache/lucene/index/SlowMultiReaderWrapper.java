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
name|ReaderUtil
import|;
end_import

begin_comment
comment|// javadoc
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
name|DirectoryReader
import|;
end_import

begin_comment
comment|// javadoc
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
name|MultiReader
import|;
end_import

begin_comment
comment|// javadoc
end_comment

begin_comment
comment|/**  * This class forces a composite reader (eg a {@link  * MultiReader} or {@link DirectoryReader} or any other  * IndexReader subclass that returns non-null from {@link  * IndexReader#getSequentialSubReaders}) to emulate an  * atomic reader.  This requires implementing the postings  * APIs on-the-fly, using the static methods in {@link  * MultiFields}, by stepping through the sub-readers to  * merge fields/terms, appending docs, etc.  *  *<p>If you ever hit an UnsupportedOperationException saying  * "please use MultiFields.XXX instead", the simple  * but non-performant workaround is to wrap your reader  * using this class.</p>  *  *<p><b>NOTE</b>: this class almost always results in a  * performance hit.  If this is important to your use case,  * it's better to get the sequential sub readers (see {@link  * ReaderUtil#gatherSubReaders}, instead, and iterate through them  * yourself.</p>  */
end_comment

begin_class
DECL|class|SlowMultiReaderWrapper
specifier|public
specifier|final
class|class
name|SlowMultiReaderWrapper
extends|extends
name|FilterIndexReader
block|{
DECL|method|SlowMultiReaderWrapper
specifier|public
name|SlowMultiReaderWrapper
parameter_list|(
name|IndexReader
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
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
return|return
name|MultiFields
operator|.
name|getFields
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDeletedDocs
specifier|public
name|Bits
name|getDeletedDocs
parameter_list|()
block|{
return|return
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSequentialSubReaders
specifier|public
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

