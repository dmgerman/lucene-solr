begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * contributor license agreements.  See the NOTICE file distributed with  * Licensed to the Apache Software Foundation (ASF) under one or more  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * The "atom:contributor" element is a Person construct that indicates a person  * or other entity who contributed to the entry or feed.  *   *<pre>  * atomContributor = element atom:contributor { atomPersonConstruct }  *</pre>  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMContributor
specifier|public
interface|interface
name|GOMContributor
extends|extends
name|GOMPerson
block|{
comment|/** 	 * Atom 1.0 local name for the xml element 	 */
DECL|field|LOCALNAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME
init|=
literal|"contributor"
decl_stmt|;
block|}
end_interface

end_unit

