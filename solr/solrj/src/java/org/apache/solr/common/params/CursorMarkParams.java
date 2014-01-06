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
comment|/**  * Parameters and constants used when dealing with cursor based requests across   * large sorted result sets.  */
end_comment

begin_interface
DECL|interface|CursorMarkParams
specifier|public
interface|interface
name|CursorMarkParams
block|{
comment|/**    * Param clients should specify indicating that they want a cursor based search.    * The value specified must either be {@link #CURSOR_MARK_START} indicating the     * first page of results, or a value returned by a previous search via the     * {@link #CURSOR_MARK_NEXT} key.    */
DECL|field|CURSOR_MARK_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|CURSOR_MARK_PARAM
init|=
literal|"cursorMark"
decl_stmt|;
comment|/**    * Key used in Solr response to inform the client what the "next"     * {@link #CURSOR_MARK_PARAM} value should be to continue pagination    */
DECL|field|CURSOR_MARK_NEXT
specifier|public
specifier|static
specifier|final
name|String
name|CURSOR_MARK_NEXT
init|=
literal|"nextCursorMark"
decl_stmt|;
comment|/**     * Special value for {@link #CURSOR_MARK_PARAM} indicating that cursor functionality     * should be used, and a new cursor value should be computed afte the last result,    * but that currently the "first page" of results is being requested    */
DECL|field|CURSOR_MARK_START
specifier|public
specifier|static
specifier|final
name|String
name|CURSOR_MARK_START
init|=
literal|"*"
decl_stmt|;
block|}
end_interface

end_unit

