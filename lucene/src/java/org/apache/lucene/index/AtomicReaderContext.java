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

begin_comment
comment|/**  * {@link IndexReaderContext} for {@link AtomicReader} instances  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AtomicReaderContext
specifier|public
specifier|final
class|class
name|AtomicReaderContext
extends|extends
name|IndexReaderContext
block|{
comment|/** The readers ord in the top-level's leaves array */
DECL|field|ord
specifier|public
specifier|final
name|int
name|ord
decl_stmt|;
comment|/** The readers absolute doc base */
DECL|field|docBase
specifier|public
specifier|final
name|int
name|docBase
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|AtomicReader
name|reader
decl_stmt|;
comment|/**    * Creates a new {@link AtomicReaderContext}     */
DECL|method|AtomicReaderContext
name|AtomicReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|AtomicReader
name|reader
parameter_list|,
name|int
name|ord
parameter_list|,
name|int
name|docBase
parameter_list|,
name|int
name|leafOrd
parameter_list|,
name|int
name|leafDocBase
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|ord
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
name|this
operator|.
name|ord
operator|=
name|leafOrd
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|leafDocBase
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
DECL|method|AtomicReaderContext
name|AtomicReaderContext
parameter_list|(
name|AtomicReader
name|atomicReader
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|atomicReader
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|leaves
specifier|public
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|children
specifier|public
name|IndexReaderContext
index|[]
name|children
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|reader
specifier|public
name|AtomicReader
name|reader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
block|}
end_class

end_unit

