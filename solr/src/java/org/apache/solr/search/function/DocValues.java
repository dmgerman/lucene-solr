begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|search
operator|.
name|*
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
name|solr
operator|.
name|search
operator|.
name|MutableValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|MutableValueFloat
import|;
end_import

begin_comment
comment|/**  * Represents field values as different types.  * Normally created via a {@link ValueSource} for a particular field and reader.  *  * @version $Id$  */
end_comment

begin_comment
comment|// DocValues is distinct from ValueSource because
end_comment

begin_comment
comment|// there needs to be an object created at query evaluation time that
end_comment

begin_comment
comment|// is not referenced by the query itself because:
end_comment

begin_comment
comment|// - Query objects should be MT safe
end_comment

begin_comment
comment|// - For caching, Query objects are often used as keys... you don't
end_comment

begin_comment
comment|//   want the Query carrying around big objects
end_comment

begin_class
DECL|class|DocValues
specifier|public
specifier|abstract
class|class
name|DocValues
block|{
DECL|method|byteVal
specifier|public
name|byte
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|shortVal
specifier|public
name|short
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|floatVal
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|intVal
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// TODO: should we make a termVal, returns BytesRef?
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/** @lucene.experimental  */
DECL|class|ValueFiller
specifier|public
specifier|static
specifier|abstract
class|class
name|ValueFiller
block|{
comment|/** MutableValue will be reused across calls */
DECL|method|getValue
specifier|public
specifier|abstract
name|MutableValue
name|getValue
parameter_list|()
function_decl|;
comment|/** MutableValue will be reused across calls.  Returns true if the value exists. */
DECL|method|fillValue
specifier|public
specifier|abstract
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
block|}
comment|/** @lucene.experimental  */
DECL|method|getValueFiller
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueFloat
name|mval
init|=
operator|new
name|MutableValueFloat
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|mval
operator|.
name|value
operator|=
name|floatVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|//For Functions that can work with multiple values from the same document.  This does not apply to all functions
DECL|method|byteVal
specifier|public
name|void
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|byte
index|[]
name|vals
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|shortVal
specifier|public
name|void
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|short
index|[]
name|vals
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|floatVal
specifier|public
name|void
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
index|[]
name|vals
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|intVal
specifier|public
name|void
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
index|[]
name|vals
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|longVal
specifier|public
name|void
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
index|[]
name|vals
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|doubleVal
specifier|public
name|void
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|double
index|[]
name|vals
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// TODO: should we make a termVal, fills BytesRef[]?
DECL|method|strVal
specifier|public
name|void
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
index|[]
name|vals
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|,
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getScorer
specifier|public
name|ValueSourceScorer
name|getScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|// A RangeValueSource can't easily be a ValueSource that takes another ValueSource
comment|// because it needs different behavior depending on the type of fields.  There is also
comment|// a setup cost - parsing and normalizing params, and doing a binary search on the StringIndex.
DECL|method|getRangeScorer
specifier|public
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|float
name|lower
decl_stmt|;
name|float
name|upper
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|lower
operator|=
name|Float
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
else|else
block|{
name|lower
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|upper
operator|=
name|Float
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
else|else
block|{
name|upper
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
block|}
specifier|final
name|float
name|l
init|=
name|lower
decl_stmt|;
specifier|final
name|float
name|u
init|=
name|upper
decl_stmt|;
if|if
condition|(
name|includeLower
operator|&&
name|includeUpper
condition|)
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|float
name|docVal
init|=
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>=
name|l
operator|&&
name|docVal
operator|<=
name|u
return|;
block|}
block|}
return|;
block|}
elseif|else
if|if
condition|(
name|includeLower
operator|&&
operator|!
name|includeUpper
condition|)
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|float
name|docVal
init|=
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>=
name|l
operator|&&
name|docVal
operator|<
name|u
return|;
block|}
block|}
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|includeLower
operator|&&
name|includeUpper
condition|)
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|float
name|docVal
init|=
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>
name|l
operator|&&
name|docVal
operator|<=
name|u
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|float
name|docVal
init|=
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>
name|l
operator|&&
name|docVal
operator|<
name|u
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

