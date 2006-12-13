begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
package|;
end_package

begin_comment
comment|/**  * Atom e.g. feed element can contain text, html and xhtml content as character  * values  *   * @author Simon Willnauer  *   */
end_comment

begin_enum
DECL|enum|ContentType
specifier|public
enum|enum
name|ContentType
block|{
comment|/** 	 * GOM content type text 	 */
DECL|enum constant|TEXT
name|TEXT
block|,
comment|/** 	 * GOM content type XHTML 	 */
DECL|enum constant|XHTML
name|XHTML
block|,
comment|/** 	 * GOM content type HTML 	 */
DECL|enum constant|HTML
name|HTML
block|,
comment|/** 	 * GOM atom media type 	 * @see AtomMediaType 	 */
DECL|enum constant|ATOM_MEDIA_TYPE
name|ATOM_MEDIA_TYPE
block|}
end_enum

end_unit

