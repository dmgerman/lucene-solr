begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|directory
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Consts
specifier|abstract
class|class
name|Consts
block|{
DECL|field|FULL
specifier|static
specifier|final
name|String
name|FULL
init|=
literal|"$full_path$"
decl_stmt|;
DECL|field|FIELD_PAYLOADS
specifier|static
specifier|final
name|String
name|FIELD_PAYLOADS
init|=
literal|"$payloads$"
decl_stmt|;
DECL|field|PAYLOAD_PARENT
specifier|static
specifier|final
name|String
name|PAYLOAD_PARENT
init|=
literal|"p"
decl_stmt|;
DECL|field|PAYLOAD_PARENT_BYTES_REF
specifier|static
specifier|final
name|BytesRef
name|PAYLOAD_PARENT_BYTES_REF
init|=
operator|new
name|BytesRef
argument_list|(
name|PAYLOAD_PARENT
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

