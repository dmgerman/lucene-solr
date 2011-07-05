begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|valuesource
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
name|index
operator|.
name|Fields
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
name|IndexReader
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
name|Terms
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
name|queries
operator|.
name|function
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|LongDocValues
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
name|search
operator|.
name|IndexSearcher
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
name|BytesRef
import|;
end_import

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
name|Map
import|;
end_import

begin_comment
comment|/**  *<code>TotalTermFreqValueSource</code> returns the total term freq (sum of term freqs across all docuyments).  * @lucene.internal  */
end_comment

begin_class
DECL|class|SumTotalTermFreqValueSource
specifier|public
class|class
name|SumTotalTermFreqValueSource
extends|extends
name|ValueSource
block|{
DECL|field|indexedField
specifier|protected
name|String
name|indexedField
decl_stmt|;
DECL|method|SumTotalTermFreqValueSource
specifier|public
name|SumTotalTermFreqValueSource
parameter_list|(
name|String
name|indexedField
parameter_list|)
block|{
name|this
operator|.
name|indexedField
operator|=
name|indexedField
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"sumtotaltermfreq"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|+
literal|'('
operator|+
name|indexedField
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
operator|.
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|DocValues
operator|)
name|context
operator|.
name|get
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|sumTotalTermFreq
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexReader
operator|.
name|AtomicReaderContext
name|readerContext
range|:
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
control|)
block|{
name|Fields
name|fields
init|=
name|readerContext
operator|.
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
continue|continue;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|indexedField
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
continue|continue;
name|sumTotalTermFreq
operator|+=
name|terms
operator|.
name|getSumTotalTermFreq
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|ttf
init|=
name|Math
operator|.
name|max
argument_list|(
operator|-
literal|1
argument_list|,
name|sumTotalTermFreq
argument_list|)
decl_stmt|;
comment|// we may have added up -1s if not supported
name|context
operator|.
name|put
argument_list|(
name|this
argument_list|,
operator|new
name|LongDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ttf
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|indexedField
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|SumTotalTermFreqValueSource
name|other
init|=
operator|(
name|SumTotalTermFreqValueSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|indexedField
operator|.
name|equals
argument_list|(
name|other
operator|.
name|indexedField
argument_list|)
return|;
block|}
block|}
end_class

end_unit

