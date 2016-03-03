begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericDocValuesField
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
name|document
operator|.
name|SortedNumericDocValuesField
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
name|index
operator|.
name|LeafReaderContext
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
name|SortedNumericDocValues
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
name|Arrays
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Like {@link DocValuesTermsQuery}, but this query only  * runs on a long {@link NumericDocValuesField} or a  * {@link SortedNumericDocValuesField}, matching  * all documents whose value in the specified field is  * contained in the provided set of long values.  *  *<p>  *<b>NOTE</b>: be very careful using this query: it is  * typically much slower than using {@code TermsQuery},  * but in certain specialized cases may be faster.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocValuesNumbersQuery
specifier|public
class|class
name|DocValuesNumbersQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|numbers
specifier|private
specifier|final
name|Set
argument_list|<
name|Long
argument_list|>
name|numbers
decl_stmt|;
DECL|method|DocValuesNumbersQuery
specifier|public
name|DocValuesNumbersQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Set
argument_list|<
name|Long
argument_list|>
name|numbers
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|numbers
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|numbers
argument_list|,
literal|"Set of numbers must not be null"
argument_list|)
expr_stmt|;
block|}
DECL|method|DocValuesNumbersQuery
specifier|public
name|DocValuesNumbersQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Long
modifier|...
name|numbers
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|numbers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// super.equals ensures we are the same class:
name|DocValuesNumbersQuery
name|that
init|=
operator|(
name|DocValuesNumbersQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|numbers
operator|.
name|equals
argument_list|(
name|that
operator|.
name|numbers
argument_list|)
return|;
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
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|Objects
operator|.
name|hash
argument_list|(
name|field
argument_list|,
name|numbers
argument_list|)
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getNumbers
specifier|public
name|Set
argument_list|<
name|Long
argument_list|>
name|getNumbers
parameter_list|()
block|{
return|return
name|numbers
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|": ["
argument_list|)
expr_stmt|;
for|for
control|(
name|Long
name|number
range|:
name|numbers
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|number
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numbers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RandomAccessWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Bits
name|getMatchingDocs
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|numbers
operator|.
name|contains
argument_list|(
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

