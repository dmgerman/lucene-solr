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
comment|/**  *   * The "atom:published" element is a Date construct indicating an instant in  * time associated with an event early in the life cycle of the entry.  * Typically, atom:published will be associated with the initial creation or  * first availability of the resource.  *   *<pre>  *  atomPublished = element atom:published { atomDateConstruct}  *</pre>  *   * @author Simon Willnauer  * @see org.apache.lucene.gdata.gom.GOMDateConstruct  */
end_comment

begin_interface
DECL|interface|GOMPublished
specifier|public
interface|interface
name|GOMPublished
extends|extends
name|GOMDateConstruct
block|{
comment|/** 	 * Atom local name for the xml element 	 */
DECL|field|LOCALNAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME
init|=
literal|"published"
decl_stmt|;
block|}
end_interface

end_unit

