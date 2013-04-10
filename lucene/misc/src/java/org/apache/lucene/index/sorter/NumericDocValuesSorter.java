begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|AtomicReader
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
name|NumericDocValues
import|;
end_import

begin_comment
comment|/**  * A {@link Sorter} which sorts documents according to their  * {@link NumericDocValues}. One can specify ascending or descending sort order.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|NumericDocValuesSorter
specifier|public
class|class
name|NumericDocValuesSorter
extends|extends
name|Sorter
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|ascending
specifier|private
specifier|final
name|boolean
name|ascending
decl_stmt|;
comment|/** Constructor over the given field name, and ascending sort order. */
DECL|method|NumericDocValuesSorter
specifier|public
name|NumericDocValuesSorter
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor over the given field name, and whether sorting should be    * ascending ({@code true}) or descending ({@code false}).    */
DECL|method|NumericDocValuesSorter
specifier|public
name|NumericDocValuesSorter
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
name|boolean
name|ascending
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|ascending
operator|=
name|ascending
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sort
specifier|public
name|Sorter
operator|.
name|DocMap
name|sort
parameter_list|(
specifier|final
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NumericDocValues
name|ndv
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
specifier|final
name|DocComparator
name|comparator
decl_stmt|;
if|if
condition|(
name|ascending
condition|)
block|{
name|comparator
operator|=
operator|new
name|DocComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|docID1
parameter_list|,
name|int
name|docID2
parameter_list|)
block|{
specifier|final
name|long
name|v1
init|=
name|ndv
operator|.
name|get
argument_list|(
name|docID1
argument_list|)
decl_stmt|;
specifier|final
name|long
name|v2
init|=
name|ndv
operator|.
name|get
argument_list|(
name|docID2
argument_list|)
decl_stmt|;
return|return
name|v1
operator|<
name|v2
condition|?
operator|-
literal|1
else|:
name|v1
operator|==
name|v2
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|comparator
operator|=
operator|new
name|DocComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|docID1
parameter_list|,
name|int
name|docID2
parameter_list|)
block|{
specifier|final
name|long
name|v1
init|=
name|ndv
operator|.
name|get
argument_list|(
name|docID1
argument_list|)
decl_stmt|;
specifier|final
name|long
name|v2
init|=
name|ndv
operator|.
name|get
argument_list|(
name|docID2
argument_list|)
decl_stmt|;
return|return
name|v1
operator|>
name|v2
condition|?
operator|-
literal|1
else|:
name|v1
operator|==
name|v2
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
expr_stmt|;
block|}
return|return
name|sort
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getID
specifier|public
name|String
name|getID
parameter_list|()
block|{
return|return
literal|"DocValues("
operator|+
name|fieldName
operator|+
literal|","
operator|+
operator|(
name|ascending
condition|?
literal|"ascending"
else|:
literal|"descending"
operator|)
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

