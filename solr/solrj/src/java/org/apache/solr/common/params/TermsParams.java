begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_interface
DECL|interface|TermsParams
specifier|public
interface|interface
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
comment|/**    * Optional.  The number of results to return.  If not specified, looks for {@link org.apache.solr.common.params.CommonParams#ROWS}.  If that's not specified, uses {@link org.apache.solr.common.params.CommonParams#ROWS_DEFAULT}.    */
DECL|field|TERMS_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_LIMIT
init|=
name|TERMS_PREFIX
operator|+
literal|"limit"
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
DECL|field|TERMS_REGEXP_STR
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_REGEXP_STR
init|=
name|TERMS_PREFIX
operator|+
literal|"regex"
decl_stmt|;
DECL|field|TERMS_REGEXP_FLAG
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_REGEXP_FLAG
init|=
name|TERMS_REGEXP_STR
operator|+
literal|".flag"
decl_stmt|;
DECL|enum|TermsRegexpFlag
specifier|public
specifier|static
enum|enum
name|TermsRegexpFlag
block|{
DECL|enum constant|UNIX_LINES
name|UNIX_LINES
parameter_list|(
name|Pattern
operator|.
name|UNIX_LINES
parameter_list|)
operator|,
DECL|enum constant|CASE_INSENSITIVE
constructor|CASE_INSENSITIVE(Pattern.CASE_INSENSITIVE
block|)
enum|,
DECL|enum constant|COMMENTS
name|COMMENTS
parameter_list|(
name|Pattern
operator|.
name|COMMENTS
parameter_list|)
operator|,
DECL|enum constant|MULTILINE
constructor|MULTILINE(Pattern.MULTILINE
block|)
operator|,
DECL|enum constant|LITERAL
name|LITERAL
argument_list|(
name|Pattern
operator|.
name|LITERAL
argument_list|)
operator|,
DECL|enum constant|DOTALL
name|DOTALL
argument_list|(
name|Pattern
operator|.
name|DOTALL
argument_list|)
operator|,
DECL|enum constant|UNICODE_CASE
name|UNICODE_CASE
argument_list|(
name|Pattern
operator|.
name|UNICODE_CASE
argument_list|)
operator|,
DECL|enum constant|CANON_EQ
name|CANON_EQ
argument_list|(
name|Pattern
operator|.
name|CANON_EQ
argument_list|)
expr_stmt|;
end_interface

begin_decl_stmt
DECL|field|value
name|int
name|value
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|TermsRegexpFlag
name|TermsRegexpFlag
argument_list|(
name|int
name|value
argument_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
block|;       }
DECL|method|getValue
specifier|public
name|int
name|getValue
argument_list|()
block|{
return|return
name|value
return|;
block|}
end_expr_stmt

begin_comment
unit|}
comment|/**    * Optional.  The minimum value of docFreq to be returned.  1 by default    */
end_comment

begin_decl_stmt
DECL|field|TERMS_MINCOUNT
unit|public
specifier|static
specifier|final
name|String
name|TERMS_MINCOUNT
init|=
name|TERMS_PREFIX
operator|+
literal|"mincount"
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Optional.  The maximum value of docFreq to be returned.  -1 by default means no boundary    */
end_comment

begin_decl_stmt
DECL|field|TERMS_MAXCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_MAXCOUNT
init|=
name|TERMS_PREFIX
operator|+
literal|"maxcount"
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Optional.  If true, return the raw characters of the indexed term, regardless of if it is readable.    * For instance, the index form of numeric numbers is not human readable.  The default is false.    */
end_comment

begin_decl_stmt
DECL|field|TERMS_RAW
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_RAW
init|=
name|TERMS_PREFIX
operator|+
literal|"raw"
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Optional.  If sorting by frequency is enabled.  Defaults to sorting by count.    */
end_comment

begin_decl_stmt
DECL|field|TERMS_SORT
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_SORT
init|=
name|TERMS_PREFIX
operator|+
literal|"sort"
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|TERMS_SORT_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_SORT_COUNT
init|=
literal|"count"
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|TERMS_SORT_INDEX
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_SORT_INDEX
init|=
literal|"index"
decl_stmt|;
end_decl_stmt

unit|}
end_unit

