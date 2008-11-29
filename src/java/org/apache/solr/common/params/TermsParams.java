begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|TermsParams
specifier|public
class|class
name|TermsParams
block|{
comment|/**    * The component name.  Set to true to turn on the TermsComponent    */
DECL|field|TERMS
specifier|public
specifier|static
specifier|final
name|String
name|TERMS
init|=
literal|"terms"
decl_stmt|;
comment|/**    * Used for building up the other terms    */
DECL|field|TERMS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_PREFIX
init|=
name|TERMS
operator|+
literal|"."
decl_stmt|;
comment|/**    * Required.  Specify the field to look up terms in.    */
DECL|field|TERMS_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_FIELD
init|=
name|TERMS_PREFIX
operator|+
literal|"fl"
decl_stmt|;
comment|/**    * Optional.  The lower bound term to start at.  The TermEnum will start at the next term after this term in the dictionary.    *    * If not specified, the empty string is used    */
DECL|field|TERMS_LOWER
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_LOWER
init|=
name|TERMS_PREFIX
operator|+
literal|"lower"
decl_stmt|;
comment|/**    * Optional.  The term to stop at.    *    * @see #TERMS_UPPER_INCLUSIVE    */
DECL|field|TERMS_UPPER
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_UPPER
init|=
name|TERMS_PREFIX
operator|+
literal|"upper"
decl_stmt|;
comment|/**    * Optional.  If true, include the upper bound term in the results.  False by default.    */
DECL|field|TERMS_UPPER_INCLUSIVE
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_UPPER_INCLUSIVE
init|=
name|TERMS_PREFIX
operator|+
literal|"upper.incl"
decl_stmt|;
comment|/**    * Optional.  If true, include the lower bound term in the results, otherwise skip to the next one.  True by default.    */
DECL|field|TERMS_LOWER_INCLUSIVE
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_LOWER_INCLUSIVE
init|=
name|TERMS_PREFIX
operator|+
literal|"lower.incl"
decl_stmt|;
comment|/**    * Optional.  The number of results to return.  If not specified, looks for {@link org.apache.solr.common.params.CommonParams#ROWS}.  If that's not specified, uses 10.    */
DECL|field|TERMS_ROWS
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_ROWS
init|=
name|TERMS_PREFIX
operator|+
literal|"rows"
decl_stmt|;
DECL|field|TERMS_PREFIX_STR
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_PREFIX_STR
init|=
name|TERMS_PREFIX
operator|+
literal|"prefix"
decl_stmt|;
block|}
end_class

end_unit

