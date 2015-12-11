begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.range
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|range
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|DrillDownQuery
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|search
operator|.
name|DimensionalRangeQuery
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|Query
import|;
end_import

begin_comment
comment|/** Base class for a single labeled range.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|Range
specifier|public
specifier|abstract
class|class
name|Range
block|{
comment|/** Label that identifies this range. */
DECL|field|label
specifier|public
specifier|final
name|String
name|label
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Range
specifier|protected
name|Range
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"label cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
block|}
comment|/** Returns a new {@link Query} accepting only documents    *  in this range.  This query might not be very efficient    *  when run on its own since it is optimized towards    *  random-access, so it is best used either with    *  {@link DrillDownQuery#add(String, Query) DrillDownQuery}    *  or when intersected with another query that can lead the    *  iteration.  If the {@link ValueSource} is static, e.g. an    *  indexed numeric field, then it may be more efficient to use    *  {@link DimensionalRangeQuery}. The provided fastMatchQuery,    *  if non-null, will first be consulted, and only if    *  that is set for each document will the range then be    *  checked. */
DECL|method|getQuery
specifier|public
specifier|abstract
name|Query
name|getQuery
parameter_list|(
name|Query
name|fastMatchQuery
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|)
function_decl|;
comment|/** Returns a new {@link Query} accepting only documents    *  in this range.  This query might not be very efficient    *  when run on its own since it is optimized towards    *  random-access, so it is best used either with    *  {@link DrillDownQuery#add(String, Query) DrillDownQuery}    *  or when intersected with another query that can lead the    *  iteration.  If the {@link ValueSource} is static, e.g. an    *  indexed numeric field, then it may be more efficient to    *  use {@link DimensionalRangeQuery}. */
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|)
block|{
return|return
name|getQuery
argument_list|(
literal|null
argument_list|,
name|valueSource
argument_list|)
return|;
block|}
comment|/** Invoke this for a useless range. */
DECL|method|failNoMatch
specifier|protected
name|void
name|failNoMatch
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"range \""
operator|+
name|label
operator|+
literal|"\" matches nothing"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

