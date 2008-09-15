begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * An iterator over {@link Hits} that provides lazy fetching of each document.  * {@link Hits#iterator()} returns an instance of this class.  Calls to {@link #next()}  * return a {@link Hit} instance.  *  * @deprecated Hits will be removed in Lucene 3.0. Use {@link TopDocCollector} and {@link TopDocs} instead.  */
end_comment

begin_class
DECL|class|HitIterator
specifier|public
class|class
name|HitIterator
implements|implements
name|Iterator
block|{
DECL|field|hits
specifier|private
name|Hits
name|hits
decl_stmt|;
DECL|field|hitNumber
specifier|private
name|int
name|hitNumber
init|=
literal|0
decl_stmt|;
comment|/**    * Constructed from {@link Hits#iterator()}.    */
DECL|method|HitIterator
name|HitIterator
parameter_list|(
name|Hits
name|hits
parameter_list|)
block|{
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
block|}
comment|/**    * @return true if current hit is less than the total number of {@link Hits}.    */
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|hitNumber
operator|<
name|hits
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**    * Returns a {@link Hit} instance representing the next hit in {@link Hits}.    *    * @return Next {@link Hit}.    */
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|hitNumber
operator|==
name|hits
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
name|Object
name|next
init|=
operator|new
name|Hit
argument_list|(
name|hits
argument_list|,
name|hitNumber
argument_list|)
decl_stmt|;
name|hitNumber
operator|++
expr_stmt|;
return|return
name|next
return|;
block|}
comment|/**    * Unsupported operation.    *    * @throws UnsupportedOperationException    */
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Returns the total number of hits.    */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|hits
operator|.
name|length
argument_list|()
return|;
block|}
block|}
end_class

end_unit

