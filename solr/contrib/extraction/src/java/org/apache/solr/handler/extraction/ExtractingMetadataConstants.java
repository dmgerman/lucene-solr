begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
package|;
end_package

begin_comment
comment|/**  * Constants used internally by the {@link ExtractingRequestHandler}.  *  **/
end_comment

begin_interface
DECL|interface|ExtractingMetadataConstants
specifier|public
interface|interface
name|ExtractingMetadataConstants
block|{
DECL|field|STREAM_NAME
name|String
name|STREAM_NAME
init|=
literal|"stream_name"
decl_stmt|;
DECL|field|STREAM_SOURCE_INFO
name|String
name|STREAM_SOURCE_INFO
init|=
literal|"stream_source_info"
decl_stmt|;
DECL|field|STREAM_SIZE
name|String
name|STREAM_SIZE
init|=
literal|"stream_size"
decl_stmt|;
DECL|field|STREAM_CONTENT_TYPE
name|String
name|STREAM_CONTENT_TYPE
init|=
literal|"stream_content_type"
decl_stmt|;
block|}
end_interface

end_unit

