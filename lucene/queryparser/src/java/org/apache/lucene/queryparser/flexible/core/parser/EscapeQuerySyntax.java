begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.parser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * A parser needs to implement {@link EscapeQuerySyntax} to allow the QueryNode  * to escape the queries, when the toQueryString method is called.  */
end_comment

begin_interface
DECL|interface|EscapeQuerySyntax
specifier|public
interface|interface
name|EscapeQuerySyntax
block|{
comment|/**    * Type of escaping: String for escaping syntax,    * NORMAL for escaping reserved words (like AND) in terms    */
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|enum constant|STRING
DECL|enum constant|NORMAL
name|STRING
block|,
name|NORMAL
block|;   }
comment|/**    * @param text    *          - text to be escaped    * @param locale    *          - locale for the current query    * @param type    *          - select the type of escape operation to use    * @return escaped text    */
DECL|method|escape
name|CharSequence
name|escape
parameter_list|(
name|CharSequence
name|text
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|Type
name|type
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

