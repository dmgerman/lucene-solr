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
comment|/**  * Describes an abstraction of a mime-type used to define the content of a  * atom:content element. This could be every possible mimetype but has to match  * this ".+/.+" Pattern.  *   * @author Simon Willnauer  *   */
end_comment

begin_enum
DECL|enum|AtomMediaType
specifier|public
enum|enum
name|AtomMediaType
block|{
comment|/** 	 * A xml media type (mime-type) 	 */
DECL|enum constant|XML
name|XML
block|,
comment|/** 	 *A text but no xml media type (mime-type)  	 */
DECL|enum constant|TEXT
name|TEXT
block|,
comment|/** 	 * A binary media type (mime-type) 	 */
DECL|enum constant|BINARY
name|BINARY
block|}
end_enum

end_unit

