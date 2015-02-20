begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|DocValuesRangeQuery
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
name|MultiTermQueryWrapperFilter
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
name|NumericRangeFilter
import|;
end_import

begin_comment
comment|// javadoc
end_comment

begin_comment
comment|// javadoc
end_comment

begin_comment
comment|/**  * A Filter that restricts search results to a range of term  * values in a given field.  *  *<p>This filter matches the documents looking for terms that fall into the  * supplied range according to {@link  * String#compareTo(String)}, unless a<code>Collator</code> is provided. It is not intended  * for numerical ranges; use {@link NumericRangeFilter} instead.  *  *<p>If you construct a large number of range filters with different ranges but on the   * same field, {@link DocValuesRangeQuery} may have significantly better performance.   * @deprecated Index collation keys with CollationKeyAnalyzer or ICUCollationKeyAnalyzer instead.  * This class will be removed in Lucene 5.0  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SlowCollatedTermRangeFilter
specifier|public
class|class
name|SlowCollatedTermRangeFilter
extends|extends
name|MultiTermQueryWrapperFilter
argument_list|<
name|SlowCollatedTermRangeQuery
argument_list|>
block|{
comment|/**    *    * @param lowerTerm The lower bound on this range    * @param upperTerm The upper bound on this range    * @param includeLower Does this range include the lower bound?    * @param includeUpper Does this range include the upper bound?    * @param collator The collator to use when determining range inclusion; set    *  to null to use Unicode code point ordering instead of collation.    * @throws IllegalArgumentException if both terms are null or if    *  lowerTerm is null and includeLower is true (similar for upperTerm    *  and includeUpper)    */
DECL|method|SlowCollatedTermRangeFilter
specifier|public
name|SlowCollatedTermRangeFilter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|SlowCollatedTermRangeQuery
argument_list|(
name|fieldName
argument_list|,
name|lowerTerm
argument_list|,
name|upperTerm
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
name|collator
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the lower value of this range filter */
DECL|method|getLowerTerm
specifier|public
name|String
name|getLowerTerm
parameter_list|()
block|{
return|return
name|query
operator|.
name|getLowerTerm
argument_list|()
return|;
block|}
comment|/** Returns the upper value of this range filter */
DECL|method|getUpperTerm
specifier|public
name|String
name|getUpperTerm
parameter_list|()
block|{
return|return
name|query
operator|.
name|getUpperTerm
argument_list|()
return|;
block|}
comment|/** Returns<code>true</code> if the lower endpoint is inclusive */
DECL|method|includesLower
specifier|public
name|boolean
name|includesLower
parameter_list|()
block|{
return|return
name|query
operator|.
name|includesLower
argument_list|()
return|;
block|}
comment|/** Returns<code>true</code> if the upper endpoint is inclusive */
DECL|method|includesUpper
specifier|public
name|boolean
name|includesUpper
parameter_list|()
block|{
return|return
name|query
operator|.
name|includesUpper
argument_list|()
return|;
block|}
comment|/** Returns the collator used to determine range inclusion, if any. */
DECL|method|getCollator
specifier|public
name|Collator
name|getCollator
parameter_list|()
block|{
return|return
name|query
operator|.
name|getCollator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

