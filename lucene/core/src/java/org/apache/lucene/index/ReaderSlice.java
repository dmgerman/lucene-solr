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

begin_comment
comment|/**  * Subreader slice from a parent composite reader.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|ReaderSlice
specifier|public
specifier|final
class|class
name|ReaderSlice
block|{
comment|/** Zero-length {@code ReaderSlice} array. */
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|ReaderSlice
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|ReaderSlice
index|[
literal|0
index|]
decl_stmt|;
comment|/** Document ID this slice starts from. */
DECL|field|start
specifier|public
specifier|final
name|int
name|start
decl_stmt|;
comment|/** Number of documents in this slice. */
DECL|field|length
specifier|public
specifier|final
name|int
name|length
decl_stmt|;
comment|/** Sub-reader index for this slice. */
DECL|field|readerIndex
specifier|public
specifier|final
name|int
name|readerIndex
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|ReaderSlice
specifier|public
name|ReaderSlice
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|readerIndex
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|readerIndex
operator|=
name|readerIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"slice start="
operator|+
name|start
operator|+
literal|" length="
operator|+
name|length
operator|+
literal|" readerIndex="
operator|+
name|readerIndex
return|;
block|}
block|}
end_class

end_unit

