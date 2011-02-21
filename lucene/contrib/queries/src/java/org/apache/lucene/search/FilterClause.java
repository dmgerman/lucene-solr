begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BooleanClause
operator|.
name|Occur
import|;
end_import

begin_comment
comment|/**  * A Filter that wrapped with an indication of how that filter  * is used when composed with another filter.  * (Follows the boolean logic in BooleanClause for composition   * of queries.)  */
end_comment

begin_class
DECL|class|FilterClause
specifier|public
class|class
name|FilterClause
block|{
DECL|field|occur
name|Occur
name|occur
init|=
literal|null
decl_stmt|;
DECL|field|filter
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
comment|/** 	 * Create a new FilterClause 	 * @param filter A Filter object containing a BitSet 	 * @param occur A parameter implementation indicating SHOULD, MUST or MUST NOT 	 */
DECL|method|FilterClause
specifier|public
name|FilterClause
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|Occur
name|occur
parameter_list|)
block|{
name|this
operator|.
name|occur
operator|=
name|occur
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/** 	 * Returns this FilterClause's filter 	 * @return A Filter object 	 */
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
comment|/** 	 * Returns this FilterClause's occur parameter 	 * @return An Occur object 	 */
DECL|method|getOccur
specifier|public
name|Occur
name|getOccur
parameter_list|()
block|{
return|return
name|occur
return|;
block|}
block|}
end_class

end_unit

