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

begin_comment
comment|/**  *  *  **/
end_comment

begin_interface
DECL|interface|TermVectorParams
specifier|public
interface|interface
name|TermVectorParams
block|{
DECL|field|TV_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TV_PREFIX
init|=
literal|"tv."
decl_stmt|;
comment|/**   * Return Term Frequency info   * */
DECL|field|TF
specifier|public
specifier|static
specifier|final
name|String
name|TF
init|=
name|TV_PREFIX
operator|+
literal|"tf"
decl_stmt|;
comment|/**   * Return Term Vector position information   *   * */
DECL|field|POSITIONS
specifier|public
specifier|static
specifier|final
name|String
name|POSITIONS
init|=
name|TV_PREFIX
operator|+
literal|"positions"
decl_stmt|;
comment|/**   * Return Term Vector payloads information   *   * */
DECL|field|PAYLOADS
specifier|public
specifier|static
specifier|final
name|String
name|PAYLOADS
init|=
name|TV_PREFIX
operator|+
literal|"payloads"
decl_stmt|;
comment|/**   * Return offset information, if available   * */
DECL|field|OFFSETS
specifier|public
specifier|static
specifier|final
name|String
name|OFFSETS
init|=
name|TV_PREFIX
operator|+
literal|"offsets"
decl_stmt|;
comment|/**   * Return IDF information.  May be expensive   * */
DECL|field|DF
specifier|public
specifier|static
specifier|final
name|String
name|DF
init|=
name|TV_PREFIX
operator|+
literal|"df"
decl_stmt|;
comment|/**    * Return TF-IDF calculation, i.e. (tf / idf).  May be expensive.    */
DECL|field|TF_IDF
specifier|public
specifier|static
specifier|final
name|String
name|TF_IDF
init|=
name|TV_PREFIX
operator|+
literal|"tf_idf"
decl_stmt|;
comment|/**    * Return all the options: TF, positions, offsets, idf    */
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|String
name|ALL
init|=
name|TV_PREFIX
operator|+
literal|"all"
decl_stmt|;
comment|/**    * The fields to get term vectors for    */
DECL|field|FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS
init|=
name|TV_PREFIX
operator|+
literal|"fl"
decl_stmt|;
comment|/**    * The Doc Ids (Lucene internal ids) of the docs to get the term vectors for    */
DECL|field|DOC_IDS
specifier|public
specifier|static
specifier|final
name|String
name|DOC_IDS
init|=
name|TV_PREFIX
operator|+
literal|"docIds"
decl_stmt|;
block|}
end_interface

end_unit

