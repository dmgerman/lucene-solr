begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Parameters used across many handlers  */
end_comment

begin_interface
DECL|interface|CommonParams
specifier|public
interface|interface
name|CommonParams
block|{
comment|/** the query type - which query handler should handle the request */
DECL|field|QT
specifier|public
specifier|static
specifier|final
name|String
name|QT
init|=
literal|"qt"
decl_stmt|;
comment|/** the response writer type - the format of the response */
DECL|field|WT
specifier|public
specifier|static
specifier|final
name|String
name|WT
init|=
literal|"wt"
decl_stmt|;
comment|/** query string */
DECL|field|Q
specifier|public
specifier|static
specifier|final
name|String
name|Q
init|=
literal|"q"
decl_stmt|;
comment|/** sort order */
DECL|field|SORT
specifier|public
specifier|static
specifier|final
name|String
name|SORT
init|=
literal|"sort"
decl_stmt|;
comment|/** Lucene query string(s) for filtering the results without affecting scoring */
DECL|field|FQ
specifier|public
specifier|static
specifier|final
name|String
name|FQ
init|=
literal|"fq"
decl_stmt|;
comment|/** zero based offset of matching documents to retrieve */
DECL|field|START
specifier|public
specifier|static
specifier|final
name|String
name|START
init|=
literal|"start"
decl_stmt|;
comment|/** number of documents to return starting at "start" */
DECL|field|ROWS
specifier|public
specifier|static
specifier|final
name|String
name|ROWS
init|=
literal|"rows"
decl_stmt|;
comment|/** stylesheet to apply to XML results */
DECL|field|XSL
specifier|public
specifier|static
specifier|final
name|String
name|XSL
init|=
literal|"xsl"
decl_stmt|;
comment|/** stylesheet to apply to XML results */
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"version"
decl_stmt|;
comment|/** query and init param for field list */
DECL|field|FL
specifier|public
specifier|static
specifier|final
name|String
name|FL
init|=
literal|"fl"
decl_stmt|;
comment|/** default query field */
DECL|field|DF
specifier|public
specifier|static
specifier|final
name|String
name|DF
init|=
literal|"df"
decl_stmt|;
comment|/** whether to include debug data */
DECL|field|DEBUG_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DEBUG_QUERY
init|=
literal|"debugQuery"
decl_stmt|;
comment|/** another query to explain against */
DECL|field|EXPLAIN_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|EXPLAIN_OTHER
init|=
literal|"explainOther"
decl_stmt|;
comment|/** If the content stream should come from a URL (using URLConnection) */
DECL|field|STREAM_URL
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_URL
init|=
literal|"stream.url"
decl_stmt|;
comment|/** If the content stream should come from a File (using FileReader) */
DECL|field|STREAM_FILE
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_FILE
init|=
literal|"stream.file"
decl_stmt|;
comment|/** If the content stream should come directly from a field */
DECL|field|STREAM_BODY
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_BODY
init|=
literal|"stream.body"
decl_stmt|;
comment|/**     * Explicitly set the content type for the input stream    * If multiple streams are specified, the explicit contentType    * will be used for all of them.      */
DECL|field|STREAM_CONTENTTYPE
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_CONTENTTYPE
init|=
literal|"stream.contentType"
decl_stmt|;
comment|/** 'true' if the header should include the handler name */
DECL|field|HEADER_ECHO_HANDLER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER_ECHO_HANDLER
init|=
literal|"echoHandler"
decl_stmt|;
comment|/** include the parameters in the header **/
DECL|field|HEADER_ECHO_PARAMS
specifier|public
specifier|static
specifier|final
name|String
name|HEADER_ECHO_PARAMS
init|=
literal|"echoParams"
decl_stmt|;
comment|/** valid values for:<code>echoParams</code> */
DECL|enum|EchoParamStyle
specifier|public
enum|enum
name|EchoParamStyle
block|{
DECL|enum constant|EXPLICIT
name|EXPLICIT
block|,
DECL|enum constant|ALL
name|ALL
block|,
DECL|enum constant|NONE
name|NONE
block|;
DECL|method|get
specifier|public
specifier|static
name|EchoParamStyle
name|get
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|v
operator|=
name|v
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"EXPLICIT"
argument_list|)
condition|)
block|{
return|return
name|EXPLICIT
return|;
block|}
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"ALL"
argument_list|)
condition|)
block|{
return|return
name|ALL
return|;
block|}
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"NONE"
argument_list|)
condition|)
block|{
comment|// the same as nothing...
return|return
name|NONE
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
empty_stmt|;
block|}
end_interface

end_unit

